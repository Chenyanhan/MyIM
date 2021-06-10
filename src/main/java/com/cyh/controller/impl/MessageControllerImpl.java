package com.cyh.controller.impl;

import com.alibaba.fastjson.JSON;
import com.cyh.common.enums.StatusEnum;
import com.cyh.common.res.BaseResponse;
import com.cyh.common.res.Result;
import com.cyh.controller.MessageController;
import com.cyh.entity.UserInfo;
import com.cyh.service.impl.MessageService;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import protocol.request.GroupMessageRequestPacket;
import protocol.request.MessageRequestPacket;

/**
 * @author C
 */
public class MessageControllerImpl implements MessageController
{
    @Autowired
    private MessageService messageService;

    /**
     * 单聊
     * @param message
     * @return
     */
    @Override
    @RequestMapping(value = "msg", method = RequestMethod.POST)
    @ResponseBody()
    public BaseResponse<MessageRequestPacket> msgPush(@RequestBody MessageRequestPacket message)
    {
        BaseResponse<MessageRequestPacket> response = new BaseResponse<>();
        try
        {
            Result result = messageService.MessagePush(message);
            if (result != null && result.getCode() == 1){
                response.setCode(StatusEnum.SUCCESS.getCode());
                response.setCode(StatusEnum.SUCCESS.getMessage());
            }
            else {
                response.setCode(StatusEnum.FAIL.getCode());
                response.setMessage(StatusEnum.FAIL.getMessage());
            }
            return response;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("发送成功");
        return response;
    }

    /**
     * 群聊
     * @param groupMessage
     * @return
     */
    @RequestMapping(value = "sendMsgByGroup", method = RequestMethod.GET)
    @ResponseBody()
    @Override
    public BaseResponse sendMsgByGroup(GroupMessageRequestPacket groupMessage)
    {
        messageService.sendMsgByGroup(groupMessage);
        return new BaseResponse();

    }

    /**
     * 随机匹配
     * @param userInfo
     * @return
     */
    @Override
    @RequestMapping(value = "RadomMatchV1", method = RequestMethod.GET)
    @ResponseBody()
    public BaseResponse radommatch(UserInfo userInfo){
        BaseResponse response = new BaseResponse<>();
        // 实例化消息生产者Producer
        DefaultMQProducer producer = new DefaultMQProducer("producer");
        try
        {
            // 设置NameServer的地址
//            producer.setNamesrvAddr("localhost:9876");
            producer.setNamesrvAddr("47.95.217.141:9876");
            // 启动Producer实例
            producer.start();
            userInfo.setExpireTime(System.currentTimeMillis() + 15 * 1000);
            org.apache.rocketmq.common.message.Message msg = new Message("Topic_Test_4" ,
                    userInfo.getHobby(),
                    JSON.toJSONBytes(userInfo)
            );

            // 发送消息到一个Broker
            SendResult sendResult = producer.send(msg);
            // 通过sendResult返回消息是否成功送达
            System.out.printf("%s%n", sendResult);
            // 如果不再发送消息，关闭Producer实例。
            producer.shutdown();
        }
        catch (Exception e)
        {
            producer.shutdown();
            response.setCode(StatusEnum.FAIL.getCode());
            response.setMessage(e.getMessage());
        }
        return response;
    }
}
