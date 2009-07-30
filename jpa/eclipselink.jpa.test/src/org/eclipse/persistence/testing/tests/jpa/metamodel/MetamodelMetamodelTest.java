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
 *     06/30/2009-2.0  mobrien - finish JPA Metadata API modifications in support
 *       of the Metamodel implementation for EclipseLink 2.0 release involving
 *       Map, ElementCollection and Embeddable types on MappedSuperclass descriptors
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)  
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.metamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.QueryBuilder;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;
import javax.persistence.metamodel.Type.PersistenceType;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.expressions.ClassTypeExpression;
import org.eclipse.persistence.internal.jpa.metamodel.EntityTypeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.MappedSuperclassTypeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.MetamodelImpl;
import org.eclipse.persistence.internal.jpa.metamodel.TypeImpl;
import org.eclipse.persistence.internal.jpa.querydef.ExpressionImpl;
import org.eclipse.persistence.internal.jpa.querydef.SelectionImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.testing.framework.QuerySQLTracker;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.metamodel.Board;
import org.eclipse.persistence.testing.models.jpa.metamodel.CompositePK;
import org.eclipse.persistence.testing.models.jpa.metamodel.Computer;
import org.eclipse.persistence.testing.models.jpa.metamodel.EmbeddedPK;
import org.eclipse.persistence.testing.models.jpa.metamodel.HardwareDesigner;
import org.eclipse.persistence.testing.models.jpa.metamodel.Location;
import org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer;
import org.eclipse.persistence.testing.models.jpa.metamodel.Memory;
import org.eclipse.persistence.testing.models.jpa.metamodel.SoftwareDesigner;
import org.eclipse.persistence.testing.models.jpa.metamodel.User;
import org.eclipse.persistence.testing.models.jpa.metamodel.VectorProcessor;

/**
 * Disclaimer:
 *    Yes I know the following are true for this test suite - but implementation time must be ""triaged"", and this testing code is at the bottom of the list when placed against actual implementation in the time provided.
 *    - Tests must be modular - not one big huge test case that either passes or fails - it is better to have 10's of granular failures instead of only 1
 *    - proper and fully optimized test cases
 *    - full exception handling
 *    - full rollback handling
 *    - better documented assertion failures
 *    - fully described test model with links to design document
 *    - traceability back to use cases
 *    
 * 
 * These tests verify the JPA 2.0 Metamodel API.
 * The framework is as follows:
 *   - initialize persistence unit
 *   - start a transaction
 *   - persist some entities to test
 *   - verify metamodel
 *   - delete test entities created above (to reset the database)
 *   - close persistence unit
 *   
 *   API Usage 
 *   There are three ways to query using the Criteria API which can wrap the Metamodel API
 *   1) Static metamodel class model for type safe queries - these are the _Underscore design time classes
 *   2) Dynamic metamodel class model for type safe queries 
 *      - we use generics and pass in both the return type and the type containing the return type
 *   3) String attribute references for non-type safe queries 
 *      - see p.262 of the JPA 2.0 specification section 6.7 
 *      - there may be type or generic usage compiler warnings that 
 *        the user will need to workaround when using this non-type-safe query in a type-safe environment.
 *   
 *   This enhancement deals only with # 2) Dynamic metamodel query generation. 
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

        //suite.addTest(new MetamodelMetamodelTest("testMetamodelStringBasedQuery"));
        //suite.addTest(new MetamodelMetamodelTest("testMetamodelTypeSafeBasedQuery"));
        suite.addTest(new MetamodelMetamodelTest("testImplementation"));
        return suite;
    }

    /**
     * Test the Metamodel API using a TypeSafe query via the Criteria API (a user of the Metamodel)
     */
    public void testMetamodelTypeSafeBasedQuery() {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        
        Set<Computer> computersList = new HashSet();
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
        Board board1 = null;
        Memory memory1 = null;
        Memory memory2 = null;
        Location location1 = null;
        Location location2 = null;        
        boolean exceptionThrown = false;
        Metamodel metamodel = null;

        try {
            emf = initialize();
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
            board1 = new Board();
            location1 = new Location();
            location2 = new Location();        

            // setup collections
            computersList.add(computer1);
            computersList.add(computer2);
            processors.add(vectorProcessor1);
            memories.add(memory1);
            memories.add(memory2);
            hardwareDesigners.add(hardwareDesigner1);

            // set owning and inverse sides of 1:m and m:1 relationships
            manufacturer.setComputers(computersList);
            manufacturer.setHardwareDesigners(hardwareDesigners);
            hardwareDesigner1.setEmployer(manufacturer);
            hardwareDesigner1.setPrimaryEmployer(manufacturer);
            hardwareDesigner1.setSecondaryEmployer(manufacturer);
            computer1.setManufacturer(manufacturer);
            computer2.setManufacturer(manufacturer);
            board1.setMemories(memories);
            memory1.setBoard(board1);
            memory2.setBoard(board1);
            board1.setProcessors(processors);
            vectorProcessor1.setBoard(board1);            
            softwareDesigner1.setPrimaryEmployer(manufacturer);
            softwareDesigner1.setSecondaryEmployer(manufacturer);
            
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
            em.persist(board1);
            em.persist(memory1);
            em.persist(memory2);
            em.persist(location1);
            em.persist(location2);        
            
            em.getTransaction().commit();            
            
            // get Metamodel representation of the entity schema
            metamodel = em.getMetamodel();
            assertNotNull(metamodel);

            // Setup TypeSafe Criteria API query  
            QueryBuilder aQueryBuilder = em.getQueryBuilder();
            // Setup a query to get a list (the JPA 1.0 way) and compare it to the (JPA 2.0 way)
            // avoid a NPE on .where in query.setExpressionBuilder(((ExpressionImpl)this.where).getCurrentNode().getBuilder());
            //TypedQuery<Computer> aManufacturerQuery = em.createQuery(aQueryBuilder.createQuery(Computer.class));
            Query aManufacturerQuery = em.createQuery(aQueryBuilder.createQuery(Computer.class));
            List<Computer> computersResultsList = aManufacturerQuery.getResultList();
            Computer aComputer = (Computer)computersResultsList.get(0);
/*
            // Get the primary key of the Computer
            CriteriaQuery<Tuple> aCriteriaQuery = aQueryBuilder.createQuery(Tuple.class);
            Root from = cq.from(Employee.class);
            cq.multiselect(from.get("id"), from.get("firstName"));
            cq.where(qb.equal(from.get("id"), qb.parameter(from.get("id").getModel().getBindableJavaType(), "id")).add(qb.equal(from.get("firstName"), qb.parameter(from.get("firstName").getModel().getBindableJavaType(), "firstName"))));
            TypedQuery<Tuple> typedQuery = em.createQuery(cq);

            typedQuery.setParameter("id", employee.getId());
            typedQuery.setParameter("firstName", employee.getFirstName());

            Tuple queryResult = typedQuery.getSingleResult();
            assertTrue("Query Results do not match selection", queryResult.get(0).equals(employee.getId()) && queryResult.get(1).equals(employee.getFirstName()));
*/            
        } catch (Exception e) {
            // we enter here on a failed commit() - for example if the table schema is incorrectly defined
            e.printStackTrace();
            exceptionThrown = true;
        } finally {
            assertFalse(exceptionThrown);
            //finalizeForTest(em, entityMap);
            if(null != em) {
                cleanup(em);
            }
        }
    }
    
    /**
     * Test the Metamodel API or lack of using it via a Criteria API using string based queries
     */
    public void testMetamodelStringBasedQuery() {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        Set<Computer> computersList = new HashSet();
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
            board1 = new Board();
            location1 = new Location();
            location2 = new Location();        

            // setup collections
            computersList.add(computer1);
            computersList.add(computer2);
            processors.add(vectorProcessor1);
            memories.add(memory1);
            memories.add(memory2);
            hardwareDesigners.add(hardwareDesigner1);

            // set owning and inverse sides of 1:m and m:1 relationships
            manufacturer.setComputers(computersList);
            manufacturer.setHardwareDesigners(hardwareDesigners);
            hardwareDesigner1.setEmployer(manufacturer);
            hardwareDesigner1.setPrimaryEmployer(manufacturer);
            hardwareDesigner1.setSecondaryEmployer(manufacturer);
            computer1.setManufacturer(manufacturer);
            computer2.setManufacturer(manufacturer);
            board1.setMemories(memories);
            memory1.setBoard(board1);
            memory2.setBoard(board1);
            board1.setProcessors(processors);
            vectorProcessor1.setBoard(board1);            
            softwareDesigner1.setPrimaryEmployer(manufacturer);
            softwareDesigner1.setSecondaryEmployer(manufacturer);
            
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

            // Setup a non TypeSafe Criteria API query
            
        } catch (Exception e) {
            // we enter here on a failed commit() - for example if the table schema is incorrectly defined
            e.printStackTrace();
            exceptionThrown = true;
        } finally {
            assertFalse(exceptionThrown);
            //finalizeForTest(em, entityMap);
            if(null != em) {
                cleanup(em);
            }
        }
    }
    
    public void testImplementation() {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        Set<Computer> computersList = new HashSet<Computer>();
        Collection<Memory> memories = new HashSet<Memory>();
        Collection<VectorProcessor> processors = new HashSet<VectorProcessor>();
        Collection<HardwareDesigner> hardwareDesigners = new HashSet<HardwareDesigner>();
        Map<String, HardwareDesigner> mappedDesigners = new HashMap<String, HardwareDesigner>();
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
        
        // Embedded objects
        EmbeddedPK embeddedPKforLocation1 = new EmbeddedPK();        
        EmbeddedPK embeddedPKforLocation2 = new EmbeddedPK();
        
        boolean exceptionThrown = false;
        Metamodel metamodel = null;

        try {
            emf = initialize();
            //emf = initialize();
            em = emf.createEntityManager();

            // Pre-Persist: get Metamodel representation of the entity schema
            metamodel = em.getMetamodel();
            assertNotNull(metamodel);

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
            
            mappedDesigners.put(hardwareDesigner1.getName(), hardwareDesigner1);

            // set owning and inverse sides of 1:m and m:1 relationships
            manufacturer.setComputers(computersList);
            manufacturer.setHardwareDesigners(hardwareDesigners);
            hardwareDesigner1.setEmployer(manufacturer);
            hardwareDesigner1.setPrimaryEmployer(manufacturer);
            hardwareDesigner1.setSecondaryEmployer(manufacturer);
            // both sides of the relationship are set
            hardwareDesigner1.setMappedEmployer(manufacturer);
            computer1.setManufacturer(manufacturer);
            computer2.setManufacturer(manufacturer);
            board1.setMemories(memories);
            memory1.setBoard(board1);
            memory2.setBoard(board1);
            board1.setProcessors(processors);
            //arrayProcessor1.setBoard(board1);
            vectorProcessor1.setBoard(board1);            
            
            softwareDesigner1.setPrimaryEmployer(manufacturer);
            softwareDesigner1.setSecondaryEmployer(manufacturer);
            
            
            // set 1:1 relationships
            computer1.setLocation(location1);
            computer2.setLocation(location2);
            
            // set attributes
            computer1.setName("CDC-6600");
            computer2.setName("CM-5");
            
            // setup embedded objects
            location1.setPrimaryKey(embeddedPKforLocation1);
            location2.setPrimaryKey(embeddedPKforLocation2);
                
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
            
            // Post-Persist: get Metamodel representation of the entity schema
            //metamodel = em.getMetamodel();
            //assertNotNull(metamodel);
            
            // Verify EntityType access to entities in the metamodel
            // These entities are metamodel entities (1 per type) not JPA entity instances (IdentifiableType)
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
            
            // Criteria queries (use the Metamodel)
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            QueryBuilder qb = null;
            List results = null;
            try {
                qb = em.getQueryBuilder();
                //CriteriaQuery<String> cq = qb.createQuery(String.class);
                CriteriaQuery<Computer> criteriaQuery = qb.createQuery(Computer.class);
                Expression expression = new ClassTypeExpression(); // probably not the right expression impl
                // somehow add "name" to the expression TBD                
                //criteriaQuery.select((new SelectionImpl(String.class, expression)));
                EntityTypeImpl<Computer> entityComputer2 = (EntityTypeImpl)metamodel.entity(Computer.class);                
                Root from = criteriaQuery.from(entityComputer2);
                Path path = from.get("name");
                criteriaQuery.where(qb.equal(path, "CM-5"));
                Query query = em.createQuery(criteriaQuery);
                results = query.getResultList();
                if(results.size() > 0) {
                    Computer computer = (Computer)results.get(0);
                    assertNotNull(computer);
                } else {
                    fail("Results from criteria query (ReadAllQuery(referenceClass=Computer sql=SELECT COMPUTER_ID, NAME, COMPUTER_VERSION, MANUFACTURER_PERSON_ID, LOCATION_LOCATION_ID FROM CMP3_MM_COMPUTER WHERE NAME = 'CM-5') were expected");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            /* uncomment when the QueryBuilderImpl function below is implemented
             * public <T> ParameterExpression<T> parameter(Class<T> paramClass, String name){
            try {
                qb = em.getQueryBuilder();
                CriteriaQuery<Computer> cq = qb.createQuery(Computer.class);
                Root from = cq.from(Computer.class);
                Path c = from.get("name");
                cq.where(qb.equal(c, qb.parameter(String.class, "emp_name")));
                Query query = em.createQuery(cq);
                results = query.getResultList();
                Computer computer = (Computer)results.get(0);
                assertNotNull(computer);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            
            // SingularAttributeType
            // Test getBindableType - this is for SVN rev# 4644
            //http://fisheye2.atlassian.com/changelog/eclipselink/?cs=4644
            // Basic
            Class nameJavaType = ((SingularAttribute<Computer, String>)entityComputer.getAttribute("name")).getBindableJavaType();
            assertNotNull(nameJavaType);
            assertEquals(String.class, nameJavaType);
            
            // OneToOne Entity
            Class locationJavaType = ((SingularAttribute<Computer, Location>)entityComputer.getAttribute("location")).getBindableJavaType();
            assertNotNull(locationJavaType);
            assertEquals(Location.class, locationJavaType);
            
            
            
            /**
             * TODO: all test code below requires assert*() calls - and is in mid implementation
             */
            boolean expectedIAExceptionThrown = false;
            
            // Verify IdentifiableType operations
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            /**
             *  Return the attribute that corresponds to the id attribute of 
             *  the entity or mapped superclass.
             *  @param type  the type of the represented id attribute
             *  @return id attribute
             *  @throws IllegalArgumentException if id attribute of the given
             *          type is not present in the identifiable type or if
             *          the identifiable type has an id class
             */
            //<Y> SingularAttribute<? super X, Y> getId(Class<Y> type);

            /**
             *  Return the attribute that corresponds to the version 
             *    attribute of the entity or mapped superclass.
             *  @param type  the type of the represented version attribute
             *  @return version attribute
             *  @throws IllegalArgumentException if version attribute of the 
             *          given type is not present in the identifiable type
             */
            //<Y> SingularAttribute<? super X, Y> getVersion(Class<Y> type);

            /**
             *  Return the attribute that corresponds to the id attribute 
             *  declared by the entity or mapped superclass.
             *  @param type  the type of the represented declared id attribute
             *  @return declared id attribute
             *  @throws IllegalArgumentException if id attribute of the given
             *          type is not declared in the identifiable type or if
             *          the identifiable type has an id class
             */
            //<Y> SingularAttribute<X, Y> getDeclaredId(Class<Y> type);

            /**
             *  Return the attribute that corresponds to the version 
             *  attribute declared by the entity or mapped superclass.
             *  @param type  the type of the represented declared version 
             *               attribute
             *  @return declared version attribute
             *  @throws IllegalArgumentException if version attribute of the 
             *          type is not declared in the identifiable type
             */
            //<Y> SingularAttribute<X, Y> getDeclaredVersion(Class<Y> type);
            
            /**
             *  Return the identifiable type that corresponds to the most
             *  specific mapped superclass or entity extended by the entity 
             *  or mapped superclass. 
             *  @return supertype of identifiable type or null if no such supertype
             */
            //IdentifiableType<? super X> getSupertype();
            
            // Test normal path
            expectedIAExceptionThrown = false;
            IdentifiableType<? super Manufacturer> superTypeManufacturer = null;
            try {
                superTypeManufacturer = entityManufacturer.getSupertype();
            } catch (IllegalArgumentException iae) {
                // expecting no exception
                //iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);
            assertNotNull(superTypeManufacturer);
            /**
             * see 
 metamodel    MetamodelImpl  (id=113) 
    embeddables LinkedHashMap<K,V>  (id=251)    
        size    0   
    entities    LinkedHashMap<K,V>  (id=253)    
        size    10  
    managedTypes    LinkedHashMap<K,V>  (id=254)    
        size    14  
    mappedSuperclasses  HashSet<E>  (id=255)    
        map HashMap<K,V>  (id=278)  
            size    4   
    types   LinkedHashMap<K,V>  (id=259)    
        size    17  
              */
            // Check for superclass using non-API code
            //assertEquals(metamodel.type("org.eclipse.persistence.testing.models.jpa.metamodel.Corporation"), superType);
            
            // Test error path (null return)
            expectedIAExceptionThrown = false;
            IdentifiableType<? super Location> superTypeLocation = null;
            try {
                superTypeLocation = entityLocation.getSupertype();
            } catch (IllegalArgumentException iae) {
                // expecting no exception
                //iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);
            assertNull(superTypeLocation);
            

            /**
             *  Whether or not the identifiable type has an id attribute.
             *  Returns true for a simple id or embedded id; returns false
             *  for an idclass.
             *  @return boolean indicating whether or not the identifiable
             *           type has a single id attribute
             */
            //boolean hasSingleIdAttribute();

            /**
             *  Whether or not the identifiable type has a version attribute.
             *  @return boolean indicating whether or not the identifiable
             *           type has a version attribute
             */
            //boolean hasVersionAttribute();

            /**
             *   Return the attributes corresponding to the id class of the
             *   identifiable type.
             *   @return id attributes
             *   @throws IllegalArgumentException if the identifiable type
             *           does not have an id class
             */
             //java.util.Set<SingularAttribute<? super X, ?>> getIdClassAttributes();

            /**
             *  Return the type that represents the type of the id.
             *  @return type of id
             */
            //Type<?> getIdType();            
            // Test normal path for an [Embeddable] type via @EmbeddedId
            expectedIAExceptionThrown = false;
            Type<?> locationIdType = null;
            try {
                locationIdType = entityLocation.getIdType();
            } catch (IllegalArgumentException iae) {
                // expecting no exception
                //iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);
            assertNotNull(locationIdType);
            assertEquals(PersistenceType.EMBEDDABLE, locationIdType.getPersistenceType());
            assertEquals(EmbeddedPK.class, locationIdType.getJavaType());


            // Test normal path for a [Basic] type
            expectedIAExceptionThrown = false;
            Type<?> computerIdType = null;
            try {
                computerIdType = entityComputer.getIdType();
            } catch (IllegalArgumentException iae) {
                // expecting no exception
                //iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);
            assertNotNull(computerIdType);
            assertEquals(PersistenceType.BASIC, computerIdType.getPersistenceType());
            assertEquals(Integer.class, computerIdType.getJavaType());

            
            // Verify all types (entities, embeddables, mappedsuperclasses and basic)
            try {
                // get all 19 types (a non spec function - for testing introspection)
                Map<Class, TypeImpl<?>> typesMap = ((MetamodelImpl)metamodel).getTypes();
                // verify each one
                assertNotNull(typesMap);
                assertEquals(19, typesMap.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
            /*
            {class org.eclipse.persistence.testing.models.jpa.metamodel.SoftwareDesigner=ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.SoftwareDesigner --> [DatabaseTable(CMP3_MM_SWDESIGNER)])], 
                class org.eclipse.persistence.testing.models.jpa.metamodel.EmbeddedPK=ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.EmbeddedPK --> [])], 
                class org.eclipse.persistence.testing.models.jpa.metamodel.User=ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.User --> [DatabaseTable(CMP3_MM_USER)])], 
                class org.eclipse.persistence.testing.models.jpa.metamodel.Location=ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Location --> [DatabaseTable(CMP3_MM_LOCATION)])], 
                class org.eclipse.persistence.testing.models.jpa.metamodel.CompositePK=ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.CompositePK --> [])], 
                class org.eclipse.persistence.testing.models.jpa.metamodel.ArrayProcessor=ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.ArrayProcessor --> [DatabaseTable(CMP3_MM_ARRAYPROC)])], 
                class org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer=ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])], 
                class org.eclipse.persistence.testing.models.jpa.metamodel.Memory=ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Memory --> [DatabaseTable(CMP3_MM_MEMORY)])], 
                class org.eclipse.persistence.testing.models.jpa.metamodel.VectorProcessor=ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.VectorProcessor --> [DatabaseTable(CMP3_MM_PROC)])], 
                class org.eclipse.persistence.testing.models.jpa.metamodel.Board=ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Board --> [DatabaseTable(CMP3_MM_BOARD)])], 
                class org.eclipse.persistence.testing.models.jpa.metamodel.Computer=ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Computer --> [DatabaseTable(CMP3_MM_COMPUTER)])], 
                class org.eclipse.persistence.testing.models.jpa.metamodel.HardwareDesigner=ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.HardwareDesigner --> [DatabaseTable(CMP3_MM_HWDESIGNER)])], 
                class org.eclipse.persistence.testing.models.jpa.metamodel.Corporation=MappedSuperclassTypeImpl@24202381 [descriptor: RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Corporation --> [DatabaseTable(__METAMODEL_RESERVED_IN_MEM_ONLY_TABLE_NAME)]), mappings: [org.eclipse.persistence.mappings.ManyToManyMapping[corporateComputers]]], 
                class org.eclipse.persistence.testing.models.jpa.metamodel.Designer=MappedSuperclassTypeImpl@22595578 [descriptor: RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Designer --> [DatabaseTable(__METAMODEL_RESERVED_IN_MEM_ONLY_TABLE_NAME)]), mappings: [org.eclipse.persistence.mappings.OneToOneMapping[secondaryEmployer], org.eclipse.persistence.mappings.OneToOneMapping[primaryEmployer]]], 
                class org.eclipse.persistence.testing.models.jpa.metamodel.Processor=MappedSuperclassTypeImpl@28381082 [descriptor: RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Processor --> [DatabaseTable(__METAMODEL_RESERVED_IN_MEM_ONLY_TABLE_NAME)]), mappings: []], 
                class org.eclipse.persistence.testing.models.jpa.metamodel.Person=MappedSuperclassTypeImpl@24600030 [descriptor: RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Person --> [DatabaseTable(__METAMODEL_RESERVED_IN_MEM_ONLY_TABLE_NAME)]), mappings: [org.eclipse.persistence.mappings.DirectToFieldMapping[id-->__METAMODEL_RESERVED_IN_MEM_ONLY_TABLE_NAME.PERSON_ID], org.eclipse.persistence.mappings.DirectToFieldMapping[name-->__METAMODEL_RESERVED_IN_MEM_ONLY_TABLE_NAME.NAME]]], 
                class java.lang.Integer=org.eclipse.persistence.internal.jpa.metamodel.BasicTypeImpl@1b7e37, 
                class java.lang.String=org.eclipse.persistence.internal.jpa.metamodel.BasicTypeImpl@fb541d, 
                int=org.eclipse.persistence.internal.jpa.metamodel.BasicTypeImpl@4f5403}
                */
            
            // Verify ManagedType operations
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            /**
             * The following variant test cases are common to all functions
             * 1) get*Attribute(name) = null (missing) --> IAE
             * 2) get*Attribute(name, type) = null (missing, type=irrelevant) --> IAE
             * 3) get*Attribute(name, type) = exists but type != returned type --> IAE
             */
            
            /**
             *  Return the attributes of the managed type.
             */
             //java.util.Set<Attribute<? super X, ?>> getAttributes();
            Set<Attribute<? super Manufacturer, ?>> attributeSet = entityManufacturer.getAttributes();
            assertNotNull(attributeSet);
            // We should see 6 attributes (3 List, 3 Singular) for Manufacturer (computers, hardwareDesigners, id(from the mappedSuperclass), 
            // version, name(from the mappedSuperclass) and corporateComputers from the Corporation mappedSuperclass)
            assertEquals(7, attributeSet.size());
            // for each managed entity we will see 2 entries (one for the Id, one for the Version)
            assertTrue(attributeSet.contains(entityManufacturer.getAttribute("id"))); // 
            assertTrue(attributeSet.contains(entityManufacturer.getAttribute("version"))); //
            assertTrue(attributeSet.contains(entityManufacturer.getAttribute("name"))); //
            assertTrue(attributeSet.contains(entityManufacturer.getAttribute("computers"))); //
            assertTrue(attributeSet.contains(entityManufacturer.getAttribute("hardwareDesigners"))); //
            assertTrue(attributeSet.contains(entityManufacturer.getAttribute("corporateComputers"))); //
            assertTrue(attributeSet.contains(entityManufacturer.getAttribute("hardwareDesignersMap"))); //

            
            /**
             *  Return the attributes declared by the managed type.
             */
             //java.util.Set<Attribute<X, ?>> getDeclaredAttributes();

            /**
             *  Return the single-valued attribute of the managed 
             *  type that corresponds to the specified name and Java type 
             *  in the represented type.
             *  @param name  the name of the represented attribute
             *  @param type  the type of the represented attribute
             *  @return single-valued attribute with given name and type
             *  @throws IllegalArgumentException if attribute of the given
             *          name and type is not present in the managed type
             */
            //<Y> SingularAttribute<? super X, Y> getSingularAttribute(String name, Class<Y> type);

            /**
             *  Return the declared single-valued attribute of the 
             *  managed type that corresponds to the specified name and Java 
             *  type in the represented type.
             *  @param name  the name of the represented attribute
             *  @param type  the type of the represented attribute
             *  @return declared single-valued attribute of the given 
             *          name and type
             *  @throws IllegalArgumentException if attribute of the given
             *          name and type is not declared in the managed type
             */
            //<Y> SingularAttribute<X, Y> getDeclaredSingularAttribute(String name, Class<Y> type);
            
            /**
             *  Return the single-valued attributes of the managed type.
             *  @return single-valued attributes
             */
            //java.util.Set<SingularAttribute<? super X, ?>> getSingularAttributes();
            Set<SingularAttribute<? super Manufacturer, ?>> singularAttributeSet = entityManufacturer.getSingularAttributes();
            assertNotNull(singularAttributeSet);
            // We should see 3 singular attributes for Manufacturer (id(from the mappedSuperclass), version, name(from the mappedSuperclass))
            assertEquals(3, singularAttributeSet.size());
            // for each managed entity we will see 2 entries (one for the Id, one for the Version)
            assertTrue(singularAttributeSet.contains(entityManufacturer.getAttribute("id"))); // 
            assertTrue(singularAttributeSet.contains(entityManufacturer.getAttribute("version"))); //
            assertTrue(singularAttributeSet.contains(entityManufacturer.getAttribute("name"))); //

            /**
             *  Return the single-valued attributes declared by the managed
             *  type.
             *  @return declared single-valued attributes
             */
            //java.util.Set<SingularAttribute<X, ?>> getDeclaredSingularAttributes();
            
            /**
             *  Return the Collection-valued attribute of the managed type 
             *  that corresponds to the specified name and Java element type.
             *  @param name  the name of the represented attribute
             *  @param elementType  the element type of the represented 
             *                      attribute
             *  @return CollectionAttribute of the given name and element
             *          type
             *  @throws IllegalArgumentException if attribute of the given
             *          name and type is not present in the managed type
             */    
            //<E> CollectionAttribute<? super X, E> getCollection(String name, Class<E> elementType);

            /**
             *  Return the Set-valued attribute of the managed type that
             *  corresponds to the specified name and Java element type.
             *  @param name  the name of the represented attribute
             *  @param elementType  the element type of the represented 
             *                      attribute
             *  @return SetAttribute of the given name and element type
             *  @throws IllegalArgumentException if attribute of the given
             *          name and type is not present in the managed type
             */
            //<E> SetAttribute<? super X, E> getSet(String name, Class<E> elementType);

            /**
             *  Return the List-valued attribute of the managed type that
             *  corresponds to the specified name and Java element type.
             *  @param name  the name of the represented attribute
             *  @param elementType  the element type of the represented 
             *                      attribute
             *  @return ListAttribute of the given name and element type
             *  @throws IllegalArgumentException if attribute of the given
             *          name and type is not present in the managed type
             */
            //<E> ListAttribute<? super X, E> getList(String name, Class<E> elementType);

            /**
             *  Return the Map-valued attribute of the managed type that
             *  corresponds to the specified name and Java key and value
             *  types.
             *  @param name  the name of the represented attribute
             *  @param keyType  the key type of the represented attribute
             *  @param valueType  the value type of the represented attribute
             *  @return MapAttribute of the given name and key and value
             *  types
             *  @throws IllegalArgumentException if attribute of the given
             *          name and type is not present in the managed type
             */
            //<K, V> MapAttribute<? super X, K, V> getMap(String name, Class<K> keyType, Class<V> valueType);

            /**
             *  Return the Collection-valued attribute declared by the 
             *  managed type that corresponds to the specified name and Java 
             *  element type.
             *  @param name  the name of the represented attribute
             *  @param elementType  the element type of the represented 
             *                      attribute
             *  @return declared CollectionAttribute of the given name and 
             *          element type
             *  @throws IllegalArgumentException if attribute of the given
             *          name and type is not declared in the managed type
             */
            //<E> CollectionAttribute<X, E> getDeclaredCollection(String name, Class<E> elementType);
            expectedIAExceptionThrown = false;            
            try {
                //<E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);
                // UC1 - the attribute does not exist on the managedType (regardless of whether it is on any superType)
                CollectionAttribute<Manufacturer, Location> anAttribute = 
                    entityManufacturer.getDeclaredCollection("locations", entityLocation.getJavaType());
            } catch (IllegalArgumentException iae) {
                // expecting
                //java.lang.IllegalArgumentException: The attribute [locations] from the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] is not present.
                //iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertTrue(expectedIAExceptionThrown);
            
            expectedIAExceptionThrown = false;            
            try {
                //<E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);
                // UC2 - the attribute is on the managedType (but is the wrong type)
                // Also avoid a CCE on a List attribute
                //java.lang.ClassCastException: org.eclipse.persistence.internal.jpa.metamodel.ListAttributeImpl cannot be cast to javax.persistence.metamodel.CollectionAttribute
                CollectionAttribute<Manufacturer, ?> anAttribute = 
                    entityManufacturer.getDeclaredCollection("hardwareDesigners", entityManufacturer.getJavaType());
            } catch (IllegalArgumentException iae) {
                // expecting
                //java.lang.IllegalArgumentException: Expected attribute return type [COLLECTION] on the existing attribute [hardwareDesigners] on the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] but found attribute return type [LIST].
                //iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertTrue(expectedIAExceptionThrown);            
            
            
            // TODO: We need a Collection (computers is a Set)
/*            expectedIAExceptionThrown = false;            
            try {
                //<E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);
                // UC3 - the attribute is on the managedType (not on any superType)
                CollectionAttribute<Manufacturer, HardwareDesigner> anAttribute = 
                    entityManufacturer.getDeclaredCollection("computers", entityHardwareDesigner.getJavaType());
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);            
*/
            
/*            // TODO: We need a Collection on a superclass (computers is a Set)            
            expectedIAExceptionThrown = false;            
            try {
                //<E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);
                // UC4 - the attribute is on the immediate superclass but it is the wrong return type of LIST instead of COLLECTION
                CollectionAttribute<Manufacturer, Computer> anAttribute = 
                    entityManufacturer.getDeclaredCollection("corporateComputers", entityComputer.getJavaType());
            } catch (IllegalArgumentException iae) {
                // expecting
                // java.lang.IllegalArgumentException: The declared attribute [corporateComputers] from the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] is not present - however, it is declared on a superclass.
                //iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertTrue(expectedIAExceptionThrown);
*/            
            
            expectedIAExceptionThrown = false;            
            try {
                //<E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);
                // UC4 - the attribute is on the immediate superclass but it is the wrong return type of LIST instead of COLLECTION
                CollectionAttribute<Manufacturer, Computer> anAttribute = 
                    entityManufacturer.getDeclaredCollection("corporateComputers", entityComputer.getJavaType());
            } catch (IllegalArgumentException iae) {
                // expecting
                // java.lang.IllegalArgumentException: Expected attribute return type [COLLECTION] on the existing attribute [corporateComputers] on the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] but found attribute return type [LIST].
                //iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertTrue(expectedIAExceptionThrown);
            
            /**
             *  Return the Set-valued attribute declared by the managed type 
             *  that corresponds to the specified name and Java element type.
             *  @param name  the name of the represented attribute
             *  @param elementType  the element type of the represented 
             *                      attribute
             *  @return declared SetAttribute of the given name and 
             *          element type
             *  @throws IllegalArgumentException if attribute of the given
             *          name and type is not declared in the managed type
             */
            //<E> SetAttribute<X, E> getDeclaredSet(String name, Class<E> elementType);

            /**
             *  Return the List-valued attribute declared by the managed 
             *  type that corresponds to the specified name and Java 
             *  element type.
             *  @param name  the name of the represented attribute
             *  @param elementType  the element type of the represented 
             *                      attribute
             *  @return declared ListAttribute of the given name and 
             *          element type
             *  @throws IllegalArgumentException if attribute of the given
             *          name and type is not declared in the managed type
             */
            expectedIAExceptionThrown = false;            
            try {
                //<E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);
                // UC1 - the attribute does not exist on the managedType (regardless of whether it is on any superType)
                ListAttribute<Manufacturer, Location> anAttribute = 
                    entityManufacturer.getDeclaredList("locations", entityLocation.getJavaType());
            } catch (IllegalArgumentException iae) {
                // expecting
                //java.lang.IllegalArgumentException: The attribute [locations] from the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] is not present.
                //iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertTrue(expectedIAExceptionThrown);
            
            expectedIAExceptionThrown = false;            
            try {
                //<E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);
                // UC2 - the attribute is on the managedType (but is the wrong type)
                ListAttribute<Manufacturer, ?> anAttribute = 
                    entityManufacturer.getDeclaredList("hardwareDesigners", entityManufacturer.getJavaType());
            } catch (IllegalArgumentException iae) {
                // expecting
                //java.lang.IllegalArgumentException: Expected attribute type [class org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer] on the existing attribute [hardwareDesigners] on the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] but found attribute type [class org.eclipse.persistence.testing.models.jpa.metamodel.HardwareDesigner].
                //iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertTrue(expectedIAExceptionThrown);            
            
            expectedIAExceptionThrown = false;            
            try {
                //<E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);
                // UC3 - the attribute is on the managedType (not on any superType)
                ListAttribute<Manufacturer, HardwareDesigner> anAttribute = 
                    entityManufacturer.getDeclaredList("hardwareDesigners", entityHardwareDesigner.getJavaType());
                //System.out.println("entityManufacturer.getDeclaredList(hardwareDesigners) " + anAttribute);
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);            
            
            expectedIAExceptionThrown = false;            
            try {
                //<E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);
                // UC4 - the attribute is on the immediate superclass
                ListAttribute<Manufacturer, Computer> anAttribute = 
                    entityManufacturer.getDeclaredList("corporateComputers", entityComputer.getJavaType());
                //System.out.println("entityManufacturer.getDeclaredList(corporateComputers) " + anAttribute);
            } catch (IllegalArgumentException iae) {
                // expecting
                // java.lang.IllegalArgumentException: The declared attribute [corporateComputers] from the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] is not present - however, it is declared on a superclass.
                //iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertTrue(expectedIAExceptionThrown);
            
            /**
             *  Return the Map-valued attribute declared by the managed 
             *  type that corresponds to the specified name and Java key 
             *  and value types.
             *  @param name  the name of the represented attribute
             *  @param keyType  the key type of the represented attribute
             *  @param valueType  the value type of the represented attribute
             *  @return declared MapAttribute of the given name and key 
             *          and value types
             *  @throws IllegalArgumentException if attribute of the given
             *          name and type is not declared in the managed type
             */
            //<K, V> MapAttribute<X, K, V> getDeclaredMap(String name, Class<K> keyType, Class<V> valueType);
            
            /**
             *  Return all collection-valued attributes of the managed type.
             *  @return collection valued attributes
             */
            //java.util.Set<PluralAttribute<? super X, ?, ?>> getCollections();

            /**
             *  Return all collection-valued attributes declared by the 
             *  managed type.
             *  @return declared collection valued attributes
             */
            //java.util.Set<PluralAttribute<X, ?, ?>> getDeclaredCollections();
            // This also tests getCollections()
            // Here we start with 6 attributes in getAttributes() - this is reduced to 3 in getCollections before declared filtering
            // In declaredCollections we reduce this to 2 because one of the types "corporateComputers" is on a mappedSuperclass
            expectedIAExceptionThrown = false;            
            try {
                //<E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);
                // UC3 - the attribute is on the managedType (not on any superType)
                Set<PluralAttribute<Manufacturer, ?, ?>> collections = 
                    entityManufacturer.getDeclaredCollections();
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);            
            

            /**
             *  Return the attribute of the managed
             *  type that corresponds to the specified name.
             *  @param name  the name of the represented attribute
             *  @return attribute with given name
             *  @throws IllegalArgumentException if attribute of the given
             *          name is not present in the managed type
             */
            //Attribute<? super X, ?> getAttribute(String name); 

            /**
             *  Return the declared attribute of the managed
             *  type that corresponds to the specified name.
             *  @param name  the name of the represented attribute
             *  @return attribute with given name
             *  @throws IllegalArgumentException if attribute of the given
             *          name is not declared in the managed type
             */
            //Attribute<X, ?> getDeclaredAttribute(String name);
            expectedIAExceptionThrown = false;            
            try {
                //<E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);
                // UC1 - the attribute does not exist on the managedType (regardless of whether it is on any superType)
                Attribute<Manufacturer, ?> anAttribute = 
                    entityManufacturer.getDeclaredAttribute("locations");//, entityLocation.getJavaType());
            } catch (IllegalArgumentException iae) {
                // expecting
                //java.lang.IllegalArgumentException: The attribute [locations] from the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] is not present.
                //iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertTrue(expectedIAExceptionThrown);
            
            expectedIAExceptionThrown = false;            
            try {
                //<E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);
                // UC3 - the attribute is on the managedType (not on any superType)
                Attribute<Manufacturer, ?> anAttribute = 
                    entityManufacturer.getDeclaredAttribute("hardwareDesigners");//, entityHardwareDesigner.getJavaType());
                //System.out.println("entityManufacturer.getDeclaredAttribute(hardwareDesigners) " + anAttribute);
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);            
            
            expectedIAExceptionThrown = false;            
            try {
                //<E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);
                // UC4 - the attribute is on the immediate superclass
                Attribute<Manufacturer, ?> anAttribute = 
                    entityManufacturer.getDeclaredAttribute("corporateComputers");//, entityComputer.getJavaType());
                //System.out.println("entityManufacturer.getDeclaredList(corporateComputers) " + anAttribute);
            } catch (IllegalArgumentException iae) {
                // expecting
                // java.lang.IllegalArgumentException: The declared attribute [corporateComputers] from the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] is not present - however, it is declared on a superclass.
                //iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertTrue(expectedIAExceptionThrown);
            
            

            /**
             *  Return the single-valued attribute of the managed type that
             *  corresponds to the specified name in the represented type.
             *  @param name  the name of the represented attribute
             *  @return single-valued attribute with the given name
             *  @throws IllegalArgumentException if attribute of the given
             *          name is not present in the managed type
             */
            //SingularAttribute<? super X, ?> getSingularAttribute(String name);

            /**
             *  Return the declared single-valued attribute of the managed
             *  type that corresponds to the specified name in the
             *  represented type.
             *  @param name  the name of the represented attribute
             *  @return declared single-valued attribute of the given 
             *          name
             *  @throws IllegalArgumentException if attribute of the given
             *          name is not declared in the managed type
             */
            //SingularAttribute<X, ?> getDeclaredSingularAttribute(String name);

            /**
             *  Return the Collection-valued attribute of the managed type 
             *  that corresponds to the specified name.
             *  @param name  the name of the represented attribute
             *  @return CollectionAttribute of the given name
             *  @throws IllegalArgumentException if attribute of the given
             *          name is not present in the managed type
             */    
            //CollectionAttribute<? super X, ?> getCollection(String name); 

            /**
             *  Return the Set-valued attribute of the managed type that
             *  corresponds to the specified name.
             *  @param name  the name of the represented attribute
             *  @return SetAttribute of the given name
             *  @throws IllegalArgumentException if attribute of the given
             *          name is not present in the managed type
             */
            //SetAttribute<? super X, ?> getSet(String name);

            /**
             *  Return the List-valued attribute of the managed type that
             *  corresponds to the specified name.
             *  @param name  the name of the represented attribute
             *  @return ListAttribute of the given name
             *  @throws IllegalArgumentException if attribute of the given
             *          name is not present in the managed type
             */
            //ListAttribute<? super X, ?> getList(String name);

            /**
             *  Return the Map-valued attribute of the managed type that
             *  corresponds to the specified name.
             *  @param name  the name of the represented attribute
             *  @return MapAttribute of the given name
             *  @throws IllegalArgumentException if attribute of the given
             *          name is not present in the managed type
             */
            //MapAttribute<? super X, ?, ?> getMap(String name); 

            /**
             *  Return the Collection-valued attribute declared by the 
             *  managed type that corresponds to the specified name.
             *  @param name  the name of the represented attribute
             *  @return declared CollectionAttribute of the given name
             *  @throws IllegalArgumentException if attribute of the given
             *          name is not declared in the managed type
             */
            //CollectionAttribute<X, ?> getDeclaredCollection(String name); 

            /**
             *  Return the Set-valued attribute declared by the managed type 
             *  that corresponds to the specified name.
             *  @param name  the name of the represented attribute
             *  @return declared SetAttribute of the given name
             *  @throws IllegalArgumentException if attribute of the given
             *          name is not declared in the managed type
             */
            //SetAttribute<X, ?> getDeclaredSet(String name);

            /**
             *  Return the List-valued attribute declared by the managed 
             *  type that corresponds to the specified name.
             *  @param name  the name of the represented attribute
             *  @return declared ListAttribute of the given name
             *  @throws IllegalArgumentException if attribute of the given
             *          name is not declared in the managed type
             */
            //ListAttribute<X, ?> getDeclaredList(String name);

            /**
             *  Return the Map-valued attribute declared by the managed 
             *  type that corresponds to the specified name.
             *  @param name  the name of the represented attribute
             *  @return declared MapAttribute of the given name
             *  @throws IllegalArgumentException if attribute of the given
             *          name is not declared in the managed type
             */
            //MapAttribute<X, ?, ?> getDeclaredMap(String name);            
            
            // Verify ManagedType operations
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            
            /**
             *  Return the attribute that corresponds to the id attribute 
             *  declared by the entity or mapped superclass.
             *  @param type  the type of the represented declared id attribute
             *  @return declared id attribute
             *  @throws IllegalArgumentException if id attribute of the given
             *          type is not declared in the identifiable type or if
             *          the identifiable type has an id class
             */
            //public <Y> SingularAttribute<X, Y> getDeclaredId(Class<Y> type) {
            
            /**
             *  Return the attribute that corresponds to the version 
             *  attribute declared by the entity or mapped superclass.
             *  @param type  the type of the represented declared version 
             *               attribute
             *  @return declared version attribute
             *  @throws IllegalArgumentException if version attribute of the 
             *          type is not declared in the identifiable type
             */
            //public <Y> SingularAttribute<X, Y> getDeclaredVersion(Class<Y> type) {
            
            /**
             *   Return the attributes corresponding to the id class of the
             *   identifiable type.
             *   @return id attributes
             *   @throws IllegalArgumentException if the identifiable type
             *           does not have an id class
             */
            //public Set<SingularAttribute<? super X, ?>> getIdClassAttributes() {
            
            /**
             *  Return the attribute that corresponds to the id attribute of 
             *  the entity or mapped superclass.
             *  @param type  the type of the represented id attribute
             *  @return id attribute
             *  @throws IllegalArgumentException if id attribute of the given
             *          type is not present in the identifiable type or if
             *          the identifiable type has an id class
             */
            //public <Y> SingularAttribute<? super X, Y> getId(Class<Y> type) {
            
            /**
             *  Return the type that represents the type of the id.
             *  @return type of id
             */
            //public abstract Type<?> getIdType();
            
            /**
             *  Return the identifiable type that corresponds to the most
             *  specific mapped superclass or entity extended by the entity 
             *  or mapped superclass. 
             *  @return supertype of identifiable type or null if no such supertype
             */
            //public IdentifiableType<? super X> getSupertype() {

            /**
             *  Return the attribute that corresponds to the version 
             *    attribute of the entity or mapped superclass.
             *  @param type  the type of the represented version attribute
             *  @return version attribute
             *  @throws IllegalArgumentException if version attribute of the 
             *          given type is not present in the identifiable type
             */
            //public <Y> SingularAttribute<? super X, Y> getVersion(Class<Y> type) {
            // in progress
            //SingularAttribute<? super Manufacturer, Integer> versionAttribute = entityManufacturer.getVersion(Integer.class);
            
            /**
             *  Whether or not the identifiable type has an id attribute.
             *  Returns true for a simple id or embedded id; returns false
             *  for an idclass.
             *  @return boolean indicating whether or not the identifiable
             *           type has a single id attribute
             */
            //public boolean hasSingleIdAttribute() {
            
            /**
             *  Whether or not the identifiable type has a version attribute.
             *  @return boolean indicating whether or not the identifiable
             *           type has a version attribute
             */
            //public boolean hasVersionAttribute() {

            
            
            
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
            
            // 20090707: We are getting a CCE because "all" Collections are defaulting to List 
            // when they are lazy instantiated as IndirectList if persisted as a List independent of what the OneToOne mapping is defined as
//            javax.persistence.metamodel.CollectionAttribute<? super Manufacturer, Computer> computersAttribute = 
//                entityManufacturer.getCollection("computers", Computer.class);
//            javax.persistence.metamodel.CollectionAttribute<? super Manufacturer, Computer> computersAttribute2 = 
//                entityManufacturer.getCollection("computers", Computer.class);
            javax.persistence.metamodel.SetAttribute<? super Manufacturer, Computer> computersAttribute = 
                entityManufacturer.getSet("computers", Computer.class);
            //System.out.println("_Manufacturer.computers: " + computersAttribute);
            
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

            
            // Normal use cases
            // Composite table FK's that include a MappedSuperclass
            // get an Attribute<Container, Type==String>
            Attribute nameAttribute = entityManufacturer.getAttribute("name");
            assertTrue(null != nameAttribute);
            
            // get an Attribute<Container, Type==MappedSuperclass>
            Attribute employerAttribute = entityHardwareDesigner.getAttribute("employer");
            assertTrue(null != employerAttribute);

            

            // Variant use cases
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            boolean iae1thrown = false;
            
            // A Criteria API query that is expected to fail with an IAE
            QueryBuilder qbForExceptions = null;
            
            iae1thrown = false;
            try {
                qbForExceptions = em.getQueryBuilder();
                CriteriaQuery<Computer> cq = qbForExceptions.createQuery(Computer.class);
                Root from = cq.from(Computer.class);
                Path invalidPath = from.get("____unknown_attribute_name_should_fail_with_IAE_____");
            } catch (Exception e) {
                iae1thrown = true;
                //e.printStackTrace();
            }
            assertTrue(iae1thrown);
            
            
            // try a getAttribute on a missing attribute - should cause an IAE
            iae1thrown = false;
            try {
                entityManufacturer.getAttribute("_unknownAttribute");
            } catch (IllegalArgumentException expectedIAE) {
                //System.err.println("Metamodel: The following IAE exception is expected");
                // java.lang.IllegalArgumentException: The attribute [_unknownAttribute] from the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] is not present.
                iae1thrown = true;
            }
            // verify that we got an expected exception
            assertTrue(iae1thrown);

            // try a getSet on an unknown Set attribute - should still cause a IAE
            iae1thrown = false;
            try {
                entityManufacturer.getSet("_unknownAttribute");
            } catch (IllegalArgumentException expectedIAE) {
                //System.err.println("Metamodel: The following IAE exception is expected");
                // java.lang.IllegalArgumentException: The attribute [_unknownAttribute] from the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] is not present.
                iae1thrown = true;
            }
            // verify that we got an expected exception
            assertTrue(iae1thrown);
            
            // try a getSet on an unknown Set attribute - but with the right type (but how do we really know the type) - should still cause the same IAE
            iae1thrown = false;
            try {
                entityManufacturer.getSet("_unknownSet", entityComputer.getJavaType());
            } catch (IllegalArgumentException expectedIAE) {
                //System.err.println("Metamodel: The following IAE exception is expected");
                // java.lang.IllegalArgumentException: The attribute [_unknownSet] from the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] is not present.
                iae1thrown = true;
            }
            // verify that we got an expected exception
            assertTrue(iae1thrown);

            // try a getSet on a known Set attribute - but with the wrong type like another EntityType Memory - should cause a different IAE
            iae1thrown = false;
            try {
                entityManufacturer.getSet("computers", entityMemory.getJavaType());
            } catch (IllegalArgumentException expectedIAE) {
                //System.err.println("Metamodel: The following IAE exception is expected");
                //expectedIAE.printStackTrace();
                //java.lang.IllegalArgumentException: Expected attribute type [class org.eclipse.persistence.testing.models.jpa.metamodel.Memory] on the existing attribute [computers] on the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] but found attribute type [org.eclipse.persistence.testing.models.jpa.metamodel.Computer].
                iae1thrown = true;
            } catch (Exception unexpectedException) {
                unexpectedException.printStackTrace();
            }
            // verify that we got an expected exception
            assertTrue(iae1thrown);

            exceptionThrown = false;
            try {
                //System.out.println("_entityManufacturer.getDeclaredCollection(type): " + entityManufacturer.getDeclaredCollection("name", String.class));
                // Ask for a Collection using a String type - invalid
                entityManufacturer.getDeclaredCollection("name", String.class);
            } catch (Exception e) {
                // This exception is expected here
                exceptionThrown = true;                
                //e.printStackTrace();
            }
            assertTrue(exceptionThrown);
            // reset exception flag
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
            // we enter here on a failed commit() - for example if the table schema is incorrectly defined
            e.printStackTrace();
            exceptionThrown = true;
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
