package de.rwth.i9.palm.persistence.relational;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;

import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.Event;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.model.PublicationType;
import de.rwth.i9.palm.persistence.PublicationDAO;

public class PublicationDAOHibernate extends GenericDAOHibernate<Publication> implements PublicationDAO
{

	public PublicationDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws InterruptedException
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
	public Map<String, Object> getPublicationWithPaging( String query, String publicationType, Author author, Event event, int pageNo, int maxResult, String orderBy )
	{
		// container
		Map<String, Object> publicationMap = new LinkedHashMap<String, Object>();

		Set<PublicationType> publicationTypes = new HashSet<PublicationType>();
		if ( !publicationType.equals( "all" ) )
		{
			String[] publicationTypeArray = publicationType.split( "-" );

			if ( publicationTypeArray.length > 0 )
			{
				for ( String eachPublicatonType : publicationTypeArray )
				{
					try
					{
						publicationTypes.add( PublicationType.valueOf( eachPublicatonType.toUpperCase() ) );
					}
					catch ( Exception e )
					{
					}
				}
			}
		}

		boolean isWhereClauseEvoked = false;

		StringBuilder mainQuery = new StringBuilder();
		mainQuery.append( "SELECT DISTINCT p " );

		StringBuilder countQuery = new StringBuilder();
		countQuery.append( "SELECT COUNT(DISTINCT p) " );

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( "FROM Publication p " );

		if ( author != null )
		{
			isWhereClauseEvoked = true;
			stringBuilder.append( "LEFT JOIN p.publicationAuthors pa " );
			stringBuilder.append( "WHERE pa.author = :author " );
		}

		if ( !query.equals( "" ) )
		{
			if ( !isWhereClauseEvoked )
			{
				stringBuilder.append( "WHERE " );
				isWhereClauseEvoked = true;
			}
			else
				stringBuilder.append( "AND " );
			stringBuilder.append( "name LIKE :query " );
		}
		if ( !publicationTypes.isEmpty() )
		{
			for ( int i = 1; i <= publicationTypes.size(); i++ )
			{
				if ( !isWhereClauseEvoked )
				{
					stringBuilder.append( "WHERE ( " );
					isWhereClauseEvoked = true;
				}
				else
				{
					if ( i == 1 )
						stringBuilder.append( "AND ( " );
					else
						stringBuilder.append( "OR " );
				}
				stringBuilder.append( "p.publicationType = :publicationType" + i + " " );
			}
			stringBuilder.append( " ) " );
		}

		if ( event != null )
		{
			if ( !isWhereClauseEvoked )
			{
				stringBuilder.append( "WHERE " );
				isWhereClauseEvoked = true;
			}
			else
				stringBuilder.append( "AND " );
			stringBuilder.append( "p.event = :event " );
		}

		if ( orderBy.equals( "citation" ) )
			stringBuilder.append( "ORDER BY p.citedBy DESC" );
		else if ( orderBy.equals( "date" ) )
			stringBuilder.append( "ORDER BY p.publicationDate DESC" );

		/* Executes main query */
		Query hibQueryMain = getCurrentSession().createQuery( mainQuery.toString() + stringBuilder.toString() );
		if ( author != null )
			hibQueryMain.setParameter( "author", author );

		if ( !query.equals( "" ) )
			hibQueryMain.setParameter( "query", "%" + query + "%" );

		if ( !publicationTypes.isEmpty() )
		{
			int publicationTypeIndex = 1;
			for ( PublicationType eachPublicationType : publicationTypes )
			{
				hibQueryMain.setParameter( "publicationType" + publicationTypeIndex, eachPublicationType );
				publicationTypeIndex++;
			}
		}

		if ( event != null )
			hibQueryMain.setParameter( "event", event );

		hibQueryMain.setFirstResult( pageNo * maxResult );
		hibQueryMain.setMaxResults( maxResult );

		@SuppressWarnings( "unchecked" )
		List<Publication> publications = hibQueryMain.list();

		if ( publications == null || publications.isEmpty() )
		{
			publicationMap.put( "totalCount", 0 );
			return publicationMap;
		}

		publicationMap.put( "publications", publications );

		/* Executes count query */
		Query hibQueryCount = getCurrentSession().createQuery( countQuery.toString() + stringBuilder.toString() );
		if ( author != null )
			hibQueryCount.setParameter( "author", author );

		if ( !query.equals( "" ) )
			hibQueryCount.setParameter( "query", "%" + query + "%" );

		if ( !publicationTypes.isEmpty() )
		{
			int publicationTypeIndex = 1;
			for ( PublicationType eachPublicationType : publicationTypes )
			{
				hibQueryCount.setParameter( "publicationType" + publicationTypeIndex, eachPublicationType );
				publicationTypeIndex++;
			}
		}

		if ( event != null )
			hibQueryCount.setParameter( "event", event );

		int count = ( (Long) hibQueryCount.uniqueResult() ).intValue();
		publicationMap.put( "totalCount", count );

		return publicationMap;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Publication> getPublicationByFullTextSearch( String queryString )
	{
		FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );
		
		// create native Lucene query using the query DSL
		// alternatively you can write the Lucene query using the Lucene query parser
		// or the Lucene programmatic API. The Hibernate Search DSL is recommended though
		QueryBuilder qb = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( Publication.class ).get();
		
		org.apache.lucene.search.Query query = qb
				  .keyword()
				  .onFields("title", "abstractText", "contentText")
				  .matching( queryString )
				  .createQuery();
		
		// wrap Lucene query in a org.hibernate.Query
		org.hibernate.search.FullTextQuery hibQuery =
		    fullTextSession.createFullTextQuery(query, Publication.class);
		
		// org.apache.lucene.search.Sort sort = new Sort( new SortField(
		// "title", (Type) SortField.STRING_FIRST ) );
		// hibQuery.setSort( sort );

		@SuppressWarnings( "unchecked" )
		List<Publication> publications = hibQuery.list();
		
		if( publications ==  null || publications.isEmpty() )
			return Collections.emptyList();
		
		return publications;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Map<String, Object> getPublicationByFullTextSearchWithPaging( String query, String publicationType, Author author, Event event, int page, int maxResult, String orderBy )
	{
		// Due to difficulties connecting author with publication on Hibernate Search
		// if author is not null, then use standard search
		if( author != null )
			this.getPublicationWithPaging( query, publicationType, author, event, page, maxResult, orderBy );

		// container
		Map<String, Object> publicationMap = new LinkedHashMap<String, Object>();
		
		// publication types
		Set<PublicationType> publicationTypes = new HashSet<PublicationType>();
		if ( !publicationType.equals( "all" ) )
		{
			String[] publicationTypeArray = publicationType.split( "-" );

			if ( publicationTypeArray.length > 0 )
			{
				for ( String eachPublicatonType : publicationTypeArray )
				{
					try
					{
						publicationTypes.add( PublicationType.valueOf( eachPublicatonType.toUpperCase() ) );
					}
					catch ( Exception e )
					{
					}
				}
			}
		}

		if ( query.equals( "" ) )
			return this.getPublicationWithPaging( query, publicationType, author, event, page, maxResult, orderBy );

		FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );
		
		// create native Lucene query using the query DSL
		// alternatively you can write the Lucene query using the Lucene query parser
		// or the Lucene programmatic API. The Hibernate Search DSL is recommended though
		QueryBuilder qb = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( Publication.class ).get();
		
		// query builder for specific 
		
		@SuppressWarnings( "rawtypes" )
		BooleanJunction combinedBooleanJunction = qb.bool();
		
		combinedBooleanJunction
					.must( qb
					  .keyword()
					  .onFields("title", "abstractText", "contentText")
					  .matching( query )
					  .createQuery() );
					/*
					.must( qb
						  .keyword()
						  .onFields("publicationType")
						  .matching( PublicationType.CONFERENCE )
						  .createQuery() )
					.must( qb
						  .keyword()
						  .onFields("publicationType")
						  .matching( PublicationType.BOOK )
						  .createQuery() )
				  .createQuery();
					*/
		
		/* unfortunately not working
		@SuppressWarnings( "rawtypes" )
		BooleanJunction authorBooleanJunction = qb.bool();
		
		if( author != null ){
			org.apache.lucene.search.Query mustAuthorQuery = qb
					  .keyword()
.onFields( "publication_author.author" )
					  .matching( author )
					  .createQuery();
			
			authorBooleanJunction.must( mustAuthorQuery );
		}
		
		if( !authorBooleanJunction.isEmpty() )
			combinedBooleanJunction.must( authorBooleanJunction.createQuery() );
		
		*/
		
		if ( !publicationTypes.isEmpty() )
		{
			int publicationTypeIndex = 1;
			for ( PublicationType eachPublicationType : publicationTypes )
			{
				publicationTypeIndex++;
			}
		}
		
		// wrap Lucene query in a org.hibernate.Query
		org.hibernate.search.FullTextQuery hibQuery =
		    fullTextSession.createFullTextQuery(combinedBooleanJunction.createQuery(), Publication.class);
		
		// get the total number of matching elements
		int totalRows = hibQuery.getResultSize();
		
		// apply limit
		hibQuery.setFirstResult( page * maxResult );
		hibQuery.setMaxResults( maxResult );
		
		// org.apache.lucene.search.Sort sort = new Sort( new SortField(
		// "title", (Type) SortField.STRING_FIRST ) );
		// hibQuery.setSort( sort );
		
		// prepare the container for result
		@SuppressWarnings( "unchecked" )
		List<Publication> publications = hibQuery.list();

		if ( totalRows == 0 )
		{
			publicationMap.put( "totalCount", 0 );
			return publicationMap;
		}

		publicationMap.put( "publications", publications );

		if ( publications.size() < maxResult && publications.size() < totalRows )
			totalRows = publications.size();

		publicationMap.put( "totalCount", totalRows );

		return publicationMap;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<Publication> getPublicationByEventWithPaging( Event event, int pageNo, int maxResult )
	{
		// do query twice, first query the total rows
		Query queryCount = getCurrentSession().createQuery( "FROM Publication WHERE event = :event" );
		queryCount.setParameter( "event", event );
		int countTotal = queryCount.list().size();

		Query hibQuery = getCurrentSession().createQuery( "FROM Publication WHERE event = :event" );
		hibQuery.setParameter( "event", event );
		hibQuery.setFirstResult( pageNo * maxResult );
		hibQuery.setMaxResults( maxResult );

		@SuppressWarnings( "unchecked" )
		List<Publication> publications = hibQuery.list();

		if ( publications == null || publications.isEmpty() )
			return Collections.emptyList();

		return publications;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<Publication> getPublicationViaPhraseSlopQuery( String publicationTitle , int slope)
	{
		FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );
		
		// create native Lucene query using the query DSL
		// alternatively you can write the Lucene query using the Lucene query parser
		// or the Lucene programmatic API. The Hibernate Search DSL is recommended though
		QueryBuilder qb = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( Publication.class ).get();
		
		@SuppressWarnings( "deprecation" )
		org.apache.lucene.search.Query query = qb
					.phrase().withSlop( slope )
					.onField( "title" )
					.sentence( publicationTitle )
					.createQuery();
		
		// wrap Lucene query in a org.hibernate.Query
		org.hibernate.search.FullTextQuery hibQuery =
		    fullTextSession.createFullTextQuery(query, Publication.class);
		
		// org.apache.lucene.search.Sort sort = new Sort( new SortField(
		// "title", (Type) SortField.STRING_FIRST ) );
		// hibQuery.setSort( sort );

		@SuppressWarnings( "unchecked" )
		List<Publication> publications = hibQuery.list();
		
		if( publications ==  null || publications.isEmpty() )
			return Collections.emptyList();
		
		return publications;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<Publication> getPublicationByCoAuthors( Author... coauthors )
	{
		if ( coauthors == null || coauthors.length == 0 )
			return Collections.emptyList();

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( "SELECT DISTINCT p " );
		stringBuilder.append( "FROM Publication p " );
		for ( int i = 0; i < coauthors.length; i++ )
		{
			if ( i == 0 )
				stringBuilder.append( "WHERE :author" + i + " in elements(p.coAuthors) " );
			else
				stringBuilder.append( "AND :author" + i + " in elements(p.coAuthors) " );
		}

		Query query = getCurrentSession().createQuery( stringBuilder.toString() );
		for ( int i = 0; i < coauthors.length; i++ )
			query.setParameter( "author" + i, coauthors[i] );

		@SuppressWarnings( "unchecked" )
		List<Publication> publications = query.list();

		if ( publications == null || publications.isEmpty() )
			return Collections.emptyList();

		return publications;
	}

}
