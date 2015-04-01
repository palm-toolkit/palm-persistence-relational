package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Institution;
import de.rwth.i9.palm.persistence.InstitutionDAO;

public class InstitutionDAOHibernate extends GenericDAOHibernate<Institution> implements InstitutionDAO
{

	public InstitutionDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}
