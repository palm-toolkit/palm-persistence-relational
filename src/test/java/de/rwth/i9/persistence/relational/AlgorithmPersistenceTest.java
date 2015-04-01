package de.rwth.i9.persistence.relational;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import de.rwth.i9.palm.config.DatabaseConfigTest;
import de.rwth.i9.palm.model.Algorithm;
import de.rwth.i9.palm.persistence.AlgorithmDAO;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = DatabaseConfigTest.class, loader = AnnotationConfigContextLoader.class )
@Transactional
public class AlgorithmPersistenceTest
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	private AlgorithmDAO algorithmDAO;

	@Before
	public void init()
	{
		algorithmDAO = persistenceStrategy.getAlgorithmDAO();
		assertNotNull( algorithmDAO );
	}

	@Test
	public void test()
	{
		List<Algorithm> alg = persistenceStrategy.getAlgorithmDAO().getAll();
		assertNotNull( alg );
	}

}
