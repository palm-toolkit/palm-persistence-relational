package de.rwth.i9.palm.persistence.relational;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;

import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.persistence.UserDAO;

public class UserDAOHibernate extends GenericDAOHibernate<User> implements UserDAO
{

	public UserDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public boolean isAuthorizedForFunction( User user, String functionName )
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public User getByUsername( String username )
	{

		if ( username.equalsIgnoreCase( "" ) )
			return null;

		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM User u " );
		queryString.append( "WHERE u.username = :username " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "username", username );

		@SuppressWarnings( "unchecked" )
		List<User> users = query.list();

		if ( users == null || users.isEmpty() )
			return null;

		return users.get( 0 );
	}

	@Override
	public User touch( User user )
	{
		if ( user == null )
			return null;

		user.setLastLogin( DateTime.now().toDate() );

		return this.persist( user );
	}

}
