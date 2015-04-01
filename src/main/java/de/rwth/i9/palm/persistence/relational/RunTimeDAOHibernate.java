package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.RunTime;
import de.rwth.i9.palm.persistence.RunTimeDAO;

public class RunTimeDAOHibernate extends GenericDAOHibernate<RunTime> implements RunTimeDAO
{

	public RunTimeDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}
