package com.freemaker.fasf.remoter;

import com.freemaker.fasf.annotation.GetParam;
import com.freemaker.fasf.annotation.Interceptors;
import com.freemaker.fasf.annotation.Remoter;
import com.freemaker.fasf.annotation.Request;
import com.freemaker.fasf.http.HttpMethod;
import com.freemaker.fasf.interceptor.AuthInterceptor;


@Remoter(endpoint = "http://www.baidu.com")
public interface Animal {

    @Request(path = "/remote")
    @Interceptors(interceptors = {AuthInterceptor.class})
    String remote(String param);

    @Request(path = "/get", method = HttpMethod.GET)
    @Interceptors(interceptors = {AuthInterceptor.class})
    String get(@GetParam("userName")String userName);
}
