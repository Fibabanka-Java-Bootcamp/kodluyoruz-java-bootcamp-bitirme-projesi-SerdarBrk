package org.kodluyoruz.mybank.debitcard;



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
@RequestMapping("/api/debitCard")
public class DebitCardController {
    private final DebitCardService debitCardService;

    public DebitCardController(DebitCardService debitCardService) {
        this.debitCardService = debitCardService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DebitCardDto create(@Valid @RequestBody DebitCardDto debitCardDto){
        return this.debitCardService.create(debitCardDto.toDebitCard()).toDebitCardDto();
    }

    @PostMapping("{debitcardNumber}/updateAccount")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public DebitCardDto updateAccount(@PathVariable("debitcardNumber")UUID debitcardNumber,
                                      @RequestParam("accountId") UUID accountId){
        return this.debitCardService.updateAccount(debitcardNumber, accountId).toDebitCardDto();
    }
    @PostMapping( "{debitcardNumber}/updatePassword")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public DebitCardDto updatePassword(@PathVariable("debitcardNumber")UUID debitcardNumber,
                                       @Pattern(regexp = "^[0-9]{4}",message = "password length must be 4 and password contains only alphanumeric characters")
                                       @RequestParam("password") String password){
        return this.debitCardService.updatePassword(debitcardNumber, password).toDebitCardDto();
    }
    @PostMapping("/withdrawlMoneyFromAtm")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public DebitCardDto withdrawlMoneyFromAtm(@RequestParam("debitcardNumber") UUID debitcardNumber,
                                              @RequestParam("password") String password,
                                              @Min(value = 10,message = "least 10.")
                                              @RequestParam("money") double money){
        return this.debitCardService.withdrawlMoneyFromAtm(debitcardNumber, password, money).toDebitCardDto();
    }
    @PostMapping("/depositMoneyFromAtm")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public DebitCardDto depositMoneyFromAtm(@RequestParam("debitcardNumber") UUID debitcardNumber,
                                            @RequestParam("password") String password,
                                            @Min(value = 10,message = "least 10.")
                                            @RequestParam("money") double money){
        return this.debitCardService.depositMoneyFromAtm(debitcardNumber, password, money).toDebitCardDto();
    }
    @PostMapping("/shopping")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public DebitCardDto shopping(@RequestParam("debitcardNumber") UUID debitcardNumber,
                                 @RequestParam("receiverIban") UUID receiverIban,
                                 @RequestParam("password") String password,
                                 @RequestParam("money") double money){
        return this.debitCardService.shopping(debitcardNumber, receiverIban, password, money).toDebitCardDto();
    }
    @PostMapping(value = "/onlineShopping",params = {"receiverIban","debitcardNumber","password","ccv","expirationMonth","expirationYear","money"})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public DebitCardDto onlineShopping(@RequestParam("receiverIban") UUID receiverIban,
                                        @RequestParam("debitcardNumber") UUID debitcardNumber,
                                        @RequestParam("password") String password,
                                        @RequestParam("ccv") String ccv,
                                        @RequestParam("expirationMonth") String expirationMonth,
                                        @RequestParam("expirationYear") String expirationYear,
                                        @RequestParam("money") double money){
        return this.debitCardService.onlineShopping(receiverIban, debitcardNumber, password,
                ccv, expirationMonth, expirationYear, money).toDebitCardDto();
    }

    @DeleteMapping(params = {"debitcardNumber"})
    @ResponseStatus(HttpStatus.OK)
    public void delete(@RequestParam("debitcardNumber") UUID debitcardNumber){
        this.debitCardService.delete(debitcardNumber);
    }

    @GetMapping("/{debitcardNumber}")
    public DebitCardDto get(@PathVariable("debitcardNumber") UUID debitcardNumber){
        return this.debitCardService.get(debitcardNumber)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Debitcard not found with number: "+debitcardNumber))
                .toDebitCardDto();
    }
}
