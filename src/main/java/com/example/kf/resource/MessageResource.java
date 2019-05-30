package com.example.kf.resource;

import com.example.kf.domain.Message;
import com.example.kf.domain.User;
import com.example.kf.domain.common.MyJson;
import com.example.kf.service.MessageService;
import com.example.kf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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

    /**
     * 根据客服名获得聊天内容
     * @return
     */
    @RequestMapping("/message/getEmployeeMessage")
    @ResponseBody
    public Map<String,Integer> getEmployeeMessage(){
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
    public MyJson<Message> findMessageByFromUser(@PathVariable String fromUser) {
        System.out.println(fromUser);
        List<Message> message = messageService.findMessageByFromUser(fromUser);
        int size = message.size();
        MyJson<Message> myJson = new MyJson();
        myJson.setCode(0);
        myJson.setMsg("");
        myJson.setCount(size);
        myJson.setData(message);
        System.out.println(myJson.toString());
        return myJson;
    }
}
