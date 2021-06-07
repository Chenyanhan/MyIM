package protocol.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import protocol.Packet;


import java.io.Serializable;

import static protocol.command.Command.MESSAGE_RESPONSE;

@Data
public class MessageResponsePacket extends Packet implements Serializable
{

    private String fromUserId;

    private String fromUserName;

    private String message;

    public MessageResponsePacket()
    {
    }

    public MessageResponsePacket(String fromUserId, String fromUserName, String message)
    {
        this.fromUserId = fromUserId;
        this.fromUserName = fromUserName;
        this.message = message;
    }

    @Override
    @JSONField(deserialize = false, serialize = false)
    public Byte getCommand() {

        return MESSAGE_RESPONSE;
    }
}
