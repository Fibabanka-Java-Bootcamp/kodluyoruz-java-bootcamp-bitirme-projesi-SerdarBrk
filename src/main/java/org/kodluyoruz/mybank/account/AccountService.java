package org.kodluyoruz.mybank.account;




import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;


import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {
    private final AccountRepo accountRepo;

    public AccountService(AccountRepo accountRepo) { this.accountRepo = accountRepo;}

    public Account create(Account account){return this.accountRepo.save(account);}

    public Page<Account> list(UUID customerId, Pageable pageable){return this.accountRepo.findAllByCustomer_CustomerId(customerId,pageable);}

    public void sendMoney(UUID senderIban,UUID receiverIban,double money){
        Account sender =this.accountRepo.findByIban(senderIban)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Sender account not found with iban: "+senderIban));
        Account receiver=this.accountRepo.findByIban(receiverIban)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Receiver account not found with iban: "+receiverIban));

        if(sender.getCurrency() < money)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"There is not enough money in the account");

        if(sender.getAccountType().equals(receiver.getAccountType())
                && sender.getMoneyType().equals(receiver.getMoneyType())){

            sender.setCurrency(sender.getCurrency()-money);
            receiver.setCurrency(receiver.getCurrency()+money);

        }else{
            RestTemplate restTemplate=new RestTemplate();
            JSONObject jsonObject=new JSONObject(restTemplate
                    .getForObject("https://api.exchangeratesapi.io/latest?base="+sender.getMoneyType().toString()
                            +"&symbols="+receiver.getMoneyType().toString(), String.class));

            double currency=jsonObject.getJSONObject("rates").getDouble(receiver.getMoneyType().toString());
            sender.setCurrency(sender.getCurrency()-(currency*money));
            receiver.setCurrency(receiver.getCurrency()+(currency*money));
        }

        this.accountRepo.save(receiver);
        this.accountRepo.save(sender);
    }

    public void delete(UUID accountId){this.accountRepo.delete(this.accountRepo.findByAccountId(accountId));}


    public Optional<Account> get(UUID accountId){return this.accountRepo.findById(accountId);}
}
