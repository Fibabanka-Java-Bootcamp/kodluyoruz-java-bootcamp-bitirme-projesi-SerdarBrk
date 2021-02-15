package org.kodluyoruz.mybank.accountTest;

import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.kodluyoruz.mybank.account.Account;
import org.kodluyoruz.mybank.account.AccountDto;
import org.kodluyoruz.mybank.account.AccountType;
import org.kodluyoruz.mybank.account.MoneyType;
import org.kodluyoruz.mybank.customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.jupiter.api.Assertions.*;


import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource({ "classpath:application.properties" })
public class AccountControllerIntegrationTest {
    @LocalServerPort
    private int portNo;
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerService customerService;
    @Test
    @Order(1)
    public void create(){
        String url="http://localhost:" + portNo + "/api/account";
        Account account = AccountDto.builder()
                .iban(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66444a"))
                .customer(this.customerService
                        .get(UUID.fromString("cdfb4132-e333-4eba-b472-efcbf79cbdd0"))
                        .get().toCustomerDto())
                .accountType(AccountType.CURRENT_ACCOUNT)
                .moneyType(MoneyType.TRY)
                .currency(100)
                .build().toAccount();
        ResponseEntity<Account> responseEntity=restTemplate.postForEntity(url,account,Account.class);
        Account respnseAccount=responseEntity.getBody();
        assertNotNull(respnseAccount);

    }

    @Test
    @Order(2)
    public void listByCustomerReturnPage(){
        String url="http://localhost:" + portNo + "/api/account"+"/listByCustomer?customerId="+"cdfb4132-e333-4eba-b472-efcbf79cbdd0"
                +"&page=0&size=20";
        ResponseEntity<Account[]> responseEntity=restTemplate.getForEntity(url,Account[].class);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @Order(3)
    public void sendMoney(){
        String url="http://localhost:" + portNo + "/api/account"+"/1d67bd94-7c2e-4a62-8edd-1104c077b715" +
                "/sendMoney?receiverIban=3fa85f63-5717-4562-b3fc-2c463f66afa5"
                +"&money=30";

        ResponseEntity<Account> responseEntity=restTemplate.postForEntity(url,null,Account.class);
        Account account=responseEntity.getBody();
        assertNotNull(account);
    }
    @Test
    @Order(4)
    public void get(){
        UUID id=UUID.fromString("1d67bd94-7c2e-4a62-8edd-1104c077b715");
        String url="http://localhost:" + portNo + "/api/account/"+id;
        ResponseEntity<Account> responseEntity=restTemplate.getForEntity(url,Account.class);
        Account account=responseEntity.getBody();
        assertNotNull(account);
    }

    @Test
    @Order(5)
    public void delete(){
        restTemplate.delete("http://localhost:" + portNo + "/api/account/"+"0bd0fb85-b010-4c0c-922b-774f62340d66");
    }


}
