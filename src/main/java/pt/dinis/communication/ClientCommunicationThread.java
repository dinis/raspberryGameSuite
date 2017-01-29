package pt.dinis.communication;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import pt.dinis.main.Dealer;
import pt.dinis.temporary.WorkerThread;

import java.io.*;
import java.net.Socket;

/**
 * Created by tiago on 22-01-2017.
 */
public class ClientCommunicationThread extends Thread{

    private final static Logger logger = Logger.getLogger(ClientCommunicationThread.class);

    private Integer id;
    private Socket socket;
    private boolean running;
    private PrintWriter out;
    private BufferedReader in;
    private final DateTime time;

    public ClientCommunicationThread(Socket socket, Integer id) throws IOException {

        this.id = id;
        this.socket = socket;
        this.running = true;
        this.time = new DateTime();

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        while(running) {
            try {
                String message = in.readLine();
                logger.debug("Receiving and sending a message");
                WorkerThread temporaryThread = new WorkerThread(message, id);
                temporaryThread.run();
            } catch (IOException e) {
                if(!toContinue()) {
                    Dealer.closeClient(id);
                }
                logger.warn("Problem receiving message", e);
            }
        }
    }

    public boolean close() {
        logger.info("Closing thread of client " + id + " opened at " + time.toString());

        boolean result = true;
        running = false;

        try {
            in.close();
        } catch (IOException e) {
            logger.info("Problem closing socket input of client " + id + ".");
            result = false;
        }

        out.close();

        try {
            socket.close();
        } catch (IOException e) {
            logger.info("Problem closing socket of client " + id + ".");
            result = false;
        }

        return result;
    }

    private boolean toContinue() {
        if(!socket.isConnected()) {
            return false;
        }
        try {
            return in.ready();
        } catch (IOException e) {
            return false;
        }
    }

    public boolean sendMessage(String message) {
        if (!toContinue()) {
            Dealer.closeClient(id);
            return false;
        }

        out.println(message);
        return true;
    }
}
