package org.example.websocket;

import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.Session;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;
import org.example.model.Device;

@ApplicationScoped
@ServerEndpoint("/actions")
public class DeviceWebSocketServer {
    
    @Inject
    private DeviceSessionHandler sessionHandler;

    @OnOpen
    public void open(Session session) {
        Logger.getLogger(DeviceWebSocketServer.class.getName()).log(Level.INFO,"Creating Session:" + session.getId());
        sessionHandler.addSession(session);
    }

    @OnClose
    public void close(Session session) {
        sessionHandler.removeSession(session);
    }

    @OnError
    public void onError(Throwable error) {
         Logger.getLogger(DeviceWebSocketServer.class.getName()).log(Level.SEVERE, null, error);
    }

    /**
     * Handles messages send from a client
     *
     * @param message - a json message as a string
     * @param session - the sending session?
     */
    @OnMessage
    public void handleMessage(String message, Session session) {
        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();                               // Get the msg in correct json format
            System.out.println("\nSERVER\n"+jsonMessage + "\n");
            if ("add".equals(jsonMessage.getString("action"))) {                        // If we are adding a device
                Device device = new Device();                                           // Get a new device object
                device.setName(jsonMessage.getString("name"));                          // Set its name to the name in the message
                device.setDescription(jsonMessage.getString("description"));            // Set Description to that in the message
                device.setType(jsonMessage.getString("type"));                          // Se the type to that in the message
                device.setStatus("Off");                                                // Initally state is OFF
                sessionHandler.addDevice(device);                                       // Add it to the list of devices in the server
            }

            if ("remove".equals(jsonMessage.getString("action"))) {                     // If we are asked to remove a device
                int id = (int) jsonMessage.getInt("id");                                // Get it's Id
                sessionHandler.removeDevice(id);                                        // Remove it from the session handler
            }

            if ("toggle".equals(jsonMessage.getString("action"))) {                     // If we are toggling
                int id = (int) jsonMessage.getInt("id");                                // Get the Id
                sessionHandler.toggleDevice(id);                                        // Get the session handler to toggle the status
            }
        } catch (Exception e) {                                                         // On any failure
            System.out.println("BNAG");                                                 // Message
            e.printStackTrace();                                                        // Stack
        }
    }

    

}
