package org.example.virtualEnvironment;

import org.example.Renderer;
import org.example.VectorMatrixPool;
import org.example.sensors.*;
import org.joml.Vector3f;

import java.util.ArrayList;

public class Environment {
    public static Environment environment;

    public String sceneName;

    public Auto auto;
    public ArrayList<VirtualObject> objects = new ArrayList<>();
    public ArrayList<VirtualObject> debugObjects = new ArrayList<>();
    public Camera camera;
    private RenderTarget renderTarget;

    Environment(String sceneName) {
        this.sceneName = sceneName;
        renderTarget = new RenderTarget(720, 480, 0, RENDER_TARGET_COLOR_FORMAT.RGB);
        camera = new Camera(renderTarget, false);
    }

    public void update(long nanoseconds) {
        VectorMatrixPool.returnAll();
        if(auto != null)
            auto.update(nanoseconds);
    }

    public void draw() {
        Renderer renderer = Renderer.getInstance();

        for (VirtualObject o : objects) {
            renderer.draw(o);
        }

        if(auto != null)
            renderer.draw(auto);
    }

    public void visualize() {
        Renderer renderer = Renderer.getInstance();

        camera.position.x = 200;
        camera.position.y = 300;
        camera.position.z = -250;

        camera.lookAt.x = 0;
        camera.lookAt.y = -150;
        camera.lookAt.z = 0;

        camera.updateMatrix();

        renderer.setKamera(camera);

        draw();

        for (VirtualObject o : debugObjects) {
            renderer.draw(o);
        }
        debugObjects.clear();
    }

    public void getRayIntersection(Vector3f rayOrigin, Vector3f rayDirection, float maxDistance, Vector3f outPosition) {
        Vector3f rayDirectionNormalized = VectorMatrixPool.getVector3f();
        Vector3f intersection = VectorMatrixPool.getVector3f();
        Vector3f intersectionOffset = VectorMatrixPool.getVector3f();

        rayDirection.normalize(rayDirectionNormalized);

        rayDirectionNormalized.mulAdd(maxDistance, rayOrigin, outPosition);

        float distanceSquared = maxDistance * maxDistance;

        for (VirtualObject o : objects) {
            boolean intersecting = o.isRayIntersecting(rayOrigin, rayDirection);

            if(intersecting) {
                intersection.set(o.getRayIntersectionPosition());
                intersection.sub(rayOrigin, intersectionOffset);

                float distance = intersectionOffset.lengthSquared();
                if(distance < distanceSquared) {
                    distanceSquared = distance;
                    outPosition.set(intersection);
                }
            }
        }
    }

    public void destroy() {
        if(auto != null)
            auto.destroy();

        renderTarget.destroy();
        objects.clear();
    }
}
