package co.com.laboratorio1.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import co.com.laboratorio1.domain.Customer;
import co.com.laboratorio1.repositories.CustomerRepository;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
@Tag("Database")
@DisplayName("Suite Test Repository :: Pruebas unitarias para la capa de datos(Customer Entity)")
public class CustomerRepositoryTest {
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private CustomerRepository repository;
	
	@Test
	@DisplayName("Repository :: Prueba que valida que no se encontro registro...")
	public void testCustomerNotFound() {
		Optional<Customer> customer = repository.findCustomerByName("wilman");
		
		assertThat(customer).isNotNull();
	}
	
	@Test
	@DisplayName("Repository :: Prueba que valida que la consulta de todos los registros esta vacia")
	public void testFindAllListEmpty() {
		Iterable<Customer> customers = repository.findAll();
		
		assertThat(customers).isEmpty();
	}
	
	@Test
	@DisplayName("Repository :: Prueba que retorna todos los customer registrados en la tabla Customer")
	public void testFindAllCustomers() {
		Customer customer1 = new Customer();
		entityManager.persist(customer1);
		
		Customer customer2 = new Customer();
		entityManager.persist(customer2);
		
		Customer customer3 = new Customer();
		entityManager.persist(customer3);
		
		Customer customer4 = new Customer();
		entityManager.persist(customer4);
		
		Customer customer5 = new Customer();
		entityManager.persist(customer5);
		
		Customer customer6 = new Customer();
		entityManager.persist(customer6);
		
		Iterable<Customer> customers = repository.findAll();
		
		assertThat(customers).hasSize(6)
		                     .contains(customer1, 
		                    		   customer2, 
		                    		   customer3, 
		                    		   customer4, 
		                    		   customer5, 
		                    		   customer6);
		
	}
	
	@Test
	@DisplayName("Repository :: Prueba que consulta por nombre de customer...")
	public void testFindByCustomerName() {
		Customer customer1 = new Customer();
		customer1.setFname("Nombre 1");
		customer1.setFname("Apellido 1");
		customer1.setAddress("Direccion 1");
		customer1.setEmail("customer1@gmail.com");
		customer1.setPhone(98765);
		customer1.setActive(Boolean.TRUE);
		
		entityManager.persist(customer1);
		
		Customer customer2 = new Customer();
		customer2.setFname("Nombre 2");
		customer2.setFname("Apellido 2");
		customer2.setAddress("Direccion 2");
		customer2.setEmail("customer2@gmail.com");
		customer2.setPhone(123456);
		customer2.setActive(Boolean.TRUE);
		
		entityManager.persist(customer2);
		
		Optional<Customer> c = repository.findCustomerByName(customer2.getFname());
		
		assertThat(c).isEqualTo(Optional.of(customer2));
	}
	
}
