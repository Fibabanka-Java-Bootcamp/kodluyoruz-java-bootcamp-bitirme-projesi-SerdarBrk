package org.kodluyoruz.mybank.accountTest;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.kodluyoruz.mybank.account.*;
import org.kodluyoruz.mybank.customer.Customer;
import org.kodluyoruz.mybank.customer.CustomerDto;
import org.kodluyoruz.mybank.customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;


import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource({ "classpath:application.properties" })

public class AccountServiceIntegrationTest {



    @Autowired
    private AccountService accountService;

    @Autowired
    private CustomerService customerService;

    @Test
    @Order(1)
    public void create() {
        UUID iban = UUID.randomUUID();

        Account account = AccountDto.builder()
                .iban(iban)
                .customer(this.customerService
                        .get(UUID.fromString("cdfb4132-e333-4eba-b472-efcbf79cbdd0"))
                        .get().toCustomerDto())
                .accountType(AccountType.CURRENT_ACCOUNT)
                .moneyType(MoneyType.TRY)
                .currency(100)
                .build().toAccount();
        Account create = accountService.create(account);
        assertNotNull(create);
    }

    @Test
    @Order(2)
    public void delete(){
        UUID id=UUID.fromString("0512fa21-2609-4e28-ab84-dcbf3ba046ee");
        accountService.delete(id);
    }

    @Test
    @Order(3)
    public void listByCustomerIdReturnPageAccount(){
        UUID id=UUID.fromString("cdfb4132-e333-4eba-b472-efcbf79cbdd0");
        Page<Account> accounts=this.accountService.list(id, PageRequest.of(0, 20));
        assertNotNull(accounts);
    }

    @Test
    @Order(4)
    public void sendMoneyReturnAccount(){
        UUID sender=UUID.fromString("f5884081-d4b5-4a62-ba88-f19f6fc902ad");
        UUID receiver=UUID.fromString("3fa85f63-5717-4562-b3fc-2c463f66afa5");
        Account account=this.accountService.sendMoney(sender,receiver,10);
        assertNotNull(account);
    }

    @Test
    @Order(5)
    public void get(){
        UUID id=UUID.fromString("f5884081-d4b5-4a62-ba88-f19f6fc902ad");
        assertNotNull(accountService.get(id));
    }

}
