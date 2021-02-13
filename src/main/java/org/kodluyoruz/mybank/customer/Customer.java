package org.kodluyoruz.mybank.customer;

import lombok.*;
import org.kodluyoruz.mybank.account.Account;
import org.kodluyoruz.mybank.address.Address;
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
    @GeneratedValue
    private UUID customerId;
    @Column(unique = true)
    @Pattern(regexp = "^[0-9]{11}",message = "TC password length must be 4 and password contains only alphanumeric characters")
    private String tc;
    private String name;
    private String surname;
    @Column(unique = true,length = 11)
    private String phoneNumber;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(columnDefinition = "addresId",referencedColumnName ="addressId")
    private Address address;
    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL)
    private Set<Account> accounts;
    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL)
    private Set<CreditCard> creditCards;
    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL)
    private Set<DebitCard> debitCards;

    public CustomerDto toCustomerDto() {
        return CustomerDto.builder()
                .customerId(this.customerId)
                .tc(this.tc)
                .name(this.name)
                .surname(this.surname)
                .phoneNumber(this.phoneNumber)
                .address(this.address)
                .build();
    }

}
