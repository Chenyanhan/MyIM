package protocol.response;

import lombok.Data;
import protocol.Packet;
import static protocol.command.Command.GROUP_MESSAGE_RESPONSE;


@Data
public class GroupMessageResponsePacket extends Packet
{
    private String fromGroupId;

    private String fromUser;

    private String message;

    @Override
    public Byte getCommand() {

        return GROUP_MESSAGE_RESPONSE;
    }

}
