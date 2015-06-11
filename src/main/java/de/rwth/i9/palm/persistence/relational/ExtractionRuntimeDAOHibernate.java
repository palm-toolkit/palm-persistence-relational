package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.ExtractionRuntime;
import de.rwth.i9.palm.persistence.ExtractionRuntimeDAO;

public class ExtractionRuntimeDAOHibernate extends GenericDAOHibernate<ExtractionRuntime> implements ExtractionRuntimeDAO
{

	public ExtractionRuntimeDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}
