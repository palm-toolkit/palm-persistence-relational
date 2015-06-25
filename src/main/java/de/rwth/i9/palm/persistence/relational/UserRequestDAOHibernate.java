package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.UserRequest;
import de.rwth.i9.palm.persistence.UserRequestDAO;

public class UserRequestDAOHibernate extends GenericDAOHibernate<UserRequest> implements UserRequestDAO
{

	public UserRequestDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}
