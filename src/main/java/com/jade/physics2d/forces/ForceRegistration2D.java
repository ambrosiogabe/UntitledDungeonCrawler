package com.jade.physics2d.forces;

import com.jade.physics2d.rigidbody.Rigidbody2D;

public class ForceRegistration2D {
    private Rigidbody2D body;
    private ForceGenerator2D fg;

    public ForceRegistration2D(Rigidbody2D body, ForceGenerator2D fg) {
        this.body = body;
        this.fg = fg;
    }

    public Rigidbody2D body() {
        return this.body;
    }

    public ForceGenerator2D fg() {
        return this.fg;
    }

    @Override
    public boolean equals(Object body) {
        if (body == null) return false;
        if (body.getClass() != ForceRegistration2D.class) return false;

        ForceRegistration2D fr = (ForceRegistration2D) body;
        return fr.body() == this.body && fr.fg() == this.fg;
    }
}
