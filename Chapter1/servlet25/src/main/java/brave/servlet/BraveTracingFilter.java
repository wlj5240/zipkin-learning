package brave.servlet;

import brave.Tracing;
import brave.context.log4j2.ThreadContextCurrentTraceContext;
import brave.http.HttpTracing;
import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldPropagation;
import zipkin2.codec.SpanBytesEncoder;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;

import javax.servlet.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class BraveTracingFilter implements Filter {
    TracingFilter tracingFilter;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Sender sender = OkHttpSender.create("http://localhost:9411/api/v2/spans");
        AsyncReporter asyncReporter = AsyncReporter.builder(sender)
                .closeTimeout(500, TimeUnit.MILLISECONDS)
                .build(SpanBytesEncoder.JSON_V2);

        Tracing tracing = Tracing.newBuilder()
                .localServiceName(System.getProperty("zipkin.service", "servlet25-demo"))
                .spanReporter(asyncReporter)
                .propagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "user-name"))
                .currentTraceContext(ThreadContextCurrentTraceContext.create())
                .build();

        HttpTracing httpTracing = HttpTracing.create(tracing);
        filterConfig.getServletContext().setAttribute("TRACING", httpTracing);
        tracingFilter = new TracingFilter(httpTracing);
        tracingFilter.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        tracingFilter.doFilter(servletRequest, servletResponse, filterChain);
    }

    @Override
    public void destroy() {
        tracingFilter.destroy();
    }

}
