package pt.dinis.temporary;

import java.sql.Connection;
import java.util.List;

/**
 * Created by diogo on 10-02-2017.
 */
public class LoginWorkerThread extends WorkerThread {

    List<String> words;

    public LoginWorkerThread (List<String> words) {
        this.words = words;
    }

    @Override
    protected boolean working(Connection connection) {
        // go to database and confirm if the user exist in DB and if is authentication is correct.


        return false;
    }
}
