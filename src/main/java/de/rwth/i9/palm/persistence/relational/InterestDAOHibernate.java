package de.rwth.i9.palm.persistence.relational;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Interest;
import de.rwth.i9.palm.persistence.InterestDAO;

public class InterestDAOHibernate extends GenericDAOHibernate<Interest> implements InterestDAO
{
	public InterestDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public Interest getInterestByTerm( String term )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Interest " );
		queryString.append( "WHERE term = :term " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "term", term );

		@SuppressWarnings( "unchecked" )
		List<Interest> interests = query.list();

		if ( interests == null || interests.isEmpty() )
			return null;

		return interests.get( 0 );
	}

	@Override
	public List<Interest> allTerms()
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Interest " );

		Query query = getCurrentSession().createQuery( queryString.toString() );

		@SuppressWarnings( "unchecked" )
		List<Interest> interests = query.list();

		if ( interests == null || interests.isEmpty() )
			return null;

		return interests;
	}

	@Override
	public Map<String, Object> allTermsByPaging( String query, Integer pageNo, Integer maxResult )
	{
		// container
		Map<String, Object> interestMap = new LinkedHashMap<String, Object>();

		// Interests
		StringBuilder mainQuery = new StringBuilder();
		mainQuery.append( "SELECT DISTINCT t " );

		StringBuilder countQuery = new StringBuilder();
		countQuery.append( "SELECT COUNT(DISTINCT t) " );

		StringBuilder restQuery = new StringBuilder();
		restQuery.append( "FROM Interest t " );
		restQuery.append( "WHERE t.term like :query" );

		restQuery.append( " ORDER BY t.term" );

		/* Executes main query */
		Query hibQueryMain = getCurrentSession().createQuery( mainQuery.toString() + restQuery.toString() );
		hibQueryMain.setParameter( "query", "%" + query + "%" );

		if ( pageNo != null )
			hibQueryMain.setFirstResult( pageNo * maxResult );
		if ( maxResult != null )
			hibQueryMain.setMaxResults( maxResult );

		@SuppressWarnings( "unchecked" )
		List<Interest> interests = hibQueryMain.list();

		if ( interests == null || interests.isEmpty() )
		{
			interestMap.put( "totalCount", 0 );
			return interestMap;
		}

		/* Executes count query */
		Query hibQueryCount = getCurrentSession().createQuery( countQuery.toString() + restQuery.toString() );
		hibQueryCount.setParameter( "query", "%" + query + "%" );

		int count = ( (Long) hibQueryCount.uniqueResult() ).intValue();

		interestMap.put( "interests", interests );
		interestMap.put( "totalCount", count );

		return interestMap;

	}

}
