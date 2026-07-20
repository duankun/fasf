package org.fasf.mqyz.api;

import org.fasf.core.annotation.Api;
import org.fasf.core.annotation.Interceptors;
import org.fasf.core.annotation.RequestBody;
import org.fasf.core.annotation.RequestMapping;
import org.fasf.core.http.HttpMethod;
import org.fasf.mqyz.model.ro.TrendRO;
import org.fasf.mqyz.model.vo.energy.EnergyResult;
import org.fasf.mqyz.model.vo.energy.MonthEnergyConsumption;
import org.fasf.sctel.interceptor.AuthorizationRequestInterceptor;
import org.fasf.sctel.interceptor.DecryptResponseInterceptor;
import org.fasf.sctel.interceptor.EncryptRequestInterceptor;

import java.util.List;

@Api(endpoint = "${fasf.api.energy.endpoint}")
@Interceptors(requestInterceptors = {AuthorizationRequestInterceptor.class, EncryptRequestInterceptor.class}, responseInterceptor = DecryptResponseInterceptor.class)
public interface EnergyApi {
    @RequestMapping(path = "/energy/energy/api/energy/analyse/consumption/getTrend", method = HttpMethod.POST)
    EnergyResult<List<MonthEnergyConsumption>> getTrend(@RequestBody TrendRO ro);
}
