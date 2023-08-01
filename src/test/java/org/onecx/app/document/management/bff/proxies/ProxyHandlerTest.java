import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import static org.mockito.Mockito.*;

@Slf4j
@QuarkusTest
public class ProxyHandlerTest {

    private ProxyHandler proxyHandler;
    private final String baseUrl = "baseURL";
    private HttpClient httpClient;

    @BeforeMethod
    public void setUp() {
        httpClient = mock(HttpClient.class);
        proxyHandler = new ProxyHandler(httpClient, baseUrl)
    }

    @Test
    public void testHandle() {
        //given

        //when
        proxyHandler.handle
        //then

    }

    @Test
    public void testCreate() {
        //given
        HttpClient testHttpClient = new HttpClient();
        String testBaseUrl = "testBaseUrl";
        proxyHandler = new ProxyHandler(testHttpClient, testBaseUrl)

        //when
        ProxyHandler = proxyHandler.create(testHttpClient, testBaseUrl)
        //then
        assertThat(proxyHandler.).isEqualTo(true);
}