package org.kodluyoruz.mybank.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface CustomerRepo extends CrudRepository<Customer, UUID> {

    Page<Customer> findAllByName(String name, Pageable pageable);
    Page<Customer> findAllBySurname(String name, Pageable pageable);
    Page<Customer> findAllByNameAndSurname(String name,String surname,Pageable pageable);

    Page<Customer> findAllByNameAndCreditCardsNotNull(String name,Pageable pageable);
    Page<Customer> findAllByNameAndAccountsNotNull(String name,Pageable pageable);

    Customer findByTc(String tc);

}
