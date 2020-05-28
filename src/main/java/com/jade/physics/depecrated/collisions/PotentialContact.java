package com.jade.physics.depecrated.collisions;

import com.jade.physics.depecrated.Rigidbody;

public class PotentialContact {
    public Rigidbody[] body = new Rigidbody[2];

    public PotentialContact(Rigidbody r1, Rigidbody r2) {
        body[0] = r1;
        body[1] = r2;
    }
}
