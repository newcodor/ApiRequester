package com.newcodor.apirequester.bean;

import java.util.HashMap;

public class HttpRequest {

    public String method;
    public String url;
    public HashMap<String,String> headers;
    public String content;

    public HttpRequest(String method,String url,HashMap headers,String content){
        this.method = method;
        this.url = url;
        this.headers = headers;
        this.content = content;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", headers=" + headers +
                ", content='" + content + '\'' +
                '}';
    }
}
