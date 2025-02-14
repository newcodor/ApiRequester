package com.newcodor.apirequester.bean;

import java.util.List;
import java.util.Map;

public class HttpResponse {
    public int staticCode;
    private String responseBody;

    private long responseTime;

    private int contentSize;

    public Map<String, List<String>> headers;
    public HttpResponse(int staticCode,String responseBody,Map headers){
        this.staticCode =staticCode;
        this.setResponseBody(responseBody);
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

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
}
