package com.jade.physics.depecrated.colliders;

import com.jade.physics.depecrated.collisions.CollisionData;
import com.jade.physics.depecrated.collisions.Contact;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class CollisionDetector {
    public static int sphereAndSphere(SphereCollider one, SphereCollider two, CollisionData data) {
        // Make sure we have contacts
        if (data.contactsLeft() <= 0) return 0;

        // Cache the sphere positions
        Vector3f positionOne = one.getPosition();
        Vector3f positionTwo = two.getPosition();

        // Find the vector between the objects
        Vector3f midline = new Vector3f(positionOne).sub(positionTwo);
        float size = midline.length();

        // See if it is large enough
        if (size <= 0.0f || size >= one.getRadius() + two.getRadius()) {
            return 0;
        }

        // We manually create the normal, because we have the size to hand.
        Vector3f normal = new Vector3f(midline).mul(1.0f / size);

        Vector3f contactPoint = new Vector3f(positionOne).add(midline).mul(0.5f);
        float penetration = one.getRadius() + two.getRadius() - size;

        Contact contact = data.getCurrentContact();
        contact.setContactNormal(normal);
        contact.setContactPoint(contactPoint);
        contact.setPenetrationDepth(penetration);
        contact.setBodyData(one.getRigidbody(), two.getRigidbody(), data.getFriction(), data.getRestitution());
        data.addContacts(1);

        return 1;
    }

    /* **********************************************************************************************
     * The plane is treated as if it was infinitely long in the direction opposite to its normal.
     * So, if an object collides with a plane, collisions will always resolve in the direction
     * of the plane's normal.
     * ********************************************************************************************** */
    public static int sphereAndHalfSpace(SphereCollider sphere, Plane plane, CollisionData data) {
        // Make sure we have contacts
        if (data.contactsLeft() <= 0) return 0;

        // Cache the sphere position
        Vector3f spherePosition = new Vector3f(sphere.gameObject.transform.position);
        Vector3f planePosition = plane.gameObject.transform.position;

        // Find the distance from the plane
        float ballDistance = plane.getNormal().dot(new Vector3f(spherePosition).sub(planePosition)) - sphere.getRadius() - plane.getOffset();

        if (ballDistance > plane.getOffset()) return 0;

        // Create the contact. It has a normal in the plane's normal direction
        Contact contact = data.getCurrentContact();
        contact.setContactNormal(new Vector3f(plane.getNormal()));
        contact.setPenetrationDepth(-ballDistance);
        contact.setContactPoint(spherePosition.mul(ballDistance + sphere.getRadius()));
        contact.setBodyData(sphere.getRigidbody(), plane.getRigidbody(), data.getFriction(), data.getRestitution());

        data.addContacts(1);
        return 1;
    }

    public static int boxAndHalfSpace(BoxCollider box, Plane plane, CollisionData data) {
        // Make sure we have contacts
        if (data.contactsLeft() <= 0) return 0;

        // Check for intersection
        if (!IntersectionTests.boxAndHalfSpace(box, plane)) {
            return 0;
        }

        // We have an intersection, so find the intersection points. We can make
        // do with only checking vertices. If the box is resting on a plane or on an
        // edge, it will be reported as four or two contact points.

        Contact contact = data.getCurrentContact();
        int contactsUsed = 0;
        Vector3f[] boxVertices = box.getVertices();
        for (int i=0; i < boxVertices.length; i++) {
            Vector3f vertexPos = boxVertices[i];

            // Calculate the distance from the plane
            Vector3f planePosition = plane.gameObject.transform.position;
            float vertexDistance = plane.getNormal().dot(new Vector3f(vertexPos).sub(planePosition));

            // Compare this to the plane's offset
            if (vertexDistance <= plane.getOffset()) {
                //DebugDraw.addLine(vertexPos, new Vector3f(vertexPos).add(new Vector3f(plane.getNormal()).mul(plane.getOffset() - vertexDistance)), 0.05f, Constants.COLOR3_CYAN);
                // Create the contact data

                // The contact point is halfway between the vertex and the plane. We
                // multiply the direction by half the separation distance and add the vertex location.
                Vector3f contactPoint = new Vector3f(plane.getNormal()).mul(vertexDistance - plane.getOffset()).add(vertexPos);
                contact.setContactPoint(contactPoint);
                contact.setContactNormal(new Vector3f(plane.getNormal()));
                contact.setPenetrationDepth(plane.getOffset() - vertexDistance);

                // Write the appropriate data
                contact.setBodyData(box.getRigidbody(), plane.getRigidbody(), data.getFriction(), data.getRestitution());

                // Move on to the next contact
                contactsUsed++;
                data.addContacts(1);
                contact = data.getCurrentContact();
                if (true) return 1;
                if (data.contactsLeft() <= 0) return contactsUsed;
            }
        }

        return contactsUsed;
    }

    public static int boxAndSphere(BoxCollider box, SphereCollider sphere, CollisionData data) {
        // Transform the center of the sphere into the box's coordinates
        Vector3f center = new Vector3f(sphere.gameObject.transform.position);
        Vector4f tmp = new Vector4f(center, 1);
        box.gameObject.transform.getInverseModelMatrix().transform(tmp);
        Vector3f relCenter = new Vector3f(tmp.x, tmp.y, tmp.z);

        // Early out check to see if we can exclude the contact
        if (Math.abs(relCenter.x) - sphere.getRadius() > box.getHalfSize().x ||
                Math.abs(relCenter.y) - sphere.getRadius() > box.getHalfSize().y ||
                Math.abs(relCenter.z) - sphere.getRadius() > box.getHalfSize().z) {
            return 0;
        }

        Vector3f closestPt = new Vector3f();
        float distance;

        // Clamp each coordinate to the box
        distance = relCenter.x;
        if (distance > box.getHalfSize().x) distance = box.getHalfSize().x;
        if (distance < -box.getHalfSize().x) distance = -box.getHalfSize().x;
        closestPt.x = distance;

        distance = relCenter.y;
        if (distance > box.getHalfSize().y) distance = box.getHalfSize().y;
        if (distance < -box.getHalfSize().y) distance = -box.getHalfSize().y;
        closestPt.y = distance;

        distance = relCenter.z;
        if (distance > box.getHalfSize().z) distance = box.getHalfSize().z;
        if (distance < -box.getHalfSize().z) distance = -box.getHalfSize().z;
        closestPt.z = distance;

        // Check to see if we're in contact
        distance = new Vector3f(closestPt).sub(relCenter).lengthSquared();
        if (distance > sphere.getRadius() * sphere.getRadius()) return 0;

        // Create the contact data
        tmp.set(closestPt, 1);
        box.gameObject.transform.modelMatrix.transform(tmp);
        Vector3f closestPtWorld = new Vector3f(tmp.x, tmp.y, tmp.z);

        Contact contact = data.getCurrentContact();
        contact.setContactNormal(new Vector3f(closestPtWorld).sub(center).normalize());
        contact.setContactPoint(closestPtWorld);
        contact.setPenetrationDepth(sphere.getRadius() - (float)Math.sqrt(distance));
        contact.setBodyData(box.getRigidbody(), sphere.getRigidbody(), data.getFriction(), data.getRestitution());

        data.addContacts(1);
        return 1;
    }

    public static int boxAndBox(BoxCollider boxOne, BoxCollider boxTwo, CollisionData data) {
        // Find the vector between the two centers
        Vector3f toCenter = new Vector3f(boxTwo.gameObject.transform.position).sub(boxOne.gameObject.transform.position);

        // We start assuming there is no contact
        float penetration = Float.MAX_VALUE;
        int best = 0xffffff;

        Vector2f values = new Vector2f(penetration, best);
        if (!tryAxis(boxOne, boxTwo, boxOne.getAxis(0), toCenter, 0, values)) return 0;
        if (!tryAxis(boxOne, boxTwo, boxOne.getAxis(1), toCenter, 1, values)) return 0;
        if (!tryAxis(boxOne, boxTwo, boxOne.getAxis(2), toCenter, 2, values)) return 0;

        if (!tryAxis(boxOne, boxTwo, boxTwo.getAxis(0), toCenter, 3, values)) return 0;
        if (!tryAxis(boxOne, boxTwo, boxTwo.getAxis(1), toCenter, 4, values)) return 0;
        if (!tryAxis(boxOne, boxTwo, boxTwo.getAxis(2), toCenter, 5, values)) return 0;

        best = (int)values.y;

        // Store the best axis-major, in case we run into almost parallel edge collisions later
        int bestSingleAxis = best;

        if (!tryAxis(boxOne, boxTwo, boxOne.getAxis(0).cross(boxTwo.getAxis(0)), toCenter, 6, values)) return 0;
        if (!tryAxis(boxOne, boxTwo, boxOne.getAxis(0).cross(boxTwo.getAxis(1)), toCenter, 7, values)) return 0;
        if (!tryAxis(boxOne, boxTwo, boxOne.getAxis(0).cross(boxTwo.getAxis(2)), toCenter, 8, values)) return 0;
        if (!tryAxis(boxOne, boxTwo, boxOne.getAxis(1).cross(boxTwo.getAxis(0)), toCenter, 9, values)) return 0;
        if (!tryAxis(boxOne, boxTwo, boxOne.getAxis(1).cross(boxTwo.getAxis(1)), toCenter, 10, values)) return 0;
        if (!tryAxis(boxOne, boxTwo, boxOne.getAxis(1).cross(boxTwo.getAxis(2)), toCenter, 11, values)) return 0;
        if (!tryAxis(boxOne, boxTwo, boxOne.getAxis(2).cross(boxTwo.getAxis(0)), toCenter, 12, values)) return 0;
        if (!tryAxis(boxOne, boxTwo, boxOne.getAxis(2).cross(boxTwo.getAxis(1)), toCenter, 13, values)) return 0;
        if (!tryAxis(boxOne, boxTwo, boxOne.getAxis(2).cross(boxTwo.getAxis(2)), toCenter, 14, values)) return 0;

        penetration = values.x;
        best = (int)values.y;

        // Make sure we've got a result
        assert best != 0xffffff : "Assertion: We did not find a result in box vs box collision detection";

        // We know there's a collision, and we know which of the axes
        // gave the smallest penetration. We now can deal with it in
        // different ways depending on the best case.
        if (best < 3) {
            // We've got a vertex of box two on a face of box one.
            fillPointFaceBoxBox(boxOne, boxTwo, toCenter, data, best, penetration);
            data.addContacts(1);
            return 1;
        } else if (best < 6) {
            // We've got a vertex of box one on a face of box two. We use the
            // same algorithm as above, but swap around one and two (and therefore
            // also the vector between their centers.
            fillPointFaceBoxBox(boxTwo, boxOne, toCenter.mul(-1), data, best - 3, penetration);
            data.addContacts(1);
            return 1;
        } else {
            // We've got an edge-edge contact. Find out which axes
            best -= 6;
            int oneAxisIndex = best / 3;
            int twoAxisIndex = best % 3;
            Vector3f oneAxis = boxOne.getAxis(oneAxisIndex);
            Vector3f twoAxis = boxTwo.getAxis(twoAxisIndex);
            Vector3f axis = oneAxis.cross(twoAxis);
            axis.normalize();

            // The axis should point from box one to bxo two.
            if (axis.dot(toCenter) > 0) axis.mul(-1.0f);

            // We have the axes, but not the edges: each axis has 4 edges parallel to it,
            // we need to find which of the 4 for each object. We do that by finding the
            // point in the center of the edge. We know its component in the direction of
            // the box's collision axis is zero (its a mid-point) and we determine which of
            // the extremes in each of the other axes is closest.
            float[] ptOnOneEdgeArr = {boxOne.getHalfSize().x, boxOne.getHalfSize().y, boxOne.getHalfSize().z};
            float[] ptOnTwoEdgeArr = {boxTwo.getHalfSize().x, boxTwo.getHalfSize().y, boxTwo.getHalfSize().z};
            for (int i=0; i < 3; i++) {
                if (i == oneAxisIndex) ptOnOneEdgeArr[i] = 0;
                else if (boxOne.getAxis(i).dot(axis) > 0) ptOnOneEdgeArr[i] = -ptOnOneEdgeArr[i];

                if (i == twoAxisIndex) ptOnTwoEdgeArr[i] = 0;
                else if (boxTwo.getAxis(i).dot(axis) < 0) ptOnTwoEdgeArr[i] = -ptOnTwoEdgeArr[i];
            }

            // Move them into world coordinates (they are already oriented correctly
            // since they have been derived from the axes.
            Vector4f tmp = new Vector4f(ptOnOneEdgeArr[0], ptOnOneEdgeArr[1], ptOnOneEdgeArr[2], 1).mul(boxOne.gameObject.transform.modelMatrix);
            Vector3f ptOnOneEdge = new Vector3f(tmp.x, tmp.y, tmp.z);
            tmp = new Vector4f(ptOnTwoEdgeArr[0], ptOnTwoEdgeArr[1], ptOnTwoEdgeArr[2], 1).mul(boxTwo.gameObject.transform.modelMatrix);
            Vector3f ptOnTwoEdge = new Vector3f(tmp.x, tmp.y, tmp.z);

            // So we have a point and a direction for the colliding edges. We need to find
            // out point of closest approach of the two line-segments.
            Vector3f vertex = contactPoint(
                    ptOnOneEdge, oneAxis, boxOne.getHalfSize().get(oneAxisIndex),
                    ptOnTwoEdge, twoAxis, boxTwo.getHalfSize().get(twoAxisIndex),
                    bestSingleAxis > 2
            );

            // We can fill the contact.
            Contact contact = data.getCurrentContact();

            contact.setPenetrationDepth(penetration);
            contact.setContactNormal(axis);
            contact.setContactPoint(vertex);
            contact.setBodyData(boxOne.getRigidbody(), boxTwo.getRigidbody(), data.getFriction(), data.getRestitution());
            data.addContacts(1);
            return 1;
        }
    }

    private static void fillPointFaceBoxBox(BoxCollider one, BoxCollider two, Vector3f toCenter, CollisionData data, int best, float penetration) {
        // This method is called when we know that a vertex from box two
        // is in contact with box one.
        Contact contact = data.getCurrentContact();

        // We know which axis the collision is on (i.e best), but we need
        // to work out which of the two faces on the axis.
        Vector3f normal = one.getAxis(best);
        if (one.getAxis(best).dot(toCenter) > 0) {
            normal.mul(-1.0f);
        }

        // Work out which vertex of box two we're colliding with.
        // Using toCenter doesn't work!
        Vector3f vertex = new Vector3f(two.getHalfSize());
        if (two.getAxis(0).dot(toCenter) > 0) vertex.x = -vertex.x;
        if (two.getAxis(1).dot(toCenter) > 0) vertex.y = -vertex.y;
        if (two.getAxis(2).dot(toCenter) > 0) vertex.z = -vertex.z;

        // Create contact point
        Vector4f tmp = two.gameObject.transform.modelMatrix.transform(new Vector4f(vertex, 1));
        Vector3f contactPoint = new Vector3f(tmp.x, tmp.y, tmp.z);
        /// TODO: REMOVE THIS AFTER FINISH TESTING
        //DebugDraw.addLine(contactPoint, new Vector3f(contactPoint).add(Constants.UP), 0.05f, Constants.COLOR3_YELLOW, 60 * 5);

        // Create the contact data
        contact.setContactNormal(normal);
        contact.setPenetrationDepth(penetration);
        contact.setContactPoint(contactPoint);
        contact.setBodyData(one.getRigidbody(), two.getRigidbody(), data.getFriction(), data.getRestitution());
    }

    private static Vector3f contactPoint(Vector3f pOne, Vector3f dOne, float oneSize, Vector3f pTwo, Vector3f dTwo, float twoSize, boolean useOne) {
        Vector3f toSt, cOne, cTwo;
        float dpStaOne, dpStaTwo, dpOneTwo, smOne, smTwo;
        float denom, mua, mub;

        // Square magnitude
        smOne = dOne.lengthSquared();
        smTwo = dTwo.lengthSquared();
        dpOneTwo = dTwo.dot(dOne);

        toSt = new Vector3f(pOne).sub(pTwo);
        dpStaOne = dOne.dot(toSt);
        dpStaTwo = dTwo.dot(toSt);

        denom = smOne * smTwo - dpOneTwo * dpOneTwo;

        // Zero denominator indicates parallel lines
        if (Math.abs(denom) < 0.0001f) {
            return useOne ? pOne : pTwo;
        }

        mua = (dpOneTwo * dpStaTwo - smTwo * dpStaOne) / denom;
        mub = (smOne * dpStaTwo - dpOneTwo * dpStaOne) / denom;

        // If either of the edges has the nearest point out of bounds,
        // then the edges aren't crossed, we have an edge-face contact. Our
        // point is on the edge, which we know from the useOne parameter.
        if (mua > oneSize || mua < -oneSize || mub > twoSize || mub < -twoSize) {
            return useOne ? pOne : pTwo;
        } else {
            cOne = new Vector3f(pOne).add(new Vector3f(dOne).mul(mua));
            cTwo = new Vector3f(pTwo).add(new Vector3f(dTwo).mul(mub));

            return new Vector3f(cOne.mul(0.5f)).add(new Vector3f(cTwo).mul(0.5f));
        }
    }

    private static boolean tryAxis(BoxCollider one, BoxCollider two, Vector3f axis, Vector3f toCenter, int index, Vector2f modifiableValues) {
        // Make sure we have normalized axis, and don't check almost parallel axes
        if (axis.lengthSquared() < 0.0001f) {
            return true;
        }
        axis.normalize();

        float penetration = penetrationOnAxis(one, two, axis, toCenter);

        // ModifiableValues:
        //      x: smallestPenetration
        //      y: smallestCase
        if (penetration < 0) return false;
        if (penetration < modifiableValues.x) {
            modifiableValues.x = penetration;
            modifiableValues.y = index;
        }

        return true;
    }

    private static float penetrationOnAxis(BoxCollider one, BoxCollider two, Vector3f axis, Vector3f toCenter) {
        // Project the half-size of one onto axis
        float oneProject = transformToAxis(one, axis);
        float twoProject = transformToAxis(two, axis);

        // Project this onto the axis
        float distance = Math.abs(toCenter.dot(axis));

        // Return the overlap (i.e positive indicates overlap, negative indicates separation)
        return oneProject + twoProject - distance;
    }

    private static float transformToAxis(BoxCollider box, Vector3f axis) {
        return  box.getHalfSize().x * Math.abs(axis.dot(box.getAxis(0))) +
                box.getHalfSize().y * Math.abs(axis.dot(box.getAxis(1))) +
                box.getHalfSize().z * Math.abs(axis.dot(box.getAxis(2)));
    }
}
