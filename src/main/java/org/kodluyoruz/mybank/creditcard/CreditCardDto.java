package org.kodluyoruz.mybank.creditcard;

import lombok.*;
import org.kodluyoruz.mybank.customer.Customer;
import org.kodluyoruz.mybank.customer.CustomerDto;

import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class CreditCardDto {

    private UUID cardNumber;
    @Min(value=100, message = "Card credit cannot be lower than 100")
    private double credit;
    private String password;
    private double debt;
    private String ccv;
    private LocalDate expirationDate;
    private CustomerDto customer;

    public CreditCard toCreditCard(){
        return CreditCard.builder()
                .cardNumber(this.cardNumber)
                .credit(this.credit)
                .password(this.password)
                .debt(this.debt)
                .ccv(this.ccv)
                .expirationDate(this.expirationDate)
                .customer(this.customer.toCustomer())
                .build();
    }

}
