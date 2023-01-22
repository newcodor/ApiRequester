package com.newcodor.apirequester.bean;

import java.util.List;
import java.util.Map;

public class HttpResponse {
    public int staticCode;
    public String responseText;

    public Map<String, List<String>> headers;
    public HttpResponse(int staticCode,String responseText,Map headers){
        this.staticCode =staticCode;
        this.responseText = responseText;
        this.headers =headers;
    }
}
