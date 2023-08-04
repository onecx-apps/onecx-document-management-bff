package org.onecx.app.document.management.bff.proxies;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.vertx.http.runtime.QuarkusHttpHeaders;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.http.*;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
@Slf4j
public class ProxyHandlerTest {

  private ProxyHandler proxyHandler;
  private final String baseUrl = "http://test.de";
  private final String uri = "/api/resource";
  @Mock
  private HttpClient httpClient;

  @BeforeEach
  public void setUp() {
    httpClient = spy(HttpClient.class);
    proxyHandler = ProxyHandler.create(httpClient, baseUrl);
  }

  @Test
  public void testOnSuccessHandle() {
    //given
    RoutingContext routingContext = spy(RoutingContext.class);
    HttpServerRequest inputRequest = mock(HttpServerRequest.class);
    HttpServerResponse proxyResponse = mock(HttpServerResponse.class);
    HttpClientRequest proxyRequest = mock(HttpClientRequest.class);

    MultiMap initialRequestHeaders = new QuarkusHttpHeaders();
    initialRequestHeaders.add(HttpHeaderNames.HOST, "host123");
    initialRequestHeaders.add(HttpHeaderNames.USER_AGENT, "agent123");

    MultiMap expectedRequestHeaders = new QuarkusHttpHeaders().add(HttpHeaderNames.USER_AGENT, "agent123");
    MultiMap proxyResponseHeaders = new QuarkusHttpHeaders().add(HttpHeaderNames.ACCEPT, "accept");

    when(routingContext.request()).thenReturn(inputRequest);
    when(inputRequest.method()).thenReturn(HttpMethod.GET);
    when(inputRequest.uri()).thenReturn(uri);
    when(inputRequest.headers()).thenReturn(initialRequestHeaders);

    when(inputRequest.response()).thenReturn(proxyResponse);

    when(proxyRequest.headers()).thenReturn(mock(MultiMap.class));
    when(proxyRequest.absoluteURI()).thenReturn(baseUrl + uri);
    when(httpClient.request(any(RequestOptions.class))).thenReturn(Future.succeededFuture(proxyRequest));

    HttpClientResponse clientResponse = mock(HttpClientResponse.class);
    when(clientResponse.statusCode()).thenReturn(200);
    when(clientResponse.headers()).thenReturn(proxyResponseHeaders);
    when(proxyResponse.headers()).thenReturn(mock(MultiMap.class));
    when(proxyRequest.send(inputRequest)).thenReturn(Future.succeededFuture(clientResponse));

    //when
    proxyHandler.handle(routingContext);

    //then
    verify(proxyRequest.headers(), times(1)).setAll(eq(expectedRequestHeaders));
    verify(proxyResponse, times(1)).setStatusCode(200);
    verify(proxyResponse.headers(), times(1)).setAll(proxyResponseHeaders);
    verify(proxyResponse, times(1)).send(clientResponse);
  }
}