package com.cyh.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity()
@Table(name="user_info",catalog="",schema="")
public class UserInfo
{
    // 主键
    @Id
    private String id;
    @Column
    private String name;
    @Column
    private String phone;
    @Column
    private String portrait;
    @Column(name = "description")
    private String desc;
    @Column
    private Integer sex = 0;

    // 我对某人的备注信息，也应该写入到数据库中
    @Column
    private String alias;

    // 用户关注人的数量
    @Column
    private Integer follows;

    // 用户粉丝的数量
    @Column
    private Integer following;

    // 我与当前User的关系状态，是否已经关注了这个人
    @Column
    private Boolean isFollow;

    // 时间字段
    @Column
    private Date modifyAt;


    // 时间字段
    @Column
    private String hobby;

    private Long expireTime;
}
