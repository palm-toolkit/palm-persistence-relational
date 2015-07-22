package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.ConferenceGroup;
import de.rwth.i9.palm.persistence.ConferenceGroupDAO;

public class ConferenceGroupDAOHibernate extends GenericDAOHibernate<ConferenceGroup>implements ConferenceGroupDAO
{

	public ConferenceGroupDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}
