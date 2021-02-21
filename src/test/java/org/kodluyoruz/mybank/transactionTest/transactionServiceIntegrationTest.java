package org.kodluyoruz.mybank.transactionTest;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.kodluyoruz.mybank.transaction.Transaction;
import org.kodluyoruz.mybank.transaction.TransactionDto;
import org.kodluyoruz.mybank.transaction.TransactionService;
import org.kodluyoruz.mybank.transaction.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource({ "classpath:application.properties" })
public class transactionServiceIntegrationTest {

    @Autowired
    private TransactionService transactionService;

    @Test
    @Order(1)
    public void create(){
        Transaction transaction= TransactionDto.builder()
                .performedId(UUID.fromString("3fc6f6bc-6885-4254-88dd-26a138728e23"))
                .transactionType(TransactionType.TRANSFER)
                .explanation("sadasd")
                .transactionDate(LocalDate.now())
                .build().toTransaction();
        assertNotNull(this.transactionService.create(transaction));

    }
    @Test
    @Order(2)
    public void summary(){
        assertNotNull(this.transactionService.summary(UUID.fromString("f5884081-d4b5-4a62-ba88-f19f6fc902ad"),
                PageRequest.of(0, 10)));
    }

    @Test
    @Order(3)
    public void get(){
        assertNotNull(this.transactionService.get(1).get());
    }

    @Test
    @Order(4)
    public void delete(){
        this.transactionService.delete(UUID.fromString("f5884081-d4b5-4a62-ba88-f19f6fc902ad"));
    }
}
