package org.kodluyoruz.mybank.account;

import org.json.JSONObject;
import org.kodluyoruz.mybank.transaction.Transaction;
import org.kodluyoruz.mybank.transaction.TransactionRepo;
import org.kodluyoruz.mybank.transaction.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.util.*;

@Service
public class AccountService {
    private final AccountRepo accountRepo;
    private final TransactionRepo transactionRepo;
    public AccountService(AccountRepo accountRepo, TransactionRepo transactionRepo) {
        this.accountRepo = accountRepo;
        this.transactionRepo = transactionRepo;
    }

    public Account create(Account account){
        if(account.getAccountType()==AccountType.CURRENT_ACCOUNT
                && account.getMoneyType()!=MoneyType.TRY){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Money Type must be TRY for CURRENT_ACCOUNT");
        }
        return this.accountRepo.save(account);
    }

    public Page<Account> list(UUID customerId, Pageable pageable){return this.accountRepo.findAllByCustomer_CustomerId(customerId,pageable);}

    public Account sendMoney(UUID accountId,UUID receiverIban,double money){
        Account sender =this.accountRepo.findById(accountId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Sender account not found with accountId: "+accountId));
        Account receiver=this.accountRepo.findByIban(receiverIban)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Receiver account not found with iban: "+receiverIban));

        if(sender.getCurrency() < money)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"There is not enough money in the account");

        RestTemplate restTemplate=new RestTemplate();
        JSONObject jsonObject=new JSONObject(restTemplate
                .getForObject("https://api.exchangeratesapi.io/latest?base="+sender.getMoneyType().toString()
                                +"&symbols="+receiver.getMoneyType().toString(), String.class));

        double rate=jsonObject.getJSONObject("rates").getDouble(receiver.getMoneyType().toString());
        sender.setCurrency(sender.getCurrency()-(money));
        receiver.setCurrency(receiver.getCurrency()+(rate*money));



        Transaction transactionSender=new Transaction();
        transactionSender.setPerformedId(sender.getAccountId());
        transactionSender.setTransactionType(TransactionType.TRANSFER);
        transactionSender.setExplanation("Reicever IBAN:"+receiverIban+" Money: "+money);
        transactionSender.setTransactionDate(LocalDate.now());
        Transaction transactionReceiver=new Transaction();
        transactionReceiver.setPerformedId(receiver.getAccountId());
        transactionReceiver.setTransactionType(TransactionType.TRANSFER);
        transactionReceiver.setExplanation("Sender IBAN:"+ sender.getIban()+" Money: "+money);
        transactionReceiver.setTransactionDate(LocalDate.now());

        this.transactionRepo.save(transactionSender);
        this.transactionRepo.save(transactionReceiver);
        this.accountRepo.save(receiver);
        return this.accountRepo.save(sender);
    }


    public void delete(UUID accountId){
        Account account=this.accountRepo.findById(accountId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Account not found with accountId: "+accountId));
        if(account.getCurrency() != 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"There is money in the account. Account cannot be deleted");
        this.accountRepo.delete(account);
        List<Transaction> transactions=this.transactionRepo.findAllByPerformedId(accountId);
        if(account.getDebitCard() != null)
        {
            RestTemplate restTemplate=new RestTemplate();
            restTemplate.delete("http://localhost:8080/api/debitCard?debitcardNumber="+ account.getDebitCard().getCardNumber());
        }

        if(transactions != null){
            this.transactionRepo.deleteAll(transactions);
        }
    }

    public Optional<Account> get(UUID accountId){return this.accountRepo.findById(accountId);}

}
