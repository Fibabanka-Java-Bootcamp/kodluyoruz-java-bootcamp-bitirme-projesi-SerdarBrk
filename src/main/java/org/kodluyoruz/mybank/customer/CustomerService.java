package org.kodluyoruz.mybank.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<Customer> listByName(String name, Pageable pageable){
        return this.customerRepo.findAllByName(name, pageable);
    }
    public Page<Customer> listBySurname(String surname,Pageable pageable){
        return this.customerRepo.findAllBySurname(surname, pageable);
    }
    public Page<Customer> listByNameAndSurname(String name,String surname,Pageable pageable){
        return this.customerRepo.findAllByNameAndSurname(name, surname, pageable);
    }
    public Page<Customer> listByNameAndCreditCardsNotNull(String name,Pageable pageable){
        return this.customerRepo.findAllByNameAndCreditCardsNotNull(name, pageable);
    }
    public Page<Customer> listByNameAndDebitCardsNotNull(String name,Pageable pageable){
        return this.customerRepo.findAllByNameAndDebitCardsNotNull(name, pageable);
    }
    public Page<Customer> listByNameAndAccountsNotNull(String name,Pageable pageable){
        return this.customerRepo.findAllByNameAndAccountsNotNull(name, pageable);
    }


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
    public HttpStatus updatePhone(UUID customerId,String phone){
        Customer customer =this.customerRepo.findById(customerId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Account not found with id"+customerId));
        if(phone.length() != 11){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Phone number length should be 11."); }
        customer.setPhoneNumber(phone);
        this.customerRepo.save(customer);
        return HttpStatus.OK;
    }

    public HttpStatus updateName(UUID customerId,String name){
        Customer customer =this.customerRepo.findById(customerId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Customer not found with id"+customerId));
        customer.setName(name);
        this.customerRepo.save(customer);
        return HttpStatus.OK;
    }
    public HttpStatus updateSurname(UUID customerId,String surname){
        Customer customer =this.customerRepo.findById(customerId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Customer not found with id"+customerId));
        customer.setSurname(surname);
        this.customerRepo.save(customer);
        return HttpStatus.OK;
    }

    public Optional<Customer> get(UUID customerId){
        return this.customerRepo.findById(customerId);
    }
}
