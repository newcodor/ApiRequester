package com.newcodor.apirequester.bean;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class HttpRequest {

    private String method = "GET";

    private String Host = "";

    private String protocol = "http";
    private String protocolVersion = "HTTP/1.1";
    private String uri = "/";
    private String url = "";


    private HashMap<String,String> headers = new HashMap<String,String>(){{
        put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36");
        put("Accept","*/*");
        put("Accept-Encoding","gzip, deflate");
    }};
    private String body = "";

    public HttpRequest() {
    }

    public HttpRequest(String method, String url, HashMap<String,String> headers, String body){
        this.setMethod(method);
        this.setUrl(url);
        this.setHeaders(headers);
        this.setBody(body);
    }

    public String getProtocol() {
        return this.protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
        this.url = this.getUrlCombined();
    }

    public String getProtocolVersion() {
        return this.protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getHost() {
        return this.Host;
    }


    public void setHost(String host) {
        this.Host = host;
        this.url = this.getUrlCombined();
    }


    public String getUri() {
        return this.uri;
    }


    public void setUri(String uri) {
        this.uri = uri;
        this.url = this.getUrlCombined();
    }

    public  String  getBody(){
        return this.body;
    }

    public  void setBody(String body){
        this.body = body;
    }

    public String getUrl(){
        return this.url;
    }

    public String getUrlCombined(){
        return this.getProtocol() +"://"+ this.getHost() + this.getUri();
    }

    public  static String getUriFromUrl(String url){
        System.out.println(url);
        return "/"+url.split("/",4)[3];
    }
    public  static String getHostFromUrl(String url){
        return url.split("/",4)[2];
    }

    public  static String getProtocolFromUrl(String url){
        return url.split(":",2)[0];
    }

    public  void  setUrl(String url){
        this.url = url;
        this.Host = getHostFromUrl(this.url);
        this.uri = getUriFromUrl(this.url);
        this.protocol = getProtocolFromUrl(this.url);
    }


    public  String  getMethod(){
        return this.method;
    }

    public  void  setMethod(String method){
        this.method = method;
    }

    public  HashMap<String,String> getHeaders(){
        return this.headers;
    }

    public  void setHeaders(HashMap<String,String> headers){
        this.headers=headers;
    }

    public  void setHeadersByKV(String key, String value){
        this.getHeaders().put(key,value);
    }
    @Override
    public String toString() {
        return "HttpRequest{" +
                "method='" + getMethod() + '\'' +
                ", url='" + getUrl() + '\'' +
                ", headers=" + getHeaders() +
                ", body='" + getBody() + '\'' +
                '}';
    }


    public static HttpRequest StringToRequest(String req) {
        HttpRequest request = new HttpRequest();
        String[] item = req.split("\n");
        Iterator items = Arrays.stream(item).iterator();
        String line = "";
        String[] headerItem;
        if (items.hasNext()) {
            headerItem = items.next().toString().split(" ");
            request.setMethod(headerItem[0].trim());
            request.setUri(headerItem[1].trim());
            request.setProtocolVersion(headerItem[2].trim());
        } else {
            return null;
        }
        request.setHeaders(new HashMap());
        while (items.hasNext()) {
            line = items.next().toString().trim();
            if (line.isEmpty()) {
                break;
            } else {
                headerItem = line.split(":", 2);
                request.getHeaders().put(headerItem[0].trim(), headerItem[1].trim());
                if (headerItem[0].trim().toLowerCase().equals("host")) {
                    request.setHost(headerItem[1].trim());
                    if (request.getHost().endsWith(":443") || request.getHost().endsWith(":8443")) {
                        request.setProtocol("https");
                    }
                } else if (headerItem[0].trim().toLowerCase().equals("accept-encoding")) {
                    if(headerItem[1].trim().contains("gzip,")){
                        request.getHeaders().put(headerItem[0].trim(), "gzip, deflate");
                    }
                }
            }

        }

        StringBuilder sb = new StringBuilder();
        while (items.hasNext()) {
            sb.append(items.next().toString().trim());
        }
        request.setBody(sb.toString());
//        for (String i:item
//             ) {
//            System.out.println(i);
//        }
        return request;
    }

    public static String  RequestToString(HttpRequest request) {

        StringBuilder sb = new StringBuilder();
        sb.append(request.getMethod());
        sb.append(" ");
        sb.append(request.getUri());
        sb.append(" ");
        sb.append(request.getProtocolVersion());
        sb.append("\n");
        if(request.getHeaders()!=null){
            ((HashMap<String,String>)request.getHeaders()).entrySet().stream().forEach((header) -> {
                sb.append(header.getKey());
                sb.append(": ");
                sb.append(header.getValue());
                sb.append("\n");
            });
        }
        sb.append("\n");
        if(request.getBody()!=null){
            sb.append(request.getBody());
        }
        return sb.toString();
    }

    public static HttpRequest CurlToRequest(String curlCmd) {
        HttpRequest request = new HttpRequest();
        String[] item = curlCmd.split(":\\\\");
        Iterator items = Arrays.stream(item).iterator();
        String line = "";
        String[] headerItem;
        if (items.hasNext()) {
            headerItem = items.next().toString().split(" ");
            if (headerItem[0].trim() == "curl") {
                request.setUrl(headerItem[1].trim());
                request.setHeaders(new HashMap());
                while (items.hasNext()) {
                    line = items.next().toString().trim();
                    if (line.isEmpty()) {
                        break;
                    } else {
                        headerItem = line.split(":", 2);
                        for (String i:headerItem
                        ) {
                            System.out.println(i);
                        }
//                        request.headers.put(headerItem[0].trim(), headerItem[1].trim());
//                        if (headerItem[0].trim().toLowerCase().equals("host")) {
//                            request.Host = headerItem[1].trim();
//                            if (request.Host.endsWith(":443") || request.Host.endsWith(":8443")) {
//                                request.protocol = "https";
//                            }
//                            request.url = request.getUrl();
//                        }
                    }

                }
//                StringBuilder sb = new StringBuilder();
//                while (items.hasNext()) {
//                    sb.append(items.next().toString().trim() + "\r\n");
//                }
//                request.body = sb.toString();
//                System.out.println(request.toString());
                //        for (String i:item
                //             ) {
                //            System.out.println(i);
                //        }
//                return request;
            }
        }
        return null;
    }

}
