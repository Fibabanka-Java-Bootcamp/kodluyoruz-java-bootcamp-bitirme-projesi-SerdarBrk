package org.kodluyoruz.mybank.debitcardTest;

import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.kodluyoruz.mybank.account.AccountService;
import org.kodluyoruz.mybank.debitcard.DebitCard;
import org.kodluyoruz.mybank.debitcard.DebitCardDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource({ "classpath:application.properties" })
public class DebitcardControllerIntegrationTest {
    @LocalServerPort
    private String portNo;

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private AccountService accountService;

    @Test
    @Order(1)
    public void create(){
        String url="http://localhost:" + portNo + "/api/debitCard";
        DebitCard debitCard= DebitCardDto.builder()
                .account(accountService.get(UUID.fromString("0bd0fb85-b010-4c0c-922b-774f62340d66")).get().toAccountDto())
                .password("1234")
                .ccv("324")
                .expirationMonth("05")
                .expirationYear("2022")
                .build().toDebitCard();
        ResponseEntity<DebitCard> responseEntity=restTemplate.postForEntity(url,debitCard,DebitCard.class);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @Order(2)
    public void updateAccount(){
        String number="3750f985-3ff6-4c9c-ad63-074083cb565e";
        String accountId="2029072f-d8e6-4880-8e1c-f64637977e83";
        String url="http://localhost:" + portNo + "/api/debitCard/"+number
                +"/updateAccount?accountId="+accountId;
        ResponseEntity<DebitCard> responseEntity=restTemplate.postForEntity(url,null,DebitCard.class);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @Order(3)
    public void updatePassword(){
        String number="3750f985-3ff6-4c9c-ad63-074083cb565e";
        String url="http://localhost:" + portNo + "/api/debitCard/"+number
                +"/updatePassword?password=7777";
        ResponseEntity<DebitCard> responseEntity=restTemplate.postForEntity(url,null,DebitCard.class);
        assertNotNull(responseEntity.getBody());
    }
    @Test
    @Order(4)
    public void withdrawlMoneyFromAtm(){
        String number="3750f985-3ff6-4c9c-ad63-074083cb565e";
        String url="http://localhost:" + portNo + "/api/debitCard"
                +"/withdrawlMoneyFromAtm?debitcardNumber="+number+"&password=7777&money=20";
        ResponseEntity<DebitCard> responseEntity=restTemplate.postForEntity(url,null,DebitCard.class);
        assertNotNull(responseEntity.getBody());
    }
    @Test
    @Order(5)
    public void depositMoneyFromAtm(){
        String number="3750f985-3ff6-4c9c-ad63-074083cb565e";
        String url="http://localhost:" + portNo + "/api/debitCard"
                +"/depositMoneyFromAtm?debitcardNumber="+number+"&password=7777&money=200";
        ResponseEntity<DebitCard> responseEntity=restTemplate.postForEntity(url,null,DebitCard.class);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @Order(6)
    public void shopping(){
        String number="3750f985-3ff6-4c9c-ad63-074083cb565e";
        String accountIban="3fa85f64-5717-4562-b3fc-2c463f66afa5";
        String url="http://localhost:" + portNo + "/api/debitCard"
                +"/shopping?debitcardNumber="+number+"&receiverIban="+accountIban
                +"&password=7777&money=20";
        ResponseEntity<DebitCard> responseEntity=restTemplate.postForEntity(url,null,DebitCard.class);
        assertNotNull(responseEntity.getBody());
    }
    @Test
    @Order(7)
    public void onlineShopping(){
        String number="3750f985-3ff6-4c9c-ad63-074083cb565e";
        String accountIban="3fa85f64-5717-4562-b3fc-2c463f66afa5";
        String url="http://localhost:" + portNo + "/api/debitCard"
                +"/onlineShopping?receiverIban="+accountIban+"&debitcardNumber="+number
                +"&password=7777&ccv=324&expirationMonth=05&expirationYear=2022&money=10";
        ResponseEntity<DebitCard> responseEntity=restTemplate.postForEntity(url,null,DebitCard.class);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @Order(8)
    public void get(){
        String url="http://localhost:" + portNo + "/api/debitCard/3750f985-3ff6-4c9c-ad63-074083cb565e";
        ResponseEntity<DebitCard> responseEntity=restTemplate.getForEntity(url,DebitCard.class);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @Order(9)
    public void delete(){
        this.restTemplate.delete("http://localhost:" + portNo + "/api/debitCard?debitcardNumber=35617ff4-9208-4921-87cf-6c69e72ee3b2");
    }

}
