package pt.dinis.client.login;

import org.apache.log4j.Logger;
import pt.dinis.main.Display;

import java.util.*;

/**
 * Created by tiago on 22-01-2017.
 */
public class LoginClientScannerProtocol {

    public enum MessageType {
        HELP("help", "Displays this message",
                "help", Arrays.asList("h")),
        LOGIN("login", "xxx Logs in the server",
                "login; login ip; login ip port", Collections.emptyList()),
        LOGOUT("logout", "xxx Logs out of the server",
                "logout", Collections.emptyList()),
        RELOGIN("relogin", "xxx Redo the login",
                "relogin", Arrays.asList("re-login")),
        START("start", "xxx Starts a communication with the server",
                "start", Arrays.asList("open", "connect")),
        CLOSE("close", "xxx Closes a communication with the server",
                "close", Arrays.asList("disconnect")),
        MESSAGE("message", "xxx Sends a message to the server",
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
            String result = word + ": " + help + "\n\tUsage: " + usage;
            return result;
        }

        public Collection<String> getKeys() {
            Collection<String> words = new HashSet<String>(alternatives);
            words.add(word);
            return words;
        }
    }

    private static Logger logger = Logger.getLogger(LoginClientScannerProtocol.class);

    // TODO
    public static boolean protocol(String message) {

        String[] splitMessage = message.split("\\s+");
        if(splitMessage.length == 0) {
            // TODO: this should do login and relogin
            if(!LoginClient.isConnected()) {
                return start(Optional.empty(), Optional.empty());
            }
            return message(message);
        }

        String word = splitMessage[0].toLowerCase();

        if(MessageType.START.getKeys().contains(word)) {
            if(splitMessage.length == 1) {
                return start(Optional.empty(), Optional.empty());
            } else if (splitMessage.length == 2) {
                return start(Optional.of(splitMessage[1]), Optional.empty());
            } else {
                return start(Optional.of(splitMessage[1]), Optional.of(Integer.parseInt(splitMessage[2])));
            }
        }

        if(MessageType.CLOSE.getKeys().contains(word)) {
            return close();
        }

        if(MessageType.EXIT.getKeys().contains(word)) {
            return exit();
        }

        return true;
    }

    // TODO
    private static boolean login() {
        return true;
    }

    // TODO
    private static boolean logout() {
        return true;
    }

    // TODO
    private static boolean relogin() {
        return true;
    }

    private static boolean start(Optional<String> ip, Optional<Integer> port) {
        if(LoginClient.isConnected()) {
            logger.info("Trying to open socket when it is already open");
            Display.alert("Already connected");
            return false;
        }
        return LoginClient.connect(ip, port);
    }

    // TODO
    private static boolean close() {
        return true;
    }

    private static boolean message(String message) {
        return LoginClient.sendMessage(message);
    }

    // TODO
    private static boolean hash() {
        return true;
    }

    private static boolean help() {
        for(MessageType messageType: MessageType.values()) {
            Display.clean(messageType.getHelpMessage());
        }
        return true;
    }

    private static boolean exit() {
        LoginClient.close();
        return true;
    }
}
