package collision;

import org.joml.AABBf;
import org.joml.Vector3f;

/**
 * A 3D Box around an object that determines the boundaries of the model.
 * has absolute and relative minimum and maximum for each of the three coordinates as well as
 * quick access to dimension
 *
 * @author Joe's Buddler Corp.
 */
public class BoundingBox {

  private float minX;
  private float maxX;
  private float minY;
  private float maxY;
  private float minZ;
  private float maxZ;

  private float minXO;
  private float maxXO;
  private float minYO;
  private float maxYO;
  private float minZO;
  private float maxZO;

  private float dimX;
  private float dimY;
  private float dimZ;

  /**
   * Generate the box around (0/0/0), then wait for the Entity to move it to their position.
   *
   * @param bb a float array of exactly size 6 with the relative coordinates for all dimensions
   *           and 0/0/0 being the center
   */
  public BoundingBox(float[] bb) {
    if (bb.length < 6) {
      return;
    }
    minXO = bb[0];
    maxXO = bb[1];
    minYO = bb[2];
    maxYO = bb[3];
    minZO = bb[4];
    maxZO = bb[5];
  }

  /**
   * Move the Bounding Box to a new position. Usually called when the entity moves.
   *
   * @param newPos this coordinate will be the center of the new bounding box.
   */
  public void moveTo(Vector3f newPos) {
    minX = newPos.x + minXO;
    maxX = newPos.x + maxXO;
    minY = newPos.y + minYO;
    maxY = newPos.y + maxYO;
    minZ = newPos.z + minZO;
    maxZ = newPos.z + maxZO;
    dimX = Math.abs(Math.abs(maxX) - Math.abs(minX));
    dimY = Math.abs(Math.abs(maxY) - Math.abs(minY));
    dimZ = Math.abs(Math.abs(maxZ) - Math.abs(minZ));
  }

  /**
   * Check if two bounding boxes overlap in 1, 2 or 3 dimensions.
   *
   * @param box Bounding Box from a different entity
   * @param dim 1 = X line, 2 = XY plane, 3 = XYZ space
   * @return true if the boxes overlap
   */
  public boolean collidesWith(BoundingBox box, int dim) {
    if (dim >= 1 && !collidesX(box)) {
      return false;
    }
    if (dim >= 2 && !collidesY(box)) {
      return false;
    }
    return dim < 3 || collidesZ(box);
  }

  /**
   * Check if two boxes overlap on the X-axis.
   *
   * @param box Bounding Box from a different entity
   * @return true if the boxes overlap
   */
  private boolean collidesX(BoundingBox box) {
    return !(box.minX > this.maxX) && !(this.minX > box.maxX);
  }

  /**
   * Check if two boxes overlap on the Y-axis.
   *
   * @param box Bounding Box from a different entity
   * @return true if the boxes overlap
   */
  private boolean collidesY(BoundingBox box) {
    return !(box.minY > this.maxY) && !(this.minY > box.maxY);
  }

  /**
   * Check if two boxes overlap on the Z-axis.
   *
   * @param box Bounding Box from a different entity
   * @return true if the boxes overlap
   */
  private boolean collidesZ(BoundingBox box) {
    return !(box.minZ > this.maxZ) && !(this.minZ > box.maxZ);
  }

  /**
   * Scale a box to match the new size of an entity. Usually called when an entity is scaled.
   *
   * @param scale factor to scale with
   */
  public void scale(Vector3f scale) {
    minXO *= scale.x;
    maxXO *= scale.x;
    minYO *= scale.y;
    maxYO *= scale.y;
    minZO *= scale.z;
    maxZO *= scale.z;
  }

  //Getters

  public AABBf getAabbf() {
    return new AABBf(minX, minY, minZ, maxX, maxY, maxZ);
  }

  /**
   * Get the 3D center of the bounding box.
   * @return 3D world coordinates of the center of the box
   */
  public Vector3f getCenter() {
    return new Vector3f(
        getMinX() + getDimX() / 2,
        getMinY() + getDimY() / 2,
        getMinZ() + getDimZ() / 2
    );
  }

  public float getMinX() {
    return minX;
  }

  public float getMaxX() {
    return maxX;
  }

  public float getMinY() {
    return minY;
  }

  public float getMaxY() {
    return maxY;
  }

  private float getMinZ() {
    return minZ;
  }

  public float getMaxZ() {
    return maxZ;
  }

  /*public float getMinXO() {
    return minXO;
  }

  public float getMaxXO() {
    return maxXO;
  }

  public float getMinYO() {
    return minYO;
  }

  public float getMaxYO() {
    return maxYO;
  }

  public float getMinZO() {
    return minZO;
  }

  public float getMaxZO() {
    return maxZO;
  }*/


  public float getDimX() {
    return dimX;
  }

  public float getDimY() {
    return dimY;
  }

  public float getDimZ() {
    return dimZ;
  }
}
