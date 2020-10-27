package com.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

public class Demo {

    public static final String URL = "http://api.kdniao.com/Ebusiness/EbusinessOrderHandle.aspx";
    public static final String KEY = "06920cf9-2f55-49b1-b209-4afaceed36ee";//APP KEY,请向快递鸟申请
    public static final String BUSINESS_ID = "1112222";//用户ID，请向快递鸟申请
    public static final String REQUEST_TYPE = "8001";//请求接口指令（8001查询）

    public static void main(String[] args) {
//        System.out.println(new Demo().getRoute("STO", "773061132607004",""));
        System.out.println(new Demo().getRoute("SF", "SF1040595620412","5690"));
    }

    public String getRoute(String expressCode, String logisticCode, String customerName) {
        LinkedMultiValueMap<String, String> param = parseParam(expressCode, logisticCode,customerName);
        return springSend(param);
    }

    private String springSend(LinkedMultiValueMap<String, String> param) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(param, headers);
        String response = null;
        try {
            response = restTemplate.postForObject(URL, request, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return response;
    }

    private LinkedMultiValueMap<String, String> parseParam(String expressCode, String logisticCode, String customerName) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("ShipperCode", expressCode);
        map.put("LogisticCode", logisticCode);
        map.put("CustomerName", customerName);
        LinkedMultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        String jsonStr = null;
        String DataSign = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonStr = mapper.writeValueAsString(map);
            DataSign = Base64.encodeBase64String(DigestUtils.md5Hex((jsonStr + KEY).getBytes()).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        param.add("RequestType", REQUEST_TYPE);
        param.add("EBusinessID", BUSINESS_ID);
        param.add("RequestData", jsonStr);
        param.add("DataSign", DataSign);
        param.add("DataType", "2");
        return param;
    }
}
