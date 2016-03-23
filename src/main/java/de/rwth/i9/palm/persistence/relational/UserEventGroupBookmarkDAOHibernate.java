package de.rwth.i9.palm.persistence.relational;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.EventGroup;
import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.model.UserEventGroupBookmark;
import de.rwth.i9.palm.persistence.UserEventGroupBookmarkDAO;

public class UserEventGroupBookmarkDAOHibernate extends GenericDAOHibernate<UserEventGroupBookmark> implements UserEventGroupBookmarkDAO
{

	public UserEventGroupBookmarkDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public UserEventGroupBookmark getByUserAndEventGroup( User user, EventGroup publication )
	{
		if ( user == null || publication == null )
			return null;

		StringBuilder queryString = new StringBuilder();
		queryString.append( "SELECT ueb FROM User user " );
		queryString.append( "JOIN user.userEventGroupBookmarks ueb " );
		queryString.append( "WHERE user = :user " );
		queryString.append( "AND ueb.publication = :publication" );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "user", user );
		query.setParameter( "publication", publication );

		@SuppressWarnings( "unchecked" )
		List<UserEventGroupBookmark> userEventGroupBookmarks = query.list();

		if ( userEventGroupBookmarks == null || userEventGroupBookmarks.size() == 0 )
			return null;

		return userEventGroupBookmarks.get( 0 );
	}

}
