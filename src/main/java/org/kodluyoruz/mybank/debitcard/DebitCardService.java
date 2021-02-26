package org.kodluyoruz.mybank.debitcard;

import org.json.JSONObject;
import org.kodluyoruz.mybank.account.Account;
import org.kodluyoruz.mybank.account.AccountRepo;
import org.kodluyoruz.mybank.transaction.Transaction;
import org.kodluyoruz.mybank.transaction.TransactionRepo;
import org.kodluyoruz.mybank.transaction.TransactionType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DebitCardService {
    private final DebitCardRepo debitCardRepo;
    private final AccountRepo accountRepo;
    private final TransactionRepo transactionRepo;

    public DebitCardService(DebitCardRepo debitCardRepo, AccountRepo accountRepo, TransactionRepo transactionRepo) {
        this.debitCardRepo = debitCardRepo;
        this.accountRepo = accountRepo;
        this.transactionRepo = transactionRepo;
    }

    public DebitCard create(DebitCard debitCard){return this.debitCardRepo.save(debitCard);}

    public DebitCard updateAccount(UUID debitcardNumber,UUID accountId){
        Account account=this.accountRepo.findById(accountId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Account not found with id: "+accountId));
        DebitCard debitCard=this.debitCardRepo.findById(debitcardNumber)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Debitcard not found with number: "+debitcardNumber));
        debitCard.setAccount(account);
        return  this.debitCardRepo.save(debitCard);
    }

    public DebitCard updatePassword(UUID debitcardNumber,String password){
        DebitCard debitCard=this.debitCardRepo.findById(debitcardNumber)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Debitcard not found with number: "+debitcardNumber));
        debitCard.setPassword(password);
        return this.debitCardRepo.save(debitCard);
    }

    public DebitCard withdrawlMoneyFromAtm(UUID debitcardNumber,String password,double money){
       DebitCard debitCard=this.debitCardRepo.findById(debitcardNumber)
                    .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"DebitCard not found with number"+debitcardNumber));

       if(!debitCard.getPassword().equals(password))
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Password is incorrect");

       if (money > debitCard.getAccount().getCurrency())
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"There is not enough money in the account");

       debitCard.getAccount().setCurrency(debitCard.getAccount().getCurrency()-money);
       Transaction transactionAccount=new Transaction();
       transactionAccount.setPerformedId(debitCard.getAccount().getAccountId());
       transactionAccount.setTransactionType(TransactionType.WITHDRAWAL_ACCOUNT);
       transactionAccount.setExplanation("Withdrawl money from ATM, Money: "+money);
       transactionAccount.setTransactionDate(LocalDate.now());

       Transaction transactionDebitCard=new Transaction();
       transactionDebitCard.setPerformedId(debitcardNumber);
       transactionDebitCard.setTransactionType(TransactionType.WITHDRAWAL_DEBITCARD);
       transactionDebitCard.setExplanation("Withdrawl money from ATM, Money: "+money);
       transactionDebitCard.setTransactionDate(LocalDate.now());

       this.transactionRepo.save(transactionAccount);
       this.transactionRepo.save(transactionDebitCard);
       this.accountRepo.save(debitCard.getAccount());
       return this.debitCardRepo.save(debitCard);
    }
    public DebitCard depositMoneyFromAtm(UUID debitcardNumber,String password,double money){
        DebitCard debitCard=this.debitCardRepo.findById(debitcardNumber)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"DebitCard not found with number"+debitcardNumber));
        if(!debitCard.getPassword().equals(password))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Password is incorrect");
        if (money<0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Money must be greater than 0: "+money);

        debitCard.getAccount().setCurrency(debitCard.getAccount().getCurrency()+money);

        Transaction transactionAccount=new Transaction();
        transactionAccount.setPerformedId(debitCard.getAccount().getAccountId());
        transactionAccount.setTransactionType(TransactionType.DEPOSIT_ACCOUNT);
        transactionAccount.setExplanation("DEPOSIT from ATM, Money: "+money);
        transactionAccount.setTransactionDate(LocalDate.now());

        Transaction transactionDebitCard=new Transaction();
        transactionDebitCard.setPerformedId(debitcardNumber);
        transactionDebitCard.setTransactionType(TransactionType.DEPOSIT_DEBITCARD);
        transactionDebitCard.setExplanation("DEPOSIT from ATM, Money: "+money);
        transactionDebitCard.setTransactionDate(LocalDate.now());

        this.transactionRepo.save(transactionAccount);
        this.transactionRepo.save(transactionDebitCard);
        this.accountRepo.save(debitCard.getAccount());
        return this.debitCardRepo.save(debitCard);
    }

    public DebitCard shopping(UUID debitcardNumber,UUID receiverIban,String password,double money){

        DebitCard debitCard=this.debitCardRepo.findById(debitcardNumber)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"DebitCard not found with number"+debitcardNumber));
        Account receiverAccount=this.accountRepo.findByIban(receiverIban)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Receiver Account not found with iban"+receiverIban));
        if (money<0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Money must be greater than 0: "+money);
        if(!debitCard.getPassword().equals(password))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Password is incorrect");

        RestTemplate restTemplate=new RestTemplate();
        JSONObject jsonObject=new JSONObject(restTemplate
                .getForObject("https://api.exchangeratesapi.io/latest?base="+debitCard.getAccount().getMoneyType().toString()
                                +"&symbols="+receiverAccount.getMoneyType().toString(), String.class));

        double rate=jsonObject.getJSONObject("rates").getDouble(receiverAccount.getMoneyType().toString());
        if(debitCard.getAccount().getCurrency() < money*rate)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"There is not enough credit in the creditcard");
        debitCard.getAccount().setCurrency(debitCard.getAccount().getCurrency()-(money*rate));
        receiverAccount.setCurrency(receiverAccount.getCurrency()+(money));

        Transaction transactionAccount=new Transaction();
        transactionAccount.setPerformedId(debitCard.getAccount().getAccountId());
        transactionAccount.setTransactionType(TransactionType.SHOPPING_ACCOUNT);
        transactionAccount.setExplanation("Receiver IBAN: "+receiverIban+", Money: "+money);
        transactionAccount.setTransactionDate(LocalDate.now());

        Transaction transactionDebitCard=new Transaction();
        transactionDebitCard.setPerformedId(debitcardNumber);
        transactionDebitCard.setTransactionType(TransactionType.SHOPPING_DEBITCARD);
        transactionDebitCard.setExplanation("Receiver IBAN: "+receiverIban+", Money: "+money);
        transactionDebitCard.setTransactionDate(LocalDate.now());

        Transaction transactionReceiverAccount=new Transaction();
        transactionReceiverAccount.setPerformedId(debitCard.getAccount().getAccountId());
        transactionReceiverAccount.setTransactionType(TransactionType.TRANSFER);
        transactionReceiverAccount.setExplanation("Sender Card Id: "+debitcardNumber+", Money: "+money);
        transactionReceiverAccount.setTransactionDate(LocalDate.now());

        this.transactionRepo.save(transactionAccount);
        this.transactionRepo.save(transactionDebitCard);
        this.transactionRepo.save(transactionReceiverAccount);

        this.accountRepo.save(debitCard.getAccount());
        this.accountRepo.save(receiverAccount);
        return this.debitCardRepo.save(debitCard);
    }
    public DebitCard onlineShopping(UUID receiverIban,UUID debitcardNumber,String password
            ,String ccv, String expirationMonth,String expirationYear,double money){
        DebitCard debitCard=this.debitCardRepo.findById(debitcardNumber)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"DebitCard not found with number"+debitcardNumber));
        Account receiverAccount=this.accountRepo.findByIban(receiverIban)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Receiver Account not found with iban"+receiverIban));
        if (money<0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Money must be greater than 0: "+money);
        if(!debitCard.getPassword().equals(password))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Password is incorrect");
        if(!debitCard.getCcv().equals(ccv))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"CCV is incorrect");
        if(!debitCard.getExpirationMonth().equals(expirationMonth))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"ExpirationMonth is incorrect");
        if(!debitCard.getExpirationYear().equals(expirationYear))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"ExpirationYear is incorrect");

        RestTemplate restTemplate=new RestTemplate();
        JSONObject jsonObject=new JSONObject(restTemplate
                .getForObject("https://api.exchangeratesapi.io/latest?base="+debitCard.getAccount().getMoneyType().toString()
                        +"&symbols="+receiverAccount.getMoneyType().toString(), String.class));

        double rate=jsonObject.getJSONObject("rates").getDouble(receiverAccount.getMoneyType().toString());
        if(debitCard.getAccount().getCurrency() < money*rate)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"There is not enough credit in the creditcard");
        debitCard.getAccount().setCurrency(debitCard.getAccount().getCurrency()-(money*rate));
        receiverAccount.setCurrency(receiverAccount.getCurrency()+(money));

        Transaction transactionAccount=new Transaction();
        transactionAccount.setPerformedId(debitCard.getAccount().getAccountId());
        transactionAccount.setTransactionType(TransactionType.SHOPPING_ACCOUNT);
        transactionAccount.setExplanation("OnlineShopping,Receiver IBAN: "+receiverIban+", Money: "+money);
        transactionAccount.setTransactionDate(LocalDate.now());

        Transaction transactionDebitCard=new Transaction();
        transactionDebitCard.setPerformedId(debitcardNumber);
        transactionDebitCard.setTransactionType(TransactionType.SHOPPING_DEBITCARD);
        transactionDebitCard.setExplanation("OnlineShopping,Receiver IBAN: "+receiverIban+", Money: "+money);
        transactionDebitCard.setTransactionDate(LocalDate.now());

        Transaction transactionReceiverAccount=new Transaction();
        transactionReceiverAccount.setPerformedId(debitCard.getAccount().getAccountId());
        transactionReceiverAccount.setTransactionType(TransactionType.TRANSFER);
        transactionReceiverAccount.setExplanation("Sender Card Id: "+debitcardNumber+", Money: "+money);
        transactionReceiverAccount.setTransactionDate(LocalDate.now());

        this.transactionRepo.save(transactionAccount);
        this.transactionRepo.save(transactionDebitCard);
        this.transactionRepo.save(transactionReceiverAccount);

        this.accountRepo.save(debitCard.getAccount());
        this.accountRepo.save(receiverAccount);
        return this.debitCardRepo.save(debitCard);
    }
    public void delete(UUID debitcardNumber){
        DebitCard debitCard=this.debitCardRepo.findById(debitcardNumber)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND
                        ,"Debitcard not found with number: "+debitcardNumber));


        this.debitCardRepo.delete(debitCard);
        List<Transaction> transactions=this.transactionRepo.findAllByPerformedId(debitcardNumber);
        if(transactions != null){
            this.transactionRepo.deleteAll(transactions);
        }

    }
    public Optional<DebitCard> get(UUID debitcardNumber){return this.debitCardRepo.findById(debitcardNumber);}
}
