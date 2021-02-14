package org.kodluyoruz.mybank.debitcard;

import lombok.Builder;
import lombok.Data;
import org.kodluyoruz.mybank.account.Account;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.UUID;

@Data
@Builder
public class DebitCardDto {
    private UUID cardNumber;
    private Account account;
    @NotBlank(message = "Password for the creditcard is mandatory")
    @Pattern(regexp ="^[0-9]{4}",message = "password length must be 4 and password contains only alphanumeric characters")
    private String password;
    @NotBlank(message = "CCV for the creditcard is mandatory")
    @Pattern(regexp ="^[0-9]{3}",message = "ccv length must be 3 and ccv contains only alphanumeric characters")
    private String ccv;
    @NotBlank(message = "Expirationmonth for the creditcard is mandatory")
    @Pattern(regexp = "^(1[0-2]|0[1-9])$",message = "")
    private String expirationMonth;
    @NotBlank(message = "Expirationyear for the creditcard is mandatory")
    @Pattern(regexp = "^(20[2-9][0-9])$",message = "")
    private String expirationYear;

    public DebitCard toDebitCard(){
        return DebitCard.builder()
                .cardNumber(this.cardNumber)
                .account(this.account)
                .password(this.password)
                .ccv(this.ccv)
                .expirationMonth(this.expirationMonth)
                .expirationYear(this.expirationYear)
                .build();
    }
}
