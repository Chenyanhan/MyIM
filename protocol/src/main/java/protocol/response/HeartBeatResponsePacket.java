package protocol.response;

import protocol.Packet;

import java.io.Serializable;

import static protocol.command.Command.HEARTBEAT_RESPONSE;

public class HeartBeatResponsePacket extends Packet implements Serializable
{
    @Override
    public Byte getCommand()
    {
        return HEARTBEAT_RESPONSE;
    }
}
