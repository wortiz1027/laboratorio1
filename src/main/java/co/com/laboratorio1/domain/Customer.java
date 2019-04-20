package co.com.laboratorio1.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiParam(required = false, value = "null")
	@ApiModelProperty("Modelo :: Identificador del registro en la base de datos")
	private Long id;
	
	@Column
	@ApiParam(required = true, value = "Cadena de caracteres")
	@ApiModelProperty("Modelo :: Nombre del cliente")
	private String fname;
	
	@Column
	@ApiParam(required = true, value = "Cadena de caracteres")
	@ApiModelProperty("Modelo :: Apellido del cliente")
	private String lname;
	
	@Column
	@ApiParam(required = true, value = "Cadena de caracteres alfanum\u00e9ricos")
	@ApiModelProperty("Modelo :: Informaci\u00f3n de la direcci\u00f3n de residencia del cliente")
	private String address;
	
	@Column
	@ApiParam(required = true, value = "Cadena de caracteres alfanum\u00e9ricos")
	@ApiModelProperty("Modelo :: Direcci\u00f3n de correo electr\u00f3nico del cliente")
	private String email;
	
	@Column
	@ApiParam(required = true, value = "Valor num\u00e9rico")
	@ApiModelProperty("Modelo :: N\u00famero de tel\u00e9fono del cliente")
	private Integer phone;
	
	@Column
	@ApiParam(required = true, value = "Valor [true|false]")
	@ApiModelProperty("Modelo :: N\u00famero de tel\u00e9fono del cliente")
	private Boolean active;
	
}
