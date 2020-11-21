package com.insistingon.binlogportal.autoconfig;

import java.util.List;

public class HttpHandlerConfig {
    private List<String> urlList;
    private String resultCallback;

    public List<String> getUrlList() {
        return urlList;
    }

    public void setUrlList(List<String> urlList) {
        this.urlList = urlList;
    }

    public String getResultCallback() {
        return resultCallback;
    }

    public void setResultCallback(String resultCallback) {
        this.resultCallback = resultCallback;
    }
}
