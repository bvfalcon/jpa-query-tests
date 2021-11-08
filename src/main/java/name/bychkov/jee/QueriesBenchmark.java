package name.bychkov.jee;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

import name.bychkov.jee.jpa.Apartment_;
import name.bychkov.jee.jpa.House_;
import name.bychkov.jee.jpa.Resident;
import name.bychkov.jee.jpa.Resident_;

@BenchmarkMode(Mode.Throughput)
@Fork(value = 1)
@Warmup(iterations = 5)
@Measurement(iterations = 9)
public class QueriesBenchmark
{
	static EntityManagerFactory dbFactory = Persistence.createEntityManagerFactory("jpa-query-test");
	
	public static void main(String[] args) throws IOException
	{
		org.openjdk.jmh.Main.main(args);
	}
	
	@State(Scope.Thread)
	public static class QueryState
	{
		public EntityManager em;
		public List<String> cities;
		
		@Setup(Level.Iteration)
		public void setupEachIter()
		{
			this.em = dbFactory.createEntityManager();			
		}
		
		@Setup(Level.Invocation)
		public void setupEach()
		{
			this.cities = Arrays.asList(FillDatabase.getRandomString(20), FillDatabase.getRandomString(20), FillDatabase.getRandomString(20));
		}
		
		@TearDown(Level.Invocation)
		public void tearDownEach()
		{
			em.clear();
			((SessionFactoryImpl) dbFactory).getQueryPlanCache().cleanup();
		}
		
		@TearDown(Level.Iteration)
		public void tearDownEachIter()
		{
			this.em.close();
		}
	}
	
	@Benchmark
	public void nativeSql(QueryState state)
	{
		state.em.createNativeQuery("SELECT r.* FROM Apartment a JOIN Resident r ON r.apartment_id = a.id JOIN House h ON a.house_id = h.id WHERE h.city IN :cities")
				.setParameter("cities", state.cities).getResultList();
	}
	
	@Benchmark
	public void hql(QueryState state)
	{
		state.em.createQuery("SELECT r FROM Resident r WHERE r.apartment.house.city IN :cities").setParameter("cities", state.cities).getResultList();
	}
	
	@Benchmark
	public void namedHql(QueryState state)
	{
		state.em.createNamedQuery("Resident.findByHouseCity").setParameter("cities", state.cities).getResultList();
	}
	
	@Benchmark
	public void criteriaApi(QueryState state)
	{
		CriteriaBuilder cb = state.em.getCriteriaBuilder();
		CriteriaQuery<Resident> cq = cb.createQuery(Resident.class);
		Root<Resident> h = cq.from(Resident.class);
		ParameterExpression<List> citiesParameter = cb.parameter(List.class, "cities");
		cq.where(h.join(Resident_.apartment).join(Apartment_.house).get(House_.CITY).in(citiesParameter));
		state.em.createQuery(cq).setParameter("cities", state.cities).getResultList();
	}
	
	@Benchmark
	public void entityGraph(QueryState state)
	{
		EntityGraph<?> graph = state.em.createEntityGraph("resident-graph");
		Query query = state.em.createQuery("SELECT r FROM Resident r WHERE r.apartment.house.city IN :cities");
		query.setHint("javax.persistence.loadgraph", graph); // see note at https://www.baeldung.com/jpa-entity-graph#creating-entity-graph-2
		query.setParameter("cities", state.cities).getResultList();
	}
}