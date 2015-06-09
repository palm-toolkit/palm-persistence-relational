package de.rwth.i9.persistence.relational;

import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import de.rwth.i9.palm.config.DatabaseConfigTest;
import de.rwth.i9.palm.model.Conference;
import de.rwth.i9.palm.persistence.ConferenceDAO;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = DatabaseConfigTest.class, loader = AnnotationConfigContextLoader.class )
@Transactional
public class ConferenceDAOHibernateTest
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	private ConferenceDAO conferenceDAO;

	@Before
	public void init()
	{
		conferenceDAO = persistenceStrategy.getConferenceDAO();
		assertNotNull( conferenceDAO );
	}

	@Test
	public void test()
	{
		Map<String, Conference> notationConferenceMaps = persistenceStrategy.getConferenceDAO().getNotationConferenceMaps();

		for ( Map.Entry<String, Conference> entry : notationConferenceMaps.entrySet() )
		{
			System.out.println( entry.getKey() + "/" + entry.getValue().getYear() );
		}
	}

}
