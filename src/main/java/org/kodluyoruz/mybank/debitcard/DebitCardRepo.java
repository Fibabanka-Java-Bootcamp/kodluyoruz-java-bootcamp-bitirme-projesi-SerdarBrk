package org.kodluyoruz.mybank.debitcard;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface DebitCardRepo extends CrudRepository<DebitCard, UUID> {

}
