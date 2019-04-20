package co.com.laboratorio1.service;

import java.util.List;
import java.util.Optional;

import co.com.laboratorio1.domain.Customer;

public interface ICustomerService {
	
	Customer saveCustomer(Customer customer);
	List<Customer> getAllCustomers();
	Optional<Customer> getCustomerById(Long id);
	Optional<Customer> getCustomerByName(String name);
	Customer updateCustomer(Customer customer);
	void deleteCustomer(Customer customer);
	boolean isUserExist(Customer customer);
	
}
