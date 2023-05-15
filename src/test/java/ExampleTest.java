import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ExampleTest {

    @Test
    public void exampleTest() {
        assertThat(true).isEqualTo(true);
    }
}
