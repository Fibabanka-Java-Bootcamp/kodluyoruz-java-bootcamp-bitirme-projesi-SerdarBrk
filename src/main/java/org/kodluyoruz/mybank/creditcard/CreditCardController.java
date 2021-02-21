package org.kodluyoruz.mybank.creditcard;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/creditcard")
public class CreditCardController {
    private final CreditCardService creditCardService;

    public CreditCardController(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreditCardDto create(@Valid @RequestBody CreditCardDto creditCardDto){
        return this.creditCardService.create(creditCardDto.toCreditCard()).toCreditCardDto();
    }

    @PostMapping(value = "/payDeptFromAtm",params = {"creditcardNumber","password","money"})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CreditCardDto payDeptFromAtm(@RequestParam("creditcardNumber")UUID creditcardNumber,
                                        @RequestParam("password") String password,
                                        @RequestParam("money") double money){
        return this.creditCardService.payDeptFromAtm(creditcardNumber, password, money).toCreditCardDto();
    }

    @PostMapping(value = "/payDeptFromAccount",params = {"accountId","creditcardNumber","money"})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CreditCardDto payDeptFromAccount(@RequestParam("accountId")UUID accountId,
                                        @RequestParam("creditcardNumber") UUID creditcardNumber,
                                        @RequestParam("money") double money){
        return this.creditCardService.payDebtFromAccount(accountId, creditcardNumber, money).toCreditCardDto();
    }

    @PostMapping(value = "/shopping",params = {"receiverAccountIban","creditcardNumber","password","money"})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CreditCardDto shopping(@RequestParam("receiverAccountIban")UUID receiverAccountIban,
                                            @RequestParam("creditcardNumber") UUID creditcardNumber,
                                            @RequestParam("password") String password,
                                            @RequestParam("money") double money){
        return this.creditCardService.shopping(receiverAccountIban, creditcardNumber, password, money).toCreditCardDto();
    }

    @PostMapping(value = "/onlineShopping",params = {"receiverAccountIban","creditcardNumber","password","ccv","expirationMonth","expirationYear","money"})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CreditCardDto onlineShopping(@RequestParam("receiverAccountIban") UUID receiverAccountIban,
                                        @RequestParam("creditcardNumber") UUID creditcardNumber,
                                        @RequestParam("password") String password,
                                        @RequestParam("ccv") String ccv,
                                        @RequestParam("expirationMonth") String expirationMonth,
                                        @RequestParam("expirationYear") String expirationYear,
                                        @RequestParam("money") double money){
        return this.creditCardService.onlineShopping(receiverAccountIban, creditcardNumber, password,
                ccv, expirationMonth, expirationYear, money).toCreditCardDto();
    }
    @GetMapping("{creditcardNumber}/hasDept")
    public JSONObject hasDept(@PathVariable("creditcardNumber") UUID creditcardNumber){
        return this.creditCardService.hasDebt(creditcardNumber);
    }

    @PostMapping(value = "{creditcardNumber}/updateCredit")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CreditCardDto updateCredit(@PathVariable("creditcardNumber") UUID creditcardNumber,
                                      @Min(value=100, message = "Card credit cannot be lower than 100")
                                      @RequestParam("credit") double credit){
        return this.creditCardService.updateCredit(creditcardNumber, credit).toCreditCardDto();
    }
    @PostMapping( "{creditcardNumber}/updatePassword")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CreditCardDto updatePassword(@PathVariable("creditcardNumber") UUID creditcardNumber,
                                      @Pattern (regexp = "^[0-9]{4}",message = "password length must be 4 and password contains only alphanumeric characters")
                                      @RequestParam("password") String password){
        return this.creditCardService.updatePassword(creditcardNumber, password).toCreditCardDto();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void delete(@RequestParam("creditcardNumber") UUID creditcardNumber){
        this.creditCardService.delete(creditcardNumber);
    }

    @GetMapping("/{creditcardNumber}")
    public CreditCardDto get(@PathVariable("creditcardNumber") UUID creditcardNumber){
        return this.creditCardService.get(creditcardNumber).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Creditcard not found with id: "+creditcardNumber)).toCreditCardDto();
    }
}
