package pt.dinis.client.login;

import org.apache.log4j.Logger;
import pt.dinis.main.Display;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

/**
 * Created by tiago on 22-01-2017.
 */
public class LoginClientScannerProtocol {

    public enum MessageType {
        HELP("help", "Displays this message",
                "help", Arrays.asList("h")),
        INFO("info", "xxx Shows client information",
                "info", Arrays.asList("information", "status")),
        LOGIN("login", "xxx Logs in the server",
                "login; login ip; login ip port", Collections.emptyList()),
        LOGOUT("logout", "xxx Logs out of the server",
                "logout", Collections.emptyList()),
        RELOGIN("relogin", "xxx Redo the login",
                "relogin", Arrays.asList("re-login")),
        START("start", "Starts a communication with the server",
                "start", Arrays.asList("open", "connect")),
        CLOSE("close", "Closes a communication with the server",
                "close", Arrays.asList("disconnect")),
        MESSAGE("message", "Sends a message to the server",
                "message text; text", Collections.emptyList()),
        HASH("hash", "xxx print hash given from server while logging in",
                "hash", Collections.emptyList()),
        EXIT("exit", "xxx Ends this client",
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
            if(!LoginClient.isConnected()) {
                return start(Optional.empty(), Optional.empty());
            }
            if (!LoginClient.isLogged()) {
                if (!LoginClient.hasHash()) {
                    return login();
                }
                return relogin();
            }
            return true;
        }

        String word = words.get(0);

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

    // TODO
    private static boolean login() {
        throw new NotImplementedException();
    }

    // TODO
    private static boolean logout() {
        throw new NotImplementedException();
    }

    // TODO
    private static boolean relogin() {
        throw new NotImplementedException();
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

    // TODO
    private static boolean info() {
        throw new NotImplementedException();
    }

    // TODO
    private static boolean hash() {
        throw new NotImplementedException();
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
