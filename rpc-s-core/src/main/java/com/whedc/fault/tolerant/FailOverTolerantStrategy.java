package com.whedc.fault.tolerant;

import com.whedc.loadBalancer.LoadBalancer;
import com.whedc.loadBalancer.LoadBalancerFactory;
import com.whedc.model.RpcRequest;
import com.whedc.model.RpcResponse;
import com.whedc.model.ServiceMeteInfo;
import com.whedc.server.tcp.VertxTcpClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 故障转移
 */
public class FailOverTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // 获取服务节点列表
        List<ServiceMeteInfo> serviceList = (List<ServiceMeteInfo>) context.get("serviceList");
        if (serviceList.size() <= 1) {
            // 如果没有其他节点可以转移，直接快速失败
            return new FailFastTolerantStrategy().doTolerant(context, e);
        }
        // 移除列表中当前调用的节点
        ServiceMeteInfo currentNode = (ServiceMeteInfo) context.get("currentNode");
        serviceList.remove(currentNode);
        LoadBalancer loadBalancer = (LoadBalancer) context.get("loadBalancer");
        ServiceMeteInfo meteInfo = loadBalancer.select(null, serviceList);
        context.put("currentNode", meteInfo);
        RpcRequest rpcRequest = (RpcRequest) context.get("rpcRequest");
        RpcResponse rpcResponse = null;
        try {
            rpcResponse = VertxTcpClient.doRequest(rpcRequest, meteInfo);
        } catch (ExecutionException | InterruptedException ex) {
            // 如果当前调用的节点仍有问题，继续调用其他的节点
            doTolerant(context, ex);
        }
        // 获取其它服务节点并调用
        return rpcResponse;
    }
}
