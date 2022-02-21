package base2app.network.ssl;

import base2app.BaseApplication;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Objects;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SSLHelper {

    /**
     * 获取X509TrustManager
     */
    public static X509TrustManager getX509TrustManager() {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
            }
            X509TrustManager trustManager = (X509TrustManager) trustManagers[0];
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            return trustManager;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    /**
//     * 获取SSLSocketFactory
//     */
//    public static SSLSocketFactory getSSLSocketFactory() {
//        try {
//            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
//            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            keyStore.load(null);
//            InputStream is = Objects.requireNonNull(BaseApplication.Companion.getContext()).getAssets().open("server-cert.cer");
//            keyStore.setCertificateEntry("0", certificateFactory.generateCertificate(is));
//            if (is != null) {
//                is.close();
//            }
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//            trustManagerFactory.init(keyStore);
//
//            // 初始化双向客户端keyStore
//            KeyStore clientKeyStore = KeyStore.getInstance("BKS");
//            clientKeyStore.load(Objects.requireNonNull(BaseApplication.Companion.getContext()).getAssets().open("client.bks"), "zr@666".toCharArray());
//            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//            keyManagerFactory.init(clientKeyStore, "zr@666".toCharArray());
//            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
//            return sslContext.getSocketFactory();
//        } catch (CertificateException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException | IOException | UnrecoverableKeyException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

//    public static SSLSocketFactory getSSLCertifcation() {
//        SSLSocketFactory sslSocketFactory = null;
//        try {
//            // 服务器端需要验证的客户端证书，其实就是客户端的keystore
//            KeyStore keyStore = KeyStore.getInstance("BKS");// 客户端信任的服务器端证书
//            KeyStore trustStore = KeyStore.getInstance("BKS");//读取证书
//            InputStream ksIn = Objects.requireNonNull(BaseApplication.Companion.getContext()).getAssets().open("client");
//            InputStream tsIn = Objects.requireNonNull(BaseApplication.Companion.getContext()).getAssets().open("truststore_s_for_c");//加载证书
//            keyStore.load(ksIn, "zr@666".toCharArray());
//            trustStore.load(tsIn, "zr@666".toCharArray());
//            ksIn.close();
//            tsIn.close();
//            //初始化
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("X509");
//            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509");
//            trustManagerFactory.init(trustStore);
//            keyManagerFactory.init(keyStore, "zr@666".toCharArray());
//            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
//            sslSocketFactory = sslContext.getSocketFactory();
//        } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException | UnrecoverableKeyException | KeyManagementException e) {
//            e.printStackTrace();
//        }
//        return sslSocketFactory;
//    }

    public static SSLSocketFactory getSSLCertifcation() {
        SSLSocketFactory sslSocketFactory = null;
        // 服务器端需要验证的客户端证书，其实就是客户端的keystore
        InputStream ksIn = null;
        try {
            KeyStore clientKey = KeyStore.getInstance("BKS");// 客户端信任的服务器端证书
            ksIn = Objects.requireNonNull(BaseApplication.Companion.getContext()).getAssets().open("client.bks");
            clientKey.load(ksIn, "Zrkj!1201".toCharArray());
            ksIn.close();
            //初始化SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X.509");

            keyManagerFactory.init(clientKey, "Zrkj!1201".toCharArray());
            keyManagerFactory.init(clientKey, "Zrkj!1201".toCharArray());

            // 默认客户端验证
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagers, new java.security.SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException | KeyManagementException | UnrecoverableKeyException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ksIn != null) {
                    ksIn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sslSocketFactory;
    }
}
