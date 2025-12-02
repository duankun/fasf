package org.fasf.mqyz.api;

import org.fasf.core.annotation.Api;
import org.fasf.core.annotation.Interceptors;
import org.fasf.core.annotation.RequestMapping;
import org.fasf.core.http.HttpMethod;
import org.fasf.mqyz.interceptor.EnergyAuthorizationRequestInterceptor;
import org.fasf.mqyz.interceptor.EnergyDecryptResponseInterceptor;
import org.fasf.mqyz.interceptor.EnergyEncryptRequestInterceptor;
import org.fasf.mqyz.model.ro.TrendRO;
import org.fasf.mqyz.model.vo.EnergyResult;
import org.fasf.mqyz.model.vo.MonthEnergyConsumption;

import java.util.List;

@Api(endpoint = "${fasf.api.energy.endpoint}")
@Interceptors(requestInterceptors = {EnergyAuthorizationRequestInterceptor.class, EnergyEncryptRequestInterceptor.class}, responseInterceptor = EnergyDecryptResponseInterceptor.class)
public interface EnergyApi {
    @RequestMapping(path = "/energy/energy/api/energy/analyse/consumption/getTrend", method = HttpMethod.POST)
    EnergyResult<List<MonthEnergyConsumption>> getTrend(TrendRO ro);
}
