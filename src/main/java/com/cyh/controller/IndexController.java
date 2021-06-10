package com.cyh.controller;

import com.cyh.common.res.BaseResponse;
import com.cyh.vo.RegisterInfoReqVO;


import protocol.request.GroupMessageRequestPacket;
import protocol.request.LoginRequestPacket;
import protocol.request.MessageRequestPacket;
import com.cyh.entity.UserInfo;
import com.cyh.entity.viewModel.UserInfoViewModel;

public interface IndexController
{
    /**
     * Register account
     *
     * @param registerInfoReqVO
     * @return
     * @throws Exception
     */
    BaseResponse<RegisterInfoReqVO> registerAccount(RegisterInfoReqVO registerInfoReqVO) throws Exception;

    BaseResponse<UserInfoViewModel> login(LoginRequestPacket loginRequestPacket) throws Exception;

    BaseResponse Logout(UserInfo userInfo)throws Exception;
}
