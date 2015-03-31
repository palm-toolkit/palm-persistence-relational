package de.rwth.i9.palm.persistence.relational;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.rwth.i9.palm.persistence.InstantiableDAO;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

public class PersistenceStrategyImpl implements PersistenceStrategy
{
	@Autowired
	private SessionFactory sessionFactory;

	private final Map<String, InstantiableDAO> daoMap = new HashMap<String, InstantiableDAO>();

	public void setSessionFactory( SessionFactory sessionFactory )
	{
		this.sessionFactory = sessionFactory;
	}

	public void registerDAO( String name, InstantiableDAO dao )
	{
		if ( name == null || name.trim().equals( "" ) )
			return;
		if ( dao == null )
		{
			return;
		}
		else
		{
			daoMap.put( name, dao );
			return;
		}
	}
}
