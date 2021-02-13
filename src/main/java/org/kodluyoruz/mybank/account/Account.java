package org.kodluyoruz.mybank.account;

import lombok.*;
import org.kodluyoruz.mybank.debitcard.DebitCard;
import org.kodluyoruz.mybank.customer.Customer;
import org.kodluyoruz.mybank.transaction.Transaction;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue
    private UUID accountId;

    @Column(unique = true)
    @GeneratedValue
    private UUID iban;

    @ManyToOne
    @JoinColumn(name="customer_id",referencedColumnName ="customerId")
    private Customer customer;
    @Enumerated(value = EnumType.STRING)
    private AccountType accountType;
    @Enumerated(value = EnumType.STRING)
    private MoneyType moneyType;
    private double currency;
    @OneToOne(mappedBy = "account",cascade = CascadeType.ALL)
    private DebitCard debitCard;


    public AccountDto toAccountDto() {
        return AccountDto.builder()
                .accountId(this.accountId)
                .iban(this.iban)
                .customer(customer.toCustomerDto())
                .accountType(this.accountType)
                .moneyType(this.moneyType)
                .currency(this.currency)
                .build();
    }

}
