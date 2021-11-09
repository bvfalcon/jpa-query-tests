package name.bychkov.jee.jpa;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Data;

@Entity
@Data
public class Apartment implements Serializable
{
	private static final long serialVersionUID = 194109956071475857L;
	
	@Id
	private long id;
	
	private int nummer;
	private int square;
	private Boolean hasBalcony;
	private Integer bedroomsCount;
	private Integer bathroomsCount;
	private BigDecimal rent;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "house_id")
	private House house;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "apartment")
	private List<Resident> residents = new ArrayList<>(0);
}