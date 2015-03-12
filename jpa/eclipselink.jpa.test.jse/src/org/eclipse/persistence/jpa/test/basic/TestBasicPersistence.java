/*******************************************************************************
 * Copyright (c) 2014, 2015 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     11/04/2014 - Rick Curtis  
 *       - 450010 : Add java se test bucket
 *     12/19/2014 - Dalia Abo Sheasha
 *       - 454917 : Added a test to use the IDENTITY strategy to generate values
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.basic;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.jpa.test.basic.model.Dog;
import org.eclipse.persistence.jpa.test.basic.model.Employee;
import org.eclipse.persistence.jpa.test.basic.model.Person;
import org.eclipse.persistence.jpa.test.basic.model.XmlFish;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.framework.SQLListener;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestBasicPersistence {
    @Emf(createTables = DDLGen.DROP_CREATE, classes = { Dog.class, XmlFish.class, Person.class, Employee.class }, properties = {
        @Property(name = "eclipselink.cache.shared.default", value = "false") }, mappingFiles = { "META-INF/fish-orm.xml" })
    private EntityManagerFactory emf;

    @SQLListener
    List<String> _sql;

    @Test
    public void activeTransaction() {
        Assert.assertNotNull(emf);
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
    }

    @Test
    public void testNonNullEmf() {
        Assert.assertNotNull(emf);
    }

    @Test
    public void persistTest() {
        if (emf == null)
            return;
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Person p = new Person();
            Dog d = new Dog("Bingo");
            p.setDog(d);
            d.setOwner(p);

            em.persist(p);
            em.persist(d);

            em.persist(new XmlFish());
            em.getTransaction().commit();
            em.clear();

            Dog foundDog = em.find(Dog.class, d.getId());
            foundDog.getOwner();
            Assert.assertTrue(_sql.size() > 0);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
    
    @Test
    public void identityStrategyTest() {
    	if (emf == null)
            return;
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Employee e = new Employee();
            em.persist(e);
            em.getTransaction().commit();
            em.clear();
            
            Employee foundEmp = em.find(Employee.class, e.getId());
            Assert.assertNotNull(foundEmp);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
}
