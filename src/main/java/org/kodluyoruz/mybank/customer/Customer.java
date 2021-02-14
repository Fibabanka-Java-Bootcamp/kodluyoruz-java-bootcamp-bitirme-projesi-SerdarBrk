package org.kodluyoruz.mybank.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.kodluyoruz.mybank.account.Account;
import org.kodluyoruz.mybank.debitcard.DebitCard;
import org.kodluyoruz.mybank.creditcard.CreditCard;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customer")
public class Customer {
    @Id
    private UUID customerId;
    @Column(unique = true)
    private String tc;
    private String name;
    private String surname;
    @Column(unique = true)
    private String phoneNumber;
    @JsonIgnore
    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL)
    private Set<Account> accounts;
    @JsonIgnore
    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL)
    private Set<CreditCard> creditCards;


    public CustomerDto toCustomerDto() {
        return CustomerDto.builder()
                .customerId(this.customerId)
                .tc(this.tc)
                .name(this.name)
                .surname(this.surname)
                .phoneNumber(this.phoneNumber)
                .build();
    }

}
