package com.newcodor.apirequester.bean;

import java.util.HashMap;

public class HttpRequest {

    public String method;

    public String Host;

    public String protocol = "http";
    public String uri = "/";
    public String url;


    public HashMap<String,String> headers;
    public String body;

    public HttpRequest() {
    }

    public HttpRequest(String method, String url, HashMap headers, String body){
        this.method = method;
        this.url = url;
        this.headers = headers;
        this.body = body;
    }

    public String getUrl(){
        return this.protocol+"://"+this.Host+this.uri;
    }
    @Override
    public String toString() {
        return "HttpRequest{" +
                "method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }
}
