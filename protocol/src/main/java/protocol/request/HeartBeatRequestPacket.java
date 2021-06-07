package protocol.request;


import protocol.Packet;


import static protocol.command.Command.HEARTBEAT_REQUEST;


public class HeartBeatRequestPacket extends Packet
{
    @Override
    public Byte getCommand()
    {
        return HEARTBEAT_REQUEST;
    }
}
