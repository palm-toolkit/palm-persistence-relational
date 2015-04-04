package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Keyword;
import de.rwth.i9.palm.persistence.KeywordDAO;

public class KeywordDAOHibernate extends GenericDAOHibernate<Keyword> implements KeywordDAO
{

	public KeywordDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}
