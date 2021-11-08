package name.bychkov.jee.jpa;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedQuery;

import lombok.Data;

@Entity
@Data
@NamedQuery(name = "Resident.findByHouseCity", query = "SELECT r FROM Resident r WHERE r.apartment.house.city IN :cities")
@NamedEntityGraph(name = "resident-graph")
public class Resident implements Serializable
{
	private static final long serialVersionUID = 5592496784847499586L;
	
	@Id
	private long id;
	
	private String firstname;
	private String middlename;
	private String lastname;
	private LocalDate birthDate;
	private Gender gender;
	private String email;
	private String phoneNumber;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Apartment apartment;
}