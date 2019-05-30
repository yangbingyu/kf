package com.example.kf.repository;

import com.example.kf.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders,Integer> {

    @Query(value = "select * from orders where customer_id = ?1 and type = 2",nativeQuery = true)
    @Modifying
    List<Orders> findAllByCustomerId(int customerId);

    @Query(value = "select * from orders where type = 0",nativeQuery = true)
    List<Orders> findByType();

    @Query(value = "select * from orders where type = 1 and customer_id = ?1",nativeQuery = true)
    List<Orders> findByTypeAndUserId(int customerId);

    @Query(value = "select * from orders where type = 0 and customer_id = ?1",nativeQuery = true)
    List<Orders> findByTypeAndCustomerId(int customerId);

    @Query(value = "select * from orders where type = 1 and driver = ?1",nativeQuery = true)
    List<Orders> findByTypeAndDriver(int driver);

    @Query(value = "select * from orders where type = 2 and driver = ?1",nativeQuery = true)
    List<Orders> findOrdersByDriver(int driver);

    @Query(value = "select * from orders where customer_id = ?1",nativeQuery = true)
    List<Orders> findOrdersByCustomerId(int customerId);
}
