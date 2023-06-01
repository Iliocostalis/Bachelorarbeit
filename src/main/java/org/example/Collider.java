package org.example;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Collider implements Listener {

    Vector3f intersectingPosition;

    Objekt objekt;

    Vector3f boundingBoxMin;
    Vector3f boundingBoxMax;

    boolean isBoundingSphereOk = false;

    public Collider(Objekt objekt)
    {
        this.objekt = objekt;
        intersectingPosition = new Vector3f();
        boundingBoxMin = new Vector3f();
        boundingBoxMax = new Vector3f();

        objekt.transformation.addModifiedListener(this);
    }


    public boolean isRayIntersecting(Vector3f rayOrigin, Vector3f rayDirection)
    {
        if(!isBoundingSphereOk)
            calculateBoundingSphere();

        Vector3f rayDirectionInverse = VectorMatrixPool.getVector3f();

        rayDirectionInverse.set(1,1,1);
        rayDirectionInverse.div(rayDirection);

        //box b, ray r
        float tx1 = (boundingBoxMin.x - rayOrigin.x)*rayDirectionInverse.x;
        float tx2 = (boundingBoxMax.x - rayOrigin.x)*rayDirectionInverse.x;

        float tmin = Math.min(tx1, tx2);
        float tmax = Math.max(tx1, tx2);

        float ty1 = (boundingBoxMin.y - rayOrigin.y)*rayDirectionInverse.y;
        float ty2 = (boundingBoxMax.y - rayOrigin.y)*rayDirectionInverse.y;

        tmin = Math.max(tmin, Math.min(ty1, ty2));
        tmax = Math.min(tmax, Math.max(ty1, ty2));

        float tz1 = (boundingBoxMin.z - rayOrigin.z)*rayDirectionInverse.z;
        float tz2 = (boundingBoxMax.z - rayOrigin.z)*rayDirectionInverse.z;

        tmin = Math.max(tmin, Math.min(tz1, tz2));
        tmax = Math.min(tmax, Math.max(tz1, tz2));

        boolean hit = tmax >= Math.max(0.0, tmin) && tmin >= 0;
        if(hit)
        {
            return exactCollision(rayOrigin, rayDirection);
        }

        return false;
    }

    private boolean exactCollision(Vector3f rayOrigin, Vector3f rayDirection)
    {
        Mesh mesh = UmgebungsLader.getMesh(objekt.meshHash);

        Matrix4f matrixInverted = VectorMatrixPool.getMatrix4f();
        objekt.transformation.getMatrix().invert(matrixInverted);

        Vector3f rayOriginTransformed = VectorMatrixPool.getVector3f();
        Vector3f rayDirectionTransformed = VectorMatrixPool.getVector3f();

        rayOriginTransformed.set(rayOrigin);
        rayOriginTransformed.mulPosition(matrixInverted);
        rayDirectionTransformed.set(rayDirection);
        rayDirectionTransformed.mulDirection(matrixInverted);

        float t;
        Vector3f v0 = VectorMatrixPool.getVector3f();
        Vector3f v1 = VectorMatrixPool.getVector3f();
        Vector3f v2 = VectorMatrixPool.getVector3f();
        Vector3f edgeV0V1 = VectorMatrixPool.getVector3f();
        Vector3f edgeV0V2 = VectorMatrixPool.getVector3f();
        Vector3f edgeV1V2 = VectorMatrixPool.getVector3f();
        Vector3f edgeV2V1 = VectorMatrixPool.getVector3f();
        Vector3f normal = VectorMatrixPool.getVector3f();
        Vector3f v0ToP = VectorMatrixPool.getVector3f();
        Vector3f v1ToP = VectorMatrixPool.getVector3f();
        Vector3f v2ToP = VectorMatrixPool.getVector3f();
        Vector3f c = VectorMatrixPool.getVector3f();
        for(int i = 0; i < mesh.vertices.length; i+=9)
        {
            v0.x = mesh.vertices[i];
            v0.y = mesh.vertices[i+1];
            v0.z = mesh.vertices[i+2];

            v1.x = mesh.vertices[i+3];
            v1.y = mesh.vertices[i+4];
            v1.z = mesh.vertices[i+5];

            v2.x = mesh.vertices[i+6];
            v2.y = mesh.vertices[i+7];
            v2.z = mesh.vertices[i+8];

            // compute the plane's normal
            ///Vec3f v0v1 = v1 - v0;
            ///Vec3f v0v2 = v2 - v0;
            v1.sub(v0, edgeV0V1);
            v2.sub(v0, edgeV0V2);

            // no need to normalize
            ///Vec3f N = v0v1.crossProduct(v0v2); // N
            edgeV0V1.cross(edgeV0V2, normal);
            ///float area2 = N.length();
            //float area2 = normal.length();
            normal.normalize();

            if (rayDirectionTransformed.dot(normal) > 0)
                continue; // back-facing surface

            // Step 1: finding P

            // check if the ray and plane are parallel.
            ///float NdotRayDirection = N.dotProduct(dir);
            ///if (fabs(NdotRayDirection) < kEpsilon) // almost 0
            ///    return false; // they are parallel, so they don't intersect!
            float NdotRayDirection = normal.dot(rayDirectionTransformed);
            if(Math.abs(NdotRayDirection) < 0.000001f)
                continue;

            // compute d parameter using equation 2
            ///float d = -N.dotProduct(v0);
            float d = -normal.dot(v0);

            // compute t (equation 3)
            //t = -(N.dotProduct(orig) + d) / NdotRayDirection;
            t = -(normal.dot(rayOriginTransformed) + d) / NdotRayDirection;

            // check if the triangle is behind the ray
            if (t < 0) continue; // the triangle is behind

            // compute the intersection point using equation 1
            ///Vec3f P = orig + t * dir;
            rayDirectionTransformed.mulAdd(t, rayOriginTransformed, intersectingPosition);

            // Step 2: inside-outside test
            ///Vec3f C; // vector perpendicular to triangle's plane

            // edge 0
            ///Vec3f edge0 = v1 - v0;
            ///Vec3f vp0 = P - v0;
            ///C = edge0.crossProduct(vp0);
            ///if (N.dotProduct(C) < 0) return false; // P is on the right side
            intersectingPosition.sub(v0, v0ToP);
            edgeV0V1.cross(v0ToP, c);
            if(normal.dot(c) < 0)
                continue;


            // edge 1
            ///Vec3f edge1 = v2 - v1;
            ///Vec3f vp1 = P - v1;
            ///C = edge1.crossProduct(vp1);
            ///if (N.dotProduct(C) < 0)  return false; // P is on the right side
            v2.sub(v1, edgeV1V2);
            intersectingPosition.sub(v1, v1ToP);
            edgeV1V2.cross(v1ToP, c);
            if(normal.dot(c) < 0)
                continue;

            // edge 2
            ///Vec3f edge2 = v0 - v2;
            ///Vec3f vp2 = P - v2;
            ///C = edge2.crossProduct(vp2);
            ///if (N.dotProduct(C) < 0) return false; // P is on the right side;
            v0.sub(v2, edgeV2V1);
            intersectingPosition.sub(v2, v2ToP);
            edgeV2V1.cross(v2ToP, c);
            if(normal.dot(c) < 0)
                continue;


            // convert position back
            intersectingPosition.mulPosition(objekt.transformation.getMatrix());
            return true; // this ray hits the triangle
        }

        return false;
    }

    public Vector3fc getIntersectionPosition()
    {
        return intersectingPosition;
    }

    private void calculateBoundingSphere()
    {
        isBoundingSphereOk = true;

        Mesh mesh = UmgebungsLader.getMesh(objekt.meshHash);

        boundingBoxMin.set(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
        boundingBoxMax.set(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);

        Vector3f vector3f = VectorMatrixPool.getVector3f();

        for(int i = 0; i < mesh.vertices.length; i+=3)
        {
            vector3f.x = mesh.vertices[i];
            vector3f.y = mesh.vertices[i+1];
            vector3f.z = mesh.vertices[i+2];

            vector3f.mulPosition(objekt.transformation.getMatrix());

            boundingBoxMin.x = Math.min(boundingBoxMin.x, vector3f.x);
            boundingBoxMin.y = Math.min(boundingBoxMin.y, vector3f.y);
            boundingBoxMin.z = Math.min(boundingBoxMin.z, vector3f.z);

            boundingBoxMax.x = Math.max(boundingBoxMax.x, vector3f.x);
            boundingBoxMax.y = Math.max(boundingBoxMax.y, vector3f.y);
            boundingBoxMax.z = Math.max(boundingBoxMax.z, vector3f.z);
        }

        float border = 0.001f;
        boundingBoxMin.sub(border, border, border);
        boundingBoxMax.add(border, border, border);
    }

    @Override
    public void notifyListener() {
        isBoundingSphereOk = false;
    }
}
