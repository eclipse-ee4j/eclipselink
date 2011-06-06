/* $Id: //dev/eclipselink/jpa/eclipselink.jpa.wdf.test/src/org/eclipse/persistence/testing/tests/wdf/jpa1/generator/TestIdentity.java#2 $
 * Last changelist: $Change: 38610 $
 * Last changed at: $DateTime: 2009/09/23 09:51:44 $
 * Last changed by: $Author: d024108 $
 */
package org.eclipse.persistence.testing.tests.wdf.jpa1.generator;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;

import junit.framework.Assert;

import org.eclipse.persistence.platform.database.OraclePlatform;
import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.Skip;
import org.eclipse.persistence.testing.models.wdf.jpa1.myst.Cave;
import org.eclipse.persistence.testing.models.wdf.jpa1.myst.Creature;
import org.eclipse.persistence.testing.models.wdf.jpa1.myst.MythicalCreature;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestIdentity extends JPA1Base {

    @Test
    @Skip(databases = OraclePlatform.class, databaseNames="org.eclipse.persistence.platform.database.MaxDBPlatform")
    public void testPersist() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            em.getTransaction().begin();
            Creature fluppi1 = new Creature("fluppi1");
            fluppi1.setColor("red");
            Creature fluppi2 = new Creature("fluppi2");
            fluppi2.setColor("blue");
            Set<Creature> creatureSet = new HashSet<Creature>();
            creatureSet.add(fluppi1);
            creatureSet.add(fluppi2);
            em.persist(fluppi1);
            em.persist(fluppi2);
            
            em.flush(); // added as suggested by Andrei
            
            Integer id1 = fluppi1.getId();
            Integer id2 = fluppi2.getId();
            Assert.assertNotNull("id1 null", id1);
            Assert.assertNotNull("id2 null", id2);

            Integer caveId = id1 + 7;
            Cave darkHole = new Cave(caveId);
            darkHole.setName("dark hole");
            darkHole.setCreatures(creatureSet);
            em.persist(darkHole);

            em.getTransaction().commit();

            Creature fluppi3 = em.find(Creature.class, id1);
            Assert.assertEquals("wrong object name", fluppi1.getName(), fluppi3.getName());
            Assert.assertEquals("wrong object color", fluppi1.getColor(), fluppi3.getColor());

            fluppi3 = em.find(Creature.class, id2);
            Assert.assertEquals("wrong object name", fluppi2.getName(), fluppi3.getName());
            Assert.assertEquals("wrong object color", fluppi2.getColor(), fluppi3.getColor());

            Cave referenceCave = em.find(Cave.class, caveId);
            Set<Creature> referenceSet = referenceCave.getCreatures();
            Assert.assertEquals("wrong set size", referenceSet.size(), 2);
            Assert.assertEquals("missing element", referenceSet.contains(fluppi1), true);
            Assert.assertEquals("missing element", referenceSet.contains(fluppi2), true);

        } finally {
            if (env.isTransactionActive(em)) {
            env.rollbackTransactionAndClear(em);
            }
        }
    }

    @Test
    @Skip(databases = OraclePlatform.class, databaseNames="org.eclipse.persistence.platform.database.MaxDBPlatform")
    public void testMerge() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            em.getTransaction().begin();
            Creature schnappi1 = new Creature("schnappi");
            schnappi1.setColor("green");
            schnappi1 = em.merge(schnappi1);

            em.flush(); // added as suggested by Andrei
            
            Integer id = schnappi1.getId();
            Assert.assertNotNull("id null", id);
            em.getTransaction().commit();
            
            Creature schnappi2 = em.find(Creature.class, id);
            Assert.assertEquals("wrong object name", schnappi1.getName(), schnappi2.getName());
            Assert.assertEquals("wrong object color", schnappi1.getColor(), schnappi2.getColor());
            em.merge(schnappi2);
            Assert.assertEquals("id change", schnappi1.getId(), schnappi2.getId());
        } finally {
            if (env.isTransactionActive(em)) {
                env.rollbackTransactionAndClear(em);
            }
        }
    }

    // @Test
    // public void testForeignKey() throws SQLException {
    // //TODO
    // if (true) {
    // return;
    // }
    // final EntityManager em = emf.createEntityManager();
    // try {
    // execute(dataSource, addFK);
    //            
    // em.getTransaction().begin();
    // Creature creature = new Creature("Creature");
    // Weapon weapon = new Weapon(47);
    // creature.setWeapon(weapon);
    // em.persist(creature);
    // em.persist(weapon);
    // em.getTransaction().commit();
    // //SQLException -> PersistenceException->IllegalArgumentException
    // } finally {
    // if (em.getTransaction().isActive()) {
    // em.getTransaction().rollback();
    // }
    // em.close();
    // execute(dataSource, dropFK);
    // }
    // }

    @Test
    @Skip(databases = OraclePlatform.class, databaseNames="org.eclipse.persistence.platform.database.MaxDBPlatform")
    public void testJoinedSubclass() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            em.getTransaction().begin();
            MythicalCreature unicorn = new MythicalCreature("unicorn");
            unicorn.setStory("once upon a time");
            unicorn.setColor("white");
            em.persist(unicorn);
            
            em.flush(); // added as suggested by Andrei
            
            Integer id = unicorn.getId();
            em.getTransaction().commit();
            MythicalCreature duocorn = em.find(MythicalCreature.class, id);
            Assert.assertEquals("wrong object name", unicorn.getName(), duocorn.getName());
            Assert.assertEquals("wrong object color", unicorn.getColor(), duocorn.getColor());
            Assert.assertEquals("wrong object story", unicorn.getStory(), duocorn.getStory());

        } finally {
            if (env.isTransactionActive(em)) {
                env.rollbackTransactionAndClear(em);
            }
        }
    }

    // @Test
    // public void testUnsupported() {
    // boolean caught = false;
    // try {
    // final Map<String, Object> map = new HashMap<String, Object>();
    // // map.put(Constants.JDBC_DATASOURCE, dataSource); FIXME
    // emf = Persistence.createEntityManagerFactory("ems/ATSTestEM_Identity", map);
    // } catch (PersistenceException pe) {
    // caught = true;
    // } finally {
    // if (emf != null) {
    // emf.close();
    // emf = null;
    // }
    // Assert.assertTrue("missing PersistenceException", caught);
    // }
    // }
    //    
    @Test
    @Skip(databases = OraclePlatform.class, databaseNames="org.eclipse.persistence.platform.database.MaxDBPlatform")
    public void testPersistNoTx() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            Creature fluppi1 = new Creature("fluppi1");
            fluppi1.setColor("red");
                em.persist(fluppi1);
                env.beginTransaction(em);
                env.commitTransaction(em);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

}
