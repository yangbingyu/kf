package com.example.kf.resource;

import com.example.kf.domain.Message;
import com.example.kf.domain.User;
import com.example.kf.domain.common.MyJson;
import com.example.kf.service.MessageService;
import com.example.kf.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class MessageResource {

    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;

    Logger logger = LoggerFactory.getLogger(MessageResource.class);

    /**
     * 根据客服名获得聊天内容
     * @return
     */
    @RequestMapping("/message/getEmployeeMessage")
    @ResponseBody
    public Map<String,Integer> getEmployeeMessage(){
        logger.debug("根据客服名获得聊天内容");
        List<Message> messages = messageService.getAll();
        List<User> users = userService.findUserByRole();
        List<String> usernames = new ArrayList<>();
        Map<String,List<String>> map = new ConcurrentHashMap<>();
        Map<String,Integer> badMap = new ConcurrentHashMap<>();
        users.stream().forEach(u -> usernames.add(u.getUsername()));
        for (Message message : messages){
            if (usernames.contains(message.getFromUser())){
                List<String> messageList = messageService.findAllByFromUser(message.getFromUser());
                map.put(message.getFromUser(),messageList);
            }
        }
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            List<String> value = entry.getValue();
            logger.debug("如果聊天内容里包括禁忌词超过三次，则禁用该客服");
            Integer badNum = 0;
            for(String s : value){
                if(s.contains("我没空") || s.contains("你烦不烦") || s.contains("我就是这个态度")){
                    badNum += 1;
                }
            }
            if(badNum >= 3){
                User user = userService.findUserByUsername(entry.getKey());
                user.setType(0);
                userService.saveUser(user);
            }
            badMap.put(entry.getKey(),badNum);
        }
        return badMap;
    }

    /**
     * 根据客服名获得message
     * @param fromUser
     * @return
     */
    @RequestMapping("/message/findMessageByFromUser/{fromUser}")
    @ResponseBody
    public MyJson<Message> findMessageByFromUser(@PathVariable String fromUser, @RequestParam int page, @RequestParam int limit) {
        logger.debug("根据客服名获得聊天内容");
        Pageable pageable = new PageRequest(page-1, limit);
        Page<Message> message = messageService.findMessageByFromUser(fromUser,pageable);
        long size = message.getTotalElements();
        MyJson<Message> myJson = new MyJson();
        myJson.setCode(0);
        myJson.setMsg("");
        myJson.setCount((int) size);
        myJson.setData(message.getContent());
        System.out.println(myJson.toString());
        return myJson;
    }
}
