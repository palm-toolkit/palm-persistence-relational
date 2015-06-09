package de.rwth.i9.palm.persistence.relational;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Location;
import de.rwth.i9.palm.persistence.LocationDAO;

public class LocationDAOHibernate extends GenericDAOHibernate<Location> implements LocationDAO
{

	public LocationDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public Location getByCountry( String countryName )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Location " );
		queryString.append( "WHERE country = :country " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "country", countryName );

		@SuppressWarnings( "unchecked" )
		List<Location> locations = query.list();

		if ( locations == null || locations.isEmpty() )
			return null;

		return locations.get( 0 );
	}

}
