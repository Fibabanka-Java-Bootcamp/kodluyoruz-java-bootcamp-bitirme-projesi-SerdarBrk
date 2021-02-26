package org.kodluyoruz.mybank.account;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepo extends CrudRepository<Account, UUID> {
    Page<Account> findAllByCustomer_CustomerId(UUID customerId,Pageable pageable);
    Optional<Account> findByIban(UUID iban);


}
