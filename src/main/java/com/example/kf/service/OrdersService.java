package com.example.kf.service;

import com.example.kf.domain.Orders;
import com.example.kf.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class OrdersService {

    @Autowired
    private OrdersRepository ordersRepository;

    /**
     * 根据当前登录用户得到已完成订单
     * @param customerId
     * @return
     */
    public List<Orders> findAllByCustomerId(int customerId) {
        List<Orders> list = ordersRepository.findAllByCustomerId(customerId);
        return list;
    }

    /**
     * 保存订单
     * @param orders
     * @return
     */
    public Orders saveOrders(Orders orders){
        Orders orders1 = ordersRepository.save(orders);
        return orders1;
    }

    /**
     * 查询未被接的订单（全部）
     * @return
     */
    public List<Orders> findByType(){
        return ordersRepository.findByType();
    }

    /**
     * 通过登录用户查询已被接订单
     * @param userId
     * @return
     */
    public List<Orders> findByTypeAndUserId(int userId){
        return ordersRepository.findByTypeAndUserId(userId);
    }

    /**
     * 通过登录用户查询已被接订单
     * @param userId
     * @return
     */
    public List<Orders> findByTypeAndCustomerId(int userId){
        return ordersRepository.findByTypeAndCustomerId(userId);
    }

    /**
     * 通过id查询订单
     * @param id
     * @return
     */
    public Orders findOrdersById(int id){
        return ordersRepository.getOne(id);
    }

    /**
     * 取消订单
     * @param ordersId
     */
    public void deleteOrders(int ordersId) {
        ordersRepository.deleteById(ordersId);
    }

    /**
     * 通过司机id查询已接订单
     * @param driver
     * @return
     */
    public List<Orders> findByTypeAndDriver(int driver){
        return ordersRepository.findByTypeAndDriver(driver);
    }

    /**
     * 通过司机id查询已完成订单
     * @param driver
     * @return
     */
    public List<Orders> findOrdersByDriver(int driver){
        return ordersRepository.findOrdersByDriver(driver);
    }

    /**
     * 查询全部
     * @return
     */
    public List<Orders> findOrdersByCustomerId(int customerId){
        return ordersRepository.findOrdersByCustomerId(customerId);
    }
}
