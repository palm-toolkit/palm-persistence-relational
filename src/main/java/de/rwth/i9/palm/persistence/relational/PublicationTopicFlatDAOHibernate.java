package de.rwth.i9.palm.persistence.relational;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.PublicationTopicFlat;
import de.rwth.i9.palm.persistence.PublicationTopicFlatDAO;

public class PublicationTopicFlatDAOHibernate extends GenericDAOHibernate<PublicationTopicFlat> implements PublicationTopicFlatDAO
{
	private SessionFactory sessionFactory;

	/**
	 * @param sessionFactory
	 */
	public PublicationTopicFlatDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
		this.sessionFactory = sessionFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.rwth.i9.palm.persistence.PublicationTopicFlatDAO#publicationIdExists(
	 * java.lang.String)
	 */
	public boolean publicationIdExists( String publicationId )
	{
		if ( publicationId.equals( "" ) )
			return false;
		Query query = getCurrentSession().createSQLQuery( "SELECT 1 from publication_topic_flat WHERE publication_id = :publication_id" ).setParameter( "publication_id", publicationId );
		return query.uniqueResult() != null;
	}
}