package org.kodluyoruz.mybank.transaction;

import org.kodluyoruz.mybank.account.AccountRepo;

import org.kodluyoruz.mybank.creditcard.CreditCardRepo;
import org.kodluyoruz.mybank.debitcard.DebitCardRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {
    private final TransactionRepo transactionRepo;
    private final AccountRepo accountRepo;
    private final DebitCardRepo debitcardRepo;
    private final CreditCardRepo creditcardRepo;

    public TransactionService(TransactionRepo transactionRepo, AccountRepo accountRepo, DebitCardRepo debitcardRepo, CreditCardRepo creditcardRepo) {
        this.transactionRepo = transactionRepo;
        this.accountRepo = accountRepo;
        this.debitcardRepo = debitcardRepo;
        this.creditcardRepo = creditcardRepo;
    }

    public Transaction create(Transaction transaction){
        if(transaction.getTransactionType().equals(TransactionType.TRANSFER))
            accountRepo.findById(transaction.getPerformedId())
                    .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Account not found with performedId."));

        if(transaction.getTransactionType().equals(TransactionType.WITHDRAWAL_DEBITCARD))
            debitcardRepo.findById(transaction.getPerformedId())
                    .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"DebitCard not found with performedId."));

        if(transaction.getTransactionType().equals(TransactionType.WITHDRAWAL_ACCOUNT))
            accountRepo.findById(transaction.getPerformedId())
                    .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Account not found with performedId."));

        if(transaction.getTransactionType().equals(TransactionType.DEPOSIT_ACCOUNT))
            accountRepo.findById(transaction.getPerformedId())
                    .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Account not found with performedId."));

        if(transaction.getTransactionType().equals(TransactionType.DEPOSIT_DEBITCARD))
            debitcardRepo.findById(transaction.getPerformedId())
                    .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"DebitCard not found with performedId."));

        if(transaction.getTransactionType().equals(TransactionType.SHOPPING_DEBITCARD))
            debitcardRepo.findById(transaction.getPerformedId())
                    .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"DebitCard not found with performedId."));

        if(transaction.getTransactionType().equals(TransactionType.SHOPPING_ACCOUNT))
            accountRepo.findById(transaction.getPerformedId())
                    .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Account not found with performedId."));

        if(transaction.getTransactionType().equals(TransactionType.SHOPPING_CREDITCARD))
            creditcardRepo.findById(transaction.getPerformedId())
                    .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"CreditCard not found with performedId"));

        if(transaction.getTransactionType().equals(TransactionType.PAYDEBT_CREDITCARD))
            creditcardRepo.findById(transaction.getPerformedId())
                    .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"CreditCard not found with performedId"));

        if(transaction.getTransactionType().equals(TransactionType.PAYDEBT_ACCOUNT))
            accountRepo.findById(transaction.getPerformedId())
                    .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Account not found with performedId."));

        return this.transactionRepo.save(transaction);
    }

    public Page<Transaction> summary(UUID performedid, Pageable pageable){
        return this.transactionRepo.findAllByPerformedId(performedid,pageable);
    }

    public void delete(UUID performedId){
        List<Transaction> transactionList=this.transactionRepo.findAllByPerformedId(performedId);
        if(transactionList != null){
            this.transactionRepo.deleteAll(transactionList);
        }
    }
    public Optional<Transaction> get(int transactionId){return this.transactionRepo.findById(transactionId);}
}
