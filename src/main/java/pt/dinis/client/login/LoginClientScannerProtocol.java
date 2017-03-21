package pt.dinis.client.login;

import org.apache.log4j.Logger;
import pt.dinis.common.Display;
import pt.dinis.common.messages.basic.CloseConnectionRequest;
import pt.dinis.common.messages.chat.ChatMessage;
import pt.dinis.common.messages.chat.ChatMessageToServer;
import pt.dinis.common.messages.user.LoginRequest;
import pt.dinis.common.messages.user.LogoutRequest;
import pt.dinis.common.messages.user.ReLoginRequest;
import pt.dinis.common.messages.user.RegisterRequest;

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

        ChatMessageToServer.Destiny destiny = ChatMessageToServer.Destiny.SPECIFIC;
        try {
            destiny = ChatMessageToServer.Destiny.valueOf(word.toUpperCase());
        } catch (IllegalArgumentException e) {}

        Integer person = null;
        if (destiny == ChatMessageToServer.Destiny.SPECIFIC) {
            try {
                person = Integer.parseInt(word);
            } catch (NumberFormatException e) {
                logger.info("Trying to send a message that cannot be understood '" + message + "'.");
                Display.alert("Wrong message destiny");
            }
        }

        message = message.substring(message.indexOf(word) + word.length()).trim();

        if(LoginClient.isConnected()) {
            return LoginClient.sendMessage(
                    new ChatMessageToServer(message, messageType, destiny, person));
        }
        logger.info("Trying to send a message while connection is off");
        Display.alert("No connection");
        return false;
    }

    private static boolean info() {
        Display.cleanColor("Connected: " + Boolean.toString(LoginClient.isConnected()));
        Display.cleanColor("Logged in: " + Boolean.toString(LoginClient.isLoggedIn()));
        if(LoginClient.isLoggedIn()) {
            Display.cleanColor("Token: " + LoginClient.getToken());
        }
        return true;
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
}
