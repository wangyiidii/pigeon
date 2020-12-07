package cn.yiidii.pigeon;

import cn.yiidii.pigeon.adapter.grpc.HelloWorldClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
class PigeonApplicationTests {
    @Autowired
    private HelloWorldClient helloWorldClient;

    @Test
    public void testSayHello() {
        assertThat(helloWorldClient.sayHello("Grpc", "Java")).isEqualTo("Hello Grpc Java!");
        assertThat(helloWorldClient.addOperation(1, 2)).isEqualTo(3);
    }

}
