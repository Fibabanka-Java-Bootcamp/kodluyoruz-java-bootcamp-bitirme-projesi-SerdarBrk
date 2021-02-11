package org.kodluyoruz.mybank.creditcard;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface CreditCardRepo extends CrudRepository<CreditCard, UUID> {
}
