package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Widget;
import de.rwth.i9.palm.persistence.WidgetDAO;

public class WidgetDAOHibernate extends GenericDAOHibernate<Widget> implements WidgetDAO
{

	public WidgetDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}
