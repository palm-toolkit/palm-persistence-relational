package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.persistence.AuthorDAO;

public class AuthorDAOHibernate extends GenericDAOHibernate<Author> implements AuthorDAO
{

	public AuthorDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}
