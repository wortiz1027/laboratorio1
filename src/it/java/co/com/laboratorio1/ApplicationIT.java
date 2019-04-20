package co.com.laboratorio1;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.com.laboratorio1.domain.Customer;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		classes = Application.class		
)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-qa.yml")
@ActiveProfiles("qa")
@Tag("Integration")
@DisplayName("Suite Test IntegrationTest :: Probado todos los componentes y capas de la aplicacion...")
public class ApplicationIT {
	
	private final String API_URI_BASE = "/api/v1";
	private final String API_URI_CUSTOMERS = API_URI_BASE + "/customers";
	private final String API_URI_CUSTOMERS_ID = API_URI_BASE + "/customers/{id}";
	private final String API_URI_CUSTOMERS_NAME = API_URI_BASE + "/customers/names";
	
	@Autowired
	private MockMvc mvc;
	
	@Test
	@DisplayName("IntegrationTest :: Prueba guardar informacion del customer...")
	public void testSaveCustomerInformation() throws Exception {
		this.mvc.perform(post(this.API_URI_CUSTOMERS)
						 .content(asJsonString(getCustomer()))
						 .contentType(MediaType.APPLICATION_JSON_UTF8)
						 .accept(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
		        .andExpect(status().isCreated());
	}
	
	@Test
	@DisplayName("IntegrationTest :: Prueba obtener todos los customers...")
	public void testGetAllCustomers() throws Exception {
		int array_size = 6;
		
		this.mvc.perform(get(API_URI_CUSTOMERS)
                         .accept(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
		        .andExpect(status().isOk())
				.andExpect(jsonPath("$.length()", is(array_size)))
				.andExpect(jsonPath("$.[?(@.id == 2)].phone", hasItem(456)));		
	}
	
	@Test
	@DisplayName("IntegrationTest :: Prueba consultar informacion del customer por identificador...")
	public void testGetCustomerById() throws Exception {
		this.mvc.perform(get(API_URI_CUSTOMERS_ID, 4L)
						.accept(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email", is("dgodin@outlook.com")));	
	}
	
	@Test
	@DisplayName("IntegrationTest :: Prueba consultar informacion del customer por nombre...")
	public void testGetCustomerByName() throws Exception {
		this.mvc.perform(get(API_URI_CUSTOMERS_NAME)
				.param("fname", "Diblo")
				.accept(MediaType.APPLICATION_JSON_UTF8))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.email", is("ddibala@gmail.com")));	
	}
	
	private static String asJsonString(final Object obj) {
	    try {
	    	return new ObjectMapper().writeValueAsString(obj);
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}
	
	private Customer getCustomer() {
		Customer customer = new Customer();
		customer.setFname("Michael");
		customer.setLname("Shumacher");
		customer.setAddress("Calle 123");
		customer.setPhone(123456);
		customer.setEmail("mshumacher@outlook.com");
		customer.setActive(Boolean.TRUE);
				
		return customer;
	}
	
}
