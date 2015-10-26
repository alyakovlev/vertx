import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

import java.util.concurrent.ConcurrentMap;

public class Server2 extends Verticle {
    public void start() {

        // Create our dependent verticles
        container.deployWorkerVerticle("MyWorkerVerticle.java");

        // Start a server that handles things with point-to-point messaging
        vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
            @Override
            public void handle(final HttpServerRequest req) {

                // Set a shared variable
                ConcurrentMap<String, String> map = vertx.sharedData().getMap("mymap");
                map.put("mykey", "myvalue");

                // Let's send a message to a worker verticle and wait for it to respond
                EventBus eb = vertx.eventBus();
                eb.send("request.worker", req.path(), new Handler<Message<String>>() {
                    @Override
                    public void handle(Message<String> message) {
                        Logger logger = container.logger();
                        logger.info( "Received a reply from our worker: " + message.body() );
                        req.response().headers().set("Content-Length", Integer.toString(message.body().length()));
                        req.response().write(message.body());
                    }
                });
            }
        }).listen(8080);
    }
}