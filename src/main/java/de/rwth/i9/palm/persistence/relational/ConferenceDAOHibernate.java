package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Conference;
import de.rwth.i9.palm.persistence.ConferenceDAO;

public class ConferenceDAOHibernate extends GenericDAOHibernate<Conference> implements ConferenceDAO
{

	public ConferenceDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}
