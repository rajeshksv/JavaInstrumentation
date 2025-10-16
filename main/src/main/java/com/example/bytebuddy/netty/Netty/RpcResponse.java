package com.example.bytebuddy.netty.Netty;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RPC Response message
 */
public class RpcResponse extends RpcMessage {
    @JsonProperty("result")
    private Object result;
    
    @JsonProperty("error")
    private String error;
    
    @JsonProperty("success")
    private boolean success;
    
    public RpcResponse() {
        super();
    }
    
    public RpcResponse(String messageId, Object result) {
        super(messageId);
        this.result = result;
        this.success = true;
    }
    
    public RpcResponse(String messageId, String error) {
        super(messageId);
        this.error = error;
        this.success = false;
    }
    
    public Object getResult() {
        return result;
    }
    
    public void setResult(Object result) {
        this.result = result;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
}
