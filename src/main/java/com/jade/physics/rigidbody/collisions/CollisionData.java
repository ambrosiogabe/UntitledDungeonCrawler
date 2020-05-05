package com.jade.physics.rigidbody.collisions;

public class CollisionData {
    private Contact[] contacts;

    // The number of contacts that this collision data can add
    private int contactsLeft;

    private float friction;
    private float restitution;
    private int numContacts;

    public CollisionData() {
        this.contacts = new Contact[100];
        for (int i=0; i < contacts.length; i++) {
            this.contacts[i] = new Contact();
        }
        this.contactsLeft = contacts.length;
        numContacts = 0;
    }

    public int contactsLeft() {
        return this.contactsLeft;
    }

    public Contact[] getContacts() {
        return this.contacts;
    }

    public void setContactsLeft(int val) {
        this.contactsLeft = val;
    }

    public Contact getCurrentContact() {
        return this.contacts[this.contacts.length - this.contactsLeft];
    }

    public void addContacts(int num) {
        this.contactsLeft -= num;
        this.numContacts += num;
    }

    public int getNumContacts() {
        return this.numContacts;
    }

    public float getFriction() {
        return this.friction;
    }

    public float getRestitution() {
        return this.restitution;
    }

    public void reset() {
        this.contactsLeft = this.contacts.length;
    }
}
