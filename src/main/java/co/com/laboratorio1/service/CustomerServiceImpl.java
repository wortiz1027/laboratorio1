package co.com.laboratorio1.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import co.com.laboratorio1.domain.Customer;
import co.com.laboratorio1.repositories.CustomerRepository;

@Service
public class CustomerServiceImpl implements ICustomerService {

	private CustomerRepository repository;
	
	public CustomerServiceImpl(CustomerRepository repository) {
		this.repository = repository;
	}
	
	@Override
	public Customer saveCustomer(Customer customer) {
		return repository.save(customer);
	}

	@Override
	public List<Customer> getAllCustomers() {
		return (List<Customer>) repository.findAll();
	}

	@Override
	public Optional<Customer> getCustomerById(Long id) {		
		return repository.findById(id);				
	}

	@Override
	public Optional<Customer> getCustomerByName(String name) {
		return repository.findCustomerByName(name);
	}

	@Override
	public boolean isUserExist(Customer customer) {
		return repository.findCustomerByName(customer.getFname()).isPresent();
	}

	@Override
	public void deleteCustomer(Customer customer) {
		repository.delete(customer);
	}

	@Override
	public Customer updateCustomer(Customer customer) {
		return repository.saveAndFlush(customer);
	}
	
}