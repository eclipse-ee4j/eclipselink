/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     05/18/2022
 *       - Bug 579409: Add support for accurate IDENTITY generation when the database contains separate TRIGGER objects
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.sequence;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.sequence.model.Coffee;
import org.eclipse.persistence.jpa.test.sequence.model.Tea;
import org.eclipse.persistence.jpa.test.sequence.model.TeaShop;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestIdentityGeneration {
    @Emf(name = "baseEMF", createTables = DDLGen.DROP_CREATE, classes = { Coffee.class, Tea.class, TeaShop.class }, 
            properties = { 
                    @Property(name="eclipselink.logging.level", value="FINE")})
    private EntityManagerFactory emf;

    @Emf(name = "propertyEMF", createTables = DDLGen.DROP_CREATE, classes = { Coffee.class, Tea.class, TeaShop.class, StepExecutionEntity.class }, 
            properties = { 
                    @Property(name="eclipselink.target-database-properties", value="supportsReturnGeneratedKeys=true"),
                    @Property(name="eclipselink.logging.level", value="FINE")})
    private EntityManagerFactory emf2;

    @Test
    public void testPersistWithSecondaryTables() {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        try {
            Set<Tea> teas = new HashSet<Tea>();

            Tea tea1 = new Tea("Earl Grey", 3.29, "Lipton", 1.29);
            teas.add(tea1);
            Tea tea2 = new Tea("Oolong", 2.34, "Lipton", 0.75);
            teas.add(tea2);

            em.getTransaction().begin();
            for(Tea tea : teas)
                em.persist(tea);
            em.getTransaction().commit();

            for(Tea tea : teas)
                Assert.assertNotNull(tea.getName() + " 'id' is null", tea.getId());

            Long id = tea2.getId() + 11;
            TeaShop teaShop = new TeaShop(id, "MyShop");
            teaShop.setTeas(teas);

            em.getTransaction().begin();
            em.persist(teaShop);
            em.getTransaction().commit();

            Tea findTea = em.find(Tea.class, tea1.getId());
            Assert.assertEquals("wrong object name", tea1.getName(), findTea.getName());
            Assert.assertEquals("wrong object color", tea1.getManufacturer(), findTea.getManufacturer());

            findTea = em.find(Tea.class, tea2.getId());
            Assert.assertEquals("wrong object name", tea2.getName(), findTea.getName());
            Assert.assertEquals("wrong object color", tea2.getManufacturer(), findTea.getManufacturer());

            TeaShop referenceCave = em.find(TeaShop.class, id);
            Set<Tea> referenceSet = referenceCave.getTeas();
            Assert.assertEquals("wrong set size", teas.size(), referenceSet.size());
            Assert.assertTrue("missing element", referenceSet.contains(tea1));
            Assert.assertTrue("missing element", referenceSet.contains(tea2));
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Test to cover updating foreign key field after insert.
     * 
     * https://github.com/eclipse-ee4j/eclipselink/issues/1711
     */
    @Test
    public void testPersistWithSecondaryTables2() {
        if (emf2 == null)
            return;

        EntityManager em = emf2.createEntityManager();
        DatabasePlatform platform = getPlatform(emf2);

        // Only these platforms support {@link java.sql.Statement#getGeneratedKeys()}
        Assume.assumeTrue(platform.isDB2() || platform.isDerby() || platform.isMySQL() || platform.isSQLServer());

        try {
            StepExecutionEntity e1 = new StepExecutionEntity();
            e1.setChildEntity(e1);

            em.getTransaction().begin();
            em.persist(e1);
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testRefreshWithTriggers() {
        if (emf == null)
            return;

        DatabasePlatform platform = getPlatform(emf);
        Assume.assumeTrue(platform.supportsReturnGeneratedKeys());

        createNonJPAObjects(emf);

        EntityManager em = emf.createEntityManager();

        try {
            Coffee coffee = new Coffee("Capa", 4.2);

            // persist and commit to initiate an INSERT into COFFEE and trigger a COFFEE_AUDIT INSERT
            em.getTransaction().begin();
            em.persist(coffee);
            em.getTransaction().commit();

            // refreshing the entity will trigger EclipseLink to attempt a SELECT with the ID they have
            // If it isn't correct, it will fail
            em.getTransaction().begin();
            em.refresh(coffee);
            em.getTransaction().commit();

            em.getTransaction().begin();
            List<Object[]> result = em.createNativeQuery("SELECT AUDIT_ID, COFFEE_ID FROM COFFEE_AUDIT WHERE NAME = 'Capa' AND PRICE = 4.2").getResultList();
            em.getTransaction().commit();

            Assert.assertNotNull(result);
            Assert.assertEquals(1, result.size());
            Assert.assertEquals(2, result.get(0).length);

            if(platform.isSQLServer()) {
                Assert.assertEquals(new BigDecimal(4), result.get(0)[0]);
                Assert.assertEquals(new BigDecimal(1), result.get(0)[1]);
            } else {
                Assert.assertEquals(4l, result.get(0)[0]);
                Assert.assertEquals(1l, result.get(0)[1]);
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    private void createNonJPAObjects(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        DatabasePlatform platform = getPlatform(emf);
        try {
            // Drop the audit table if it exists
            try {
                em.getTransaction().begin();
                em.createNativeQuery("DROP TABLE COFFEE_AUDIT").executeUpdate();
                em.getTransaction().commit();
            } catch (Exception e) {
                em.getTransaction().rollback();
            } 

            // Create an audit table outside of JPA
            em.getTransaction().begin();
            if(platform.isMySQL()) {
                em.createNativeQuery("CREATE TABLE COFFEE_AUDIT (AUDIT_ID BIGINT AUTO_INCREMENT NOT NULL, COFFEE_ID BIGINT NOT NULL, NAME VARCHAR(255), PRICE DOUBLE, PRIMARY KEY (AUDIT_ID))").executeUpdate();
            } else if(platform.isSQLServer()) {
                em.createNativeQuery("CREATE TABLE COFFEE_AUDIT (AUDIT_ID NUMERIC(19) IDENTITY NOT NULL, COFFEE_ID NUMERIC(19) NOT NULL, NAME VARCHAR(255) NULL, PRICE FLOAT(32) NULL, PRIMARY KEY (AUDIT_ID))").executeUpdate();
            } else if (platform.isDB2() || platform.isDerby()) {
                em.createNativeQuery("CREATE TABLE COFFEE_AUDIT (AUDIT_ID BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, COFFEE_ID BIGINT NOT NULL, NAME VARCHAR(255), PRICE FLOAT, PRIMARY KEY (AUDIT_ID))").executeUpdate();
            } else {
                Assume.assumeTrue("The database [" + platform + "] supports ReturnGeneratedKeys [" + platform.supportsReturnGeneratedKeys() + "], but this test does not support this database. Add support.", false);
            }
            em.getTransaction().commit();

            // Drop the audit table trigger if it exists
            try {
                em.getTransaction().begin();
                em.createNativeQuery("DROP TRIGGER coffeetrigger").executeUpdate();
                em.getTransaction().commit();
            } catch (Exception e) {
                em.getTransaction().rollback();
            }

            // Create an audit table trigger outside JPA
            em.getTransaction().begin();
            if(platform.isMySQL()) {
                em.createNativeQuery("CREATE TRIGGER coffeetrigger AFTER INSERT ON COFFEE FOR EACH ROW INSERT INTO COFFEE_AUDIT( COFFEE_ID, NAME, PRICE ) VALUES (NEW.ID, NEW.NAME, NEW.PRICE)").executeUpdate();
            } else if(platform.isSQLServer()) {
                em.createNativeQuery("CREATE TRIGGER coffeetrigger on COFFEE AFTER INSERT as BEGIN SET NOCOUNT ON; INSERT INTO COFFEE_AUDIT( COFFEE_ID, NAME, PRICE ) SELECT i.ID, i.NAME, i.PRICE FROM inserted i END").executeUpdate();
            } else if (platform.isDB2()) {
                em.createNativeQuery("CREATE TRIGGER coffeetrigger AFTER INSERT ON COFFEE REFERENCING NEW_TABLE AS NEW_COFFEE FOR EACH STATEMENT MODE DB2SQL BEGIN ATOMIC INSERT INTO COFFEE_AUDIT( COFFEE_ID, NAME, PRICE ) SELECT i.ID, i.NAME, i.PRICE FROM NEW_COFFEE i; END").executeUpdate();
            } else if (platform.isDerby()) {
                em.createNativeQuery("CREATE TRIGGER coffeetrigger AFTER INSERT ON COFFEE REFERENCING NEW_TABLE AS NEW_COFFEE FOR EACH STATEMENT MODE DB2SQL INSERT INTO COFFEE_AUDIT( COFFEE_ID, NAME, PRICE ) SELECT i.ID, i.NAME, i.PRICE FROM NEW_COFFEE i").executeUpdate();
            } else {
                Assume.assumeTrue("The database [" + platform + "] supports ReturnGeneratedKeys [" + platform.supportsReturnGeneratedKeys() + "], but this test does not support this database. Add support.", false);
            }
            em.getTransaction().commit();

            // Add values to the audit table so that the audit table identity generated will be out of sync with the entity table
            em.getTransaction().begin();
            em.createNativeQuery("INSERT INTO COFFEE_AUDIT (COFFEE_ID, NAME, PRICE) VALUES (1, 'COFFEE1', 11.1)").executeUpdate();
            em.createNativeQuery("INSERT INTO COFFEE_AUDIT (COFFEE_ID, NAME, PRICE) VALUES (2, 'COFFEE2', 22.2)").executeUpdate();
            em.createNativeQuery("INSERT INTO COFFEE_AUDIT (COFFEE_ID, NAME, PRICE) VALUES (3, 'COFFEE3', 33.3)").executeUpdate();
            em.getTransaction().commit();
        } finally {
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    private DatabasePlatform getPlatform(EntityManagerFactory emf) {
        return ((EntityManagerFactoryImpl)emf).getServerSession().getPlatform();
    }
}
