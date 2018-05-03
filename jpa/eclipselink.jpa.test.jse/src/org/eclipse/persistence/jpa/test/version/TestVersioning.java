/*******************************************************************************
 * Copyright (c) 2018 IBM and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     05/10/2018-2.7 Joe Grassel
 *       - Github#93: Bug with bulk update processing involving version field update parameter
 ******************************************************************************/

package org.eclipse.persistence.jpa.test.version;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.version.model.TemporalVersionedEntity;
import org.eclipse.persistence.jpa.test.version.model.TemporalVersionedEntity2;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestVersioning {
	@Emf(createTables = DDLGen.DROP_CREATE, classes = { TemporalVersionedEntity.class, TemporalVersionedEntity2.class },
			properties = { @Property(name="eclipselink.logging.level", value="FINE"),
					       @Property(name="eclipselink.logging.parameters", value="true")})
    private EntityManagerFactory emf;
	
	private final static String qStr1 = "UPDATE TemporalVersionedEntity " + 
			"SET updatetimestamp = ?3 " +
			"WHERE id = ?1 AND updatetimestamp = ?2";
	
	private final static String qStr2 = "UPDATE TemporalVersionedEntity2 " + 
			"SET version = ?3 " +
			"WHERE id = ?1 AND version = ?2";
			
	@Test
    public void testTemporalVersionField1() throws Exception {
//		System.out.println("*\n*\n*\n*\nRunning testTemporalVersionField1 ...");
        if (emf == null)
            return;
        
        EntityManager em = emf.createEntityManager();
        try {
        	final TemporalVersionedEntity tve = new TemporalVersionedEntity();
        	tve.setId(1L);
        	
        	em.getTransaction().begin();
        	em.persist(tve);
        	em.getTransaction().commit();
        	em.clear();
//        	System.out.println("New Entity Timestamp = " + tve.getUpdatetimestamp());
        	
        	final TemporalVersionedEntity tve_update1 = performJpqlBulkUpdate(em, tve);
        	em.clear();
//        	System.out.println("Timestamp After First Bulk Update = " + tve_update1.getUpdatetimestamp());
        	
        	final TemporalVersionedEntity tve_update2 = performJpqlBulkUpdate(em, tve_update1);
        	em.clear();
//        	System.out.println("Timestamp After Second Bulk Update = " + tve_update2.getUpdatetimestamp());
        	
        	Assert.assertNotEquals(tve.getUpdatetimestamp(), tve_update1.getUpdatetimestamp());
        	Assert.assertNotEquals(tve.getUpdatetimestamp(), tve_update2.getUpdatetimestamp());
        	Assert.assertNotEquals(tve_update1.getUpdatetimestamp(), tve_update2.getUpdatetimestamp());
        	
        } finally {
        	if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
	}
	
	private TemporalVersionedEntity performJpqlBulkUpdate(EntityManager em, TemporalVersionedEntity entity) {
		try { Thread.sleep(500); } catch (Exception e) {}
		em.getTransaction().begin();
		
		final Query q = em.createQuery(qStr1);
		q.setParameter(1, entity.getId());
		q.setParameter(2, entity.getUpdatetimestamp());
		
		try { Thread.sleep(500); } catch (Exception e) {}
		final java.sql.Timestamp newTime = new java.sql.Timestamp(System.currentTimeMillis());
		Assert.assertNotNull(newTime);
		q.setParameter(3, newTime);
//		System.out.println("Bulk Update Parameter 3 = " + newTime);
		
		try { Thread.sleep(500); } catch (Exception e) {}
		final int count = q.executeUpdate();
		Assert.assertEquals(1, count);
		
		em.getTransaction().commit();
		em.clear();
		
		final TemporalVersionedEntity tve = em.find(TemporalVersionedEntity.class, entity.getId());
		Assert.assertNotNull(tve);
		Assert.assertNotSame(tve,  entity);
		
		Assert.assertNotEquals(entity.getUpdatetimestamp(), tve.getUpdatetimestamp());
		Assert.assertEquals(newTime, tve.getUpdatetimestamp());
		
		
		return tve;
	}
	
	@Test
    public void testTemporalVersionField2() throws Exception {
//		System.out.println("*\n*\n*\n*\nRunning testTemporalVersionField2 ...");
        if (emf == null)
            return;
        
        EntityManager em = emf.createEntityManager();
        try {
        	final TemporalVersionedEntity2 tve = new TemporalVersionedEntity2();
        	tve.setId(1L);
        	
        	em.getTransaction().begin();
        	em.persist(tve);
        	em.getTransaction().commit();
        	em.clear();
//        	System.out.println("New Entity Timestamp = " + tve.getVersion());
        	
        	final TemporalVersionedEntity2 tve_update1 = performJpqlBulkUpdate2(em, tve);
        	em.clear();
//        	System.out.println("Timestamp After First Bulk Update = " + tve_update1.getVersion());
        	
        	final TemporalVersionedEntity2 tve_update2 = performJpqlBulkUpdate2(em, tve_update1);
        	em.clear();
//        	System.out.println("Timestamp After Second Bulk Update = " + tve_update2.getVersion());
        	
        	Assert.assertNotEquals(tve.getVersion(), tve_update1.getVersion());
        	Assert.assertNotEquals(tve.getVersion(), tve_update2.getVersion());
        	Assert.assertNotEquals(tve_update1.getVersion(), tve_update2.getVersion());     	
        } finally {
        	if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
	}
	
	private TemporalVersionedEntity2 performJpqlBulkUpdate2(EntityManager em, TemporalVersionedEntity2 entity) {
		try { Thread.sleep(500); } catch (Exception e) {}
		em.getTransaction().begin();
		
		final Query q = em.createQuery(qStr2);
		q.setParameter(1, entity.getId());
		q.setParameter(2, entity.getVersion());
		
		try { Thread.sleep(500); } catch (Exception e) {}
		final java.sql.Timestamp newTime = new java.sql.Timestamp(System.currentTimeMillis());
		Assert.assertNotNull(newTime);
		q.setParameter(3, newTime);
//		System.out.println("Bulk Update Parameter 3 = " + newTime);
		
		try { Thread.sleep(500); } catch (Exception e) {}
		final int count = q.executeUpdate();
		Assert.assertEquals(1, count);
		
		em.getTransaction().commit();
		em.clear();
		
		final TemporalVersionedEntity2 tve = em.find(TemporalVersionedEntity2.class, entity.getId());
		Assert.assertNotNull(tve);
		Assert.assertNotSame(tve,  entity);
		
		Assert.assertNotEquals(entity.getVersion(), tve.getVersion());
		Assert.assertEquals(newTime, tve.getVersion());
		
		
		return tve;
	}
}
