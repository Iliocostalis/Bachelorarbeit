package org.example;

import org.example.assets.JsonCar;
import org.example.assets.JsonObjektInstance;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;

public class Auto extends Objekt implements Listener{

    //Default direction -> X+

    ArrayList<Sensor> sensoren;
    Vector3f back = new Vector3f(0,0.1f,0);
    Vector3f front = new Vector3f(1,0.1f,0);

    Vector3f currentDirection = new Vector3f(1,0,0);

    Vector3f direction = new Vector3f(1,0,0);
    Vector3f directionFront = new Vector3f(1,0,0);

    Vector3f vector3fTmp = new Vector3f();
    Quaternionf quaternionTmp = new Quaternionf();

    float rotation = (float)Math.toRadians(20);

    static float rotationS = 0;
    float length;

    private boolean skipTransformationUpdate;

    public Auto(JsonCar jsonCar, JsonObjektInstance jsonObjektInstance, int meshHash, ArrayList<Sensor> sensoren)
    {
        super(jsonObjektInstance, meshHash);
        this.sensoren = sensoren;
        transformation.addModifiedListener(this);

        skipTransformationUpdate = false;
        length = jsonCar.car_length;
        Mesh mesh = UmgebungsLader.getMesh(meshHash);
        mesh.getSize(vector3fTmp);

        float scale = length / vector3fTmp.x;
        transformation.setScale(scale);

        measureSpeedDif();

        isRayIntersecting(new Vector3f(0.0f,0.2f,-10), new Vector3f(0,0,1));
    }

    private void measureSpeedDif()
    {
        Vector3f pos = new Vector3f(1,0,0);
        Quaternionf rot = new Quaternionf();

        Vector3f posStart = new Vector3f(1,0,0);
        Quaternionf rotStart = new Quaternionf();

        posStart.set(transformation.getPosition());
        rotStart.set(transformation.getQuaternion());
        pos.set(transformation.getPosition());
        rot.set(transformation.getQuaternion());

        for(int l = 0; l < 2; l++)
        {
            rotationS = l*40;

            move();

            vector3fTmp.set(transformation.getPosition());

            float distanceNormal = vector3fTmp.distance(pos) * 1000;
            System.out.println(distanceNormal);

            transformation.setPosition(posStart);
            transformation.setQuaternion(rotStart);
        }
    }

    public void update()
    {
        move();

        for(Sensor sensor : sensoren)
        {
            sensor.updatePosition(transformation);
            sensor.ausfuehren(0);
        }
    }

    private void move()
    {
        skipTransformationUpdate = true;

        //calculate front /back from direction and position
        currentDirection.mul(length*0.5f, vector3fTmp);

        transformation.getPosition().add(vector3fTmp, front);
        transformation.getPosition().sub(vector3fTmp, back);

        //transformation.getPosition(front);
        //front.add(vector3fTmp);
        //transformation.getPosition(back);
        //back.sub(vector3fTmp);

        // rotate direction
        currentDirection.rotateY((float)Math.toRadians(rotationS), direction);

        // move front
        front.add(direction.mul(0.1f));

        // get new direction
        front.sub(back, direction);
        direction.normalize();
        currentDirection.set(direction);

        // calculate new back position
        direction.mul(length, vector3fTmp);
        front.sub(vector3fTmp, back);

        // calculate new position (center of front and back)
        front.add(back, vector3fTmp);
        vector3fTmp.mul(0.5f);
        transformation.setPosition(vector3fTmp);

        // calculate new rotation
        quaternionTmp.identity();
        quaternionTmp.rotateTo(directionFront, direction);
        transformation.setQuaternion(quaternionTmp);

        skipTransformationUpdate = false;
    }

    public void destroy()
    {
        for(Sensor sensor : sensoren)
            sensor.destroy();
    }

    @Override
    public void notifyListener() {
        if(skipTransformationUpdate)
            return;

        directionFront.rotate(transformation.getQuaternion(), vector3fTmp);
        currentDirection.set(vector3fTmp);
    }
}