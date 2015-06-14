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

import de.rwth.i9.palm.model.Conference;
import de.rwth.i9.palm.model.ConferenceGroup;
import de.rwth.i9.palm.persistence.ConferenceDAO;

public class ConferenceDAOHibernate extends GenericDAOHibernate<Conference> implements ConferenceDAO
{
	/**
	 * {@inheritDoc}
	 */
	public ConferenceDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Conference> getNotationConferenceMaps()
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "SELECT cg, c " );
		queryString.append( "FROM ConferenceGroup cg " );
		queryString.append( "JOIN cg.conferences c " );
		queryString.append( "ORDER BY cg.notation ASC, c.year ASC " );

		Query query = getCurrentSession().createQuery( queryString.toString() );

		@SuppressWarnings( "unchecked" )
		List<Object[]> conferenceObjects = query.list();

		if ( conferenceObjects == null || conferenceObjects.isEmpty() )
			return Collections.emptyMap();

		// prepare the map object
		Map<String, Conference> conferencesMap = new LinkedHashMap<String, Conference>();

		// loop through resultList object
		for ( Object[] item : conferenceObjects )
		{
			ConferenceGroup conferenceGroup = (ConferenceGroup) item[0];
			Conference conference = (Conference) item[1];

			conferencesMap.put( conferenceGroup.getNotation() + conference.getYear(), conference );
		}

		return conferencesMap;
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> getConferenceWithPaging( int pageNo, int maxResult )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "SELECT c " );
		queryString.append( "FROM ConferenceGroup cg " );
		queryString.append( "JOIN cg.conferences c " );
		queryString.append( "ORDER BY cg.notation ASC, c.year ASC " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setFirstResult( pageNo * maxResult );
		query.setMaxResults( maxResult );

		// prepare the container for result
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		resultMap.put( "count", this.countTotal() );
		resultMap.put( "result", query.list() );

		return resultMap;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Conference> getConferenceByFullTextSearch( String queryString )
	{
FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );
		
		// create native Lucene query using the query DSL
		// alternatively you can write the Lucene query using the Lucene query parser
		// or the Lucene programmatic API. The Hibernate Search DSL is recommended though
		QueryBuilder qb = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( Conference.class ).get();
		
		org.apache.lucene.search.Query query = qb
				  .keyword()
				  .onFields("conferenceGroup.name", "year", "thema")
				  .matching( queryString )
				  .createQuery();
		
		// wrap Lucene query in a org.hibernate.Query
		org.hibernate.search.FullTextQuery hibQuery =
		    fullTextSession.createFullTextQuery(query, Conference.class);

		@SuppressWarnings( "unchecked" )
		List<Conference> conferences = hibQuery.list();
		
		if( conferences ==  null || conferences.isEmpty() )
			return Collections.emptyList();
		
		return conferences;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> getConferenceByFullTextSearchWithPaging( String queryString, int page, int maxResult )
	{

		if ( queryString.equals( "" ) )
			return this.getConferenceWithPaging( page, maxResult );

		FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );
		
		// create native Lucene query using the query DSL
		// alternatively you can write the Lucene query using the Lucene query parser
		// or the Lucene programmatic API. The Hibernate Search DSL is recommended though
		QueryBuilder qb = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( Conference.class ).get();
		
		org.apache.lucene.search.Query query = qb
				  .keyword()
				  .onFields("conferenceGroup.name", "year", "thema")
				  .matching( queryString )
				  .createQuery();
		
		// wrap Lucene query in a org.hibernate.Query
		org.hibernate.search.FullTextQuery hibQuery =
		    fullTextSession.createFullTextQuery(query, Conference.class);
		
		// get the total number of matching elements
		int totalRows = hibQuery.getResultSize();
		
		// apply limit
		hibQuery.setFirstResult( page * maxResult );
		hibQuery.setMaxResults( maxResult );
		
		if( totalRows == 0 )
			return null;
		
		// prepare the container for result
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		resultMap.put( "count", totalRows );
		resultMap.put( "result", hibQuery.list() );

		return resultMap;
	}

}
