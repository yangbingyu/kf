package com.example.kf.resource;

import com.example.kf.domain.Evaluation;
import com.example.kf.domain.Orders;
import com.example.kf.domain.OrdersDTO;
import com.example.kf.domain.User;
import com.example.kf.domain.common.MyJson;
import com.example.kf.repository.EvaluationRepository;
import com.example.kf.service.OrdersService;
import com.example.kf.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    Logger logger = LoggerFactory.getLogger(OrdersResource.class);

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
     * @return
     */
    @RequestMapping("/orders/findAll/{userId}")
    @ResponseBody
    public MyJson<OrdersDTO> findAllByCustomerId(@PathVariable int userId, @RequestParam int page,@RequestParam int limit) {
        logger.debug("根据当前登录用户得到已完成订单");
        Pageable pageable = new PageRequest(page-1,limit);
        Page<Orders> list = ordersService.findAllByCustomerId(userId,pageable);
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
        long size = list.getTotalElements();
        MyJson<OrdersDTO> myJson = new MyJson();
        myJson.setCode(0);
        myJson.setMsg("");
        myJson.setCount((int) size);
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
        logger.debug("id为"+userId+"的用户对id为"+orderId+"的订单进行了评价，分数分别为："+overallEvaluation+","+driverEvaluation+","+employeeEvaluation);
        Evaluation evaluation = new Evaluation();
        evaluation.setCustomerId(userId);
        evaluation.setDriverEvaluation(driverEvaluation);
        evaluation.setEmployeeEvaluation(employeeEvaluation);
        evaluation.setOrderId(orderId);
        evaluation.setOverallEvaluation(overallEvaluation);
        evaluation.setReview(review);
        logger.debug("对评价进行分类");
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
        logger.debug("通过订单id查询评价");
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
        logger.debug("id为"+orders.getCustomerId()+"的用户预约了"+orders.getDate()+"从"+orders.getOrigin()+"到"+orders.getDestination()+"的车");
        orders.setType(0);
        ordersService.saveOrders(orders);
        return "forward:/takeTaxi";
    }

    /**
     * 查询未被接的订单（全部）
     * @return
     */
    @RequestMapping("/orders/findByType")
    @ResponseBody
    public MyJson<Orders> findByType(@RequestParam int page,@RequestParam int limit) {
        logger.debug("查询所有未被接的订单");
        Pageable pageable = new PageRequest(page-1,limit);
        Page<Orders> list = ordersService.findByType(pageable);
        long size = list.getTotalElements();
        MyJson<Orders> myJson = new MyJson();
        myJson.setCode(0);
        myJson.setMsg("");
        myJson.setCount((int) size);
        myJson.setData(list.getContent());
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
    public MyJson<OrdersDTO> findByTypeAndUserId(@PathVariable int userId,@RequestParam int page,@RequestParam int limit) {
        logger.debug("通过用户id查询已接订单");
        Pageable pageable = new PageRequest(page-1,limit);
        Page<Orders> list = ordersService.findByTypeAndUserId(userId,pageable);
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
        long size = list.getTotalElements();
        MyJson<OrdersDTO> myJson = new MyJson();
        myJson.setCode(0);
        myJson.setMsg("");
        myJson.setCount((int) size);
        myJson.setData(ordersDTOS);
        System.out.println(myJson.toString());
        return myJson;
    }

    /**
     * 通过用户id查询未接订单
     * @param userId
     * @return
     */
    @RequestMapping("/orders/findByTypeAndCustomerId/{userId}")
    @ResponseBody
    public MyJson<Orders> findByTypeAndCustomerId(@PathVariable int userId,@RequestParam int page,@RequestParam int limit) {
        logger.debug("通过用户id查询未接订单");
        Pageable pageable = new PageRequest(page-1,limit);
        Page<Orders> list = ordersService.findByTypeAndCustomerId(userId,pageable);
        long size = list.getTotalElements();
        MyJson<Orders> myJson = new MyJson();
        myJson.setCode(0);
        myJson.setMsg("");
        myJson.setCount((int) size);
        myJson.setData(list.getContent());
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
        logger.debug("司机"+driver+"接了"+orders.getDate()+"从"+orders.getOrigin()+"到"+orders.getDestination()+"的订单");
        orders.setDriver(driver);
        orders.setType(1);
        ordersService.saveOrders(orders);
    }

    /**
     * 取消订单
     * @param ordersId
     * @return
     */
    @RequestMapping("/orders/cancelOrders")
    @ResponseBody
    public void cancelOrders(int ordersId){
        logger.debug("用户取消了id为"+ordersId+"的订单");
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
        logger.debug("id为"+orders.getCustomerId()+"的用户驳回了id为"+ordersId+"的订单，接单司机id为"+orders.getDriver());
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
    public MyJson<OrdersDTO> findByTypeAndDriver(@PathVariable int driver,@RequestParam int page,@RequestParam int limit){
        logger.debug("根据司机的id查询已接订单");
        Pageable pageable = new PageRequest(page-1,limit);
        Page<Orders> list = ordersService.findByTypeAndDriver(driver,pageable);
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
        long size = list.getTotalElements();
        MyJson<OrdersDTO> myJson = new MyJson();
        myJson.setCode(0);
        myJson.setMsg("");
        myJson.setCount((int) size);
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
    public MyJson<OrdersDTO> findOrdersByDriver(@PathVariable int driver,@RequestParam int page,@RequestParam int limit){
        logger.debug("根据司机的id查询已完成订单");
        Pageable pageable = new PageRequest(page-1,limit);
        Page<Orders> list = ordersService.findOrdersByDriver(driver,pageable);
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
        long size = list.getTotalElements();
        MyJson<OrdersDTO> myJson = new MyJson();
        myJson.setCode(0);
        myJson.setMsg("");
        myJson.setCount((int) size);
        myJson.setData(ordersDTOS);
        System.out.println(myJson.toString());
        return myJson;
    }

    /**
     * 预约时间超过当前时间并且订单已被接则修改为已完成订单(用户)
     */
    @RequestMapping("/orders/findOrdersByCustomerId/{customerId}")
    @ResponseBody
    public void findOrdersByCustomerId(@PathVariable int customerId){
        logger.debug("用户预约时间超过当前时间并且订单已被接则修改为已完成订单");
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

    /**
     * 预约时间超过当前时间并且订单未被接则删除订单
     */
    @RequestMapping("/orders/deleteOrders")
    @ResponseBody
    public void deleteOrders(){
        logger.debug("预约时间超过当前时间并且订单未被接则删除订单");
        List<Orders> ordersList = ordersService.findAll();
        for(Orders orders : ordersList){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            String s = df.format(new Date());
            int i = orders.getDate().compareTo(s);
            if(i < 0 && orders.getType() == 0){
                ordersService.deleteOrders(orders.getId());
            }
        }
    }

    /**
     * 预约时间超过当前时间并且订单已被接则修改为已完成订单(司机)
     */
    @RequestMapping("/orders/findOrdersByUserId/{userId}")
    @ResponseBody
    public void findOrdersByUserId(@PathVariable int userId){
        logger.debug("预约时间超过当前时间并且订单已被接则修改为已完成订单(司机)");
        List<Orders> ordersList = ordersService.findOrdersByUserId(userId);
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
