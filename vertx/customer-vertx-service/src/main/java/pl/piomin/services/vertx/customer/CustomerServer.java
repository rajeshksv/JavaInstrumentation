package pl.piomin.services.vertx.customer;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ResponseContentTypeHandler;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.consul.ConsulServiceImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.piomin.services.vertx.customer.data.Customer;
import pl.piomin.services.vertx.customer.data.CustomerRepository;
import pl.piomin.services.vertx.customer.client.AccountClient;


public class CustomerServer extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServer.class);

    private Integer port;

    public CustomerServer() {

    }

    public CustomerServer(Integer port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MongoVerticle());
        vertx.deployVerticle(new CustomerServer());
    }

    @Override
    public void start() throws Exception {
        ServiceDiscovery discovery = ServiceDiscovery.create(vertx);
        CustomerRepository repository = CustomerRepository.createProxy(vertx, "customer-service");
        AccountClient accountClient = new AccountClient(discovery);

        Router router = Router.router(vertx);
        router.route("/customer/*").handler(ResponseContentTypeHandler.create());
        router.route(HttpMethod.POST, "/customer").handler(BodyHandler.create());
        router.get("/customer/:id").produces("application/json").handler(rc -> {
            String customerId = rc.request().getParam("id");
            repository.findById(customerId).compose(customer -> {
                if (customer == null) {
                    return Future.failedFuture("Customer not found");
                }
                // Fetch accounts for this customer
                return accountClient.findCustomerAccounts(customerId).map(accounts -> {
                    customer.setAccounts(accounts);
                    return customer;
                });
            }).onComplete(res -> {
                if (res.succeeded()) {
                    rc.response().end(Json.encodePrettily(res.result()));
                } else {
                    if ("Customer not found".equals(res.cause().getMessage())) {
                        rc.response().setStatusCode(404).end();
                    } else {
                        LOGGER.error("Error fetching customer with accounts", res.cause());
                        rc.response().setStatusCode(500).end();
                    }
                }
            });
        });
        router.get("/customer/name/:name").produces("application/json").handler(rc -> {
            repository.findByName(rc.request().getParam("name")).onComplete(res -> {
                if (res.succeeded()) {
                    rc.response().end(Json.encodePrettily(res.result()));
                } else {
                    rc.response().setStatusCode(500).end();
                }
            });
        });
        
        // New endpoint to get customer with accounts - fetches customer data and their associated accounts
        router.get("/customer/:id/with-accounts").produces("application/json").handler(rc -> {
            String customerId = rc.request().getParam("id");
            repository.findById(customerId).compose(customer -> {
                if (customer == null) {
                    return Future.failedFuture("Customer not found");
                }
                // Fetch accounts for this customer
                return accountClient.findCustomerAccounts(customerId).map(accounts -> {
                    customer.setAccounts(accounts);
                    return customer;
                });
            }).onComplete(res -> {
                if (res.succeeded()) {
                    rc.response().end(Json.encodePrettily(res.result()));
                } else {
                    if ("Customer not found".equals(res.cause().getMessage())) {
                        rc.response().setStatusCode(404).end();
                    } else {
                        LOGGER.error("Error fetching customer with accounts", res.cause());
                        rc.response().setStatusCode(500).end();
                    }
                }
            });
        });
        router.get("/customer").produces("application/json").handler(rc -> {
            repository.findAll().onComplete(res -> {
                if (res.succeeded()) {
                    rc.response().end(Json.encodePrettily(res.result()));
                } else {
                    rc.response().setStatusCode(500).end();
                }
            });
        });
        router.post("/customer").produces("application/json").handler(rc -> {
            Customer c = rc.body().asPojo(Customer.class);
            repository.save(c).onComplete(res -> {
                if (res.succeeded()) {
                    rc.response().end(Json.encodePrettily(res.result()));
                } else {
                    rc.response().setStatusCode(500).end();
                }
            });
        });
        router.delete("/customer/:id").handler(rc -> {
            repository.remove(rc.request().getParam("id"));
            rc.response().setStatusCode(200);
        });

        ConfigStoreOptions file = new ConfigStoreOptions().setType("file").setConfig(new JsonObject().put("path", "application.json"));
        ConfigRetriever retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions().addStore(file));
        retriever.getConfig().onComplete(conf -> {
            vertx.createHttpServer().requestHandler(router).listen(conf.result().getInteger("port"));
            JsonObject discoveryConfig = conf.result().getJsonObject("discovery");
            discovery.registerServiceImporter(new ConsulServiceImporter(),
                    new JsonObject()
                            .put("host", discoveryConfig.getString("host"))
                            .put("port", port == null ? discoveryConfig.getInteger("port") : port)
                            .put("scan-period", 2000));
        });

    }

}
