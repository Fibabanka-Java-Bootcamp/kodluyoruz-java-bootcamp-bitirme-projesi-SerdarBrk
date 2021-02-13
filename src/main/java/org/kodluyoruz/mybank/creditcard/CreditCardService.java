package org.kodluyoruz.mybank.creditcard;

import org.json.JSONObject;
import org.kodluyoruz.mybank.account.Account;
import org.kodluyoruz.mybank.account.AccountRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class CreditCardService {
    private final CreditCardRepo creditCardRepo;
    private final AccountRepo accountRepo;

    public CreditCardService(CreditCardRepo creditCardRepo, AccountRepo accountRepo) {
        this.creditCardRepo = creditCardRepo;
        this.accountRepo = accountRepo;
    }

    public CreditCard create(CreditCard creditCard){
        return this.creditCardRepo.save(creditCard);
    }

    public CreditCard payDeptFromAtm(UUID creditcardId,String password,double money){
        CreditCard creditCard=this.creditCardRepo.findById(creditcardId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Creditcard not found with id: "+creditcardId));
        if(!creditCard.getPassword().equals(password))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Password is incorrect");
        if (money<0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Money must be greater than 0: "+money);
        creditCard.setDebt(creditCard.getDebt()-money);
        return this.creditCardRepo.save(creditCard);
    }

    public CreditCard payDebtFromAccount(UUID accountId,UUID creditcardId,double money){
        CreditCard creditCard=this.creditCardRepo.findById(creditcardId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Creditcard not found with id: "+creditcardId));
        Account account=this.accountRepo.findById(accountId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Account not found with accountId: "+accountId));
        if (money<0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Money must be greater than 0: "+money);
        if(account.getCurrency() < money)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"There is not enough money in the account");
        account.setCurrency(account.getCurrency()-money);
        creditCard.setDebt(creditCard.getDebt()-money);
        this.accountRepo.save(account);
        return this.creditCardRepo.save(creditCard);
    }

    public CreditCard shopping(UUID receiverAccountIban,UUID creditcardId,String password,double money){
        CreditCard creditCard=this.creditCardRepo.findById(creditcardId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Creditcard not found with id: "+creditcardId));
        Account receiver=this.accountRepo.findByIban(receiverAccountIban)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Receiver account not found with iban: "+receiverAccountIban));

        if (money<0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Money must be greater than 0: "+money);
        if(!creditCard.getPassword().equals(password))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Password is incorrect");
        RestTemplate restTemplate=new RestTemplate();
        JSONObject jsonObject=new JSONObject(restTemplate
                .getForObject("https://api.exchangeratesapi.io/latest?base=TRY&symbols="
                        +receiver.getMoneyType().toString(), String.class));
        double rate=jsonObject.getJSONObject("rates").getDouble(receiver.getMoneyType().toString());
        if((creditCard.getCredit()-creditCard.getDebt()) < rate*money)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"There is not enough credit in the creditcard");
        receiver.setCurrency(receiver.getCurrency()+(rate*money));
        creditCard.setDebt(creditCard.getDebt()+(rate*money));
        this.accountRepo.save(receiver);
        return this.creditCardRepo.save(creditCard);
    }
}
