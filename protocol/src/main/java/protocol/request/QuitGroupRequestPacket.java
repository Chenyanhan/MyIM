package protocol.request;

import lombok.Data;
import protocol.Packet;


import static protocol.command.Command.QUIT_GROUP_REQUEST;


@Data
public class QuitGroupRequestPacket extends Packet
{
    private String groupId;
    private String userId;

    @Override
    public Byte getCommand() {

        return QUIT_GROUP_REQUEST;
    }
}
