package de.rwth.i9.palm.persistence.relational;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.model.UserAuthorBookmark;
import de.rwth.i9.palm.persistence.UserAuthorBookmarkDAO;

public class UserAuthorBookmarkDAOHibernate extends GenericDAOHibernate<UserAuthorBookmark> implements UserAuthorBookmarkDAO
{

	public UserAuthorBookmarkDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public UserAuthorBookmark getByUserAndAuthor( User user, Author publication )
	{
		if ( user == null || publication == null )
			return null;

		StringBuilder queryString = new StringBuilder();
		queryString.append( "SELECT uab FROM User user " );
		queryString.append( "JOIN user.userAuthorBookmarks uab " );
		queryString.append( "WHERE user = :user " );
		queryString.append( "AND uab.publication = :publication" );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "user", user );
		query.setParameter( "publication", publication );

		@SuppressWarnings( "unchecked" )
		List<UserAuthorBookmark> userAuthorBookmarks = query.list();

		if ( userAuthorBookmarks == null || userAuthorBookmarks.size() == 0 )
			return null;

		return userAuthorBookmarks.get( 0 );
	}

}
