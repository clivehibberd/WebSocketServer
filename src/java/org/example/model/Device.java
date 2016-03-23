/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example.model;

/**
 *
 * @author clivehibberd
 */
public class Device {
    private int id;
    private String name;
    public static final String STATUS_ON = "ON";
    public static final String STATUS_OFF = "OFF";
    private String status = STATUS_OFF;
    private String purpose;
    
    private String type;
    private String description;

    public Device() {
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }
    
    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void toggleStatus(){
        if (this.status.equals(STATUS_ON)){
            this.status = STATUS_OFF;
        }else{
            this.status = STATUS_ON;
        }
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
    
    
    
}
