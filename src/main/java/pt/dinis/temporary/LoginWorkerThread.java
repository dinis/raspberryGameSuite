package pt.dinis.temporary;

import pt.dinis.dataaccess.DBConnection;
import java.util.List;

/**
 * Created by diogo on 10-02-2017.
 */
public class LoginWorkerThread extends WorkerThread {

    public LoginWorkerThread (List<String> words) {
        super(words);
    }

    @Override
    protected boolean working(List<String> words, DBConnection connection) {
        // go to database and confirm if the user exist in DB and if is authentication is correct.
        

        return false;
    }
}
