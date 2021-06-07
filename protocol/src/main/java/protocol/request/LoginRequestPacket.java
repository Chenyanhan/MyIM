package protocol.request;

import lombok.Data;
import protocol.Packet;


import static protocol.command.Command.LOGIN_REQUEST;


@Data
public class LoginRequestPacket extends Packet
{
    private String account;
    private String password;
    private String pushId;


    private String userName;
    private String mobile;
    @Override
    public Byte getCommand() {

        return LOGIN_REQUEST;
    }
}
