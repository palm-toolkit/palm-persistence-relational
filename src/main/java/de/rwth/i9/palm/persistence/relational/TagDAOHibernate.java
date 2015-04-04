package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Tag;
import de.rwth.i9.palm.persistence.TagDAO;

public class TagDAOHibernate extends GenericDAOHibernate<Tag> implements TagDAO
{

	public TagDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}