package de.rwth.i9.palm.persistence.relational;

import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.model.UserWidget;
import de.rwth.i9.palm.model.Widget;
import de.rwth.i9.palm.model.WidgetStatus;
import de.rwth.i9.palm.model.WidgetType;
import de.rwth.i9.palm.persistence.InstantiableDAO;
import de.rwth.i9.palm.persistence.UserWidgetDAO;

public class UserWidgetDAOHibernate extends GenericDAOHibernate<Widget> implements UserWidgetDAO, InstantiableDAO
{

	public UserWidgetDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public List<UserWidget> getWidget( User user, WidgetType widgetType, WidgetStatus... widgetStatuses )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "SELECT uw " );
		queryString.append( "FROM UserWidget uw " );
		queryString.append( "JOIN uw.widget w " );
		queryString.append( "WHERE w.widgetType = :widgetType " );
		queryString.append( "AND uw.user = :user " );
		for ( int i = 0; i < widgetStatuses.length; i++ )
		{
			queryString.append( "AND uw.widgetStatus = :widgetStatus" + i + " " );
		}
		queryString.append( "ORDER BY uw.position ASC" );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "widgetType", widgetType );
		query.setParameter( "user", user );
		for ( int i = 0; i < widgetStatuses.length; i++ )
		{
			query.setParameter( "widgetStatus" + i, widgetStatuses[i] );
		}

		@SuppressWarnings( "unchecked" )
		List<UserWidget> userWidgets = query.list();

		if ( userWidgets == null || userWidgets.isEmpty() )
			return Collections.emptyList();

		return userWidgets;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public List<UserWidget> getWidget( User user, WidgetType widgetType, String widgetGroup, WidgetStatus... widgetStatuses )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "SELECT uw " );
		queryString.append( "FROM UserWidget uw " );
		queryString.append( "JOIN uw.widget w " );
		queryString.append( "WHERE w.widgetType = :widgetType " );
		queryString.append( "AND w.widgetGroup = :widgetGroup " );
		queryString.append( "AND uw.user = :user " );
		for ( int i = 0; i < widgetStatuses.length; i++ )
		{
			queryString.append( "AND uw.widgetStatus = :widgetStatus" + i + " " );
		}
		queryString.append( "ORDER BY uw.position ASC" );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "widgetType", widgetType );
		query.setParameter( "widgetGroup", widgetGroup );
		query.setParameter( "user", user );

		for ( int i = 0; i < widgetStatuses.length; i++ )
		{
			query.setParameter( "widgetStatus" + i, widgetStatuses[i] );
		}

		@SuppressWarnings( "unchecked" )
		List<UserWidget> userWidgets = query.list();

		if ( userWidgets == null || userWidgets.isEmpty() )
			return Collections.emptyList();

		return userWidgets;
	}

}
