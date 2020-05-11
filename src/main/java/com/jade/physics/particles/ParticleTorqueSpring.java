package com.jade.physics.particles;

import com.jade.util.JMath;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class ParticleTorqueSpring implements ParticleForceGenerator {

    private Vector2f spring;
    private float stiffness;
    private float angularDamping = -7f;
    private float damping = -1f;

    public ParticleTorqueSpring(Vector2f anchorPos, float stiffness) {
        this.spring = anchorPos;
        this.stiffness = stiffness;
    }

    @Override
    public void updateForce(Particle particle, float duration) {
        Vector2f topLeft = new Vector2f(particle.gameObject.transform.position.x - (particle.gameObject.transform.scale.x / 2.0f),
                            particle.gameObject.transform.position.y + (particle.gameObject.transform.scale.y / 2.0f));
        JMath.rotate(topLeft, particle.gameObject.transform.rotation.z, particle.gameObject.transform.position);

        Vector2f springForce = new Vector2f(topLeft).sub(spring).mul(-1 * stiffness);
        Vector2f r = new Vector2f(particle.gameObject.transform.position.x, particle.gameObject.transform.position.y).sub(topLeft);
        float rxf = r.x * springForce.y - r.y * springForce.x;

        // Spring torque
        particle.addTorque(-1 * rxf);
        particle.addTorque(particle.getAngularVelocity() * angularDamping);
        // Spring force
        particle.addForce(new Vector3f(springForce.x, springForce.y, 0));
        // Damping force
        particle.addForce(new Vector3f(particle.getVelocity()).mul(damping));
    }
}
