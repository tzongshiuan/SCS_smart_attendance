package com.gorilla.attendance.enterprise.util;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Created by ggshao on 2017/2/9.
 */

public class HttpHandler {
    private static final String TAG = "HttpHandler";

    private String mUrl = "";
    private List<NameValuePair> mParams = null;

    private final int TIMEOUT_VALUE = 60000;
    private final int HTTP_STATUS_SUCCESS = 200;

    public HttpHandler(String url, List<NameValuePair> params) {
        mUrl = url;
        mParams = params;

    }

    public String getResponse() {
        return getResponseByGet();
    }

    public String getResponseByPostJsonBody() {
        HttpPost post = new HttpPost(mUrl);
        HttpResponse responsePOST = null;
        HttpClient client = MySSLSocketFactory.createMyHttpClient();
        String strResult = null;

        if (mUrl.length() == 0 || mParams == null) {
            LOG.W(TAG, "[getHtmlByPost] mUrl = " + mUrl + ", mParams = " + mParams);
            return null;
        }

        String apiString = "";
        long beforeTime = System.currentTimeMillis();
        //LOG.V(TAG, "getResponseByPost() - beforeTime is "+beforeTime);

        try {
            JSONObject json = new JSONObject();
            for(int i = 0 ; i < mParams.size() ; i++){
                if(mParams.get(i).getName().equals("records") || mParams.get(i).getName().equals("errorLogs") || mParams.get(i).getName().equals("imageList")){
                    JSONArray jsonArray = new JSONArray(mParams.get(i).getValue());
                    LOG.D(TAG,"mParams.get(i).getValue() = " + mParams.get(i).getValue());
                    json.put(mParams.get(i).getName(), jsonArray);
                }else{
                    json.put(mParams.get(i).getName(), mParams.get(i).getValue());
                }
            }

            //JSON物件放到POST Request
            StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");

            stringEntity.setContentType("application/json");
            post.setEntity(stringEntity);

//            post.setEntity(new UrlEncodedFormEntity(mParams, HTTP.UTF_8));
            LOG.V(TAG, "[getResponseByPostJsonBody] request= " + mUrl + convertStreamToString(post.getEntity().getContent()));
            apiString = mUrl + convertStreamToString(post.getEntity().getContent());
            // post.setHeader("Content-Type", "application/x-www-form-urlencoded;Charset=UTF-8");
            // HttpConnectionParams.setConnectionTimeout(httcl.getParams(), TIMEOUT_VALUE);
            // HttpConnectionParams.setSoTimeout(httcl.getParams(), TIMEOUT_VALUE);
//            post.addHeader("Accept-Encoding", "gzip");
            post.addHeader("Content-Type", "application/json");
            responsePOST = client.execute(post);

            if (responsePOST.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream instream = responsePOST.getEntity().getContent();
                Header contentEncoding = responsePOST.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    instream = new GZIPInputStream(instream);
                }
                strResult = IOUtils.toString(instream, "UTF-8");
            } else {
                LOG.E(TAG, "[getResponseByPostJsonBody] HTTP Request Fail, " + responsePOST.getStatusLine().getStatusCode());
                InputStream instream = responsePOST.getEntity().getContent();
                Header contentEncoding = responsePOST.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    instream = new GZIPInputStream(instream);
                }
                strResult = IOUtils.toString(instream, "UTF-8");

            }
        } catch (Exception e) {
            LOG.E(TAG, "[getHtmlByPost] Exception occurs!", e);
        } finally {
            client.getConnectionManager().shutdown();
        }

        long afterTime = System.currentTimeMillis();
        //LOG.V(TAG, "getResponseByPost() - afterTime is "+afterTime);

        LOG.D(TAG, "[API_PERFORMANCE] : " + (afterTime-beforeTime) +"ms");

        LOG.V(TAG, "[getHtmlByPost] HTTP Request Completed " + strResult);

        return strResult;
    }

    public String getResponseByPost() {
        HttpPost post = new HttpPost(mUrl);
        HttpResponse responsePOST = null;
        HttpClient client = MySSLSocketFactory.createMyHttpClient();
        String strResult = null;

        if (mUrl.length() == 0 || mParams == null) {
            LOG.W(TAG, "[getHtmlByPost] mUrl = " + mUrl + ", mParams = " + mParams);
            return null;
        }

        String apiString = "";
        long beforeTime = System.currentTimeMillis();
        //LOG.V(TAG, "getResponseByPost() - beforeTime is "+beforeTime);

        try {
            post.setEntity(new UrlEncodedFormEntity(mParams, HTTP.UTF_8));
            LOG.V(TAG, "[getHtmlByPost] request= " + mUrl + convertStreamToString(post.getEntity().getContent()));
            apiString = mUrl + convertStreamToString(post.getEntity().getContent());
            // post.setHeader("Content-Type", "application/x-www-form-urlencoded;Charset=UTF-8");
            // HttpConnectionParams.setConnectionTimeout(httcl.getParams(), TIMEOUT_VALUE);
            // HttpConnectionParams.setSoTimeout(httcl.getParams(), TIMEOUT_VALUE);
            post.addHeader("Accept-Encoding", "gzip");
            responsePOST = client.execute(post);

            if (responsePOST.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream instream = responsePOST.getEntity().getContent();
                Header contentEncoding = responsePOST.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    instream = new GZIPInputStream(instream);
                }
                strResult = IOUtils.toString(instream, "UTF-8");
            } else {
                LOG.E(TAG, "[getHtmlByPost] HTTP Request Fail, " + responsePOST.getStatusLine().getStatusCode());
                InputStream instream = responsePOST.getEntity().getContent();
                Header contentEncoding = responsePOST.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    instream = new GZIPInputStream(instream);
                }
                strResult = IOUtils.toString(instream, "UTF-8");
            }
        } catch (Exception e) {
            LOG.E(TAG, "[getHtmlByPost] Exception occurs!", e);
        } finally {
            client.getConnectionManager().shutdown();
        }

        long afterTime = System.currentTimeMillis();
        //LOG.V(TAG, "getResponseByPost() - afterTime is "+afterTime);

        LOG.D(TAG, "[API_PERFORMANCE] "+apiString+" : "+(afterTime-beforeTime) +"ms");

        LOG.V(TAG, "[getHtmlByPost] HTTP Request Completed " + strResult);

        return strResult;
    }

    private String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }

    public String getResponseByGet() {
        HttpGet get = null;
        HttpResponse responseGET = null;
        HttpClient client = MySSLSocketFactory.createMyHttpClient();
        String strResult = null;

        if (mUrl.length() == 0 || mParams == null) {
            LOG.W(TAG, "[getHtmlByGet] mUrl = " + mUrl + ", mParams = " + mParams);
            return null;
        }


        long beforeTime = System.currentTimeMillis();
        //LOG.V(TAG, "getResponseByGet() - beforeTime is "+beforeTime);

        try {
            String paramString = URLEncodedUtils.format(mParams, HTTP.UTF_8);
            mUrl += paramString;
            LOG.W(TAG, "[getHtmlByGet] mUrl = " + mUrl);

            get = new HttpGet(mUrl);
            //LOG.V(TAG, "Http Get request= " + mUrl);
            // get.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            // get.setHeader("Content-Type", "application/x-www-form-urlencoded;Charset=UTF-8");
            get.addHeader("Accept-Encoding", "gzip");

//            HttpConnectionParams.setConnectionTimeout(client.getParams(), TIMEOUT_VALUE);
//            HttpConnectionParams.setSoTimeout(client.getParams(), TIMEOUT_VALUE);

            responseGET = client.execute(get);

            if (responseGET.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream instream = responseGET.getEntity().getContent();
                Header contentEncoding = responseGET.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    instream = new GZIPInputStream(instream);
                }
                strResult = IOUtils.toString(instream, "UTF-8");
            } else {
                LOG.E(TAG, "[getHtmlByGet] HTTP Request Fail, " + responseGET.getStatusLine().getStatusCode());
                if(mUrl.indexOf("login") > -1 || mUrl.indexOf("V1_1beta/user?") > -1 || mUrl.indexOf("user/employee-register?") > -1 ||
                        mUrl.indexOf("user/visitor-register?") > -1 || mUrl.indexOf("V1_1beta/employee?") > -1 || mUrl.indexOf("V1_1beta/visitor?") > -1){
                    InputStream instream = responseGET.getEntity().getContent();
                    Header contentEncoding = responseGET.getFirstHeader("Content-Encoding");
                    if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                        instream = new GZIPInputStream(instream);
                    }
                    strResult = IOUtils.toString(instream, "UTF-8");
                }

            }
        } catch (Exception e) {
            LOG.E(TAG, "[getHtmlByGet] Exception occurs!", e);
        } finally {
            client.getConnectionManager().shutdown();
        }
        long afterTime = System.currentTimeMillis();
        //LOG.V(TAG, "getResponseByGet() - afterTime is "+afterTime);

        LOG.D(TAG, "[API_PERFORMANCE] "+mUrl+" : "+(afterTime-beforeTime) +"ms");
        LOG.V(TAG, "[getHtmlByGet] HTTP Request Completed " + strResult);

        return strResult;
    }


    public String getResponseByPostUrl() {
        HttpPost post = null;
        HttpResponse responsePOST = null;
        HttpClient client = MySSLSocketFactory.createMyHttpClient();
        String strResult = null;

        if (mUrl.length() == 0 || mParams == null) {
            LOG.W(TAG, "[getResponseByPostUrl] mUrl = " + mUrl + ", mParams = " + mParams);
            return null;
        }

        String apiString = "";
        long beforeTime = System.currentTimeMillis();
        //LOG.V(TAG, "getResponseByPost() - beforeTime is "+beforeTime);

        try {
            String paramString = URLEncodedUtils.format(mParams, HTTP.UTF_8);
            mUrl += paramString;
            LOG.W(TAG, "[getResponseByPostUrl] mUrl = " + mUrl);
            post = new HttpPost(mUrl);


//            LOG.V(TAG, "[getHtmlByPost] request= " + mUrl + convertStreamToString(post.getEntity().getContent()));
            // post.setHeader("Content-Type", "application/x-www-form-urlencoded;Charset=UTF-8");
            // HttpConnectionParams.setConnectionTimeout(httcl.getParams(), TIMEOUT_VALUE);
            // HttpConnectionParams.setSoTimeout(httcl.getParams(), TIMEOUT_VALUE);
            post.addHeader("Accept-Encoding", "gzip");
            post.addHeader("Content-Type", "x-www-form-urlencoded");
            responsePOST = client.execute(post);


            LOG.W(TAG, "[getResponseByPostUrl] postMessageLimited responsePOST = " + responsePOST);

            if (responsePOST.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream instream = responsePOST.getEntity().getContent();
                Header contentEncoding = responsePOST.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    instream = new GZIPInputStream(instream);
                }
                strResult = IOUtils.toString(instream, "UTF-8");
            } else {
                LOG.E(TAG, "[getHtmlByPost] HTTP Request Fail, " + responsePOST.getStatusLine().getStatusCode());
                InputStream instream = responsePOST.getEntity().getContent();
                Header contentEncoding = responsePOST.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    instream = new GZIPInputStream(instream);
                }
                strResult = IOUtils.toString(instream, "UTF-8");
            }
        } catch (Exception e) {
            LOG.E(TAG, "[getHtmlByPost] Exception occurs!", e);
        } finally {
            client.getConnectionManager().shutdown();
        }

        long afterTime = System.currentTimeMillis();
        //LOG.V(TAG, "getResponseByPost() - afterTime is "+afterTime);

        LOG.D(TAG, "postMessageLimited [API_PERFORMANCE]  : "+(afterTime-beforeTime) +" ms");

        LOG.V(TAG, "postMessageLimited [getHtmlByPost] HTTP Request Completed " + strResult);

        return strResult;
    }

}
