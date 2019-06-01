package com.example.kf.resource;

import com.example.kf.service.TakeTaxiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TakeTaxiResource {

    @Autowired
    private TakeTaxiService takeTaxiService;

    Logger logger = LoggerFactory.getLogger(TakeTaxiResource.class);

    @RequestMapping("/getLatitude")
    @ResponseBody
    public List<String> getLatitude(String fromPoint, String toPoint){
        logger.debug("根据起点终点定位城市以及获得经纬度");
        List<String> list = new ArrayList<>();
        try {
            String city = takeTaxiService.getCity();
            String origin = takeTaxiService.getCoordinate(fromPoint,city);
            String destination = takeTaxiService.getCoordinate(toPoint,city);
            list.add(origin);
            list.add(destination);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }
}
