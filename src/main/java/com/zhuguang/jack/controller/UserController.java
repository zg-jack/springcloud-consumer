package com.zhuguang.jack.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;

import com.alibaba.fastjson.JSONObject;
import com.zhuguang.jack.bean.ConsultContent;
import com.zhuguang.jack.hystrixCommand.UserhystrixCommand;
import com.zhuguang.jack.hystrixObservableCommand.UserHystrixObservableCommand;
import com.zhuguang.jack.ribbon.RibbonConfiguration;
import com.zhuguang.jack.service.UserService;

/** 
 *  往期视频加小露老师QQ：1533922121
 *  Jack老师QQ： 2943489129
 *  时间   ：     2018年1月26日 下午2:56:18 
 *  作者   ：   烛光学院【Jack老师】
 */

@RestController
//name是provider的服务名   RibbonConfiguration为自定义配置
@RibbonClient(name = "springcloud-provider", configuration = RibbonConfiguration.class)
public class UserController {
    
    Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    DiscoveryClient disClient;
    
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    
    @Autowired
    UserService userService;
    
    public static String SERVIER_NAME = "springcloud-provider";
    
    UserHystrixObservableCommand uhoc = new UserHystrixObservableCommand();
    
    @RequestMapping("/index")
    public List<ConsultContent> index() {
        
        ServiceInstance serviceInstance = this.loadBalancerClient.choose("springcloud-provider");
        logger.info("===" + ":" + serviceInstance.getServiceId() + ":"
                + serviceInstance.getHost() + ":" + serviceInstance.getPort());// 打印当前调用服务的信息
        
        //        restTemplate.getForEntity("http://"
        //                + SERVIER_NAME + "/queryContents", String.class).getBody();
        List<ConsultContent> contents = userService.queryContents();
        
        logger.info(JSONObject.toJSONString(contents));
        return userService.queryContents();
    }
    
    @RequestMapping("/getResult")
    public void getResult() {
        Observable<String> observer = userService.getResult();
        final List<String> result = new ArrayList<String>();
        observer.subscribe(new Observer<String>() {
            
            public void onCompleted() {
                logger.info(JSONObject.toJSONString(result));
                logger.info("==========onCompleted be invoke===========");
            }
            
            public void onError(Throwable e) {
                logger.info("==========onError be invoke==========="
                        + e.getMessage());
            }
            
            public void onNext(String t) {
                result.add(t);
                logger.info(Thread.currentThread().getName()
                        + "==========onNext be invoke===========" + t);
            }
        });
    }
    
    @RequestMapping("/serverInfo")
    public List<String> serverInfo() {
        List<String> lists = new ArrayList<String>();
        String description = disClient.description();
        logger.info(description);
        lists.add(description);
        List<ServiceInstance> sis = disClient.getInstances(SERVIER_NAME);
        if (sis != null) {
            for (ServiceInstance si : sis) {
                String host = si.getHost();
                Map<String, String> metadatas = si.getMetadata();
                int port = si.getPort();
                String serviceId = si.getServiceId();
                URI uri = si.getUri();
                boolean isSecure = si.isSecure();
                String siStr = "host:" + host + "<-->" + "metadata:"
                        + metadatas + "<-->" + "port:" + port + "<-->"
                        + "serviceId:" + serviceId + "<-->" + "isSecure:"
                        + isSecure;
                logger.info(siStr);
                lists.add(siStr);
            }
        }
        ServiceInstance localsi = disClient.getLocalServiceInstance();
        if (localsi != null) {
            String host = localsi.getHost();
            Map<String, String> metadatas = localsi.getMetadata();
            int port = localsi.getPort();
            String serviceId = localsi.getServiceId();
            URI uri = localsi.getUri();
            boolean isSecure = localsi.isSecure();
            String siStr = "host:" + host + "<-->" + "metadata:" + metadatas
                    + "<-->" + "port:" + port + "<-->" + "serviceId:"
                    + serviceId + "<-->" + "isSecure:" + isSecure;
            logger.info("LocalServiceInstance:" + siStr);
            lists.add(siStr);
        }
        List<String> services = disClient.getServices();
        if (services != null) {
            for (String service : services) {
                logger.info(service);
                lists.add(service);
            }
        }
        return lists;
    }
    
    @RequestMapping("/hystrixObservableCommand")
    public String hystrixObservableCommand() {
        final List<String> list = new ArrayList<String>();
        UserHystrixObservableCommand uhoc = new UserHystrixObservableCommand();
        //创建事件源  hot Observable
        Observable<String> observe = uhoc.observe();
        //注册观察者
        observe.subscribe(new Observer<String>() {
            
            public void onCompleted() {
                logger.info(Thread.currentThread().getName()
                        + "-------observer onCompleted");
                logger.info(JSONObject.toJSONString(list));
            }
            
            public void onError(Throwable e) {
                logger.info(Thread.currentThread().getName()
                        + "-------observer onError");
            }
            
            public void onNext(String t) {
                logger.info(Thread.currentThread().getName()
                        + "-------observer onNext");
                list.add(t);
            }
        });
        try {
            Thread.sleep(4000);
        }
        catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        UserHystrixObservableCommand uhoc1 = new UserHystrixObservableCommand();
        Observable<String> observe1 = uhoc1.observe();
        observe1.subscribe(new Subscriber<String>() {
            
            public void onCompleted() {
                logger.info(Thread.currentThread().getName()
                        + "-------observer Subscriber2 onCompleted");
                logger.info(JSONObject.toJSONString(list));
            }
            
            public void onError(Throwable e) {
                logger.info(Thread.currentThread().getName()
                        + "-------observer Subscriber2 onError");
            }
            
            public void onNext(String t) {
                logger.info(Thread.currentThread().getName()
                        + "-------observer Subscriber2 onNext");
                list.add(t);
            }
        });
        return "OK";
    }
    
    @RequestMapping("/hystrixObservableCommand1")
    public String hystrixObservableCommand1() {
        final List<String> list = new ArrayList<String>();
        UserHystrixObservableCommand uhoc = new UserHystrixObservableCommand();
        Observable<String> observe = uhoc.observe();
        observe.subscribe(new Observer<String>() {
            
            public void onCompleted() {
                logger.info(Thread.currentThread().getName()
                        + "-------observer1 onCompleted");
                logger.info(JSONObject.toJSONString(list));
            }
            
            public void onError(Throwable e) {
                logger.info(Thread.currentThread().getName()
                        + "-------observer1 onError");
            }
            
            public void onNext(String t) {
                logger.info(Thread.currentThread().getName()
                        + "-------observer1 onNext");
                list.add(t);
            }
        });
        return "OK";
    }
    
    /** 
     *  往期视频加小露老师QQ：1533922121
     *  Jack老师QQ： 2943489129
     *  时间   ：     2018年1月26日 下午2:56:22 
     *  作者   ：   烛光学院【Jack老师】
     *  cold Observable
     */
    @RequestMapping("/hystrixObservableCommand2")
    public String hystrixObservableCommand2() {
        final List<String> list = new ArrayList<String>();
        UserHystrixObservableCommand uhoc = new UserHystrixObservableCommand();
        Observable<String> observe = uhoc.toObservable();
        observe.subscribe(new Observer<String>() {
            
            public void onCompleted() {
                logger.info(Thread.currentThread().getName()
                        + "-------observer1 onCompleted");
                logger.info(JSONObject.toJSONString(list));
            }
            
            public void onError(Throwable e) {
                logger.info(Thread.currentThread().getName()
                        + "-------observer1 onError");
            }
            
            public void onNext(String t) {
                logger.info(Thread.currentThread().getName()
                        + "-------observer1 onNext");
                list.add(t);
            }
        });
        return "OK";
    }
    
    @RequestMapping("/hystrixCommand")
    public String hystrixCommandTest() {
        UserhystrixCommand user = new UserhystrixCommand("sync-jack");
        String result = user.execute();
        logger.info(Thread.currentThread().getName() + "----------execute:"
                + result);
        
        UserhystrixCommand user1 = new UserhystrixCommand("aync-jack");
        Future<String> queue = user1.queue();
        try {
            String string = queue.get(9000, TimeUnit.MILLISECONDS);
            logger.info(Thread.currentThread().getName() + "----------queue:"
                    + string);
        }
        catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "OK";
    }
    
    /** 
     *  往期视频加瑶瑶老师QQ：2483034688
     *  Jack老师QQ： 2943489129
     *  时间   ：     2018年2月7日 下午12:34:56 
     *  作者   ：   烛光学院【Jack老师】
     *  
     *  注解 @HystrixCommand的 Observeable测试
     */
    @RequestMapping("/hystrixAnnotationTest")
    public String hystrixAnnotationTest() {
        Observable<String> observer = userService.getResult();
        
        final List<String> result = new ArrayList<String>();
        observer.subscribe(new Observer<String>() {
            
            public void onCompleted() {
                logger.info(JSONObject.toJSONString(result));
                logger.info("==========onCompleted be invoke===========");
            }
            
            public void onError(Throwable e) {
                
            }
            
            public void onNext(String t) {
                result.add(t);
                logger.info("==========onNext be invoke===========" + t);
            }
        });
        
        return "OK";
    }
}
