/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - initial contribution for Bug 366748 - JPA 2.1 Injectable Entity Listeners
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa21.sessionbean;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.spi.PersistenceProvider;

import org.eclipse.persistence.testing.models.jpa21.entitylistener.EntityListener;
import org.eclipse.persistence.testing.models.jpa21.entitylistener.EntityListenerHolder;

@Stateless
public class EntityListenerTestBean implements EntityListenerTest {

    @PersistenceUnit(name="jpa21-sessionbean")
    private EntityManagerFactory emf;

    
    public boolean triggerInjection(){
        EntityListener.INJECTED_RETURN_VALUE = false;
        EntityListener.POST_CONSTRUCT_CALLS = 0;
        EntityManager em = emf.createEntityManager();
        EntityListenerHolder holder = new EntityListenerHolder();
        em.persist(holder);
        return EntityListener.INJECTED_RETURN_VALUE && EntityListener.POST_CONSTRUCT_CALLS == 1;
    }
    
    public boolean triggerPreDestroy(){
        EntityListener.PRE_DESTROY_CALLS = 0;
        emf.close();
        return EntityListener.PRE_DESTROY_CALLS == 1;
    }
}
