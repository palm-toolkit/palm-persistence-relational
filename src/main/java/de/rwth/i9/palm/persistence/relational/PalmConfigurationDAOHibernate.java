package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.PalmConfiguration;
import de.rwth.i9.palm.persistence.PalmConfigurationDAO;

public class PalmConfigurationDAOHibernate extends GenericDAOHibernate<PalmConfiguration>implements PalmConfigurationDAO
{

	public PalmConfigurationDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}