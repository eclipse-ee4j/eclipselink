package org.eclipse.persistence.testing.tests.jpa.metamodel;

import java.lang.reflect.Method;

import org.eclipse.persistence.internal.jpa.EntityManagerFactoryDelegate;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider;
import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;
import org.eclipse.persistence.jpa.JpaEntityManager;

import junit.framework.Test;
import junit.framework.TestSuite;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class EntityManagerSetupImplTest extends MetamodelTest {

    public EntityManagerSetupImplTest() {
        super();
    }

    public EntityManagerSetupImplTest(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("EntityManagerSetupImplTest");
        if(!isJPA10() && !isOnServer()) {
            suite.addTest(new EntityManagerSetupImplTest("testPessimisticLockTimeout"));
        }
        return suite;
    }


    public void testPessimisticLockTimeout() {
    	 EntityManagerFactory emf = getEntityManagerFactory();
         EntityManager em = emf.createEntityManager();
         try{
             EntityManagerFactoryDelegate delegate = (EntityManagerFactoryDelegate)em.unwrap(JpaEntityManager.class).getEntityManagerFactory();
             EntityManagerSetupImpl setupImpl = EntityManagerFactoryProvider.emSetupImpls.get(delegate.getSetupImpl().getSessionName());
             
             Method retrieveMethod = EntityManagerSetupImpl.class.getDeclaredMethod("convertTimeMillisToSeconds", String.class);
             retrieveMethod.setAccessible(true);
             int returnValue = (Integer)retrieveMethod.invoke(retrieveMethod, "10000");
             assertEquals(returnValue,100);
         }catch(Exception e){
        	 e.printStackTrace();
         }
         finally {
             if (isTransactionActive(em)) {
                 rollbackTransaction(em);
             }
             em.close();
         }
     
    }

}

