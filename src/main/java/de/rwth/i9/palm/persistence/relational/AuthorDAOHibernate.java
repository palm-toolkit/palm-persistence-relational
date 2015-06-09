package de.rwth.i9.palm.persistence.relational;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.persistence.AuthorDAO;

public class AuthorDAOHibernate extends GenericDAOHibernate<Author> implements AuthorDAO
{

	public AuthorDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

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

}
