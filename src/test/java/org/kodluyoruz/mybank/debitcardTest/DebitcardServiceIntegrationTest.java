package org.kodluyoruz.mybank.debitcardTest;

import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.kodluyoruz.mybank.account.AccountService;
import org.kodluyoruz.mybank.debitcard.DebitCard;
import org.kodluyoruz.mybank.debitcard.DebitCardDto;
import org.kodluyoruz.mybank.debitcard.DebitCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource({ "classpath:application.properties" })
public class DebitcardServiceIntegrationTest {
    @Autowired
    private DebitCardService debitCardService;
    @Autowired
    private AccountService accountService;

    @Test
    @Order(1)
    public void create(){
        DebitCard debitCard= DebitCardDto.builder()
                .account(accountService.get(UUID.fromString("d3b88114-673e-481b-beee-e8fe4f49c8b2"))
                        .get())
                .password("1234")
                .ccv("324")
                .expirationMonth("05")
                .expirationYear("2022")
                .build().toDebitCard();
        assertNotNull(this.debitCardService.create(debitCard));
    }

    @Test
    @Order(2)
    public void updateAccount(){
        assertNotNull(this.debitCardService.updateAccount(UUID.fromString("3750f985-3ff6-4c9c-ad63-074083cb565e"),
                UUID.fromString("1d67bd94-7c2e-4a62-8edd-1104c077b715")));
    }
    @Test
    @Order(3)
    public void updatePassword(){
        UUID number=UUID.fromString("3750f985-3ff6-4c9c-ad63-074083cb565e");
        assertNotNull(this.debitCardService.updatePassword(number,
                "3569"));
    }

    @Test
    @Order(4)
    public void withdrawlMoneyFromAtm(){
        UUID number=UUID.fromString("3750f985-3ff6-4c9c-ad63-074083cb565e");
        assertNotNull(this.debitCardService.withdrawlMoneyFromAtm(number,"3569",30));
    }

    @Test
    @Order(5)
    public void depositMoneyFromAtm(){
        UUID number=UUID.fromString("3750f985-3ff6-4c9c-ad63-074083cb565e");
        assertNotNull(this.debitCardService.depositMoneyFromAtm(number,"3569",200));
    }

    @Test
    @Order(6)
    public void shopping(){
        UUID number=UUID.fromString("3750f985-3ff6-4c9c-ad63-074083cb565e");
        UUID receiverIban=UUID.fromString("3fa85f63-5717-4562-b3fc-2c463f66afa5");
        assertNotNull(this.debitCardService.shopping(number,receiverIban,"3569",200));
    }

    @Test
    @Order(7)
    public void onlineShopping(){
        UUID number=UUID.fromString("3750f985-3ff6-4c9c-ad63-074083cb565e");
        UUID receiverIban=UUID.fromString("3fa85f63-5717-4562-b3fc-2c463f66afa5");

        assertNotNull(this.debitCardService.onlineShopping(receiverIban,number,"3569",
                "324","05","2022",30));
    }

    @Test
    @Order(8)
    public void get(){
        UUID number=UUID.fromString("3750f985-3ff6-4c9c-ad63-074083cb565e");
        assertNotNull(this.debitCardService.get(number).get());
    }

    @Test
    @Order(9)
    public void delete(){
        this.debitCardService.delete(UUID.fromString("3750f985-3ff6-4c9c-ad63-074083cb565e"));
    }
}
