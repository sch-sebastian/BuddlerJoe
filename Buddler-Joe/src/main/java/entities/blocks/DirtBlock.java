package entities.blocks;

import org.joml.Vector3f;

/**
 * Dirt Block.
 *
 * <p>Holds methods and variables specific to Dirt Blocks.
 */
public class DirtBlock extends Block {

  /** Extended Constructor, dont call directly. */
  DirtBlock(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
    // Must pass block type and hardness here as they are required
    super(BlockMaster.BlockTypes.DIRT, 0.9f, 1f, position, rotX, rotY, rotZ, scale);
  }

  /** Shortened constructer with just position. Dont call directly. */
  DirtBlock(Vector3f position) {
    this(position, 0, 0, 0, 3);
  }

  @Override
  protected void onDestroy() {}
}
