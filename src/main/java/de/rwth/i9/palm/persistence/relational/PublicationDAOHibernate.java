package de.rwth.i9.palm.persistence.relational;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
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
import de.rwth.i9.palm.model.Circle;
import de.rwth.i9.palm.model.DataMiningPublication;
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
	public Map<String, Object> getPublicationWithPaging( String query, String publicationType, Author author, Event event, Integer pageNo, Integer maxResult, String year, String orderBy )
	{
		// container
		Map<String, Object> publicationMap = new LinkedHashMap<String, Object>();

		// replace -
		query = query.replace( "-", " " );

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
			stringBuilder.append( "(REPLACE(p.title,'-',' ') LIKE :query " );
			stringBuilder.append( "OR REPLACE(p.abstractText,'-',' ') LIKE :query1 " );
			stringBuilder.append( "OR REPLACE(p.keywordText,'-',' ') LIKE :query2) " );
		}
		if ( !year.equals( "all" ) )
		{
			if ( !isWhereClauseEvoked )
			{
				stringBuilder.append( "WHERE " );
				isWhereClauseEvoked = true;
			}
			else
				stringBuilder.append( "AND " );
			stringBuilder.append( "p.year = :year " );
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

		if ( orderBy != null )
		{
			if ( orderBy.equals( "citation" ) )
				stringBuilder.append( "ORDER BY p.citedBy DESC" );
			else if ( orderBy.equals( "date" ) )
				stringBuilder.append( "ORDER BY p.publicationDate DESC" );
		}
		/* Executes main query */
		Query hibQueryMain = getCurrentSession().createQuery( mainQuery.toString() + stringBuilder.toString() );
		if ( author != null )
			hibQueryMain.setParameter( "author", author );

		if ( !query.equals( "" ) )
		{
			hibQueryMain.setParameter( "query", "%" + query + "%" );
			hibQueryMain.setParameter( "query1", "%" + query + "%" );
			hibQueryMain.setParameter( "query2", "%" + query + "%" );
		}
		if ( !year.equals( "all" ) )
		{
			hibQueryMain.setParameter( "year", year );
		}
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

		if ( pageNo != null )
			hibQueryMain.setFirstResult( pageNo * maxResult );
		if ( maxResult != null )
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
		{
			hibQueryCount.setParameter( "query", "%" + query + "%" );
			hibQueryCount.setParameter( "query1", "%" + query + "%" );
			hibQueryCount.setParameter( "query2", "%" + query + "%" );
		}

		if ( !year.equals( "all" ) )
			hibQueryCount.setParameter( "year", year );

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

	@Override
	public Map<String, Object> getPublicationWithoutPaging( String query, String publicationType, Author author, Event event, String year, String orderBy )
	{
		// container
		Map<String, Object> publicationMap = new LinkedHashMap<String, Object>();

		// replace -
		query = query.replace( "-", " " );

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
			stringBuilder.append( "(REPLACE(p.title,'-',' ') LIKE :query " );
			stringBuilder.append( "OR REPLACE(p.abstractText,'-',' ') LIKE :query1 " );
			stringBuilder.append( "OR REPLACE(p.keywordText,'-',' ') LIKE :query2) " );
		}
		if ( !year.equals( "all" ) )
		{
			if ( !isWhereClauseEvoked )
			{
				stringBuilder.append( "WHERE " );
				isWhereClauseEvoked = true;
			}
			else
				stringBuilder.append( "AND " );
			stringBuilder.append( "p.year = :year " );
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

		if ( orderBy != null )
		{
			if ( orderBy.equals( "citation" ) )
				stringBuilder.append( "ORDER BY p.citedBy DESC" );
			else if ( orderBy.equals( "date" ) )
				stringBuilder.append( "ORDER BY p.publicationDate DESC" );
		}
		/* Executes main query */
		Query hibQueryMain = getCurrentSession().createQuery( mainQuery.toString() + stringBuilder.toString() );
		if ( author != null )
			hibQueryMain.setParameter( "author", author );

		if ( !query.equals( "" ) )
		{
			hibQueryMain.setParameter( "query", "%" + query + "%" );
			hibQueryMain.setParameter( "query1", "%" + query + "%" );
			hibQueryMain.setParameter( "query2", "%" + query + "%" );
		}
		if ( !year.equals( "all" ) )
		{
			hibQueryMain.setParameter( "year", year );
		}
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

		Integer pageNo = 1;
		Integer maxResult = 1000;

		if ( pageNo != null )
			hibQueryMain.setFirstResult( pageNo * maxResult );
		if ( maxResult != null )
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
		{
			hibQueryCount.setParameter( "query", "%" + query + "%" );
			hibQueryCount.setParameter( "query1", "%" + query + "%" );
			hibQueryCount.setParameter( "query2", "%" + query + "%" );
		}

		if ( !year.equals( "all" ) )
			hibQueryCount.setParameter( "year", year );

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
		// alternatively you can write the Lucene query using the Lucene query
		// parser
		// or the Lucene programmatic API. The Hibernate Search DSL is
		// recommended though
		QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity( Publication.class ).get();

		org.apache.lucene.search.Query query = qb.keyword().onFields( "title", "abstractText", "contentText" ).matching( queryString ).createQuery();

		// wrap Lucene query in a org.hibernate.Query
		org.hibernate.search.FullTextQuery hibQuery = fullTextSession.createFullTextQuery( query, Publication.class );

		// org.apache.lucene.search.Sort sort = new Sort( new SortField(
		// "title", (Type) SortField.STRING_FIRST ) );
		// hibQuery.setSort( sort );

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
	public Map<String, Object> getPublicationByFullTextSearchWithPaging( String query, String publicationType, Author author, Event event, Integer page, Integer maxResult, String year, String orderBy )
	{
		// Due to difficulties connecting author with publication on Hibernate
		// Search
		// if author is not null, then use standard search
		if ( author != null )
			return this.getPublicationWithPaging( query, publicationType, author, event, page, maxResult, year, orderBy );

		if ( query.equals( "" ) )
			return this.getPublicationWithPaging( query, publicationType, author, event, page, maxResult, year, orderBy );

		// container
		Map<String, Object> publicationMap = new LinkedHashMap<String, Object>();

		// publication types, collect as Enun list
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

		FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );

		// create native Lucene query using the query DSL
		// alternatively you can write the Lucene query using the Lucene query
		// parser
		// or the Lucene programmatic API. The Hibernate Search DSL is
		// recommended though
		QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity( Publication.class ).get();

		// query builder for specific

		@SuppressWarnings( "rawtypes" )
		BooleanJunction combinedBooleanJunction = qb.bool();

		combinedBooleanJunction.must( qb.keyword().onFields( "title", "abstractText", "contentText" ).matching( query ).createQuery() );

		@SuppressWarnings( "rawtypes" )
		BooleanJunction authorBooleanJunction = qb.bool();

		// TODO
		// org.hibernate.search.exception.SearchException: Unable to find field
		// publicationAuthors.author in de.rwth.i9.palm.model.Publication
		// at
		// org.hibernate.search.engine.spi.DocumentBuilderIndexedEntity.objectToString(DocumentBuilderIndexedEntity.java:888)
		if ( author != null && false )
		{
			org.apache.lucene.search.Query mustAuthorQuery = qb.keyword().onFields( "publicationAuthors.author" ).matching( author ).createQuery();

			authorBooleanJunction.must( mustAuthorQuery );
		}

		if ( !authorBooleanJunction.isEmpty() )
			combinedBooleanJunction.must( authorBooleanJunction.createQuery() );

		@SuppressWarnings( "rawtypes" )
		BooleanJunction yearBooleanJunction = qb.bool();

		if ( !year.equals( "all" ) )
		{
			org.apache.lucene.search.Query mustAuthorQuery = qb.keyword().onFields( "year" ).matching( year ).createQuery();

			yearBooleanJunction.must( mustAuthorQuery );
		}

		if ( !yearBooleanJunction.isEmpty() )
			combinedBooleanJunction.must( yearBooleanJunction.createQuery() );

		if ( !publicationTypes.isEmpty() )
		{
			@SuppressWarnings( "rawtypes" )
			BooleanJunction publicationTypeBooleanJunction = qb.bool();
			for ( PublicationType eachPublicationType : publicationTypes )
			{

				// TODO : change must with should
				org.apache.lucene.search.Query mustPublicationTypeQuery = qb.keyword().onFields( "publicationType" ).matching( eachPublicationType ).createQuery();

				publicationTypeBooleanJunction.should( mustPublicationTypeQuery );

			}
			combinedBooleanJunction.must( publicationTypeBooleanJunction.createQuery() );
		}

		// wrap Lucene query in a org.hibernate.Query
		org.hibernate.search.FullTextQuery hibQuery = fullTextSession.createFullTextQuery( combinedBooleanJunction.createQuery(), Publication.class );

		// get the total number of matching elements
		int totalRows = hibQuery.getResultSize();

		// apply limit
		if ( page != null )
			hibQuery.setFirstResult( page * maxResult );
		if ( maxResult != null )
			hibQuery.setMaxResults( maxResult );

		// org.apache.lucene.search.Sort sort = new Sort( new SortField(
		// "title", (Type) SortField.STRING_FIRST ) );
		// hibQuery.setSort( sort );

		// prepare the container for result
		@SuppressWarnings( "unchecked" )
		List<Publication> publications = hibQuery.list();

		// TODO : remove this code after problem with hibernate search solved
		// Eliminate publication that not written by author

		if ( author != null )
		{
			for ( Iterator<Publication> iter = publications.listIterator(); iter.hasNext(); )
			{
				Publication publication = iter.next();
				boolean removePublication = true;
				for ( Author eachAuthor : publication.getAuthors() )
				{
					if ( author.equals( eachAuthor ) )
					{
						removePublication = false;
						break;
					}
				}
				if ( removePublication )
					iter.remove();
			}
		}

		if ( totalRows == 0 )
		{
			publicationMap.put( "totalCount", 0 );
			return publicationMap;
		}

		publicationMap.put( "publications", publications );

		if ( maxResult != null && publications.size() < maxResult && publications.size() < totalRows )
			totalRows = publications.size();

		publicationMap.put( "totalCount", totalRows );

		return publicationMap;
	}

	@Override
	public Map<String, Object> getPublicationByFullTextSearchWithoutPaging( String query, String publicationType, Author author, Event event, String year, String orderBy )
	{
		// Due to difficulties connecting author with publication on Hibernate
		// Search
		// if author is not null, then use standard search
		if ( author != null )
			return this.getPublicationWithoutPaging( query, publicationType, author, event, year, orderBy );

		System.out.println( "step 8" );

		if ( query.equals( "" ) )
			return this.getPublicationWithoutPaging( query, publicationType, author, event, year, orderBy );

		// container
		Map<String, Object> publicationMap = new LinkedHashMap<String, Object>();

		// publication types, collect as Enun list
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

		System.out.println( "step 9" );

		FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );

		// create native Lucene query using the query DSL
		// alternatively you can write the Lucene query using the Lucene query
		// parser
		// or the Lucene programmatic API. The Hibernate Search DSL is
		// recommended though
		QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity( Publication.class ).get();

		// query builder for specific

		@SuppressWarnings( "rawtypes" )
		BooleanJunction combinedBooleanJunction = qb.bool();

		combinedBooleanJunction.must( qb.keyword().onFields( "title", "abstractText", "contentText" ).matching( query ).createQuery() );

		@SuppressWarnings( "rawtypes" )
		BooleanJunction authorBooleanJunction = qb.bool();

		// TODO
		// org.hibernate.search.exception.SearchException: Unable to find field
		// publicationAuthors.author in de.rwth.i9.palm.model.Publication
		// at
		// org.hibernate.search.engine.spi.DocumentBuilderIndexedEntity.objectToString(DocumentBuilderIndexedEntity.java:888)
		if ( author != null && false )
		{
			org.apache.lucene.search.Query mustAuthorQuery = qb.keyword().onFields( "publicationAuthors.author" ).matching( author ).createQuery();

			authorBooleanJunction.must( mustAuthorQuery );
		}

		if ( !authorBooleanJunction.isEmpty() )
			combinedBooleanJunction.must( authorBooleanJunction.createQuery() );

		@SuppressWarnings( "rawtypes" )
		BooleanJunction yearBooleanJunction = qb.bool();

		if ( !year.equals( "all" ) )
		{
			org.apache.lucene.search.Query mustAuthorQuery = qb.keyword().onFields( "year" ).matching( year ).createQuery();

			yearBooleanJunction.must( mustAuthorQuery );
		}

		if ( !yearBooleanJunction.isEmpty() )
			combinedBooleanJunction.must( yearBooleanJunction.createQuery() );

		if ( !publicationTypes.isEmpty() )
		{
			@SuppressWarnings( "rawtypes" )
			BooleanJunction publicationTypeBooleanJunction = qb.bool();
			for ( PublicationType eachPublicationType : publicationTypes )
			{

				// TODO : change must with should
				org.apache.lucene.search.Query mustPublicationTypeQuery = qb.keyword().onFields( "publicationType" ).matching( eachPublicationType ).createQuery();

				publicationTypeBooleanJunction.should( mustPublicationTypeQuery );

			}
			combinedBooleanJunction.must( publicationTypeBooleanJunction.createQuery() );
		}

		System.out.println( "step 10" );

		// wrap Lucene query in a org.hibernate.Query
		org.hibernate.search.FullTextQuery hibQuery = fullTextSession.createFullTextQuery( combinedBooleanJunction.createQuery(), Publication.class );

		// get the total number of matching elements
		int totalRows = hibQuery.getResultSize();

		// org.apache.lucene.search.Sort sort = new Sort( new SortField(
		// "title", (Type) SortField.STRING_FIRST ) );
		// hibQuery.setSort( sort );

		// prepare the container for result
		@SuppressWarnings( "unchecked" )
		List<Publication> publications = hibQuery.list();

		// TODO : remove this code after problem with hibernate search solved
		// Eliminate publication that not written by author

		if ( author != null )
		{
			for ( Iterator<Publication> iter = publications.listIterator(); iter.hasNext(); )
			{
				Publication publication = iter.next();
				boolean removePublication = true;
				for ( Author eachAuthor : publication.getAuthors() )
				{
					if ( author.equals( eachAuthor ) )
					{
						removePublication = false;
						break;
					}
				}
				if ( removePublication )
					iter.remove();
			}
		}

		if ( totalRows == 0 )
		{
			publicationMap.put( "totalCount", 0 );
			return publicationMap;
		}

		publicationMap.put( "publications", publications );

		totalRows = publications.size();

		publicationMap.put( "totalCount", totalRows );

		System.out.println( "step 11" );

		return publicationMap;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<Publication> getPublicationByEventWithPaging( Event event, Integer pageNo, Integer maxResult )
	{
		// do query twice, first query the total rows
		Query queryCount = getCurrentSession().createQuery( "FROM Publication WHERE event = :event" );
		queryCount.setParameter( "event", event );
		int countTotal = queryCount.list().size();

		Query hibQuery = getCurrentSession().createQuery( "FROM Publication WHERE event = :event" );
		hibQuery.setParameter( "event", event );
		if ( pageNo != null )
			hibQuery.setFirstResult( pageNo * maxResult );
		if ( maxResult != null )
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
	public List<Publication> getPublicationByEventId( String eventId )
	{
		Query hibQuery = getCurrentSession().createQuery( "FROM Publication WHERE event.id = :eventId" );
		hibQuery.setParameter( "eventId", eventId );

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
	public List<Publication> getPublicationViaPhraseSlopQuery( String publicationTitle, int slope )
	{
		FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );

		// create native Lucene query using the query DSL
		// alternatively you can write the Lucene query using the Lucene query
		// parser
		// or the Lucene programmatic API. The Hibernate Search DSL is
		// recommended though
		QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity( Publication.class ).get();

		@SuppressWarnings( "deprecation" )
		org.apache.lucene.search.Query query = qb.phrase().withSlop( slope ).onField( "title" ).sentence( publicationTitle ).createQuery();

		// wrap Lucene query in a org.hibernate.Query
		org.hibernate.search.FullTextQuery hibQuery = fullTextSession.createFullTextQuery( query, Publication.class );

		// org.apache.lucene.search.Sort sort = new Sort( new SortField(
		// "title", (Type) SortField.STRING_FIRST ) );
		// hibQuery.setSort( sort );

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

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<Publication> getPublicationByAuthorId( String authorId )
	{
		StringBuilder queryString = new StringBuilder();

		queryString.append( "SELECT DISTINCT p FROM Author a " );
		queryString.append( "JOIN a.publicationAuthors p_a " );
		queryString.append( "JOIN p_a.publication p " );
		queryString.append( "WHERE a.id = :authorId" );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "authorId", authorId );

		@SuppressWarnings( "unchecked" )
		List<Publication> publications = query.list();

		if ( publications == null || publications.isEmpty() )
			return Collections.emptyList();

		return publications;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<String> getDistinctPublicationYearByAuthor( Author author, String orderBy )
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( "SELECT DISTINCT p.year " );
		stringBuilder.append( "FROM Publication p " );
		stringBuilder.append( "LEFT JOIN p.publicationAuthors pa " );
		stringBuilder.append( "WHERE pa.author = :author " );
		stringBuilder.append( "AND p.year IS NOT NULL " );
		if ( orderBy == null )
			stringBuilder.append( "ORDER BY p.year DESC" );
		else
		{
			if ( orderBy.equals( "ASC" ) )
				stringBuilder.append( "ORDER BY p.year ASC" );
			else
				stringBuilder.append( "ORDER BY p.year DESC" );
		}
		/* Executes main query */
		Query hibQueryMain = getCurrentSession().createQuery( stringBuilder.toString() );
		if ( author != null )
			hibQueryMain.setParameter( "author", author );

		@SuppressWarnings( "unchecked" )
		List<String> yearList = hibQueryMain.list();

		return yearList;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<String> getDistinctPublicationYearByCircle( Circle circle, String orderBy )
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( "SELECT DISTINCT p.year " );
		stringBuilder.append( "FROM Circle c " );
		stringBuilder.append( "LEFT JOIN c.publications p " );
		stringBuilder.append( "WHERE c = :c " );
		stringBuilder.append( "AND p.year IS NOT NULL " );
		if ( orderBy == null )
			stringBuilder.append( "ORDER BY p.year DESC" );
		else
		{
			if ( orderBy.equals( "ASC" ) )
				stringBuilder.append( "ORDER BY p.year ASC" );
			else
				stringBuilder.append( "ORDER BY p.year DESC" );
		}
		/* Executes main query */
		Query hibQueryMain = getCurrentSession().createQuery( stringBuilder.toString() );
		if ( circle != null )
			hibQueryMain.setParameter( "c", circle );

		@SuppressWarnings( "unchecked" )
		List<String> yearList = hibQueryMain.list();

		return yearList;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Map<String, Object> getPublicationWithPaging( String query, String publicationType, Circle circle, Integer pageNo, Integer maxResult, String year, String orderBy )
	{
		if ( circle == null )
			return Collections.emptyMap();

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
		stringBuilder.append( "FROM Circle c " );
		stringBuilder.append( "LEFT JOIN c.publications p " );
		stringBuilder.append( "WHERE c = :c " );

		if ( !query.equals( "" ) )
		{
			stringBuilder.append( "AND " );
			stringBuilder.append( "(REPLACE(p.title,'-',' ') LIKE :query " );
			stringBuilder.append( "OR REPLACE(p.abstractText,'-',' ') LIKE :query1 " );
			stringBuilder.append( "OR REPLACE(p.keywordText,'-',' ') LIKE :query2) " );
		}
		if ( !year.equals( "all" ) )
		{

			stringBuilder.append( "AND " );
			stringBuilder.append( "p.year = :year " );
		}
		if ( !publicationTypes.isEmpty() )
		{
			for ( int i = 1; i <= publicationTypes.size(); i++ )
			{
				if ( i == 1 )
					stringBuilder.append( "AND ( " );
				else
					stringBuilder.append( "OR " );

				stringBuilder.append( "p.publicationType = :publicationType" + i + " " );
			}
			stringBuilder.append( " ) " );
		}

		if ( orderBy.equals( "citation" ) )
			stringBuilder.append( "ORDER BY p.citedBy DESC" );
		else if ( orderBy.equals( "date" ) )
			stringBuilder.append( "ORDER BY p.publicationDate DESC" );

		/* Executes main query */
		Query hibQueryMain = getCurrentSession().createQuery( mainQuery.toString() + stringBuilder.toString() );
		hibQueryMain.setParameter( "c", circle );

		if ( !query.equals( "" ) )
		{
			hibQueryMain.setParameter( "query", "%" + query + "%" );
			hibQueryMain.setParameter( "query1", "%" + query + "%" );
			hibQueryMain.setParameter( "query2", "%" + query + "%" );
		}
		if ( !year.equals( "all" ) )
		{
			hibQueryMain.setParameter( "year", year );
		}
		if ( !publicationTypes.isEmpty() )
		{
			int publicationTypeIndex = 1;
			for ( PublicationType eachPublicationType : publicationTypes )
			{
				hibQueryMain.setParameter( "publicationType" + publicationTypeIndex, eachPublicationType );
				publicationTypeIndex++;
			}
		}

		if ( pageNo != null )
			hibQueryMain.setFirstResult( pageNo * maxResult );
		if ( maxResult != null )
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
		hibQueryCount.setParameter( "c", circle );

		if ( !query.equals( "" ) )
		{
			hibQueryCount.setParameter( "query", "%" + query + "%" );
			hibQueryCount.setParameter( "query1", "%" + query + "%" );
			hibQueryCount.setParameter( "query2", "%" + query + "%" );
		}

		if ( !year.equals( "all" ) )
			hibQueryCount.setParameter( "year", year );

		if ( !publicationTypes.isEmpty() )
		{
			int publicationTypeIndex = 1;
			for ( PublicationType eachPublicationType : publicationTypes )
			{
				hibQueryCount.setParameter( "publicationType" + publicationTypeIndex, eachPublicationType );
				publicationTypeIndex++;
			}
		}

		int count = ( (Long) hibQueryCount.uniqueResult() ).intValue();
		publicationMap.put( "totalCount", count );

		return publicationMap;
	}

	@Override
	public List<DataMiningPublication> getDataMiningObjects()
	{
		@SuppressWarnings( "unchecked" )
		List<DataMiningPublication> result = getCurrentSession().createSQLQuery( "SELECT * FROM Publication p WHERE p.id IN ( SELECT p_t.publication_id FROM publication_topic p_t )" ).addEntity( DataMiningPublication.class ).list();
		return result;
	}

	@Override
	public List<Publication> getAllPublications()
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Publication " );

		Query query = getCurrentSession().createQuery( queryString.toString() );

		@SuppressWarnings( "unchecked" )
		List<Publication> publications = query.list();

		if ( publications == null || publications.isEmpty() )
			return null;

		return publications;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public List<Publication> getPublicationByIds( List<String> ids )
	{
		StringBuilder queryString = new StringBuilder();

		queryString.append( "SELECT DISTINCT p FROM Publication p " );
		queryString.append( "WHERE p.id in ( " );

		for ( int i = 0; i < ids.size(); i++ )
		{
			String str = ids.get( i );
			if ( i != ids.size() - 1 )
				queryString.append( "'" + str + "'" + "," );
			else
				queryString.append( "'" + str + "')" );
		}
		// queryString.append( " " );

		Query query;
		List<Publication> publications = new ArrayList<Publication>();
			query = getCurrentSession().createQuery( queryString.toString() );
			publications = query.list();

		if ( publications == null || publications.isEmpty() )
			return Collections.emptyList();

		return publications;
	}
}
