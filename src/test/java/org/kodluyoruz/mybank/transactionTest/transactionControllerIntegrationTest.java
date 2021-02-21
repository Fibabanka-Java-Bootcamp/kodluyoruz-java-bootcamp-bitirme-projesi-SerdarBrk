package org.kodluyoruz.mybank.transactionTest;

import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.kodluyoruz.mybank.transaction.Transaction;
import org.kodluyoruz.mybank.transaction.TransactionDto;
import org.kodluyoruz.mybank.transaction.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource({ "classpath:application.properties" })
public class transactionControllerIntegrationTest {
    @LocalServerPort
    private String portNo;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Order(1)
    public void create(){
        Transaction transaction= TransactionDto.builder()
                .performedId(UUID.fromString("3fc6f6bc-6885-4254-88dd-26a138728e23"))
                .transactionType(TransactionType.TRANSFER)
                .explanation("sadasd")
                .transactionDate(LocalDate.now())
                .build().toTransaction();
        ResponseEntity<Transaction> responseEntity = restTemplate.postForEntity("http://localhost:"+portNo+"/api/transaction"
                ,transaction,Transaction.class);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @Order(2)
    public void listByPerformedId(){
        ResponseEntity<List> responseEntity = restTemplate.getForEntity("http://localhost:"+portNo+"/api/transaction"
                +"/listByPerformedId?performedId=3fc6f6bc-6885-4254-88dd-26a138728e23"
                +"&page=0&size=10"
                , List.class);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @Order(3)
    public void get(){
        ResponseEntity<Transaction> responseEntity = restTemplate.getForEntity("http://localhost:"+portNo+"/api/transaction"
                        +"/4"
                , Transaction.class);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @Order(4)
    public void delete(){
        this.restTemplate.delete("http://localhost:"+portNo+"/api/transaction?performedId=0bd0fb85-b010-4c0c-922b-774f62340d66");
    }
}
