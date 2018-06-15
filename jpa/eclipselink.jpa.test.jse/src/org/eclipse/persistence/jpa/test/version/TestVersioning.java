/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2018 IBM and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     05/10/2018-master Joe Grassel
//       - Github#93: Bug with bulk update processing involving version field update parameter

package org.eclipse.persistence.jpa.test.version;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.OptimisticLockException;
import javax.persistence.Query;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.version.model.IntegerVersionedEntity;
import org.eclipse.persistence.jpa.test.version.model.TemporalVersionedEntity;
import org.eclipse.persistence.jpa.test.version.model.TemporalVersionedEntity2;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestVersioning {
	@Emf(createTables = DDLGen.DROP_CREATE, classes = { TemporalVersionedEntity.class, TemporalVersionedEntity2.class,
			IntegerVersionedEntity.class},
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
		try { Thread.sleep(1500); } catch (Exception e) {}
		em.getTransaction().begin();
		
		final Query q = em.createQuery(qStr1);
		q.setParameter(1, entity.getId());
		q.setParameter(2, entity.getUpdatetimestamp());
		
		try { Thread.sleep(500); } catch (Exception e) {}
		final java.sql.Timestamp newTime = new java.sql.Timestamp(System.currentTimeMillis());
		newTime.setNanos(0);  // Some DB platforms don't do milli/nano granularity with timestamp.
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
		try { Thread.sleep(1500); } catch (Exception e) {}
		em.getTransaction().begin();
		
		final Query q = em.createQuery(qStr2);
		q.setParameter(1, entity.getId());
		q.setParameter(2, entity.getVersion());
		
		try { Thread.sleep(500); } catch (Exception e) {}
		final java.sql.Timestamp newTime = new java.sql.Timestamp(System.currentTimeMillis());
		newTime.setNanos(0);  // Some DB platforms don't do milli/nano granularity with timestamp.
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
	
	/**
	 * Verify basic Integer version incrementation.  
	 * 
	 */
	@Test
	public void testIntegerVersioning001() throws Exception {
		if (emf == null)
            return;
        
        EntityManager em = emf.createEntityManager();
        try {
        	final IntegerVersionedEntity newEntity = new IntegerVersionedEntity();
        	newEntity.setId(1L);
        	newEntity.setData("Data");
        	
        	em.getTransaction().begin();
        	em.persist(newEntity);
        	em.getTransaction().commit();
        	em.clear();
        	
        	em.getTransaction().begin();
        	final IntegerVersionedEntity findEntity1 = em.find(IntegerVersionedEntity.class, 1L);
        	Assert.assertNotNull(findEntity1);
        	Assert.assertNotSame(newEntity, findEntity1);
        	Assert.assertEquals("Data", findEntity1.getData());
        	Assert.assertEquals(newEntity.getTheVersion(), findEntity1.getTheVersion());
        	
        	findEntity1.setData("Lore");
        	
        	em.getTransaction().commit();
        	em.clear();
        	
        	em.getTransaction().begin();
        	final IntegerVersionedEntity findEntity2 = em.find(IntegerVersionedEntity.class, 1L);
        	Assert.assertNotNull(findEntity2);
        	Assert.assertNotSame(findEntity1, findEntity2);
        	Assert.assertEquals("Lore", findEntity2.getData());
        	Assert.assertEquals(newEntity.getTheVersion() + 1, findEntity1.getTheVersion());
        	
        	findEntity1.setData("Information");
        	
        	em.getTransaction().commit();
        	em.clear();
        	
        } finally {
        	if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
	}
	
	/**
	 * Test based on org.eclipse.persistence.testing.tests.jpa.jpql.JUnitJPQLValidationTestSuite.JTAOptimisticLockExceptionTest()
	 * 
	 */
	@Test
	public void testIntegerVersioning002() throws Throwable {
		if (emf == null)
            return;
        
        EntityManager em = emf.createEntityManager();
        try {
        	final IntegerVersionedEntity newEntity = new IntegerVersionedEntity();
        	newEntity.setId(2L);
        	newEntity.setData("Data");
        	
        	em.getTransaction().begin();
        	em.persist(newEntity);
        	em.getTransaction().commit();
        	em.clear();
        	
        	em.getTransaction().begin();
        	final IntegerVersionedEntity findEntity1 = (IntegerVersionedEntity) em
        			.createQuery("SELECT fe FROM IntegerVersionedEntity fe WHERE fe.id = 2")
        			.getSingleResult();
        	Assert.assertNotNull(findEntity1);
        	Assert.assertNotSame(newEntity, findEntity1);
        	Assert.assertEquals("Data", findEntity1.getData());
        	Assert.assertEquals(newEntity.getTheVersion(), findEntity1.getTheVersion());
        	
        	try {
            	em.createQuery("UPDATE IntegerVersionedEntity SET data = 'Lore' WHERE id = 2").executeUpdate();
            	
            	findEntity1.setData("Information");
            	
            	em.getTransaction().commit();
            	Assert.fail("Lock exception should have been thrown");
        	} catch (Throwable lockException) {
        		while ((lockException != null) && !(lockException instanceof OptimisticLockException)) {
                    lockException = lockException.getCause();
                }
                if (lockException instanceof OptimisticLockException) {
                    return; // Pass
                }
                
                throw lockException;
        	}       	
        } finally {
        	if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
	}
	
	@Test
	public void testIntegerVersioning003() throws Exception {
		if (emf == null)
            return;
        
        EntityManager em = emf.createEntityManager();
        try {
        	final IntegerVersionedEntity newEntity = new IntegerVersionedEntity();
        	newEntity.setId(3L);
        	newEntity.setData("Data");
        	
        	em.getTransaction().begin();
        	em.persist(newEntity);
        	em.getTransaction().commit();
        	em.clear();
        	
        	em.getTransaction().begin();
        	int updates = em.createQuery("UPDATE IntegerVersionedEntity SET theVersion = 10 WHERE id = 3 AND theVersion = 1").executeUpdate();
        	Assert.assertEquals(1,  updates);
        	
        	em.getTransaction().commit();
        	em.clear();
        	
        	
        } finally {
        	if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
	}
}
