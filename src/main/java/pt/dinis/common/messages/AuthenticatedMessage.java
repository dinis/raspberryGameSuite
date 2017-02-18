package pt.dinis.common.messages;

/**
 * Created by tiago on 16-02-2017.
 */
public interface AuthenticatedMessage extends GenericMessage {

     String getToken();
}
