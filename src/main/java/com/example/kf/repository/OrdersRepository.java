package com.example.kf.repository;

import com.example.kf.domain.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders,Integer> {

    @Query(value = "select * from orders where customer_id = ?1 and type = 2",nativeQuery = true)
    Page<Orders> findAllByCustomerId(int customerId, Pageable pageable);

    @Query(value = "select * from orders where type = 0",nativeQuery = true)
    Page<Orders> findByType(Pageable pageable);

    @Query(value = "select * from orders where type = 1 and customer_id = ?1",nativeQuery = true)
    Page<Orders> findByTypeAndUserId(int customerId,Pageable pageable);

    @Query(value = "select * from orders where type = 0 and customer_id = ?1",nativeQuery = true)
    Page<Orders> findByTypeAndCustomerId(int customerId,Pageable pageable);

    @Query(value = "select * from orders where type = 1 and driver = ?1",nativeQuery = true)
    Page<Orders> findByTypeAndDriver(int driver,Pageable pageable);

    @Query(value = "select * from orders where type = 2 and driver = ?1",nativeQuery = true)
    Page<Orders> findOrdersByDriver(int driver,Pageable pageable);

    @Query(value = "select * from orders where customer_id = ?1",nativeQuery = true)
    List<Orders> findOrdersByCustomerId(int customerId);

    @Query(value = "select * from orders where driver = ?1",nativeQuery = true)
    List<Orders> findOrdersByUserId(int userId);
}
