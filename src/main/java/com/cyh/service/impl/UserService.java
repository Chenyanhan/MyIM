package com.cyh.service.impl;

import com.cyh.common.res.Result;
import com.cyh.repository.ContactRepository;
import com.cyh.repository.UserRepository;
import com.cyh.util.CopyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cyh.entity.ContactInfo;
import com.cyh.entity.UserInfo;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserService
{
    @Resource
    private UserRepository userRepository;
    @Resource
    private ContactRepository contactRepository;
    public Result<UserInfo> save(UserInfo userInfo)
    {
        Result<UserInfo> result = new Result<>();
        try
        {
            boolean exists = userRepository.existsById(userInfo.getId());
            UserInfo info;
            if (!exists)
            {
                userInfo.setId(UUID.randomUUID().toString());
                info = userInfo;
            }
            else
            {
                info = userRepository.findById(userInfo.getId()).get();
                CopyUtils.copyProperties(userInfo,info);
            }
            userRepository.save(info);
            result.setCode(1);
            result.setMessage("获取成功");
            result.setData(info);
        }
        catch (Exception e)
        {
            throw e;
        }
        return result;
    }

    public List<UserInfo> findContact(String userId)
    {
        List<UserInfo> userList = null;
        try
        {
            List<ContactInfo> contactInfos = contactRepository.findAllByFromUserId(userId);
            userList = new ArrayList<>();
            if (contactInfos.size() > 0)
            {
                for (ContactInfo info:contactInfos
                     )
                {
                    String toUserId = info.getToUserId();
                    UserInfo userInfo = userRepository.findById(toUserId).get();
                    userList.add(userInfo);
                }

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return userList;
    }



    public UserInfo findUser(String userId)
    {
        UserInfo user = null;
        try
        {
            user = userRepository.findById(userId).get();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return user;
    }
}
