package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Circle;
import de.rwth.i9.palm.persistence.CircleDAO;

public class CircleDAOHibernate extends GenericDAOHibernate<Circle> implements CircleDAO
{

	public CircleDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}