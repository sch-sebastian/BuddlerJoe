package entities.items;

import bin.Game;
import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.particles.systems.Fire;
import engine.particles.systems.Smoke;
import engine.render.Loader;
import engine.render.objConverter.OBJFileLoader;
import engine.textures.ModelTexture;
import entities.blocks.Block;
import entities.light.Light;
import entities.light.LightMaster;
import org.joml.Vector3f;
import util.MousePlacer;

import java.util.Random;

public class Torch extends Item {

    private static TexturedModel preloadedModel;

    private Block block;

    private Vector3f colour;
    private float brightness;

    private Light light;

    private Vector3f flamePosition;
    private Vector3f flameOffset;
    private Fire flame;
    private Smoke smoke;

    private float flickerFactor;
    private Random random;

    /**
     * Extended Constructor for Torch. Don't use directly. Use the Item Master to create items.
     */
    Torch(Vector3f position, Block block, Vector3f colour, float rotX, float rotY, float rotZ, float scale) {
        super(ItemMaster.ItemTypes.TORCH, getPreloadedModel(), position, rotX, rotY, rotZ, scale);

        setPlacerMode(MousePlacer.modes.BLOCK.getMode()); //Torches can be placed on blocks

        this.colour = colour;
        this.brightness = 2;
        this.block = block;

        random = new Random();
        flameOffset = new Vector3f(
                getbBox().getDimX()/2,
                getbBox().getDimY()-getbBox().getDimY()/20,
                -getbBox().getDimZ()/2
        );

        flamePosition = new Vector3f(position).add(flameOffset);
        light = LightMaster.generateLight(LightMaster.LightTypes.TORCH, flamePosition, colour.mul(brightness));

        //Generate Fuse Effect
        flame = new Fire(15, .4f, 0, 2f, 1.5f);
        flame.setDirection(new Vector3f(0,1,0), 0f);
        flame.setLifeError(.2f);
        flame.setSpeedError(.5f);
        flame.setScaleError(.3f);

        //Generate Smoke Effect
        smoke = new Smoke(20, 0, -.017f, 3f, 1f);
        smoke.setDirection(new Vector3f(0,1,0), .2f);
        smoke.setLifeError(.3f);
        smoke.randomizeRotation();

    }

    Torch(Vector3f position) {
        this(position, null);
    }

    Torch(Vector3f position, Block block) {
        this(position,  block, new Vector3f(1f,244/255f,229/255f), 0,0,0, .2f);
    }

    /**
     * Preload model
     *
     * @param loader passed by Item Master
     */
    public static void init(Loader loader) {
        RawModel rawTorch = loader.loadToVAO(OBJFileLoader.loadOBJ("torch"));
        setPreloadedModel(new TexturedModel(rawTorch, new ModelTexture(loader.loadTexture("torch"))));
    }

    private static void setPreloadedModel(TexturedModel preloadedModel) {
        Torch.preloadedModel = preloadedModel;
    }

    private static TexturedModel getPreloadedModel() {
        return preloadedModel;
    }


    @Override
    public void update() {
        flame.generateParticles(flamePosition);
//        smoke.generateParticles(new Vector3f(flamePosition.x, flamePosition.y+0.2f, flamePosition.z));
        updateAttenuationNoise();
        if(block != null && block.isDestroyed()) {
            setDestroyed(true);
        }
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
        light.setColour(colour.mul(brightness));
    }

    public Vector3f getAttenuation() {
        return light.getAttenuation();
    }

    public void setAttenuation(Vector3f attenuation) {
        light.setAttenuation(attenuation);
    }

    @Override
    public void setDestroyed(boolean destroyed) {
        super.setDestroyed(destroyed);
        light.setDestroyed(destroyed);

    }

    private void updateAttenuationNoise() {
        //Add "light flicker" effect with gaussian random walk and pull to the average
        flickerFactor += (float) (random.nextGaussian() / 5000);
        flickerFactor -= flickerFactor * .05f;
        light.setAttenuation(new Vector3f(light.getType().getBaseAttenuation()).add(new Vector3f(0, flickerFactor, flickerFactor / 5f)));
    }

    @Override
    public void setPosition(Vector3f position) {
        super.setPosition(position);
        flamePosition = new Vector3f(position).add(flameOffset);
        light.setPosition(flamePosition);
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }
}