import org.vertx.java.core.Handler;
  import org.vertx.java.core.http.HttpServerRequest;
  import org.vertx.java.platform.Verticle;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.logging.Logger;
  /*public class Server extends Verticle {
      public void start() {
          vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
              public void handle(HttpServerRequest req) {
                  String file = req.path().equals("/") ? "index.html" : req.path();
                  req.response().sendFile("webroot/" + file);
              }
          }).listen(8080);
      }
  }*/
  
  
  
  
  
  public class Server extends Verticle {
    public void start() {

        // Create our dependent verticles
        container.deployVerticle("AuditVerticle.java");

        // Create an HTTP Server that serves files
        vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
            public void handle(HttpServerRequest req) {
                Logger logger = container.logger();

                if (logger.isDebugEnabled()) {
                    logger.debug("Received a request for resource: " + req.path());
                }
                logger.fatal("Where are my logs!?!?");
                logger.info("Here is an info message");

                // Serve up our files
                String file = req.path().equals("/") ? "index.html" : req.path();
                req.response().sendFile("webroot/" + file);

                // Let's tell the world (via the event bus) that we received a request
                EventBus eb = vertx.eventBus();
                eb.publish( "Server.announcements", "We received a request for resource: " + req.path() );

            }
        }).listen(8080);
    }
}