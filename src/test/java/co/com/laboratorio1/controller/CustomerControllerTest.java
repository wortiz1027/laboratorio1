package co.com.laboratorio1.controller;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.com.laboratorio1.domain.Customer;
import co.com.laboratorio1.service.CustomerServiceImpl;

@ExtendWith(SpringExtension.class)
@WebMvcTest({CustomerController.class})
@ActiveProfiles("test")
@Tag("Controller")
@DisplayName("Suite Test Controller :: Pruebas de los metodos expuestos en el controller...")
public class CustomerControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
    private WebApplicationContext wac;
	
	@MockBean
	private CustomerServiceImpl service;
	
	private final String API_URI_BASE = "/api/v1";
	private final String API_URI_CUSTOMERS = API_URI_BASE + "/customers";
	private final String API_URI_CUSTOMERS_ID = API_URI_BASE + "/customers/{id}";
	private final String API_URI_CUSTOMERS_NAME = API_URI_BASE + "/customers/names";
	
	private Customer customer;
	private List<Customer> customers;
	
	@BeforeEach
	public void init() {
		this.customer  = getCustomer();
		this.customers = getCustormers();
		this.mvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}
	
	@Test
	@DisplayName("Controller :: Prueba obtener todos los registros...")
	public void testGetAllCustomesrs() throws Exception {
		int array_size = 3;
		
		when(this.service.getAllCustomers()).thenReturn(this.customers);		
		
		this.mvc.perform(get(this.API_URI_CUSTOMERS)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
				.andExpect(jsonPath("$.length()", is(array_size)))
				.andExpect(jsonPath("$.[?(@.id == 2)].lname", hasItem("Apellido 2")));
	}
	
	@Test
	@DisplayName("Controller :: Prueba guardar informacion del customer...")
	public void testSaveCustomer() throws Exception {
		when(this.service.saveCustomer(this.customer)).thenReturn(this.customer);
		
		this.mvc.perform(post(this.API_URI_CUSTOMERS)
				.content(asJsonString(this.customer))
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON_UTF8))
		        .andDo(print())		        
		        .andExpect(status().isCreated())
		        .andExpect(jsonPath("$.fname", is("Nombre 1")));		        		        
	}
	
	@Test
	@DisplayName("Controller :: Prueba consultar informacion del customer por id...")
	public void testCustomerById() throws Exception {
		when(this.service.getCustomerById(1L)).thenReturn(Optional.ofNullable(this.customer));
		
		this.mvc.perform(get(this.API_URI_CUSTOMERS_ID, 1L)
				.accept(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email", is("customer1@outlook.com")));		
	}
	
	@Test
	@DisplayName("Controller :: Prueba consultar informacion del customer por name...")
	public void testCustomerByName() throws Exception {
		when(this.service.getCustomerByName("Nombre 1")).thenReturn(Optional.ofNullable(this.customer));
		
		this.mvc.perform(get(this.API_URI_CUSTOMERS_NAME)
				.param("fname", "Nombre 1")
				.accept(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email", is("customer1@outlook.com")));
	}
	
	@Test
	@DisplayName("Controller :: Prueba actualizar informacion del customer...")
	public void testActualizarCustomer() throws Exception {		
		Customer customerUpdated = new Customer();
		
		customerUpdated.setId(1L);
		customerUpdated.setFname("Nombre Actualizado");
		customerUpdated.setLname("Apellido Actualizado");
		customerUpdated.setAddress("Cra 13 # 10 - 60");
		customerUpdated.setPhone(987654);
		customerUpdated.setEmail("customer1@gmail.com");
		customerUpdated.setActive(Boolean.FALSE);
		
		when(this.service.getCustomerById(this.customer.getId())).thenReturn(Optional.ofNullable(this.customer));
		when(this.service.updateCustomer(this.customer)).thenReturn(customerUpdated);
		
		this.mvc.perform(put(this.API_URI_CUSTOMERS_ID, 1L)
				.content(asJsonString(customerUpdated))
				.contentType(MediaType.APPLICATION_JSON_UTF8))
		        .andDo(print())
		        .andExpect(status().isOk())
		        .andExpect(jsonPath("$.address", is("Cra 13 # 10 - 60")));
		
		verify(this.service, times(1)).getCustomerById(this.customer.getId());
		verify(this.service, times(1)).updateCustomer(this.customer);
	}
	
	@Test
	@DisplayName("Controller :: Prueba eliminar informacion del customer...")
	public void testDeleteCustomer() throws Exception {
		when(this.service.getCustomerById(this.customer.getId())).thenReturn(Optional.ofNullable(this.customer));
		doNothing().when(this.service).deleteCustomer(this.customer);
		
		this.mvc.perform(delete(this.API_URI_CUSTOMERS_ID, 1L)
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
				.andExpect(status().isOk());
		
		verify(this.service, times(1)).deleteCustomer(this.customer);
	}
	
	public static String asJsonString(final Object obj) {
	    try {
	    	return new ObjectMapper().writeValueAsString(obj);
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
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
}
