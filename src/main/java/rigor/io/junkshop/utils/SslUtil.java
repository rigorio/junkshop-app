package rigor.io.junkshop.utils;

import okhttp3.OkHttpClient;

import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

public class SslUtil {

  public static OkHttpClient getSslOkHttpClient() {
    try {
      // Create a trust manager that does not validate certificate chains
      final TrustManager[] trustAllCerts = new TrustManager[]{
          new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
              return new java.security.cert.X509Certificate[]{};
            }
          }
      };

      // Install the all-trusting trust manager
      final SSLContext sslContext = SSLContext.getInstance("SSL");
      sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
      // Create an ssl socket factory with our all-trusting manager
      final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
      return new OkHttpClient.Builder()
          .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
          .hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
              return true;
            }
          })
          .connectTimeout(30, TimeUnit.SECONDS)
          .writeTimeout(30, TimeUnit.SECONDS)
          .readTimeout(30, TimeUnit.SECONDS).build();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
