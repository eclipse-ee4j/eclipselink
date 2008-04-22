package org.eclipse.persistence.testing.tests.jpa.spring.dao;


/**
 * This class applies the basic Spring tests to a configuration that uses:
 * - Spring's LocalContainerEntityManagerFactoryBean
 * - a dao and jpaTemplate
 * - a JPATransactionManager
 */
public class TestContainerEMFwithDao extends SpringAbstractJpaTestsCase {
    
    @Override
    protected String[] getConfigLocations() {
        return new String[]{"classpath:appContext_ContainerEMFwithDao.xml"};
    }

    @Override
    public void setDao(SpringDao dao) {
        TestContainerEMFwithDao.dao = dao; 
    }
    
    //By default, true
    //Disable when instrumentation is not required
    protected boolean shouldUseShadowLoader() {
        return false;
    }

}
