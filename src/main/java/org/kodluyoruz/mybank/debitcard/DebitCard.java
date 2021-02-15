package org.kodluyoruz.mybank.debitcard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.kodluyoruz.mybank.account.Account;


import javax.persistence.*;

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
    private String expirationMonth;
    private String expirationYear;

    public DebitCardDto toDebitCardDto(){
        return DebitCardDto.builder()
                .cardNumber(this.cardNumber)
                .account(this.account.toAccountDto())
                .password(this.password)
                .ccv(this.ccv)
                .expirationMonth(this.expirationMonth)
                .expirationYear(this.expirationYear)
                .build();
    }

}
