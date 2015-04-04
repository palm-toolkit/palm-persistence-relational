package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Function;
import de.rwth.i9.palm.persistence.FunctionDAO;

public class FunctionDAOHibernate extends GenericDAOHibernate<Function> implements FunctionDAO
{

	public FunctionDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}