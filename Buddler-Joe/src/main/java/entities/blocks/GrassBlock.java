package entities.blocks;

import org.joml.Vector3f;

/**
 * Dirt Block
 *
 * Holds methods and variables specific to Dirt Blocks.
 */
public class GrassBlock extends Block {

    /**
     * Extended Constructor, dont call directly.
     */
    GrassBlock(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(BlockMaster.BlockTypes.GRASS, .7f, position, rotX, rotY, rotZ, scale);
    }

    /**
     Shortened constructer with just position. Dont call directly.
     */
    GrassBlock(Vector3f position) {
        this(position, 0, 0 ,0, 3);

    }

    @Override
    protected void onDestroy() {

    }
}
