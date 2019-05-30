package com.example.kf.service;

import com.example.kf.domain.User;
import com.example.kf.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findUserByUsername(String username){
        User user = userRepository.findUserByUsername(username);
        return user;
    }

    public User findUserById(int id){
        User user = userRepository.getOne(id);
        return user;
    }

    public User saveUser(User user){
        User user1 = userRepository.save(user);
        return user1;
    }

    public List<User> findUser(){
        List<User> user = userRepository.findUser();
        return user;
    }

    public User addUser(User user){
        User user1 = userRepository.save(user);
        return user1;

    }

    public void updateUser(User user) {
        User user1 = userRepository.getOne(user.getId());
        user1.setRole(user.getRole());
        userRepository.save(user1);
    }

    public void deleteUser(int userId) {
        userRepository.deleteById(userId);
    }

    public String checkUserName(String username){
        String result;
        User user = userRepository.findUserByUsername(username);
        if(user == null){
            result = "200";
        }else{
            result = "201";
        }
        return result;
    }

    /**
     * 查询角色是客服的user
     * @return
     */
    public List<User> findUserByRole(){
        return userRepository.findUserByRole();
    }

    public void updatePic(String filename, int id) {
        userRepository.updatePic(filename,id);
    }
}
