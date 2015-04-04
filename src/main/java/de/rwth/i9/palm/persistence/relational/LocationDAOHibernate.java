package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Location;
import de.rwth.i9.palm.persistence.LocationDAO;

public class LocationDAOHibernate extends GenericDAOHibernate<Location> implements LocationDAO
{

	public LocationDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}
