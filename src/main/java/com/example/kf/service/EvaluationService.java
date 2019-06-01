package com.example.kf.service;

import com.example.kf.domain.Evaluation;
import com.example.kf.repository.EvaluationRepository;
import com.example.kf.resource.EvaluationParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvaluationService {

    @Autowired
    private EvaluationRepository evaluationRepository;

    public Page<Evaluation> findAll(Pageable pageable){
        return evaluationRepository.findAll(pageable);
    }

    /**
     * 根据tag分组并查询每个分组的个数
     * @return
     */
    public List findCount(){
        return evaluationRepository.findCount();
    }

    /**
     * 根据tag查询评价
     * @param tag
     * @return
     */
    public Page<Evaluation> findEvaluationByTag(String tag,Pageable pageable){
        return evaluationRepository.findEvaluationByTag(tag,pageable);
    }
}
