package terrains;

import engine.models.RawModel;
import engine.render.Loader;
import engine.textures.TerrainTexture;
import engine.textures.TerrainTexturePack;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import javax.imageio.ImageIO;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.Maths;

/** Terrain with height map. */
public class Terrain extends TerrainFlat {

  private static final float MAX_HEIGHT = SIZE / 10;
  private static final float MAX_PIXEL_COLOUR = 256 * 256 * 256;

  private float[][] heights;

  /**
   * Create a terrain tile according to a height map.
   *
   * @param gridX starting point X world coordinate
   * @param gridZ starting point Z world coordinate
   * @param loader main loader
   * @param texturePack texture pack with all the textures required for the blend map
   * @param blendMap "heat map" image for how to blend images (load as Texture)
   * @param heightMap "heat map" image for the height of the terrain at every coordinate (specify
   *     image name)
   */
  public Terrain(
      int gridX,
      int gridZ,
      Loader loader,
      TerrainTexturePack texturePack,
      TerrainTexture blendMap,
      String heightMap) {
    super(gridX, gridZ, loader, texturePack, blendMap);
    model = this.generateTerrain(loader, heightMap);
  }

  /**
   * Used by other functions to get Y coordinate of the terrain at a point. For example to place
   * objects on the terrain.
   *
   * @param worldX X coordinate
   * @param worldZ Z coordinate
   * @return Y coordinate at XZ
   */
  public float getHeightOfTerrain(float worldX, float worldZ) {
    float terrainX = worldX - getCoordX();
    float terrainZ = worldZ - getCoordZ();
    float gridSquareSize = SIZE / ((float) heights.length - 1);
    int gridX = (int) Math.floor(terrainX / gridSquareSize);
    int gridZ = (int) Math.floor(terrainZ / gridSquareSize);

    if (gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0) {
      return 0;
    }

    float coordX = (terrainX % gridSquareSize) / gridSquareSize;
    float coordZ = (terrainZ % gridSquareSize) / gridSquareSize;

    // Determine which triangle in square. x = 1-z is true on the border
    float answer;
    if (coordX <= (1 - coordZ)) {
      answer =
          Maths.barryCentric(
              new Vector3f(0, heights[gridX][gridZ], 0),
              new Vector3f(1, heights[gridX + 1][gridZ], 0),
              new Vector3f(0, heights[gridX][gridZ + 1], 1),
              new Vector2f(coordX, coordZ));
    } else {
      answer =
          Maths.barryCentric(
              new Vector3f(1, heights[gridX + 1][gridZ], 0),
              new Vector3f(1, heights[gridX + 1][gridZ + 1], 1),
              new Vector3f(0, heights[gridX][gridZ + 1], 1),
              new Vector2f(coordX, coordZ));
    }

    return answer;
  }

  /**
   * Generate a Terrain with height map. Creates Vertices, Texture Coords, Normals and Indices for a
   * Terrain according to a heat map Size and "resolution" can be set in the class vars, this is
   * intended to be used as "Tiles" of terrain
   *
   * @param loader main
   * @param heightMap "heat map" image for the height of the terrain at every coordinate (specify
   *     image name)
   * @return Raw Model of Terrain
   */
  private RawModel generateTerrain(Loader loader, String heightMap) {
    BufferedImage image = null;
    try {
      InputStream in = getClass().getResourceAsStream("/assets/textures/" + heightMap + ".png");
      image = ImageIO.read(in);
    } catch (IOException e) {
      e.printStackTrace();
    }

    int vertexCount = Objects.requireNonNull(image).getHeight();

    heights = new float[vertexCount][vertexCount];

    int count = vertexCount * vertexCount;
    float[] vertices = new float[count * 3];
    float[] normals = new float[count * 3];
    float[] textureCoords = new float[count * 2];
    int[] indices = new int[6 * (vertexCount - 1) * (vertexCount - 1)];
    int vertexPointer = 0;
    for (int i = 0; i < vertexCount; i++) {
      for (int j = 0; j < vertexCount; j++) {
        vertices[vertexPointer * 3] = (float) j / ((float) vertexCount - 1) * SIZE;
        float height = getHeight(j, i, image);
        heights[j][i] = height;
        vertices[vertexPointer * 3 + 1] = height;
        vertices[vertexPointer * 3 + 2] = (float) i / ((float) vertexCount - 1) * SIZE;
        Vector3f normal = calculateNormal(j, i, image);
        normals[vertexPointer * 3] = normal.x;
        normals[vertexPointer * 3 + 1] = normal.y;
        normals[vertexPointer * 3 + 2] = normal.z;
        textureCoords[vertexPointer * 2] = (float) j / ((float) vertexCount - 1);
        textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) vertexCount - 1);
        vertexPointer++;
      }
    }
    int pointer = 0;
    for (int gz = 0; gz < vertexCount - 1; gz++) {
      for (int gx = 0; gx < vertexCount - 1; gx++) {
        int topLeft = (gz * vertexCount) + gx;
        int topRight = topLeft + 1;
        int bottomLeft = ((gz + 1) * vertexCount) + gx;
        indices[pointer++] = topLeft;
        indices[pointer++] = bottomLeft;
        indices[pointer++] = topRight;
        indices[pointer++] = topRight;
        indices[pointer++] = bottomLeft;

        int bottomRight = bottomLeft + 1;
        indices[pointer++] = bottomRight;
      }
    }
    return loader.loadToVao(vertices, textureCoords, normals, indices);
  }

  /** Calculate normals at a coordinate to get proper lighting and pseudo shadow effect. */
  private Vector3f calculateNormal(int x, int z, BufferedImage image) {
    float heightL = getHeight(x - 1, z, image);
    float heightR = getHeight(x + 1, z, image);
    float heightD = getHeight(x, z - 1, image);
    float heightU = getHeight(x, z + 1, image);
    Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
    normal.normalize();
    return normal;
  }

  private float getHeight(int x, int z, BufferedImage image) {
    if (x < 0 || x >= image.getHeight() || z < 0 || z >= image.getHeight()) {
      return 0;
    }
    float height = image.getRGB(x, z);
    // Scale and norm
    height += MAX_PIXEL_COLOUR / 2f;
    height /= MAX_PIXEL_COLOUR / 2f;
    height *= MAX_HEIGHT;
    return height;
  }
}
