package org.kodluyoruz.mybank.customer;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.kodluyoruz.mybank.address.Address;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
@Builder
public class CustomerDto {

    private UUID customerId;
    @NotBlank(message = "TC for the customer is mandatory")
    @Length(min = 11,max = 11,message = "TC must have 11 digits")
    private String tc;
    @NotBlank(message = "Name for the customer is mandatory")
    private String name;
    @NotBlank(message = "Surname for the customer is mandatory")
    private String surname;
    @NotBlank(message = "Phone number for the customer is mandatory,Ex:05xxxxxxxxx")
    @Length(min = 11,max = 11,message = "Phone number must have 11 digits")
    private String phoneNumber;
    private Address address;

    public Customer toCustomer() {
        return Customer.builder()
                .customerId(this.customerId)
                .tc(this.tc)
                .name(this.name)
                .surname(this.surname)
                .phoneNumber(this.phoneNumber)
                .address(this.address)
                .build();
    }

}
