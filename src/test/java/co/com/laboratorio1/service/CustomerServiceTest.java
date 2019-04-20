package co.com.laboratorio1.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.context.ActiveProfiles;

import co.com.laboratorio1.domain.Customer;
import co.com.laboratorio1.repositories.CustomerRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
@ActiveProfiles("test")
@Tag("Service")
@DisplayName("Suite Test Service :: Pruebas unitarias de la capa de servicio...")
public class CustomerServiceTest {
	
	@Mock
	private CustomerRepository repository;
	
	@InjectMocks
	private CustomerServiceImpl service;
	
	private Customer customer;
	private List<Customer> customers;
	
	@BeforeEach
	public void init() {
		this.customer  = getCustomer();
		this.customers = getCustormers();
	}
	
	private Customer getCustomer() {
		Customer customer = new Customer();
		
		customer.setId((long) 1);
		customer.setFname("Nombre 1");
		customer.setLname("Apellido 1");
		customer.setAddress("Calle 123");
		customer.setPhone(123456);
		customer.setEmail("customer1@outlook.com");
		customer.setActive(Boolean.TRUE);
				
		return customer;
	}
	
	private List<Customer> getCustormers() {
		Customer customer1 = new Customer();
		customer1.setId((long) 1);
		customer1.setFname("Nombre 1");
		customer1.setLname("Apellido 1");
		customer1.setAddress("Calle 123");
		customer1.setPhone(123456);
		customer1.setEmail("customer1@outlook.com");
		customer1.setActive(Boolean.TRUE);
		
		Customer customer2 = new Customer();
		customer2.setId((long) 2);
		customer2.setFname("Nombre 2");
		customer2.setLname("Apellido 2");
		customer2.setAddress("Calle 456");
		customer2.setPhone(9876543);
		customer2.setEmail("customer2@gmail.com");
		customer2.setActive(Boolean.FALSE);
		
		Customer customer3 = new Customer();
		customer3.setId((long) 3);
		customer3.setFname("Nombre 3");
		customer3.setLname("Apellido 3");
		customer3.setAddress("Calle 7890");
		customer3.setPhone(91287456);
		customer3.setEmail("customer3@yahoo.com");
		customer3.setActive(Boolean.TRUE);
		
		List<Customer> customers = new ArrayList<Customer>();
		customers.add(customer1);
		customers.add(customer2);
		customers.add(customer3);
		
		return customers;
	}
	
	@Test
	@DisplayName("Service :: Prueba para guardar informacion del customer...")
	public void testGuardarCustomer() {
		service.saveCustomer(this.customer);
		
		verify(repository, times(1)).save(customer);
	}
	
	@Test
	@DisplayName("Service :: Prueba para consultar customers...")
	public void testConsultarCustomers() {
		when(repository.findAll()).thenReturn(customers);
		
		List<Customer> list = service.getAllCustomers();
		
		assertEquals(3, list.size());
		
		verify(repository, times(1)).findAll();		
	}
	
	@Test
	@DisplayName("Service :: Prueba para consultar customer por id...")
	public void testConsultarCustomerPorId() {
		when(repository.findById((long) 1)).thenReturn(Optional.ofNullable(this.customer));
		
		Optional<Customer> tmp = service.getCustomerById((long) 1);
		
		assertEquals(Boolean.TRUE, tmp.get().getActive());
		
	}
	
	@Test
	@DisplayName("Service :: Prueba para consultar customer por nombre...")
	public void testConsultarCustomerPorNombre() {
		when(repository.findCustomerByName("Nombre 1")).thenReturn(Optional.ofNullable(this.customer));
		
		Optional<Customer> tmp = service.getCustomerByName("Nombre 1");
		
		assertEquals("customer1@outlook.com", tmp.get().getEmail());
	}
	
	@Test
	@DisplayName("Service :: Prueba para actualizar customer por nombre...")
	public void testActualizarCustomer() {
		this.customer.setFname("Wilman");
		this.customer.setLname("Ortiz");
		
		service.updateCustomer(this.customer);
		
		assertEquals("Wilman", this.customer.getFname());		
	}
	
	@Test
	@DisplayName("Service :: Prueba para eliminar customer por nombre...")
	public void testEliminarCustomer() {
		doNothing().when(repository).delete(this.customer);
		
		service.deleteCustomer(this.customer);
		
		verify(repository, times(1)).delete(this.customer);
	}
	
}