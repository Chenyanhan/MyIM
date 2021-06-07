package com.cyh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cyh.entity.ContactInfo;

import java.util.List;

public interface ContactRepository  extends JpaRepository<ContactInfo,Integer>
{

    List<ContactInfo> findAllByFromUserId(String userId);

}
