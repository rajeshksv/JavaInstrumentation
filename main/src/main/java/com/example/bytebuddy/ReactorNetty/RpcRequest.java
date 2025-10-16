package com.example.bytebuddy.ReactorNetty;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RPC Request message
 */
public class RpcRequest extends RpcMessage {
    @JsonProperty("method")
    private String method;
    
    @JsonProperty("params")
    private java.util.List<Object> params;
    
    public RpcRequest() {
        super();
    }
    
    public RpcRequest(String messageId, String method, Object... params) {
        super(messageId);
        this.method = method;
        this.params = java.util.Arrays.asList(params);
    }
    
    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    public java.util.List<Object> getParams() {
        return params;
    }
    
    public void setParams(java.util.List<Object> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        StringBuilder paramsStr = new StringBuilder("[");
        if (params != null) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                paramsStr.append(String.valueOf(param));
                if (i < params.size() - 1) {
                    paramsStr.append(", ");
                }
            }
        }
        paramsStr.append("]");
        return "RpcRequest{" +
                "messageId='" + getMessageId() + '\'' +
                ", method='" + method + '\'' +
                ", params=" + paramsStr +
                '}';
    }
}
