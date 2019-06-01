package com.example.kf.service;

import com.example.kf.domain.Message;
import com.example.kf.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    /**
     * 保存聊天内容
     * @param message
     * @return
     */
    public Message save(Message message){
        return messageRepository.save(message);
    }

    /**
     * 获得所有聊天内容
     * @return
     */
    public List<Message> getAll(){
        return messageRepository.findAll();
    }

    /**
     * 根据客服名获得聊天内容
     * @param fromUser
     * @return
     */
    public List<String> findAllByFromUser(String fromUser){
        return messageRepository.findAllByFromUser(fromUser);
    }

    /**
     * 根据客服名获得message
     * @param fromUser
     * @return
     */
    public Page<Message> findMessageByFromUser(String fromUser, Pageable pageable) {
        return messageRepository.findMessageByFromUser(fromUser,pageable);
    }
}
