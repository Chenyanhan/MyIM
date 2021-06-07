package com.cyh.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CacheMsg implements Serializable
{
    private String id;
    private String content;
    private String attach;
    private int type;
    private Date createAt;
    private String groupId;
    private String senderId;
    private String receiverId;
}
