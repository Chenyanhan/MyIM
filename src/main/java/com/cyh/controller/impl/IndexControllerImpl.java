package com.cyh.controller.impl;


import com.alibaba.fastjson.JSON;
import com.cyh.common.enums.StatusEnum;
import com.cyh.common.res.BaseResponse;
import com.cyh.common.res.Result;
import com.cyh.controller.IndexController;
import com.cyh.entity.UserInfo;
import com.cyh.entity.viewModel.UserInfoViewModel;
import com.cyh.service.impl.MessageService;
import com.cyh.vo.RegisterInfoReqVO;
import com.cyh.vo.ServerResVO;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;


import protocol.request.GroupMessageRequestPacket;
import protocol.request.LoginRequestPacket;
import protocol.request.MessageRequestPacket;

import java.util.*;


@RestController
@RequestMapping("/")
@Scope("prototype")
class IndexControllerImpl extends BaseController implements IndexController
{

    @Autowired
    private ServerResVO serverResVO;
    @Autowired
    @Qualifier("defaultRedisTemplate")
    private RedisTemplate<String,Object> redisTemplate;
    @Autowired
    private CachingConnectionFactory connectionFactory;

    @Autowired
    @Qualifier("rabbitTemplate")
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private org.springframework.amqp.rabbit.connection.ConnectionFactory factory;

    @RequestMapping(value = "SendVerifyCode", method = RequestMethod.GET)
    @ResponseBody()
    public BaseResponse<RegisterInfoReqVO> sendRegistCode(String mobile) throws Exception
    {
        BaseResponse<RegisterInfoReqVO> res = new BaseResponse<>();
        try
        {

        }
        catch (Exception ex)
        {
            res.setCode(StatusEnum.FAIL.code());
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    @RequestMapping(value = "VerifyCode", method = RequestMethod.GET)
    @ResponseBody()
    public BaseResponse<RegisterInfoReqVO> verifyCode(String mobile,String code) throws Exception
    {
        BaseResponse<RegisterInfoReqVO> res = new BaseResponse<>();
        try
        {

        }
        catch (Exception ex)
        {
            res.setCode(StatusEnum.FAIL.code());
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    @RequestMapping(value = "registerAccount", method = RequestMethod.GET)
    @ResponseBody()
    @Override
    public BaseResponse<RegisterInfoReqVO> registerAccount(RegisterInfoReqVO registerInfoReqVO) throws Exception
    {
        BaseResponse<RegisterInfoReqVO> res = new BaseResponse<>();
        String mobile = registerInfoReqVO.getMobile();

        try
        {
            HashMap<String,RegisterInfoReqVO> userInfoMap = (HashMap<String, RegisterInfoReqVO>) redisTemplate.opsForValue().get("userInfo");
            if (userInfoMap == null)
            {
                userInfoMap = new HashMap<>();
                redisTemplate.opsForValue().set("userInfo",userInfoMap);
            }
            if (!userInfoMap.containsKey(mobile))
            {
                registerInfoReqVO.setUserId(UUID.randomUUID().toString());
                //MD5??????
                String md5Password = DigestUtils.md5DigestAsHex(registerInfoReqVO.getPassword().getBytes());
                registerInfoReqVO.setPassword(md5Password);
                userInfoMap.put(mobile, registerInfoReqVO);
                //??????????????????
                redisTemplate.opsForValue().set("userInfo",userInfoMap);
                //????????????
                res.setResult(registerInfoReqVO);
                res.setCode(StatusEnum.SUCCESS.getCode());
                res.setMessage(StatusEnum.SUCCESS.getMessage());
            }

            else
            {
                res.setCode(StatusEnum.FAIL.getCode());
                res.setMessage(StatusEnum.FAIL.getMessage());
            }
        }
        catch (Exception e)
        {
            res.setCode(StatusEnum.FAIL.getCode());
            res.setMessage(e.getMessage());
        }
        return res;
    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    @ResponseBody()
    @Override
    public BaseResponse<UserInfoViewModel> login(LoginRequestPacket loginRequestPacket) throws Exception
    {
        BaseResponse<UserInfoViewModel> response = new BaseResponse<>();
        String account = loginRequestPacket.getAccount();
        try
        {
            Object userInfo1 = redisTemplate.opsForValue().get("userInfo");

            //????????????
            HashMap<String,RegisterInfoReqVO> userInfoMap = (HashMap<String, RegisterInfoReqVO>) redisTemplate.opsForValue().get("userInfo");
            if (userInfoMap != null)
            {
                boolean hasKey = userInfoMap.containsKey(account);
                if (hasKey)
                {
                    RegisterInfoReqVO userInfo = userInfoMap.get(account);
                    //????????????
                    String md5Password = userInfo.getPassword();
                    //????????????
                    loginRequestPacket.setPassword(DigestUtils.md5DigestAsHex(loginRequestPacket.getPassword().getBytes()));
                    //????????????
                    if (md5Password.equals(loginRequestPacket.getPassword()))
                    {
                        Object session1 = redisTemplate.opsForValue().get("session");

                        HashMap<String, String> session = (HashMap<String, String>) redisTemplate.opsForValue().get("session");
                        if (session == null)
                        {
                            session = new HashMap<>();
                        }
                        //????????????2??????
                        session.put(userInfo.getUserId(),userInfo.getUserName());
                        redisTemplate.opsForValue().set("session",session);
                        //????????????
                        UserInfoViewModel userInfoViewModel = new UserInfoViewModel();
                        UserInfo info = new UserInfo();
                        //??????????????????
                        info.setId(userInfo.getUserId());
                        info.setPhone(userInfo.getMobile());
                        info.setName(userInfo.getUserName());
                        userInfoViewModel.setUser(info);
                        //??????????????????
                        userInfoViewModel.setBind(true);
                        userInfoViewModel.setToken(UUID.randomUUID().toString());
                        userInfoViewModel.setAccount(userInfo.getMobile());
                        //????????????
                        userInfoViewModel.setRemoteInfo(serverResVO);

                        response.setResult(userInfoViewModel);
                        response.setCode(StatusEnum.SUCCESS.getCode());
                        response.setMessage(StatusEnum.SUCCESS.getMessage());
                    }
                    else
                    {
                        response.setCode(StatusEnum.ACCOUNT_NOT_MATCH.getCode());
                        response.setMessage(StatusEnum.ACCOUNT_NOT_MATCH.getMessage());
                    }
                }
                else
                {
                    response.setCode(StatusEnum.ACCOUNT_NOT_MATCH.getCode());
                    response.setMessage(StatusEnum.ACCOUNT_NOT_MATCH.getMessage());
                }
            }
            else
            {
                redisTemplate.opsForValue().set("userInfo",new HashMap<String, RegisterInfoReqVO>());
                response.setCode(StatusEnum.ACCOUNT_NOT_REGISTERED.getCode());
                response.setMessage(StatusEnum.ACCOUNT_NOT_REGISTERED.getMessage());
            }
        }
        catch (Exception e)
        {
            response.setCode(StatusEnum.FAIL.getCode());
            response.setMessage(e.getMessage());
        }

        return response;
    }
    @Override
    @RequestMapping(value = "Logout", method = RequestMethod.GET)
    @ResponseBody()
    public BaseResponse Logout(UserInfo userInfo)
    {
        BaseResponse<ServerResVO> response = new BaseResponse<>();
        Map session = (Map<String,String>)redisTemplate.opsForValue().get("session");
        boolean key = session.containsKey(userInfo);
        if (key)
        {
            session.remove(userInfo.getId());
            redisTemplate.opsForValue().set("session",session);
            response.setMessage(StatusEnum.SUCCESS.getMessage());
            response.setCode(StatusEnum.SUCCESS.getCode());
        }
        return response;
    }



//    @Override
//    @RequestMapping(value = "RadomMatch", method = RequestMethod.GET)
//    @ResponseBody()
//    public BaseResponse RadomMatch(UserInfo userInfo) throws Exception
//    {
//        BaseResponse response = new BaseResponse<>();
//        Connection connection = connectionFactory.createConnection();
//        try
//        {
//            //????????????
//            com.rabbitmq.client.Channel channel = connection.createChannel(false);
//
//            // ????????????
//            // ??????1???queue ????????????
//            // ??????2???durable ???????????????
//            // ??????3???exclusive ????????????
//            // ??????4???autoDelete ??????????????????
//            // ??????5???arguments ????????????????????????
//            String queueName = "Queue_" + userInfo.getId();
//
//            System.out.println(queueName);
//
//            channel.queueDeclare(queueName, true, false, false, null);
//
//            //????????????????????????
//            SimpleMessageListenerContainer container = SpringUtils.getBean(SimpleMessageListenerContainer.class);
//
//            //????????????????????????????????????????????????????????????
//            DirectExchange maleExchange = SpringUtils.getBean("maleExchange", DirectExchange.class);
//
//            //????????????????????????????????????????????????????????????
//            DirectExchange femaleExchange = SpringUtils.getBean("femaleExchange", DirectExchange.class);
//
//             // ????????????
//             // ??????1???queue ????????????
//             // ??????2???exchange ???????????????
//             // ??????3???routingKey ??????key
//             // ??????4???arguments ?????????????????????
//            if (userInfo.getSex() == 1)
//            {
//                //??????????????????????????????????????????????????????
//                channel.queueBind(queueName, maleExchange.getName(), userInfo.getHobby());
//            }
//            else
//            {
//                //??????????????????????????????????????????????????????
//                channel.queueBind(queueName, femaleExchange.getName(), userInfo.getHobby());
//            }
//
//            //????????????
//            container.addQueueNames(queueName);
//
//            //????????????
//            //MessageProperties messageProperties = new MessageProperties();
//            //messageProperties.getHeaders().put("desc", "????????????..");
//            //messageProperties.getHeaders().put("type", "?????????????????????..");
//            //Message message = new Message("hello".getBytes(), messageProperties);
//            //CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
//
//            String messageId = UUID.randomUUID().toString();
//            HashMap<String, Object> map = new HashMap<>();
//            map.put("userId",userInfo.getId());
//            map.put("queueName",queueName);
//            map.put("messageId",messageId);
//            //??????1??????????????????
//            //??????2????????????
//            //??????3????????????
//            if (userInfo.getSex() == 1)
//            {
//                redisTemplate.opsForValue().set("messageId",messageId);
//                //??????????????????????????????????????????????????????
//                rabbitTemplate.convertAndSend(femaleExchange.getName(), userInfo.getHobby(), map);// ????????????
//            }
//            else
//            {
//                redisTemplate.opsForValue().set("messageId",messageId);
//                //??????????????????????????????????????????????????????
//                rabbitTemplate.convertAndSend(maleExchange.getName(), userInfo.getHobby(), map);// ????????????
//            }
//            //??????????????????????????????????????????????????????????????????????????????
//            HashMap<String, Object> hashMap = new HashMap<>();
//            Thread thread = new Thread(() ->
//            {
//                while (done)
//                {
//                    Boolean hasKey = redisTemplate.hasKey(userInfo.getId());
//                    if (hasKey)
//                    {
//                        Object value = redisTemplate.opsForValue().get(userInfo.getId());
//                        hashMap.put(userInfo.getId(), value);
//                        redisTemplate.delete(userInfo.getId());
//                        done = false;
//                        isExcuted = true;
//                    }
//                }
//            });
//            thread.start();
//            Thread.sleep(5000);
//            thread.interrupt();
//            if (isExcuted)
//            {
//                response.setCode(StatusEnum.SUCCESS.getCode());
//                response.setResult(hashMap);
//                response.setMessage(StatusEnum.SUCCESS.getMessage());
//            }
//            else
//            {
//                response.setCode(StatusEnum.FAIL.getCode());
//                response.setMessage(StatusEnum.FAIL.getMessage());
//            }
//
//
//        }
//        catch (AmqpException e)
//        {
//            response.setCode(StatusEnum.FAIL.getCode());
//            response.setMessage(StatusEnum.FAIL.getMessage());
//        }
//        finally
//        {
//            connection.close();
//        }
//
//        return response;
//    }




}

