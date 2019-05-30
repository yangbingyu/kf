package com.example.kf.domain.util;

import net.sf.json.JSONException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.DecimalFormat;

public class TakeTaxiUtils {

    static String AK = "iNXPRyw5FQpMFK4X5dTKzXbk9haQOzme"; // 百度地图密钥

    // 调用百度地图API根据地址，获取坐标
    public static String getCoordinate(String address,String city) {
        if (address != null && !"".equals(address)) {
            address = address.replaceAll("\\s*", "").replace("#", "栋");
            String url = "http://api.map.baidu.com/geocoder/v2/?address=" + address+"city="+city + "&output=json&ak=" + AK;
            String json = loadJSON(url);
            System.out.println(json);
            if (json != null && !"".equals(json)) {
                net.sf.json.JSONObject obj = net.sf.json.JSONObject.fromObject(json);
                if ("0".equals(obj.getString("status"))) {
                    double lng = obj.getJSONObject("result").getJSONObject("location").getDouble("lng"); // 经度
                    double lat = obj.getJSONObject("result").getJSONObject("location").getDouble("lat"); // 纬度
                    DecimalFormat df = new DecimalFormat("#.######");
                    return df.format(lng) + "," + df.format(lat);
                }
            }
        }
        return null;
    }

    public static String loadJSON(String url) {
        StringBuilder json = new StringBuilder();
        try {
            URL oracle = new URL(url);
            URLConnection yc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream(), "UTF-8"));
            String inputLine = null;
            while ((inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {} catch (IOException e) {}
        return json.toString();
    }

    /**
     * 得到本机的外网ip，出现异常时返回空串""
     * @return
     */
    public static String getPublicIP() {
        String ip = "";
        org.jsoup.nodes.Document doc = null;
        Connection con = Jsoup.connect("http://www.ip138.com").timeout(10000);
        try {
            doc = con.get();

            // 获得包含本机ip的文本串：您的IP是：[xxx.xxx.xxx.xxx]
            org.jsoup.select.Elements els = doc.body().select("center");
            for (org.jsoup.nodes.Element el : els) {
                ip = el.text();
            }
            // 从文本串过滤出ip，用正则表达式将非数字和.替换成空串""
            ip = ip.replaceAll("[^0-9.]", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ip;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static org.json.JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = null;
        try {
            is = new URL(url).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            org.json.JSONObject json = new org.json.JSONObject(jsonText);
            return json;
        } finally {
            //关闭输入流
            is.close();
        }
    }

    public static String getAdd(String lng, String lat ){
        //lat 小  log  大
        System.out.println(lng+","+lat);
        String urlString = "http://api.map.baidu.com/geocoder/v2/?callback=renderReverse&location="+lng+","+lat+"&output=json&pois=0&latest_admin=1&ak="+AK;
        String json = loadJSON(urlString);
        String[] split = json.split("\\(");
        String[] split1 = split[1].split("\\)");
        System.out.println(split1[0]);
        if (split1[0] != null && !"".equals(split1[0])) {
            net.sf.json.JSONObject obj = net.sf.json.JSONObject.fromObject(split1[0]);
            System.out.println(obj);
        }
        return null;
    }


}
