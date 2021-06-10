package com.cyh.controller.impl;

import com.cyh.common.enums.StatusEnum;
import com.cyh.common.res.BaseResponse;
import com.cyh.common.res.Result;
import com.cyh.repository.UserRepository;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.cyh.controller.UserController;
import com.cyh.entity.UserInfo;
import com.cyh.service.impl.UserService;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserControllerImpl implements UserController
{
    @Resource
    private UserRepository userRepository;

    @Resource
    private UserService userService;

    @RequestMapping(value = "updateInfo", method = RequestMethod.POST)
    public BaseResponse<UserInfo> updateInfo(@RequestBody UserInfo userInfo) throws Exception
    {
        BaseResponse<UserInfo> response = new BaseResponse<>();
        Result<UserInfo> result;
        try
        {
            result = userService.save(userInfo);
            response.setCode(String.valueOf(result.getCode()));
            response.setMessage(result.getMessage());
            response.setResult(result.getData());
        }
        catch (Exception e)
        {
            response.setCode(StatusEnum.FAIL.getCode());
            response.setMessage(e.getMessage());
        }
        return response;
    }


    @RequestMapping(value = "getUserInfo", method = RequestMethod.GET)
    public BaseResponse<UserInfo> getUserInfo(String userId) throws Exception
    {
        BaseResponse<UserInfo> response = new BaseResponse<>();
        try
        {
            UserInfo userInfo = userRepository.findById(userId).get();
            response.setMessage(StatusEnum.SUCCESS.getMessage());
            response.setCode(StatusEnum.SUCCESS.getCode());
            response.setResult(userInfo);
        }
        catch (Exception e)
        {
            response.setCode(StatusEnum.FAIL.getCode());
            response.setMessage(StatusEnum.FAIL.getMessage());
        }
        return response;
    }



    /**
     * 获取联系人
     * @param userId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "contact", method = RequestMethod.POST)
    public BaseResponse<List<UserInfo>> getContact(@RequestBody String userId) throws Exception
    {
        BaseResponse<List<UserInfo>> response = new BaseResponse<>();
        try
        {
            userId = userId.substring(1,userId.length()-1);
            List<UserInfo> userInfos = userService.findContact(userId);
            response.setResult(userInfos);
            response.setMessage(StatusEnum.SUCCESS.getMessage());
            response.setCode(StatusEnum.SUCCESS.getCode());
        }
        catch (Exception e)
        {
            response.setCode(StatusEnum.FAIL.getCode());
            response.setMessage(StatusEnum.FAIL.getMessage());
        }
        return response;
    }

}
