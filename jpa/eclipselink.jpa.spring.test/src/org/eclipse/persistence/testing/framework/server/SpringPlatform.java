/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.framework.server;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Spring container platform.
 */
public class SpringPlatform implements ServerPlatform {
    TransactionStatus status;
    String persistenceUnit;
    Map<String, ClassPathXmlApplicationContext> contexts = new HashMap<String, ClassPathXmlApplicationContext>();

    /**
     * This is a hack to enable weaving in Spring tests.
     * The Spring agent does not load persistence units in premain
     * So it must be forced to do so before any domain classes are loaded,
     * otherwise weaving will not work.
     * TODO: Spring needs to fix this or something.
     */
    public void initialize() {
        getContext("default");
        getContext("fieldaccess");
    }
    
    /**
     * Return if the Spring transaction is active.
     */
    public boolean isTransactionActive() {
        return (this.status != null);
    }

    /**
     * Return if the Spring transaction is roll back only.
     */
    public boolean getRollbackOnly() {
        return (this.status != null) && (this.status.isRollbackOnly());
    }

    /**
     * Start a new Spring transaction.
     */
    public void beginTransaction() {
        if (this.status != null) {
            rollbackTransaction();
            throw new Error("Attempt to begin transaction while transaction still active.");
        }
        this.status = getTransactionManager().getTransaction(new DefaultTransactionDefinition());
    }

    /**
     * Commit the existing Spring transaction.
     */
    public void commitTransaction() {
        try {
            getTransactionManager().commit(this.status);
        } finally {
            this.status = null;
        }
    }

    /**
     * Roll back the existing Spring transaction.
     */
    public void rollbackTransaction() {
        try {
            getTransactionManager().rollback(this.status);
        } finally {
            this.status = null;
        }
    }

    public PlatformTransactionManager getTransactionManager() {
        return (JpaTransactionManager)getContext().getBean("transactionManager");
    }
   
    /**
     * Mark the existing Spring transaction for rollback.
     */
    public void setTransactionForRollback() {
        if (this.status != null) {
            this.status.setRollbackOnly();
        }
    }

    /**
     * Is the platform Oracle?
     */
    public boolean isOc4j() {
        return false;
    }

    /**
     * Is the platform Weblogic?
     */
    public boolean isWeblogic() {
        return false;
    }

    /**
     * Is the platform JBoss?
     */
    public boolean isJBoss() {
        return false;
    }

    /**
     * Is the platform clustered?
     */
    public boolean isClustered() {
        return false;
    }

    /**
     * Is the platform Spring?
     */
    public boolean isSpring() {
        return true;
    }

    /**
     * Managed entity managers do not need to be closed.
     */
    public void closeEntityManager(EntityManager entityManager) {
    }
    
    /**
     * Return the managed EntityManager for the persistence unit.
     */
    public EntityManager getEntityManager(String persistenceUnit) {
        this.persistenceUnit = persistenceUnit;
        return (EntityManager)getContext().getBean("entityManager");
        //SpringBean bean = (SpringBean)getContext().getBean("testBean");
        //return bean.getEntityManager();
    }
    
    /**
     * Return the managed EntityManagerFactory for the persistence unit.
     */
    public EntityManagerFactory getEntityManagerFactory(String persistenceUnit) {
        this.persistenceUnit = persistenceUnit;
        return (EntityManagerFactory)getContext().getBean("entityManagerFactory");
    }
    
    /**
     * Return the managed EntityManagerFactory for the persistence unit.
     */
    public ClassPathXmlApplicationContext getContext() {
        return getContext(this.persistenceUnit);
    }
    
    /**
     * Return the managed EntityManagerFactory for the persistence unit.
     */
    public ClassPathXmlApplicationContext getContext(String persistenceUnit) {
        ClassPathXmlApplicationContext context = this.contexts.get(persistenceUnit);
        if (context == null) {
            context = new ClassPathXmlApplicationContext(persistenceUnit + "-spring.xml");
            this.contexts.put(persistenceUnit, context);            
        }
        return context;
    }
    
}
