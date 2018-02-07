package com.zhuguang.jack.service;

import java.util.List;
import java.util.concurrent.Future;

import rx.Observable;

import com.zhuguang.jack.bean.ConsultContent;

public interface UserService {
    List<ConsultContent> queryContents();
    
    public Observable<String> getResult();
    
    public Future<String> getResultAsync();
}
