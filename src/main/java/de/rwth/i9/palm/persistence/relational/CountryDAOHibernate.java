package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Country;
import de.rwth.i9.palm.persistence.CountryDAO;

public class CountryDAOHibernate extends GenericDAOHibernate<Country> implements CountryDAO
{

	public CountryDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}