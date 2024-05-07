import com.whedc.fault.retry.FixedIntervalRetryStrategy;
import com.whedc.fault.retry.NoRetryingStrategy;
import com.whedc.fault.retry.RetryStrategy;
import com.whedc.model.RpcResponse;
import org.junit.Test;

public class RetryTest {

    RetryStrategy strategy = new FixedIntervalRetryStrategy();

    @Test
    public void doRetry() {
        try {
            RpcResponse response = strategy.doRetry(() -> {
                System.out.println("Test Retry");
                throw new RuntimeException("mock retry failed");
            });
            System.out.println(response);
        } catch (Exception e) {
            System.out.println("Retrying too many times");
            e.printStackTrace();
        }
    }
}
