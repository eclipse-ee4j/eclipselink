package org.eclipse.persistence.testing.tests.jpa.spring.dao;


/**
 * This class applies the basic Spring tests to a configuration that uses:
 * - Spring's LocalEntityManagerFactoryBean
 * - a dao and jpaTemplate
 * - a JPATransactionManager
 */
public class TestLocalEMFwithDao extends SpringAbstractJpaTestsCase {
    
    @Override
    protected String[] getConfigLocations() {
        return new String[]{"classpath:appContext_LocalEMFwithDao.xml"};
    }

    @Override
    public void setDao(SpringDao dao) {
        TestLocalEMFwithDao.dao = dao;
    }
    
    //By default, true
    //Disable when instrumentation is not required
    protected boolean shouldUseShadowLoader() {
        return false;
    }

}
