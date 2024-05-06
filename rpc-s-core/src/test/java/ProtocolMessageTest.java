import cn.hutool.core.util.IdUtil;
import com.whedc.constant.RpcConstant;
import com.whedc.model.RpcRequest;
import com.whedc.protocol.*;
import io.vertx.core.buffer.Buffer;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ProtocolMessageTest {

    @Test
    public void testEncoderDecoder() throws IOException {
        ProtocolMessage<RpcRequest> message = new ProtocolMessage<>();
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
        header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
        header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
        header.setSerializer((byte) ProtocolMessageSerializerEnum.JDK.getKey());
        header.setStatus((byte) ProtocolMessageStatusEnum.OK.getValue());
        header.setRequestId(IdUtil.getSnowflakeNextId());
        header.setLength(0);
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setServiceName("testService");
        rpcRequest.setMethodName("testMethod");
        rpcRequest.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        rpcRequest.setParameterTypes(new Class[]{String.class});
        rpcRequest.setArgs(new Object[]{"arg1", "arg2"});
        message.setHeader(header);
        message.setBody(rpcRequest);
        Buffer buffer = ProtocolMessageEncoder.encode(message);
        ProtocolMessage<?> protocolMessage = ProtocolMessageDecoder.decode(buffer);
        System.out.println("protocolMessage = " + protocolMessage);
        Assert.assertNotNull(protocolMessage);
    }
}
