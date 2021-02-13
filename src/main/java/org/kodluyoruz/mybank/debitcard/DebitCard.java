package org.kodluyoruz.mybank.debitcard;

import lombok.*;
import org.kodluyoruz.mybank.account.Account;
import org.kodluyoruz.mybank.customer.Customer;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "debitcard")
public class DebitCard {

    @Id
    @GeneratedValue
    private UUID cardNumber;
    @OneToOne
    @JoinColumn(name = "account_id",referencedColumnName = "accountId")
    private Account account;
    private String password;
    private String ccv;
    private LocalDate expirationDate;
    @ManyToOne
    @JoinColumn(name="customer_id",referencedColumnName = "customerId")
    private Customer customer;

    public DebitCardDto toDebitCardDto(){
        return DebitCardDto.builder()
                .cardNumber(this.cardNumber)
                .account(this.account)
                .password(this.password)
                .ccv(this.ccv)
                .expirationDate(this.expirationDate)
                .customer(this.customer.toCustomerDto())
                .build();
    }


}
