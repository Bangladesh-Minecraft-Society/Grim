package ac.grim.grimac.utils.nmsutil;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.data.Pair;
import lombok.Getter;
import org.bukkit.util.Vector;

// Copied directly from Hawk
@Getter
public class Ray implements Cloneable {

    private Vector origin;
    private Vector direction;

    public Ray(Vector origin, Vector direction) {
        this.origin = origin;
        this.direction = direction;
    }

    public Ray(GrimPlayer player, double x, double y, double z, float xRot, float yRot) {
        this.origin = new Vector(x, y, z);
        this.direction = calculateDirection(player, xRot, yRot);
    }

    // Account for FastMath by using player's trig handler
    // Copied from hawk which probably copied it from NMS
    public static Vector calculateDirection(GrimPlayer player, float xRot, float yRot) {
        Vector vector = new Vector();
        float rotX = (float) Math.toRadians(xRot);
        float rotY = (float) Math.toRadians(yRot);
        vector.setY(-player.trigHandler.sin(rotY));
        double xz = player.trigHandler.cos(rotY);
        vector.setX(-xz * player.trigHandler.sin(rotX));
        vector.setZ(xz * player.trigHandler.cos(rotX));
        return vector;
    }

    public Ray clone() {
        Ray clone;
        try {
            clone = (Ray) super.clone();
            clone.origin = this.origin.clone();
            clone.direction = this.direction.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String toString() {
        return "origin: " + origin + " direction: " + direction;
    }

    public Vector getPointAtDistance(double distance) {
        Vector dir = new Vector(direction.getX(), direction.getY(), direction.getZ());
        Vector orig = new Vector(origin.getX(), origin.getY(), origin.getZ());
        return orig.add(dir.multiply(distance));
    }

    //https://en.wikipedia.org/wiki/Skew_lines#Nearest_Points
    public Pair<Vector, Vector> closestPointsBetweenLines(Ray other) {
        // Mathematical Concept: Double cross product to find normal vectors perpendicular to both rays
        // n1 is perpendicular to both the direction vector and the plane formed by both direction vectors
        Vector n1 = direction.clone().crossProduct(other.direction.clone().crossProduct(direction));
        
        // n2 is perpendicular to other.direction and the plane formed by both direction vectors
        Vector n2 = other.direction.clone().crossProduct(direction.clone().crossProduct(other.direction));

        // Mathematical Concept: Parametric point calculation for skew line closest approach
        // Find the parameter value along first ray that gives closest point to second ray
        // Uses dot product to project displacement vector onto normal vector, divided by projection of ray direction
        Vector c1 = origin.clone().add(direction.clone().multiply(other.origin.clone().subtract(origin).dot(n2) / direction.dot(n2)));
        
        // Find the parameter value along second ray that gives closest point to first ray
        Vector c2 = other.origin.clone().add(other.direction.clone().multiply(origin.clone().subtract(other.origin).dot(n1) / other.direction.dot(n1)));

        return new Pair<>(c1, c2);
    }
}
