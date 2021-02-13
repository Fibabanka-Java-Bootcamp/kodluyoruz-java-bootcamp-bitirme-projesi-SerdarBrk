package org.kodluyoruz.mybank.account;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/api/account")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {this.accountService = accountService;}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDto create(@Valid @RequestBody AccountDto accountDto){
        return this.accountService.create(accountDto.toAccount()).toAccountDto();
    }

    @GetMapping(params = {"customerId", "page", "size"})
    public List<AccountDto> listByCustomer(@RequestParam("customerId") UUID customerId, @Min(value = 0) @RequestParam("page") int page, @RequestParam("size") int size) {

        return accountService.list(customerId, PageRequest.of(page, size)).stream()
                .map(Account::toAccountDto)
                .collect(Collectors.toList());
    }
    @PostMapping("/{accountId}/sendMoney")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AccountDto sendMoney(@PathVariable("accountId") UUID accountId,
                                @RequestParam("receiverIban") UUID receiverIban,
                                @RequestParam("money") double money){
        return this.accountService.sendMoney(accountId,receiverIban,money).toAccountDto();
    }


    @DeleteMapping(params = {"accountId"})
    @ResponseStatus(HttpStatus.OK)
    public void delete(@RequestParam("accountId") UUID accountId){
        accountService.delete(accountId);
    }

    @GetMapping("/{accountId}")
    public AccountDto get(@PathVariable("accountId") UUID accountId){
        return this.accountService.get(accountId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Account not found with id"+accountId)).toAccountDto();
    }
}
