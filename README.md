# 快递鸟-接口调用DEMO
## 前期准备工作
去[快递鸟官网](http://www.kdniao.com/reg)免费注册一个账号，免费获得一个apiKey(接口权限验证需要)，完成实名认证流程，订购一个免费或付费套餐（也可找快递鸟工作人员免费申请付费的接口测试联调）

注：登录快递鸟用户管理后台后获得用户ID和APIKey对应各demo中的EBusinessID、AppKey。

小知识：EBusinessID跟APIKey是什么？EBusinessID跟APIKey您在快递鸟网站注册之后快递鸟分配的密钥（对应官网上的用户ID和API key），用于保证应用来源的可靠性，避免应用伪造，被不法使用。

快递鸟是专业的第三方物流数据服务商，国家高新技术企业，已先后完成四轮融资，一直专注于企业级物流API技术研发和打通物流各节点信息服务，致力于成为全球最大的物流信息枢纽中心，为零售电商企业级提供标准的物流接口和物流模块整体解决方案，为开发者聚合600+快递物流公司接口，可一次性快速对接完成，实现物流轨迹信息查询、电子面单批量打印、预约快递员上门取件等一站式物流模块功能植入。

快递鸟API查询接口支持包括顺丰、中通、韵达、圆通、申通、百世、EMS、邮政等600家以上快递物流公司，详情点击查看快递鸟支持的快递公司列表。http://www.kdniao.com/documents

https://pic1.zhimg.com/v2-b876bfd717ce94bdf105819b58d36690_b.png


## 对接中的其他说明
1、物流查询（免费版）会员套餐为免费版，有效期1年结束后，如在近3个月内有数据交互系统会自动免费续期；

2、即时查询（RequestType：1002/8001） 日查询次数<=3000次对接即时查询接口

3、请求接口之前需要先实名认证，开通相关会员服务，否则会请求失败并返回提示“未申请开通接口”；

4、接口开发可以下载“当前项目”更改KEY密钥；

5、物流跟踪（RequestType：1008/8008） 日查询次数>3000次对接物流跟踪接口

6、测试订阅接口，对照技术文档正确返回代表订阅接口对接成功，详情可见技术文档。

7、开发推送接口，无demo提供，推送时会推送requestType、requestData和DataSign三个参数，您开发一个推送接口接收这三个参数就行，成功接收后需要在5S内给快递鸟返回成功收数据的报文，否则超时。RequestData中包含应用级参数，即物流轨迹（详情看技术文档）；

8、订阅接口、推送接口分别测试成功后，可使用正式地址进行订阅真实的快递单号，快递鸟一般会在2-12小时内推送物流信息至您已经配置好的回调地址上;

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
    public static final String KEY = "3e3b8652-1234-4a68-8c1b-7ec469ef3a19";//APP KEY,请向快递鸟申请
    public static final String BUSINESS_ID = "11122233";//用户ID，请向快递鸟申请
    public static final String REQUEST_TYPE = "8001";//请求接口指令（8001查询）

    public static void main(String[] args) {
        System.out.println(new Demo().getRoute("STO", "773061132607004"));
    }

    public String getRoute(String expressCode, String logisticCode) {
        LinkedMultiValueMap<String, String> param = parseParam(expressCode, logisticCode);
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

    private LinkedMultiValueMap<String, String> parseParam(String expressCode, String logisticCode) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("ShipperCode", expressCode);
        map.put("LogisticCode", logisticCode);
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

