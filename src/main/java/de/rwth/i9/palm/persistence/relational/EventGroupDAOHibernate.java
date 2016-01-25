package de.rwth.i9.palm.persistence.relational;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import de.rwth.i9.palm.model.EventGroup;
import de.rwth.i9.palm.model.PublicationType;
import de.rwth.i9.palm.persistence.EventGroupDAO;

public class EventGroupDAOHibernate extends GenericDAOHibernate<EventGroup>implements EventGroupDAO
{

	public EventGroupDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public List<EventGroup> getEventGroupListWithPaging( String queryString, String type, int pageNo, int maxResult )
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( "SELECT cg " );
		stringBuilder.append( "FROM EventGroup cg " );
		if ( !queryString.equals( "" ) )
			stringBuilder.append( "WHERE cg.name LIKE :queryString AND cg.added IS TRUE " );
		else
			stringBuilder.append( "WHERE cg.added IS TRUE " );

		if ( type.equals( "conference" ) || type.equals( "workshop" ) )
			stringBuilder.append( "AND ( cg.publicationType = :pubTypeConference OR cg.publicationType = :pubTypeWorkshop ) " );
		if ( type.equals( "journal" ) )
			stringBuilder.append( "AND cg.publicationType = :pubTypeJournal " );

		stringBuilder.append( "ORDER BY cg.name" );

		Query query = getCurrentSession().createQuery( stringBuilder.toString() );
		if ( !queryString.equals( "" ) )
			query.setParameter( "queryString", "%" + queryString + "%" );
		if ( type.equals( "conference" ) || type.equals( "workshop" ) )
		{
			query.setParameter( "pubTypeConference", PublicationType.CONFERENCE );
			query.setParameter( "pubTypeWorkshop", PublicationType.WORKSHOP );
		}
		if ( type.equals( "journal" ) )
			query.setParameter( "pubTypeJournal", PublicationType.JOURNAL );

		query.setFirstResult( pageNo * maxResult );
		query.setMaxResults( maxResult );

		// prepare the container for result
		List<EventGroup> eventGroup = new ArrayList<EventGroup>();

		eventGroup = query.list();

		return eventGroup;
	}

	@Override
	public Map<String, Object> getEventGroupMapWithPaging( String queryString, String type, int pageNo, int maxResult )
	{
		StringBuilder mainQuery = new StringBuilder();
		mainQuery.append( "SELECT cg " );

		StringBuilder countQuery = new StringBuilder();
		countQuery.append( "SELECT COUNT(DISTINCT cg) " );

		StringBuilder restQuery = new StringBuilder();
		restQuery.append( "FROM EventGroup cg " );
		if ( !queryString.equals( "" ) )
			restQuery.append( "WHERE cg.name LIKE :queryString AND cg.added IS TRUE " );
		else
			restQuery.append( "WHERE cg.added IS TRUE " );

		if ( type.equals( "conference" ) || type.equals( "workshop" ) )
			restQuery.append( "AND ( cg.publicationType = :pubTypeConference OR cg.publicationType = :pubTypeWorkshop ) " );
		if ( type.equals( "journal" ) )
			restQuery.append( "AND cg.publicationType = :pubTypeJournal " );

		restQuery.append( "ORDER BY cg.name" );

		Query query = getCurrentSession().createQuery( mainQuery.toString() + restQuery.toString() );
		if ( !queryString.equals( "" ) )
			query.setParameter( "queryString", "%" + queryString + "%" );
		if ( type.equals( "conference" ) || type.equals( "workshop" ) )
		{
			query.setParameter( "pubTypeConference", PublicationType.CONFERENCE );
			query.setParameter( "pubTypeWorkshop", PublicationType.WORKSHOP );
		}
		if ( type.equals( "journal" ) )
			query.setParameter( "pubTypeJournal", PublicationType.JOURNAL );

		query.setFirstResult( pageNo * maxResult );
		query.setMaxResults( maxResult );

		/* Executes count query */
		Query hibQueryCount = getCurrentSession().createQuery( countQuery.toString() + restQuery.toString() );
		if ( !queryString.equals( "" ) )
			query.setParameter( "queryString", "%" + queryString + "%" );
		if ( type.equals( "conference" ) || type.equals( "workshop" ) )
		{
			query.setParameter( "pubTypeConference", PublicationType.CONFERENCE );
			query.setParameter( "pubTypeWorkshop", PublicationType.WORKSHOP );
		}
		if ( type.equals( "journal" ) )
			query.setParameter( "pubTypeJournal", PublicationType.JOURNAL );

		int count = ( (Long) hibQueryCount.uniqueResult() ).intValue();

		// prepare the container for result
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		resultMap.put( "totalCount", count );
		resultMap.put( "eventGroups", query.list() );

		return resultMap;
	}

	@Override
	public EventGroup getEventGroupByEventNameOrNotation( String eventNameOrNotation )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "SELECT cg " );
		queryString.append( "FROM EventGroup cg " );
		queryString.append( "WHERE cg.name = :eventNameOrNotation " );
		queryString.append( "OR cg.notation = :eventNameOrNotation " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "eventNameOrNotation", eventNameOrNotation );

		@SuppressWarnings( "unchecked" )
		List<EventGroup> eventGroups = query.list();

		if ( eventGroups == null || eventGroups.isEmpty() )
			return null;

		return eventGroups.get( 0 );
	}

	@Override
	public List<EventGroup> getEventGroupListFullTextSearchWithPaging( String queryString, String type, int pageNo, int maxResult )
	{
		if ( queryString.equals( "" ) )
			return this.getEventGroupListWithPaging( queryString, type, pageNo, maxResult );

		FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );
		
		// create native Lucene query using the query DSL
		// alternatively you can write the Lucene query using the Lucene query parser
		// or the Lucene programmatic API. The Hibernate Search DSL is recommended though
		QueryBuilder qb = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( EventGroup.class ).get();
		
		org.apache.lucene.search.Query query = qb
				  .keyword()
				  .onFields("name", "notation")
				  .matching( queryString )
				  .createQuery();
		
		// wrap Lucene query in a org.hibernate.Query
		org.hibernate.search.FullTextQuery hibQuery =
		    fullTextSession.createFullTextQuery(query, EventGroup.class);
		
		// apply limit
		hibQuery.setFirstResult( pageNo * maxResult );
		hibQuery.setMaxResults( maxResult );
		
		// org.apache.lucene.search.Sort sort = new Sort( new SortField(
		// "title", (Type) SortField.STRING_FIRST ) );
		// hibQuery.setSort( sort );

		@SuppressWarnings( "unchecked" )
		List<EventGroup> eventGroups = hibQuery.list();
		
		if( eventGroups ==  null || eventGroups.isEmpty() )
			return Collections.emptyList();
		
		return eventGroups;
	}

	@Override
	public Map<String, Object> getEventGroupMapFullTextSearchWithPaging( String queryString, String type, int pageNo, int maxResult )
	{
		if ( queryString.equals( "" ) )
			return this.getEventGroupMapWithPaging( queryString, type, pageNo, maxResult );

		FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );
		
		// create native Lucene query using the query DSL
		// alternatively you can write the Lucene query using the Lucene query parser
		// or the Lucene programmatic API. The Hibernate Search DSL is recommended though
		QueryBuilder qb = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( EventGroup.class ).get();
		
		org.apache.lucene.search.Query query = qb
				  .keyword()
				  .onFields("name", "notation")
				  .matching( queryString )
				  .createQuery();
		
		// wrap Lucene query in a org.hibernate.Query
		org.hibernate.search.FullTextQuery hibQuery =
		    fullTextSession.createFullTextQuery(query, EventGroup.class);
		
		// org.apache.lucene.search.Sort sort = new Sort( new SortField(
		// "title", (Type) SortField.STRING_FIRST ) );
		// hibQuery.setSort( sort );
		
		// get the total number of matching elements
		int totalRows = hibQuery.getResultSize();
		
		// apply limit
		hibQuery.setFirstResult( pageNo * maxResult );
		hibQuery.setMaxResults( maxResult );
		
		// prepare the container for result
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
				
		resultMap.put( "totalCount", totalRows );
		resultMap.put( "eventGroups", hibQuery.list() );

		return resultMap;
	}

}
