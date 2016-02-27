package eu.ioservices.canopus.gateway;

import eu.ioservices.canopus.gateway.handling.RequestProcessor;
import eu.ioservices.canopus.gateway.routing.Router;
import eu.ioservices.canopus.gateway.routing.Track;
import eu.ioservices.canopus.http.HttpMethod;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.Objects;
import java.util.Optional;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class Gateway {
    private Router router;
    private RequestProcessor requestProcessor;

    public Gateway(Router router, RequestProcessor requestProcessor) {
        this.router = Objects.requireNonNull(router);
        this.requestProcessor = Objects.requireNonNull(requestProcessor);
    }

    public void listen() {
        Spark.get("*", (req, res) -> processRequest(HttpMethod.GET, req, res));
        Spark.post("*", (req, res) -> processRequest(HttpMethod.POST, req, res));
        Spark.put("*", (req, res) -> processRequest(HttpMethod.PUT, req, res));
        Spark.delete("*", (req, res) -> processRequest(HttpMethod.DELETE, req, res));
        Spark.head("*", (req, res) -> processRequest(HttpMethod.HEAD, req, res));
        Spark.options("*", (req, res) -> processRequest(HttpMethod.OPTIONS, req, res));
        Spark.trace("*", (req, res) -> processRequest(HttpMethod.TRACE, req, res));

        Spark.awaitInitialization();
    }

    protected Object processRequest(HttpMethod method, Request req, Response res) throws Exception {
        final Optional<Track> track = router.route(method, req.url());
        this.requestProcessor.process(req.raw(), res.raw(), track);
        return ""; // https://github.com/perwendel/spark/issues/335
    }

    public Gateway ipAddress(String ipAddress) {
        Spark.ipAddress(ipAddress);
        return this;
    }

    public Gateway port(int port) {
        Spark.port(port);
        return this;
    }

    public Gateway halt() {
        Spark.halt();
        return this;
    }

    public Gateway halt(int status) {
        Spark.halt(status);
        return this;
    }

    public Gateway threadPool(int maxThreads) {
        Spark.threadPool(maxThreads);
        return this;
    }

    public Gateway threadPool(int maxThreads, int minThreads, int idleTimeoutMillis) {
        Spark.threadPool(maxThreads, minThreads, idleTimeoutMillis);
        return this;
    }

    public Gateway secure(String keystoreFile, String keystorePassword, String truststoreFile, String truststorePassword) {
        Spark.secure(keystoreFile, keystorePassword, truststoreFile, truststorePassword);
        return this;
    }

    public Gateway exception(Class<? extends Exception> exceptionClass, ExceptionHandler handler) {
        Spark.exception(exceptionClass, handler);
        return this;
    }

    public Gateway halt(int status, String body) {
        Spark.halt(status, body);
        return this;
    }
}
