package entities;

import engine.models.TexturedModel;
import engine.render.Loader;
import gui.DirectionalUsername;
import net.packets.Packet;
import org.joml.Vector3f;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.UUID;

public class NetPlayer extends Entity {

    private InetAddress ipAddress;
    private int port;
    private String username;
    private String model;
    private String texture;
    private float modelSize;

    private DirectionalUsername directionalUsername;

//    private UUID uuid = UUID.randomUUID();

    public NetPlayer(TexturedModel playerModel, Vector3f position, float rotX, float rotY, float rotZ, float scale,
                     InetAddress inetAddress, int port, String username, String strModel, String texture, float modelSize) {
        super(playerModel, position, rotX, rotY, rotZ, modelSize);

        this.ipAddress = inetAddress;
        this.port = port;
        this.username = username;
        this.model = strModel;
        this.texture = texture;
        this.modelSize = modelSize;

    }

//    public void tick() {
//        super.tick();
//    }

    public boolean ownsDatagram(DatagramPacket datagramPacket) {
        return (datagramPacket.getAddress() == ipAddress && datagramPacket.getPort() == port);
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object obj) {
        if (!obj.getClass().isInstance(this))
            return false;
        return (this.ipAddress == ((NetPlayer) obj).getIpAddress() && this.port == ((NetPlayer) obj).getPort());
    }

    public String getModelStr() {
        return model;
    }

    public String getTextureStr() {
        return texture;
    }

    public float getModelSize() {
        return modelSize;
    }

    public void loadDirectionalUsername(Loader loader) {
        this.directionalUsername = new DirectionalUsername(this, loader);
    }

    public DirectionalUsername getDirectionalUsername() {
        return directionalUsername;
    }
}