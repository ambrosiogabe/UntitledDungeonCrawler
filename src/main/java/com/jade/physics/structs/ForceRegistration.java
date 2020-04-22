package com.jade.physics.structs;


import com.jade.physics.rigidbody.ForceGenerator;
import com.jade.physics.rigidbody.Rigidbody;

public class ForceRegistration {
    public Rigidbody body;
    public ForceGenerator fg;

    public ForceRegistration(Rigidbody body, ForceGenerator fg) {
        this.body = body;
        this.fg = fg;
    }

    @Override
    public boolean equals(Object body) {
        if (body == null) return false;
        if (body.getClass() != ForceRegistration.class) return false;

        ForceRegistration fr = (ForceRegistration) body;
        return fr.body == this.body && fr.fg == this.fg;
    }
}
