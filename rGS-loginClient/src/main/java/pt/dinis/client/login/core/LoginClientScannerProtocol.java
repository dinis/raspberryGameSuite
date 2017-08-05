package pt.dinis.client.login.core;

import org.apache.log4j.Logger;
import pt.dinis.common.core.Display;

import pt.dinis.common.core.GameType;
import pt.dinis.common.messages.GenericMessage;
import pt.dinis.common.messages.basic.CloseConnectionRequest;
import pt.dinis.common.messages.chat.ChatMessage;
import pt.dinis.common.messages.chat.ChatMessageToServer;
import pt.dinis.common.messages.invite.Invite;
import pt.dinis.common.messages.invite.ListOfInvitesRequest;
import pt.dinis.common.messages.invite.ListOfPlayersRequest;
import pt.dinis.common.messages.invite.RespondToInvite;
import pt.dinis.common.messages.user.*;

import java.util.*;

/**
 * Created by tiago on 22-01-2017.
 */
public class LoginClientScannerProtocol {

    public enum MessageType {
        HELP("help", "Displays this message",
                "help", Arrays.asList("h")),
        INFO("info", "Shows client information",
                "info", Arrays.asList("information", "status")),
        LOGIN("login", "Logs in the server",
                "login name password", Collections.emptyList()),
        REGISTER("register", "Register as a new client",
                "register name password", Collections.emptyList()),
        LOGOUT("logout", "Logs out of the server",
                "logout", Collections.emptyList()),
        RELOGIN("relogin", "Redo the login",
                "relogin", Arrays.asList("re-login")),
        START("start", "Starts a communication with the server",
                "start; start ip; start ip port", Arrays.asList("open", "connect")),
        CLOSE("close", "Closes a communication with the server",
                "close", Arrays.asList("disconnect")),
        MESSAGE("message", "Sends a message to the server",
                "message [all|echo|others|server|#] text; [all|echo|others|server|#] text", Collections.emptyList()),
        ERROR("error", "Sends an error message to the server",
                "error [all|echo|others|server|#] text", Collections.emptyList()),
        TOKEN("token", "Print the token given from server while logging in",
                "token", Arrays.asList("hash")),
        PLAYERS("players", "Get a list of logged in players",
                "players", Arrays.asList("users")),
        GAMES("games", "Get a list of available games",
                "games", Collections.emptyList()),
        INVITES("invites", "Get a list of active invites",
                "invites", Collections.emptyList()),
        INVITE("invite", "Invite someone (a list)",
                "invite game_type_id (#)", Collections.emptyList()),
        ANSWER("answer", "Answer to an invite or cancel an invite",
                "invite game_id [y|yes|accept|n|no|refuse]", Collections.emptyList()),
        EXIT("exit", "Ends this client",
                "exit", Arrays.asList("quit", "end"));

        private String word;
        private String help;
        private String usage;
        private Collection<String> alternatives;

        MessageType(String word, String help, String usage, Collection<String> alternatives) {
            this.word = word;
            this.help = help;
            this.usage = usage;
            this.alternatives = alternatives;
        }

        public String getHelpMessage() {
            String result = word + ": " + help + " [" + usage + "]";
            return result;
        }

        public Collection<String> getKeys() {
            Collection<String> words = new HashSet<>(alternatives);
            words.add(word);
            return words;
        }
    }

    private static Logger logger = Logger.getLogger(LoginClientScannerProtocol.class);

    public static boolean protocol(String message) {

        List<String> words = splitMessage(message);

        // If message is empty, then it tries to connect
        if(words.isEmpty()) {
            if(!LoginClient.isConnected()) {
                return start(Optional.empty(), Optional.empty());
            }
            return false;
        }

        String firstWord = words.get(0);
        String word = firstWord.toLowerCase();

        if(MessageType.START.getKeys().contains(word)) {
            if(words.size() == 1) {
                return start(Optional.empty(), Optional.empty());
            } else if (words.size() == 2) {
                return start(Optional.of(words.get(1)), Optional.empty());
            } else {
                return start(Optional.of(words.get(1)), Optional.of(Integer.parseInt(words.get(2))));
            }
        }

        if(MessageType.CLOSE.getKeys().contains(word)) {
            return close();
        }

        if(MessageType.EXIT.getKeys().contains(word)) {
            return exit();
        }

        if(MessageType.LOGIN.getKeys().contains(word)) {
            if (words.size() < 3) {
                Display.alert("Not enough arguments");
                return false;
            }
            return login(words.get(1), words.get(2));
        }

        if(MessageType.REGISTER.getKeys().contains(word)) {
            if (words.size() < 3) {
                Display.alert("Not enough arguments");
                return false;
            }
            return register(words.get(1), words.get(2));
        }
        if(MessageType.LOGOUT.getKeys().contains(word)) {
            return logout();
        }

        if(MessageType.RELOGIN.getKeys().contains(word)) {
            return relogin();
        }

        if(MessageType.HELP.getKeys().contains(word)) {
            return help();
        }

        if(MessageType.INFO.getKeys().contains(word)) {
            return info();
        }

        if(MessageType.PLAYERS.getKeys().contains(word)) {
            return players();
        }

        if(MessageType.GAMES.getKeys().contains(word)) {
            return games();
        }

        if(MessageType.INVITES.getKeys().contains(word)) {
            return invites();
        }

        if(MessageType.INVITE.getKeys().contains(word)) {
            if (words.size() < 2) {
                Display.alert("Not enough arguments");
                return false;
            }
            return invite(words.get(1), words.subList(2, words.size()));
        }

        if(MessageType.ANSWER.getKeys().contains(word)) {
            if (words.size() < 3) {
                Display.alert("Not enough arguments");
                return false;
            }
            return answer(words.get(1), words.get(2));
        }

        if(MessageType.TOKEN.getKeys().contains(word)) {
            return token();
        }

        if(MessageType.ERROR.getKeys().contains(word)) {
            return message(message.substring(message.indexOf(word) + word.length()).trim(),
                    ChatMessage.ChatMessageType.ERROR);
        }

        if(MessageType.MESSAGE.getKeys().contains(word)) {
            return message(message.substring(message.indexOf(firstWord) + firstWord.length()).trim(),
                    ChatMessage.ChatMessageType.NORMAL);
        }

        Display.alert("Unknown message");
        logger.info("Trying to send an unknown message '" + message + "'.");
        return false;
    }

    private static List<String> splitMessage(String message) {
        List<String> words = (new ArrayList<>());
        words.addAll(Arrays.asList(message.split("\\s+")));
        while(words.remove("")) { }
        return words;
    }

    private static boolean login(String name, String password) {
        if (!LoginClient.isConnected()) {
            Display.alert("No connection");
            logger.warn("Trying to log in before connect");
            return false;
        }
        if (LoginClient.isLoggedIn()) {
            logger.info("Trying to log in while already logged in.");
        }
        return LoginClient.sendMessage(new LoginRequest(name, password));
    }

    // We do not accept a new registry from a logged in client
    private static boolean register(String name, String password) {
        if (!LoginClient.isConnected()) {
            Display.alert("No connection");
            logger.warn("Trying to register before connect");
            return false;
        }
        if (LoginClient.isLoggedIn()) {
            logger.info("Trying to register while already logged in.");
        }
        return LoginClient.sendMessage(new RegisterRequest(name, password));
    }

    private static boolean logout() {
        boolean result = true;

        if(LoginClient.isConnected()) {
            if(!LoginClient.sendMessage(new LogoutRequest())) {
                result = false;
            }
        }

        if(!LoginClient.isLoggedIn()) {
            Display.alert("Already logged out");
            logger.info("Trying to log out when is already logged out");
            result = false;
        } else {
            if(!LoginClient.logout()) {
                result = false;
            }
        }
        return result;
    }

    private static boolean relogin() {
        if (!LoginClient.isConnected()) {
            Display.alert("No connection");
            logger.warn("Trying to re log in before connect");
            return false;
        }
        if(!LoginClient.isLoggedIn()) {
            Display.alert("No token");
            logger.warn("Trying to re log in without token");
            return false;
        }
        return LoginClient.sendMessage(new ReLoginRequest(LoginClient.getToken()));
    }

    private static boolean start(Optional<String> ip, Optional<Integer> port) {
        if(LoginClient.isConnected()) {
            logger.info("Trying to open socket when it is already open");
            Display.alert("Already connected");
            return false;
        }
        return LoginClient.connect(ip, port);
    }

    private static boolean close() {
        if (LoginClient.isConnected()) {
            LoginClient.sendMessage(new CloseConnectionRequest());
        }
        return LoginClient.disconnect();
    }

    private static boolean message(String message, ChatMessage.ChatMessageType messageType) {
        List<String> words = splitMessage(message);

        String word = words.get(0);
        GenericMessage chatMessage;
        String shortMessage = message.substring(message.indexOf(word) + word.length()).trim();

        try {
            Integer person = Integer.parseInt(word);
            chatMessage = new ChatMessageToServer(shortMessage, messageType, person);
        } catch (NumberFormatException e) {
            try {
                ChatMessageToServer.Destiny destiny = ChatMessageToServer.Destiny.valueOf(word.toUpperCase());
                chatMessage = new ChatMessageToServer(shortMessage, messageType, destiny);
            } catch (IllegalArgumentException e1) {
                logger.info("Wrong message '" + message + "'");
                Display.alert("Wrong message");
                return false;
            }
        }

        if(LoginClient.isConnected()) {
            return LoginClient.sendMessage(chatMessage);
        }
        logger.info("Trying to send a message while connection is off");
        Display.alert("No connection");
        return false;
    }

    private static boolean info() {
        Display.cleanColor("Connected: " + Boolean.toString(LoginClient.isConnected()));
        Display.cleanColor("Logged in: " + Boolean.toString(LoginClient.isLoggedIn()));
        if(LoginClient.isLoggedIn()) {
            Display.cleanColor("Me: " + LoginClient.getMe());
            Display.cleanColor("Token: " + LoginClient.getToken());
        }
        return true;
    }

    private static boolean players() {
        return LoginClient.sendMessage(new ListOfPlayersRequest());
    }

    private static boolean games() {
        for (GameType game: GameType.values()) {
            Display.cleanColor(game.toString());
        }
        return true;
    }

    private static boolean invites() {
        return LoginClient.sendMessage(new ListOfInvitesRequest());
    }

    private static boolean invite(String gameId, List<String> message) {
        try {
            GameType game = getGameType(Integer.parseInt(gameId));
            if (game == null) {
                Display.alert("Game " + gameId + " does not exist");
                return false;
            }
            if (message.isEmpty()) {
                return LoginClient.sendMessage(new Invite(game, null));
            }
            List<Integer> players = new ArrayList<>();
            for (String word: message) {
                try {
                    players.add(Integer.parseInt(word));
                } catch (NumberFormatException e) {
                    break;
                }
            }
            if (!players.isEmpty()) {
                return LoginClient.sendMessage(new Invite(game, players));
            }
        } catch (NumberFormatException e) { }
        Display.alert(gameId + " is not a number");
        return false;
    }

    private static boolean answer(String gameId, String answer) {
        try {
            Integer id = Integer.parseInt(gameId);
            switch (answer.toLowerCase()) {
                case "n":
                case "no":
                case "refuse":
                    return LoginClient.sendMessage(new RespondToInvite(id, false));
                case "y":
                case "yes":
                case "accept":
                    return LoginClient.sendMessage(new RespondToInvite(id, true));
                default:
                    Display.alert("Unknown message " + answer);
                    return false;
            }
        } catch (NumberFormatException e) {
            Display.alert(gameId + " is not a number");
            return false;
        }
    }

    private static boolean token() {
        if(!LoginClient.isLoggedIn()) {
            Display.cleanColor("No token");
            logger.info("Cannot show token because there isn't any.");
            return false;
        }
        Display.cleanColor("Token: " + LoginClient.getToken());
        return true;
    }

    private static boolean help() {
        for(MessageType messageType: MessageType.values()) {
            Display.cleanColor(messageType.getHelpMessage());
        }
        return true;
    }

    private static boolean exit() {
        LoginClient.close();
        return true;
    }

    private static GameType getGameType(Integer id) {
        for (GameType game: GameType.values()) {
            if (game.getId() == id) {
                return game;
            }
        }
        return null;
    }
}
