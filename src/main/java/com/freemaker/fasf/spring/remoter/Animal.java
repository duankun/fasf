package com.freemaker.fasf.spring.remoter;

import com.freemaker.fasf.annotation.GetParam;
import com.freemaker.fasf.annotation.Remoter;
import com.freemaker.fasf.annotation.RequestLine;
import com.freemaker.fasf.http.HttpMethod;
import com.freemaker.fasf.interceptor.AuthInterceptor;


@Remoter(endpoint = "http://www.baidu.com", interceptors = {AuthInterceptor.class})
public interface Animal {

    @RequestLine(path = "/remote")
    String remote(String param);

    @RequestLine(path = "/get", method = HttpMethod.GET)
    String get(@GetParam("userName")String userName);
}
