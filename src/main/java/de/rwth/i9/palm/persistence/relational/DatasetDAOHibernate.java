package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Dataset;
import de.rwth.i9.palm.persistence.DatasetDAO;

public class DatasetDAOHibernate extends GenericDAOHibernate<Dataset> implements DatasetDAO
{

	public DatasetDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}