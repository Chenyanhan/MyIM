package com.cyh.entity.viewModel;

import com.cyh.vo.ServerResVO;
import lombok.Data;
import com.cyh.entity.UserInfo;

@Data
public class UserInfoViewModel
{
    private UserInfo user;
    // 当前登录的账号
    private String account;
    // 当前登录成功后获取的Token,
    // 可以通过Token获取用户的所有信息
    private String token;
    // 标示是否已经绑定到了设备PushId
    private boolean isBind;

    private ServerResVO remoteInfo;
}
