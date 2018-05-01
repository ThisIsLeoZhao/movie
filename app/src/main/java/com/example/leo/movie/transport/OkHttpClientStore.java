package com.example.leo.movie.transport;

import com.example.leo.movie.BuildConfig;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okio.Utf8;

public class OkHttpClientStore {
    private static final OkHttpClient BASE_OK_HTTP_CLIENT = new OkHttpClient();
    static final OkHttpClient MOVIE_HTTP_CLIENT = getMovieHttpClient(BASE_OK_HTTP_CLIENT);
    static final OkHttpClient AUTH_HTTP_CLIENT = getUnsafeAuthHttpClient(BASE_OK_HTTP_CLIENT);

    private static OkHttpClient getMovieHttpClient(final OkHttpClient client) {
        return client.newBuilder().addInterceptor(chain -> {
            Request original = chain.request();
            HttpUrl originalHttpUrl = original.url();

            HttpUrl url = originalHttpUrl.newBuilder()
                    .addQueryParameter("api_key", BuildConfig.MY_MOVIE_DB_API_KEY)
                    .build();

            return chain.proceed(original.newBuilder().url(url).build());
        }).build();
    }

    private static OkHttpClient getUnsafeAuthHttpClient(final OkHttpClient client) {
        // Create a trust manager that does not validate certificate chains
        final X509TrustManager trustManager;
        try {
            trustManager = trustManager(trustedCertInputStream());
        } catch (CertificateException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | IOException e) {
            throw new RuntimeException(e);
        }

        // Install the all-trusting trust manager
        try {
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] { trustManager }, new SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            return client.newBuilder()
                    .sslSocketFactory(sslSocketFactory, trustManager)
                    .hostnameVerifier((hostname, session) -> true).build();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Cannot find SSL protocol in SSLContext", e);
        } catch (KeyManagementException e) {
            throw new RuntimeException("Key corrupted", e);
        }
    }

    private static X509TrustManager trustManager(InputStream inputStream)
            throws CertificateException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, IOException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(inputStream);
        if(certificates.isEmpty()) {
            throw new IllegalArgumentException("Invalid certificate");
        }

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        int index = 0;
        for (Certificate certificate : certificates) {
            keyStore.setCertificateEntry(Integer.toString(index++), certificate);
        }

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
        }

        return (X509TrustManager) trustManagers[0];
    }

    private static InputStream trustedCertInputStream() {
        final String certs = "-----BEGIN CERTIFICATE-----\n" +
                "MIIDVjCCAj4CCQCk9OZS6tF7QzANBgkqhkiG9w0BAQsFADBtMQswCQYDVQQGEwJV\n" +
                "UzEPMA0GA1UECAwGT3JlZ29uMREwDwYDVQQHDAhQb3J0bGFuZDEVMBMGA1UECgwM\n" +
                "Q29tcGFueSBOYW1lMQwwCgYDVQQLDANPcmcxFTATBgNVBAMMDDE5Mi4xNjguMC4x\n" +
                "MDAeFw0xODA1MDExODI0MzlaFw0xOTA1MDExODI0MzlaMG0xCzAJBgNVBAYTAlVT\n" +
                "MQ8wDQYDVQQIDAZPcmVnb24xETAPBgNVBAcMCFBvcnRsYW5kMRUwEwYDVQQKDAxD\n" +
                "b21wYW55IE5hbWUxDDAKBgNVBAsMA09yZzEVMBMGA1UEAwwMMTkyLjE2OC4wLjEw\n" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuZypHoyn7Iv01yp0cPMK\n" +
                "HKJi2vt/dQO2YV2zLz1aNSIKm1SYw3sfBeYuLg5TLztcyjHeUaYuLU7pQlVFXV6w\n" +
                "Bvm0/2nsHU5OyqhGFuzNGMTGphKZDaiK4+3SSQF8y1lgl4yoL2DDAirOFwu7Xd0s\n" +
                "9JVQOEfI2J6kfKAY32/1UzTVHbNfp9SWwMwez4EbhPmydGrKXDiGsUD0p4okUGR1\n" +
                "noG5uXFS7Ln9ioM6unRQxAH1mk19qMnq7n020pEEWqOpKX+vfY7frfsml1H2pjmg\n" +
                "Q3V6vHHy0XU4DPoKsexpShM/AlwxSXl3OAi8A29KiWUpU1IX3/fp1k78Nmjo6Ara\n" +
                "iwIDAQABMA0GCSqGSIb3DQEBCwUAA4IBAQCGSNgT0Q5nGFakaeMwo3EIr0fe/hIR\n" +
                "BP+YaWV283XnsshENpK6pnsIczA3WQhJ1qLiHEwN0TEZwM4MeptJZRa25MrIjtZq\n" +
                "OLNrBkarnA9YoB2fgqdGmaaUvXurKDj1APjBdrz7hMZ2QzKRbp6eEDYdPZCxKmyZ\n" +
                "J5I6TQL1k0kMojcYw1OWmsK8GvexheCFFSjJOnn0Wx8vFFPyUhX8Vm69h0WWPYBZ\n" +
                "IkTI1v65jIyrGhYSgVPJCO+C0ZM386rikQI1WUo/g3jaa/OtC+SCGCtnzGFI9/zI\n" +
                "Xz87ElfZ1wbPvDEoomkHQGMSqnCbdTKf9q6iiJ3rpGvQ/eTsOcgM22DK\n" +
                "-----END CERTIFICATE-----\n";

        try {
            return new ByteArrayInputStream(certs.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
