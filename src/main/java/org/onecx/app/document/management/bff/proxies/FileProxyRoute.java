package org.onecx.app.document.management.bff.proxies;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.tkit.quarkus.log.cdi.LogExclude;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class FileProxyRoute {
    private final String proxyPath;
    private final String baseUrl;
    private final HttpClient httpClient;

    FileProxyRoute(
            Vertx vertx,
            @ConfigProperty(name = "documentmgmt/mp-rest/url") String processUrl,
            @ConfigProperty(name = "dms.file-upload.proxy-path") String path) {
        this.baseUrl = processUrl;
        this.httpClient = vertx.createHttpClient(new HttpClientOptions());
        this.proxyPath = path;
    }

    public void init(@Observes @LogExclude Router router) {
        log.info("Adding proxy route for logo upload endpoint path {} with a proxy forward to {}", proxyPath, baseUrl);
        router.route(proxyPath).handler(ProxyHandler.create(httpClient, baseUrl));
    }

}