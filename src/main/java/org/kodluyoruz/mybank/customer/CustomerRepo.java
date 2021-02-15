package org.kodluyoruz.mybank.customer;

import org.springframework.data.repository.CrudRepository;


import java.util.UUID;

public interface CustomerRepo extends CrudRepository<Customer, UUID> {

}
