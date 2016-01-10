package de.rwth.i9.palm.persistence.relational;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.Circle;
import de.rwth.i9.palm.persistence.CircleDAO;

public class CircleDAOHibernate extends GenericDAOHibernate<Circle> implements CircleDAO
{

	public CircleDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public Map<String, Object> getCircleWithPaging( String query, Author creator, int pageNo, int maxResult, String orderBy )
	{
		boolean isWhereClauseEvoked = false;

		// container
		Map<String, Object> circleMap = new LinkedHashMap<String, Object>();

		StringBuilder mainQuery = new StringBuilder();
		mainQuery.append( "SELECT DISTINCT c " );

		StringBuilder countQuery = new StringBuilder();
		countQuery.append( "SELECT COUNT(DISTINCT c) " );

		StringBuilder restQuery = new StringBuilder();
		restQuery.append( "FROM Circle c " );

		if ( !query.equals( "" ) )
		{
			isWhereClauseEvoked = true;
			restQuery.append( "WHERE name LIKE :query " );
		}

		if ( creator != null )
		{
			if ( !isWhereClauseEvoked )
			{
				restQuery.append( "WHERE " );
				isWhereClauseEvoked = true;
			}
			else
				restQuery.append( "AND " );
			restQuery.append( "c.creator = :creator " );
		}

		if ( orderBy.equals( "date" ) )
			restQuery.append( "ORDER BY c.creationDate DESC" );
		else if ( orderBy.equals( "name" ) )
			restQuery.append( "ORDER BY c.name DESC" );

		/* Executes main query */
		Query hibQueryMain = getCurrentSession().createQuery( mainQuery.toString() + restQuery.toString() );

		if ( !query.equals( "" ) )
			hibQueryMain.setParameter( "query", "%" + query + "%" );

		if ( creator != null )
			hibQueryMain.setParameter( "creator", creator );

		hibQueryMain.setFirstResult( pageNo * maxResult );
		hibQueryMain.setMaxResults( maxResult );

		@SuppressWarnings( "unchecked" )
		List<Circle> circles = hibQueryMain.list();

		if ( circles == null || circles.isEmpty() )
		{
			circleMap.put( "totalCount", 0 );
			return circleMap;
		}

		circleMap.put( "circles", circles );

		/* Executes count query */
		Query hibQueryCount = getCurrentSession().createQuery( countQuery.toString() + restQuery.toString() );

		if ( !query.equals( "" ) )
			hibQueryCount.setParameter( "query", "%" + query + "%" );

		if ( creator != null )
			hibQueryCount.setParameter( "creator", creator );

		int count = ( (Long) hibQueryCount.uniqueResult() ).intValue();
		circleMap.put( "totalCount", count );

		return circleMap;
	}

}