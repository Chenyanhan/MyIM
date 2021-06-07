package protocol.request;


import lombok.Data;
import protocol.Packet;

import static protocol.command.Command.BIND_CHANNEL_REQ;


@Data
public class BindChannelReqPacket extends Packet
{
    private String userId;
    private String userName;
    @Override
    public Byte getCommand()
    {
        return BIND_CHANNEL_REQ;
    }
}
