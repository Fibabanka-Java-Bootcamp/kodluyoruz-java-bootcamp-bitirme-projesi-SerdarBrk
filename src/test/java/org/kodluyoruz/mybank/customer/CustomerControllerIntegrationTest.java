package org.kodluyoruz.mybank.customer;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource({ "classpath:application.properties" })
public class CustomerControllerIntegrationTest {

    @LocalServerPort
    private int portNo;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Order(1)
    public void create(){
        String url="http://localhost:" + portNo + "/api/customer";
        Customer customer=CustomerDto.builder()
                .tc("35689754620")
                .name("serdar")
                .surname("berk")
                .phoneNumber("+(444)-333-222-111")
                .build().toCustomer();
        ResponseEntity<Customer> responseEntity=restTemplate.postForEntity(url,customer,Customer.class);
        assertNotNull(responseEntity.getBody());
    }
    @Test
    @Order(2)
    public void updatePhone(){
        String url="http://localhost:" + portNo + "/api/customer" +
                "/8eb232aa-7328-4569-951e-c0709bae0582"
                +"/updatePhone?phoneNumber=+(222)-333-222-111";
        ResponseEntity<Customer> responseEntity=restTemplate.postForEntity(url,null,Customer.class);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @Order(3)
    public void get(){
        String url="http://localhost:" + portNo + "/api/customer" +
                "/8eb232aa-7328-4569-951e-c0709bae0582";
        ResponseEntity<Customer> responseEntity=restTemplate.getForEntity(url,Customer.class);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @Order(4)
    public void delete(){
        restTemplate.delete("http://localhost:" + portNo + "/api/customer" +
                "/8eb232aa-7328-4569-951e-c0709bae0582");
    }

}
