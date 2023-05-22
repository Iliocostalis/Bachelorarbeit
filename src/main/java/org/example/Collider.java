package org.example;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Collider implements Listener {

    Vector3f vector3f;
    Vector3f vector3fTmp;
    Vector3f vector3fTmp2;
    Vector3f vector3fTmp3;
    Vector3f vector3fTmp4;
    Vector3f vector3fTmp5;
    Vector3f vector3fTmp6;

    Vector3f intersectingPosition;

    Objekt objekt;

    Vector3f boundingBoxMin;
    Vector3f boundingBoxMax;

    boolean isBoundingSphereOk = false;

    public Collider(Objekt objekt)
    {
        this.objekt = objekt;
        vector3f = new Vector3f();
        vector3fTmp = new Vector3f();
        vector3fTmp2 = new Vector3f();
        vector3fTmp3 = new Vector3f();
        vector3fTmp4 = new Vector3f();
        vector3fTmp5 = new Vector3f();
        vector3fTmp6 = new Vector3f();
        intersectingPosition = new Vector3f();
        boundingBoxMin = new Vector3f();
        boundingBoxMax = new Vector3f();

        objekt.transformation.addModifiedListener(this);
    }


    public boolean isRayIntersecting(Vector3f rayOrigin, Vector3f rayDirection)
    {
        if(!isBoundingSphereOk)
            calculateBoundingSphere();

        /*
        // Construct a line segment out of the ray
        //Line ab = new Line(r.Position, r.Position + r.Normal);
        // Break ray into a (start) and b (end) components

        rayOrigin.add(rayDirection, vector3fTmp);

        // Project c onto ab, computing the
        // paramaterized position d(t) = a + t * (b - a)
        //t = Dot(c - rayOrigin, ab) / Dot(ab, ab);


        float dotLineLine = vector3fTmp.dot(vector3fTmp);

        boundingSphereCenter.sub(rayOrigin, vector3f);
        float dot = vector3f.dot(vector3fTmp);

        float t = dot / dotLineLine;



        // We only want to clamp t in the positive direction.
        // The ray extends infinatley in this direction!
        //t = Max(t, 0f);

        t = Math.max(t, 0f);

        // Compute the projected position from the clamped t
        // Notice we multiply r.Normal by t, not AB.
        // This is becuase we want the ray in the direction
        // of the normal, which technically the line segment is
        // but this is much more explicit and easy to read.
        //Point d = new Point(rayOrigin + t * rayDirection);
        rayDirection.mulAdd(t, rayOrigin, vector3f);

        // Return result

        //return false;

*/
        //boundingBoxMin.set(0,0,0);
        //boundingBoxMax.set(1,1,1);
        //rayOrigin.set(0.5f,0.5f,-10);
        //rayDirection.set(0,0,1);


        Matrix4f matrix4f = VectorMatrixPool.getMatrix4f();

        objekt.transformation.getMatrix().get(matrix4f);

        matrix4f.invert();

        Matrix4f matrix4f1 = VectorMatrixPool.getMatrix4f();
        Matrix4f matrix4f2 = VectorMatrixPool.getMatrix4f();
        Matrix4f matrix4f3 = VectorMatrixPool.getMatrix4f();
        Matrix4f matrix4f4 = VectorMatrixPool.getMatrix4f();

        Quaternionf quaternionf = VectorMatrixPool.getQuaternionf();

        matrix4f1.identity();
        matrix4f1.translate(10,0,0);
        matrix4f1.invert(matrix4f2);

        matrix4f3.identity();
        matrix4f3.scale(10,1,1);
        matrix4f3.invert(matrix4f4);



        quaternionf.identity();
        quaternionf.rotateY(ConstValues.DEGREES_TO_RADIANS * 45f);

        matrix4f1.identity();
        matrix4f1.translate(10,0,0);
        matrix4f1.rotate(quaternionf);
        matrix4f1.scale(2);

        matrix4f1.invert(matrix4f2);

        Vector3f pos = VectorMatrixPool.getVector3f();
        Vector3f dir = VectorMatrixPool.getVector3f();
        pos.set(0,0,0);
        dir.set(1,0,0);

        pos.mulPosition(matrix4f2);
        dir.mulDirection(matrix4f2);




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
            Vector3f rayOriginTransformed = VectorMatrixPool.getVector3f();
            Vector3f rayDirectionTransformed = VectorMatrixPool.getVector3f();

            rayOriginTransformed.set(rayOrigin);
            rayOriginTransformed.mulPosition(matrix4f2);
            rayDirectionTransformed.set(rayDirection);
            rayDirectionTransformed.mulDirection(matrix4f2);

            return exactCollision(rayOriginTransformed, rayDirectionTransformed);
        }

        return false;
    }

    private boolean exactCollision(Vector3f rayOrigin, Vector3f rayDirection)
    {
        Mesh mesh = UmgebungsLader.getMesh(objekt.meshHash);
        float t = 0;

        for(int i = 0; i < mesh.vertices.length; i+=9)
        {
            Vector3f v0 = vector3fTmp2;
            Vector3f v1 = vector3fTmp3;
            Vector3f v2 = vector3fTmp4;

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
            v1.sub(v0, vector3fTmp5);
            v2.sub(v0, vector3fTmp6);

            // no need to normalize
            ///Vec3f N = v0v1.crossProduct(v0v2); // N
            Vector3f normal = vector3fTmp;
            vector3fTmp5.cross(vector3fTmp6, normal);
            ///float area2 = N.length();
            float area2 = normal.length();
            normal.normalize();

            if (rayDirection.dot(normal) > 0)
                continue; // back-facing surface

            // Step 1: finding P

            // check if the ray and plane are parallel.
            ///float NdotRayDirection = N.dotProduct(dir);
            ///if (fabs(NdotRayDirection) < kEpsilon) // almost 0
            ///    return false; // they are parallel, so they don't intersect!
            float NdotRayDirection = normal.dot(rayDirection);
            if(Math.abs(NdotRayDirection) < 0.000001f)
                continue;

            // compute d parameter using equation 2
            ///float d = -N.dotProduct(v0);
            float d = -normal.dot(v0);

            // compute t (equation 3)
            //t = -(N.dotProduct(orig) + d) / NdotRayDirection;
            t = -(normal.dot(rayOrigin) + d) / NdotRayDirection;

            // check if the triangle is behind the ray
            if (t < 0) continue; // the triangle is behind

            // compute the intersection point using equation 1
            ///Vec3f P = orig + t * dir;
            rayDirection.mulAdd(t, rayOrigin, intersectingPosition);

            // Step 2: inside-outside test
            ///Vec3f C; // vector perpendicular to triangle's plane

            // edge 0
            ///Vec3f edge0 = v1 - v0;
            ///Vec3f vp0 = P - v0;
            ///C = edge0.crossProduct(vp0);
            ///if (N.dotProduct(C) < 0) return false; // P is on the right side
            v1.sub(v0, vector3fTmp);
            intersectingPosition.sub(v0, vector3fTmp6);
            vector3fTmp.cross(vector3fTmp6);
            if(normal.dot(vector3fTmp) < 0)
                continue;


            // edge 1
            ///Vec3f edge1 = v2 - v1;
            ///Vec3f vp1 = P - v1;
            ///C = edge1.crossProduct(vp1);
            ///if (N.dotProduct(C) < 0)  return false; // P is on the right side
            v2.sub(v1, vector3fTmp);
            intersectingPosition.sub(v1, vector3fTmp6);
            vector3fTmp.cross(vector3fTmp6);
            if(normal.dot(vector3fTmp) < 0)
                continue;

            // edge 2
            ///Vec3f edge2 = v0 - v2;
            ///Vec3f vp2 = P - v2;
            ///C = edge2.crossProduct(vp2);
            ///if (N.dotProduct(C) < 0) return false; // P is on the right side;
            v0.sub(v2, vector3fTmp);
            intersectingPosition.sub(v2, vector3fTmp6);
            vector3fTmp.cross(vector3fTmp6);
            if(normal.dot(vector3fTmp) < 0)
                continue;

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

        for(int i = 0; i < mesh.vertices.length; i+=3)
        {
            vector3fTmp.x = mesh.vertices[i];
            vector3fTmp.y = mesh.vertices[i+1];
            vector3fTmp.z = mesh.vertices[i+2];

            vector3fTmp.mulPosition(objekt.transformation.getMatrix());

            boundingBoxMin.x = Math.min(boundingBoxMin.x, vector3fTmp.x);
            boundingBoxMin.y = Math.min(boundingBoxMin.y, vector3fTmp.y);
            boundingBoxMin.z = Math.min(boundingBoxMin.z, vector3fTmp.z);

            boundingBoxMax.x = Math.max(boundingBoxMax.x, vector3fTmp.x);
            boundingBoxMax.y = Math.max(boundingBoxMax.y, vector3fTmp.y);
            boundingBoxMax.z = Math.max(boundingBoxMax.z, vector3fTmp.z);
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
