package org.kodluyoruz.mybank.creditcard;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.kodluyoruz.mybank.customer.CustomerDto;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class CreditCardDto {

    private UUID cardNumber;
    @NotBlank(message = "Credit for the creditcard is mandatory")
    @Min(value=100, message = "Card credit cannot be lower than 100")
    private double credit;
    @NotBlank(message = "Password for the creditcard is mandatory")
    @Length()
    private String password;
    @Min(value = 0,message = "Card debt must be 0")
    @Max(value = 0,message = "Card debt must be 0")
    private double debt;
    @NotBlank(message = "CCV for the creditcard is mandatory")
    private String ccv;
    @NotBlank(message = "Expirationmonth for the creditcard is mandatory")
    @DateTimeFormat(pattern = "MM")
    private String expirationMonth;
    @NotBlank(message = "Expirationyear for the creditcard is mandatory")
    private String expirationYear;
    @NotBlank(message = "Customer for the creditcard is mandatory")
    private CustomerDto customer;

    public CreditCard toCreditCard(){
        return CreditCard.builder()
                .cardNumber(this.cardNumber)
                .credit(this.credit)
                .password(this.password)
                .debt(this.debt)
                .ccv(this.ccv)
                .expirationMonth(this.expirationMonth)
                .expirationYear(this.expirationYear)
                .customer(this.customer.toCustomer())
                .build();
    }

}
