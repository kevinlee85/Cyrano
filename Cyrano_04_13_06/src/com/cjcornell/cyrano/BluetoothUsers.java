package com.cjcornell.cyrano;

/**
 * CLASS: Friend
 *   This class represents a friend of a user of Cyrano
 */

import java.io.Serializable;

public class BluetoothUsers implements Serializable {
    
    private static final long serialVersionUID = 1L;
    /** Attributes */
    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private String address;

    /** Constructors */
    public BluetoothUsers(String id, String firstname, String lastname, String email, String address) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * toString The toString method will return the friend's name and the
     * distance from the user in parentheses
     */
    @Override
    public String toString() {
        // return getName() + " (" + getDistanceString() + ")";
        return getName();
    }

    public String getName() {
        
        return firstname + " " + lastname;
    }
    
}