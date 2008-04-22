package org.eclipse.persistence.testing.tests.jpa.spring;

import javax.persistence.EntityManagerFactory;

import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * This class applies the basic Spring tests to a configuration that uses: 
 * - Spring framework's LocalContainerEntityManagerFactoryBean
 * - direct instantiation of the EntityManagerFactory through bean
 * - a JPATransactionManager
 */
public class TestContainerEMF extends SpringJunitTestCase {
    
    private ClassPathXmlApplicationContext context;
    private EntityManagerFactory emf;
    private EntityManagerWrapper em;

    public void setUp() {
        context = new ClassPathXmlApplicationContext("appContext_ContainerEMF.xml");
        emf = (EntityManagerFactory)context.getBean("entityManagerFactory");
        em  = new EntityManagerTransactionWrapper(emf);
        super.setEntityManager(em);    
    }
    
    public void tearDown(){
        context.close();
    }
    
    public void testDataExceptionTranslation(){
        //Not tested in this class; direction instantiation of emf does not support exception translation
    }
}
