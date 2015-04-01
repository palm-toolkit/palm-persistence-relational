package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Topic;
import de.rwth.i9.palm.persistence.TopicDAO;

public class TopicDAOHibernate extends GenericDAOHibernate<Topic> implements TopicDAO
{

	public TopicDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}
