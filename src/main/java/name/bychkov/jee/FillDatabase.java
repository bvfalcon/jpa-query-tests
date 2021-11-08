package name.bychkov.jee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import name.bychkov.jee.jpa.Apartment;
import name.bychkov.jee.jpa.Gender;
import name.bychkov.jee.jpa.House;
import name.bychkov.jee.jpa.Resident;

public class FillDatabase
{
	static int INITIAL_ROWS_COUNT = 100_000;
	
	static EntityManagerFactory dbFactory = Persistence.createEntityManagerFactory("jpa-query-test");
	static Random random = new Random();
	
	public static void main(String... args)
	{
		EntityManager lem = dbFactory.createEntityManager();
		Number count = ((Number) lem.createQuery("SELECT COUNT(o) FROM House o").getSingleResult());
		if (count.intValue() < INITIAL_ROWS_COUNT)
		{
			lem.getTransaction().begin();
			for (int i = count.intValue(); i < INITIAL_ROWS_COUNT; i++)
			{
				House house = createHouse();
				lem.persist(house);
				for (int j = 0; j < random.nextInt(20); j++)
				{
					Apartment ap = createApartment(house);
					lem.persist(ap);
					for (int k = 0; k < random.nextInt(6); k++)
					{
						Resident r = createResident(ap);
						lem.persist(r);
					}
				}
				
				if (i % 1000 == 0)
				{
					lem.flush();
					lem.clear();
				}
			}
			lem.getTransaction().commit();
		}
		lem.close();
	}
	
	static String getRandomString(int length)
	{
		String longString = random.ints(32, 127).limit(length)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
		return longString;
	}
	
	private static House createHouse()
	{
		House house = new House();
		house.setId(random.nextLong());
		house.setAddress1(getRandomString(64));
		house.setAddress2(getRandomString(32));
		house.setAddress3(getRandomString(128));
		house.setAddress4(getRandomString(16));
		house.setCity(getRandomString(20));
		house.setCountry(getRandomString(20));
		house.setState(getRandomString(20));
		house.setLatitude(random.nextDouble() * 180 - 90);
		house.setLongitude(random.nextDouble() * 360 - 180);
		house.setPostalCode(Integer.toString(random.nextInt(1_000_000)));
		house.setProvince(getRandomString(20));
		return house;
	}
	
	private static Apartment createApartment(House house)
	{
		Apartment apartment = new Apartment();
		apartment.setId(random.nextLong());
		apartment.setBathroomsCount(random.nextInt(4));
		apartment.setBedroomsCount(random.nextInt(6));
		apartment.setHasBalcony(random.nextBoolean());
		apartment.setNummer(random.nextInt(50));
		apartment.setRent(BigDecimal.valueOf(random.nextDouble()));
		apartment.setSquare(random.nextInt(300));
		apartment.setHouse(house);
		return apartment;
	}
	
	private static Resident createResident(Apartment apartment)
	{
		Resident resident = new Resident();
		resident.setId(random.nextLong());
		resident.setFirstname(getRandomString(15));
		resident.setLastname(getRandomString(20));
		resident.setMiddlename(getRandomString(20));
		resident.setBirthDate(LocalDate.ofEpochDay(random.nextInt(40_000)));
		resident.setEmail(getRandomString(20));
		resident.setPhoneNumber(Integer.toString(random.nextInt(100_000_000)));
		resident.setGender(Gender.values()[random.nextInt(3)]);
		resident.setApartment(apartment);
		return resident;
	}
}
