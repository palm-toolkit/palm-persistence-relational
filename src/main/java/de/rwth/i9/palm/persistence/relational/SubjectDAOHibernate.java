package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Subject;
import de.rwth.i9.palm.persistence.SubjectDAO;

public class SubjectDAOHibernate extends GenericDAOHibernate<Subject> implements SubjectDAO
{

	public SubjectDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}
