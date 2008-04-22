package org.eclipse.persistence.testing.tests.jpa.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * This class applies the basic Spring tests to a configuration that uses: 
 * - Spring framework's LocalEntityManagerFactoryBean
 * - injection of EntityManager; use of @Transactional annotation
 * - a JPATransactionManager
 */
public class TestLocalEMFtransactional extends SpringJunitTestCase {

    private ClassPathXmlApplicationContext context;
    private EntityManagerWrapper em;
    
    public void setUp() {
        context = new ClassPathXmlApplicationContext("appContext_LocalEMFtransactional.xml");
        em = (EntityManagerWrapper) context.getBean("transactionalEM");
        super.setEntityManager(em);
    }
    
    public void tearDown(){
        context.close();
    }

}
