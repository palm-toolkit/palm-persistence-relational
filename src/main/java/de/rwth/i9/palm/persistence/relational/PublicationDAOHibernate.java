package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.persistence.PublicationDAO;

public class PublicationDAOHibernate extends GenericDAOHibernate<Publication> implements PublicationDAO
{

	public PublicationDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}
