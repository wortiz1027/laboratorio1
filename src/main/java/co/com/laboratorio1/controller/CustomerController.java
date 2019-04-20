package co.com.laboratorio1.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import co.com.laboratorio1.domain.Customer;
import co.com.laboratorio1.exceptions.ResourceNotFoundException;
import co.com.laboratorio1.service.ICustomerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;

@RestController
@RequestMapping("/api/v1")
public class CustomerController {
	
	private ICustomerService service;
	
	public CustomerController(ICustomerService service) {
		this.service = service;
	}
	
	@ApiOperation(value = "Guardar Informaci\u00f3n del cliente",
			      notes = "Operaci\u00f3n que reune lo datos del cliente a registrar",
			      response = Customer.class)
	@ApiResponses(value = { 
					        @ApiResponse(code = 201, message = "Cliente ha sido creado exitosamente!"),
					        @ApiResponse(code = 409, message = "Conflicto, el cliente ya existe!") 
					      }
	             )
	@PostMapping("/customers")
	public ResponseEntity<Customer> guardarCustomer(@Valid @RequestBody Customer input, UriComponentsBuilder ucBuilder) {
		if (this.service.isUserExist(input)) {
			return new ResponseEntity<Customer>(HttpStatus.CONFLICT);
		}
		
		this.service.saveCustomer(input);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/api/v1/customers/{id}").buildAndExpand(input.getId()).toUri());
        return new ResponseEntity<Customer>(input, headers, HttpStatus.CREATED);
	}
	
	@ApiOperation(value = "Consultar todos los clientes registrados",
			      notes = "Operaci\u00f3n que retorna todos los clientes registrados en el sistema",
			      response = List.class)
	@ApiResponses(value = { 
					        @ApiResponse(code = 200, message = "Listado de clientes consultado exitosamente!"),
					        @ApiResponse(code = 404, message = "No existen clientes registrados!") 
					      }
	             )
	@GetMapping("/customers")
	public ResponseEntity<List<Customer>> consultarCustomers() {
		return !this.service.getAllCustomers().isEmpty() ? new ResponseEntity<List<Customer>>(this.service.getAllCustomers(), HttpStatus.OK) : new ResponseEntity<List<Customer>>(HttpStatus.NO_CONTENT); 
	}

	@ApiOperation(value = "Consultar Informaci\u00f3n del cliente por identificador",
			      notes = "Operaci\u00f3n que retorna Informaci\u00f3n del cliente por [ID]",
			      response = Customer.class)
	@ApiResponses(value = { 
					        @ApiResponse(code = 200, message = "Informaci\u00f3n del cliente consultada exitosamente!"),
					        @ApiResponse(code = 404, message = "No existe Informaci\u00f3n del cliente!") 
					      }
	           	 )	
	@GetMapping("/customers/{id:[0-9]+}")
	public ResponseEntity<Customer> consultarCustomerPorId(@PathVariable("id") Long id) {
		return this.service.getCustomerById(id)
				      .map(row -> ResponseEntity.ok().body(row))
				      .orElse(ResponseEntity.notFound().build());
	}
	
	@ApiOperation(value = "Consultar Informaci\u00f3n del cliente por el nombre",
			      notes = "Operaci\u00f3n que retorna Informaci\u00f3n del cliente por [Nombre]",
			      response = Customer.class)
	@ApiResponses(value = { 
					        @ApiResponse(code = 200, message = "Informaci\u00f3n del cliente consultada exitosamente!"),
					        @ApiResponse(code = 404, message = "No existe Informaci\u00f3n del cliente!") 
					      }
	         	 )	
	@GetMapping("/customers/names")
	public ResponseEntity<Customer> consultarCustomerPorName(@RequestParam(name = "fname", required = true) String fname) throws ResourceNotFoundException {
		Customer customer = this.service.getCustomerByName(fname)
				                        .orElseThrow(() -> new ResourceNotFoundException("Customer Not Found :: " + fname));
		
		return ResponseEntity.ok().body(customer);
	}
	
	@ApiOperation(value = "Actualiza la Informaci\u00f3n del cliente por identificador",
			      notes = "Operaci\u00f3n que actualiza Informaci\u00f3n del cliente por [ID]",
			      response = Customer.class)
	@ApiResponses(value = { 
					        @ApiResponse(code = 200, message = "Informaci\u00f3n del cliente actualizada exitosamente!"),
					        @ApiResponse(code = 404, message = "No existe Informaci\u00f3n del cliente!") 
					      }
	         	 )	
	@PutMapping("/customers/{id}")
	public ResponseEntity<Customer> actualizarCustomer(@PathVariable(value = "id") Long id, 
			                                           @Valid @RequestBody Customer input) throws ResourceNotFoundException {
		return this.service.getCustomerById(id)
					       .map(row -> {
								    	  row.setFname(input.getFname());
								    	  row.setLname(input.getLname());
								    	  row.setAddress(input.getAddress());
								    	  row.setPhone(input.getPhone());
								    	  row.setEmail(input.getEmail());
								    	  row.setActive(input.getActive());
								    	  
								    	  Customer customer = this.service.updateCustomer(row);
								    	  
								    	  return ResponseEntity.ok().body(customer);
					                  }
					          ).orElse(ResponseEntity.notFound().build());
	}
	
	@ApiOperation(value = "Elimina la Informaci\u00f3n del cliente por identificador",
			      notes = "Operaci\u00f3n que Elimina Informaci\u00f3n del cliente por [ID]",
			      response = Customer.class)
	@ApiResponses(value = { 
					        @ApiResponse(code = 200, message = "Informaci\u00f3n del cliente eliminada exitosamente!"),
					        @ApiResponse(code = 404, message = "No existe Informaci\u00f3n del cliente!") 
					      }
	       	 )	
	@DeleteMapping("/customers/{id}")
	public Map<String, Boolean> eliminarCustomer(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
		Customer customer = this.service.getCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer Not Found :: " + id));
		
		this.service.deleteCustomer(customer);
		
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted", Boolean.TRUE);
		
		return response;
	}
	
}