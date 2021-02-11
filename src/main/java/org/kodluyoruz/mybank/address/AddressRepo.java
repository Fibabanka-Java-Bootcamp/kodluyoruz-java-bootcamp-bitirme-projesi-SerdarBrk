package org.kodluyoruz.mybank.address;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepo extends CrudRepository<Address,Integer> {
    Page<Address> findAllByCity(String city, Pageable pageable);
    Page<Address> findAllByDistrict(String district, Pageable pageable);
    Page<Address> findAllByCityAndDistrict(String city,String district, Pageable pageable);
    Page<Address> findAllByCountry(String country,Pageable pageable);
    Page<Address> findAllByCountryAndCityAndDistrict(String country,String city,String district, Pageable pageable);
}
