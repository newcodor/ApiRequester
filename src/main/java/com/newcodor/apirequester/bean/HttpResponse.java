package com.newcodor.apirequester.bean;

import java.util.List;
import java.util.Map;

public class HttpResponse {
    public int staticCode;
    public String responseText;

    private long responseTime;

    private int contentSize;

    public Map<String, List<String>> headers;
    public HttpResponse(int staticCode,String responseText,Map headers){
        this.staticCode =staticCode;
        this.responseText = responseText;
        this.headers =headers;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    public int getContentSize() {
        return contentSize;
    }

    public void setContentSize(int contentSize) {
        this.contentSize = contentSize;
    }
}
