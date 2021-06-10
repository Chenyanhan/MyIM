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
                //MD5加密
                String md5Password = DigestUtils.md5DigestAsHex(registerInfoReqVO.getPassword().getBytes());
                registerInfoReqVO.setPassword(md5Password);
                userInfoMap.put(mobile, registerInfoReqVO);
                //保存到数据库
                redisTemplate.opsForValue().set("userInfo",userInfoMap);
                //设置响应
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

            //是否注册
            HashMap<String,RegisterInfoReqVO> userInfoMap = (HashMap<String, RegisterInfoReqVO>) redisTemplate.opsForValue().get("userInfo");
            if (userInfoMap != null)
            {
                boolean hasKey = userInfoMap.containsKey(account);
                if (hasKey)
                {
                    RegisterInfoReqVO userInfo = userInfoMap.get(account);
                    //获取密码
                    String md5Password = userInfo.getPassword();
                    //密码加密
                    loginRequestPacket.setPassword(DigestUtils.md5DigestAsHex(loginRequestPacket.getPassword().getBytes()));
                    //加密验证
                    if (md5Password.equals(loginRequestPacket.getPassword()))
                    {
                        Object session1 = redisTemplate.opsForValue().get("session");

                        HashMap<String, String> session = (HashMap<String, String>) redisTemplate.opsForValue().get("session");
                        if (session == null)
                        {
                            session = new HashMap<>();
                        }
                        //缓存时间2小时
                        session.put(userInfo.getUserId(),userInfo.getUserName());
                        redisTemplate.opsForValue().set("session",session);
                        //设置响应
                        UserInfoViewModel userInfoViewModel = new UserInfoViewModel();
                        UserInfo info = new UserInfo();
                        //设置用户信息
                        info.setId(userInfo.getUserId());
                        info.setPhone(userInfo.getMobile());
                        info.setName(userInfo.getUserName());
                        userInfoViewModel.setUser(info);
                        //设置设备绑定
                        userInfoViewModel.setBind(true);
                        userInfoViewModel.setToken(UUID.randomUUID().toString());
                        userInfoViewModel.setAccount(userInfo.getMobile());
                        //远程主机
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
//            //创建信道
//            com.rabbitmq.client.Channel channel = connection.createChannel(false);
//
//            // 队列声明
//            // 参数1：queue 队列名称
//            // 参数2：durable 是否持久化
//            // 参数3：exclusive 是否排外
//            // 参数4：autoDelete 是否自动删除
//            // 参数5：arguments 什么时候自动删除
//            String queueName = "Queue_" + userInfo.getId();
//
//            System.out.println(queueName);
//
//            channel.queueDeclare(queueName, true, false, false, null);
//
//            //获取监听队列容器
//            SimpleMessageListenerContainer container = SpringUtils.getBean(SimpleMessageListenerContainer.class);
//
//            //从容器中获取交换器，被动接受消息的交换器
//            DirectExchange maleExchange = SpringUtils.getBean("maleExchange", DirectExchange.class);
//
//            //从容器中获取交换器，主动推送消息的交换器
//            DirectExchange femaleExchange = SpringUtils.getBean("femaleExchange", DirectExchange.class);
//
//             // 队列绑定
//             // 参数1：queue 队列名称
//             // 参数2：exchange 交换器名称
//             // 参数3：routingKey 路由key
//             // 参数4：arguments 其它的一些参数
//            if (userInfo.getSex() == 1)
//            {
//                //如果是男的就将队列绑定到男性的交换器
//                channel.queueBind(queueName, maleExchange.getName(), userInfo.getHobby());
//            }
//            else
//            {
//                //如果是女的就将队列绑定到女性的交换器
//                channel.queueBind(queueName, femaleExchange.getName(), userInfo.getHobby());
//            }
//
//            //监听队列
//            container.addQueueNames(queueName);
//
//            //创建消息
//            //MessageProperties messageProperties = new MessageProperties();
//            //messageProperties.getHeaders().put("desc", "信息描述..");
//            //messageProperties.getHeaders().put("type", "自定义消息类型..");
//            //Message message = new Message("hello".getBytes(), messageProperties);
//            //CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
//
//            String messageId = UUID.randomUUID().toString();
//            HashMap<String, Object> map = new HashMap<>();
//            map.put("userId",userInfo.getId());
//            map.put("queueName",queueName);
//            map.put("messageId",messageId);
//            //参数1：交换器名称
//            //参数2：路由键
//            //参数3：消息体
//            if (userInfo.getSex() == 1)
//            {
//                redisTemplate.opsForValue().set("messageId",messageId);
//                //如果是男的就将消息推送到女性的交换器
//                rabbitTemplate.convertAndSend(femaleExchange.getName(), userInfo.getHobby(), map);// 发送消息
//            }
//            else
//            {
//                redisTemplate.opsForValue().set("messageId",messageId);
//                //如果是女的就将消息推送到男性的交换器
//                rabbitTemplate.convertAndSend(maleExchange.getName(), userInfo.getHobby(), map);// 发送消息
//            }
//            //由于主线程去执行监听器的方法所以以下程序由子线程执行
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

