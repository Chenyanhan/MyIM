package com.cyh.vo;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Data
@Component
//@ConfigurationProperties(prefix = "serverinfo")
public
class ServerResVO
{
    @Value("${remote.host}")
    private String host;
    @Value("${remote.port}")
    private String port;
}