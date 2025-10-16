package com.example.bytebuddy.ReactorNetty;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Base class for RPC messages
 */
public abstract class RpcMessage {
    @JsonProperty("messageId")
    private String messageId;
    
    @JsonProperty("timestamp")
    private long timestamp;
    
    public RpcMessage() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public RpcMessage(String messageId) {
        this.messageId = messageId;
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getMessageId() {
        return messageId;
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
