package org.kodluyoruz.mybank.creditcardTest;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.kodluyoruz.mybank.creditcard.CreditCard;
import org.kodluyoruz.mybank.creditcard.CreditCardDto;
import org.kodluyoruz.mybank.customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource({ "classpath:application.properties" })
public class CreditcardControllerIntegrationTest {
    @LocalServerPort
    private String portNo;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerService customerService;

    @Test
    @Order(1)
    public void create(){
        CreditCard creditCard= CreditCardDto.builder()
                .credit(500)
                .password("1234")
                .ccv("123")
                .expirationMonth("05")
                .expirationYear("2023")
                .customer(this.customerService.get(UUID.fromString("4fbbf5a8-49a0-4682-8dc7-ff457adafddd"))
                        .get().toCustomerDto())
                .build().toCreditCard();
        ResponseEntity<CreditCard> responseEntity=restTemplate.postForEntity("http://localhost:"+portNo+"/api/creditcard"
                ,creditCard,CreditCard.class);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @Order(2)
    public void payDeptFromAtm(){
        ResponseEntity<CreditCard> responseEntity=restTemplate.postForEntity("http://localhost:"+portNo+"/api/creditcard"
                +"/payDeptFromAtm?creditcardNumber=354636ed-7f56-422b-96d5-2dd017b3677e"
                        +"&password=1234&money=28"
                ,null,CreditCard.class);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @Order(3)
    public void payDeptFromAccount(){
        ResponseEntity<CreditCard> responseEntity=restTemplate.postForEntity("http://localhost:"+portNo+"/api/creditcard"
                        +"/payDeptFromAccount?accountId=1d67bd94-7c2e-4a62-8edd-1104c077b715"
                        +"&creditcardNumber=354636ed-7f56-422b-96d5-2dd017b3677e"
                        +"&money=28"
                ,null,CreditCard.class);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @Order(4)
    public void shopping(){
        ResponseEntity<CreditCard> responseEntity=restTemplate.postForEntity("http://localhost:"+portNo+"/api/creditcard"
                        +"/shopping?receiverAccountIban=3fa85f64-5717-4562-b3fc-2c963f66444a"
                        +"&creditcardNumber=354636ed-7f56-422b-96d5-2dd017b3677e"
                        +"&password=1234&money=28"
                ,null,CreditCard.class);
        assertNotNull(responseEntity.getBody());
    }
    @Test
    @Order(5)
    public void onlineShopping(){
        ResponseEntity<CreditCard> responseEntity=restTemplate.postForEntity("http://localhost:"+portNo+"/api/creditcard"
                        +"/onlineShopping?receiverAccountIban=3fa85f64-5717-4562-b3fc-2c963f66444a"
                        +"&creditcardNumber=354636ed-7f56-422b-96d5-2dd017b3677e"
                        +"&password=1234&ccv=325&expirationMonth=06&expirationYear=2023&money=28"
                ,null,CreditCard.class);
        assertNotNull(responseEntity.getBody());
    }
    @Test
    @Order(6)
    public void hasDept(){
        ResponseEntity<JSONObject> responseEntity=restTemplate.getForEntity("http://localhost:"+portNo+"/api/creditcard"
                        +"354636ed-7f56-422b-96d5-2dd017b3677e/hasDept", JSONObject.class);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @Order(7)
    public void updateCredit(){
        ResponseEntity<CreditCard> responseEntity=restTemplate.postForEntity("http://localhost:"+portNo+"/api/creditcard"
                +"354636ed-7f56-422b-96d5-2dd017b3677e/updateCredit?credit=2500", null,CreditCard.class);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @Order(8)
    public void updatePassword(){
        ResponseEntity<CreditCard> responseEntity=restTemplate.postForEntity("http://localhost:"+portNo+"/api/creditcard"
                +"/354636ed-7f56-422b-96d5-2dd017b3677e/updatePassword?password=3333", null,CreditCard.class);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @Order(9)
    public void get(){
        ResponseEntity<CreditCard> responseEntity=restTemplate.getForEntity("http://localhost:"+portNo+"/api/creditcard"
                +"/354636ed-7f56-422b-96d5-2dd017b3677e",CreditCard.class);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @Order(10)
    public void delete(){
        this.restTemplate.delete("http://localhost:"+portNo+"/api/creditcard"
                +"?creditcardNumber=354636ed-7f56-422b-96d5-2dd017b3677e");
    }

}

