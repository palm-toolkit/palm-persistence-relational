package de.rwth.i9.palm.persistence.relational;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Reference;
import de.rwth.i9.palm.persistence.ReferenceDAO;

public class ReferenceDAOHibernate extends GenericDAOHibernate<Reference> implements ReferenceDAO
{

	public ReferenceDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public Reference getByUri( String uri )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Reference " );
		queryString.append( "WHERE uri = :uri " );
		queryString.append( "OR sameAsUri = :sameAsUri " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "uri", uri );
		query.setParameter( "sameAsUri", uri );

		@SuppressWarnings( "unchecked" )
		List<Reference> References = query.list();

		if ( References == null || References.isEmpty() )
			return null;

		return References.get( 0 );
	}

}
