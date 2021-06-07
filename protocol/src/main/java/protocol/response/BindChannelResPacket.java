package protocol.response;

import lombok.Data;
import protocol.Packet;

import static protocol.command.Command.BIND_CHANNEL_RES;

@Data
public class BindChannelResPacket extends Packet
{
    private String userId;

    private String userName;

    private boolean success;

    private String reason;
    @Override
    public Byte getCommand()
    {
        return BIND_CHANNEL_RES;
    }
}
