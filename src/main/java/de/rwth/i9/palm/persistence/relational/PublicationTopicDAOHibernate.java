package de.rwth.i9.palm.persistence.relational;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.PublicationTopic;
import de.rwth.i9.palm.persistence.PublicationTopicDAO;

public class PublicationTopicDAOHibernate extends GenericDAOHibernate<PublicationTopic> implements PublicationTopicDAO
{

	public PublicationTopicDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public List<String> allTopics()
	{
		StringBuilder mainTopicQuery = new StringBuilder();
		mainTopicQuery.append( "SELECT DISTINCT t.term " );

		StringBuilder stringTopicBuilder = new StringBuilder();
		stringTopicBuilder.append( "FROM TermValue t " );

		Query hibQueryTopicMain = getCurrentSession().createQuery( mainTopicQuery.toString() + stringTopicBuilder.toString() );

		@SuppressWarnings( "unchecked" )
		List<String> termValues = hibQueryTopicMain.list();

		return termValues;
	}

}
