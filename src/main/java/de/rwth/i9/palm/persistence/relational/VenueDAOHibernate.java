package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Venue;
import de.rwth.i9.palm.persistence.VenueDAO;

public class VenueDAOHibernate extends GenericDAOHibernate<Venue> implements VenueDAO
{

	public VenueDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}
