package com.example.kf.repository;

import com.example.kf.domain.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message,Integer> {

    @Query(value = "select alert from message where from_user = ?1",nativeQuery = true)
    List<String> findAllByFromUser(String fromUser);

    @Query(value = "select * from message where from_user = ?1",nativeQuery = true)
    Page<Message> findMessageByFromUser(String fromUser, Pageable pageable);
}
