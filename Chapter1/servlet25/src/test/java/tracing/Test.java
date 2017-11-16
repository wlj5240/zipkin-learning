package tracing;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.context.log4j2.ThreadContextCurrentTraceContext;
import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.TraceContext;
import zipkin2.codec.SpanBytesEncoder;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

public class Test {
    public static void main(String[] args) throws Exception {
//        test();
        testReportOrphanedSpans();
    }

    public static void test() throws Exception {
        Object o = new Object();
        // 默认的构造函数，会使用ReferenceQueue.NULL 作为queue
        WeakReference<Object> wr = new WeakReference<Object>(o);
        System.out.println(wr.get() == null);
        o = null;
        System.gc();
        System.out.println(wr.get() == null);
    }

    public static Tracing initTracing() {
        Sender sender = OkHttpSender.create("http://localhost:9411/api/v2/spans");
        AsyncReporter asyncReporter = AsyncReporter.builder(sender)
                .closeTimeout(500, TimeUnit.MILLISECONDS)
                .build(SpanBytesEncoder.JSON_V2);

        Tracing tracing = Tracing.newBuilder()
                .localServiceName("tracer-demo")
                .spanReporter(asyncReporter)
                .propagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "user-name"))
                .currentTraceContext(ThreadContextCurrentTraceContext.create())
                .build();
        return tracing;
    }
    public static void testReportOrphanedSpans() throws Exception {
        Tracing tracing = initTracing();
        Tracer tracer = tracing.tracer();
        TraceContext context = tracer.newTrace().context();

        TraceContext context1 = context.toBuilder().spanId(1).build();
        tracer.newTrace(context1).name("span1").start();

        TraceContext context2 = context.toBuilder().spanId(2).build();
        tracer.newTrace(context2).name("span2").start();
        TraceContext context3 = context.toBuilder().spanId(3).build();
        tracer.newTrace(context3).name("span3").start();
        TraceContext context4 = context.toBuilder().spanId(4).build();
        tracer.newTrace(context4).name("span4").start();



        System.gc();
        try {
//            doSomethingExpensive();
        } finally {
//            span.finish();
        }
        tracer.newTrace(context4).name("span4-2").start();
        sleep(2000);
        System.out.println("finish");

    }


    private static void sleep(long milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
