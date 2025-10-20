## **Service Proxy Usage in the Codebase**

### **1. Service Registration (Server Side)**

#### **Customer Service Registration:**
```42:44:vertx/customer-vertx-service/src/main/java/pl/piomin/services/vertx/customer/MongoVerticle.java
new ServiceBinder(vertx)
        .setAddress("customer-service")
        .register(CustomerRepository.class, service);
```

#### **Account Service Registration:**
```43:45:vertx/account-vertx-service/src/main/java/pl/piomin/services/vertx/account/MongoVerticle.java
new ServiceBinder(vertx)
    .setAddress("account-service")
    .register(AccountRepository.class, service);
```

### **2. Service Proxy Creation (Client Side)**

#### **Customer Service Proxy Usage:**
```47:47:vertx/customer-vertx-service/src/main/java/pl/piomin/services/vertx/customer/CustomerServer.java
CustomerRepository repository = CustomerRepository.createProxy(vertx, "customer-service");
```

#### **Account Service Proxy Usage:**
```34:34:vertx/account-vertx-service/src/main/java/pl/piomin/services/vertx/account/AccountServer.java
AccountRepository repository = AccountRepository.createProxy(vertx, "account-service");
```

### **3. Service Interface Definitions**

Both services use the `@ProxyGen` annotation to generate proxy classes:

#### **Customer Repository Interface:**
```10:21:vertx/customer-vertx-service/src/main/java/pl/piomin/services/vertx/customer/data/CustomerRepository.java
@ProxyGen
public interface CustomerRepository {
    // ... methods ...
    
    static CustomerRepository createProxy(Vertx vertx, String address) {
        return new CustomerRepositoryVertxEBProxy(vertx, address);
    }
}
```

#### **Account Repository Interface:**
```10:21:vertx/account-vertx-service/src/main/java/pl/piomin/services/vertx/account/data/AccountRepository.java
@ProxyGen
public interface AccountRepository {
    // ... methods ...
    
    static AccountRepository createProxy(Vertx vertx, String address) {
        return new AccountRepositoryVertxEBProxy(vertx, address);
    }
}
```

### **4. How Service Proxies Work**

1. **Registration**: `MongoVerticle` registers the service implementation with a specific address
2. **Proxy Creation**: `CustomerServer` and `AccountServer` create proxies to communicate with the registered services
3. **Event Bus Communication**: The proxies use the event bus internally to send messages to the registered services

### **5. Generated Proxy Classes**

The `@ProxyGen` annotation generates these proxy classes:
- `CustomerRepositoryVertxEBProxy` - Client-side proxy for customer service
- `AccountRepositoryVertxEBProxy` - Client-side proxy for account service
- `CustomerRepositoryVertxProxyHandler` - Server-side handler for customer service
- `AccountRepositoryVertxProxyHandler` - Server-side handler for account service

### **6. Communication Flow**

```
CustomerServer → CustomerRepositoryVertxEBProxy → Event Bus → CustomerRepositoryVertxProxyHandler → CustomerRepositoryImpl
AccountServer → AccountRepositoryVertxEBProxy → Event Bus → AccountRepositoryVertxProxyHandler → AccountRepositoryImpl
```

### **Key Points:**

- **Service proxies are used for intra-service communication** (within the same Vert.x instance)
- **Each service has its own repository** that's accessed via service proxy
- **The proxies abstract away the event bus communication** - you just call methods on the proxy
- **No service proxies are used for inter-service communication** (customer ↔ account) - that uses HTTP + Service Discovery

This is a clean separation where service proxies handle internal data access, while HTTP handles external service communication.