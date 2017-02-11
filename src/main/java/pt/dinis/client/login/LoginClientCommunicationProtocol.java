package pt.dinis.client.login;

import pt.dinis.common.Display;

import java.util.*;

/**
 * Created by tiago on 03-02-2017.
 */
public class LoginClientCommunicationProtocol {

    public enum MessageType {
        LOGIN("login"),
        LOGOUT("logout"),
        CLOSE("disconnect"),
        MESSAGE("message"),
        ERROR("error");

        private String word;

        MessageType(String word) {
            this.word = word;
        }

        String getWord() {
            return word;
        }
    }

    public static boolean protocol(String message) {

        List<String> words = splitMessage(message);

        // If message is empty, then it tries to start, login or relogin
        if(words.isEmpty()) {
            return false;
        }

        String word = words.get(0).toLowerCase();

        if(MessageType.LOGIN.getWord().equals(word)) {
            if(words.size() != 2) {
                Display.alert("Unknown message '" + message + "'");
                return false;
            }
            return login(words.get(1));
        }

        if(MessageType.CLOSE.getWord().equals(word)) {
            return close();
        }

        if(MessageType.LOGOUT.getWord().equals(word)) {
            return logout();
        }

        if(MessageType.ERROR.getWord().equals(word)) {
            return error(message.substring(message.indexOf(word) + word.length()).trim());
        }

        if(MessageType.MESSAGE.getWord().equals(word)) {
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

    private static boolean login(String hash) {
        Display.info("New hash");
        return LoginClient.setHash(hash);
    }

    private static boolean logout() {
        Display.info("Logged out");
        return LoginClient.logout();
    }

    private static boolean close() {
        Display.alert("Sent out by server");
        return LoginClient.disconnect();
    }

    private static boolean message(String message) {
        Display.display(message);
        return true;
    }

    private static boolean error(String message) {
        Display.alert(message);
        return true;
    }
}
