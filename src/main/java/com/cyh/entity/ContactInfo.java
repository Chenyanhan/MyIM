package com.cyh.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Data
@Entity
@Table(name="contact_info",catalog="",schema="")
public class ContactInfo
{
    // 主键
    @Id
    private int id;
    @Column
    private String fromUserId;
    @Column
    private String toUserId;
}
