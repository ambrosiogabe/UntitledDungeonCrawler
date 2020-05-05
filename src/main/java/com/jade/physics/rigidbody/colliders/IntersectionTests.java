package com.jade.physics.rigidbody.colliders;

import com.jade.util.JMath;
import org.joml.Vector3f;

public class IntersectionTests {

    public static boolean boxAndHalfSpace(BoxCollider box, Plane plane) {
        float projectedRadius = box.getHalfSize().x * Math.abs(plane.getNormal().dot(new Vector3f(box.gameObject.transform.position.x, 0, 0))) +
                            box.getHalfSize().y * Math.abs(plane.getNormal().dot(new Vector3f(0, box.gameObject.transform.position.y, 0))) +
                            box.getHalfSize().z * Math.abs(plane.getNormal().dot(new Vector3f(0, 0, box.gameObject.transform.position.z)));

        float boxDistance = plane.getNormal().dot(new Vector3f(box.gameObject.transform.position).sub(plane.gameObject.transform.position)) - projectedRadius;

        return boxDistance <= plane.getOffset();
    }

    public static boolean boxAndBox(BoxCollider boxOne, BoxCollider boxTwo) {
        // Find the vector between the two centers
        Vector3f toCenter = new Vector3f(boxTwo.gameObject.transform.position).sub(boxOne.gameObject.transform.position);

        return (
                // Check on box one's axes first
                overlapOnAxis(boxOne, boxTwo, boxOne.getAxis(0), toCenter) &&
                overlapOnAxis(boxOne, boxTwo, boxOne.getAxis(1), toCenter) &&
                overlapOnAxis(boxOne, boxTwo, boxOne.getAxis(2), toCenter) &&

                // And on two's
                overlapOnAxis(boxOne, boxTwo, boxTwo.getAxis(0), toCenter) &&
                overlapOnAxis(boxOne, boxTwo, boxTwo.getAxis(1), toCenter) &&
                overlapOnAxis(boxOne, boxTwo, boxTwo.getAxis(2), toCenter) &&

                // Now on the cross products
                overlapOnAxis(boxOne, boxTwo, boxOne.getAxis(0).cross(boxTwo.getAxis(0)), toCenter) &&
                overlapOnAxis(boxOne, boxTwo, boxOne.getAxis(0).cross(boxTwo.getAxis(1)), toCenter) &&
                overlapOnAxis(boxOne, boxTwo, boxOne.getAxis(0).cross(boxTwo.getAxis(2)), toCenter) &&
                overlapOnAxis(boxOne, boxTwo, boxOne.getAxis(1).cross(boxTwo.getAxis(0)), toCenter) &&
                overlapOnAxis(boxOne, boxTwo, boxOne.getAxis(1).cross(boxTwo.getAxis(1)), toCenter) &&
                overlapOnAxis(boxOne, boxTwo, boxOne.getAxis(1).cross(boxTwo.getAxis(2)), toCenter) &&
                overlapOnAxis(boxOne, boxTwo, boxOne.getAxis(2).cross(boxTwo.getAxis(0)), toCenter) &&
                overlapOnAxis(boxOne, boxTwo, boxOne.getAxis(2).cross(boxTwo.getAxis(1)), toCenter) &&
                overlapOnAxis(boxOne, boxTwo, boxOne.getAxis(2).cross(boxTwo.getAxis(2)), toCenter)
            );
    }

    private static boolean overlapOnAxis(BoxCollider one, BoxCollider two, Vector3f axis, Vector3f toCenter) {
        // Project the half-size of one onto axis
        float oneProject = transformToAxis(one, axis);
        float twoProject = transformToAxis(two, axis);

        // Project this onto the axis
        float distance = Math.abs(toCenter.dot(axis));

        // Check for overlap
        return (distance < oneProject + twoProject);
    }

    private static float transformToAxis(BoxCollider box, Vector3f axis) {
        return  box.getHalfSize().x * Math.abs(axis.dot(box.getAxis(0))) +
                box.getHalfSize().y * Math.abs(axis.dot(box.getAxis(1))) +
                box.getHalfSize().z * Math.abs(axis.dot(box.getAxis(2)));
    }
}
