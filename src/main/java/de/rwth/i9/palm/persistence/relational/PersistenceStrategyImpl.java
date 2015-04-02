package de.rwth.i9.palm.persistence.relational;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.rwth.i9.palm.persistence.AlgorithmDAO;
import de.rwth.i9.palm.persistence.AuthorAliasDAO;
import de.rwth.i9.palm.persistence.AuthorDAO;
import de.rwth.i9.palm.persistence.InstantiableDAO;
import de.rwth.i9.palm.persistence.InstitutionDAO;
import de.rwth.i9.palm.persistence.KeywordDAO;
import de.rwth.i9.palm.persistence.LocationDAO;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.persistence.PublicationDAO;
import de.rwth.i9.palm.persistence.PublicationOldDAO;
import de.rwth.i9.palm.persistence.RoleDAO;
import de.rwth.i9.palm.persistence.RunTimeDAO;
import de.rwth.i9.palm.persistence.SourceDAO;
import de.rwth.i9.palm.persistence.TopicDAO;
import de.rwth.i9.palm.persistence.UserDAO;
import de.rwth.i9.palm.persistence.VenueDAO;

public class PersistenceStrategyImpl implements PersistenceStrategy
{
	@Autowired
	private SessionFactory sessionFactory;

	private final Map<String, InstantiableDAO> daoMap = new HashMap<String, InstantiableDAO>();

	public void setSessionFactory( SessionFactory sessionFactory )
	{
		this.sessionFactory = sessionFactory;
	}

	public void registerDAO( String name, InstantiableDAO dao )
	{
		if ( name == null || name.trim().equals( "" ) )
			return;
		if ( dao == null )
		{
			return;
		}
		else
		{
			daoMap.put( name, dao );
			return;
		}
	}

	@Autowired( required = false )
	private AlgorithmDAO algorithmDAO;

	@Autowired( required = false )
	private AuthorAliasDAO authorAliasDAO;

	@Autowired( required = false )
	private AuthorDAO authorDAO;

	@Autowired( required = false )
	private InstitutionDAO institutionDAO;

	@Autowired( required = false )
	private KeywordDAO keywordDAO;

	@Autowired( required = false )
	private LocationDAO locationDAO;

	@Autowired( required = false )
	private PublicationDAO publicationDAO;

	@Autowired( required = false )
	private PublicationOldDAO publicationOldDAO;

	@Autowired( required = false )
	private RoleDAO roleDAO;

	@Autowired( required = false )
	private RunTimeDAO runTimeDAO;

	@Autowired( required = false )
	private SourceDAO sourceDAO;

	@Autowired( required = false )
	private TopicDAO topicDAO;

	@Autowired( required = false )
	private UserDAO userDAO;

	@Autowired( required = false )
	private VenueDAO venueDAO;

	@Override
	public AlgorithmDAO getAlgorithmDAO()
	{
		if ( this.algorithmDAO == null )
			this.algorithmDAO = new AlgorithmDAOHibernate( this.sessionFactory );

		return this.algorithmDAO;
	}

	@Override
	public AuthorDAO getAuthorDAO()
	{
		if ( this.authorDAO == null )
			this.authorDAO = new AuthorDAOHibernate( this.sessionFactory );

		return this.authorDAO;
	}

	@Override
	public InstitutionDAO getInstitutionDAO()
	{
		if ( this.institutionDAO == null )
			this.institutionDAO = new InstitutionDAOHibernate( this.sessionFactory );

		return this.institutionDAO;
	}

	@Override
	public KeywordDAO getKeywordDAO()
	{
		if ( this.keywordDAO == null )
			this.keywordDAO = new KeywordDAOHibernate( this.sessionFactory );

		return this.keywordDAO;
	}

	@Override
	public LocationDAO getLocationDAO()
	{
		if ( this.locationDAO == null )
			this.locationDAO = new LocationDAOHibernate( this.sessionFactory );

		return this.locationDAO;
	}

	@Override
	public PublicationDAO getPublicationDAO()
	{
		if ( this.publicationDAO == null )
			this.publicationDAO = new PublicationDAOHibernate( this.sessionFactory );

		return this.publicationDAO;
	}

	@Override
	public RoleDAO getRoleDAO()
	{
		if ( this.roleDAO == null )
			this.roleDAO = new RoleDAOHibernate( this.sessionFactory );

		return this.roleDAO;
	}

	@Override
	public RunTimeDAO getRunTimeDAO()
	{
		if ( this.runTimeDAO == null )
			this.runTimeDAO = new RunTimeDAOHibernate( this.sessionFactory );

		return this.runTimeDAO;
	}

	@Override
	public SourceDAO getSourceDAO()
	{
		if ( this.sourceDAO == null )
			this.sourceDAO = new SourceDAOHibernate( this.sessionFactory );

		return this.sourceDAO;
	}

	@Override
	public TopicDAO getTopicDAO()
	{
		if ( this.topicDAO == null )
			this.topicDAO = new TopicDAOHibernate( this.sessionFactory );

		return this.topicDAO;
	}

	@Override
	public UserDAO getUserDAO()
	{
		if ( this.userDAO == null )
			this.userDAO = new UserDAOHibernate( this.sessionFactory );

		return this.userDAO;
	}

	@Override
	public VenueDAO getVenueDAO()
	{
		if ( this.venueDAO == null )
			this.venueDAO = new VenueDAOHibernate( this.sessionFactory );

		return this.venueDAO;
	}

	@Override
	public AuthorAliasDAO getAuthorAliasDAO()
	{
		if ( this.authorAliasDAO == null )
			this.authorAliasDAO = new AuthorAliasDAOHibernate( this.sessionFactory );

		return this.authorAliasDAO;
	}

	@Override
	public PublicationOldDAO getPublicationOldDAO()
	{
		if ( this.publicationOldDAO == null )
			this.publicationOldDAO = new PublicationOldDAOHibernate( this.sessionFactory );

		return this.publicationOldDAO;
	}
}
