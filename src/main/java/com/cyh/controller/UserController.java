package com.cyh.controller;


import com.cyh.common.res.BaseResponse;
import com.cyh.entity.UserInfo;


import java.util.List;

public interface UserController
{
    BaseResponse<UserInfo> updateInfo(UserInfo userInfo) throws Exception;

    BaseResponse<List<UserInfo>> getContact(String userId) throws Exception;
}
