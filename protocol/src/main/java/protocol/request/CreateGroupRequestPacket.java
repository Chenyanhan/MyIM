package protocol.request;

import lombok.Data;
import protocol.Packet;
import java.util.List;
import static protocol.command.Command.CREATE_GROUP;


@Data
public class CreateGroupRequestPacket extends Packet
{
    private List<String> userIdList;

    @Override
    public Byte getCommand()
    {
        return CREATE_GROUP;
    }
}
