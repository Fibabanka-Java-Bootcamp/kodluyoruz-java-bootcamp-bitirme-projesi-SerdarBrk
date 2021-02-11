package org.kodluyoruz.mybank.debitcard;

import lombok.Builder;
import lombok.Data;
import org.kodluyoruz.mybank.account.Account;
import org.kodluyoruz.mybank.customer.CustomerDto;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class DebitCardDto {
    private UUID cardNumber;
    private Account account;
    private String password;
    private String ccv;
    private LocalDate expirationDate;
    private CustomerDto customer;

    public DebitCard toDebitCard(){
        return DebitCard.builder()
                .cardNumber(this.cardNumber)
                .account(this.account)
                .password(this.password)
                .ccv(this.ccv)
                .expirationDate(this.expirationDate)
                .customer(this.customer.toCustomer())
                .build();
    }
}
