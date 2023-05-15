package org.tkit.app.document.management.bff.ManualProxies;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.RequestOptions;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProxyHandler implements Handler<RoutingContext> {
    private final String baseUrl;
    private HttpClient httpClient;

    private ProxyHandler(HttpClient client, String baseUrl) {
        this.httpClient = client;
        this.baseUrl = baseUrl;
    }

    public static ProxyHandler create(HttpClient httpClient, String baseUrl) {
        return new ProxyHandler(httpClient, baseUrl);
    }

    @Override
    public void handle(RoutingContext routingContext) {
        HttpServerRequest serverRequest = routingContext.request();
        log.info("Proxying request {} {} to {}", serverRequest.method().name(), serverRequest.uri(),
                this.baseUrl + serverRequest.uri());
        HttpServerResponse serverResponse = serverRequest.response();
        httpClient.request(new RequestOptions()
                .setAbsoluteURI(this.baseUrl + serverRequest.uri())
                .setMethod(serverRequest.method()))
                .onSuccess(clientRequest -> {
                    clientRequest.headers().setAll(serverRequest.headers().remove("Host"));
                    clientRequest.send(serverRequest).onSuccess(clientResponse -> {
                        log.info("Proxying {} response from {} to the client.", clientResponse.statusCode(),
                                clientRequest.absoluteURI());
                        serverResponse.setStatusCode(clientResponse.statusCode());
                        serverResponse.headers().setAll(clientResponse.headers());
                        serverResponse.send(clientResponse);
                    }).onFailure(err -> {
                        log.error("Backend failure", err);
                        serverResponse.setStatusCode(500).end();
                    });
                }).onFailure(err -> {
                    log.error("Could not connect to server {}", baseUrl, err);
                    serverResponse.setStatusCode(500).end();
                });
    }
}