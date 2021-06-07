package protocol.command;

public interface Command {

    Byte LOGIN_REQUEST = 1;
    Byte LOGIN_RESPONSE = 2;

    Byte MESSAGE_REQUEST = 3;
    Byte MESSAGE_RESPONSE = 4;

    Byte CREATE_GROUP = 5;

    Byte BIND_CHANNEL_REQ = 6;
    Byte BIND_CHANNEL_RES = 7;

    Byte CREATE_GROUP_RESPONSE = 8;

    Byte JOIN_GROUP_REQ = 9;
    Byte JOIN_GROUP_RESPONSE = 10;

    Byte QUIT_GROUP_REQUEST = 11;
    Byte QUIT_GROUP_RESPONSE = 12;

    Byte GROUP_MESSAGE_REQUEST = 13;
    Byte GROUP_MESSAGE_RESPONSE = 14;

    Byte HEARTBEAT_REQUEST = 15;
    Byte HEARTBEAT_RESPONSE = 16;
}
