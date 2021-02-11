package org.kodluyoruz.mybank.address;

import lombok.*;
import org.kodluyoruz.mybank.customer.Customer;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue
    private int addressId;
    private String address;
    private String district;
    private String city;
    private String country;
    @OneToOne(mappedBy = "address")
    private Customer customer;

    public AddressDto toAddressDto(){
        return AddressDto.builder()
                .addressId(this.addressId)
                .address(this.address)
                .district(this.district)
                .city(this.city)
                .country(this.country)
                .customer(this.customer)
                .build();
    }
}
