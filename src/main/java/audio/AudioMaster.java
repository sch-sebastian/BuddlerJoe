package audio;

import static org.lwjgl.openal.AL10.AL_FORMAT_MONO16;
import static org.lwjgl.openal.AL10.AL_FORMAT_STEREO16;
import static org.lwjgl.openal.AL10.AL_PLAYING;
import static org.lwjgl.openal.AL10.AL_SOURCE_STATE;
import static org.lwjgl.openal.AL10.alBufferData;
import static org.lwjgl.openal.AL10.alGenBuffers;
import static org.lwjgl.openal.AL10.alGetSourcei;
import static org.lwjgl.openal.AL10.alSourceStop;
import static org.lwjgl.openal.ALC11.ALC_ALL_DEVICES_SPECIFIER;
import static org.lwjgl.openal.ALC11.alcCloseDevice;
import static org.lwjgl.openal.ALC11.alcCreateContext;
import static org.lwjgl.openal.ALC11.alcDestroyContext;
import static org.lwjgl.openal.ALC11.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC11.alcOpenDevice;
import static org.lwjgl.openal.EXTThreadLocalContext.alcSetThreadContext;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_close;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_get_info;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_get_samples_short_interleaved;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_open_memory;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_stream_length_in_samples;
import static org.lwjgl.system.MemoryUtil.NULL;
import static util.IoUtil.ioResourceToByteBuffer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALUtil;
import org.lwjgl.stb.STBVorbisInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Derived from the official LWJGL 3 demo class to create audio capabilities via openAL. Heavily
 * modified for our purposes.
 */
public class AudioMaster {

  private static final Logger logger = LoggerFactory.getLogger(AudioMaster.class);

  private static Map<SoundCategory, ArrayList<Integer>> buffers = new HashMap<>();
  private static long context;
  private static long device;

  private static boolean enabled = true;

  /**
   * Initialize the AudioMaster with its capabilities. Needs to be called before anything else can
   * be done with audio. Must be called on the main thread!
   */
  public static void init() {
    device = alcOpenDevice((ByteBuffer) null);
    if (device == NULL) {
      throw new IllegalStateException("Failed to open the default device.");
    }

    ALCCapabilities deviceCaps = ALC.createCapabilities(device);

    if (!deviceCaps.OpenALC10) {
      throw new IllegalStateException();
    }

    /* print capabilities
    System.out.println("OpenALC10: " + deviceCaps.OpenALC10);
    System.out.println("OpenALC11: " + deviceCaps.OpenALC11);
    System.out.println("caps.ALC_EXT_EFX = " + deviceCaps.ALC_EXT_EFX);
    */

    if (deviceCaps.OpenALC11) {
      List<String> devices = ALUtil.getStringList(NULL, ALC_ALL_DEVICES_SPECIFIER);
      if (devices == null) {
        logger.warn("Error no device.");
      } else {
        if (devices.size() > 0) {
          logger.info("Using audio device: " + devices.get(0));
        }
        /* Print all devices
        for (int i = 0; i < devices.size(); i++) {
          System.out.println(i + ": " + devices.get(i));
        } */
      }
    }

    /* print devault specifier
    String defaultDeviceSpecifier =
        Objects.requireNonNull(alcGetString(NULL, ALC_DEFAULT_DEVICE_SPECIFIER));
     System.out.println("Default device: " + defaultDeviceSpecifier);
     */

    // Create context and capabilities (must be on game loop thread)
    context = alcCreateContext(device, (IntBuffer) null);
    alcSetThreadContext(context);
    AL.createCapabilities(deviceCaps);

    /* Debug Messages
    System.out.println("ALC_FREQUENCY: " + alcGetInteger(device, ALC_FREQUENCY) + "Hz");
    System.out.println("ALC_REFRESH: " + alcGetInteger(device, ALC_REFRESH) + "Hz");
    System.out.println("ALC_SYNC: " + (alcGetInteger(device, ALC_SYNC) == ALC_TRUE));
    System.out.println("ALC_MONO_SOURCES: " + alcGetInteger(device, ALC_MONO_SOURCES));
    System.out.println("ALC_STEREO_SOURCES: " + alcGetInteger(device, ALC_STEREO_SOURCES));
    */

    // Load sounds
    loadSound(SoundCategory.DIG, "dig1");
    loadSound(SoundCategory.DIG, "dig2");
    loadSound(SoundCategory.DIG, "dig3");
    loadSound(SoundCategory.DIG, "dig4");
    loadSound(SoundCategory.DIG, "dig5");
    loadSound(SoundCategory.DIG, "dig6");
    loadSound(SoundCategory.DIG, "dig7");
    loadSound(SoundCategory.DIG, "dig8");
    loadSound(SoundCategory.EXPLOSION, "Explosion");
    loadSound(SoundCategory.EXPLOSION, "Explosion2");
    loadSound(SoundCategory.FUSE, "fuse");
    loadSound(SoundCategory.HEART, "Heart");
    loadSound(SoundCategory.HEART, "take Heart");
    loadSound(SoundCategory.FREEZE, "Freeze1");
    loadSound(SoundCategory.FREEZE, "Freeze2");
    loadSound(SoundCategory.FREEZE, "Freeze3");
    loadSound(SoundCategory.DAMAGE, "Autsch");
    loadSound(SoundCategory.DAMAGE, "Game Over");
    loadSound(SoundCategory.GAMEOVER, "Game Over2");
    loadSound(SoundCategory.GAMEOVER, "Game Over3");
    loadSound(SoundCategory.PICK, "pick1");
    loadSound(SoundCategory.PICK, "pick2");
    loadSound(SoundCategory.PICK, "pick3");
    loadSound(SoundCategory.PICK, "pick4");
    loadSound(SoundCategory.PICK, "pick5");
    loadSound(SoundCategory.PICK, "pick6");
    loadSound(SoundCategory.PICK, "pick7");
    loadSound(SoundCategory.PICK, "pick8");
    loadSound(SoundCategory.STEROID, "steroid2");
    loadSound(SoundCategory.STEROID, "steam");
    loadSound(SoundCategory.BACKGROUND, "mining");
    loadSound(SoundCategory.INTRO, "intro");
  }

  /**
   * Load a sound file into openAL and retrieve the buffer ID. Adds the bufferId to the respective
   * category list.
   *
   * @param category Sound Category
   * @param file file name without path or extension. Must be an .ogg file in /assets/audio/
   * @return bufferId for the loaded sound file (usually not needed, as it is added to the category)
   */
  public static int loadSound(SoundCategory category, String file) {
    // generate buffers and sources
    int buffer = alGenBuffers();

    ArrayList<Integer> catBuffers = buffers.computeIfAbsent(category, k -> new ArrayList<>());
    catBuffers.add(buffer);

    try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
      ShortBuffer pcm = readVorbis("/assets/audio/" + file + ".ogg", 32 * 1024, info);

      // copy to buffer
      alBufferData(
          buffer,
          info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16,
          pcm,
          info.sample_rate());
    }

    return buffer;
  }

  /**
   * Read a vorbis (ogg) file.
   *
   * @param resource path to resource ("/assets/audio/sound.ogg")
   * @param bufferSize usually 32 * 1024
   * @param info Vorbis Stream Info
   * @return buffer of the file to be loaded into openAL
   */
  private static ShortBuffer readVorbis(String resource, int bufferSize, STBVorbisInfo info) {
    ByteBuffer vorbis;

    vorbis = ioResourceToByteBuffer(resource, bufferSize);

    IntBuffer error = BufferUtils.createIntBuffer(1);
    long decoder = stb_vorbis_open_memory(vorbis, error, null);
    if (decoder == NULL) {
      throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
    }

    stb_vorbis_get_info(decoder, info);

    int channels = info.channels();

    ShortBuffer pcm =
        BufferUtils.createShortBuffer(stb_vorbis_stream_length_in_samples(decoder) * channels);

    stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm);
    stb_vorbis_close(decoder);

    return pcm;
  }

  /** Destroy all context and delete buffers from openAL memory. */
  public static void cleanUp() {
    for (ArrayList<Integer> catBuffers : buffers.values()) {
      for (Integer catBuffer : catBuffers) {
        AL10.alDeleteBuffers(catBuffer);
      }
    }

    alcMakeContextCurrent(NULL);
    alcDestroyContext(context);
    alcCloseDevice(device);
  }

  /**
   * Get all bufferIds in a Sound Category.
   *
   * @param category Category to get buffers for
   * @return ArrayList with all bufferIds in that category
   */
  static ArrayList<Integer> getCategoryBuffers(SoundCategory category) {
    return buffers.get(category);
  }

  /**
   * Used to test sound capabilities.
   *
   * @param args -
   */
  public static void main(String[] args) {
    init();
    Source source = new Source();

    // wait
    for (int i = 0; i < 5; i++) {
      source.playRandom(SoundCategory.DIG);
      logger.info("Waiting for sound to complete...");
      while (true) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException ignored) {
          break;
        }
        if (!source.isPlaying()) {
          break;
        }
        logger.info(".");
      }
    }

    source.delete();
    cleanUp();
    System.exit(0);
  }

  /**
   * Check if the audio for the game is enabled.
   *
   * @return true if audio is enabled, false if the game is muted
   */
  public static boolean isEnabled() {
    return enabled;
  }

  /**
   * Enable or disable all audio.
   *
   * @param enabled true to enable audio, false to stop and disable audio
   */
  public static void setEnabled(boolean enabled) {
    AudioMaster.enabled = enabled;
    if (enabled) {
      logger.info("Sound: ON");
    } else {
      logger.info("Sound: OFF");
      // If mute, stop all sounds
      for (ArrayList<Integer> category : buffers.values()) {
        for (Integer bufferId : category) {
          if (alGetSourcei(bufferId, AL_SOURCE_STATE) == AL_PLAYING) {
            alSourceStop(bufferId);
          }
        }
      }
    }
  }

  /**
   * Sound Categories. Sounds are grouped so we can easily play random sounds from a category and to
   * make the code more descriptive.
   */
  public enum SoundCategory {
    DIG,
    EXPLOSION,
    HEART,
    FREEZE,
    DAMAGE,
    GAMEOVER,
    PICK,
    FUSE,
    STEROID,
    BACKGROUND,
    INTRO
  }
}
