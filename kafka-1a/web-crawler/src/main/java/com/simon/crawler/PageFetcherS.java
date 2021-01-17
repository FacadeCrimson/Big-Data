package com.simon.crawler;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;

public class PageFetcherS extends PageFetcher {
public PageFetcherS(CrawlConfig config) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
    super(config);

    RequestConfig DEFAULT_REQUEST_CONFIG = RequestConfig.custom().setConnectTimeout(config.getConnectionTimeout())
        .setSocketTimeout(config.getSocketTimeout()).build();
    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setMaxTotal(config.getMaxTotalConnections());
    connectionManager.setDefaultMaxPerRoute(config.getMaxConnectionsPerHost());

    SSLContext sslContext = new SSLContextBuilder()
              .loadTrustMaterial(null, (certificate, authType) -> true).build();

    httpClient = HttpClients.custom()
              .setSSLContext(sslContext)
              .setSSLHostnameVerifier(new NoopHostnameVerifier())
              .setConnectionManager(connectionManager)
              .setUserAgent(config.getUserAgentString())
              .setDefaultRequestConfig(DEFAULT_REQUEST_CONFIG)
              .build();
}

}
