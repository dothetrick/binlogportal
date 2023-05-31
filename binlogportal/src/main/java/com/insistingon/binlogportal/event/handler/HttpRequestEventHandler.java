package com.insistingon.binlogportal.event.handler;

import com.insistingon.binlogportal.BinlogPortalException;
import com.insistingon.binlogportal.event.EventEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * 默认的http请求类，使用固定的参数格式，发送post请求
 */
public class HttpRequestEventHandler implements IEventHandler {
    private static final Logger log = LoggerFactory.getLogger(HttpRequestEventHandler.class);

    CloseableHttpClient httpClient = HttpClients.custom().setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(1000).build())
            .setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(1000).setConnectionRequestTimeout(1000).build()).build();

    List<String> urlList = new ArrayList<>();

    IHttpCallback httpCallback;

    public List<String> getUrlList() {
        return urlList;
    }

    public void setUrlList(List<String> urlList) {
        this.urlList = urlList;
    }

    public void registerUrl(String url) {
        urlList.add(url);
    }

    public IHttpCallback getHttpCallback() {
        return httpCallback;
    }

    public void setHttpCallback(IHttpCallback httpCallback) {
        this.httpCallback = httpCallback;
    }

    @Override
    public void process(EventEntity eventEntity) throws BinlogPortalException {
        post(eventEntity.getJsonFormatData());
    }

    private void post(String param) throws BinlogPortalException {
        for (String url : urlList) {
            try {
                HttpPost httpPost = new HttpPost(url);
                StringEntity requestEntity = new StringEntity(param, "UTF-8");
                requestEntity.setContentEncoding("UTF-8");
                httpPost.setHeader("Content-type", "application/json");
                httpPost.setEntity(requestEntity);
                CloseableHttpResponse response = httpClient.execute(httpPost);
                //设置了回调实现，调用回调处理返回结果
                if (httpCallback != null) {
                    httpCallback.call(response);
                } else {
                    String res = EntityUtils.toString(response.getEntity());
                    if (response.getStatusLine().getStatusCode() != 200) {
                        log.error("http request failed.url:{},params:{},code:{}", url, param, response.getStatusLine().getStatusCode());
                        throw new BinlogPortalException("http request failed.code:" + response.getStatusLine().getStatusCode());
                    }
                    log.info("http request success.url:{},params:{},res:{}", url, param, res);
                }
                //response要关闭，不然httpClient无法复用，实际上是断开长连接
                response.close();
            } catch (IOException e) {
                throw new BinlogPortalException(e);
            }
        }
    }
}
