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

import java.lang.reflect.Field;
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
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.QueryBuilder;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;
import javax.persistence.metamodel.Type.PersistenceType;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.eclipse.persistence.internal.expressions.ClassTypeExpression;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.metamodel.AttributeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.EmbeddableTypeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.EntityTypeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.ManagedTypeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.MappedSuperclassTypeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.MetamodelImpl;
import org.eclipse.persistence.internal.jpa.metamodel.SingularAttributeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.TypeImpl;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.metamodel.ArrayProcessor;
import org.eclipse.persistence.testing.models.jpa.metamodel.Board;
import org.eclipse.persistence.testing.models.jpa.metamodel.Computer;
import org.eclipse.persistence.testing.models.jpa.metamodel.Corporation;
import org.eclipse.persistence.testing.models.jpa.metamodel.EmbeddedPK;
import org.eclipse.persistence.testing.models.jpa.metamodel.GalacticPosition;
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
 * Disclaimer:
 *    Yes, the following are true for this test suite - but implementation time must be ""triaged"", and this testing code is at the bottom of the list when placed against actual implementation in the time provided.
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
        // Drop all tables : Thank you Chris
        /*java.util.Vector v = JUnitTestCase.getServerSession("metamodel1").executeSQL("select tablename from sys.systables where tabletype='T'");
        for (int i=0; i<v.size(); i++){
            try{
                DatabaseRecord dr = (DatabaseRecord)v.get(i);
                JUnitTestCase.getServerSession().executeNonSelectingSQL("Drop table "+dr.getValues().get(0));
            } catch (Exception e){
                System.out.println(e);
            }
        }*/        
        /*JUnitTestCase.getServerSession(PERSISTENCE_UNIT_NAME).executeNonSelectingSQL("DROP TABLE CMP3_MM_HIST_EMPLOY");
        JUnitTestCase.getServerSession(PERSISTENCE_UNIT_NAME).executeNonSelectingSQL("DROP TABLE CMP3_MM_MANUF_MM_HWDES_MAP");
        JUnitTestCase.getServerSession(PERSISTENCE_UNIT_NAME).executeNonSelectingSQL("DROP TABLE CMP3_MM_MANUF_MM_CORPCOMPUTER");
        JUnitTestCase.getServerSession(PERSISTENCE_UNIT_NAME).executeNonSelectingSQL("DROP TABLE CMP3_MM_MANUF_MM_COMPUTER");
        JUnitTestCase.getServerSession(PERSISTENCE_UNIT_NAME).executeNonSelectingSQL("DROP TABLE CMP3_MM_MANUF_MM_HWDESIGNER");
        JUnitTestCase.getServerSession(PERSISTENCE_UNIT_NAME).executeNonSelectingSQL("DROP TABLE CMP3_MM_BOARD_MM_MEMORY");
        JUnitTestCase.getServerSession(PERSISTENCE_UNIT_NAME).executeNonSelectingSQL("DROP TABLE CMP3_MM_BOARD_MM_PRO");
        JUnitTestCase.getServerSession(PERSISTENCE_UNIT_NAME).executeNonSelectingSQL("DROP TABLE CMP3_MM_COMPUTER_MM_USER");
        JUnitTestCase.getServerSession(PERSISTENCE_UNIT_NAME).executeNonSelectingSQL("DROP TABLE CMP3_MM_BOARD_SEQ");

        JUnitTestCase.getServerSession(PERSISTENCE_UNIT_NAME).executeNonSelectingSQL("DROP TABLE CMP3_MM_COMPUTER");
        JUnitTestCase.getServerSession(PERSISTENCE_UNIT_NAME).executeNonSelectingSQL("DROP TABLE CMP3_MM_USER");
        JUnitTestCase.getServerSession(PERSISTENCE_UNIT_NAME).executeNonSelectingSQL("DROP TABLE CMP3_MM_HWDESIGNER");
        JUnitTestCase.getServerSession(PERSISTENCE_UNIT_NAME).executeNonSelectingSQL("DROP TABLE CMP3_MM_MEMORY");
        JUnitTestCase.getServerSession(PERSISTENCE_UNIT_NAME).executeNonSelectingSQL("DROP TABLE CMP3_MM_PROC");
        JUnitTestCase.getServerSession(PERSISTENCE_UNIT_NAME).executeNonSelectingSQL("DROP TABLE CMP3_MM_LOCATION");
        JUnitTestCase.getServerSession(PERSISTENCE_UNIT_NAME).executeNonSelectingSQL("DROP TABLE CMP3_MM_BOARD");
        JUnitTestCase.getServerSession(PERSISTENCE_UNIT_NAME).executeNonSelectingSQL("DROP TABLE CMP3_MM_SWDESIGNER");
        JUnitTestCase.getServerSession(PERSISTENCE_UNIT_NAME).executeNonSelectingSQL("DROP TABLE CMP3_MM_MANUF");
        */        
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite("MetamodelMetamodelTest");

        //suite.addTest(new MetamodelMetamodelTest("testMetamodelStringBasedQuery"));
        //suite.addTest(new MetamodelMetamodelTest("testMetamodelTypeSafeBasedQuery"));
        suite.addTest(new MetamodelMetamodelTest("testMetamodelFullImplementation"));
        return suite;
    }

    /**
     * The following large single test case contains signatures of all the spec functions.
     * Those that have a test are implemented, the missing ones may still be in development.
     */
    public void testMetamodelFullImplementation() {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        Collection<Board> boardCollection = new HashSet<Board>();        
        Set<Computer> computersList = new HashSet<Computer>();
        Collection<Memory> vectorMemories = new HashSet<Memory>();
        Collection<Memory> arrayMemories = new HashSet<Memory>();
        Collection<Processor> vectorProcessors = new HashSet<Processor>();
        Collection<Processor> arrayProcessors = new HashSet<Processor>();
        List<HardwareDesigner> hardwareDesigners = new ArrayList<HardwareDesigner>();
        Map<String, HardwareDesigner> mappedDesigners = new HashMap<String, HardwareDesigner>();
        Computer arrayComputer1 = null;
        Computer vectorComputer2 = null;
        Manufacturer manufacturer = null;
        User user = null;
        HardwareDesigner hardwareDesigner1 = null;
        SoftwareDesigner softwareDesigner1 = null;        
        Processor vectorProcessor1 = null;
        Processor arrayProcessor1 = null;
        Board arrayBoard1 = null;
        Board vectorBoard2 = null;
        Memory arrayMemory1 = null;
        Memory vectorMemory2 = null;
        GalacticPosition location1 = null;
        GalacticPosition location2 = null;   
        
        // Embedded objects
        EmbeddedPK embeddedPKforLocation1 = new EmbeddedPK();        
        EmbeddedPK embeddedPKforLocation2 = new EmbeddedPK();
        
        boolean exceptionThrown = false;
        Metamodel metamodel = null;

        try {
            emf = initialize();
            em = emf.createEntityManager();

            // Unset the metamodel - for repeated runs through this test
            ((EntityManagerFactoryImpl)emf).setMetamodel(null);
            // Pre-Persist: get Metamodel representation of the entity schema
            metamodel = em.getMetamodel();
            assertNotNull(metamodel);
            //System.out.println("_Metamodel just after initialization: " + metamodel);

            em.getTransaction().begin();

            // setup entity relationships
            arrayComputer1 = new Computer();
            vectorComputer2 = new Computer();
            arrayMemory1 = new Memory();
            vectorMemory2 = new Memory();
            manufacturer = new Manufacturer();
            user = new User();
            hardwareDesigner1 = new HardwareDesigner();
            softwareDesigner1 = new SoftwareDesigner();
            vectorProcessor1 = new VectorProcessor();
            arrayProcessor1 = new ArrayProcessor();
            arrayBoard1 = new Board();
            vectorBoard2 = new Board();
            location1 = new GalacticPosition();
            location2 = new GalacticPosition();        

            // setup collections
            computersList.add(arrayComputer1);
            computersList.add(vectorComputer2);
            vectorProcessors.add(vectorProcessor1);
            arrayProcessors.add(arrayProcessor1);
            arrayMemories.add(arrayMemory1);
            vectorMemories.add(vectorMemory2);
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
            arrayComputer1.setManufacturer(manufacturer);
            vectorComputer2.setManufacturer(manufacturer);
            arrayBoard1.setMemories(arrayMemories);
            vectorBoard2.setMemories(vectorMemories);
            arrayMemory1.setBoard(arrayBoard1);
            vectorMemory2.setBoard(vectorBoard2);
            arrayBoard1.setProcessors(arrayProcessors);
            vectorBoard2.setProcessors(vectorProcessors);
            arrayProcessor1.setBoard(arrayBoard1);
            vectorProcessor1.setBoard(vectorBoard2);            
            
            softwareDesigner1.setPrimaryEmployer(manufacturer);
            softwareDesigner1.setSecondaryEmployer(manufacturer);
            
            arrayComputer1.setCircuitBoards(boardCollection);
            arrayBoard1.setComputer(arrayComputer1);
            vectorBoard2.setComputer(vectorComputer2);
            
            // set 1:1 relationships
            arrayComputer1.setLocation(location1);
            vectorComputer2.setLocation(location2);
            // No get/set accessors on purpose for testing
            //location1.futurePosition = location2;
            //location2.futurePosition = location1;
            
            // set attributes
            arrayComputer1.setName("CM-5");
            vectorComputer2.setName("CDC-6600");
            
            // setup embedded objects
//            location1.setPrimaryKey(embeddedPKforLocation1);
//            location2.setPrimaryKey(embeddedPKforLocation2);
                
            // persist all entities to the database in a single transaction
            em.persist(arrayComputer1);
            em.persist(vectorComputer2);
            em.persist(manufacturer);
            em.persist(user);
            em.persist(hardwareDesigner1);
            em.persist(softwareDesigner1);
            em.persist(vectorProcessor1);
            em.persist(arrayProcessor1);
            em.persist(arrayBoard1);
            em.persist(vectorBoard2);
            em.persist(arrayMemory1);
            em.persist(vectorMemory2);
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
            EntityTypeImpl<GalacticPosition> entityLocation =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
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
                    fail("Results from criteria query (ReadAllQuery(referenceClass=Computer sql=SELECT COMPUTER_ID, NAME, COMPUTER_VERSION, MANUFACTURER_PERSON_ID, LOCATION_LOCATION_ID FROM CMP3_MM_COMPUTER WHERE NAME='CM-5') were expected");
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
            Class locationJavaType = ((SingularAttribute<Computer, GalacticPosition>)entityComputer.getAttribute("location")).getBindableJavaType();
            assertNotNull(locationJavaType);
            assertEquals(GalacticPosition.class, locationJavaType);
            
            
            
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
            IdentifiableType<? super GalacticPosition> superTypeLocation = null;
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
            
            // Test EntityType
/*            
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

            // check that the elementType and the owningType (managedType) are set correctly
            // See issue 50 where some mapping types were not setting the elementType correctly (this includes aggregate types like Embeddable)
            // http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_50:_20090727:_Handle_all_mapping_types_in_the_SingularAttribute_constructor
            // Get the ManagedType and check this SingularAttribute PK
            Attribute locationIdAttribute = entityLocation.getAttribute("primaryKey");
            assertNotNull(locationIdAttribute);
            assertTrue(locationIdAttribute instanceof SingularAttributeImpl);
            assertFalse(locationIdAttribute.isCollection());
            assertFalse(((AttributeImpl)locationIdAttribute).isPlural()); // non-spec.
            ManagedType locationIdAttributeManagedType = locationIdAttribute.getDeclaringType();
            assertEquals(entityLocation, locationIdAttributeManagedType);
            ManagedTypeImpl locationIdAttributeManagedTypeImpl = ((SingularAttributeImpl)locationIdAttribute).getManagedTypeImpl();
            assertEquals(locationIdType.getJavaType(), ((SingularAttributeImpl)locationIdAttribute).getBindableJavaType());
            assertEquals(Bindable.BindableType.SINGULAR_ATTRIBUTE, ((SingularAttributeImpl)locationIdAttribute).getBindableType());
            assertEquals(locationIdType.getJavaType(), locationIdAttribute.getJavaType());
            Type embeddableType = ((SingularAttributeImpl)locationIdAttribute).getType();
            assertNotNull(embeddableType);
            assertNotSame(embeddableType, locationIdAttributeManagedType);
            
*/

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
            
            // Test MappedSuperclassType
            // Test normal path for a [Basic] type
            expectedIAExceptionThrown = false;
            Type<?> personIdType = null;
            MappedSuperclassTypeImpl<Person> msPerson = (MappedSuperclassTypeImpl)metamodel.type(Person.class);
            assertNotNull(msPerson);
            MappedSuperclassTypeImpl<Corporation> msCorporation = (MappedSuperclassTypeImpl)metamodel.type(Corporation.class);
            assertNotNull(msCorporation);
            
            try {
                personIdType = msPerson.getIdType();
            } catch (IllegalArgumentException iae) {
                // expecting no exception
                iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);
            assertNotNull(personIdType);
            assertEquals(PersistenceType.BASIC, personIdType.getPersistenceType());
            assertEquals(Integer.class, personIdType.getJavaType());
            
            // Verify all types (entities, embeddables, mappedsuperclasses and basic)
            try {
                // get all 21 types (a non spec function - for testing introspection)
                Map<Class, TypeImpl<?>> typesMap = ((MetamodelImpl)metamodel).getTypes();
                System.out.println("_MetamodelMetamodelTest: all Types: " + typesMap);
                // verify each one
                assertNotNull(typesMap);
                assertEquals(21, typesMap.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
            /*
             * Metamodel model toString
             * ************************************************************************************
    class org.eclipse.persistence.testing.models.jpa.metamodel.Person=MappedSuperclassTypeImpl@9206757 [ 
        javaType: class org.eclipse.persistence.testing.models.jpa.metamodel.Person 
        descriptor: RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Person --> [DatabaseTable(__METAMODEL_RESERVED_IN_MEM_ONLY_TABLE_NAME)]), 
        mappings: [
            org.eclipse.persistence.mappings.DirectToFieldMapping[
                id-->__METAMODEL_RESERVED_IN_MEM_ONLY_TABLE_NAME.PERSON_ID], 
            org.eclipse.persistence.mappings.DirectToFieldMapping[
                name-->__METAMODEL_RESERVED_IN_MEM_ONLY_TABLE_NAME.NAME]]],
    class org.eclipse.persistence.testing.models.jpa.metamodel.Corporation=MappedSuperclassTypeImpl@27921979 [ 
        javaType: class org.eclipse.persistence.testing.models.jpa.metamodel.Corporation 
        descriptor: RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Corporation --> [DatabaseTable(__METAMODEL_RESERVED_IN_MEM_ONLY_TABLE_NAME)]), 
        mappings: [
            org.eclipse.persistence.mappings.ManyToManyMapping[corporateComputers]]], 
    class org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer=EntityTypeImpl@12565475 [ 
        javaType: class org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer 
        descriptor: RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)]), 
        mappings: [
            org.eclipse.persistence.mappings.DirectToFieldMapping[
                id-->CMP3_MM_MANUF.PERSON_ID], 
            org.eclipse.persistence.mappings.DirectToFieldMapping[
                name-->CMP3_MM_MANUF.NAME], 
            org.eclipse.persistence.mappings.DirectToFieldMapping[
                version-->CMP3_MM_MANUF.MANUF_VERSION], 
            org.eclipse.persistence.mappings.OneToManyMapping[
                computers], 
            org.eclipse.persistence.mappings.OneToManyMapping[
                hardwareDesignersMap], 
            org.eclipse.persistence.mappings.ManyToManyMapping[
                corporateComputers], 
            org.eclipse.persistence.mappings.OneToManyMapping[
                hardwareDesigners]]], 
    class org.eclipse.persistence.testing.models.jpa.metamodel.Memory=EntityTypeImpl@29905988 [ 
        javaType: class org.eclipse.persistence.testing.models.jpa.metamodel.Memory 
        descriptor: RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Memory --> [DatabaseTable(CMP3_MM_MEMORY)]), 
        mappings: [
            org.eclipse.persistence.mappings.DirectToFieldMapping[
                id-->CMP3_MM_MEMORY.MEMORY_ID], 
            org.eclipse.persistence.mappings.DirectToFieldMapping[
                version-->CMP3_MM_MEMORY.MEMORY_VERSION], 
            org.eclipse.persistence.mappings.OneToOneMapping[
                board]]], 
    class org.eclipse.persistence.testing.models.jpa.metamodel.Designer=MappedSuperclassTypeImpl@25971327 [ 
        javaType: class org.eclipse.persistence.testing.models.jpa.metamodel.Designer 
        descriptor: RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Designer --> [DatabaseTable(__METAMODEL_RESERVED_IN_MEM_ONLY_TABLE_NAME)]), 
        mappings: [
            org.eclipse.persistence.mappings.OneToOneMapping[
                secondaryEmployer], 
            org.eclipse.persistence.mappings.OneToOneMapping[
                primaryEmployer], 
            org.eclipse.persistence.mappings.ManyToManyMapping[
                historicalEmployers]]], 
    class org.eclipse.persistence.testing.models.jpa.metamodel.HardwareDesigner=EntityTypeImpl@18107298 [ 
        javaType: class org.eclipse.persistence.testing.models.jpa.metamodel.HardwareDesigner 
        descriptor: RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.HardwareDesigner --> [DatabaseTable(CMP3_MM_HWDESIGNER)]), 
        mappings: [
            org.eclipse.persistence.mappings.DirectToFieldMapping[
                id-->CMP3_MM_HWDESIGNER.PERSON_ID], 
            org.eclipse.persistence.mappings.DirectToFieldMapping[
                name-->CMP3_MM_HWDESIGNER.NAME], 
            org.eclipse.persistence.mappings.DirectToFieldMapping[
                version-->CMP3_MM_HWDESIGNER.HWDESIGNER_VERSION], 
            org.eclipse.persistence.mappings.OneToOneMapping[
                employer], 
            org.eclipse.persistence.mappings.OneToOneMapping[
                mappedEmployer], 
            org.eclipse.persistence.mappings.ManyToManyMapping[
                historicalEmployers], 
            org.eclipse.persistence.mappings.OneToOneMapping[
                secondaryEmployer], 
            org.eclipse.persistence.mappings.OneToOneMapping[
                primaryEmployer]]], 
    class org.eclipse.persistence.testing.models.jpa.metamodel.SoftwareDesigner=EntityTypeImpl@26130360 [ 
        javaType: class org.eclipse.persistence.testing.models.jpa.metamodel.SoftwareDesigner 
        descriptor: RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.SoftwareDesigner --> [DatabaseTable(CMP3_MM_SWDESIGNER)]), 
        mappings: [
            org.eclipse.persistence.mappings.DirectToFieldMapping[
                id-->CMP3_MM_SWDESIGNER.PERSON_ID], 
            org.eclipse.persistence.mappings.DirectToFieldMapping[
                name-->CMP3_MM_SWDESIGNER.NAME], 
            org.eclipse.persistence.mappings.DirectToFieldMapping[
                version-->CMP3_MM_SWDESIGNER.SWDESIGNER_VERSION], 
            org.eclipse.persistence.mappings.OneToOneMapping[
                secondaryEmployer], 
            org.eclipse.persistence.mappings.OneToOneMapping[
                primaryEmployer], 
            org.eclipse.persistence.mappings.ManyToManyMapping[
                historicalEmployers]]], 
    class org.eclipse.persistence.testing.models.jpa.metamodel.Board=EntityTypeImpl@24223536 [ 
        javaType: class org.eclipse.persistence.testing.models.jpa.metamodel.Board 
        descriptor: RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Board --> [DatabaseTable(CMP3_MM_BOARD)]), 
        mappings: [
            org.eclipse.persistence.mappings.DirectToFieldMapping[
                id-->CMP3_MM_BOARD.BOARD_ID], 
            org.eclipse.persistence.mappings.DirectToFieldMapping[
                version-->CMP3_MM_BOARD.BOARD_VERSION], 
            org.eclipse.persistence.mappings.OneToManyMapping[
                memories], 
            org.eclipse.persistence.mappings.OneToManyMapping[
                processors]]], 
    class org.eclipse.persistence.testing.models.jpa.metamodel.EmbeddedPK=EmbeddableTypeImpl@29441291 [ 
        javaType: class org.eclipse.persistence.testing.models.jpa.metamodel.EmbeddedPK descriptor: 
        RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.EmbeddedPK --> []), 
        mappings: [
            org.eclipse.persistence.mappings.DirectToFieldMapping[
                pk_part1-->LOCATION_ID]]], 
    class org.eclipse.persistence.testing.models.jpa.metamodel.Location=EntityTypeImpl@9050487 [ 
        javaType: class org.eclipse.persistence.testing.models.jpa.metamodel.Location 
        descriptor: RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Location --> [DatabaseTable(CMP3_MM_LOCATION)]), 
        mappings: [
            org.eclipse.persistence.mappings.DirectToFieldMapping[
                version-->CMP3_MM_LOCATION.LOCATION_VERSION], 
            org.eclipse.persistence.mappings.AggregateObjectMapping[
                primaryKey]]], 
    class org.eclipse.persistence.testing.models.jpa.metamodel.VectorProcessor=EntityTypeImpl@9300338 [ 
        javaType: class org.eclipse.persistence.testing.models.jpa.metamodel.VectorProcessor 
        descriptor: RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.VectorProcessor --> [DatabaseTable(CMP3_MM_PROC)]), 
        mappings: [
            org.eclipse.persistence.mappings.DirectToFieldMapping[
                id-->CMP3_MM_PROC.VECTPROC_ID], 
            org.eclipse.persistence.mappings.DirectToFieldMapping[
                version-->CMP3_MM_PROC.VECTPROC_VERSION], 
            org.eclipse.persistence.mappings.OneToOneMapping[
                board]]], 
    class org.eclipse.persistence.testing.models.jpa.metamodel.ArrayProcessor=EntityTypeImpl@14247087 [ 
        javaType: class org.eclipse.persistence.testing.models.jpa.metamodel.ArrayProcessor 
        descriptor: RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.ArrayProcessor --> [DatabaseTable(CMP3_MM_ARRAYPROC)]), 
        mappings: [
            org.eclipse.persistence.mappings.DirectToFieldMapping[
                id-->CMP3_MM_ARRAYPROC.ARRAYPROC_ID], 
            org.eclipse.persistence.mappings.DirectToFieldMapping[
                version-->CMP3_MM_ARRAYPROC.ARRAYPROC_VERSION], 
            org.eclipse.persistence.mappings.OneToOneMapping[
                board]]], 
    class org.eclipse.persistence.testing.models.jpa.metamodel.Computer=EntityTypeImpl@8355938 [ 
        javaType: class org.eclipse.persistence.testing.models.jpa.metamodel.Computer 
        descriptor: RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Computer --> [DatabaseTable(CMP3_MM_COMPUTER)]), 
        mappings: [
            org.eclipse.persistence.mappings.DirectToFieldMapping[
                id-->CMP3_MM_COMPUTER.COMPUTER_ID], 
            org.eclipse.persistence.mappings.DirectToFieldMapping[
                name-->CMP3_MM_COMPUTER.NAME], 
            org.eclipse.persistence.mappings.DirectToFieldMapping[
                version-->CMP3_MM_COMPUTER.COMPUTER_VERSION], 
            org.eclipse.persistence.mappings.OneToOneMapping[
                manufacturer], 
            org.eclipse.persistence.mappings.OneToOneMapping[
                location]]], 
    class org.eclipse.persistence.testing.models.jpa.metamodel.User=EntityTypeImpl@12968655 [ 
        javaType: class org.eclipse.persistence.testing.models.jpa.metamodel.User 
        descriptor: RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.User --> [DatabaseTable(CMP3_MM_USER)]), 
        mappings: [
            org.eclipse.persistence.mappings.DirectToFieldMapping[
                id-->CMP3_MM_USER.PERSON_ID], 
            org.eclipse.persistence.mappings.DirectToFieldMapping[
                name-->CMP3_MM_USER.NAME], 
            org.eclipse.persistence.mappings.DirectToFieldMapping[
                version-->CMP3_MM_USER.USER_VERSION]]], 
    class org.eclipse.persistence.testing.models.jpa.metamodel.Processor=MappedSuperclassTypeImpl@24044524 [ 
        javaType: class org.eclipse.persistence.testing.models.jpa.metamodel.Processor 
        descriptor: RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Processor --> [DatabaseTable(__METAMODEL_RESERVED_IN_MEM_ONLY_TABLE_NAME)]), 
        mappings: []], 
    class org.eclipse.persistence.testing.models.jpa.metamodel.CompositePK=EmbeddableTypeImpl@6367194 [ 
        javaType: class org.eclipse.persistence.testing.models.jpa.metamodel.CompositePK 
        descriptor: RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.CompositePK --> []), 
        mappings: []], 
    class java.lang.Integer=BasicTypeImpl@33083511 [ 
        javaType: class java.lang.Integer], 
    class java.lang.String=BasicTypeImpl@4086417 [ 
        javaType: class java.lang.String], 
    int=BasicTypeImpl@28057122 [ 
        javaType: int]}
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
            // We should see 7 attributes (3 List, 3 Singular) for Manufacturer (computers, hardwareDesigners, id(from the mappedSuperclass), 
            // version, name(from the mappedSuperclass) and corporateComputers from the Corporation mappedSuperclass)
            assertEquals(8, attributeSet.size());
            // for each managed entity we will see 2 entries (one for the Id, one for the Version)
            assertTrue(attributeSet.contains(entityManufacturer.getAttribute("id"))); // 
            assertTrue(attributeSet.contains(entityManufacturer.getAttribute("version"))); //
            assertTrue(attributeSet.contains(entityManufacturer.getAttribute("name"))); //
            assertTrue(attributeSet.contains(entityManufacturer.getAttribute("computers"))); //
            assertTrue(attributeSet.contains(entityManufacturer.getAttribute("hardwareDesigners"))); //
            assertTrue(attributeSet.contains(entityManufacturer.getAttribute("corporateComputers"))); //
            assertTrue(attributeSet.contains(entityManufacturer.getAttribute("hardwareDesignersMap"))); //
            // ManyToMany Collection Attribute from Person MappedSuperclass
            assertTrue(attributeSet.contains(entityManufacturer.getCollection("historicalEmployers"))); //
            assertTrue(entityManufacturer.getCollection("historicalEmployers").isCollection()); //

            
            /**
             *  Return the attributes declared by the managed type.
             *  Testing for Design Issue 52:
             *  http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI:52_Refactor:_20090817
             */
             //java.util.Set<Attribute<X, ?>> getDeclaredAttributes();
            expectedIAExceptionThrown = false;            
            try {
                /**
                 * Hierarchy:
                 *   Person : MappedSuperclass
                 *     +
                 *     +- id : Integer
                 *     +- name : String
                 *     +- historicalEmployers : Manufacturer
                 *     
                 *     Corporation : MappedSuperclass extends Person
                 *       +
                 *       +- corporateComputers : Collection 
                 *       
                 *       Manufacturer : Entity extends Corporation
                 *         +
                 *         +- computers : Set
                 *         +- hardwareDesigners : List
                 *         +- hardwareDesignersMap : Map
                 *         +- version : int
                 */
                Set<Attribute<Manufacturer, ?>> declaredAttributesSet = entityManufacturer.getDeclaredAttributes();
                //System.out.println("entityManufacturer.getDeclaredAttributes() " + declaredAttributesSet);
                assertNotNull(declaredAttributesSet);
                // We should see 4 declared out of 8 attributes for Manufacturer 
                assertEquals(4, declaredAttributesSet.size());
                // Id is declared 2 levels above
                assertFalse(declaredAttributesSet.contains(entityManufacturer.getAttribute("id"))); //
                // name is declared 2 levels above
                assertFalse(declaredAttributesSet.contains(entityManufacturer.getAttribute("name"))); //
                // corporateComputers is declared 1 level above
                assertFalse(declaredAttributesSet.contains(entityManufacturer.getAttribute("corporateComputers"))); //
                // version is declared at this level
                assertTrue(declaredAttributesSet.contains(entityManufacturer.getAttribute("version"))); //
                // computers is declared at this level
                assertTrue(declaredAttributesSet.contains(entityManufacturer.getAttribute("computers"))); //
                // hardwareDesigners is declared at this level
                assertTrue(declaredAttributesSet.contains(entityManufacturer.getAttribute("hardwareDesigners"))); //
                // hardwareDesignersMap is declared at this level
                assertTrue(declaredAttributesSet.contains(entityManufacturer.getAttribute("hardwareDesignersMap"))); //
                // historicalEmployers is declared 2 levels above
                assertFalse(declaredAttributesSet.contains(entityManufacturer.getAttribute("historicalEmployers"))); //
                
                Set<Attribute<Corporation, ?>> declaredAttributesSetForCorporation = msCorporation.getDeclaredAttributes();
                assertNotNull(declaredAttributesSetForCorporation);
                // We should see 1 declared out of 4 attributes for Computer 
                assertEquals(1, declaredAttributesSetForCorporation.size());
                // Id is declared 1 level above
                //assertFalse(declaredAttributesSetForCorporation.contains(msCorporation.getAttribute("id"))); //
                // name is declared 1 level above but is not visible in a ms-->ms hierarchy
                //assertFalse(declaredAttributesSetForCorporation.contains(msCorporation.getAttribute("name"))); //
                // corporateComputers is declared at this level
                assertTrue(declaredAttributesSetForCorporation.contains(msCorporation.getAttribute("corporateComputers"))); //
                // historicalEmployers is declared 1 level above but is not visible in a ms-->ms hierarchy
                //assertFalse(declaredAttributesSetForCorporation.contains(msCorporation.getAttribute("historicalEmployers"))); //                

                Set<Attribute<Person, ?>> declaredAttributesSetForPerson = msPerson.getDeclaredAttributes();
                assertNotNull(declaredAttributesSetForPerson);
                // We should see 3 declared out of 3 attributes for Person 
                assertEquals(3, declaredAttributesSetForPerson.size());
                // Id is declared at this level
                assertTrue(declaredAttributesSetForPerson.contains(msPerson.getAttribute("id"))); //
                // name is declared at this level
                assertTrue(declaredAttributesSetForPerson.contains(msPerson.getAttribute("name"))); //
                // historicalEmployers is declared at this level
                assertTrue(declaredAttributesSetForPerson.contains(msPerson.getAttribute("historicalEmployers"))); //

            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);            
           
            

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
                CollectionAttribute<Manufacturer, GalacticPosition> anAttribute = 
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
                ListAttribute<Manufacturer, GalacticPosition> anAttribute = 
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
                CollectionAttribute<Manufacturer, Computer> anAttribute = 
                    entityManufacturer.getDeclaredCollection("corporateComputers", entityComputer.getJavaType());
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

            expectedIAExceptionThrown = false;            
            Attribute<Manufacturer, ?> aListAttribute = null;
            try {
                //<E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);
                // the attribute is on the class
                aListAttribute = entityManufacturer.getDeclaredAttribute("hardwareDesigners");//, entityComputer.getJavaType());
            } catch (IllegalArgumentException iae) {
                // expecting
                // java.lang.IllegalArgumentException: The declared attribute [corporateComputers] from the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] is not present - however, it is declared on a superclass.
                //iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);
            assertNotNull(aListAttribute);


            // check the root
            expectedIAExceptionThrown = false;            
            Attribute<Person, Manufacturer> aCollectionAttribute = null;
            try {
                //<E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);
                // the attribute is on the class
                IdentifiableType person = entityManufacturer.getSupertype().getSupertype();
                aCollectionAttribute = person.getDeclaredAttribute("historicalEmployers");//, entityComputer.getJavaType());
            } catch (IllegalArgumentException iae) {
                // expecting
                // java.lang.IllegalArgumentException: The declared attribute [corporateComputers] from the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] is not present - however, it is declared on a superclass.
                //iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);
            assertNotNull(aCollectionAttribute);
            // check managed type
            assertEquals(msPerson, aCollectionAttribute.getDeclaringType());            
            // check element type
            //assertEquals(entityManufacturer, aCollectionAttribute.getDeclaringType());


            // positive: check one level down from the root
            expectedIAExceptionThrown = false;            
            Attribute<Corporation,?> aCollectionAttribute2 = null;
            try {
                //<E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);
                // the attribute is on the class
                IdentifiableType corporation = entityManufacturer.getSupertype();                
                aCollectionAttribute2 = corporation.getDeclaredAttribute("corporateComputers");//, entityComputer.getJavaType());
            } catch (IllegalArgumentException iae) {
                // expecting
                // java.lang.IllegalArgumentException: The declared attribute [corporateComputers] from the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] is not present - however, it is declared on a superclass.
                //iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);
            assertNotNull(aCollectionAttribute2);

            // negative: check one level down from the root
            expectedIAExceptionThrown = false;            
            aCollectionAttribute2 = null;
            try {
                //<E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);
                // the attribute is on the class
                IdentifiableType corporation = entityManufacturer.getSupertype();                
                aCollectionAttribute2 = corporation.getDeclaredAttribute("notFound");//, entityComputer.getJavaType());
            } catch (IllegalArgumentException iae) {
                // expecting
                // java.lang.IllegalArgumentException: The declared attribute [corporateComputers] from the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] is not present - however, it is declared on a superclass.
                //iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            // we expect an IAE on getAttribute(name) if name does not exist
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
            
            expectedIAExceptionThrown = false;            
            try {
                //<E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);
                // UC4 - the attribute is on the immediate superclass
                CollectionAttribute<Manufacturer, Computer> anAttribute = 
                    entityManufacturer.getDeclaredCollection("corporateComputers", entityComputer.getJavaType());
                //System.out.println("entityManufacturer.getDeclaredList(corporateComputers) " + anAttribute);
            } catch (IllegalArgumentException iae) {
                // expecting
                // java.lang.IllegalArgumentException: The declared attribute [corporateComputers] from the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] is not present - however, it is declared on a superclass.
                //iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertTrue(expectedIAExceptionThrown);

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
            // verify false for "no" type of Id attribute
            // test normal path
// 20090817: mid-implementation in parallel dev stream
/*            
            expectedIAExceptionThrown = false;
            boolean hasSingleIdAttribute = false;
            try {
                EntityType<Manufacturer> aType = metamodel.entity(Manufacturer.class);
                hasSingleIdAttribute = aType.hasSingleIdAttribute();
            } catch (IllegalArgumentException iae) {
                //iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);            
            assertTrue(hasSingleIdAttribute);
*/            
            
            
            /**
             *  Whether or not the identifiable type has a version attribute.
             *  @return boolean indicating whether or not the identifiable
             *           type has a version attribute
             */
            //public boolean hasVersionAttribute() {


            // Verify MetamodelImpl operations
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            /**
             *  Return the metamodel entity type representing the entity.
             *  @param cls  the type of the represented entity
             *  @return the metamodel entity type
             *  @throws IllegalArgumentException if not an entity
             */
            //<X> EntityType<X> entity(Class<X> cls);
            // test normal path
            expectedIAExceptionThrown = false;            
            try {
                EntityType<Manufacturer> aType = metamodel.entity(Manufacturer.class);
            } catch (IllegalArgumentException iae) {
                //iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);            
            
            // test variant path: null causes IAE
            expectedIAExceptionThrown = false;            
            try {
                EntityType<Manufacturer> aType = metamodel.entity(null);
            } catch (IllegalArgumentException iae) {
                //iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertTrue(expectedIAExceptionThrown);            

            // test variant path: wrong type (java simple type)
            expectedIAExceptionThrown = false;            
            try {
                EntityType<Integer> aType = metamodel.entity(Integer.class);
            } catch (IllegalArgumentException iae) {
                //iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertTrue(expectedIAExceptionThrown);            

            // test variant path: wrong type (BasicType)
            

            /**
             *  Return the metamodel managed type representing the 
             *  entity, mapped superclass, or embeddable class.
             *  @param cls  the type of the represented managed class
             *  @return the metamodel managed type
             *  @throws IllegalArgumentException if not a managed class
             */
            //<X> ManagedType<X> type(Class<X> cls);
            // test normal path (subtype = Basic)
/*            expectedIAExceptionThrown = false;            
            try {
                Type<Manufacturer> aType = metamodel.type(Basic.class);
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);            
*/
            // test normal path (subtype = Embeddable)
            expectedIAExceptionThrown = false;            
            try {
                Type<EmbeddedPK> aType = metamodel.type(EmbeddedPK.class);
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);            
            
            // test normal path: (subtype = Entity)
            expectedIAExceptionThrown = false;            
            try {
                Type<Manufacturer> aType = metamodel.type(Manufacturer.class);
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);            

            // test normal path: (subtype = MappedSuperclass)
            expectedIAExceptionThrown = false;            
            try {
                Type<Person> aType = metamodel.type(Person.class);
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);

            // test variant path: null does not cause an IAE in this case because its return type cannot be checked for isManagedType
            expectedIAExceptionThrown = false;
            // Set type to a temporary type - to verify that we get null and not confuse a return of null with an "unset" null.
            Type<?> aTypeFromNullClass = metamodel.type(Manufacturer.class);
            try {
                aTypeFromNullClass = metamodel.type(null);
            } catch (IllegalArgumentException iae) {
                //iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertNull(aTypeFromNullClass);
            assertFalse(expectedIAExceptionThrown);            

            // test variant path: wrong type (java simple type)
            expectedIAExceptionThrown = false;            
            try {
                Type<?> aType = metamodel.embeddable(Integer.class);
            } catch (IllegalArgumentException iae) {
                //iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertTrue(expectedIAExceptionThrown);            

            // test variant path: wrong type (BasicType)

            /**
             *  Return the metamodel embeddable type representing the
             *  embeddable class.
             *  @param cls  the type of the represented embeddable class
             *  @return the metamodel embeddable type
             *  @throws IllegalArgumentException if not an embeddable class
             */
            //<X> EmbeddableType<X> embeddable(Class<X> cls);
            // test normal path
            expectedIAExceptionThrown = false;            
            try {
                EmbeddableType<EmbeddedPK> aType = metamodel.embeddable(EmbeddedPK.class);
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);            
            
            // test variant path: null causes IAE
            expectedIAExceptionThrown = false;            
            try {
                EmbeddableType<Manufacturer> aType = metamodel.embeddable(null);
            } catch (IllegalArgumentException iae) {
                //iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertTrue(expectedIAExceptionThrown);            

            // test variant path: wrong type (subtype = Entity)
            expectedIAExceptionThrown = false;            
            try {
                EmbeddableType<Manufacturer> aType = metamodel.embeddable(Manufacturer.class);
            } catch (IllegalArgumentException iae) {
                //iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertTrue(expectedIAExceptionThrown);            

            // test variant path: wrong type (java simple type)
            expectedIAExceptionThrown = false;            
            try {
                EmbeddableType<?> aType = metamodel.embeddable(Integer.class);
            } catch (IllegalArgumentException iae) {
                //iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertTrue(expectedIAExceptionThrown);            

            // test variant path: wrong type (BasicType)

            /**
             *  Return the metamodel managed types.
             *  @return the metamodel managed types
             */
            //java.util.Set<ManagedType<?>> getManagedTypes();

            /**
             * Return the metamodel entity types.
             * @return the metamodel entity types
             */
            //java.util.Set<EntityType<?>> getEntities();

            /**
             * Return the metamodel embeddable types.
             * @return the metamodel embeddable types
             */
            //java.util.Set<EmbeddableType<?>> getEmbeddables();            
            
            
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

            
            // Test SingularAttribute
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            /**
             * Return the Java type of the represented object.
             * If the bindable type of the object is PLURAL_ATTRIBUTE,
             * the Java element type is returned. If the bindable type is
             * SINGULAR_ATTRIBUTE or ENTITY_TYPE, the Java type of the
             * represented entity or attribute is returned.
             * @return Java type
             */
            //public Class<T> getBindableJavaType() {
            
            
            //public boolean isId() {
            // will test AttributeImpl.getDescriptor
            expectedIAExceptionThrown = false;            
            //Type<EmbeddedPK> anEmbeddableType = null;            
            Type<Integer> anEmbeddableType = null;
            try {
                // Note: type() will only return managedTypes (not BasicTypes)
                //anEmbeddableType = metamodel.type(EmbeddedPK.class);
                // The following call will instantiate a Basic type if it is not found
                anEmbeddableType = ((MetamodelImpl)metamodel).getType(Integer.class);
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
                expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);
            assertNotNull("EmbeddableId type is null", anEmbeddableType);            
            assertNotNull(entityLocation.getAttribute("primaryKey"));
            assertTrue(entityLocation.getAttribute("primaryKey") instanceof SingularAttributeImpl);
            assertTrue(((SingularAttribute)entityLocation.getAttribute("primaryKey")).isId());
            
            

            /** 
             *  Can the attribute be null.
             *  @return boolean indicating whether or not the attribute can
             *          be null
             */
            //public boolean isOptional() {

            //public boolean isPlural() {
            
            /**
             *  Is the attribute a version attribute.
             *  @return boolean indicating whether or not attribute is 
             *          a version attribute
             */
            //public boolean isVersion() {

            //public Bindable.BindableType getBindableType() {
            
            /**
             *  Return the Java type of the represented attribute.
             *  @return Java type
             */
            //public Class<T> getJavaType() {
            
            /**
             * Return the type that represents the type of the attribute.
             * @return type of attribute
             */
             //public Type<T> getType() {
            
            /**
             * Return the String representation of the receiver.
             */
            //public String toString() {
            
            
            

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
            // Runtime behavior should not affect the metamodel
            // MetamodelImpl@15868511 [ 19 Types: , 16 ManagedTypes: , 10 EntityTypes: , 4 MappedSuperclassTypes: , 2 EmbeddableTypes: ]
            //System.out.println("_Metamodel at test end: " + metamodel);
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
