package pt.dinis.client.login;

import org.apache.log4j.Logger;
import pt.dinis.common.Display;

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
                "login; login ip; login ip port", Collections.emptyList()),
        LOGOUT("logout", "Logs out of the server",
                "logout", Collections.emptyList()),
        RELOGIN("relogin", "Redo the login",
                "relogin", Arrays.asList("re-login")),
        START("start", "Starts a communication with the server",
                "start", Arrays.asList("open", "connect")),
        CLOSE("close", "Closes a communication with the server",
                "close", Arrays.asList("disconnect")),
        MESSAGE("message", "Sends a message to the server",
                "message text; text", Collections.emptyList()),
        HASH("hash", "Print hash given from server while logging in",
                "hash", Collections.emptyList()),
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

        // If message is empty, then it tries to start, login or relogin
        if(words.isEmpty()) {
            if(LoginClient.isConnected()) {
                if (LoginClient.isLoggedIn()) {
                    return relogin();
                }
                return login();
            }
            return start(Optional.empty(), Optional.empty());
        }

        String word = words.get(0).toLowerCase();

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
            return login();
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

        if(MessageType.HASH.getKeys().contains(word)) {
            return hash();
        }

        if(MessageType.MESSAGE.getKeys().contains(word)) {
            return message(message.substring(message.indexOf(word) + word.length()).trim());
        }

        return message(message);
    }

    private static List<String> splitMessage(String message) {
        List<String> words = (new ArrayList<>());
        words.addAll(Arrays.asList(message.split("\\s+")));
        while(words.remove("")) { }
        return words;
    }

    private static boolean login() {
        if (!LoginClient.isConnected()) {
            Display.alert("No connection");
            logger.warn("Trying to log in before connect");
            return false;
        }
        if (LoginClient.isLoggedIn()) {
            logger.info("Trying to log in while already logged in.");
        }
        return LoginClient.sendMessage("login");
    }

    private static boolean logout() {
        boolean result = true;

        if(LoginClient.isConnected()) {
            if(!LoginClient.sendMessage("logout")) {
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
            Display.alert("No hash");
            logger.warn("Trying to re log in without hash");
            return false;
        }
        return LoginClient.sendMessage("relogin " + LoginClient.getHash());
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
        LoginClient.sendMessage("disconnect");
        return LoginClient.disconnect();
    }

    private static boolean message(String message) {
        if(LoginClient.isConnected()) {
            return LoginClient.sendMessage(message);
        }
        logger.info("Trying to send a message while connection is off");
        Display.alert("No connection");
        return false;
    }

    private static boolean info() {
        Display.cleanColor("Connected: " + Boolean.toString(LoginClient.isConnected()));
        Display.cleanColor("Logged in: " + Boolean.toString(LoginClient.isLoggedIn()));
        if(LoginClient.isLoggedIn()) {
            Display.cleanColor("Hash: " + LoginClient.getHash());
        }
        return true;
    }

    private static boolean hash() {
        if(!LoginClient.isLoggedIn()) {
            Display.alert("Not logged in");
            logger.info("Cannot show hash because there isn't any.");
            return false;
        }
        Display.info("Hash: " + LoginClient.getHash());
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
