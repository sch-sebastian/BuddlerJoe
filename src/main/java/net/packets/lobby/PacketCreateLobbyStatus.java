package net.packets.lobby;

import game.Game;
import game.stages.LobbyCreation;
import net.packets.Packet;

/**
 * Packet that gets send from the Server to the Client, to inform the him over the result of the
 * lobby-creation attempt. Packet-Code: LOBCS
 *
 * @author Sebastian Schlachter
 */
public class PacketCreateLobbyStatus extends Packet {
  private String status;

  /**
   * Constructor that is used by the Server to build the Packet.
   *
   * @param clientId ClientId of the receiver.
   * @param data A String that contains Information about the lobby-creation attempt. ("OK" or in
   *     the case of an error, a suitable errormessage) {@link PacketCreateLobbyStatus#status} gets
   *     set to equal data.
   */
  public PacketCreateLobbyStatus(int clientId, String data) {
    // Server builds
    super(Packet.PacketTypes.CREATE_LOBBY_STATUS);
    setData(data);
    setClientId(clientId);
    status = getData();
    validate();
  }

  /**
   * Constructor that is used by the Client to build this packet, if he receives "LOBCS".
   *
   * @param data A String that contains Information about the lobby-creation attempt. ("OK" or in
   *     the case of an error, a suitable errormessage) {@link PacketCreateLobbyStatus#status} gets
   *     set to equal data.
   */
  public PacketCreateLobbyStatus(String data) {
    // client builds
    super(Packet.PacketTypes.CREATE_LOBBY_STATUS);
    setData(data);
    status = getData();
    validate();
  }

  /**
   * Validation method to check the data that has, or will be send in this packet. Checks if {@link
   * PacketCreateLobbyStatus#status} is not null. Checks that {@link PacketCreateLobbyStatus#status}
   * consists of extendet ASCII Characters. In the case of an error it gets added with {@link
   * Packet#addError(String)}.
   */
  @Override
  public void validate() {
    if (status != null) {
      isExtendedAscii(status);
    } else {
      addError("No Status found.");
    }
  }

  /**
   * Method that lets the Client react to the receiving of this packet. Check for errors in
   * validate.(prints errormessages if there are some) If {@link PacketCreateLobbyStatus#status}
   * starts with "OK", the message "Lobby-Creation Successful" gets printed. Else in the case of an
   * error on the serverside the error message gets printed (on console and GUI).
   */
  @Override
  public synchronized void processData() {
    Game.setLobbyCreated(true); // Duplicate Lobby is okay
    if (hasErrors()) {
      String errMsg = createErrorMessage();
      LobbyCreation.setMsg(errMsg);
    } else if (status.startsWith("OK")) {
      if (Game.getActiveStages().contains(Game.Stage.LOBBYCREATION)) {
        LobbyCreation.setRemoveAtEndOfFrame(true);
        Game.addActiveStage(Game.Stage.CHOOSELOBBY);
        Game.removeActiveStage(Game.Stage.LOBBYCREATION);
      }
      LobbyCreation.setRemoveAtEndOfFrame(true);
    } else {
      LobbyCreation.setMsg(status);
    }
  }
}
