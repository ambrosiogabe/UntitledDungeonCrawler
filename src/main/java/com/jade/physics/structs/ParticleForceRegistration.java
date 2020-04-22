package com.jade.physics.structs;


import com.jade.physics.particles.Particle;
import com.jade.physics.particles.ParticleForceGenerator;

public class ParticleForceRegistration {
    public Particle particle;
    public ParticleForceGenerator fg;

    public ParticleForceRegistration(Particle particle, ParticleForceGenerator fg) {
        this.particle = particle;
        this.fg = fg;
    }

    @Override
    public boolean equals(Object particle) {
        if (particle == null) return false;
        if (particle.getClass() != ParticleForceRegistration.class) return false;

        ParticleForceRegistration fr = (ParticleForceRegistration)particle;
        return fr.particle == this.particle && fr.fg == this.fg;
    }

    public Particle particle() {
        return this.particle;
    }
}
