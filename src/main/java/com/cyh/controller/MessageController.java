package com.cyh.controller;

import com.cyh.common.res.BaseResponse;
import com.cyh.entity.UserInfo;
import org.springframework.web.bind.annotation.RequestBody;
import protocol.request.GroupMessageRequestPacket;
import protocol.request.MessageRequestPacket;

public interface MessageController
{
    BaseResponse<MessageRequestPacket> msgPush(@RequestBody MessageRequestPacket message);
    BaseResponse sendMsgByGroup(GroupMessageRequestPacket groupMessage);
    BaseResponse radommatch(UserInfo userInfo);
    
}
