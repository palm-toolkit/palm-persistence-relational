package de.rwth.i9.palm.persistence.relational;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.rwth.i9.palm.persistence.AuthorAliasDAO;
import de.rwth.i9.palm.persistence.AuthorDAO;
import de.rwth.i9.palm.persistence.ConferenceDAO;
import de.rwth.i9.palm.persistence.DatasetDAO;
import de.rwth.i9.palm.persistence.ExtractionRuntimeDAO;
import de.rwth.i9.palm.persistence.ExtractionServiceDAO;
import de.rwth.i9.palm.persistence.FunctionDAO;
import de.rwth.i9.palm.persistence.InstantiableDAO;
import de.rwth.i9.palm.persistence.InstitutionDAO;
import de.rwth.i9.palm.persistence.LocationDAO;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.persistence.PublicationDAO;
import de.rwth.i9.palm.persistence.PublicationTopicDAO;
import de.rwth.i9.palm.persistence.ReferenceDAO;
import de.rwth.i9.palm.persistence.RoleDAO;
import de.rwth.i9.palm.persistence.SessionDataSetDAO;
import de.rwth.i9.palm.persistence.SourceDAO;
import de.rwth.i9.palm.persistence.SubjectDAO;
import de.rwth.i9.palm.persistence.UserDAO;
import de.rwth.i9.palm.persistence.WidgetDAO;

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
	private ExtractionServiceDAO extractionServiceDAO;

	@Autowired( required = false )
	private AuthorAliasDAO authorAliasDAO;

	@Autowired( required = false )
	private AuthorDAO authorDAO;

	@Autowired( required = false )
	private DatasetDAO datasetDAO;

	@Autowired( required = false )
	private FunctionDAO functionDAO;

	@Autowired( required = false )
	private InstitutionDAO institutionDAO;

	@Autowired( required = false )
	private SubjectDAO subjectDAO;

	@Autowired( required = false )
	private LocationDAO locationDAO;

	@Autowired( required = false )
	private PublicationDAO publicationDAO;

	@Autowired( required = false )
	private ReferenceDAO referenceDAO;

	@Autowired( required = false )
	private RoleDAO roleDAO;

	@Autowired( required = false )
	private ExtractionRuntimeDAO extractionRuntimeDAO;

	@Autowired( required = false )
	private SessionDataSetDAO sessionDataSetDAO;
	
	@Autowired( required = false )
	private SourceDAO sourceDAO;

	@Autowired( required = false )
	private PublicationTopicDAO publicationTopicDAO;

	@Autowired( required = false )
	private UserDAO userDAO;

	@Autowired( required = false )
	private ConferenceDAO conferenceDAO;

	@Autowired( required = false )
	private WidgetDAO widgetDAO;

	@Override
	public ExtractionServiceDAO getExtractionServiceDAO()
	{
		if ( this.extractionServiceDAO == null )
			this.extractionServiceDAO = new ExtractionServiceDAOHibernate( this.sessionFactory );

		return this.extractionServiceDAO;
	}

	@Override
	public AuthorAliasDAO getAuthorAliasDAO()
	{
		if ( this.authorAliasDAO == null )
			this.authorAliasDAO = new AuthorAliasDAOHibernate( this.sessionFactory );

		return this.authorAliasDAO;
	}

	@Override
	public AuthorDAO getAuthorDAO()
	{
		if ( this.authorDAO == null )
			this.authorDAO = new AuthorDAOHibernate( this.sessionFactory );

		return this.authorDAO;
	}

	@Override
	public DatasetDAO getDatasetDAO()
	{
		if ( this.datasetDAO == null )
			this.datasetDAO = new DatasetDAOHibernate( this.sessionFactory );

		return this.datasetDAO;
	}

	@Override
	public FunctionDAO getFunctionDAO()
	{
		if ( this.functionDAO == null )
			this.functionDAO = new FunctionDAOHibernate( this.sessionFactory );

		return this.functionDAO;
	}

	@Override
	public InstitutionDAO getInstitutionDAO()
	{
		if ( this.institutionDAO == null )
			this.institutionDAO = new InstitutionDAOHibernate( this.sessionFactory );

		return this.institutionDAO;
	}

	@Override
	public SubjectDAO getSubjectDAO()
	{
		if ( this.subjectDAO == null )
			this.subjectDAO = new SubjectDAOHibernate( this.sessionFactory );

		return this.subjectDAO;
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
	public ReferenceDAO getReferenceDAO()
	{
		if ( this.referenceDAO == null )
			this.referenceDAO = new ReferenceDAOHibernate( this.sessionFactory );

		return this.referenceDAO;
	}

	@Override
	public RoleDAO getRoleDAO()
	{
		if ( this.roleDAO == null )
			this.roleDAO = new RoleDAOHibernate( this.sessionFactory );

		return this.roleDAO;
	}

	@Override
	public ExtractionRuntimeDAO getExtractionRuntimeDAO()
	{
		if ( this.extractionRuntimeDAO == null )
			this.extractionRuntimeDAO = new ExtractionRuntimeDAOHibernate( this.sessionFactory );

		return this.extractionRuntimeDAO;
	}
	
	@Override
	public SessionDataSetDAO getSessionDataSetDAO()
	{
		if ( this.sessionDataSetDAO == null )
			this.sessionDataSetDAO = new SessionDataSetDAOHibernate( this.sessionFactory );

		return this.sessionDataSetDAO;
	}

	@Override
	public SourceDAO getSourceDAO()
	{
		if ( this.sourceDAO == null )
			this.sourceDAO = new SourceDAOHibernate( this.sessionFactory );

		return this.sourceDAO;
	}

	@Override
	public PublicationTopicDAO getPublicationTopicDAO()
	{
		if ( this.publicationTopicDAO == null )
			this.publicationTopicDAO = new PublicationTopicDAOHibernate( this.sessionFactory );

		return this.publicationTopicDAO;
	}

	@Override
	public UserDAO getUserDAO()
	{
		if ( this.userDAO == null )
			this.userDAO = new UserDAOHibernate( this.sessionFactory );

		return this.userDAO;
	}

	@Override
	public ConferenceDAO getConferenceDAO()
	{
		if ( this.conferenceDAO == null )
			this.conferenceDAO = new ConferenceDAOHibernate( this.sessionFactory );

		return this.conferenceDAO;
	}

	@Override
	public WidgetDAO getWidgetDAO()
	{
		if ( this.widgetDAO == null )
			this.widgetDAO = new WidgetDAOHibernate( this.sessionFactory );

		return this.widgetDAO;
	}

}
