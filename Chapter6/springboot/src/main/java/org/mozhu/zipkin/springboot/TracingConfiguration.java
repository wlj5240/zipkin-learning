package org.mozhu.zipkin.kafka;

import brave.Tracing;
import brave.context.log4j2.ThreadContextCurrentTraceContext;
import brave.http.HttpTracing;
import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldPropagation;
import brave.spring.web.TracingClientHttpRequestInterceptor;
import brave.spring.webmvc.TracingHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "org.mozhu.zipkin.kafka")
@Import({TracingClientHttpRequestInterceptor.class, TracingHandlerInterceptor.class})
public class TracingConfiguration extends WebMvcConfigurerAdapter {

  @Bean Sender sender() {
    return OkHttpSender.create("http://127.0.0.1:9411/api/v2/spans");
  }

  @Bean AsyncReporter<Span> spanReporter() {
    return AsyncReporter.create(sender());
  }

  @Bean Tracing tracing(@Value("${zipkin.service:kafka-example}") String serviceName) {
    return Tracing.newBuilder()
        .localServiceName(serviceName)
        .propagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "user-name"))
        .currentTraceContext(ThreadContextCurrentTraceContext.create()) // puts trace IDs into logs
        .spanReporter(spanReporter()).build();
  }

  @Bean HttpTracing httpTracing(Tracing tracing) {
    return HttpTracing.create(tracing);
  }

  @Autowired
  private TracingHandlerInterceptor serverInterceptor;

  @Autowired
  private TracingClientHttpRequestInterceptor clientInterceptor;

  @Bean RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    List<ClientHttpRequestInterceptor> interceptors =
      new ArrayList<>(restTemplate.getInterceptors());
    interceptors.add(clientInterceptor);
    restTemplate.setInterceptors(interceptors);
    return restTemplate;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(serverInterceptor);
  }
}
