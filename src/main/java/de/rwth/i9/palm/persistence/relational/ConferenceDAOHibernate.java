package de.rwth.i9.palm.persistence.relational;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Conference;
import de.rwth.i9.palm.model.ConferenceGroup;
import de.rwth.i9.palm.persistence.ConferenceDAO;

public class ConferenceDAOHibernate extends GenericDAOHibernate<Conference> implements ConferenceDAO
{

	public ConferenceDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public Map<String, Conference> getNotationConferenceMaps()
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "SELECT cg, c " );
		queryString.append( "FROM ConferenceGroup cg " );
		queryString.append( "JOIN cg.conferences c " );
		queryString.append( "ORDER BY cg.notation ASC, c.year ASC " );

		Query query = getCurrentSession().createQuery( queryString.toString() );

		@SuppressWarnings( "unchecked" )
		List<Object[]> conferenceObjects = query.list();

		if ( conferenceObjects == null || conferenceObjects.isEmpty() )
			return Collections.emptyMap();

		// prepare the map object
		Map<String, Conference> conferencesMap = new LinkedHashMap<String, Conference>();

		// loop through resultList object
		for ( Object[] item : conferenceObjects )
		{
			ConferenceGroup conferenceGroup = (ConferenceGroup) item[0];
			Conference conference = (Conference) item[1];

			conferencesMap.put( conferenceGroup.getNotation() + conference.getYear(), conference );
		}

		return conferencesMap;
	}

}
