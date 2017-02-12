package pt.dinis.main;

import pt.dinis.common.Display;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

/**
 * Created by tiago on 03-02-2017.
 */
public class ServerScannerProtocol {

    public enum MessageType {
        HELP("help", "Displays this message",
                "help", Arrays.asList("h")),
        INFO("info", "Show clients information",
                "info", Arrays.asList("information", "status")),
        GET("get", "Show a clients list",
                "get", Arrays.asList("list")),
        LOGIN("login", "Replies to login demand",
                "login i", Collections.emptyList()),
        LOGOUT("logout", "Logs out client i",
                "logout i", Collections.emptyList()),
        CLOSE("close", "Closes a communication with client i",
                "close i", Arrays.asList("disconnect")),
        MESSAGE("message", "Sends a message to client i",
                "message i text", Collections.emptyList()),
        EXIT("exit", "Kill this server",
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

    public static boolean protocol(String message) {

        List<String> words = splitMessage(message);

        if(words.isEmpty()) {
            return true;
        }

        String word = words.get(0).toLowerCase();

        if(MessageType.HELP.getKeys().contains(word)) {
            return help();
        }

        if(MessageType.GET.getKeys().contains(word)) {
            return get();
        }

        if(MessageType.INFO.getKeys().contains(word)) {
            return info();
        }

        if(MessageType.EXIT.getKeys().contains(word)) {
            return exit();
        }

        Collection<Integer> ids;
        if(words.get(1).equals("all")) {
            ids = Dealer.getActiveClients();
        } else {
            ids = Collections.singleton(Integer.parseInt(words.get(1)));
        }

        if(MessageType.LOGIN.getKeys().contains(word)) {
            return login(ids);
        }

        if(MessageType.LOGOUT.getKeys().contains(word)) {
            return logout(ids);
        }

        if(MessageType.CLOSE.getKeys().contains(word)) {
            return close(ids);
        }

        if(MessageType.MESSAGE.getKeys().contains(word)) {
            return message(ids, message.substring(message.indexOf(words.get(1)) + words.get(1).length()).trim());
        }

        return false;
    }

    private static List<String> splitMessage(String message) {
        List<String> words = (new ArrayList<>());
        words.addAll(Arrays.asList(message.split("\\s+")));
        while(words.remove("")) { }
        return words;
    }

    private static boolean get() {
        Display.cleanColor("Clients list: " + Dealer.getActiveClients());
        return true;
    }

    private static boolean login(Collection<Integer>ids) {
        if(ids.size() > 1) {
            Display.alert("Cannot reply to more than one login demand");
            return false;
        }
        boolean result = true;
        for (int id: ids) {
            result = Dealer.loginClient(id);
        }
        return result;
    }

    private static boolean logout(Collection<Integer> ids) {
        boolean result = true;
        for(int id: ids) {
            if (!Dealer.logoutClient(id)) {
                result = false;
            }
        }
        if(!Dealer.sendMessage(ids, "logout")) {
            result = false;
        }
        return result;
    }

    private static boolean close(Collection<Integer> ids) {
        boolean result = true;
        if(!Dealer.sendMessage(ids, "disconnect")) {
            result = false;
        }
        for (Integer id: ids) {
            if(!Dealer.disconnectClient(id)) {
                result = false;
            }
        }
        return result;
    }

    private static boolean message(Collection<Integer> ids, String message) {
        return Dealer.sendMessage(ids, message);
    }

    private static boolean info() {
        Collection<Integer> ids = Dealer.getActiveClients();
        if (!ids.isEmpty()) {
            Display.cleanColor("Connected clients:");
            for(int id: ids) {
                if (LoginManager.isLogged(id)) {
                    Display.cleanColor("Client " + id + " logged in with hash " + LoginManager.getClientHash(id));
                } else {
                    Display.cleanColor("Client " + id + " not logged in");
                }
            }
        }
        Map<String, Integer> disconnected = LoginManager.getLoggedClients(ids);
        if (!disconnected.isEmpty()) {
            Display.cleanColor("Disconnected clients:");
            for (Map.Entry<String, Integer> entry : disconnected.entrySet()) {
                Display.cleanColor("Defunct client " + entry.getValue() + " was logged in with hash " + entry.getKey());
            }
        }
        return true;
    }

    private static boolean help() {
        for(MessageType messageType: MessageType.values()) {
            Display.cleanColor(messageType.getHelpMessage());
        }
        return true;
    }

    private static boolean exit() {
        boolean result = Dealer.sendMessage(Dealer.getActiveClients(), "disconnect");
        Dealer.stop();
        return result;
    }
}
