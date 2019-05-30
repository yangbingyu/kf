package com.example.kf.service;

import com.example.kf.domain.util.TakeTaxiUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TakeTaxiService {

    public String getCity() throws IOException {
        System.out.println(TakeTaxiUtils.getPublicIP());
        JSONObject json = TakeTaxiUtils.readJsonFromUrl("http://api.map.baidu.com/location/ip?ak=iNXPRyw5FQpMFK4X5dTKzXbk9haQOzme&ip="+TakeTaxiUtils.getPublicIP());
        JSONObject content=json.getJSONObject("content");              //获取json对象里的content对象
        JSONObject addr_detail=content.getJSONObject("address_detail");//从content对象里获取address_detail
        String city=addr_detail.get("city").toString();                //获取市名，可以根据具体需求更改，如果需要获取省份的名字，可以把“city”改成“province”...
        return city;
    }

    public String getCoordinate(String address,String city){
        String coordinate = TakeTaxiUtils.getCoordinate(address,city);
        return coordinate;
    }
}
