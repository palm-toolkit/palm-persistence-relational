package de.rwth.i9.palm.persistence.relational;

import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Widget;
import de.rwth.i9.palm.model.WidgetType;
import de.rwth.i9.palm.persistence.InstantiableDAO;
import de.rwth.i9.palm.persistence.WidgetDAO;

public class WidgetDAOHibernate extends GenericDAOHibernate<Widget> implements WidgetDAO, InstantiableDAO
{

	public WidgetDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public List<Widget> getActiveWidgetByWidgetType( WidgetType widgetType )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Widget " );
		queryString.append( "WHERE widgetType = :widgetType " );
		queryString.append( "AND widgetStatus = 'ACTIVE'" );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "widgetType", widgetType );

		@SuppressWarnings( "unchecked" )
		List<Widget> widgets = query.list();

		if ( widgets == null || widgets.isEmpty() )
			return Collections.emptyList();

		return widgets;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public List<Widget> getActiveWidgetByWidgetTypeAndGroup( WidgetType widgetType, String widgetGroup )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Widget " );
		queryString.append( "WHERE widgetType = :widgetType " );
		queryString.append( "AND widgetGroup = :widgetGroup " );
		queryString.append( "AND widgetStatus = 'ACTIVE'" );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "widgetType", widgetType );
		query.setParameter( "widgetGroup", widgetGroup );

		@SuppressWarnings( "unchecked" )
		List<Widget> widgets = query.list();

		if ( widgets == null || widgets.isEmpty() )
			return Collections.emptyList();

		return widgets;
	}

}