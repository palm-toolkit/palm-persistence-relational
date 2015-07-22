package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.ExtractionService;
import de.rwth.i9.palm.persistence.ExtractionServiceDAO;

public class ExtractionServiceDAOHibernate extends GenericDAOHibernate<ExtractionService> implements ExtractionServiceDAO
{

	public ExtractionServiceDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}
