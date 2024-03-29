package engine.particles;

import game.Game;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Set rules of physics for particles and emits them. The physics "simulation" (transformation) is
 * done in the Particle Renderer and the shaders
 *
 * <p>This class generates and emits particles and sets rules how they behave in the 3D world.
 *
 * <p>Can set texture, amount per second, speed, gravity compliance, life duration and scale of the
 * particles including a value for how much these values can deviate among particles of this system.
 */
@SuppressWarnings("Duplicates")
public class ParticleSystem {

  private final float pps;
  private final float averageSpeed;
  private final float gravityComplient;
  private final float averageLifeLength;
  private final float averageScale;
  private final ParticleTexture texture;
  private final Random random = new Random();
  private float speedError;
  private float lifeError;
  private float scaleError = 0;
  private boolean randomRotation = false;
  private Vector3f direction;
  // private float rotationDeviation = 0; //TODO (Matthias): Add some rotation noise
  private float directionDeviation = 0;
  private Vector3f rotationAxis;

  /**
   * Create a new Particle System. Can specify various settings for how the particles behave.
   *
   * @param texture ParticleTexture Object
   * @param pps Particles per second. Will be probabilistically rounded each frame.
   * @param speed Distance travelled per second.
   * @param gravityComplient Effect of the gravity constant. 0 means no gravity, negative numbers
   *     mean negative gravity.
   * @param lifeLength Duration before the particle is removed in seconds.
   * @param scale Size of the particle.
   */
  public ParticleSystem(
      ParticleTexture texture,
      float pps,
      float speed,
      float gravityComplient,
      float lifeLength,
      float scale) {
    this.texture = texture;
    this.pps = pps;
    this.averageSpeed = speed;
    this.gravityComplient = gravityComplient;
    this.averageLifeLength = lifeLength;
    this.averageScale = scale;
  }

  /**
   * Returns a direction vector randomized over a plane area (rotate direction vector around axis by
   * a random theta).
   */
  private static Vector3f generateRandomUnitVectorWithinPlane(Vector3f direction, Vector3f axis) {

    Random random = new Random();
    float theta = (float) (random.nextFloat() * 2f * Math.PI);

    return new Vector3f().set(direction).rotateAxis(theta, axis.x, axis.y, axis.z);
  }

  /** Returns a direction vector randomized over a cone area. */
  private static Vector3f generateRandomUnitVectorWithinCone(Vector3f coneDirection, float angle) {
    float cosAngle = (float) Math.cos(angle);
    Random random = new Random();
    float theta = (float) (random.nextFloat() * 2f * Math.PI);
    float z = cosAngle + (random.nextFloat() * (1 - cosAngle));
    float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
    float x = (float) (rootOneMinusZSquared * Math.cos(theta));
    float y = (float) (rootOneMinusZSquared * Math.sin(theta));

    Vector4f direction = new Vector4f(x, y, z, 1);
    if (coneDirection.x != 0
        || coneDirection.y != 0
        || (coneDirection.z != 1 && coneDirection.z != -1)) {
      Vector3f rotateAxis =
          new Vector3f().set(coneDirection).cross(new Vector3f(0, 0, 1)).normalize();

      float rotateAngle =
          (float) Math.acos(new Vector3f().set(coneDirection).dot(new Vector3f(0, 0, 1)));
      Matrix4f rotationMatrix = new Matrix4f();
      rotationMatrix.rotate(-rotateAngle, rotateAxis).transform(direction);
    } else if (coneDirection.z == -1) {
      direction.z *= -1;
    }
    return new Vector3f(direction.x, direction.y, direction.z);
  }

  /**
   * Initial movement direction vector for the particles.
   *
   * @param direction The average direction in which particles are emitted.
   * @param deviation A value between 0 and 1 indicating how far from the chosen direction particles
   *     can deviate.
   */
  public void setDirection(Vector3f direction, float deviation) {
    this.direction = new Vector3f(direction);
    this.directionDeviation = (float) (deviation * Math.PI);
  }

  /**
   * Set rotation axis when generating particles on a plane. This is the normal vector to the plane,
   * so we distribute the particles evenly on the plane.
   *
   * @param rotationAxis normal vector to a plane usually
   * @param deviation to get some deviation away from a flat plane
   */
  @SuppressWarnings("UnusedParameters") // This is added as a to-do
  public void setRotationAxis(Vector3f rotationAxis, float deviation) {
    this.rotationAxis = rotationAxis;
    // this.rotationDeviation = (float) (deviation * Math.PI);
  }

  public void randomizeRotation() {
    randomRotation = true;
  }

  /**
   * Deviation in speed.
   *
   * @param error A number between 0 and 1, where 0 means no error margin.
   */
  public void setSpeedError(float error) {
    this.speedError = error * averageSpeed;
  }

  /**
   * Deviation in life duration.
   *
   * @param error A number between 0 and 1, where 0 means no error margin.
   */
  public void setLifeError(float error) {
    this.lifeError = error * averageLifeLength;
  }

  /**
   * Deviation in size.
   *
   * @param error A number between 0 and 1, where 0 means no error margin.
   */
  public void setScaleError(float error) {
    this.scaleError = error * averageScale;
  }

  /**
   * Emits and returns a list of these particles. Length of list will not be constant since it is
   * scaled with time delta
   *
   * @param systemCenter position of emittance
   * @return list of particles emitted with this call (if you need more control)
   */
  @SuppressWarnings("UnusedReturnValue")
  public List<Particle> generateParticles(Vector3f systemCenter) {
    List<Particle> particles = new ArrayList<>();
    float delta = (float) Game.dt();
    float particlesToCreate = pps * delta;

    // Full particles
    int count = (int) Math.floor(particlesToCreate);

    // Chance to create another particle
    float partialParticle = particlesToCreate % 1;

    for (int i = 0; i < count; i++) {
      particles.add(emitParticle(systemCenter));
    }
    if (Math.random() < partialParticle) {
      // Chance equal to fraction of particle
      particles.add(emitParticle(systemCenter));
    }
    return particles;
  }

  private Particle emitParticle(Vector3f center) {
    Vector3f velocity;
    if (direction != null) {
      // Cone style random vectors
      velocity = generateRandomUnitVectorWithinCone(direction, directionDeviation);

      if (rotationAxis != null) {
        // Random in a plane described by direction
        velocity = generateRandomUnitVectorWithinPlane(velocity, rotationAxis);
      }
    } else {
      // Completely random vectors
      velocity = generateRandomUnitVector();
    }
    velocity.normalize();
    velocity.mul(generateValue(averageSpeed, speedError));
    float scale = generateValue(averageScale, scaleError);
    float lifeLength = generateValue(averageLifeLength, lifeError);
    return new Particle(
        texture,
        new Vector3f(center),
        velocity,
        gravityComplient,
        lifeLength,
        generateRotation(),
        scale);
  }

  private float generateValue(float average, float errorMargin) {
    float offset = (random.nextFloat() - 0.5f) * 2f * errorMargin;
    return average + offset;
  }

  private float generateRotation() {
    if (randomRotation) {
      return random.nextFloat() * 360f;
    } else {
      return 0;
    }
  }

  private Vector3f generateRandomUnitVector() {
    float theta = (float) (random.nextFloat() * 2f * Math.PI);
    float z = (random.nextFloat() * 2) - 1;
    float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
    float x = (float) (rootOneMinusZSquared * Math.cos(theta));
    float y = (float) (rootOneMinusZSquared * Math.sin(theta));
    return new Vector3f(x, y, z);
  }
}
