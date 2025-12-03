package org.fasf.mqyz.api;

import org.fasf.core.annotation.Api;
import org.fasf.core.annotation.QueryParam;
import org.fasf.core.annotation.RequestMapping;
import org.fasf.core.http.HttpMethod;
import org.fasf.mqyz.model.vo.mele.LoginInfo;
import org.fasf.mqyz.model.vo.mele.MeleResult;

@Api(endpoint = "${fasf.api.mele.endpoint}")
public interface MeleApi {

    @RequestMapping(path = "/open/queryUserBySessionId", method = HttpMethod.GET)
    MeleResult<LoginInfo> queryUserBySessionId(@QueryParam("sessionId") String sessionId);
}
