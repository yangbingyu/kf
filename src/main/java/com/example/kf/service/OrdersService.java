package com.example.kf.service;

import com.example.kf.domain.Orders;
import com.example.kf.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<Orders> findAllByCustomerId(int customerId, Pageable pageable) {
        Page<Orders> list = ordersRepository.findAllByCustomerId(customerId,pageable);
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
    public Page<Orders> findByType(Pageable pageable){
        return ordersRepository.findByType(pageable);
    }

    /**
     * 通过登录用户查询已被接订单
     * @param userId
     * @return
     */
    public Page<Orders> findByTypeAndUserId(int userId,Pageable pageable){
        return ordersRepository.findByTypeAndUserId(userId,pageable);
    }

    /**
     * 通过登录用户查询已被接订单
     * @param userId
     * @return
     */
    public Page<Orders> findByTypeAndCustomerId(int userId,Pageable pageable){
        return ordersRepository.findByTypeAndCustomerId(userId,pageable);
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
    public Page<Orders> findByTypeAndDriver(int driver,Pageable pageable){
        return ordersRepository.findByTypeAndDriver(driver,pageable);
    }

    /**
     * 通过司机id查询已完成订单
     * @param driver
     * @return
     */
    public Page<Orders> findOrdersByDriver(int driver,Pageable pageable){
        return ordersRepository.findOrdersByDriver(driver,pageable);
    }

    /**
     * 查询全部
     * @return
     */
    public List<Orders> findOrdersByCustomerId(int customerId){
        return ordersRepository.findOrdersByCustomerId(customerId);
    }

    public List<Orders> findAll() {
        return ordersRepository.findAll();
    }

    public List<Orders> findOrdersByUserId(int userId) {
        return ordersRepository.findOrdersByUserId(userId);
    }
}
