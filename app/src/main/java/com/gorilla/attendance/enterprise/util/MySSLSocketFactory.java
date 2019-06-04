package com.gorilla.attendance.enterprise.util;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by ggshao on 2017/2/7.
 */

public class MySSLSocketFactory extends SSLSocketFactory
{
    SSLContext mSSLContext = SSLContext.getInstance("TLS");

//    private static final int HTTP_TIME_OUT = 15000;//15 seconds

    @Override
    public Socket createSocket() throws IOException
    {
        return mSSLContext.getSocketFactory().createSocket();
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException
    {
        return mSSLContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException
    {
        super(truststore);

        TrustManager mTrustManager = new X509TrustManager()
        {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
            {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
            {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers()
            {
                return null;
            }
        };
        mSSLContext.init(null, new TrustManager[] { mTrustManager }, null);
    }

    public static HttpClient createMyHttpClient()
    {
        try
        {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load( null, null);

            SSLSocketFactory mSSLSocketFactory = new MySSLSocketFactory(trustStore);
            mSSLSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", mSSLSocketFactory, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            LOG.D("GGGGGG", "createMyHttpClient DeviceUtils.SYNC_TIME_OUTDeviceUtils.SYNC_TIME_OUTDeviceUtils.SYNC_TIME_OUT = " + DeviceUtils.SYNC_TIME_OUT);
            HttpConnectionParams.setConnectionTimeout(params, DeviceUtils.SYNC_TIME_OUT);
            HttpConnectionParams.setSoTimeout(params, DeviceUtils.SYNC_TIME_OUT);

            DefaultHttpClient client = new DefaultHttpClient(ccm, params);
            /*
            client.addRequestInterceptor(new HttpRequestInterceptor() {


				@Override
				public void process(HttpRequest request, HttpContext context)
						throws HttpException, IOException {
					if (!request.containsHeader("Accept-Encoding")) {
	            	      request.addHeader("Accept-Encoding", "gzip");

				}
				}});

            	client.addResponseInterceptor(new HttpResponseInterceptor() {
            	  public void process(HttpResponse response, HttpContext context) {
            	    // Inflate any responses compressed with gzip
            	    final HttpEntity entity = response.getEntity();
            	    final Header encoding = entity.getContentEncoding();
            	    if (encoding != null) {
            	      for (HeaderElement element : encoding.getElements()) {
            	        if (element.getName().equalsIgnoreCase("gzip")) {
            	          response.setEntity(new InflatingEntity(response.getEntity()));
            	          break;
            	        }
            	      }
            	    }
            	  }
            	});
            */
            return client;
        }
        catch (KeyStoreException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (CertificateException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (KeyManagementException e)
        {
            e.printStackTrace();
        }
        catch (UnrecoverableKeyException e)
        {
            e.printStackTrace();
        }

        return new DefaultHttpClient();
    }

    public static HttpClient createMyHttpClient(int timeOut)
    {
        try
        {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load( null, null);

            SSLSocketFactory mSSLSocketFactory = new MySSLSocketFactory(trustStore);
            mSSLSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", mSSLSocketFactory, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            HttpConnectionParams.setConnectionTimeout(params, timeOut);
            HttpConnectionParams.setSoTimeout(params, timeOut);

            DefaultHttpClient client = new DefaultHttpClient(ccm, params);
            /*
            client.addRequestInterceptor(new HttpRequestInterceptor() {


				@Override
				public void process(HttpRequest request, HttpContext context)
						throws HttpException, IOException {
					if (!request.containsHeader("Accept-Encoding")) {
	            	      request.addHeader("Accept-Encoding", "gzip");

				}
				}});

            	client.addResponseInterceptor(new HttpResponseInterceptor() {
            	  public void process(HttpResponse response, HttpContext context) {
            	    // Inflate any responses compressed with gzip
            	    final HttpEntity entity = response.getEntity();
            	    final Header encoding = entity.getContentEncoding();
            	    if (encoding != null) {
            	      for (HeaderElement element : encoding.getElements()) {
            	        if (element.getName().equalsIgnoreCase("gzip")) {
            	          response.setEntity(new InflatingEntity(response.getEntity()));
            	          break;
            	        }
            	      }
            	    }
            	  }
            	});
            */
            return client;
        }
        catch (KeyStoreException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (CertificateException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (KeyManagementException e)
        {
            e.printStackTrace();
        }
        catch (UnrecoverableKeyException e)
        {
            e.printStackTrace();
        }

        return new DefaultHttpClient();
    }
}
