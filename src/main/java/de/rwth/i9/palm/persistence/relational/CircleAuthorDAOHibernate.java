package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.CircleAuthor;
import de.rwth.i9.palm.persistence.CircleAuthorDAO;

public class CircleAuthorDAOHibernate extends GenericDAOHibernate<CircleAuthor> implements CircleAuthorDAO
{

	public CircleAuthorDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}