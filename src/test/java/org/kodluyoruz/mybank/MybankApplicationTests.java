package org.kodluyoruz.mybank;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.TestPropertySource;


@BootstrapWith(SpringBootTestContextBootstrapper.class)
@TestPropertySource({"classpath:application.properties"})
class MybankApplicationTests {

    @Test
    void contextLoads() {

    }


}
