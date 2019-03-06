package entities.blocks;

import org.joml.Vector3f;


public class GrassBlock extends Block {

    public GrassBlock(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(BlockMaster.BlockTypes.GRASS, .7f, position, rotX, rotY, rotZ, scale);
    }

    public GrassBlock(Vector3f position) {
        this(position, 0, 0 ,0, 3);

    }

    @Override
    protected void onDestroy() {

    }
}