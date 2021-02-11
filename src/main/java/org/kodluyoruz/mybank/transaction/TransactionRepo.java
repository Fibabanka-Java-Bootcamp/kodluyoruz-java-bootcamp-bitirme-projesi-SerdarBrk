package org.kodluyoruz.mybank.transaction;

import org.springframework.data.repository.CrudRepository;

public interface TransactionRepo extends CrudRepository<Transaction,Integer> {
}
