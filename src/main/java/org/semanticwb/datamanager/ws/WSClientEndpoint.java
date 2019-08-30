package org.semanticwb.datamanager.ws;

import java.net.URI;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

/**
 * Web Sockets Client Endpoint
 *
 * @author javier.solis
 */
@ClientEndpoint
public class WSClientEndpoint {

    private Session userSession = null;
    private MessageHandler messageHandler;
    private URI endpointURI=null;
    boolean reconnect=false;
    

    public WSClientEndpoint(URI endpointURI, boolean reconnect) {
        this.endpointURI=endpointURI;
        this.reconnect=reconnect;
        init();
    }
    
    private void init(){
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }        
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
        System.out.println("opening websocket "+endpointURI);
        this.userSession = userSession;
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        System.out.println("closing websocket "+endpointURI);
        this.userSession = null;
        if(reconnect)
        {
            init();
        }
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param message The text message
     */
    @OnMessage
    public void onMessage(String message) {
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }
    }

    /**
     * register message handler
     *
     * @param msgHandler
     */
    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    /**
     * Send a message.
     *
     * @param message
     */
    public void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }

    public Session getUserSession() {
        return userSession;
    }

    /**
     * Message handler.
     *
     * @author javier.solis
     */
    public static interface MessageHandler {

        public void handleMessage(String message);
    }
}