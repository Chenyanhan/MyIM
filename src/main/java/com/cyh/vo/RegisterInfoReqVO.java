package com.cyh.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class RegisterInfoReqVO implements Serializable
{
    @NotNull(message = "用户名不能为空")
    private String userName ;

    private String password ;

    private String userId ;

    private String mobile ;

    @Override
    public String toString() {
        return "RegisterInfoReqVO{" +
                "userName='" + userName + '\'' +
                "} " + super.toString();
    }
}
