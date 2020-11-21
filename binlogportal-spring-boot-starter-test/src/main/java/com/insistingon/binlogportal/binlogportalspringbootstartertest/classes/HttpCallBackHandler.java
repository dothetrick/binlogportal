package com.insistingon.binlogportal.binlogportalspringbootstartertest.classes;

import com.insistingon.binlogportal.event.handler.IHttpCallback;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpCallBackHandler implements IHttpCallback {
    public void call(CloseableHttpResponse closeableHttpResponse) {
        log.info(closeableHttpResponse.toString());
    }
}
