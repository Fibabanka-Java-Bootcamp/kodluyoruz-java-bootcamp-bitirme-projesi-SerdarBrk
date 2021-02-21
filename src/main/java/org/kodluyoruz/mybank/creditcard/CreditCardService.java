package org.kodluyoruz.mybank.creditcard;

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
import java.util.regex.Pattern;

@Service
public class CreditCardService {
    private final CreditCardRepo creditCardRepo;
    private final AccountRepo accountRepo;
    private final TransactionRepo transactionRepo;

    public CreditCardService(CreditCardRepo creditCardRepo, AccountRepo accountRepo, TransactionRepo transactionRepo) {
        this.creditCardRepo = creditCardRepo;
        this.accountRepo = accountRepo;
        this.transactionRepo = transactionRepo;
    }

    public CreditCard create(CreditCard creditCard){
        return this.creditCardRepo.save(creditCard);
    }

    public CreditCard payDeptFromAtm(UUID creditcardNumber,String password,double money){
        CreditCard creditCard=this.creditCardRepo.findById(creditcardNumber)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Creditcard not found with number: "+creditcardNumber));
        if(!creditCard.getPassword().equals(password))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Password is incorrect");
        if (money<0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Money must be greater than 0: "+money);
        if(creditCard.getDebt()<=0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"There is no debt");
        creditCard.setDebt(creditCard.getDebt()-money);
        Transaction transactionCreditCard=new Transaction();
        transactionCreditCard.setPerformedId(creditcardNumber);
        transactionCreditCard.setTransactionType(TransactionType.PAY_DEBT);
        transactionCreditCard.setExplanation("Pay debt from ATM, Money: "+money);
        transactionCreditCard.setTransactionDate(LocalDate.now());
        this.transactionRepo.save(transactionCreditCard);
        return this.creditCardRepo.save(creditCard);
    }

    public CreditCard payDebtFromAccount(UUID accountId,UUID creditcardNumber,double money){
        CreditCard creditCard=this.creditCardRepo.findById(creditcardNumber)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Creditcard not found with number: "+creditcardNumber));
        Account account=this.accountRepo.findById(accountId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Account not found with accountId: "+accountId));
        if(creditCard.getDebt()<=0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"There is no debt");
        if (money<0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Money must be greater than 0: "+money);
        if(account.getCurrency() < money)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"There is not enough money in the account");
        RestTemplate restTemplate=new RestTemplate();
        JSONObject jsonObject=new JSONObject(restTemplate.getForObject("https://api.exchangeratesapi.io/latest?base="+account.getMoneyType().toString()
                +"&symbols=", String.class));
        double rate=jsonObject.getJSONObject("rates").getDouble("TRY");

        account.setCurrency(account.getCurrency()-money);
        creditCard.setDebt(creditCard.getDebt()-(money*rate));

        Transaction transactionAccount=new Transaction();
        transactionAccount.setPerformedId(accountId);
        transactionAccount.setTransactionType(TransactionType.PAY_DEBT);
        transactionAccount.setExplanation("CreditCard Number: "+creditcardNumber+",Money: "+money);
        transactionAccount.setTransactionDate(LocalDate.now());

        Transaction transactionCreditCard=new Transaction();
        transactionCreditCard.setPerformedId(creditcardNumber);
        transactionCreditCard.setTransactionType(TransactionType.PAY_DEBT);
        transactionCreditCard.setExplanation("Sender Account Id: "+accountId+",Money: "+money);
        transactionCreditCard.setTransactionDate(LocalDate.now());

        this.transactionRepo.save(transactionAccount);
        this.transactionRepo.save(transactionCreditCard);
        this.accountRepo.save(account);
        return this.creditCardRepo.save(creditCard);
    }

    public CreditCard shopping(UUID receiverAccountIban,UUID creditcardNumber,String password,double money){
        CreditCard creditCard=this.creditCardRepo.findById(creditcardNumber)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Creditcard not found with number: "+creditcardNumber));
        Account receiver=this.accountRepo.findByIban(receiverAccountIban)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Receiver account not found with iban: "+receiverAccountIban));

        if (money<0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Money must be greater than 0: "+money);
        if(!creditCard.getPassword().equals(password))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Password is incorrect");
        RestTemplate restTemplate=new RestTemplate();
        JSONObject jsonObject=new JSONObject(restTemplate.getForObject("https://api.exchangeratesapi.io/latest?base="+receiver.getMoneyType().toString()+"&symbols=TRY"
                , String.class));
        double rate=jsonObject.getJSONObject("rates").getDouble("TRY");
        if((creditCard.getCredit()-creditCard.getDebt()) < rate*money)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"There is not enough credit in the creditcard");
        receiver.setCurrency(receiver.getCurrency()+(money));
        creditCard.setDebt(creditCard.getDebt()+(rate*money));

        Transaction transactionAccount=new Transaction();
        transactionAccount.setPerformedId(receiver.getAccountId());
        transactionAccount.setTransactionType(TransactionType.TRANSFER);
        transactionAccount.setExplanation("Sender CreditCard number: "+creditcardNumber+",Money: "+money);
        transactionAccount.setTransactionDate(LocalDate.now());

        Transaction transactionCreditCard=new Transaction();
        transactionCreditCard.setPerformedId(creditcardNumber);
        transactionCreditCard.setTransactionType(TransactionType.SHOPPING);
        transactionCreditCard.setExplanation("Receiver Account IBAN: "+receiverAccountIban+",Money: "+money);
        transactionCreditCard.setTransactionDate(LocalDate.now());

        this.transactionRepo.save(transactionAccount);
        this.transactionRepo.save(transactionCreditCard);
        this.accountRepo.save(receiver);
        return this.creditCardRepo.save(creditCard);
    }

    public CreditCard onlineShopping(UUID receiverIban,UUID creditcardNumber,String password
            ,String ccv, String expirationMonth,String expirationYear,double money){
        Account receiver =this.accountRepo.findByIban(receiverIban)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Receiver Account not found with iban: "+receiverIban));
        CreditCard creditCard = this.creditCardRepo.findById(creditcardNumber)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Creditcard not found with number: "+creditcardNumber));
        if(!creditCard.getPassword().equals(password)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Password is incorrect.");
        }
        if(!creditCard.getCcv().equals(ccv)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"CCV is incorrect.");
        }
        if(!creditCard.getExpirationMonth().equals(expirationMonth)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Expirationmonth is incorrect.");
        }
        if(!creditCard.getExpirationYear().equals(expirationYear)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Expirationyear is incorrect.");
        }
        RestTemplate restTemplate=new RestTemplate();
        JSONObject jsonObject=new JSONObject(restTemplate.getForObject("https://api.exchangeratesapi.io/latest?base="+receiver.getMoneyType().toString()+"&symbols=TRY"
                , String.class));
        double rate=jsonObject.getJSONObject("rates").getDouble("TRY");
        if((creditCard.getCredit()-creditCard.getDebt()) < rate*money)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"There is not enough credit in the creditcard");
        receiver.setCurrency(receiver.getCurrency()+(money));
        creditCard.setDebt(creditCard.getDebt()+(rate*money));

        Transaction transactionAccount=new Transaction();
        transactionAccount.setPerformedId(receiver.getAccountId());
        transactionAccount.setTransactionType(TransactionType.TRANSFER);
        transactionAccount.setExplanation("Sender CreditCard number: "+creditcardNumber+",Money: "+money);
        transactionAccount.setTransactionDate(LocalDate.now());

        Transaction transactionCreditCard=new Transaction();
        transactionCreditCard.setPerformedId(creditcardNumber);
        transactionCreditCard.setTransactionType(TransactionType.SHOPPING);
        transactionCreditCard.setExplanation("OnlineShopping, Receiver Account IBAN: "+receiverIban+",Money: "+money);
        transactionCreditCard.setTransactionDate(LocalDate.now());

        this.transactionRepo.save(transactionAccount);
        this.transactionRepo.save(transactionCreditCard);
        this.accountRepo.save(receiver);
        return this.creditCardRepo.save(creditCard);

    }
    public JSONObject hasDebt(UUID creditcardNumber){
        CreditCard creditCard=this.creditCardRepo.findById(creditcardNumber)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND
                        ,"Creditcard not found with number: "+creditcardNumber));
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("id",creditcardNumber.toString());
        jsonObject.put("limit",creditCard.getCredit());
        jsonObject.put("debt",creditCard.getDebt());
        jsonObject.put("available",(creditCard.getCredit()-creditCard.getDebt()));
        return  jsonObject;
    }

    public CreditCard updateCredit(UUID creditcardNumber,double credit){
        CreditCard creditCard=this.creditCardRepo.findById(creditcardNumber)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Creditcard not found with number: "+creditcardNumber));
        if(creditCard.getDebt()>credit)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Dept is greater than credit.Creditcard cannot be updated.");

        creditCard.setCredit(credit);
        return this.creditCardRepo.save(creditCard);
    }

    public CreditCard updatePassword(UUID creditcardNumber,String password){
        CreditCard creditCard=this.creditCardRepo.findById(creditcardNumber)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Creditcard not found with number: "+creditcardNumber));
        creditCard.setPassword(password);
        return this.creditCardRepo.save(creditCard);
    }
    public void delete(UUID creditcardNumber){
        CreditCard creditCard=this.creditCardRepo.findById(creditcardNumber)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND
                        ,"Creditcard not found with number: "+creditcardNumber));
        if(creditCard.getDebt() != 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Debt is not 0. Creditcard cannot be deleted");
        this.creditCardRepo.delete(creditCard);
        List<Transaction> transactions=this.transactionRepo.findAllByPerformedId(creditcardNumber);
        if(transactions != null){
            this.transactionRepo.deleteAll(transactions);
        }

    }

    public Optional<CreditCard> get(UUID creditcardNumber){return this.creditCardRepo.findById(creditcardNumber);}
}
