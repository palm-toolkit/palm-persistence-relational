package de.rwth.i9.palm.persistence.relational;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.EventGroupInterestFlat;
import de.rwth.i9.palm.persistence.EventGroupInterestFlatDAO;

public class EventGroupInterestFlatDAOHibernate extends GenericDAOHibernate<EventGroupInterestFlat> implements EventGroupInterestFlatDAO
{
	private SessionFactory sessionFactory;

	/**
	 * @param sessionFactory
	 */
	public EventGroupInterestFlatDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
		this.sessionFactory = sessionFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.rwth.i9.palm.persistence.EventGroupInterestFlatDAO#eventIdExists(java.
	 * lang.String)
	 */
	public boolean eventIdExists( String eventGroupId )
	{
		if ( eventGroupId.equals( "" ) )
			return false;
		Query query = getCurrentSession().createSQLQuery( "SELECT 1 from eventgroup_interest_flat WHERE eventgroup_id = :eventgroup_id" ).setParameter( "eventgroup_id", eventGroupId );
		return query.uniqueResult() != null;
	}
}