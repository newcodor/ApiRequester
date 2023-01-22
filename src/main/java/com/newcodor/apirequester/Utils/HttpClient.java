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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpClient {
    public static ArrayList<String>  allowMethod =new ArrayList<>();
    public static Proxy globalProxy = Proxy.NO_PROXY;
    static {
        allowMethod.addAll(Arrays.asList("GET","POST"));
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
        BufferedReader bufferedReader =  null;
        if(conn!=null && (conn.getResponseCode() == 200)) {
            bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(),encoding));

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
            System.out.println(conn);
        }else{
            conn =getHttpConn(url,proxy);
        }
        conn.setRequestMethod(method);
        conn.setConnectTimeout(timeout*1000);
        conn.setReadTimeout(timeout*1000);
        conn.setDoOutput(true);
        if(null!=headers){
            for(Map.Entry<String,String>  entry : headers.entrySet()){
                if(entry.getKey().equals("Content-Type") && conn.getRequestMethod().equals("GET")){
                    continue;
                }
                conn.setRequestProperty(entry.getKey(),entry.getValue());
            }
        }
        if(conn.getRequestMethod().equals("POST")){
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
        conn.connect();
        HttpResponse response = new HttpResponse(conn.getResponseCode(),getBodyFromConn(conn,"UTF-8"), conn.getHeaderFields());
        System.out.println(response.staticCode);
        conn.disconnect();
        return  response;
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