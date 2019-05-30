package com.example.kf.resource;

import com.example.kf.domain.User;
import com.example.kf.domain.common.MyJson;
import com.example.kf.domain.enums.Role;
import com.example.kf.service.TakeTaxiService;
import com.example.kf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
            return "/customer/c_index";
        }else if(password.equals(user.getPassword()) && user.getRole().equals(Role.客服.toString()) && user.getType() == 1){
            return "/employee/e_index";
        }else if(password.equals(user.getPassword()) && user.getRole().equals(Role.系统管理员.toString()) && user.getType() == 1){
            return "/admin/a_index";
        }else if(password.equals(user.getPassword()) && user.getRole().equals(Role.司机.toString()) && user.getType() == 1){
            return "/driver/d_index";
        }else if(!password.equals(user.getPassword())){
//            model.addAttribute("msg","用户名或密码错误！");
            request.getSession().setAttribute("msg","用户名或密码错误");
            return "/login";
        }else {
//            model.addAttribute("msg","该用户已被禁用！");
            request.getSession().setAttribute("msg","该用户已被禁用");
            return "/login";
        }
    }

    @RequestMapping("/register")
    public String register(){
        return "/register";
    }

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
            userService.saveUser(user1);
            return "/login";
        }else {
            if(!password.equals(rePassword)){
                model.addAttribute("msg","密码不一致");
            }
            if(user != null){
                model.addAttribute("msg","用户名已被占用");
            }
            return "/register";
        }
    }

    @RequestMapping(value="/findUser")
    @ResponseBody
    public MyJson<User> findUser() {
        List<User> list = userService.findUser();
        int size = list.size();
        MyJson<User> myJson = new MyJson();
        myJson.setCode(0);
        myJson.setMsg("");
        myJson.setCount(size);
        myJson.setData(list);
        System.out.println(myJson.toString());
        return myJson;
    }

    @RequestMapping("/addUser")
    public String addUser(User user){
        System.out.println("================"+user);
        user.setType(1);
        String username = user.getUsername();
        User user2 = userService.findUserByUsername(username);
        if(user2 == null){
            userService.saveUser(user);
            return "forward:/permission";
        }else {
            return "";
        }
    }

    @RequestMapping("/updateUser")
    public String updateUser(User user){

        System.out.println("-------------"+user);
        userService.updateUser(user);
        return "forward:/permission";
    }

    @RequestMapping("/deleteUser")
    public String deleteUser(int userId){
        userService.deleteUser(userId);
        return "forward:/permission";
    }

    @RequestMapping("/checkUsername")
    @ResponseBody
    public String checkUserName(String username){
        return userService.checkUserName(username);
    }

    @RequestMapping("/findUserByUsername/{username}")
    @ResponseBody
    public User findUserByUsername(@PathVariable String username){
        User user = userService.findUserByUsername(username);
        return user;
    }

    @RequestMapping("/findUserById")
    @ResponseBody
    public User findUserById(int id){
        System.out.println("----------"+id);
        User user = userService.findUserById(id);
        System.out.println(user.toString());
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
            //通过CommonsMultipartFile的方法直接写文件（注意这个时候）
            try {
                file.transferTo(newFile);
                filename="img/"+id+str1+".jpg";
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
        User user = userService.findUserById(id);
        user.setTel(tel);
        user.setEmail(email);
        userService.saveUser(user);
    }
}
