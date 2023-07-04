package org.tkit.app.document.management.bff.proxies;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.tkit.quarkus.log.cdi.LogExclude;

import io.quarkus.logging.Log;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class FileProxyRoute {
    @ConfigProperty(name = "dms.file-upload.proxy-path")
    private static String proxyPath;

    private final String baseUrl;
    private final HttpClient httpClient;

    FileProxyRoute(
            Vertx vertx,
            @ConfigProperty(name = "documentmgmt/mp-rest/url") String processUrl) {
        this.baseUrl = processUrl;
        this.httpClient = vertx.createHttpClient(new HttpClientOptions());
    }

    public void init(@Observes @LogExclude Router router) {
        Log.info("Adding proxy route for logo upload endpoint path " + proxyPath + " with a proxy forward to " + baseUrl);
        router.route(proxyPath).handler(ProxyHandler.create(httpClient, baseUrl));
    }

}
