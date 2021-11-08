package name.bychkov.jee.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Data;

@Entity
@Data
public class House implements Serializable
{
	private static final long serialVersionUID = -4066219131080277411L;
	
	@Id
	private long id;
	
	private String country;
	private String state;
	private String province;
	private String city;
	private String address1;
	private String address2;
	private String address3;
	private String address4;
	private String postalCode;
	
	private Double latitude;
	private Double longitude;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "house")
	private List<Apartment> apartments = new ArrayList<>(0);
	
}