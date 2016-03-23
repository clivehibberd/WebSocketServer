package org.example.websocket;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import javax.enterprise.context.ApplicationScoped;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.spi.JsonProvider;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;
import org.example.model.Device;

@ApplicationScoped
public class DeviceSessionHandler {

    private final Set sessions = new HashSet<>();
    private final Set devices = new HashSet<>();

    private int deviceId = 0;

    /**
     * Client asks us to add a session
     *
     * @param session
     */
    public void addSession(Session session) {
        System.out.println("Adding session "+ session.getId());
        sessions.add(session);                                                      // Add session to our list
        for (Object device : devices) {                                             // For each of our existing devices
            JsonObject addMessage = createAddMessage((Device) device);              // Get its Json object
            sendToSession(session, addMessage);                                     // Send the device to the new session
        }
    }

    public void removeSession(Session session) {
        sessions.remove(session);
    }

    public List getDevices() {
        return new ArrayList<>(devices);
    }

    /**
     * We add a new Device to the collection of devices
     *
     * @param device
     */
    public void addDevice(Device device) {
        device.setId(deviceId);                                     // Set the Id of the nex device to the next value 
        devices.add(device);                                        // Add the device to our list of devices
        deviceId++;                                                 // Increment the device Id key
        JsonObject addMessage = createAddMessage(device);           // Create the Json message for the device
        sendToAllConnectedSessions(addMessage);                     // Send the json message to all connected clients
    }

    /**
     * We've been asked to remove a device from the list so we must remove it from the list of
     * devices and also let all the other clients know that the device is removed.
     *
     * @param id
     */
    public void removeDevice(int id) {
        Device device = getDeviceById(id);                                  // Get the device object
        if (device != null) {                                               // If we have one by that Id
            devices.remove(device);                                         // Zap it from the list
            JsonProvider provider = JsonProvider.provider();                // Prep for json messag
            JsonObjectBuilder builder = provider.createObjectBuilder();     // Get a json builder object
            builder.add("action", "remove");                                // Add data
            builder.add("id", id);                                          // add data
            JsonObject removeMessage = builder.build();                     // Build and get the resulting json message
            sendToAllConnectedSessions(removeMessage);                      // Send the json to all clients
        }
    }

    public void toggleDevice(int id) {
        Device device = getDeviceById(id);                                  // Get the device object
        if (device != null) {                                               // If we have one by that Id
            device.toggleStatus();
        }
        JsonProvider provider = JsonProvider.provider();                    // Prep for json messag
        JsonObjectBuilder builder = provider.createObjectBuilder();         // Get a json builder object
        builder.add("action", "toggle");                                    // Actio is toggle
        builder.add("id", device.getId());                                  // For this device
        builder.add("status", device.getStatus());                          // Status is now the toggled status
        builder.add("ts", new Date().toString());
        JsonObject toggleMessage = builder.build();                         // Build the message
        sendToAllConnectedSessions(toggleMessage);                          // Send it to all clients
    }

    private Device getDeviceById(int id) {
        for (Object obj : devices) {
            Device device = (Device) obj;
            if (device.getId() == id) {
                return device;
            }
        }
        return null;
    }

    
    /**
     * Create a Json message that adds a device
     * @param device
     * @return the Json message
     */
    private JsonObject createAddMessage(Device device) {
        JsonProvider provider = JsonProvider.provider();                    // Prep for json messag
        JsonObjectBuilder builder = provider.createObjectBuilder();         // Get a json builder object
        builder.add("action", "add");
        builder.add("id", device.getId());
        builder.add("name", device.getName());
        builder.add("type", device.getType());
        builder.add("status", device.getStatus());
        builder.add("description", device.getDescription());
        JsonObject message = builder.build();
        return message;
    }

    private void sendToAllConnectedSessions(JsonObject message) {
        for (Object obj : sessions) {
            Session session = (Session) obj;
            sendToSession(session, message);
        }
    }

    private void sendToSession(Session session, JsonObject message) {
        try {
            Basic basic = session.getBasicRemote();
            basic.sendText(message.toString());
            Logger.getLogger(DeviceSessionHandler.class.getName()).log(Level.INFO, message.toString());
        } catch (IOException ex) {
            sessions.remove(session);
            Logger.getLogger(DeviceSessionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
