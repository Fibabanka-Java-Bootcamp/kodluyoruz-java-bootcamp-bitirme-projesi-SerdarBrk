package org.kodluyoruz.mybank.creditcard;

import lombok.*;
import org.kodluyoruz.mybank.customer.Customer;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
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
    @Pattern(regexp ="^[0-9]{4}",message = "password length must be 4 and password contains only alphanumeric characters")
    private String password;
    @Pattern(regexp ="^[0-9]{3}",message = "ccv length must be 3 and ccv contains only alphanumeric characters")
    private String ccv;
    @Pattern(regexp = "^(1[0-2]|0[1-9])$",message = "")
    private String expirationMonth;
    @Pattern(regexp = "^(20[2-9][0-9])$",message = "")
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
