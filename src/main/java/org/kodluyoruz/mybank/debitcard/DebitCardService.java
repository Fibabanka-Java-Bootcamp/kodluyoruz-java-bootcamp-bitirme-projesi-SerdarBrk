package org.kodluyoruz.mybank.debitcard;

import org.json.JSONObject;
import org.kodluyoruz.mybank.account.Account;
import org.kodluyoruz.mybank.account.AccountRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;


import java.util.UUID;

@Service
public class DebitCardService {
    private final DebitCardRepo debitCardRepo;
    private final AccountRepo accountRepo;

    public DebitCardService(DebitCardRepo debitCardRepo, AccountRepo accountRepo) {
        this.debitCardRepo = debitCardRepo;
        this.accountRepo = accountRepo;
    }

    public DebitCard create(DebitCard debitCard){return this.debitCardRepo.save(debitCard);}

    public DebitCard updateAccount(UUID debitcardId,UUID accountId){
        return null;
    }

    public DebitCard updatePassword(UUID debitcardId,String password){
        return null;
    }

    public DebitCard withdrawMoneyFromAtm(UUID debitcardId,String password,double money){
       DebitCard debitCard=this.debitCardRepo.findById(debitcardId)
                    .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"DebitCard not found with id"+debitcardId));

        if (money<0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Money must be greater than 0: "+money);
       if(!debitCard.getPassword().equals(password))
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Password is incorrect");

       if (money > debitCard.getAccount().getCurrency())
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"There is not enough money in the account");

       debitCard.getAccount().setCurrency(debitCard.getAccount().getCurrency()-money);
       return this.debitCardRepo.save(debitCard);
    }
    public DebitCard depositMoneyFromAtm(UUID debitcardId,String password,double money){
        DebitCard debitCard=this.debitCardRepo.findById(debitcardId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"DebitCard not found with id"+debitcardId));
        if(!debitCard.getPassword().equals(password))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Password is incorrect");
        if (money<0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Money must be greater than 0: "+money);

        debitCard.getAccount().setCurrency(debitCard.getAccount().getCurrency()+money);
        return this.debitCardRepo.save(debitCard);
    }

    public DebitCard sendMoneyFromCard(UUID debitcardId,UUID receiverIban,String password,double money){

        DebitCard debitCard=this.debitCardRepo.findById(debitcardId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"DebitCard not found with id"+debitcardId));
        Account receiverAccount=this.accountRepo.findByIban(receiverIban)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Receiver Account not found with iban"+receiverIban));
        if (money<0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Money must be greater than 0: "+money);
        if(!debitCard.getPassword().equals(password))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Password is incorrect");
        if(debitCard.getAccount().getCurrency() < money)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"There is not enough credit in the creditcard");
        RestTemplate restTemplate=new RestTemplate();
        JSONObject jsonObject=new JSONObject(restTemplate
                .getForObject("https://api.exchangeratesapi.io/latest?base="+debitCard.getAccount().getMoneyType().toString()
                                +"&symbols="+receiverAccount.getMoneyType().toString(), String.class));

        double rate=jsonObject.getJSONObject("rates").getDouble(receiverAccount.getMoneyType().toString());

        debitCard.getAccount().setCurrency(debitCard.getAccount().getCurrency()-money);
        receiverAccount.setCurrency(receiverAccount.getCurrency()+(rate*money));

        this.accountRepo.save(receiverAccount);
        return this.debitCardRepo.save(debitCard);
    }
}
