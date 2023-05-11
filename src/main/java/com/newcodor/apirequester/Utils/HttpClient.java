package com.newcodor.apirequester.Utils;


import com.newcodor.apirequester.bean.HttpResponse;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

public class HttpClient {
    public static ArrayList<String>  allowMethod =new ArrayList<>();
    public static Proxy globalProxy = Proxy.NO_PROXY;
    public final static String defaultCharset = "UTF-8";
    static {
        allowMethod.addAll(Arrays.asList("GET","POST","HEAD","PUT","DELETE","TRACE","OPTIONS"));
    }
    public  static boolean isHttps(String url){
        return url.startsWith("https");
    }

    private static HttpURLConnection getHttpConn(String _url, Proxy _proxy) throws Exception {
        URL url = new URL(_url);
        Proxy proxy =globalProxy;
        if(_proxy != null){
            proxy = _proxy;
        }
        HttpURLConnection conn;
        conn = (HttpURLConnection) url.openConnection(proxy);
        return conn;
    }

    private static HttpsURLConnection getHttpsConn(String _url, Proxy _proxy) throws Exception {
        URL url = new URL(_url);
        Proxy proxy = globalProxy;
        if(_proxy != null){
            proxy = _proxy;
        }
        HttpsURLConnection conn ;
        conn = (HttpsURLConnection) url.openConnection(proxy);

        TrustManager[] trustManagers = {new HttpsTrustManager()};
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, trustManagers, new SecureRandom());
        ((HttpsURLConnection) conn).setSSLSocketFactory(context.getSocketFactory());
        ((HttpsURLConnection) conn).setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        return conn;
    }

//    public static HashMap getProxyItemFromString(String _proxy) throws  Exception{
//        HashMap proxyItem = null;
//        Proxy proxy =null;
//        _proxy = _proxy.toLowerCase().trim();
//        if (!_proxy.equals("")  && _proxy.length() > 0) {
//            proxyItem = new HashMap();
//            if (_proxy.startsWith("http")) {
//                proxyItem.put("protocol", Proxy.Type.HTTP);
//            } else if (_proxy.startsWith("sock")) {
//                proxyItem.put("protocol", Proxy.Type.SOCKS);
//            } else {
//                proxyItem.put("protocol", Proxy.Type.DIRECT);
//                proxyItem.put("address", null);
////                return proxyItem;
//            }
//            if (_proxy.contains("/")) {
//                String[] proxyTmp = proxy.split("/")[2].split(":");
//                String hostname = proxyTmp[0];
//                int port = Integer.parseInt(proxyTmp[1]);
//                proxyItem.put("address", new InetSocketAddress(hostname, port));
//            } else {
//                proxyItem.put("address", null);
//            }
//        }
//        return proxyItem;
//    }

    public static String getBodyFromConn(HttpURLConnection conn,String encoding) throws IOException {
        String body = "";
        BufferedReader bufferedReader = null;
        InputStreamReader in = null;
        if (conn != null && conn.getContentLength() != 0) {
            if (conn.getResponseCode() >= 400) {
                in = new InputStreamReader(conn.getErrorStream(), encoding);
            } else {
                in = new InputStreamReader(conn.getInputStream(), encoding);
            }
            bufferedReader = new BufferedReader(in);

            StringBuffer bodys = new StringBuffer();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                bodys.append(line);
                bodys.append("\r\n");
            }
            body = bodys.toString();
        }
        return  body;
    }
    public static HttpResponse request(String method, String url, int timeout, @Nullable Proxy proxy, @Nullable HashMap<String,String> headers, @Nullable Object body) throws  Exception{
        if(!allowMethod.contains(method)){
            throw new Exception("UnSupported HTTP Method: "+method);
        }
        HttpURLConnection conn = null;
        if(isHttps(url)){
            conn = getHttpsConn(url,proxy);
        }else{
            conn =getHttpConn(url,proxy);
        }
        conn.setRequestMethod(method);
        conn.setConnectTimeout(timeout*1000);
        conn.setReadTimeout(timeout*1000);
        conn.setDoOutput(true);
        if(null!=headers){
            for(Map.Entry<String,String>  entry : headers.entrySet()){
                if(entry.getKey().equals("Content-Type") && !conn.getRequestMethod().equals("POST")){
                    continue;
                }
                conn.setRequestProperty(entry.getKey(),entry.getValue());
            }
        }
        if(conn.getRequestMethod().equals("POST") || conn.getRequestMethod().equals("PUT")){
            conn.setDoInput(true);
            if(body!=null){
                if(body instanceof String){
                    OutputStream out = conn.getOutputStream();
                    out.write(((String)body).getBytes("UTF-8"));
                    out.flush();
                    out.close();
                } else if (body instanceof Byte) {
                    OutputStream out = conn.getOutputStream();
                    out.write((byte[]) body);
                    out.flush();
                    out.close();
                }
            }
        }
        String charset = defaultCharset;
        Map<String, List<String>> responseHeaders = null;
        HttpResponse response =null;
        conn.connect();
        responseHeaders = conn.getHeaderFields();
        if(responseHeaders.containsKey("Content-Type") && responseHeaders.get("Content-Type").get(0).toLowerCase().contains("charset=gbk")){
            charset="GBK";
        }
        response = new HttpResponse(conn.getResponseCode(),getBodyFromConn(conn,charset), responseHeaders);

        return response;
    }

}

class HttpsTrustManager implements X509TrustManager {
    private static TrustManager[] trustManagers = {new HttpsTrustManager()};

    @Override
    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        // TODO Auto-generated method stub
    }

    @Override
    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        // TODO Auto-generated method stub
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        // TODO Auto-generated method stub
        return null;
    }

    public static void allowAllSSL() {
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, trustManagers, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}