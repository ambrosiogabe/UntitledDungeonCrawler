package com.jade.physics.rigidbody.boundingVolumes;

import com.jade.Component;

public abstract class BoundingVolume extends Component {

    public abstract boolean overlaps(BoundingVolume other);
    public abstract float getSize();
    public abstract float getGrowth(BoundingVolume newVolume);
}
