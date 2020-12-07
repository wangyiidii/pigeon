package cn.yiidii.pigeon.adapter.grpc;


import cn.yiidii.grpc.helloword.*;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Wang
 */
@GRpcService
public class HelloWorldServiceImpl
        extends HelloWorldServiceGrpc.HelloWorldServiceImplBase {

    private static final Logger LOGGER
            = LoggerFactory.getLogger(HelloWorldServiceImpl.class);

    @Override
    public void sayHello(Person request,
                         StreamObserver<Greeting> responseObserver) {
        LOGGER.info("server received {}", request);

        String message = "Hello " + request.getFirstName() + " "
                + request.getLastName() + "!";
        Greeting greeting
                = Greeting.newBuilder().setMessage(message).build();
        LOGGER.info("server responded {}", greeting);
        System.out.println("message>>>" + message);
        responseObserver.onNext(greeting);
        responseObserver.onCompleted();
    }

    @Override
    public void addOperation(A1 request,
                             StreamObserver<A2> responseObserver) {
        LOGGER.info("server received {}", request);

        int message = request.getA() + request.getB();
        A2 a2 = A2.newBuilder().setMessage(message).build();
        LOGGER.info("server responded {}", a2);
        System.out.println("message>>>" + message);
        responseObserver.onNext(a2);
        responseObserver.onCompleted();
    }
}