package protocol.request;

import lombok.Data;
import protocol.Packet;


import static protocol.command.Command.JOIN_GROUP_REQ;


@Data
public class JoinGroupRequestPacket extends Packet
{
    private String groupId;
    private String userId;
    @Override
    public Byte getCommand()
    {
        return JOIN_GROUP_REQ;
    }
}
