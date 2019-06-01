package com.example.kf.resource;

import com.example.kf.domain.User;
import com.example.kf.domain.UserDTO;
import com.example.kf.domain.common.MyJson;
import com.example.kf.domain.enums.Role;
import com.example.kf.service.TakeTaxiService;
import com.example.kf.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class UserResource {

    @Autowired
    private UserService userService;
    @Autowired
    private TakeTaxiService takeTaxiService;

    Logger logger = LoggerFactory.getLogger(UserResource.class);

    @RequestMapping({"/","/index"})
    public String index(){
        return "/login";
    }

    @RequestMapping("/permission")
    public String permission(){return "admin/permission";}

    @RequestMapping("/satisfaction")
    public String satisfaction(){return "admin/satisfactionManagement";}

    @RequestMapping("/employeeIndex")
    public String employeeIndex(){return "employee/e_index";}

    @RequestMapping("/adminIndex")
    public String adminIndex(){return "admin/a_index";}

    @RequestMapping("/customerIndex")
    public String customerIndex(){return "customer/c_index";}

    @RequestMapping("/driverIndex")
    public String driberIndex(){return "driver/d_index";}

    /**
     * 登录
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/login")
    public String login(HttpServletRequest request, Model model){
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        User user = userService.findUserByUsername(username);
        model.addAttribute("user",user);
        System.out.println(user.toString());
        String city = null;
        try {
            city = takeTaxiService.getCity();
        } catch (IOException e) {
            e.printStackTrace();
        }
        request.getSession().setAttribute("city",city);
        request.getSession().setAttribute("userId",user.getId());
        request.getSession().setAttribute("username",user.getUsername());
        request.getSession().setAttribute("password",user.getPassword());
        request.getSession().setAttribute("role",user.getRole());
        request.getSession().setAttribute("email",user.getEmail());
        request.getSession().setAttribute("tel",user.getTel());
        if(password.equals(user.getPassword()) && user.getRole().equals(Role.用户.toString()) && user.getType() == 1){
            logger.debug("用户"+username+"登录系统了");
            return "/customer/c_index";
        }else if(password.equals(user.getPassword()) && user.getRole().equals(Role.客服.toString()) && user.getType() == 1){
            logger.debug("客服"+username+"登录系统了");
            return "/employee/e_index";
        }else if(password.equals(user.getPassword()) && user.getRole().equals(Role.系统管理员.toString()) && user.getType() == 1){
            logger.debug("系统管理员"+username+"登录系统了");
            return "/admin/a_index";
        }else if(password.equals(user.getPassword()) && user.getRole().equals(Role.司机.toString()) && user.getType() == 1){
            logger.debug("司机"+username+"登录系统了");
            return "/driver/d_index";
        }else if(!password.equals(user.getPassword())){
//            model.addAttribute("msg","用户名或密码错误！");
            logger.error("用户名或密码错误");
            request.getSession().setAttribute("msg","用户名或密码错误");
            return "/login";
        }else {
//            model.addAttribute("msg","该用户已被禁用！");
            logger.error("该用户已被禁用");
            request.getSession().setAttribute("msg","该用户已被禁用");
            return "/login";
        }
    }

    @RequestMapping("/register")
    public String register(){
        return "/register";
    }

    /**
     * 注册
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/registerUser")
    public String registerUser(HttpServletRequest request, Model model){
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String tel = request.getParameter("tel");
        String role = request.getParameter("role");
        String rePassword = request.getParameter("rePassword");
        System.out.println(username+password+rePassword+email+tel+role);
        User user = userService.findUserByUsername(username);
        System.out.println(user);
        if(password.equals(rePassword) && user == null){
            User user1 = new User();
            user1.setUsername(username);
            user1.setPassword(password);
            user1.setEmail(email);
            user1.setTel(tel);
            user1.setRole(role);
            user1.setType(1);
            user1.setpath("img/5db11ff4gw1e77d3nqrv8j203b03cweg.jpg");
            userService.saveUser(user1);
            logger.debug(role+username+"注册成功");
            return "/login";
        }else {
            if(!password.equals(rePassword)){
                logger.warn("密码不一致");
                model.addAttribute("msg","密码不一致");
            }
            if(user != null){
                logger.warn("用户名已被占用");
                model.addAttribute("msg","用户名已被占用");
            }
            return "/register";
        }
    }

    /**
     * 权限管理 查询用户
     * @param page
     * @param limit
     * @return
     */
    @RequestMapping(value="/findUser")
    @ResponseBody
    public MyJson<UserDTO> findUser(@RequestParam int page,@RequestParam int limit) {
        Pageable pageable = new PageRequest(page-1,limit);
        Page<User> list = userService.findUser(pageable);
        logger.debug("客服及系统管理员查询成功");
        List<UserDTO> userDTOS = new ArrayList<>();
        for (User user : list){
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setPassword(user.getPassword());
            userDTO.setEmail(user.getEmail());
            userDTO.setTel(user.getTel());
            userDTO.setRole(user.getRole());
            userDTO.setpath(user.getpath());
            if(user.getType()==1){
                userDTO.setType("已启用");
            }
            else {
                userDTO.setType("已禁用");
            }
            userDTOS.add(userDTO);
        }
        long size = list.getTotalElements();
        MyJson<UserDTO> myJson = new MyJson();
        myJson.setCode(0);
        myJson.setMsg("");
        myJson.setCount((int) size);
        myJson.setData(userDTOS);
        return myJson;
    }

    /**
     * 添加系统管理员或客服
     * @param user
     * @return
     */
    @RequestMapping("/addUser")
    public String addUser(User user){
        user.setType(1);
        user.setpath("img/5db11ff4gw1e77d3nqrv8j203b03cweg.jpg");
        String username = user.getUsername();
        User user2 = userService.findUserByUsername(username);
        if(user2 == null){
            userService.saveUser(user);
            logger.debug(user.getRole()+username+"添加成功");
            return "forward:/permission";
        }else {
            return "";
        }
    }

    /**
     * 修改客服或系统管理员角色
     * @param user
     * @return
     */
    @RequestMapping("/updateUser")
    public String updateUser(User user){

        System.out.println("-------------"+user);
        userService.updateUser(user);
        logger.debug(user.getRole()+user.getUsername()+"修改成功");
        return "forward:/permission";
    }

    /**
     * 删除客服或系统管理员
     * @param userId
     * @return
     */
    @RequestMapping("/deleteUser")
    public String deleteUser(int userId){
        userService.deleteUser(userId);
        logger.debug("id为"+userId+"的用户删除成功");
        return "forward:/permission";
    }

    /**
     * 检查用户名是否重复
     * @param username
     * @return
     */
    @RequestMapping("/checkUsername")
    @ResponseBody
    public String checkUserName(String username){
        logger.debug("检查用户名是否重复");
        return userService.checkUserName(username);
    }

    /**
     * 根据用户名查询用户信息
     * @param username
     * @return
     */
    @RequestMapping("/findUserByUsername/{username}")
    @ResponseBody
    public User findUserByUsername(@PathVariable String username){
        logger.debug("根据用户名查询用户信息");
        User user = userService.findUserByUsername(username);
        return user;
    }

    /**
     * 根据用户ID查询用户信息
     * @param id
     * @return
     */
    @RequestMapping("/findUserById")
    @ResponseBody
    public User findUserById(int id){
        logger.debug("根据用户ID查询用户信息");
        User user = userService.findUserById(id);
        return user;
    }

    @RequestMapping("/toTest")
    public String test(){
        return "/test";
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request){request.getSession().invalidate();return "forward:/index";}

    @RequestMapping("/takeTaxi")
    public String takeTaxi(){return "/customer/takeTaxi";}

    @RequestMapping("/receipt")
    public String receipt(){return "/driver/receipt";}

    @RequestMapping("/test")
    public String test1(){return "/admin/test";}

    @RequestMapping("/customerInfo")
    public String customerInfo(){return "/customer/info";}

    @RequestMapping("/adminInfo")
    public String adminInfo(){return "/admin/info";}

    @RequestMapping("/driverInfo")
    public String driverInfo(){return "/driver/info";}

    @RequestMapping("/employeeInfo")
    public String employeeInfo(){return "/employee/info";}

    /**
     * 上传头像
     * @param file
     * @param id
     * @param req
     * @return
     */
    @RequestMapping("/upload/{id}")
    @ResponseBody
    public MyJson<User> upload(@RequestParam(value="file") MultipartFile file, @PathVariable int id, HttpServletRequest req) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        String str1=df.format(new Date());
        String filename = null;
        List<User> list = new ArrayList<>();
        String property = System.getProperty("user.dir");
        String path = property+"/src/main/resources/static/img/"+id+str1+".jpg";
        MyJson<User> myJson = new MyJson();
        if (!file.isEmpty()) {
            File newFile=new File(path);
            try {
                logger.debug("开始上传头像");
                file.transferTo(newFile);
                filename="img/"+id+str1+".jpg";
                logger.debug("头像路径保存到数据库");
                userService.updatePic(filename,id);//增加用户
                User user = userService.findUserById(id);
                list.add(user);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int size = list.size();
            myJson.setCode(0);
            myJson.setMsg("");
            myJson.setCount(size);
            myJson.setData(list);
            System.out.println(myJson.toString());
            return myJson;
        }else {
            return myJson;
        }
    }

    @RequestMapping("/updateInfo")
    @ResponseBody
    public void updateInfo(int id,String tel,String email){
        logger.debug("修改个人信息");
        User user = userService.findUserById(id);
        user.setTel(tel);
        user.setEmail(email);
        userService.saveUser(user);
    }

    @RequestMapping("/ableUser")
    @ResponseBody
    public void ableUser(int id){
        logger.debug("启用用户");
        User user = userService.findUserById(id);
        user.setType(1);
        userService.saveUser(user);
    }
    @RequestMapping("/disableUser")
    @ResponseBody
    public void disableUser(int id){
        logger.debug("禁用用户");
        User user = userService.findUserById(id);
        user.setType(0);
        userService.saveUser(user);
    }

    @RequestMapping("/customerChat")
    public String customerChat(){return "/customer/chat";}

    @RequestMapping("/employeeChat")
    public String employeeChat(){return "/employee/chat";}
}
