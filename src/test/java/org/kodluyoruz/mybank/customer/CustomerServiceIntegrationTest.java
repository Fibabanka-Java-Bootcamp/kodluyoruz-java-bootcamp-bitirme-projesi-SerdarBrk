package org.kodluyoruz.mybank.customer;

import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource({ "classpath:application.properties" })
public class CustomerServiceIntegrationTest {
    @Autowired
    private CustomerService customerService;

    @Test
    @Order(1)
    public void create(){
        Customer customer=CustomerDto.builder()
                .tc("35689754620")
                .name("serdar")
                .surname("berk")
                .phoneNumber("+(444)-333-222-111")
                .build().toCustomer();
        assertNotNull(this.customerService.create(customer));
    }

    @Test
    @Order(2)
    public void updatePhone(){
        assertNotNull(this.customerService
                .updatePhone(UUID.fromString("7d7cd581-b6a4-4a5a-8690-575054043a89")
                ,"+(333)-333-555-555"));
    }

    @Test
    @Order(3)
    public void get(){
        assertNotNull(this.customerService.get(UUID.fromString("7d7cd581-b6a4-4a5a-8690-575054043a89")));
    }

}
