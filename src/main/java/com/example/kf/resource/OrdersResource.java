package com.example.kf.resource;

import com.example.kf.domain.Evaluation;
import com.example.kf.domain.Orders;
import com.example.kf.domain.OrdersDTO;
import com.example.kf.domain.User;
import com.example.kf.domain.common.MyJson;
import com.example.kf.repository.EvaluationRepository;
import com.example.kf.service.OrdersService;
import com.example.kf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class OrdersResource {

    @Autowired
    private OrdersService ordersService;
    @Autowired
    private EvaluationRepository evaluationRepository;
    @Autowired
    private UserService userService;

    @RequestMapping("/completeOrders")
    public String completeOrders(){
        return "/customer/completeOrders";
    }

    @RequestMapping("/receivedOrders")
    public String receivedOrders() {return "/customer/receivedOrders";}

    @RequestMapping("/openOrders")
    public String openOrders() {return "/customer/openOrders";}

    @RequestMapping("/driveCompleteOrders")
    public String driverCompleteOrders(){
        return "/driver/completeOrders";
    }

    @RequestMapping("/driverReceivedOrders")
    public String driverReceivedOrders() {return "/driver/receivedOrders";}

    /**
     * 根据当前登录用户得到已完成订单
     * @param userId
     * @param map
     * @return
     */
    @RequestMapping("/orders/findAll/{userId}")
    @ResponseBody
    public MyJson<OrdersDTO> findAllByCustomerId(@PathVariable int userId, ModelMap map) {
        List<Orders> list = ordersService.findAllByCustomerId(userId);
        List<OrdersDTO> ordersDTOS = new ArrayList<>();
        for(Orders orders: list){
            User user = userService.findUserById(orders.getDriver());
            OrdersDTO ordersDTO = new OrdersDTO();
            ordersDTO.setId(orders.getId());
            ordersDTO.setDate(orders.getDate());
            ordersDTO.setDriver(user.getUsername());
            ordersDTO.setDestination(orders.getDestination());
            ordersDTO.setOrigin(orders.getOrigin());
            ordersDTO.setPrice(orders.getPrice());
            ordersDTOS.add(ordersDTO);
        }
        int size = ordersDTOS.size();
        MyJson<OrdersDTO> myJson = new MyJson();
        myJson.setCode(0);
        myJson.setMsg("");
        myJson.setCount(size);
        myJson.setData(ordersDTOS);
        System.out.println(myJson.toString());
        return myJson;
    }

    /**
     * 对已完成订单进行评分
     * @param userId
     * @param overallEvaluation
     * @param driverEvaluation
     * @param employeeEvaluation
     * @param orderId
     */
    @RequestMapping("/orders/evaluation/{userId}")
    @ResponseBody
    public void evaluation(@PathVariable int userId, String overallEvaluation, String driverEvaluation, String  employeeEvaluation, int orderId, String review) {
        Evaluation evaluation = new Evaluation();
        evaluation.setCustomerId(userId);
        evaluation.setDriverEvaluation(driverEvaluation);
        evaluation.setEmployeeEvaluation(employeeEvaluation);
        evaluation.setOrderId(orderId);
        evaluation.setOverallEvaluation(overallEvaluation);
        evaluation.setReview(review);
        if (review.contains("司机态度好") || review.contains("司机态度不错") || Integer.parseInt(driverEvaluation)>3){
            evaluation.setTag("司机态度好");
        }
        if (review.contains("司机态度不好") || review.contains("司机态度差") || Integer.parseInt(driverEvaluation)<2){
            evaluation.setTag("司机态度差");
        }
        if (review.contains("司机态度一般") || review.contains("司机一般") || (Integer.parseInt(driverEvaluation)>1 && Integer.parseInt(overallEvaluation)<4)){
            evaluation.setTag("司机态度一般");
        }
        if (review.contains("客服很耐心") || review.contains("客服挺好的") || Integer.parseInt(employeeEvaluation)>3){
            evaluation.setTag("客服态度好");
        }
        if (review.contains("客服一般") || review.contains("客服态度一般") || (Integer.parseInt(employeeEvaluation)>1 && Integer.parseInt(employeeEvaluation)<4)){
            evaluation.setTag("客服态度一般");
        }
        if (review.contains("客服态度不好") || review.contains("客服态度差") || Integer.parseInt(employeeEvaluation)<2){
            evaluation.setTag("客服态度差");
        }
        if (review.contains("整体服务较好") || Integer.parseInt(overallEvaluation)>3){
            evaluation.setTag("整体感觉不错");
        }
        if (review.contains("整体服务一般") || (Integer.parseInt(overallEvaluation)>1 && Integer.parseInt(overallEvaluation)<4)){
            evaluation.setTag("整体感觉一般");
        }
        if (review.contains("整体服务差") || Integer.parseInt(overallEvaluation)<2){
            evaluation.setTag("整体感觉差");
        }
        evaluationRepository.save(evaluation);
    }

    /**
     * 通过订单id查询评价
     * @param orderId
     * @return
     */
    @RequestMapping("/orders/getEvaluation")
    @ResponseBody
    public Evaluation getEvaluation(int orderId){
        System.out.println(orderId);
        Evaluation evaluation = evaluationRepository.findEvaluationByOrderId(orderId);
        return evaluation;
    }

    /**
     * 保存新建的订单
     * @param orders
     * @return
     */
    @RequestMapping("/saveOrders")
    public String saveOrders(Orders orders){
        orders.setType(0);
        System.out.println("--------------------"+orders.toString());
        ordersService.saveOrders(orders);
        return "forward:/takeTaxi";
    }

    /**
     * 查询未被接的订单（全部）
     * @return
     */
    @RequestMapping("/orders/findByType")
    @ResponseBody
    public MyJson<Orders> findByType() {
        List<Orders> list = ordersService.findByType();
        int size = list.size();
        MyJson<Orders> myJson = new MyJson();
        myJson.setCode(0);
        myJson.setMsg("");
        myJson.setCount(size);
        myJson.setData(list);
        System.out.println(myJson.toString());
        return myJson;
    }

    /**
     * 通过用户id查询已接订单
     * @param userId
     * @return
     */
    @RequestMapping("/orders/findByTypeAndUserId/{userId}")
    @ResponseBody
    public MyJson<OrdersDTO> findByTypeAndUserId(@PathVariable int userId) {
        List<Orders> list = ordersService.findByTypeAndUserId(userId);
        List<OrdersDTO> ordersDTOS = new ArrayList<>();
        for(Orders orders: list){
            User user = userService.findUserById(orders.getDriver());
            OrdersDTO ordersDTO = new OrdersDTO();
            ordersDTO.setId(orders.getId());
            ordersDTO.setDate(orders.getDate());
            ordersDTO.setDriver(user.getUsername());
            ordersDTO.setDestination(orders.getDestination());
            ordersDTO.setOrigin(orders.getOrigin());
            ordersDTO.setPrice(orders.getPrice());
            ordersDTOS.add(ordersDTO);
        }
        int size = ordersDTOS.size();
        MyJson<OrdersDTO> myJson = new MyJson();
        myJson.setCode(0);
        myJson.setMsg("");
        myJson.setCount(size);
        myJson.setData(ordersDTOS);
        System.out.println(myJson.toString());
        return myJson;
    }

    /**
     * 通过用户id查询已接订单
     * @param userId
     * @return
     */
    @RequestMapping("/orders/findByTypeAndCustomerId/{userId}")
    @ResponseBody
    public MyJson<Orders> findByTypeAndCustomerId(@PathVariable int userId) {
        List<Orders> list = ordersService.findByTypeAndCustomerId(userId);
        int size = list.size();
        MyJson<Orders> myJson = new MyJson();
        myJson.setCode(0);
        myJson.setMsg("");
        myJson.setCount(size);
        myJson.setData(list);
        System.out.println(myJson.toString());
        return myJson;
    }

    /**
     * 司机接单后，把订单的状态修改为1
     * @param ordersId
     */
    @RequestMapping("/orders/updateOrdersType")
    @ResponseBody
    public void updateOrdersType(int ordersId,int driver){
        Orders orders = ordersService.findOrdersById(ordersId);
        orders.setDriver(driver);
        orders.setType(1);
        ordersService.saveOrders(orders);
    }

    /**
     * 取消订单
     * @param ordersId
     * @return
     */
    @RequestMapping("/orders/deleteOrders")
    @ResponseBody
    public void deleteOrders(int ordersId){
        ordersService.deleteOrders(ordersId);
    }

    /**
     * 驳回订单
     * @param ordersId
     * @return
     */
    @RequestMapping("/orders/updateDriverAndType")
    @ResponseBody
    public void updateDriverAndType(int ordersId){
        Orders orders = ordersService.findOrdersById(ordersId);
        orders.setType(0);
        orders.setDriver(0);
        ordersService.saveOrders(orders);
    }

    /**
     * 根据司机的id查询已接订单
     * @param driver
     */
    @RequestMapping("/orders/findByTypeAndDriver/{driver}")
    @ResponseBody
    public MyJson<OrdersDTO> findByTypeAndDriver(@PathVariable int driver){
        List<Orders> list = ordersService.findByTypeAndDriver(driver);
        List<OrdersDTO> ordersDTOS = new ArrayList<>();
        for(Orders orders: list){
            User user = userService.findUserById(orders.getCustomerId());
            OrdersDTO ordersDTO = new OrdersDTO();
            ordersDTO.setId(orders.getId());
            ordersDTO.setDate(orders.getDate());
            ordersDTO.setCustomerId(user.getUsername());
            ordersDTO.setDestination(orders.getDestination());
            ordersDTO.setOrigin(orders.getOrigin());
            ordersDTO.setPrice(orders.getPrice());
            ordersDTOS.add(ordersDTO);
        }
        int size = ordersDTOS.size();
        MyJson<OrdersDTO> myJson = new MyJson();
        myJson.setCode(0);
        myJson.setMsg("");
        myJson.setCount(size);
        myJson.setData(ordersDTOS);
        System.out.println(myJson.toString());
        return myJson;
    }

    /**
     * 根据司机的id查询已完成订单
     * @param driver
     */
    @RequestMapping("/orders/findOrdersByDriver/{driver}")
    @ResponseBody
    public MyJson<OrdersDTO> findOrdersByDriver(@PathVariable int driver){
        List<Orders> list = ordersService.findOrdersByDriver(driver);
        List<OrdersDTO> ordersDTOS = new ArrayList<>();
        for(Orders orders: list){
            User user = userService.findUserById(orders.getCustomerId());
            OrdersDTO ordersDTO = new OrdersDTO();
            ordersDTO.setId(orders.getId());
            ordersDTO.setDate(orders.getDate());
            ordersDTO.setCustomerId(user.getUsername());
            ordersDTO.setDestination(orders.getDestination());
            ordersDTO.setOrigin(orders.getOrigin());
            ordersDTO.setPrice(orders.getPrice());
            ordersDTOS.add(ordersDTO);
        }
        int size = ordersDTOS.size();
        MyJson<OrdersDTO> myJson = new MyJson();
        myJson.setCode(0);
        myJson.setMsg("");
        myJson.setCount(size);
        myJson.setData(ordersDTOS);
        System.out.println(myJson.toString());
        return myJson;
    }

    @RequestMapping("/orders/cancelOrders")
    @ResponseBody
    public void cancelOrders(int ordersId){
        Orders orders = ordersService.findOrdersById(ordersId);
        orders.setDriver(0);
        orders.setType(0);
        ordersService.saveOrders(orders);
    }

    @RequestMapping("/orders/findOrdersByCustomerId/{customerId}")
    @ResponseBody
    public void findOrdersByCustomerId(@PathVariable int customerId){
        List<Orders> ordersList = ordersService.findOrdersByCustomerId(customerId);
        for(Orders orders : ordersList){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            String s = df.format(new Date());
            int i = orders.getDate().compareTo(s);
            if(i < 0 && orders.getType() == 1){
                orders.setType(2);
                ordersService.saveOrders(orders);
            }
        }

    }
}
