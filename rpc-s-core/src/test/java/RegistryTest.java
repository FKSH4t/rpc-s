import com.whedc.config.RegistryConfig;
import com.whedc.model.ServiceMeteInfo;
import com.whedc.registry.EtcdRegistry;
import com.whedc.registry.Registry;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class RegistryTest {
    final Registry registry = new EtcdRegistry();

    @Before
    public void init() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("http://localhost:2379");
        registry.init(registryConfig);
    }

    @Test
    public void register() throws Exception{
        ServiceMeteInfo serviceMeteInfo = new ServiceMeteInfo();
        serviceMeteInfo.setServiceName("myService");
        serviceMeteInfo.setServiceVersion("1.0");
        serviceMeteInfo.setServiceHost("localhost");
        serviceMeteInfo.setServicePort(1234);
        registry.register(serviceMeteInfo);

        serviceMeteInfo = new ServiceMeteInfo();
        serviceMeteInfo.setServiceName("myService");
        serviceMeteInfo.setServiceVersion("1.0");
        serviceMeteInfo.setServiceHost("localhost");
        serviceMeteInfo.setServicePort(1235);
        registry.register(serviceMeteInfo);

        serviceMeteInfo = new ServiceMeteInfo();
        serviceMeteInfo.setServiceName("myService");
        serviceMeteInfo.setServiceVersion("2.0");
        serviceMeteInfo.setServiceHost("localhost");
        serviceMeteInfo.setServicePort(1234);
        registry.register(serviceMeteInfo);
    }

    @Test
    public void unRegister() {
        ServiceMeteInfo serviceMeteInfo = new ServiceMeteInfo();
        serviceMeteInfo.setServiceName("myService");
        serviceMeteInfo.setServiceVersion("1.0");
        serviceMeteInfo.setServiceHost("localhost");
        serviceMeteInfo.setServicePort(1234);
        registry.unRegister(serviceMeteInfo);
    }

    @Test
    public void testUnRegister() {
        Client client = Client.builder().endpoints("http://localhost:2379").build();
        KV kvClient = client.getKVClient();

        kvClient.delete(ByteSequence.from("/rpc/myService:1.0/localhost:1234", StandardCharsets.UTF_8));
    }

    @Test
    public void discovery() {
        ServiceMeteInfo serviceMeteInfo = new ServiceMeteInfo();
        serviceMeteInfo.setServiceName("myService");
        serviceMeteInfo.setServiceVersion("1.0");
        String serviceKey = serviceMeteInfo.getServiceKey();
        List<ServiceMeteInfo> serviceMeteInfoList = registry.serviceDiscovery(serviceKey);
        Assert.assertNotNull(serviceMeteInfoList);
    }
}
