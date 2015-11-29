package de.rwth.i9.palm.persistence.relational;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.rwth.i9.palm.persistence.AuthorDAO;
import de.rwth.i9.palm.persistence.AuthorInterestDAO;
import de.rwth.i9.palm.persistence.AuthorInterestProfileDAO;
import de.rwth.i9.palm.persistence.AuthorSourceDAO;
import de.rwth.i9.palm.persistence.CircleAuthorDAO;
import de.rwth.i9.palm.persistence.CirclePublicationDAO;
import de.rwth.i9.palm.persistence.CountryDAO;
import de.rwth.i9.palm.persistence.EventDAO;
import de.rwth.i9.palm.persistence.EventGroupDAO;
import de.rwth.i9.palm.persistence.ExtractionServiceDAO;
import de.rwth.i9.palm.persistence.ExtractionServicePropertyDAO;
import de.rwth.i9.palm.persistence.FunctionDAO;
import de.rwth.i9.palm.persistence.InstantiableDAO;
import de.rwth.i9.palm.persistence.InstitutionDAO;
import de.rwth.i9.palm.persistence.InterestDAO;
import de.rwth.i9.palm.persistence.InterestProfileDAO;
import de.rwth.i9.palm.persistence.InterestProfilePropertyDAO;
import de.rwth.i9.palm.persistence.LocationDAO;
import de.rwth.i9.palm.persistence.PalmConfigurationDAO;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.persistence.PublicationDAO;
import de.rwth.i9.palm.persistence.PublicationFileDAO;
import de.rwth.i9.palm.persistence.PublicationHistoryDAO;
import de.rwth.i9.palm.persistence.PublicationSourceDAO;
import de.rwth.i9.palm.persistence.PublicationTopicDAO;
import de.rwth.i9.palm.persistence.RoleDAO;
import de.rwth.i9.palm.persistence.SessionDataSetDAO;
import de.rwth.i9.palm.persistence.SourceDAO;
import de.rwth.i9.palm.persistence.SourcePropertyDAO;
import de.rwth.i9.palm.persistence.SubjectDAO;
import de.rwth.i9.palm.persistence.UserDAO;
import de.rwth.i9.palm.persistence.UserRequestDAO;
import de.rwth.i9.palm.persistence.WidgetDAO;

/**
 * {@inheritDoc}
 *
 */
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
	private AuthorDAO authorDAO;

	@Autowired( required = false )
	private AuthorInterestDAO authorInterestDAO;

	@Autowired( required = false )
	private AuthorInterestProfileDAO authorInterestProfileDAO;

	@Autowired( required = false )
	private AuthorSourceDAO authorSourceDAO;

	@Autowired( required = false )
	private CircleAuthorDAO circleAuthorDAO;

	@Autowired( required = false )
	private CirclePublicationDAO circlePublicationDAO;

	@Autowired( required = false )
	private CountryDAO countryDAO;

	@Autowired( required = false )
	private EventDAO eventDAO;

	@Autowired( required = false )
	private EventGroupDAO eventGroupDAO;

	@Autowired( required = false )
	private ExtractionServiceDAO extractionServiceDAO;

	@Autowired( required = false )
	private ExtractionServicePropertyDAO extractionServicePropertyDAO;

	@Autowired( required = false )
	private FunctionDAO functionDAO;

	@Autowired( required = false )
	private InstitutionDAO institutionDAO;

	@Autowired( required = false )
	private InterestDAO interestDAO;

	@Autowired( required = false )
	private InterestProfileDAO interestProfileDAO;

	@Autowired( required = false )
	private InterestProfilePropertyDAO interestProfilePropertyDAO;

	@Autowired( required = false )
	private LocationDAO locationDAO;

	@Autowired( required = false )
	private PalmConfigurationDAO palmConfigurationDAO;

	@Autowired( required = false )
	private PublicationDAO publicationDAO;

	@Autowired( required = false )
	private PublicationFileDAO publicationFileDAO;

	@Autowired( required = false )
	private PublicationHistoryDAO publicationHistoryDAO;

	@Autowired( required = false )
	private PublicationSourceDAO publicationSourceDAO;

	@Autowired( required = false )
	private PublicationTopicDAO publicationTopicDAO;

	@Autowired( required = false )
	private RoleDAO roleDAO;

	@Autowired( required = false )
	private SessionDataSetDAO sessionDataSetDAO;
	
	@Autowired( required = false )
	private SourceDAO sourceDAO;

	@Autowired( required = false )
	private SourcePropertyDAO sourcePropertyDAO;

	@Autowired( required = false )
	private SubjectDAO subjectDAO;

	@Autowired( required = false )
	private UserDAO userDAO;

	@Autowired( required = false )
	private UserRequestDAO userRequestDAO;

	@Autowired( required = false )
	private WidgetDAO widgetDAO;

	@Override
	public AuthorDAO getAuthorDAO()
	{
		if ( this.authorDAO == null )
			this.authorDAO = new AuthorDAOHibernate( this.sessionFactory );

		return this.authorDAO;
	}

	@Override
	public AuthorInterestDAO getAuthorInterestDAO()
	{
		if ( this.authorInterestDAO == null )
			this.authorInterestDAO = new AuthorInterestDAOHibernate( this.sessionFactory );

		return this.authorInterestDAO;
	}

	@Override
	public AuthorInterestProfileDAO getAuthorInterestProfileDAO()
	{
		if ( this.authorInterestProfileDAO == null )
			this.authorInterestProfileDAO = new AuthorInterestProfileDAOHibernate( this.sessionFactory );

		return this.authorInterestProfileDAO;
	}

	@Override
	public AuthorSourceDAO getAuthorSourceDAO()
	{
		if ( this.authorSourceDAO == null )
			this.authorSourceDAO = new AuthorSourceDAOHibernate( this.sessionFactory );

		return this.authorSourceDAO;
	}

	@Override
	public CircleAuthorDAO getCircleAuthorDAO()
	{
		if ( this.circleAuthorDAO == null )
			this.circleAuthorDAO = new CircleAuthorDAOHibernate( this.sessionFactory );

		return this.circleAuthorDAO;
	}

	@Override
	public CirclePublicationDAO getCirclePublicationDAO()
	{
		if ( this.circlePublicationDAO == null )
			this.circlePublicationDAO = new CirclePublicationDAOHibernate( this.sessionFactory );

		return this.circlePublicationDAO;
	}

	@Override
	public CountryDAO getCountryDAO()
	{
		if ( this.countryDAO == null )
			this.countryDAO = new CountryDAOHibernate( this.sessionFactory );

		return this.countryDAO;
	}

	@Override
	public EventDAO getEventDAO()
	{
		if ( this.eventDAO == null )
			this.eventDAO = new EventDAOHibernate( this.sessionFactory );

		return this.eventDAO;
	}

	@Override
	public EventGroupDAO getEventGroupDAO()
	{
		if ( this.eventGroupDAO == null )
			this.eventGroupDAO = new EventGroupDAOHibernate( this.sessionFactory );

		return this.eventGroupDAO;
	}

	@Override
	public ExtractionServiceDAO getExtractionServiceDAO()
	{
		if ( this.extractionServiceDAO == null )
			this.extractionServiceDAO = new ExtractionServiceDAOHibernate( this.sessionFactory );

		return this.extractionServiceDAO;
	}

	@Override
	public ExtractionServicePropertyDAO getExtractionServicePropertyDAO()
	{
		if ( this.extractionServicePropertyDAO == null )
			this.extractionServicePropertyDAO = new ExtractionServicePropertyDAOHibernate( this.sessionFactory );

		return this.extractionServicePropertyDAO;
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
	public InterestDAO getInterestDAO()
	{
		if ( this.interestDAO == null )
			this.interestDAO = new InterestDAOHibernate( this.sessionFactory );

		return this.interestDAO;
	}

	@Override
	public InterestProfileDAO getInterestProfileDAO()
	{
		if ( this.interestProfileDAO == null )
			this.interestProfileDAO = new InterestProfileDAOHibernate( this.sessionFactory );

		return this.interestProfileDAO;
	}

	@Override
	public InterestProfilePropertyDAO getInterestProfilePropertyDAO()
	{
		if ( this.interestProfilePropertyDAO == null )
			this.interestProfilePropertyDAO = new InterestProfilePropertyDAOHibernate( this.sessionFactory );

		return this.interestProfilePropertyDAO;
	}

	@Override
	public LocationDAO getLocationDAO()
	{
		if ( this.locationDAO == null )
			this.locationDAO = new LocationDAOHibernate( this.sessionFactory );

		return this.locationDAO;
	}

	@Override
	public PalmConfigurationDAO getPalmConfigurationDAO()
	{
		if ( this.palmConfigurationDAO == null )
			this.palmConfigurationDAO = new PalmConfigurationDAOHibernate( this.sessionFactory );

		return this.palmConfigurationDAO;
	}

	@Override
	public PublicationDAO getPublicationDAO()
	{
		if ( this.publicationDAO == null )
			this.publicationDAO = new PublicationDAOHibernate( this.sessionFactory );

		return this.publicationDAO;
	}

	@Override
	public PublicationFileDAO getPublicationFileDAO()
	{
		if ( this.publicationFileDAO == null )
			this.publicationFileDAO = new PublicationFileDAOHibernate( this.sessionFactory );

		return this.publicationFileDAO;
	}

	@Override
	public PublicationSourceDAO getPublicationSourceDAO()
	{
		if ( this.publicationSourceDAO == null )
			this.publicationSourceDAO = new PublicationSourceDAOHibernate( this.sessionFactory );

		return this.publicationSourceDAO;
	}

	@Override
	public PublicationHistoryDAO getPublicationHistoryDAO()
	{
		if ( this.publicationHistoryDAO == null )
			this.publicationHistoryDAO = new PublicationHistoryDAOHibernate( this.sessionFactory );

		return this.publicationHistoryDAO;
	}

	@Override
	public PublicationTopicDAO getPublicationTopicDAO()
	{
		if ( this.publicationTopicDAO == null )
			this.publicationTopicDAO = new PublicationTopicDAOHibernate( this.sessionFactory );

		return this.publicationTopicDAO;
	}

	@Override
	public RoleDAO getRoleDAO()
	{
		if ( this.roleDAO == null )
			this.roleDAO = new RoleDAOHibernate( this.sessionFactory );

		return this.roleDAO;
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
	public SourcePropertyDAO getSourcePropertyDAO()
	{
		if ( this.sourcePropertyDAO == null )
			this.sourcePropertyDAO = new SourcePropertyDAOHibernate( this.sessionFactory );

		return this.sourcePropertyDAO;
	}

	@Override
	public SubjectDAO getSubjectDAO()
	{
		if ( this.subjectDAO == null )
			this.subjectDAO = new SubjectDAOHibernate( this.sessionFactory );

		return this.subjectDAO;
	}

	@Override
	public UserDAO getUserDAO()
	{
		if ( this.userDAO == null )
			this.userDAO = new UserDAOHibernate( this.sessionFactory );

		return this.userDAO;
	}

	@Override
	public UserRequestDAO getUserRequestDAO()
	{
		if ( this.userRequestDAO == null )
			this.userRequestDAO = new UserRequestDAOHibernate( this.sessionFactory );

		return this.userRequestDAO;
	}

	@Override
	public WidgetDAO getWidgetDAO()
	{
		if ( this.widgetDAO == null )
			this.widgetDAO = new WidgetDAOHibernate( this.sessionFactory );

		return this.widgetDAO;
	}

}
