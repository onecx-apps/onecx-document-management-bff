import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.impl.future.PromiseImpl;
import io.vertx.core.impl.future.PromiseInternal;
import io.vertx.ext.web.RoutingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

@QuarkusTest
public class ProxyHandlerTest2 {

    @Mock
    private HttpClient httpClient;

    @InjectMocks
    private ProxyHandler proxyHandler;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHandle() {
        // Mocking the RoutingContext, HttpServerRequest, and HttpServerResponse
        RoutingContext routingContext = mock(RoutingContext.class);
        HttpServerRequest serverRequest = mock(HttpServerRequest.class);
        HttpServerResponse serverResponse = mock(HttpServerResponse.class);

        when(routingContext.request()).thenReturn(serverRequest);
        when(serverRequest.method()).thenReturn(HttpMethod.GET);
        when(serverRequest.uri()).thenReturn("/api/resource");
        when(serverRequest.response()).thenReturn(serverResponse);

        // Mocking the HttpClient and HttpClientRequest
        HttpClientRequest clientRequest = mock(HttpClientRequest.class);
        when(httpClient.request(any())).thenReturn(Future.succeededFuture(clientRequest));

        // Mocking the HttpClientResponse
        HttpClientResponse clientResponse = mock(HttpClientResponse.class);
        when(clientResponse.statusCode()).thenReturn(200);
        when(clientResponse.headers()).thenReturn(Mockito.mock(io.vertx.core.MultiMap.class));

        // Creating a promise to simulate the asynchronous behavior of the client request
        PromiseInternal<HttpClientResponse> clientResponsePromise = new PromiseImpl<>();
        when(clientRequest.send(any())).thenReturn(clientResponsePromise.future());

        // Simulating the completion of the client request
        clientResponsePromise.complete(clientResponse);

        // Call the handle method to test the ProxyHandler
        proxyHandler.handle(routingContext);

        // Verify that the proxyHandler sets the appropriate status code and headers to the server response
        verify(serverResponse).setStatusCode(200);
        verify(serverResponse.headers()).setAll(any());

        // Verify that the client request was made with the correct method and URI
        verify(httpClient).request(argThat(requestOptions -> {
            return requestOptions.getMethod() == HttpMethod.GET &&
              requestOptions.getAbsoluteURI().equals("http://backend-server/api/resource");
        }));

        // Verify that the client request headers were set correctly
        verify(clientRequest).headers().setAll(any());

        // Verify that the serverResponse.send() method was called with the clientResponse
        verify(serverResponse).send(clientResponse);
    }
}
