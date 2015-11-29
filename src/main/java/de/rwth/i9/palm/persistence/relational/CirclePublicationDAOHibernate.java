package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.CirclePublication;
import de.rwth.i9.palm.persistence.CirclePublicationDAO;

public class CirclePublicationDAOHibernate extends GenericDAOHibernate<CirclePublication> implements CirclePublicationDAO
{

	public CirclePublicationDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}