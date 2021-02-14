package org.kodluyoruz.mybank.account;

import lombok.*;
import org.kodluyoruz.mybank.customer.CustomerDto;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
@Builder
public class AccountDto {
    private UUID accountId;
    private UUID iban;
    private CustomerDto customer;
    private AccountType accountType;
    private MoneyType moneyType;
    private double currency;

    public Account toAccount() {
        return Account.builder()
                .accountId(this.accountId)
                .iban(this.iban)
                .customer(this.customer.toCustomer())
                .accountType(this.accountType)
                .moneyType(this.moneyType)
                .currency(this.currency)
                .build();
    }
}
