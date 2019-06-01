package com.example.kf.resource;

import com.example.kf.domain.Evaluation;
import com.example.kf.domain.EvaluationDTO;
import com.example.kf.domain.Orders;
import com.example.kf.domain.User;
import com.example.kf.domain.common.MyJson;
import com.example.kf.service.EvaluationService;
import com.example.kf.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class EvaluationResource {

    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private UserService userService;

    Logger logger = LoggerFactory.getLogger(EvaluationResource.class);

    /**
     * 获得所有评价
     * @param page
     * @param limit
     * @return
     */
    @RequestMapping("/evaluation/findAll")
    @ResponseBody
    public MyJson<EvaluationDTO> findAll(@RequestParam int page, @RequestParam int limit){
        logger.debug("获得所有评价");
        Pageable pageable = new PageRequest(page-1,limit);
        Page<Evaluation> list = evaluationService.findAll(pageable);
        List<EvaluationDTO> evaluationDTOS = new ArrayList<>();
        for (Evaluation e: list){
            EvaluationDTO evaluationDTO = new EvaluationDTO();
            User user = userService.findUserById(e.getCustomerId());
            evaluationDTO.setId(e.getId());
            evaluationDTO.setUsername(user.getUsername());
            evaluationDTO.setOverallEvaluation(e.getOverallEvaluation());
            evaluationDTO.setDriverEvaluation(e.getDriverEvaluation());
            evaluationDTO.setEmployeeEvaluation(e.getEmployeeEvaluation());
            evaluationDTO.setReview(e.getReview());
            evaluationDTO.setTag(e.getTag());
            evaluationDTOS.add(evaluationDTO);
        }
        long size = list.getTotalElements();
        MyJson<EvaluationDTO> myJson = new MyJson();
        myJson.setCode(0);
        myJson.setMsg("");
        myJson.setCount((int) size);
        myJson.setData(evaluationDTOS);
        System.out.println(myJson.toString());
        return myJson;
    }

    /**
     * 根据tag分组并查询每个分组的个数
     * @return
     */
    @RequestMapping("/findCount")
    @ResponseBody
    public Map<String, Integer> findCount(HttpServletRequest request){
        logger.debug("根据tag分组并查询每个分组的个数");
        List list = evaluationService.findCount();
        Map<String,Integer> map = new ConcurrentHashMap<>();
        for(Object o : list){
            Object[] cells = (Object[]) o;
            String s = (String) cells[0];
            Integer value = Integer.valueOf(cells[1].toString());
            map.put(s,value);
        }
        request.getSession().setAttribute("list",map);
        return map;
    }

    /**
     * 根据tag查询评价
     * @param tag
     * @return
     */
    @RequestMapping("/evaluation/findEvaluationByTag/{tag}")
    @ResponseBody
    public MyJson<EvaluationDTO> findEvaluationByTag(@PathVariable String tag,@RequestParam int page,@RequestParam int limit){
        logger.debug("根据tag查询评价");
        Page<Evaluation> list;
        Pageable pageable = new PageRequest(page-1,limit);
        if(tag.equals("全部")){
            list = evaluationService.findAll(pageable);
        }else{
            list = evaluationService.findEvaluationByTag(tag,pageable);
            System.out.println("---------------"+list);
        }
        List<EvaluationDTO> evaluationDTOS = new ArrayList<>();
        for (Evaluation e: list){
            EvaluationDTO evaluationDTO = new EvaluationDTO();
            User user = userService.findUserById(e.getCustomerId());
            evaluationDTO.setId(e.getId());
            evaluationDTO.setUsername(user.getUsername());
            evaluationDTO.setOverallEvaluation(e.getOverallEvaluation());
            evaluationDTO.setDriverEvaluation(e.getDriverEvaluation());
            evaluationDTO.setEmployeeEvaluation(e.getEmployeeEvaluation());
            evaluationDTO.setReview(e.getReview());
            evaluationDTO.setTag(e.getTag());
            evaluationDTOS.add(evaluationDTO);
        }
        long size =list.getTotalElements();
        MyJson<EvaluationDTO> myJson = new MyJson();
        myJson.setCode(0);
        myJson.setMsg("");
        myJson.setCount((int) size);
        myJson.setData(evaluationDTOS);
        System.out.println(myJson.toString());
        return myJson;
    }
}
