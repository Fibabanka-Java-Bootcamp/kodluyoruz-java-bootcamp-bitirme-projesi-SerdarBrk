package org.kodluyoruz.mybank.customer;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.UUID;

@Data
@Builder
public class CustomerDto {

    private UUID customerId;
    @NotBlank(message = "TC for the customer is mandatory")
    @Pattern(regexp = "^[0-9]{11}",message = "TC  length must be 11 and TC contains only alphanumeric characters.")
    private String tc;
    @NotBlank(message = "Name for the customer is mandatory")
    private String name;
    @NotBlank(message = "Surname for the customer is mandatory")
    private String surname;
    @NotBlank(message = "PhoneNumber for the customer is mandatory. Ex:+(123)-456-78-90")
    @Pattern(regexp = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$",message = "PhoneNumber Ex:+(123)-456-78-90")
    private String phoneNumber;


    public Customer toCustomer() {
        return Customer.builder()
                .customerId(this.customerId)
                .tc(this.tc)
                .name(this.name)
                .surname(this.surname)
                .phoneNumber(this.phoneNumber)
                .build();
    }

}
