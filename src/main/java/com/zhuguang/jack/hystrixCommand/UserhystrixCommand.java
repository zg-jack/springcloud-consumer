package com.zhuguang.jack.hystrixCommand;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy;
import com.netflix.hystrix.HystrixThreadPoolKey;

public class UserhystrixCommand extends HystrixCommand<String> {
    Logger logger = LoggerFactory.getLogger(getClass());
    
    private final String name;
    
    public UserhystrixCommand(String name) {
        //最少配置:指定命令组名(CommandGroup)  
        //        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
        super(
                Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("hystrixGroupkey"))
                        .andCommandKey(HystrixCommandKey.Factory.asKey("hystrixCommandKey"))
                        .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                                .withExecutionIsolationStrategy(ExecutionIsolationStrategy.THREAD))
                        .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("jackPoolkey")));
        this.name = name;
    }
    
    @Override
    protected String getFallback() {
        logger.info(Thread.currentThread().getName() + "--------getFallback");
        return "fast fail!!";
    }
    
    @Override
    protected String run() throws Exception {
        
        logger.info("Hello " + name + " thread:"
                + Thread.currentThread().getName());
        TimeUnit.MILLISECONDS.sleep(4000);
        logger.info("xx67657");
        return "Hello " + name + " thread:" + Thread.currentThread().getName();
    }
}
