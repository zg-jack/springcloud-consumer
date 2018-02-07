package com.zhuguang.jack.hystrixObservableCommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;

public class UserHystrixObservableCommand extends
        HystrixObservableCommand<String> {
    
    Logger logger = LoggerFactory.getLogger(getClass());
    
    public UserHystrixObservableCommand() {
        super(HystrixCommandGroupKey.Factory.asKey("usercommand"));// 调用父类构造方法  
    }
    
    @Override
    protected Observable<String> construct() {
        logger.info(Thread.currentThread().getName()
                + "---------construct invoke.....");
        return Observable.create(new OnSubscribe<String>() {
            
            public void call(Subscriber<? super String> observer) {
                logger.info(Thread.currentThread().getName()
                        + " call invoke...");
                
                try {
                    for (int i = 0; i < 10; i++) {
                        logger.info(Thread.currentThread().getName()
                                + "onNext invoke---------------------" + i);
                        observer.onNext(String.valueOf(i));
                        //                        Thread.sleep(1000);
                    }
                    logger.info(Thread.currentThread().getName()
                            + "onCompleted invoke---------------------");
                    observer.onCompleted();
                    
                }
                catch (Exception e) {
                    logger.info(Thread.currentThread().getName()
                            + "onError invoke---------------------");
                    observer.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }
    
    @Override
    protected Observable<String> resumeWithFallback() {
        logger.info(Thread.currentThread().getName()
                + "---------resumeWithFallback.....");
        return Observable.just("fast fail!");
    }
    
}
