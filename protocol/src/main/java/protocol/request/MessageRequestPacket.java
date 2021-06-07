package protocol.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import protocol.Packet;


import static protocol.command.Command.MESSAGE_REQUEST;


@Data
@NoArgsConstructor
public class MessageRequestPacket extends Packet
{
    private String receiverId;
    private String senderId;
    private String content;

    public MessageRequestPacket(String receiverId, String content) {
        this.receiverId = receiverId;
        this.content = content;
    }

    @Override
    public Byte getCommand() {
        return MESSAGE_REQUEST;
    }
}
