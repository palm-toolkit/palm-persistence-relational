package de.rwth.i9.palm.persistence.relational;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.persistence.AuthorDAO;

public class AuthorDAOHibernate extends GenericDAOHibernate<Author> implements AuthorDAO
{

	public AuthorDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Author getByLastName( String lastName )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Author " );
		queryString.append( "WHERE lastName = :lastName " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "lastName", lastName );

		@SuppressWarnings( "unchecked" )
		List<Author> authors = query.list();

		if ( authors == null || authors.isEmpty() )
			return null;

		return authors.get( 0 );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Author getByUri( String uri )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Author " );
		queryString.append( "WHERE uri = :uri " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "uri", uri );

		@SuppressWarnings( "unchecked" )
		List<Author> Authors = query.list();

		if ( Authors == null || Authors.isEmpty() )
			return null;

		return Authors.get( 0 );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Author> getAuthorByLastName( String lastName )
	{
		if( lastName.equals( "" ))
			return Collections.emptyList();
		
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Author " );
		queryString.append( "WHERE lastName = :lastName " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "lastName", lastName );

		@SuppressWarnings( "unchecked" )
		List<Author> authors = query.list();

		if ( authors == null || authors.isEmpty() )
			return Collections.emptyList();

		return authors;
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
	public Map<String, Object> getAuthorWithPaging( String queryString, int pageNo, int maxResult )
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( "FROM Author " );
		if ( !queryString.equals( "" ) )
			stringBuilder.append( "WHERE name LIKE :queryString " );

		Query query = getCurrentSession().createQuery( stringBuilder.toString() );
		if ( !queryString.equals( "" ) )
			query.setParameter( "queryString", "%" + queryString + "%" );
		query.setFirstResult( pageNo * maxResult );
		query.setMaxResults( maxResult );

//		@SuppressWarnings( "unchecked" )
//		List<Author> authors = query.list();

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
	public List<Author> getAuthorByFullTextSearch( String queryString )
	{
		FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );
		
		// create native Lucene query using the query DSL
		// alternatively you can write the Lucene query using the Lucene query parser
		// or the Lucene programmatic API. The Hibernate Search DSL is recommended though
		QueryBuilder qb = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( Author.class ).get();
		
		org.apache.lucene.search.Query query = qb
				  .keyword()
				  .onFields("lastName", "name", "institutions.name")
				  .matching( queryString )
				  .createQuery();
		
		// wrap Lucene query in a org.hibernate.Query
		org.hibernate.search.FullTextQuery hibQuery =
		    fullTextSession.createFullTextQuery(query, Author.class);
		
		org.apache.lucene.search.Sort sort = new Sort( new SortField("name",  SortField.Type.STRING ));
		hibQuery.setSort( sort );

		@SuppressWarnings( "unchecked" )
		List<Author> authors = hibQuery.list();
		
		if( authors ==  null || authors.isEmpty() )
			return Collections.emptyList();
		
		return authors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> getAuthorByFullTextSearchWithPaging( String queryString, int page, int maxResult )
	{
		if ( queryString.equals( "" ) )
			return this.getAuthorWithPaging( "", page, maxResult );
		
		FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );
		
		// create native Lucene query using the query DSL
		// alternatively you can write the Lucene query using the Lucene query parser
		// or the Lucene programmatic API. The Hibernate Search DSL is recommended though
		QueryBuilder qb = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( Author.class ).get();
		
		org.apache.lucene.search.Query query = qb
				  .keyword()
				  .onFields( "lastName", "name", "institutions.name" )
				  .matching( queryString )
				  .createQuery();
		
		// wrap Lucene query in a org.hibernate.Query
		org.hibernate.search.FullTextQuery hibQuery =
		    fullTextSession.createFullTextQuery(query, Author.class);
		
		// get the total number of matching elements
		int totalRows = hibQuery.getResultSize();
		
		// apply limit
		hibQuery.setFirstResult( page * maxResult );
		hibQuery.setMaxResults( maxResult );
				
		org.apache.lucene.search.Sort sort = new Sort( new SortField("name",  SortField.Type.STRING ));
		hibQuery.setSort( sort );

		if( totalRows == 0 )
			return null;
		
		// prepare the container for result
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		resultMap.put( "count", totalRows );
		resultMap.put( "result", hibQuery.list() );

		return resultMap;
	}

	@Override
	public List<Author> getAuthorByNameAndInstitution( String name, String institution )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "SELECT author " );
		queryString.append( "FROM Author author " );
		queryString.append( "LEFT JOIN author.aliases authorAlias " );
		queryString.append( "WHERE author.name = :aname " );
		queryString.append( "OR authorAlias.name = :asname " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "aname", name );
		query.setParameter( "asname", name );

		@SuppressWarnings( "unchecked" )
		List<Author> authors = query.list();

		if ( authors == null || authors.isEmpty() )
			return Collections.emptyList();

		// if only one result
		if ( authors.size() == 1 )
		{
			if ( authors.get( 0 ).getInstitutions() == null || authors.get( 0 ).getInstitutions().isEmpty() )
				return authors;
			else
			{
				if ( authors.get( 0 ).getInstitutions().get( 0 ).getName().contains( institution ) )
					return authors;
				else
					return Collections.emptyList();
			}
		}
		else
		{
			Iterator<Author> i = authors.iterator();
			while ( i.hasNext() )
			{
				Author author = i.next();
				if ( !author.getInstitutions().get( 0 ).getName().contains( institution ) )
					i.remove();
			}
		}
		return authors;
	}
}
