package pt.dinis.common.messages;

import java.io.Serializable;

/**
 * Created by tiago on 16-02-2017.
 */
public interface AuthenticatedGenericMessage extends GenericMessage {

     // we can declare an enum in a interface :)
     enum type {
          A, B
     }

     String getToken();
}
