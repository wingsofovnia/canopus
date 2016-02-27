package eu.ioservices.canopus.gateway.handling;

import eu.ioservices.canopus.http.HttpMethod;
import spark.utils.IOUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public abstract class ForwardingRequestProcessor implements RequestProcessor {
    protected void forwardRequest(HttpMethod method, URL destination,
                                  HttpServletRequest req, HttpServletResponse res) throws IOException {
        final boolean hasBodyContent = method == HttpMethod.POST;

        final HttpURLConnection conn = (HttpURLConnection) destination.openConnection();
        conn.setRequestMethod(method.name());

        final ArrayList<String> headers = Collections.list(req.getHeaderNames());
        headers.forEach(header ->
                Collections.list(req.getHeaders(header))
                        .forEach(value -> conn.addRequestProperty(header, value)));

        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(hasBodyContent);
        conn.connect();

        if (hasBodyContent) {
            final ServletInputStream reqInputStream = req.getInputStream();
            try (final OutputStream connOutputStream = conn.getOutputStream()) {
                IOUtils.copy(reqInputStream, connOutputStream);
            }
        }

        res.setStatus(conn.getResponseCode());

        conn.getHeaderFields().keySet().stream()
                .filter(k -> k != null)
                .forEach(k -> res.setHeader(k, conn.getHeaderField(k)));

        try (final InputStream connInputStream = conn.getInputStream()) {
            final ServletOutputStream respOutputStream = res.getOutputStream();
            IOUtils.copy(connInputStream, respOutputStream);
        }
    }
}
