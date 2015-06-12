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

import de.rwth.i9.palm.model.Publication;
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

	@Override
	public Map<String, Object> getPublicationWithPaging( int pageNo, int maxResult )
	{
		Query query = getCurrentSession().createQuery( "FROM Publication " );
		query.setFirstResult( pageNo * maxResult );
		query.setMaxResults( maxResult );

//		@SuppressWarnings( "unchecked" )
//		List<Publication> publications = query.list();

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
	public List<Publication> getPublicationByFUllTextSearch( String queryString )
	{
		FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );
		
		// create native Lucene query using the query DSL
		// alternatively you can write the Lucene query using the Lucene query parser
		// or the Lucene programmatic API. The Hibernate Search DSL is recommended though
		QueryBuilder qb = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( Publication.class ).get();
		
		org.apache.lucene.search.Query query = qb
				  .keyword()
				  .onFields("title", "abstractTokenized", "contentTextTokenized")
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
	public Map<String, Object> getPublicationByFullTextSearchWithPaging( String queryString, int page, int maxResult )
	{
		if ( queryString.equals( "" ) )
			return this.getPublicationWithPaging( page, maxResult );

		FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );
		
		// create native Lucene query using the query DSL
		// alternatively you can write the Lucene query using the Lucene query parser
		// or the Lucene programmatic API. The Hibernate Search DSL is recommended though
		QueryBuilder qb = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( Publication.class ).get();
		
		org.apache.lucene.search.Query query = qb
				  .keyword()
				  .onFields("title", "abstractTokenized", "contentTextTokenized")
				  .matching( queryString )
				  .createQuery();
		
		// wrap Lucene query in a org.hibernate.Query
		org.hibernate.search.FullTextQuery hibQuery =
		    fullTextSession.createFullTextQuery(query, Publication.class);
		
		// get the total number of matching elements
		int totalRows = hibQuery.getResultSize();
		
		// apply limit
		hibQuery.setFirstResult( page * maxResult );
		hibQuery.setMaxResults( maxResult );
		
		// org.apache.lucene.search.Sort sort = new Sort( new SortField(
		// "title", (Type) SortField.STRING_FIRST ) );
		// hibQuery.setSort( sort );
		
		if( totalRows == 0 )
			return null;
		
		// prepare the container for result
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		resultMap.put( "count", totalRows );
		resultMap.put( "result", hibQuery.list() );

		return resultMap;
	}

}
