package org.mozhu.zipkin.servlet;

import brave.http.HttpTracing;
import brave.okhttp3.TracingInterceptor;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class FrontendServlet extends HttpServlet {

    private final static Logger LOGGER = LoggerFactory.getLogger(FrontendServlet.class);

    private OkHttpClient client;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        HttpTracing httpTracing = (HttpTracing) config.getServletContext().getAttribute("TRACING");
        client = new OkHttpClient.Builder()
                .dispatcher(new Dispatcher(
                        httpTracing.tracing().currentTraceContext()
                                .executorService(new Dispatcher().executorService())
                ))
                .addNetworkInterceptor(TracingInterceptor.create(httpTracing))
                .build();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("frontend receive request");
        Request request = new Request.Builder()
                .url("http://localhost:9000/api")
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        PrintWriter writer = resp.getWriter();
        writer.write(response.body().string());
        writer.flush();
        writer.close();
    }

}
