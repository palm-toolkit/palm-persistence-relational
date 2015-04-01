package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.persistence.UserDAO;

public class UserDAOHibernate extends GenericDAOHibernate<User> implements UserDAO
{

	public UserDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}
