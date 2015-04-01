package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Source;
import de.rwth.i9.palm.persistence.SourceDAO;

public class SourceDAOHibernate extends GenericDAOHibernate<Source> implements SourceDAO
{

	public SourceDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}
