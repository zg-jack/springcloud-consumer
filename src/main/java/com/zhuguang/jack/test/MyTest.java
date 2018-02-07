package com.zhuguang.jack.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import rx.Observable;
import rx.Observer;

import com.alibaba.fastjson.JSONObject;
import com.zhuguang.jack.hystrixCommand.UserhystrixCommand;
import com.zhuguang.jack.hystrixObservableCommand.UserHystrixObservableCommand;
import com.zhuguang.jack.service.UserService;
import com.zhuguang.jack.start.SpringBootSampleApplication;

@RunWith(SpringJUnit4ClassRunner.class)
//这个注解必须告诉junit，springboot的启动类是哪一个
@SpringBootTest(classes = SpringBootSampleApplication.class)
//这个是junit需要模拟一个ServletContext
@WebAppConfiguration
public class MyTest {
    
    Logger logger = LoggerFactory.getLogger(getClass());
    
    CountDownLatch cdl = new CountDownLatch(count);
    
    public static Integer count = 1;
    
    @Autowired
    UserService userService;
    
    /** 
     *  往期视频加小露老师QQ：1533922121
     *  Jack老师QQ： 2943489129
     *  时间   ：     2018年1月26日 下午5:06:18 
     *  作者   ：   烛光学院【Jack老师】
     *  
     *  HystrixObservableCommand 命名模式
    线程池占满的情况下，请求拒绝，调用了降级方法
     */
    @Test
    public void test1() {
        final List<String> list = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            new Thread(new Runnable() {
                
                public void run() {
                    try {
                        cdl.await();
                        UserHystrixObservableCommand uhoc = new UserHystrixObservableCommand();
                        Observable<String> observe = uhoc.observe();
                        observe.subscribe(new Observer<String>() {
                            
                            public void onCompleted() {
                                logger.info(JSONObject.toJSONString(list));
                                logger.info("==========onCompleted be invoke===========");
                            }
                            
                            public void onError(Throwable e) {
                                logger.info("==========onError be invoke==========="
                                        + e.getMessage());
                            }
                            
                            public void onNext(String t) {
                                list.add(t);
                                logger.info(Thread.currentThread().getName()
                                        + "==========onNext be invoke==========="
                                        + t);
                            }
                        });
                    }
                    catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }).start();
            cdl.countDown();
            
        }
    }
    
    @Test
    public void test2() {
        final List<String> list = new ArrayList<String>();
        for (int i = 0; i < 15; i++) {
            new Thread(new Runnable() {
                
                public void run() {
                    try {
                        cdl.await();
                        UserhystrixCommand uhoc = new UserhystrixCommand("jack");
                        uhoc.execute();
                    }
                    catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }).start();
            cdl.countDown();
            
        }
    }
    
    @Test
    public void test3() {
        
        for (int i = 0; i < count; i++) {
            new Thread(new Runnable() {
                
                public void run() {
                    try {
                        cdl.await();
                    }
                    catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    logger.info("==========userService.getResult()==========="
                            + Thread.currentThread().getName());
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
                                    + "==========onNext be invoke==========="
                                    + t);
                        }
                    });
                }
                
            }).start();
            cdl.countDown();
        }
    }
    
    @Test
    public void test4() {
        UserhystrixCommand userhystrixCommand = new UserhystrixCommand("jack");
        Future<String> queue = userhystrixCommand.queue();
        logger.info("********************------------");
        try {
            String string = queue.get(9000, TimeUnit.MILLISECONDS);
            logger.info(string);
            logger.info("23432431111111-------------");
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
    }
    
    @Test
    public void test5() {
        Future<String> resultAsync = userService.getResultAsync();
        logger.info("99999999999999999999999966666666666666666");
        
        try {
            logger.info(resultAsync.get(9000, TimeUnit.MILLISECONDS));
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
    }
    
    @Test
    public void test6() {
        logger.info(JSONObject.toJSONString(userService.queryContents()));
    }
}
