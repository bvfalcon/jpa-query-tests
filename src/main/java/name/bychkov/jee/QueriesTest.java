package name.bychkov.jee;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.hibernate.internal.SessionFactoryImpl;

import name.bychkov.jee.jpa.Apartment_;
import name.bychkov.jee.jpa.House_;
import name.bychkov.jee.jpa.Resident;
import name.bychkov.jee.jpa.Resident_;

public class QueriesTest
{
	public static void main(String[] args) throws SQLException
	{
		List<String> cities = Arrays.asList(FillDatabase.getRandomString(20), FillDatabase.getRandomString(20), FillDatabase.getRandomString(20));
		//List<String> cities = Arrays.asList("dZo9gm2cspHBJMGY5qsb", "QNIRhX1mmnIo4O9s0wB2", "enecjhlIKZEFH4qdymeV");
		
		System.out.println("starting");
		
		// native SQL JPA
		List nativeSQL = execute(em -> em.createNativeQuery("SELECT r.* FROM Apartment a JOIN Resident r ON r.apartment_id = a.id JOIN House h ON a.house_id = h.id WHERE h.city IN :cities")
				.setParameter("cities", cities).getResultList());
		System.out.println("nativeSQL JPA: " + nativeSQL.size());
		
		// native SQL JDBC
		Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/queries", "root", "test");
		String array = String.join(", ", cities.stream().map(o -> new StringBuilder("'").append(o).append("'").toString()).collect(Collectors.toList()));
		PreparedStatement ps = conn.prepareStatement("SELECT r.* FROM Apartment a JOIN Resident r ON r.apartment_id = a.id JOIN House h ON a.house_id = h.id WHERE h.city IN (" + array + ")");
		ResultSet rs = ps.executeQuery();
		System.out.println("nativeSQL JDBC: " + size(rs));
		conn.close();
		
		// hql
		List hql = execute(em -> em.createQuery("SELECT r FROM Resident r WHERE r.apartment.house.city IN :cities").setParameter("cities", cities).getResultList());
		System.out.println("hql: " + hql.size());
		
		// named hql
		List namedHql = execute(em -> em.createNamedQuery("Resident.findByHouseCity").setParameter("cities", cities).getResultList());
		System.out.println("namedHql: " + namedHql.size());
		
		// criteria
		List criteria = execute(em ->
		{
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Resident> cq = cb.createQuery(Resident.class);
			Root<Resident> h = cq.from(Resident.class);
			ParameterExpression<List> citiesParameter = cb.parameter(List.class, "cities");
			cq.where(h.join(Resident_.apartment).join(Apartment_.house).get(House_.CITY).in(citiesParameter));
			return em.createQuery(cq).setParameter("cities", cities).getResultList();
		});
		System.out.println("criteria: " + criteria.size());
		
		// entity graph
		List entityGraph = execute(em ->
		{
			EntityGraph<?> graph = em.createEntityGraph("resident-graph");
			Query query = em.createQuery("SELECT r FROM Resident r WHERE r.apartment.house.city IN :cities");
			query.setHint("javax.persistence.loadgraph", graph);
			return query.setParameter("cities", cities).getResultList();
		});
		System.out.println("entityGraph: " + entityGraph.size());
	}
	
	static EntityManagerFactory dbFactory = Persistence.createEntityManagerFactory("jpa-query-test");
	
	private static List execute(Function<EntityManager, List> doit)
	{
		EntityManager em = dbFactory.createEntityManager();
		List result = doit.apply(em);
		em.close();
		((SessionFactoryImpl) dbFactory).getQueryPlanCache().cleanup();
		return result;
	}
	
	private static int size(ResultSet rs) throws SQLException
	{
		int i = 0;
		while (rs.next())
		{
			i++;
		}
		return i;
	}
}