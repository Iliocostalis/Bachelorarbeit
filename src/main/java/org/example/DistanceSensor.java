package org.example;

import org.joml.Vector3f;

public class DistanceSensor extends Sensor {

    DistanceSensor() {
        super();

        position.set(1,10,1);
        //rotation.lookAlong(0,-1,0,0,1,0);
        rotation.rotateZ(-ConstValues.DEGREES_TO_RADIANS * 90f);
    }

    @Override
    public void ausfuehren(float vergangeneZeit)
    {
        Vector3f pos = VectorMatrixPool.getVector3f();

        Vector3f direction = VectorMatrixPool.getVector3f();
        direction.set(1,0,0);
        direction.rotate(rotation);

        Umgebung.umgebung.getRayIntersection(position, direction, 100f, pos);

        pos.sub(position);
        System.out.println(pos.length());
    }

    @Override
    public void destroy() {

    }
}
