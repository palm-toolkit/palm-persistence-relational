package de.rwth.i9.palm.persistence.relational;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Institution;
import de.rwth.i9.palm.persistence.InstitutionDAO;

public class InstitutionDAOHibernate extends GenericDAOHibernate<Institution> implements InstitutionDAO
{

	public InstitutionDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public Institution getByUri( String institutionUrl )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Institution " );
		queryString.append( "WHERE uri = :uri " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "uri", institutionUrl );

		@SuppressWarnings( "unchecked" )
		List<Institution> institutions = query.list();

		if ( institutions == null || institutions.isEmpty() )
			return null;

		return institutions.get( 0 );
	}

}
