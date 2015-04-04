package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Algorithm;
import de.rwth.i9.palm.persistence.AlgorithmDAO;

public class AlgorithmDAOHibernate extends GenericDAOHibernate<Algorithm> implements AlgorithmDAO
{

	public AlgorithmDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}
