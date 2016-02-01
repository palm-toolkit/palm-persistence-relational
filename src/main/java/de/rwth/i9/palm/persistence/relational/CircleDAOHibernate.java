package de.rwth.i9.palm.persistence.relational;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import de.rwth.i9.palm.helper.comparator.AuthorByNoCitationComparator;
import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.Circle;
import de.rwth.i9.palm.persistence.CircleDAO;

public class CircleDAOHibernate extends GenericDAOHibernate<Circle> implements CircleDAO
{

	public CircleDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doReindexing() throws InterruptedException
	{
		FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );
		fullTextSession.createIndexer().startAndWait();
	}

	@Override
	public Map<String, Object> getCircleWithPaging( String query, Author creator, int pageNo, int maxResult, String orderBy )
	{
		boolean isWhereClauseEvoked = false;

		// container
		Map<String, Object> circleMap = new LinkedHashMap<String, Object>();

		StringBuilder mainQuery = new StringBuilder();
		mainQuery.append( "SELECT DISTINCT c " );

		StringBuilder countQuery = new StringBuilder();
		countQuery.append( "SELECT COUNT(DISTINCT c) " );

		StringBuilder restQuery = new StringBuilder();
		restQuery.append( "FROM Circle c " );

		if ( !query.equals( "" ) )
		{
			isWhereClauseEvoked = true;
			restQuery.append( "WHERE name LIKE :query " );
		}

		if ( creator != null )
		{
			if ( !isWhereClauseEvoked )
			{
				restQuery.append( "WHERE " );
				isWhereClauseEvoked = true;
			}
			else
				restQuery.append( "AND " );
			restQuery.append( "c.creator = :creator " );
		}

		if ( orderBy.equals( "date" ) )
			restQuery.append( "ORDER BY c.creationDate DESC" );
		else if ( orderBy.equals( "name" ) )
			restQuery.append( "ORDER BY c.name DESC" );

		/* Executes main query */
		Query hibQueryMain = getCurrentSession().createQuery( mainQuery.toString() + restQuery.toString() );

		if ( !query.equals( "" ) )
			hibQueryMain.setParameter( "query", "%" + query + "%" );

		if ( creator != null )
			hibQueryMain.setParameter( "creator", creator );

		hibQueryMain.setFirstResult( pageNo * maxResult );
		hibQueryMain.setMaxResults( maxResult );

		@SuppressWarnings( "unchecked" )
		List<Circle> circles = hibQueryMain.list();

		if ( circles == null || circles.isEmpty() )
		{
			circleMap.put( "totalCount", 0 );
			return circleMap;
		}

		circleMap.put( "circles", circles );

		/* Executes count query */
		Query hibQueryCount = getCurrentSession().createQuery( countQuery.toString() + restQuery.toString() );

		if ( !query.equals( "" ) )
			hibQueryCount.setParameter( "query", "%" + query + "%" );

		if ( creator != null )
			hibQueryCount.setParameter( "creator", creator );

		int count = ( (Long) hibQueryCount.uniqueResult() ).intValue();
		circleMap.put( "totalCount", count );

		return circleMap;
	}

	@Override
	public Map<String, Object> getCircleFullTextSearchWithPaging( String queryString, Author creator, int pageNo, int maxResult, String orderBy )
	{
		if ( queryString.equals( "" ) || creator != null )
			return this.getCircleWithPaging( "", creator, pageNo, maxResult, orderBy );
		
		FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );
		
		// create native Lucene query using the query DSL
		// alternatively you can write the Lucene query using the Lucene query parser
		// or the Lucene programmatic API. The Hibernate Search DSL is recommended though
		QueryBuilder qb = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( Circle.class ).get();
		
		org.apache.lucene.search.Query query = qb
				  .keyword()
				  .onFields( "name", "description" )
				  .matching( queryString )
				  .createQuery();
		
		// wrap Lucene query in a org.hibernate.Query
		org.hibernate.search.FullTextQuery hibQuery =
		    fullTextSession.createFullTextQuery(query, Author.class);
		
		// get the total number of matching elements
		int totalRows = hibQuery.getResultSize();
		
		// apply limit
		hibQuery.setFirstResult( pageNo * maxResult );
		hibQuery.setMaxResults( maxResult );

		if( totalRows == 0 )
			return null;
		
		// prepare the container for result
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		
		@SuppressWarnings( "unchecked" )
		List<Author> authors = hibQuery.list();
		
		Collections.sort( authors, new AuthorByNoCitationComparator() );
		
		resultMap.put( "totalCount", totalRows );
		resultMap.put( "authors", authors );

		return resultMap;
	}

}