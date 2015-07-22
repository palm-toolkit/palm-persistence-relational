package de.rwth.i9.persistence.relational;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import de.rwth.i9.palm.config.DatabaseConfigTest;
import de.rwth.i9.palm.persistence.ExtractionServiceDAO;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = DatabaseConfigTest.class, loader = AnnotationConfigContextLoader.class )
@Transactional
public class AlgorithmPersistenceTest
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	private ExtractionServiceDAO extractionServiceDAO;

	@Before
	public void init()
	{
		// extractionServiceDAO = persistenceStrategy.getAlgorithmDAO();
		// assertNotNull( extractionServiceDAO );
	}

	@Test
	public void test()
	{
		// List<ExtractionService> alg =
		// persistenceStrategy.getAlgorithmDAO().getAll();
		// assertNotNull( alg );
	}

}
