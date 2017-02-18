package pt.dinis.common.messages;

import java.util.Optional;

/**
 * Created by tiago on 16-02-2017.
 */
public class AuthenticatedMessage implements GenericMessage {

     private GenericMessage message;
     private String token;

     public AuthenticatedMessage(GenericMessage message, String token) {
          if (message == null) {
               throw new IllegalArgumentException();
          }
          this.message = message;
          this.token = token;
     }

     public GenericMessage getMessage() {
          return message;
     }

     public String getToken() {
          return token;
     }

     public boolean isAuthenticated() {
          return (token != null);
     }

     @Override
     public Direction getDirection() {
          return message.getDirection();
     }

     @Override
     public String toString() {
          return "AuthenticatedMessage{" +
                  "message=" + message +
                  '}';
     }
}
