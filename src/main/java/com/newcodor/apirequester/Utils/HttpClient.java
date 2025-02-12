package com.newcodor.apirequester.Utils;


import com.newcodor.apirequester.bean.HttpRequest;
import com.newcodor.apirequester.bean.HttpResponse;
import org.jetbrains.annotations.Nullable;

import javax.jws.soap.SOAPBinding;
import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

public class HttpClient {
    public static ArrayList<String> allowMethod = new ArrayList<>();
    public static Proxy globalProxy = Proxy.NO_PROXY;
    public final static String defaultCharset = "UTF-8";

    static {
        allowMethod.addAll(Arrays.asList("GET", "POST", "HEAD", "PUT", "DELETE", "TRACE", "OPTIONS"));
    }

    public static boolean isHttps(String url) {
        return url.startsWith("https");
    }

    private static HttpURLConnection getHttpConn(String _url, Proxy _proxy) throws Exception {
        URL url = new URL(_url);
        Proxy proxy = globalProxy;
        if (_proxy != null) {
            proxy = _proxy;
        }
        HttpURLConnection conn;
        conn = (HttpURLConnection) url.openConnection(proxy);
        return conn;
    }

    private static HttpsURLConnection getHttpsConn(String _url, Proxy _proxy) throws Exception {
        URL url = new URL(_url);
        Proxy proxy = globalProxy;
        if (_proxy != null) {
            proxy = _proxy;
        }
        HttpsURLConnection conn;
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

    public static String getBodyFromConn(HttpURLConnection conn, String encoding, String zipMethod) throws IOException {
        String body = "";
        BufferedReader bufferedReader = null;
        InputStream inputStream = null;
        InputStreamReader in = null;
        if (conn != null && conn.getContentLength() != 0) {
            if (conn.getResponseCode() >= 400) {
                inputStream = conn.getErrorStream();
            } else {
                inputStream = conn.getInputStream();
            }

            if (!zipMethod.equals("")) {
                if(zipMethod.equals("gzip")){
                    body = GzipDecoder.decompressGzip(inputStream);
                }
            } else {
                in = new InputStreamReader(inputStream, encoding);
                bufferedReader = new BufferedReader(in);

                StringBuffer bodys = new StringBuffer();
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    bodys.append(line);
                    bodys.append("\r\n");
                }
                body = bodys.toString();
            }

        }
        return body;
    }

    public static HttpResponse request(String method, String url, int timeout, @Nullable Proxy proxy, @Nullable HashMap<String, String> headers, @Nullable Object body) throws Exception {
        if (!allowMethod.contains(method)) {
            throw new Exception("UnSupported HTTP Method: " + method);
        }
        //allow restricted http header: Host .....
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");

        HttpURLConnection conn = null;
        String zipMehtod = "";
        if (isHttps(url)) {
            conn = getHttpsConn(url, proxy);
        } else {
            conn = getHttpConn(url, proxy);
        }

        conn.setRequestMethod(method);
        conn.setConnectTimeout(timeout * 1000);
        conn.setReadTimeout(timeout * 1000);
        conn.setDoOutput(true);
        if (null != headers) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                if (entry.getKey().equals("Content-Type") && !conn.getRequestMethod().equals("POST")) {
                    continue;
                }
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        if (conn.getRequestMethod().equals("POST") || conn.getRequestMethod().equals("PUT")) {
            conn.setDoInput(true);
            if (body != null) {
                if (body instanceof String) {
                    OutputStream out = conn.getOutputStream();
                    out.write(((String) body).getBytes("UTF-8"));
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
        HttpResponse response = null;
        long startTime = System.currentTimeMillis();
        conn.connect();
        responseHeaders = conn.getHeaderFields();
        if (responseHeaders.containsKey("Content-Type") && responseHeaders.get("Content-Type").get(0).toLowerCase().contains("charset=gbk")) {
            charset = "GBK";
        }
        if (responseHeaders.containsKey("Content-Encoding")) {
//            isGzip = true;
            zipMehtod = responseHeaders.get("Content-Encoding").get(0).trim();
        } else if (responseHeaders.containsKey("content-encoding")) {
            zipMehtod = responseHeaders.get("content-encoding").get(0).trim();
        }
        response = new HttpResponse(conn.getResponseCode(), getBodyFromConn(conn, charset, zipMehtod), responseHeaders);
        if(headers.containsKey("Connection") && headers.get("Connection").equals("close")){
//            System.out.println("close socket");
            conn.disconnect();
        }else{
//            System.out.println("keep alive");
            conn.getInputStream().close();
        }
        long endTime = System.currentTimeMillis();
        long timeSpent = endTime - startTime;
        response.setResponseTime(timeSpent);
//        System.out.println(timeSpent);
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