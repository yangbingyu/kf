package com.example.kf.repository;

import com.example.kf.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Integer>{

    @Query(value = "select * from User where username = ?1",nativeQuery = true)
    User findUserByUsername(String username);

    @Query(value = "select * from user where role = '客服' or role = '系统管理员'",nativeQuery = true)
    Page<User> findUser(Pageable pageable);

    @Query(value = "select * from User where role = '客服'",nativeQuery = true)
    List<User> findUserByRole();

    @Query(value = "update user set path=?1 where id  = ?2",nativeQuery = true)
    @Modifying
    void updatePic(String filename, int id);
}
