package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.PublicationOld;
import de.rwth.i9.palm.persistence.PublicationOldDAO;

public class PublicationOldDAOHibernate extends GenericDAOHibernate<PublicationOld> implements PublicationOldDAO
{

	public PublicationOldDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}
