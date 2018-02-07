package com.zhuguang.jack.test;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class HelloWorldCommand extends HystrixCommand<String> {
    
    private final String name;
    
    public HelloWorldCommand(String name) {
        //最少配置:指定命令组名(CommandGroup)  
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
        this.name = name;
    }
    
    @Override
    protected String run() throws Exception {
        // 依赖逻辑封装在run()方法中  
        return "Hello " + name + " thread:" + Thread.currentThread().getName();
    }
    
    //调用实例  
    public static void main(String[] args) throws Exception {
        //每个Command对象只能调用一次,不可以重复调用,  
        //重复调用对应异常信息:This instance can only be executed once. Please instantiate a new instance.  
        HelloWorldCommand helloWorldCommand = new HelloWorldCommand(
                "Synchronous-hystrix");
        //使用execute()同步调用代码,效果等同于:helloWorldCommand.queue().get();   
        String result = helloWorldCommand.execute();
        System.out.println("result=" + result);
        
        helloWorldCommand = new HelloWorldCommand("Asynchronous-hystrix");
        //异步调用,可自由控制获取结果时机,  
        Future<String> future = helloWorldCommand.queue();
        //get操作不能超过command定义的超时时间,默认:1秒  
        result = future.get(100, TimeUnit.MILLISECONDS);
        System.out.println("result=" + result);
        System.out.println("mainThread=" + Thread.currentThread().getName());
    }
    
}
