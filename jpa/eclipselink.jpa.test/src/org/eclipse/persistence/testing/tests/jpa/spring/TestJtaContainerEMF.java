package org.eclipse.persistence.testing.tests.jpa.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * This class applies the basic Spring tests to a configuration that uses: 
 * - Spring framework's LocalContainerEntityManagerFactoryBean
 * - injection of EntityManager; use of @Transactional annotation
 * - a JTATransactionManager through a JotmFactoryBean
 */
public class TestJtaContainerEMF extends SpringJunitTestCase {

    private ClassPathXmlApplicationContext context;
    private EntityManagerWrapper em;
    
    public void setUp() {
        context = new ClassPathXmlApplicationContext("appContext_JtaContainerEMF.xml");
        em = (EntityManagerWrapper) context.getBean("transactionalEM");
        super.setEntityManager(em);
    }
    
    public void tearDown() {
        context.close();
    }

}
