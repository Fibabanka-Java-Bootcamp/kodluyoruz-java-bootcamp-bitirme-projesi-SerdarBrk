package org.kodluyoruz.mybank.customer;


import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDto create(@Valid @RequestBody CustomerDto customerDto){
        return this.customerService.create(customerDto.toCustomer()).toCustomerDto();
    }
    @PostMapping("/{customerId}/updatePhone")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CustomerDto updatePhone(@PathVariable("customerId") UUID customerId,
                                   @Pattern(regexp = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$",message = "Phone Number Ex:+(123)-456-78-90")
                                   @RequestParam("phoneNumber") String phoneNumber){
        return this.customerService.updatePhone(customerId, phoneNumber).toCustomerDto();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void delete(@RequestParam("customerId") UUID customerId){
        this.customerService.delete(customerId);
    }



    @GetMapping("/{customerId}")
    public CustomerDto get(@PathVariable("customerId") UUID customerId){
        return this.customerService.get(customerId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Account not found with id"+customerId)).toCustomerDto();
    }
}
