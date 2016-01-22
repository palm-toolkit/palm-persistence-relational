package de.rwth.i9.palm.persistence.relational;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Config;
import de.rwth.i9.palm.persistence.ConfigDAO;

public class ConfigDAOHibernate extends GenericDAOHibernate<Config> implements ConfigDAO
{

	public ConfigDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public Config getConfigByName( String configType )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Config " );
		queryString.append( "WHERE name = :name " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "name", configType );

		@SuppressWarnings( "unchecked" )
		List<Config> configs = query.list();

		if ( configs == null || configs.isEmpty() )
			return null;

		return configs.get( 0 );
	}

}