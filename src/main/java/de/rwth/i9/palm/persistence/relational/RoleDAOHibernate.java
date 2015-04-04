package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Role;
import de.rwth.i9.palm.persistence.RoleDAO;

public class RoleDAOHibernate extends GenericDAOHibernate<Role> implements RoleDAO
{

	public RoleDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}
