package com.cyh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cyh.entity.UserInfo;


public interface UserRepository extends JpaRepository<UserInfo,String>
{

}
