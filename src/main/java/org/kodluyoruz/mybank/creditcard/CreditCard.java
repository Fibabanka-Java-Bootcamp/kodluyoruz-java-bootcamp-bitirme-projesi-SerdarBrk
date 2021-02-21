package org.kodluyoruz.mybank.creditcard;

import lombok.*;
import org.kodluyoruz.mybank.customer.Customer;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "creditcard")
public class CreditCard {
    @Id
    @GeneratedValue
    private UUID cardNumber;
    private double credit;
    private double debt;
    private String password;
    private String ccv;
    private String expirationMonth;
    private String expirationYear;

    @ManyToOne
    @JoinColumn(name = "customer_id",referencedColumnName = "customerId")
    private Customer customer;

    public CreditCardDto toCreditCardDto(){
        return CreditCardDto.builder()
                .cardNumber(this.cardNumber)
                .credit(this.credit)
                .password(this.password)
                .debt(this.debt)
                .ccv(this.ccv)
                .expirationMonth(this.expirationMonth)
                .expirationYear(this.expirationYear)
                .customer(this.customer.toCustomerDto())
                .build();
    }
}
