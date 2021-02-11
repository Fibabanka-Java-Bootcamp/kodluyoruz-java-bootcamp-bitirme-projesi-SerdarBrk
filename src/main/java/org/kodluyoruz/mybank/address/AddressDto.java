package org.kodluyoruz.mybank.address;

import lombok.Builder;
import lombok.Data;
import org.kodluyoruz.mybank.customer.Customer;
@Data
@Builder
public class AddressDto {

    private int addressId;
    private String address;
    private String district;
    private String  city;
    private String country;
    private Customer customer;

    public Address toAddress(){
        return Address.builder()
                .addressId(this.addressId)
                .address(this.address)
                .district(this.district)
                .city(this.city)
                .country(this.country)
                .customer(this.customer)
                .build();
    }
}
