package pt.dinis;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import pt.dinis.common.messages.AuthenticatedMessage;
import pt.dinis.common.messages.GenericMessage;
import pt.dinis.common.messages.MessagesUtils;
import pt.dinis.common.messages.basic.CloseConnectionOrder;
import pt.dinis.common.messages.basic.CloseConnectionRequest;
import pt.dinis.common.messages.chat.ChatMessage;
import pt.dinis.common.messages.chat.ChatMessageToClient;
import pt.dinis.common.messages.chat.ChatMessageToServer;
import pt.dinis.common.messages.user.*;

import java.io.IOException;

/**
 * Created by tiago on 26-02-2017.
 */
public class JacksonTest extends TestCase {
    public JacksonTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(JacksonTest.class);
    }

    public void testChatMessageToClient() throws IOException {
        ChatMessageToClient message = new ChatMessageToClient("message", ChatMessage.ChatMessageType.ERROR);
        GenericMessage newMessage = serializeObject(message);
        assertTrue(newMessage.toString().equals(message.toString()));
    }

    public void testChatMessageToServer() throws IOException {
        ChatMessageToServer message = new ChatMessageToServer("message", ChatMessage.ChatMessageType.ERROR, ChatMessageToServer.Destiny.ECHO, null);
        GenericMessage newMessage = serializeObject(message);
        assertTrue(newMessage.toString().equals(message.toString()));
    }

    public void testCloseConnectionOrder() throws IOException {
        CloseConnectionOrder message = new CloseConnectionOrder();
        GenericMessage newMessage = serializeObject(message);
        assertTrue(newMessage.toString().equals(message.toString()));
    }

    public void testCloseConnectionRequest() throws IOException {
        CloseConnectionRequest message = new CloseConnectionRequest();
        GenericMessage newMessage = serializeObject(message);
        assertTrue(newMessage.toString().equals(message.toString()));
    }

    public void testLoginAnswer() throws IOException {
        LoginAnswer message = new LoginAnswer(UserMessage.AnswerType.SUCCESS, "token", null);
        GenericMessage newMessage = serializeObject(message);
        assertTrue(newMessage.toString().equals(message.toString()));
    }

    public void testLoginRequest() throws IOException {
        LoginRequest message = new LoginRequest("name", "pass");
        GenericMessage newMessage = serializeObject(message);
        assertTrue(newMessage.toString().equals(message.toString()));
    }

    public void testLogoutOrder() throws IOException {
        LogoutOrder message = new LogoutOrder();
        GenericMessage newMessage = serializeObject(message);
        assertTrue(newMessage.toString().equals(message.toString()));
    }

    public void testLogoutRequest() throws IOException {
        LogoutRequest message = new LogoutRequest();
        GenericMessage newMessage = serializeObject(message);
        assertTrue(newMessage.toString().equals(message.toString()));
    }

    public void testRegisterAnswer() throws IOException {
        RegisterAnswer message = new RegisterAnswer(UserMessage.AnswerType.SUCCESS, "token", null);
        GenericMessage newMessage = serializeObject(message);
        assertTrue(newMessage.toString().equals(message.toString()));
    }

    public void testReLoginAnswer() throws IOException {
        ReLoginAnswer message = new ReLoginAnswer(UserMessage.AnswerType.ERROR, "no");
        GenericMessage newMessage = serializeObject(message);
        assertTrue(newMessage.toString().equals(message.toString()));
    }

    public void testReloginRequest() throws IOException {
        ReLoginRequest message = new ReLoginRequest("token");
        GenericMessage newMessage = serializeObject(message);
        assertTrue(newMessage.toString().equals(message.toString()));
    }

    public void testAuthenticatedChatMessageToClient() throws IOException {
        ChatMessageToClient message = new ChatMessageToClient("message", ChatMessage.ChatMessageType.ERROR);
        AuthenticatedMessage authenticatedMessage = new AuthenticatedMessage(message, "token");
        GenericMessage newMessage = serializeObject(authenticatedMessage);
        assertTrue(newMessage.toString().equals(authenticatedMessage.toString()));
        assertEquals(authenticatedMessage.getToken(), ((AuthenticatedMessage) newMessage).getToken());
    }

    public void testAuthenticatedChatMessageToServer() throws IOException {
        ChatMessageToServer message = new ChatMessageToServer("message", ChatMessage.ChatMessageType.ERROR, ChatMessageToServer.Destiny.ECHO, null);
        AuthenticatedMessage authenticatedMessage = new AuthenticatedMessage(message, "token");
        GenericMessage newMessage = serializeObject(authenticatedMessage);
        assertTrue(newMessage.toString().equals(authenticatedMessage.toString()));
        assertEquals(authenticatedMessage.getToken(), ((AuthenticatedMessage) newMessage).getToken());
    }

    public void testAuthenticatedCloseConnectionOrder() throws IOException {
        CloseConnectionOrder message = new CloseConnectionOrder();
        AuthenticatedMessage authenticatedMessage = new AuthenticatedMessage(message, "token");
        GenericMessage newMessage = serializeObject(authenticatedMessage);
        assertTrue(newMessage.toString().equals(authenticatedMessage.toString()));
        assertEquals(authenticatedMessage.getToken(), ((AuthenticatedMessage) newMessage).getToken());
    }

    public void testAuthenticatedCloseConnectionRequest() throws IOException {
        CloseConnectionRequest message = new CloseConnectionRequest();
        AuthenticatedMessage authenticatedMessage = new AuthenticatedMessage(message, "token");
        GenericMessage newMessage = serializeObject(authenticatedMessage);
        assertTrue(newMessage.toString().equals(authenticatedMessage.toString()));
        assertEquals(authenticatedMessage.getToken(), ((AuthenticatedMessage) newMessage).getToken());
    }

    public void testAuthenticatedLoginAnswer() throws IOException {
        LoginAnswer message = new LoginAnswer(UserMessage.AnswerType.SUCCESS, "token", null);
        AuthenticatedMessage authenticatedMessage = new AuthenticatedMessage(message, "token");
        GenericMessage newMessage = serializeObject(authenticatedMessage);
        assertTrue(newMessage.toString().equals(authenticatedMessage.toString()));
        assertEquals(authenticatedMessage.getToken(), ((AuthenticatedMessage) newMessage).getToken());
    }

    public void testAuthenticatedLoginRequest() throws IOException {
        LoginRequest message = new LoginRequest("name", "pass");
        AuthenticatedMessage authenticatedMessage = new AuthenticatedMessage(message, "token");
        GenericMessage newMessage = serializeObject(authenticatedMessage);
        assertTrue(newMessage.toString().equals(authenticatedMessage.toString()));
        assertEquals(authenticatedMessage.getToken(), ((AuthenticatedMessage) newMessage).getToken());
    }

    public void testAuthenticatedLogoutOrder() throws IOException {
        LogoutOrder message = new LogoutOrder();
        AuthenticatedMessage authenticatedMessage = new AuthenticatedMessage(message, "token");
        GenericMessage newMessage = serializeObject(authenticatedMessage);
        assertTrue(newMessage.toString().equals(authenticatedMessage.toString()));
        assertEquals(authenticatedMessage.getToken(), ((AuthenticatedMessage) newMessage).getToken());
    }

    public void testAuthenticatedLogoutRequest() throws IOException {
        LogoutRequest message = new LogoutRequest();
        AuthenticatedMessage authenticatedMessage = new AuthenticatedMessage(message, "token");
        GenericMessage newMessage = serializeObject(authenticatedMessage);
        assertTrue(newMessage.toString().equals(authenticatedMessage.toString()));
        assertEquals(authenticatedMessage.getToken(), ((AuthenticatedMessage) newMessage).getToken());
    }

    public void testAuthenticatedRegisterAnswer() throws IOException {
        RegisterAnswer message = new RegisterAnswer(UserMessage.AnswerType.SUCCESS, "token", null);
        AuthenticatedMessage authenticatedMessage = new AuthenticatedMessage(message, "token");
        GenericMessage newMessage = serializeObject(authenticatedMessage);
        assertTrue(newMessage.toString().equals(authenticatedMessage.toString()));
        assertEquals(authenticatedMessage.getToken(), ((AuthenticatedMessage) newMessage).getToken());
    }

    public void testAuthenticatedReLoginAnswer() throws IOException {
        ReLoginAnswer message = new ReLoginAnswer(UserMessage.AnswerType.ERROR, "no");
        AuthenticatedMessage authenticatedMessage = new AuthenticatedMessage(message, "token");
        GenericMessage newMessage = serializeObject(authenticatedMessage);
        assertTrue(newMessage.toString().equals(authenticatedMessage.toString()));
        assertEquals(authenticatedMessage.getToken(), ((AuthenticatedMessage) newMessage).getToken());
    }

    public void testAuthenticatedReloginRequest() throws IOException {
        ReLoginRequest message = new ReLoginRequest("token");
        AuthenticatedMessage authenticatedMessage = new AuthenticatedMessage(message, "token");
        GenericMessage newMessage = serializeObject(authenticatedMessage);
        assertTrue(newMessage.toString().equals(authenticatedMessage.toString()));
        assertEquals(authenticatedMessage.getToken(), ((AuthenticatedMessage) newMessage).getToken());
    }

    private GenericMessage serializeObject(GenericMessage message) throws IOException {
        System.out.println(message);
        String json = MessagesUtils.encode(message);
        System.out.println(json);
        GenericMessage genericMessage = MessagesUtils.decode(json);
        System.out.println(genericMessage);
        return genericMessage;
    }
}
