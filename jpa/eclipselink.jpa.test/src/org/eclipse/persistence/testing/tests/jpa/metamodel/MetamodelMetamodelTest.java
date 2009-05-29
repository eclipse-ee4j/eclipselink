/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.metamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.QueryBuilder;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.jpa.metamodel.EntityTypeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.MappedSuperclassTypeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.MetamodelImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.metamodel.ArrayProcessor;
import org.eclipse.persistence.testing.models.jpa.metamodel.Board;
import org.eclipse.persistence.testing.models.jpa.metamodel.Computer;
import org.eclipse.persistence.testing.models.jpa.metamodel.HardwareDesigner;
import org.eclipse.persistence.testing.models.jpa.metamodel.Location;
import org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer;
import org.eclipse.persistence.testing.models.jpa.metamodel.Memory;
import org.eclipse.persistence.testing.models.jpa.metamodel.Person;
import org.eclipse.persistence.testing.models.jpa.metamodel.Processor;
import org.eclipse.persistence.testing.models.jpa.metamodel.SoftwareDesigner;
import org.eclipse.persistence.testing.models.jpa.metamodel.User;
import org.eclipse.persistence.testing.models.jpa.metamodel.VectorProcessor;

/**
 * These tests verify the JPA 2.0 Metamodel API.
 * The framework is as follows:
 *   - initialize persistence unit
 *   - start a transaction
 *   - persist some entities to test
 *   - verify metamodel
 *   - delete test entities created above (to reset the database)
 *   - close persistence unit
 *
 */
public class MetamodelMetamodelTest extends MetamodelTest {

    public MetamodelMetamodelTest() {
        super();
    }
    
    public MetamodelMetamodelTest(String name) {
        super(name);
    }
    
    public void setUp() {
        super.setUp();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite("MetamodelMetamodelTest");

        suite.addTest(new MetamodelMetamodelTest("testImplementation"));
        return suite;
    }

    public void testImplementation() {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        List<Computer> computersList = new ArrayList();
        List<Memory> memories = new ArrayList();
        List<VectorProcessor> processors = new ArrayList();
        List<HardwareDesigner> hardwareDesigners = new ArrayList();
        Computer computer1 = null;
        Computer computer2 = null;
        Manufacturer manufacturer = null;
        User user = null;
        HardwareDesigner hardwareDesigner1 = null;
        SoftwareDesigner softwareDesigner1 = null;
        VectorProcessor vectorProcessor1 = null;
        //ArrayProcessor arrayProcessor1 = null;
        Board board1 = null;
        Memory memory1 = null;
        Memory memory2 = null;
        Location location1 = null;
        Location location2 = null;        
        
        boolean exceptionThrown = false;
        Metamodel metamodel = null;

        try {
            emf = initialize();
            //emf = initialize();
            em = emf.createEntityManager();
            em.getTransaction().begin();

            // setup entity relationships
            computer1 = new Computer();
            computer2 = new Computer();
            memory1 = new Memory();
            memory2 = new Memory();
            manufacturer = new Manufacturer();
            user = new User();
            hardwareDesigner1 = new HardwareDesigner();
            softwareDesigner1 = new SoftwareDesigner();
            vectorProcessor1 = new VectorProcessor();
            //arrayProcessor1 = new ArrayProcessor();
            board1 = new Board();
            location1 = new Location();
            location2 = new Location();        

            // setup collections
            computersList.add(computer1);
            computersList.add(computer2);
            processors.add(vectorProcessor1);
            //processors.add(arrayProcessor1);
            memories.add(memory1);
            memories.add(memory2);
            hardwareDesigners.add(hardwareDesigner1);

            // set owning and inverse sides of 1:m and m:1 relationships
            manufacturer.setComputers(computersList);
            manufacturer.setHardwareDesigners(hardwareDesigners);
            hardwareDesigner1.setEmployer(manufacturer);
            computer1.setManufacturer(manufacturer);
            computer2.setManufacturer(manufacturer);
            board1.setMemories(memories);
            memory1.setBoard(board1);
            memory2.setBoard(board1);
            board1.setProcessors(processors);
            //arrayProcessor1.setBoard(board1);
            vectorProcessor1.setBoard(board1);            
            
            // set 1:1 relationships
            computer1.setLocation(location1);
            computer2.setLocation(location2);
            
            // set attributes
            computer1.setName("CDC-6600");
            computer2.setName("CM-5");
            
            // persist all entities to the database in a single transaction
            em.persist(computer1);
            em.persist(computer2);
            em.persist(manufacturer);
            em.persist(user);
            em.persist(hardwareDesigner1);
            em.persist(softwareDesigner1);
            em.persist(vectorProcessor1);
            //em.persist(arrayProcessor1);
            em.persist(board1);
            em.persist(memory1);
            em.persist(memory2);
            em.persist(location1);
            em.persist(location2);        
            
            em.getTransaction().commit();            
            
            // get Metamodel representation of the entity schema
            metamodel = em.getMetamodel();
            assertNotNull(metamodel);
            
            // Verify EntityType access to entities in the metamodel
            // These enties are metamodel entities (1 per type) not JPA entity instances (IdentifiableType)
            // TODO: temporarily used the impl classes - so F3 resolves in the IDE - revert to the interface for production
            EntityTypeImpl<Computer> entityComputer = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer);
            //System.out.println("_Entity: " + entityComputer + " @" + entityComputer.hashCode());
            EntityTypeImpl<Manufacturer> entityManufacturer = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer);
            //System.out.println("_Entity: " + entityManufacturer + " @" + entityManufacturer.hashCode());
            EntityTypeImpl<User> entityUser = (EntityTypeImpl)metamodel.entity(User.class);
            assertNotNull(entityUser);
            //System.out.println("_Entity: " + entityUser + " @" + entityUser.hashCode());
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner);
            //System.out.println("_Entity: " + entityHardwareDesigner + " @" + entityHardwareDesigner.hashCode());
            EntityTypeImpl<SoftwareDesigner> entitySoftwareDesigner = (EntityTypeImpl)metamodel.entity(SoftwareDesigner.class);
            assertNotNull(entitySoftwareDesigner);
            //System.out.println("_Entity: " + entitySoftwareDesigner + " @" + entitySoftwareDesigner.hashCode());
            EntityTypeImpl<Board> entityBoard = (EntityTypeImpl)metamodel.entity(Board.class);
            assertNotNull(entityBoard);
            //System.out.println("_Entity: " + entityBoard + " @" + entityBoard.hashCode());
            EntityTypeImpl<Memory> entityMemory = (EntityTypeImpl)metamodel.entity(Memory.class);
            assertNotNull(entityMemory);
            //System.out.println("_Entity: " + entityMemory + " @" + entityMemory.hashCode());
            EntityTypeImpl<Location> entityLocation =(EntityTypeImpl) metamodel.entity(Location.class);
            assertNotNull(entityLocation);
            //System.out.println("_Entity: " + entityLocation + " @" + entityLocation.hashCode());
            
            
            
            /**
             * TODO: all test code below requires assert*() calls - and is in mid implementation
             */
            
            
            
            // get some static (non-runtime) attributes parameterized by <Owning type, return Type>
            // Note: the String based attribute names are non type-safe
            /*
            aMember CollectionAttributeImpl<X,E>  (id=183)  
             elementType BasicImpl<X>  (id=188)  
                javaClass   Class<T> (org.eclipse.persistence.testing.models.jpa.metamodel.Computer) (id=126)   
             managedType EntityTypeImpl<X>  (id=151) 
                descriptor  RelationalDescriptor  (id=156)  
                javaClass   Class<T> (org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer) (id=129)   
                members HashMap<K,V>  (id=157)  
                metamodel   MetamodelImpl  (id=52)  
                supertype   null    
            mapping OneToManyMapping  (id=191)  
            */
            // The attributes are in the field ManagedTypeImpl.members
            // The managedType is the owner of the attribute
            //hardwareDesigners=CollectionAttribute[org.eclipse.persistence.mappings.OneToManyMapping[hardwareDesigners]], 
            //computers=CollectionAttribute[org.eclipse.persistence.mappings.OneToManyMapping[computers]], 
            javax.persistence.metamodel.CollectionAttribute<? super Manufacturer, Computer> computersAttribute = 
                entityManufacturer.getCollection("computers", Computer.class);
            //javax.persistence.metamodel.Set<Manufacturer, Computer> computersAttribute2 = 
            //    entityManufacturer.getSet("computers", Computer.class);
            System.out.println("_Manufacturer.computers: " + computersAttribute);
            
            //version=Attribute[org.eclipse.persistence.mappings.DirectToFieldMapping[version-->CMP3_MM_MANUF.MANUF_VERSION]], 
            //name=Attribute[org.eclipse.persistence.mappings.DirectToFieldMapping[name-->CMP3_MM_MANUF.NAME]], 
            //id=Attribute[org.eclipse.persistence.mappings.DirectToFieldMapping[id-->CMP3_MM_MANUF.PERSON_ID]]
            
            
            // exercise EntityTypeImpl
            //System.out.println("_entityManufacturer.getBindableType(): " + entityManufacturer.getBindableType());
            //System.out.println("_entityManufacturer.getCollections(): " + entityManufacturer.getCollections());
            //System.out.println("_entityManufacturer.getDeclaredCollection(type): " + entityManufacturer.getDeclaredCollection("computers", Computer.class));
            //System.out.println("_entityManufacturer.getDeclaredAttribute(type): " + entityManufacturer.getDeclaredSingularAttribute("name", String.class));            
            //System.out.println("_entityManufacturer.getDeclaredAttribute(): " + entityManufacturer.getDeclaredAttribute("name"));
            //System.out.println("_entityManufacturer.getDeclaredAttributes(): " + entityManufacturer.getDeclaredAttributes());
            //System.out.println("_entityManufacturer.getDeclaredId(type): " + entityManufacturer.getDeclaredId(manufacturer.getId().getClass()));
            //System.out.println("_entityManufacturer.getIdType(): " + entityManufacturer.getIdType());
            //System.out.println("_entityManufacturer.getJavaType(): " + entityManufacturer.getJavaType());
            //System.out.println("_entityManufacturer.getName(): " + entityManufacturer.getName());
            //System.out.println("_entityManufacturer.getSupertype(): " + entityManufacturer.getSupertype());
            //entityManufacturer.getVersion(manufacturer.getVersion());


            // Variant use cases
            try {
                //System.out.println("_entityManufacturer.getDeclaredCollection(type): " + entityManufacturer.getDeclaredCollection("name", String.class));
                entityManufacturer.getDeclaredCollection("name", String.class);
            } catch (Exception e) {
                exceptionThrown = true;
                //e.printStackTrace();
            }
            //assertTrue(exceptionThrown);
            exceptionThrown = false;
            
            //System.out.println("_entityManufacturer.getDeclaredCollection(): " + entityManufacturer.getDeclaredCollection("name"));            
            // Inspect Metamodel object
            //System.out.println("_Metamodel: " + metamodel);
            //System.out.println("_Metamodel entities: " + metamodel.getEntities());
            //System.out.println("_Metamodel embeddables: " + metamodel.getEmbeddables());
            //System.out.println("_Metamodel managedTypes: " + metamodel.getManagedTypes());
            
            // get Mapped Superclass objects
            //Map<Class, MappedSuperclassTypeImpl<?>> mappedSuperclasses = ((MetamodelImpl)metamodel).getMappedSuperclasses();        
            Set<MappedSuperclassTypeImpl<?>> mappedSuperclasses = ((MetamodelImpl)metamodel).getMappedSuperclasses();
            int count = 0;
            for(Iterator<MappedSuperclassTypeImpl<?>> msIterator = mappedSuperclasses.iterator(); msIterator.hasNext();) {
                MappedSuperclassTypeImpl msType = msIterator.next();
                //System.out.println("_Metamodel mappedSuperclassType:" + msType);
                RelationalDescriptor descriptor = msType.getDescriptor();
                for(Iterator<DatabaseMapping> mappingIterator = descriptor.getMappings().iterator(); mappingIterator.hasNext();) {
                    DatabaseMapping mapping = mappingIterator.next();
                    //System.out.println("__Mapping: " + mapping);
                    count++;
                    //assertEquals();
                }            
            }
            
            // we should have had a non-zero number of mappings on the descriptors
            if(count < 1) {
                fail("No mappedSuperclass mappings were found");
            }
            assertTrue(count > 0);
            
            
            
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assertFalse(exceptionThrown);
            //finalizeForTest(em, entityMap);
            try {
/*                    //em = emf.createEntityManager();
                    em.getTransaction().begin();

                    em.remove(computer1);
                    em.remove(computer2);
                    em.remove(manufacturer);
                    em.remove(user);
                    em.remove(hardwareDesigner1);
                    em.remove(softwareDesigner1);
                    em.remove(vectorProcessor1);
                    //em.remove(arrayProcessor1);
                    em.remove(board1);
                    em.remove(memory1);
                    em.remove(memory2);
                    em.remove(location1);
                    em.remove(location2);        
                    
                    em.getTransaction().commit();
*/            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(null != em) {
                    cleanup(em);
                }
            }
        }
    }
    
    
    /*
     * The following functions from Ch 5 of the 17 Mar 2009 JSR-317 JPA 2.0 API PFD are tested here.
     * 
    public interface Metamodel {
        public MetamodelImpl(DatabaseSession session) {
        public MetamodelImpl(EntityManagerFactory emf) {
        public MetamodelImpl(EntityManager em) {
        public DatabaseSession getSession() {
        public <X> Embeddable<X> embeddable(Class<X> clazz) {
        public <X> Entity<X> entity(Class<X> clazz) {
        public <X> ManagedType<X> type(Class<X> clazz) {
        public Set<Embeddable<?>> getEmbeddables() {
        public Set<Entity<?>> getEntities() {
        public Set<ManagedType<?>> getManagedTypes() {
        public TypeImpl<?> getType(Class javaClass) {
        public java.util.Set<MappedSuperclassTypeImpl<?>> getMappedSuperclasses() {
        public void setMappedSuperclasses(
*/
}
