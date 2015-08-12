package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.EventGroup;
import de.rwth.i9.palm.persistence.EventGroupDAO;

public class EventGroupDAOHibernate extends GenericDAOHibernate<EventGroup>implements EventGroupDAO
{

	public EventGroupDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}
