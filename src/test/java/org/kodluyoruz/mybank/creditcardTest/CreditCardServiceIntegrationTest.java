package org.kodluyoruz.mybank.creditcardTest;

import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.kodluyoruz.mybank.creditcard.CreditCard;
import org.kodluyoruz.mybank.creditcard.CreditCardDto;
import org.kodluyoruz.mybank.creditcard.CreditCardService;
import org.kodluyoruz.mybank.customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource({ "classpath:application.properties" })
public class CreditCardServiceIntegrationTest {
    @Autowired
    private CreditCardService creditCardService;

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
        assertNotNull(this.creditCardService.create(creditCard));
    }
    @Test
    @Order(2)
    public void payDeptFromAtm(){
        assertNotNull(this.creditCardService.payDeptFromAtm(UUID.fromString("6434d0a3-914b-4df0-b110-e23b2ba8990b")
                        ,"1234",50));
    }

    @Test
    @Order(3)
    public void payDebtFromAccount(){
        assertNotNull(this.creditCardService.payDebtFromAccount(UUID.fromString("f5884081-d4b5-4a62-ba88-f19f6fc902ad")
                ,UUID.fromString("6434d0a3-914b-4df0-b110-e23b2ba8990b")
                ,50));
    }

    @Test
    @Order(4)
    public void shopping(){
        assertNotNull(this.creditCardService.shopping(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa5")
                ,UUID.fromString("6434d0a3-914b-4df0-b110-e23b2ba8990b")
                ,"1234",50));
    }

    @Test
    @Order(5)
    public void onlineShopping(){
        assertNotNull(this.creditCardService.onlineShopping(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa5")
                ,UUID.fromString("6434d0a3-914b-4df0-b110-e23b2ba8990b")
                ,"1234","123","05","2023",50));

    }

    @Test
    @Order(6)
    public void hasDept(){
        assertNotNull(this.creditCardService.hasDebt(UUID.fromString("6434d0a3-914b-4df0-b110-e23b2ba8990b")));
    }

    @Test
    @Order(7)
    public void updateCredit(){
        assertNotNull(this.creditCardService.updateCredit(UUID.fromString("6434d0a3-914b-4df0-b110-e23b2ba8990b"),1000));
    }

    @Test
    @Order(8)
    public void updatePassword(){
        assertNotNull(this.creditCardService.updatePassword(UUID.fromString("6434d0a3-914b-4df0-b110-e23b2ba8990b"),"2354"));
    }
    @Test
    @Order(9)
    public void get(){
        assertNotNull(this.creditCardService.get(UUID.fromString("6434d0a3-914b-4df0-b110-e23b2ba8990b")).get());
    }

    @Test
    @Order(10)
    public void delete(){
        this.creditCardService.delete(UUID.fromString("6434d0a3-914b-4df0-b110-e23b2ba8990b"));
    }
}
