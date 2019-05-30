package com.example.kf.repository;

import com.example.kf.domain.Evaluation;
import com.example.kf.resource.EvaluationParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation,Integer> {

    @Query(value = "select * from evaluation where order_id = ?1",nativeQuery = true)
    Evaluation findEvaluationByOrderId(int orderId);

    @Query(value = "select tag,count(tag) from evaluation group by tag",nativeQuery = true)
    List findCount();

    @Query(value = "select * from evaluation where tag = ?1",nativeQuery = true)
    List<Evaluation> findEvaluationByTag(String tag);
}
