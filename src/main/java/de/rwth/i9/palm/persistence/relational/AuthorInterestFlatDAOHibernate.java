package de.rwth.i9.palm.persistence.relational;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.AuthorInterestFlat;
import de.rwth.i9.palm.persistence.AuthorInterestFlatDAO;

public class AuthorInterestFlatDAOHibernate extends GenericDAOHibernate<AuthorInterestFlat> implements AuthorInterestFlatDAO
{
	private SessionFactory sessionFactory;

	/**
	 * @param sessionFactory
	 */
	public AuthorInterestFlatDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
		this.sessionFactory = sessionFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.rwth.i9.palm.persistence.AuthorInterestFlatDAO#authorIdExists(java.
	 * lang.String)
	 */
	public boolean authorIdExists( String authorId )
	{
		if ( authorId.equals( "" ) )
			return false;
		Query query = getCurrentSession().createSQLQuery( "SELECT 1 from author_interest_flat WHERE author_id = :author_id" ).setParameter( "author_id", authorId );
		return query.uniqueResult() != null;
	}
}