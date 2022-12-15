package com.imlaidian.okhttp.utils;

import android.net.SSLCertificateSocketFactory;
import android.os.Build;

import com.imlaidian.utilslibrary.utils.LogUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SniSSLSocketFactory extends SSLSocketFactory {
    private HttpsURLConnection conn;

    public SniSSLSocketFactory(HttpsURLConnection conn) {
        this.conn = conn;
    }

    @Override
    public Socket createSocket() throws IOException {
        return null;
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return null;
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        return null;
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return null;
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return null;
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return new String[0];
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return new String[0];
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
        String mHost = this.conn.getRequestProperty("Host");
        if (mHost == null) {
            mHost = host;
        }
        LogUtil.i("WGGetHostByName", "customized createSocket host is: " + mHost);
        InetAddress address = socket.getInetAddress();
        if (autoClose) {
            socket.close();
        }
        SSLCertificateSocketFactory sslSocketFactory = (SSLCertificateSocketFactory) SSLCertificateSocketFactory.getDefault(0);
        SSLSocket ssl = (SSLSocket) sslSocketFactory.createSocket(address, port);
        ssl.setEnabledProtocols(ssl.getSupportedProtocols());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            LogUtil.d("WGGetHostByName", "Setting SNI hostname");
            sslSocketFactory.setHostname(ssl, mHost);
        } else {
            LogUtil.d("WGGetHostByName", "No documented SNI support on Android <4.2, trying with reflection");
            try {
                java.lang.reflect.Method setHostnameMethod = ssl.getClass().getMethod("setHostname", String.class);
                setHostnameMethod.invoke(ssl, mHost);
            } catch (Exception e) {
                LogUtil.s("WGGetHostByName", "SNI not useable", e);
            }
        }
        // verify hostname and certificate
        SSLSession session = ssl.getSession();
        HostnameVerifier mHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
        if (!mHostnameVerifier.verify(mHost, session))
            throw new SSLPeerUnverifiedException("Cannot verify hostname: " + mHost);
        LogUtil.d("WGGetHostByName",
                "Established " + session.getProtocol() + " connection with " + session.getPeerHost() + " using " + session.getCipherSuite());
        return ssl;
    }
}