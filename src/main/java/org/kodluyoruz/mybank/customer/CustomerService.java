package org.kodluyoruz.mybank.customer;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;


@Service
public class CustomerService {
    private final CustomerRepo customerRepo;

    public CustomerService(CustomerRepo customerRepo) {
        this.customerRepo = customerRepo;
    }
    public Customer create(Customer customer){return this.customerRepo.save(customer);}

    public void delete(UUID customerId){
        Customer customer =this.customerRepo.findById(customerId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Account not found with id"+customerId));
        boolean hasCurrency=customer.getAccounts().stream().distinct().anyMatch(account -> account.getCurrency()!=0);
        boolean hasDebt=customer.getCreditCards().stream().distinct().anyMatch(creditCard -> creditCard.getDebt() !=0);

        if(hasCurrency || hasDebt)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"the customer's credit card has debt or the customer's account has money.");
        else{
            this.customerRepo.delete(customer);
        }

    }
    public Customer updatePhone(UUID customerId, String phone){
        Customer customer =this.customerRepo.findById(customerId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Account not found with id"+customerId));
        customer.setPhoneNumber(phone);
        return  this.customerRepo.save(customer);
    }



    public Optional<Customer> get(UUID customerId){
        return this.customerRepo.findById(customerId);
    }
}
