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
 *     06/30/2009-2.0  mobrien - finish JPA Metadata API modifications in support
 *       of the Metamodel implementation for EclipseLink 2.0 release involving
 *       Map, ElementCollection and Embeddable types on MappedSuperclass descriptors
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 *     10/15/2009-2.0  mobrien - 266912 
 *          http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_94:_20091015:_Split_and_Granularize_Test_Suite
 *     16/06/2010-2.2  mobrien - 316991: Attribute.getJavaMember() requires reflective getMethod call
 *       when only getMethodName is available on accessor for attributes of Embeddable types.
 *       see testAttribute_getJavaMember_BasicType_on_Embeddable_Method()
 *       http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_95:_20091017:_Attribute.getJavaMember.28.29_returns_null_for_a_BasicType_on_a_MappedSuperclass_because_of_an_uninitialized_accessor
 *     30/11/2010-2.2  mobrien - 300626: Nested Embeddable testing 
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.metamodel;


import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.MappedSuperclassType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.PluralAttribute.CollectionType;
import javax.persistence.metamodel.Type.PersistenceType;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jpa.metamodel.AttributeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.BasicTypeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.EmbeddableTypeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.EntityTypeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.ManagedTypeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.MapAttributeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.MappedSuperclassTypeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.MetamodelImpl;
import org.eclipse.persistence.internal.jpa.metamodel.PluralAttributeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.SingularAttributeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.TypeImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.testing.models.jpa.metamodel.ArrayProcessor;
import org.eclipse.persistence.testing.models.jpa.metamodel.Board;
import org.eclipse.persistence.testing.models.jpa.metamodel.CPU;
import org.eclipse.persistence.testing.models.jpa.metamodel.CPUEmbeddedId;
import org.eclipse.persistence.testing.models.jpa.metamodel.Computer;
import org.eclipse.persistence.testing.models.jpa.metamodel.Core;
import org.eclipse.persistence.testing.models.jpa.metamodel.Corporation;
import org.eclipse.persistence.testing.models.jpa.metamodel.Designer;
import org.eclipse.persistence.testing.models.jpa.metamodel.EmbeddedPK;
import org.eclipse.persistence.testing.models.jpa.metamodel.Enclosure;
import org.eclipse.persistence.testing.models.jpa.metamodel.EnclosureIdClassPK;
import org.eclipse.persistence.testing.models.jpa.metamodel.GalacticPosition;
import org.eclipse.persistence.testing.models.jpa.metamodel.HardwareDesigner;
import org.eclipse.persistence.testing.models.jpa.metamodel.MSRootPropertyAccess;
import org.eclipse.persistence.testing.models.jpa.metamodel.MS_MS_Entity_Center;
import org.eclipse.persistence.testing.models.jpa.metamodel.MS_MS_Entity_Leaf;
import org.eclipse.persistence.testing.models.jpa.metamodel.MS_MS_Entity_Root;
import org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer;
import org.eclipse.persistence.testing.models.jpa.metamodel.Memory;
import org.eclipse.persistence.testing.models.jpa.metamodel.MultiCoreCPU;
import org.eclipse.persistence.testing.models.jpa.metamodel.Observation;
import org.eclipse.persistence.testing.models.jpa.metamodel.ObservationDetail;
import org.eclipse.persistence.testing.models.jpa.metamodel.Person;
import org.eclipse.persistence.testing.models.jpa.metamodel.Position;
import org.eclipse.persistence.testing.models.jpa.metamodel.Processor;
import org.eclipse.persistence.testing.models.jpa.metamodel.VectorProcessor;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

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
 *   API Usage 
 *   There are three ways to query using the Criteria API which can wrap the Metamodel API
 *   1) Static canonical metamodel class model for type safe queries - these are the _Underscore design time classes
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

    public static final int METAMODEL_ALL_ATTRIBUTES_SIZE = 143;//6;
    // Note: Since BasicTypes are lazy - loaded into the metamodel-types Map - this test must preceed any test that verifies all BasicType objects like "testIdentifiableType_getIdType_Method"
    public static final int METAMODEL_ALL_TYPES = 51;
    public static final int METAMODEL_MANUFACTURER_DECLARED_TYPES = 28;
    // Get # of processor cores (hard cores + hyperthreaded cores)
    public static final int numberProcessingUnits = Runtime.getRuntime().availableProcessors();
    
    public MetamodelMetamodelTest() {
        super();
    }
    
    public MetamodelMetamodelTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("MetamodelMetamodelTest");
        /**
         * 300051: EmbeddedId directly on MappedSuperclass
         * In the following 2 tests we verify that when an EmbeddedId is on a MappedSuperclass - we do not
         * use the non-persisting MAPPED_SUPERCLASS_RESERVED_PK_NAME PK field.
         * Normally when the MappedSuperclass is part of an inheritance hierarchy of the form MS->MS->E,
         * where there is an PK Id on the root Entity E, we need to add this key solely for metadata processing to complete.
         * Why? because even though we treat MappedSuperclass objects as a RelationalDescriptor - we only persist
         * RelationalDescriptor objects that relate to concrete Entities.
         */
        if (! JUnitTestCase.isJPA10()) {
            suite.addTest(new MetamodelMetamodelTest("testMetamodelEmbeddedIdDirectlyOnMappedSuperclassRootPassesValidation"));
            // Note: Since BasicTypes are lazy - loaded into the metamodel-types Map - this test must preceed any test that verifies all BasicType objects like "testIdentifiableType_getIdType_Method"
            suite.addTest(new MetamodelMetamodelTest("testTransientNonEntityNonMappedSuperclass_SuperclassOfEntity_Exists_as_BasicType"));
            suite.addTest(new MetamodelMetamodelTest("testAttribute_getAttribute_of_TransientNonEntityNonMappedSuperclass_SuperclassOfEntity_throws_IAE"));
            suite.addTest(new MetamodelMetamodelTest("testAttribute_getPersistentAttributeType_BASIC_Method"));
            suite.addTest(new MetamodelMetamodelTest("testAttribute_getPersistentAttributeType_EMBEDDED_Method"));
            suite.addTest(new MetamodelMetamodelTest("testAttribute_getPersistentAttributeType_ONE_TO_ONE_Method"));
            suite.addTest(new MetamodelMetamodelTest("testAttribute_getPersistentAttributeType_ONE_TO_MANY_Method"));
            suite.addTest(new MetamodelMetamodelTest("testAttribute_getPersistentAttributeType_MANY_TO_MANY_Method"));
            suite.addTest(new MetamodelMetamodelTest("testAttribute_getPersistentAttributeType_MANY_TO_ONE_Method"));
            suite.addTest(new MetamodelMetamodelTest("testAttribute_getPersistentAttributeType_ELEMENT_COLLECTION_Method"));        
            suite.addTest(new MetamodelMetamodelTest("testAttribute_getName_Method"));
            suite.addTest(new MetamodelMetamodelTest("testAttribute_getDeclaringType_Method"));
            suite.addTest(new MetamodelMetamodelTest("testAttribute_getJavaType_BasicType_Method"));
            suite.addTest(new MetamodelMetamodelTest("testAttribute_getJavaType_ManagedType_Method"));
            suite.addTest(new MetamodelMetamodelTest("testAttribute_getJavaMember_BasicType_on_MappedSuperclass_Method"));
            suite.addTest(new MetamodelMetamodelTest("testAttribute_getJavaMember_BasicType_FieldAccess_on_Embeddable_Method")); // 316991
            suite.addTest(new MetamodelMetamodelTest("testAttribute_getJavaMember_BasicType_PropertyAccess_on_Embeddable_Method")); // 316991            
            suite.addTest(new MetamodelMetamodelTest("testAttribute_getJavaMember_BasicType_on_Entity_Method"));
            suite.addTest(new MetamodelMetamodelTest("testAttribute_getJavaMember_EntityType_FieldLevel_on_Entity_Method")); // 316991            
            suite.addTest(new MetamodelMetamodelTest("testAttribute_getJavaMember_EntityType_FieldLevel_on_MappedSuperclass_Method")); // 316991
            suite.addTest(new MetamodelMetamodelTest("testAttribute_getJavaMember_EntityType_PropertyMethodLevel_on_Entity_Method")); // 316991
            suite.addTest(new MetamodelMetamodelTest("testAttribute_getJavaMember_EntityType_PropertyMethodLevel_on_MappedSuperclass_Method")); // 316991            
            suite.addTest(new MetamodelMetamodelTest("testAttribute_getJavaMember_ManagedType_Method"));        
            suite.addTest(new MetamodelMetamodelTest("testAttribute_isAssociation_on_Plural_Method"));
            suite.addTest(new MetamodelMetamodelTest("testAttribute_isAssociation_on_Singular_Method"));
            suite.addTest(new MetamodelMetamodelTest("testAttribute_isCollection_false_Method"));
            suite.addTest(new MetamodelMetamodelTest("testAttribute_isCollection_true_Method"));
            
            suite.addTest(new MetamodelMetamodelTest("testBasicType"));
            suite.addTest(new MetamodelMetamodelTest("testBindable_getBindableType_Method"));
            suite.addTest(new MetamodelMetamodelTest("testBindable_getBindableJavaType_Method"));
            suite.addTest(new MetamodelMetamodelTest("testEmbeddableType"));
            suite.addTest(new MetamodelMetamodelTest("testNestedEmbeddableType"));  // 300626           
            suite.addTest(new MetamodelMetamodelTest("testEntityType"));
            suite.addTest(new MetamodelMetamodelTest("testEntityAttribute_getBindableJavaType_Method"));          
            // Note: Since BasicTypes are lazy - loaded into the metamodel-types Map - this test must proceed any test that first gets the Position class BasicType object - see  like "testTransientNonEntityNonMappedSuperclass_SuperclassOfEntity_Exists_as_BasicType"
            // You will get a lower number here - if only this single test is run via the Testing Browser            
            suite.addTest(new MetamodelMetamodelTest("testIdentifiableType_getIdType_Method"));
            suite.addTest(new MetamodelMetamodelTest("testIdentifiableType_getIdClassAttributes_Method"));
            suite.addTest(new MetamodelMetamodelTest("testIdentifiableType_getIdClassAttributesAcrossMappedSuperclassChain_Method")); // 288792        
            suite.addTest(new MetamodelMetamodelTest("testIdentifiableType_hasVersionAttribute_Method"));
            suite.addTest(new MetamodelMetamodelTest("testIdentifiableType_hasSingleIdAttribute_Method"));
            suite.addTest(new MetamodelMetamodelTest("testIdentifiableType_getSupertype_Method"));
            suite.addTest(new MetamodelMetamodelTest("testIdentifiableType_getDeclaredVersion_exists_Method"));
            suite.addTest(new MetamodelMetamodelTest("testIdentifiableType_getDeclaredVersion_exists_above_Method"));
            suite.addTest(new MetamodelMetamodelTest("testIdentifiableType_getDeclaredVersion_does_not_exist_at_all_Method"));
            suite.addTest(new MetamodelMetamodelTest("testIdentifiableType_getVersion_Method"));
            suite.addTest(new MetamodelMetamodelTest("testIdentifiableType_getDeclaredId_Method"));
            suite.addTest(new MetamodelMetamodelTest("testIdentifiableType_getId_Method"));        
            suite.addTest(new MetamodelMetamodelTest("testIdentifiableType_getDeclaredId_normal_execution_attribute_is_declared"));
            suite.addTest(new MetamodelMetamodelTest("testIdentifiableType_getDeclaredId_variant_execution_attribute_is_declared_above"));
            suite.addTest(new MetamodelMetamodelTest("testIdentifiableType_getDeclaredId_variant_execution_attribute_is_not_declared_at_all"));
            suite.addTest(new MetamodelMetamodelTest("testIdentifiableType_getDeclaredId_normal_execution_attribute_is_declared"));
            suite.addTest(new MetamodelMetamodelTest("testIdentifiableType_getIdType_handles_possible_null_cmppolicy"));
            
            suite.addTest(new MetamodelMetamodelTest("testListAttribute"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getAttributes_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getDeclaredAttributes_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getSingularAttribute_Type_param_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getDeclaredSingularAttribute_Type_param_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getSingularAttributes_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getDeclaredSingularAttributes_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getCollection_Type_param_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getDeclaredCollection_Type_param_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getSet_Type_param_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getDeclaredSet_Type_param_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getList_Type_param_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getDeclaredList_Type_param_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getMap_Type_param_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getDeclaredMap_Type_param_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getDeclaredMap_Type_param_not_found_iae_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getDeclaredMap_Type_param_declared_above_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getPluralAttributes_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getDeclaredPluralAttributes_internal_entity_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getDeclaredPluralAttributes_root_entity_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getDeclaredPluralAttributes_root_mappedSuperclass_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getAttribute_on_Entity_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getAttribute_on_MappedSuperclass_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getAttribute_doesNotExist_on_Entity_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getAttribute_doesNotExist_on_MappedSuperclass_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getDeclaredAttribute_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getDeclaredListAttribute_Method"));            
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getDeclaredAttribute_above_throws_iae_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getDeclaredAttribute_doesNotExist_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getSingularAttribute_BASIC_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getSingularAttribute_EMBEDDED_Method"));        
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getDeclaredSingularAttribute_on_Entity_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getDeclaredSingularAttribute_on_MappedSuperclass_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getCollection_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getDeclaredCollection_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getSet_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getDeclaredSet_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getList_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getDeclaredList_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getMap_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_getDeclaredMap_Method"));
            suite.addTest(new MetamodelMetamodelTest("testManagedType_variantCases"));        
            suite.addTest(new MetamodelMetamodelTest("testMapAttribute_getKeyJavaType_UC0_Method"));
            suite.addTest(new MetamodelMetamodelTest("testMapAttribute_getKeyJavaType_UC1a_Method"));
            suite.addTest(new MetamodelMetamodelTest("testMapAttribute_getKeyJavaType_UC2_Method"));
            suite.addTest(new MetamodelMetamodelTest("testMapAttribute_getKeyJavaType_UC4_Method"));
            suite.addTest(new MetamodelMetamodelTest("testMapAttribute_getKeyJavaType_UC7_Method"));
            suite.addTest(new MetamodelMetamodelTest("testMapAttribute_getKeyJavaType_UC8_Method"));
            // http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_86:_20090921:_Handle_Embeddable_Type_keyType_in_MapAttributeImpl_constructor       
            suite.addTest(new MetamodelMetamodelTest("testMapAttribute_getKeyJavaType_UC9_DI86_Embeddable_IdClass_keyType_Method"));
            suite.addTest(new MetamodelMetamodelTest("testMapAttribute_getKeyType_UC0_Method"));
            suite.addTest(new MetamodelMetamodelTest("testMapAttribute_getKeyType_UC1a_Method"));
            suite.addTest(new MetamodelMetamodelTest("testMapAttribute_getKeyType_UC2_Method"));
            suite.addTest(new MetamodelMetamodelTest("testMapAttribute_getKeyType_UC4_Method"));
            suite.addTest(new MetamodelMetamodelTest("testMapAttribute_getKeyType_UC7_Method"));
            suite.addTest(new MetamodelMetamodelTest("testMapAttribute_getKeyType_UC8_Method"));
            suite.addTest(new MetamodelMetamodelTest("testMapAttribute_getKeyType_UC9_DI86_Embeddable_IdClass_keyType_Method"));
            // These 3 test verify the workaround for 294811
            suite.addTest(new MetamodelMetamodelTest("testMapAttribute_getKeyType_294811_UC10_DI86_Embeddable_IdClass_keyType_Method"));
            suite.addTest(new MetamodelMetamodelTest("testMapAttribute_getKeyType_294811_UC12_DI86_Embedded_keyType_Method"));
            suite.addTest(new MetamodelMetamodelTest("testMapAttribute_getKeyType_294811_UC13_DI86_Embedded_keyType_Method"));
            suite.addTest(new MetamodelMetamodelTest("testMapAtributeElementTypeWhenMapKeySetButNameAttributeIsDefaulted"));
            suite.addTest(new MetamodelMetamodelTest("testMapAtributeElementTypeWhenMapKeySetAndNameAttributeSet"));
            // Pending implementation
            //suite.addTest(new MetamodelMetamodelTest("testMapAtributeElementTypeWhenMapKeyNotSet_MapKeyColumnSet"));
            //suite.addTest(new MetamodelMetamodelTest("testMapAtributeElementTypeWhenMapKeyNotSet_MapKeyColumnSet")); // JPA 2.0 specific
        
            suite.addTest(new MetamodelMetamodelTest("testMappedSuperclassType"));
            suite.addTest(new MetamodelMetamodelTest("testMetamodel_entity_Method"));
            suite.addTest(new MetamodelMetamodelTest("testMetamodel_embeddable_Method"));
            suite.addTest(new MetamodelMetamodelTest("testMetamodel_managedType_Method"));
            suite.addTest(new MetamodelMetamodelTest("testMetamodel_getEntities_Method"));
            suite.addTest(new MetamodelMetamodelTest("testMetamodel_getManagedTypes_Method"));
            suite.addTest(new MetamodelMetamodelTest("testMetamodel_getEmbeddables_Method"));
            suite.addTest(new MetamodelMetamodelTest("testPluralAttribute_CollectionType_enum"));
            suite.addTest(new MetamodelMetamodelTest("testPluralAttribute_getCollectionType_Method"));
            suite.addTest(new MetamodelMetamodelTest("testPluralAttribute_getElementType_Method"));
            suite.addTest(new MetamodelMetamodelTest("testPluralAttribute_getBindableJavaType_Method"));
            suite.addTest(new MetamodelMetamodelTest("testSetAttribute"));
            suite.addTest(new MetamodelMetamodelTest("testSingularAttribute_isOptional_Method"));
            suite.addTest(new MetamodelMetamodelTest("testSingularAttribute_isId_Method"));
            suite.addTest(new MetamodelMetamodelTest("testSingularAttribute_isVersion_Method"));
            suite.addTest(new MetamodelMetamodelTest("testSingularAttribute_getBindableType_Method"));
            suite.addTest(new MetamodelMetamodelTest("testSingularAttribute_getBindableJavaType_Method"));
            suite.addTest(new MetamodelMetamodelTest("testSingularAttribute_getJavaType_Method"));
            suite.addTest(new MetamodelMetamodelTest("testSingularAttribute_getType_Method"));
            suite.addTest(new MetamodelMetamodelTest("testToStringOverrides"));        
            suite.addTest(new MetamodelMetamodelTest("testType_PersistenceType_enum"));
            suite.addTest(new MetamodelMetamodelTest("testType_getPersistenceType_Method"));
            suite.addTest(new MetamodelMetamodelTest("testType_getJavaType_Method"));
            suite.addTest(new MetamodelMetamodelTest("testOutOfSpecificationInternalAPI"));
            // Not implemented yet
            //suite.addTest(new MetamodelMetamodelTest("testObscureInvalidStateUnitTests"));
        }
        return suite;
    }
    
    public void setUp() {
        super.setUp();
    }
    
    /**
     * Perform a test by "Not" caching the EMF and letting the test close the EMF to 
     * verify variant use cases that may throw an IAE on a closed EntityManger
     * @param overrideEMFCachingForTesting
     * @return
     */
    private EntityManager privateTestSetup(boolean overrideEMFCachingForTesting) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        boolean exceptionThrown = false;
        Metamodel metamodel = null;
        
        try {
            emf = initialize(overrideEMFCachingForTesting);
            em = emf.createEntityManager();
            
            metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
        } catch (Exception e) {
            e.printStackTrace();
            cleanup(em);
        }
        return em;
    }
    
    private EntityManager privateTestSetup() {
        return privateTestSetup(false);
    }
    
    private void privateTestTeardown() {        
    }

    // 300051: Test is that the class passes processing in the testing browser - verify that the model has loaded here as well
    public void testMetamodelEmbeddedIdDirectlyOnMappedSuperclassRootPassesValidation() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            // Verify our classes involved in an @EmbeddedId on a @MappedSuperclass loaded
            ManagedType<CPU> msCPU = metamodel.managedType(CPU.class);            
            assertNotNull(msCPU);
            assertEquals(Type.PersistenceType.MAPPED_SUPERCLASS, msCPU.getPersistenceType());
            
            ManagedType<MultiCoreCPU> eMultiCoreCPU = metamodel.managedType(MultiCoreCPU.class);            
            assertNotNull(eMultiCoreCPU);
            assertEquals(Type.PersistenceType.ENTITY, eMultiCoreCPU.getPersistenceType());
            
            ManagedType<Core> eCore = metamodel.managedType(Core.class);            
            assertNotNull(eCore);
            assertEquals(Type.PersistenceType.ENTITY, eCore.getPersistenceType());

            ManagedType<CPUEmbeddedId> embedCPUEmbeddedId = metamodel.managedType(CPUEmbeddedId.class);            
            assertNotNull(embedCPUEmbeddedId);
            assertEquals(Type.PersistenceType.EMBEDDABLE, embedCPUEmbeddedId.getPersistenceType());
            // 316991: DI 95: Handle case where getMethod is not set - but getMethodName is
            // http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_95:_20091017:_Attribute.getJavaMember.28.29_returns_null_for_a_BasicType_on_a_MappedSuperclass_because_of_an_uninitialized_accessor
            Attribute embeddedAttribute = embedCPUEmbeddedId.getAttribute("pk_part1");
            assertNotNull(embeddedAttribute);
            Member aMember = embeddedAttribute.getJavaMember();
            assertNotNull(aMember);
            assertNotNull("id java member is null", aMember);
            assertTrue("id attribute java member is not an instance of Method", aMember instanceof Method);
            assertEquals("getPk_part1", aMember.getName());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }
    
    
    /**
     * There is no simple way to test the predeploy() for this issue - however we can
     * verify that the MS - MS - Entity chain is loaded correctly.
     * 
     */
    public void testValidation_relaxed_for_composite_pk_on_mappedSuperclass_chain() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            ManagedType<Person> msPerson = metamodel.managedType(Person.class);            
            assertNotNull(msPerson);
            assertEquals(Type.PersistenceType.MAPPED_SUPERCLASS, msPerson.getPersistenceType());            
        } catch (IllegalArgumentException iae) {
            //iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }
    
    
    // http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_93:_20091014:_Single_PK_support_in_IdentifiableTypeImpl.getIdType.28.29_does_not_fully_handle_a_null_CMPPolicy_on_the_Descriptor
    public void testIdentifiableType_getIdType_handles_possible_null_cmppolicy() {
        boolean exceptionThrown = false;
        EntityManager em = null;            
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
        boolean expectedIAExceptionThrown = false;
            Type<?> personIdType = null;
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);
            
            personIdType = msPerson_.getIdType();
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
            assertNotNull(personIdType);
            assertEquals(PersistenceType.BASIC, personIdType.getPersistenceType());
            assertEquals(Integer.class, personIdType.getJavaType());
        } catch (IllegalArgumentException iae) {
            // expecting no exception
            iae.printStackTrace();
            exceptionThrown = true;            
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur on getIdType() here.", exceptionThrown);
        }
    }
    
    /**
     * These tests are for states that should never be reached by API implementors.
     */
    // TODO: This is not testing anything yet.
    public void testObscureInvalidStateUnitTests() {
        boolean exceptionThrown = false;
        EntityManager em = null;            
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            
            // Test toString() for a Type without a javaType attribute
            //Type invalidType = new BasicTypeImpl(null);
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    public void testIdentifiableType_getDeclaredId_variant_execution_attribute_is_declared_above() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            ManagedType<Manufacturer> entityManufacturer_ = metamodel.managedType(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            assertEquals(Type.PersistenceType.ENTITY, entityManufacturer_.getPersistenceType());

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
            // Not declared  - invalid - IAE thrown (actually declared 2 levels above in "Person")
            Attribute idAttribute = ((IdentifiableType)entityManufacturer_).getDeclaredId(Integer.class);
        } catch (IllegalArgumentException iae) {
            //iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertTrue("non-declared attribute should throw an IAE", exceptionThrown);
        }
    }

    // TODO: This is not testing anything yet.
    public void testIdentifiableType_getDeclaredId_variant_execution_attribute_is_not_declared_at_all() {
    }
    
    public void testIdentifiableType_getDeclaredId_normal_execution_attribute_is_declared() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            ManagedType<Person> msPerson = metamodel.managedType(Person.class);            
            assertNotNull(msPerson);
            assertEquals(Type.PersistenceType.MAPPED_SUPERCLASS, msPerson.getPersistenceType());            

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
            // declared  - valid
            Attribute idAttribute = ((IdentifiableType)msPerson).getDeclaredId(Integer.class);            
            assertNotNull(idAttribute);
            assertEquals(Integer.class, idAttribute.getJavaType());
            // declared and valid
            
            //*********************************************/
            // FUTURE: Require a version on a MappedSuperclass            
            //assertNotNull(msPerson1_.getDeclaredId(Integer.class)); - keep commented
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }
    
    public void testAttribute_getPersistentAttributeType_BASIC_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            assertEquals(Type.PersistenceType.ENTITY, entityManufacturer_.getPersistenceType());

            // Actual Test Case
            /*public static enum PersistentAttributeType {
            MANY_TO_ONE, ONE_TO_ONE, BASIC, EMBEDDED,
            MANY_TO_MANY, ONE_TO_MANY, ELEMENT_COLLECTION
            }*/
            
            /**
             *  Return the persistent attribute type for the attribute.
             *  @return persistent attribute type
             */
            //PersistentAttributeType getPersistentAttributeType();
            
            Attribute anAttribute = entityManufacturer_.getAttribute("anInt");            
            assertNotNull(anAttribute);
            assertEquals(int.class, anAttribute.getJavaType());
            assertEquals(PersistentAttributeType.BASIC, anAttribute.getPersistentAttributeType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    public void testAttribute_getPersistentAttributeType_ONE_TO_ONE_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);

            // Actual Test Case
            /*public static enum PersistentAttributeType {
            MANY_TO_ONE, ONE_TO_ONE, BASIC, EMBEDDED,
            MANY_TO_MANY, ONE_TO_MANY, ELEMENT_COLLECTION
            }*/
            
            /**
             *  Return the persistent attribute type for the attribute.
             *  @return persistent attribute type
             */
            //PersistentAttributeType getPersistentAttributeType();
            
            Attribute anAttribute = entityComputer_.getAttribute("location");            
            assertNotNull(anAttribute);
            assertEquals(GalacticPosition.class, anAttribute.getJavaType());            
            assertEquals(PersistentAttributeType.ONE_TO_ONE, anAttribute.getPersistentAttributeType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    public void testAttribute_getPersistentAttributeType_ONE_TO_MANY_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            assertEquals(Type.PersistenceType.ENTITY, entityManufacturer_.getPersistenceType());

            // Actual Test Case
            /*public static enum PersistentAttributeType {
            MANY_TO_ONE, ONE_TO_ONE, BASIC, EMBEDDED,
            MANY_TO_MANY, ONE_TO_MANY, ELEMENT_COLLECTION
            }*/
            
            /**
             *  Return the persistent attribute type for the attribute.
             *  @return persistent attribute type
             */
            //PersistentAttributeType getPersistentAttributeType();
            
            Attribute anAttribute = entityManufacturer_.getAttribute("computers");            
            assertNotNull(anAttribute);
            assertTrue(((AttributeImpl)anAttribute).isPlural());
            assertTrue(anAttribute instanceof PluralAttribute);
            Bindable aPluralAttribute = entityManufacturer_.getSet("computers");
            assertEquals(Computer.class, aPluralAttribute.getBindableJavaType());
            assertEquals(Set.class, anAttribute.getJavaType());
            assertEquals(PersistentAttributeType.ONE_TO_MANY, anAttribute.getPersistentAttributeType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    // TODO: This is not testing anything yet.
    public void testAttribute_getPersistentAttributeType_MANY_TO_MANY_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);

            // Actual Test Case
            /*public static enum PersistentAttributeType {
            MANY_TO_ONE, ONE_TO_ONE, BASIC, EMBEDDED,
            MANY_TO_MANY, ONE_TO_MANY, ELEMENT_COLLECTION
            }*/
            
            /**
             *  Return the persistent attribute type for the attribute.
             *  @return persistent attribute type
             */
            //PersistentAttributeType getPersistentAttributeType();
            // note: not implemented yet - need a manyToMany in the model
            //assertEquals(PersistentAttributeType.MANY_TO_MANY, anAttribute.getPersistentAttributeType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    public void testAttribute_getPersistentAttributeType_MANY_TO_ONE_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Board> entityBoard_ = (EntityTypeImpl)metamodel.entity(Board.class);
            assertNotNull(entityBoard_);
            assertEquals(Type.PersistenceType.ENTITY, entityBoard_.getPersistenceType());

            // Actual Test Case
            /*public static enum PersistentAttributeType {
            MANY_TO_ONE, ONE_TO_ONE, BASIC, EMBEDDED,
            MANY_TO_MANY, ONE_TO_MANY, ELEMENT_COLLECTION
            }*/
            
            /**
             *  Return the persistent attribute type for the attribute.
             *  @return persistent attribute type
             */
            //PersistentAttributeType getPersistentAttributeType();
            
            Attribute anAttribute = entityBoard_.getAttribute("computer");            
            assertNotNull(anAttribute);
            assertEquals(Computer.class, anAttribute.getJavaType());
            // Note: internally our MANY_TO_ONE is treated as a ONE_TO_ONE - although with a DB constraint 
            assertEquals(PersistentAttributeType.MANY_TO_ONE, anAttribute.getPersistentAttributeType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    // TODO: This is not testing anything yet.
    public void testAttribute_getPersistentAttributeType_ELEMENT_COLLECTION_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            assertEquals(Type.PersistenceType.ENTITY, entityManufacturer_.getPersistenceType());

            // Actual Test Case
            /*public static enum PersistentAttributeType {
            MANY_TO_ONE, ONE_TO_ONE, BASIC, EMBEDDED,
            MANY_TO_MANY, ONE_TO_MANY, ELEMENT_COLLECTION
            }*/
            
            /**
             *  Return the persistent attribute type for the attribute.
             *  @return persistent attribute type
             */
            //PersistentAttributeType getPersistentAttributeType();
            
            Attribute anAttribute = entityManufacturer_.getAttribute("computers");
            // FUTURE: Not Implemented
            //assertNotNull(anAttribute);
            //assertEquals(Computer.class, anAttribute.getJavaType());            
            //assertEquals(PersistentAttributeType.ELEMENT_COLLECTION, anAttribute.getPersistentAttributeType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    public void testAttribute_getPersistentAttributeType_EMBEDDED_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<GalacticPosition> entityGalacticPosition_ = (EntityTypeImpl)metamodel.entity(GalacticPosition.class);
            assertNotNull(entityGalacticPosition_);
            assertEquals(Type.PersistenceType.ENTITY, entityGalacticPosition_.getPersistenceType());

            // Actual Test Case
            /*public static enum PersistentAttributeType {
            MANY_TO_ONE, ONE_TO_ONE, BASIC, EMBEDDED,
            MANY_TO_MANY, ONE_TO_MANY, ELEMENT_COLLECTION
            }*/
            
            /**
             *  Return the persistent attribute type for the attribute.
             *  @return persistent attribute type
             */
            //PersistentAttributeType getPersistentAttributeType();
            
            Attribute anAttribute = entityGalacticPosition_.getAttribute("primaryKey");
            assertNotNull(anAttribute);
            assertEquals(EmbeddedPK.class, anAttribute.getJavaType());            
            assertEquals(PersistentAttributeType.EMBEDDED, anAttribute.getPersistentAttributeType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }
    
    public void testAttribute_getName_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);

            /**
             * Return the name of the attribute.
             * @return name
             */
            //String getName();
            
            ManagedType<Person> msPerson = metamodel.managedType(Person.class);            
            assertNotNull(msPerson);
            assertEquals(Type.PersistenceType.MAPPED_SUPERCLASS, msPerson.getPersistenceType());            
            Attribute idAttribute = ((IdentifiableType)msPerson).getDeclaredId(Integer.class);            
            assertNotNull(idAttribute);
            assertEquals(Integer.class, idAttribute.getJavaType());
            assertEquals("id", idAttribute.getName());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    public void testAttribute_getDeclaringType_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);
            
            /**
             *  Return the managed type representing the type in which 
             *  the attribute was declared.
             *  @return declaring type
             */
            //ManagedType<X> getDeclaringType();
            
            // Test case
            Attribute anAttribute = entityManufacturer_.getDeclaredAttribute("hardwareDesignersMapUC4");
            ManagedType aManagedType = anAttribute.getDeclaringType();
            assertEquals(entityManufacturer_, aManagedType);            
            assertNotNull(anAttribute);
            assertEquals(PersistentAttributeType.ONE_TO_MANY, anAttribute.getPersistentAttributeType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    public void testAttribute_getJavaType_BasicType_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);

            /**
             *  Return the Java type of the represented attribute.
             *  @return Java type
             */
            //Class<Y> getJavaType();
            ManagedType<Person> msPerson = metamodel.managedType(Person.class);            
            assertNotNull(msPerson);
            assertEquals(Type.PersistenceType.MAPPED_SUPERCLASS, msPerson.getPersistenceType());            
            Attribute idAttribute = ((IdentifiableType)msPerson).getDeclaredId(Integer.class);            
            assertNotNull(idAttribute);
            assertEquals(Integer.class, idAttribute.getJavaType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    public void testAttribute_getJavaType_ManagedType_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);

            /**
             *  Return the Java type of the represented attribute.
             *  @return Java type
             */
            //Class<Y> getJavaType();
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            assertEquals(Type.PersistenceType.ENTITY, entityManufacturer_.getPersistenceType());
            Attribute anAttribute = entityManufacturer_.getAttribute("computers");            
            assertNotNull(anAttribute);
            assertTrue(((AttributeImpl)anAttribute).isPlural());
            assertTrue(anAttribute instanceof PluralAttribute);
            Bindable aPluralAttribute = entityManufacturer_.getSet("computers");
            assertEquals(Computer.class, aPluralAttribute.getBindableJavaType());
            assertEquals(Set.class, anAttribute.getJavaType());
            assertEquals(PersistentAttributeType.ONE_TO_MANY, anAttribute.getPersistentAttributeType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }
    
    public void testAttribute_getJavaMember_BasicType_on_MappedSuperclass_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            ManagedType<Person> msPerson = metamodel.managedType(Person.class);            
            assertNotNull(msPerson);
            assertEquals(Type.PersistenceType.MAPPED_SUPERCLASS, msPerson.getPersistenceType());            

            /**
             *  Return the java.lang.reflect.Member for the represented 
             *  attribute.
             *  @return corresponding java.lang.reflect.Member
             */
            //java.lang.reflect.Member getJavaMember();
            Attribute idAttribute = ((IdentifiableType)msPerson).getDeclaredId(Integer.class);            
            assertNotNull(idAttribute);
            assertEquals(Integer.class, idAttribute.getJavaType());
            // For primitive and basic types - we should not return null - the attributeAccessor on the MappedSuperclass is not initialized - see
            // http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_95:_20091017:_Attribute.getJavaMember.28.29_returns_null_for_a_BasicType_on_a_MappedSuperclass_because_of_an_uninitialized_accessor
            Member aMember = idAttribute.getJavaMember();
            assertNotNull("id java member is null", aMember);
            assertTrue("id attribute java member is not an instance of Field", aMember instanceof Field);
            assertEquals("id", aMember.getName());
            assertNotNull(((Field)aMember).getType());
            assertEquals(Integer.class.getName(), ((Field)aMember).getType().getName());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    // 316991: handle missing getMethod on embeddables by using the getMethodName
    public void testAttribute_getJavaMember_BasicType_FieldAccess_on_Embeddable_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);

            /**
             *  Return the java.lang.reflect.Member for the represented attribute.
             *  @return corresponding java.lang.reflect.Member
             */
            //java.lang.reflect.Member getJavaMember();
            ManagedType<EmbeddedPK> embedableAttribute = metamodel.managedType(EmbeddedPK.class);            
            assertNotNull(embedableAttribute);
            assertEquals(Type.PersistenceType.EMBEDDABLE, embedableAttribute.getPersistenceType());
            // 316991: DI 95: Handle case where getMethod is not set - but getMethodName is
            // http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_95:_20091017:_Attribute.getJavaMember.28.29_returns_null_for_a_BasicType_on_a_MappedSuperclass_because_of_an_uninitialized_accessor
            Attribute embeddedAttribute = embedableAttribute.getAttribute("pk_part1");
            assertNotNull(embeddedAttribute);
            // The following line exercises the 2nd field part for non-MappedSuperclasses in getJavaMember()
            Member aMember = embeddedAttribute.getJavaMember();
            assertNotNull(aMember); // Fix for missing getMethod in 316991
            assertNotNull("pk_part1 java member is null", aMember);
            assertTrue("pk_part1 attribute java member is not an instance of Method", aMember instanceof Field);
            assertEquals("pk_part1", aMember.getName());
            assertNotNull(((Field)aMember).getType());
            assertEquals(int.class.getName(), ((Field)aMember).getType().getName());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }
    
    // 316991: handle missing getMethod on embeddables by using the getMethodName
    public void testAttribute_getJavaMember_BasicType_PropertyAccess_on_Embeddable_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);

            /**
             *  Return the java.lang.reflect.Member for the represented attribute.
             *  @return corresponding java.lang.reflect.Member
             */
            //java.lang.reflect.Member getJavaMember();
            ManagedType<CPUEmbeddedId> embedCPUEmbeddedId = metamodel.managedType(CPUEmbeddedId.class);            
            assertNotNull(embedCPUEmbeddedId);
            assertEquals(Type.PersistenceType.EMBEDDABLE, embedCPUEmbeddedId.getPersistenceType());
            // 316991: DI 95: Handle case where getMethod is not set - but getMethodName is
            // http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_95:_20091017:_Attribute.getJavaMember.28.29_returns_null_for_a_BasicType_on_a_MappedSuperclass_because_of_an_uninitialized_accessor
            Attribute embeddedAttribute = embedCPUEmbeddedId.getAttribute("pk_part1");
            assertNotNull(embeddedAttribute);
            Member aMember = embeddedAttribute.getJavaMember();
            assertNotNull(aMember); // Fix for missing getMethod in 316991
            assertNotNull("id java member is null", aMember);
            assertTrue("id attribute java member is not an instance of Method", aMember instanceof Method);
            assertEquals("getPk_part1", aMember.getName());
            assertEquals(int.class, ((Method)aMember).getReturnType());            
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }
    
    public void testAttribute_getJavaMember_BasicType_on_Entity_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            assertEquals(Type.PersistenceType.ENTITY, entityManufacturer_.getPersistenceType());

            /**
             *  Return the java.lang.reflect.Member for the represented 
             *  attribute.
             *  @return corresponding java.lang.reflect.Member
             */
            //java.lang.reflect.Member getJavaMember();
            Attribute anAttribute = entityManufacturer_.getAttribute("anInt");            
            assertNotNull(anAttribute);
            assertEquals(int.class, anAttribute.getJavaType());
            // For primitive and basic types - we should not return null - the attributeAccessor on the MappedSuperclass is not initialized - see
            // http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_95:_20091017:_Attribute.getJavaMember.28.29_returns_null_for_a_BasicType_on_a_MappedSuperclass_because_of_an_uninitialized_accessor
            Member aMember = anAttribute.getJavaMember();
            assertNotNull("anInt java member is null", aMember);
            assertTrue("anInt attribute java member is not an instance of Field", aMember instanceof Field);
            assertEquals("anInt", aMember.getName());
            assertNotNull(((Field)aMember).getType());
            assertEquals(int.class.getName(), ((Field)aMember).getType().getName());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    // 316991: regression test getJavaMember() on field level access Entity attribute @OneToOne on an Entity
    public void testAttribute_getJavaMember_EntityType_FieldLevel_on_Entity_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            assertEquals(Type.PersistenceType.ENTITY, entityComputer_.getPersistenceType());

            /**
             *  Return the java.lang.reflect.Member for the represented 
             *  attribute.
             *  @return corresponding java.lang.reflect.Member
             */
            //java.lang.reflect.Member getJavaMember();
            Attribute anAttribute = entityComputer_.getAttribute("location");            
            assertNotNull(anAttribute);
            assertEquals(GalacticPosition.class, anAttribute.getJavaType());
            Member aMember = anAttribute.getJavaMember();
            assertNotNull("location java member is null", aMember);
            assertTrue("location attribute java member is not an instance of Field", aMember instanceof Field);
            assertEquals("location", aMember.getName());
            assertNotNull(((Field)aMember).getType());
            assertEquals(GalacticPosition.class.getName(), ((Field)aMember).getType().getName());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    // 316991: regression test getJavaMember() on field level access Entity attribute unidirectional @OneToOne on a MappedSuperclass
    // This test exercises special handling code late in getJavaMember() for MappedSuperclasses 
    public void testAttribute_getJavaMember_EntityType_FieldLevel_on_MappedSuperclass_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            MappedSuperclassTypeImpl<Designer> msDesigner_ = (MappedSuperclassTypeImpl)metamodel.managedType(Designer.class);
            assertNotNull(msDesigner_);
            assertEquals(Type.PersistenceType.MAPPED_SUPERCLASS, msDesigner_.getPersistenceType());

            /**
             *  Return the java.lang.reflect.Member for the represented 
             *  attribute.
             *  @return corresponding java.lang.reflect.Member
             */
            //java.lang.reflect.Member getJavaMember();
            Attribute anAttribute = msDesigner_.getAttribute("primaryEmployer");            
            assertNotNull(anAttribute);
            assertEquals(Manufacturer.class, anAttribute.getJavaType());
            Member aMember = anAttribute.getJavaMember();
            assertNotNull("primaryEmployer java member is null", aMember);
            assertTrue("primaryEmployer attribute java member is not an instance of Field", aMember instanceof Field);
            assertEquals("primaryEmployer", aMember.getName());
            assertNotNull(((Field)aMember).getType());
            assertEquals(Manufacturer.class.getName(), ((Field)aMember).getType().getName());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    // 316991: regression test getJavaMember() on method level access Entity attribute unidirectional @OneToOne on an Entity
    public void testAttribute_getJavaMember_EntityType_PropertyMethodLevel_on_Entity_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityType<VectorProcessor> anEntity_ = metamodel.entity(VectorProcessor.class);
            assertNotNull(anEntity_);
            assertEquals(Type.PersistenceType.ENTITY, anEntity_.getPersistenceType());

            /**
             *  Return the java.lang.reflect.Member for the represented 
             *  attribute.
             *  @return corresponding java.lang.reflect.Member
             */
            //java.lang.reflect.Member getJavaMember();
            Attribute anAttribute = anEntity_.getAttribute("host");            
            assertNotNull(anAttribute);
            assertEquals(Computer.class, anAttribute.getJavaType());
            Member aMember = anAttribute.getJavaMember();
            assertNotNull("host java member is null", aMember);
            assertTrue("host attribute java member is not an instance of Method", aMember instanceof Method);
            assertEquals("getHost", aMember.getName());
            assertEquals(Computer.class, ((Method)aMember).getReturnType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    // 316991: regression test getJavaMember() on method level access Entity attribute unidirectional @OneToOne on a MappedSuperclass
    // This test exercises special handling code early in getJavaMember() for MappedSuperclasses 
    public void testAttribute_getJavaMember_EntityType_PropertyMethodLevel_on_MappedSuperclass_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            MappedSuperclassTypeImpl<MSRootPropertyAccess> aMappedSuperclass_ = (MappedSuperclassTypeImpl)metamodel.managedType(MSRootPropertyAccess.class);
            assertNotNull(aMappedSuperclass_);
            assertEquals(Type.PersistenceType.MAPPED_SUPERCLASS, aMappedSuperclass_.getPersistenceType());

            /**
             *  Return the java.lang.reflect.Member for the represented 
             *  attribute.
             *  @return corresponding java.lang.reflect.Member
             */
            //java.lang.reflect.Member getJavaMember();
            Attribute anAttribute = aMappedSuperclass_.getAttribute("primarySuperComputer"); // if attribute is missing from model we throw an IAE            
            assertNotNull(anAttribute);
            assertEquals(ArrayProcessor.class, anAttribute.getJavaType());
            Member aMember = anAttribute.getJavaMember();
            assertNotNull("primarySuperComputer java member is null", aMember);
            assertTrue("primarySuperComputer attribute java member is not an instance of Method", aMember instanceof Method);
            assertEquals("getPrimarySuperComputer", aMember.getName());
            assertEquals(ArrayProcessor.class, ((Method)aMember).getReturnType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }
    
    public void testAttribute_getJavaMember_ManagedType_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            assertEquals(Type.PersistenceType.ENTITY, entityManufacturer_.getPersistenceType());

            /**
             *  Return the java.lang.reflect.Member for the represented 
             *  attribute.
             *  @return corresponding java.lang.reflect.Member
             */
            //java.lang.reflect.Member getJavaMember();
            Attribute anAttribute = entityManufacturer_.getAttribute("computers");            
            assertNotNull(anAttribute);
            assertTrue(((AttributeImpl)anAttribute).isPlural());
            assertTrue(anAttribute instanceof PluralAttribute);
            Bindable aPluralAttribute = entityManufacturer_.getSet("computers");
            assertEquals(Computer.class, aPluralAttribute.getBindableJavaType());
            assertEquals(Set.class, anAttribute.getJavaType());
            assertEquals(PersistentAttributeType.ONE_TO_MANY, anAttribute.getPersistentAttributeType());
            
            Member aMember = anAttribute.getJavaMember();
            assertNotNull(aMember);
            assertTrue(aMember instanceof Field);
            assertEquals("computers", aMember.getName());
            assertNotNull(((Field)aMember).getType());
            assertEquals(Set.class.getName(), ((Field)aMember).getType().getName());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    public void testAttribute_isAssociation_on_Plural_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            assertEquals(Type.PersistenceType.ENTITY, entityManufacturer_.getPersistenceType());

            /**
             *  Is the attribute an association.
             *  @return whether boolean indicating whether attribute 
             *          corresponds to an association
             */
            //boolean isAssociation();
            Attribute anAttribute = entityManufacturer_.getAttribute("computers");            
            assertNotNull(anAttribute);
            assertTrue(((AttributeImpl)anAttribute).isPlural());
            assertTrue(anAttribute instanceof PluralAttribute);
            Bindable aBindable = entityManufacturer_.getSet("computers");
            PluralAttribute aPluralAttribute = entityManufacturer_.getSet("computers");
            assertEquals(Computer.class, aPluralAttribute.getBindableJavaType());
            assertEquals(Set.class, anAttribute.getJavaType());
            assertEquals(PersistentAttributeType.ONE_TO_MANY, anAttribute.getPersistentAttributeType());
            
            boolean isAssociation = anAttribute.isAssociation();
            assertTrue(isAssociation);
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    public void testAttribute_isAssociation_on_Singular_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<GalacticPosition> entityLocation_ = (EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);            
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);

            /**
             *  Is the attribute an association.
             *  @return whether boolean indicating whether attribute 
             *          corresponds to an association
             */
            //boolean isAssociation();
            Attribute anAttribute = entityComputer_.getAttribute("location");            
            assertNotNull(anAttribute);
            assertEquals(GalacticPosition.class, anAttribute.getJavaType());
            boolean isAssociation = anAttribute.isAssociation();
            // Only a ReferenceMapping that extends ObjectReferenceMapping returns true            
            assertTrue(isAssociation);
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }
    
    public void testAttribute_isCollection_false_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<GalacticPosition> entityLocation_ = (EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            Type<?> locationIdType = entityLocation_.getIdType();
            assertNotNull(locationIdType);
            assertEquals(PersistenceType.EMBEDDABLE, locationIdType.getPersistenceType());
            assertEquals(EmbeddedPK.class, locationIdType.getJavaType());
            Attribute locationIdAttribute = entityLocation_.getAttribute("primaryKey");
            assertNotNull(locationIdAttribute);
            assertTrue(locationIdAttribute instanceof SingularAttributeImpl);

            /**
             *  Is the attribute collection-valued.
             *  @return boolean indicating whether attribute is 
             *          collection-valued
             */
            //boolean isCollection();
            assertFalse(locationIdAttribute.isCollection());
            assertFalse(((AttributeImpl)locationIdAttribute).isPlural()); // non-spec.
            ManagedType locationIdAttributeManagedType = locationIdAttribute.getDeclaringType();
            assertEquals(entityLocation_, locationIdAttributeManagedType);
            ManagedTypeImpl locationIdAttributeManagedTypeImpl = ((SingularAttributeImpl)locationIdAttribute).getManagedTypeImpl();
            assertEquals(locationIdType.getJavaType(), ((SingularAttributeImpl)locationIdAttribute).getBindableJavaType());
            assertEquals(Bindable.BindableType.SINGULAR_ATTRIBUTE, ((SingularAttributeImpl)locationIdAttribute).getBindableType());
            assertEquals(locationIdType.getJavaType(), locationIdAttribute.getJavaType());
            Type embeddableType = ((SingularAttributeImpl)locationIdAttribute).getType();
            assertNotNull(embeddableType);
            assertNotSame(embeddableType, locationIdAttributeManagedType);
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    public void testAttribute_isCollection_true_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            assertEquals(Type.PersistenceType.ENTITY, entityManufacturer_.getPersistenceType());
            Attribute anAttribute = entityManufacturer_.getAttribute("computers");            
            assertNotNull(anAttribute);
            assertEquals(PersistentAttributeType.ONE_TO_MANY, anAttribute.getPersistentAttributeType());
            assertNotNull(anAttribute);
            assertTrue(((AttributeImpl)anAttribute).isPlural());
            assertTrue(anAttribute instanceof PluralAttribute);
            PluralAttribute aPluralAttribute = entityManufacturer_.getSet("computers");
            assertEquals(Computer.class, aPluralAttribute.getBindableJavaType());
            // 314906: javaType for collections is the collection type not the element type
            assertEquals(Set.class, aPluralAttribute.getJavaType());

            /**
             *  Is the attribute collection-valued.
             *  @return boolean indicating whether attribute is 
             *          collection-valued
             */
            //boolean isCollection();
            assertTrue(anAttribute.isCollection());
            assertTrue(((AttributeImpl)anAttribute).isPlural()); // non-spec.
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }
    
    public void testBasicType() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);

            // This interface is empty - however we can test BasicTypeImpl INTERNAL functionality
            // Test lazy creation of random basic types
            TypeImpl aRandomType = ((MetamodelImpl)metamodel).getType(Integer.class);
            assertEquals(Type.PersistenceType.BASIC, aRandomType.getPersistenceType());
            BasicTypeImpl aRandomBasicType = (BasicTypeImpl) aRandomType;
            assertFalse(aRandomBasicType.isEntity());
            assertFalse(aRandomBasicType.isMappedSuperclass());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    public void testBindable_getBindableType_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);

            // Actual Test Case
            /*public static enum BindableType { 
            SINGULAR_ATTRIBUTE, PLURAL_ATTRIBUTE, ENTITY_TYPE}*/
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);

            /**
             *  Return the bindable type of the represented object.
             *  @return bindable type
             */ 
            //BindableType getBindableType();
            // EntityTypeImpl
            assertEquals(Bindable.BindableType.ENTITY_TYPE, entityManufacturer_.getBindableType());
            // SingularAttributeImpl
            SingularAttribute<Manufacturer, Boolean> manufacturer_booleanObject = 
            entityManufacturer_.getDeclaredSingularAttribute("aBooleanObject", Boolean.class);
            assertNotNull(manufacturer_booleanObject);
            assertEquals(Bindable.BindableType.SINGULAR_ATTRIBUTE, manufacturer_booleanObject.getBindableType());
            // PluralAttributeImpl
            ListAttribute<Manufacturer, HardwareDesigner> manufacturer_hardwareDesignersList = 
            entityManufacturer_.getDeclaredList("hardwareDesigners", HardwareDesigner.class);
            assertNotNull(manufacturer_hardwareDesignersList);
            assertEquals(Bindable.BindableType.PLURAL_ATTRIBUTE, manufacturer_hardwareDesignersList.getBindableType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    public void testBindable_getBindableJavaType_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);

            // Actual Test Case
            /*public static enum BindableType { 
            SINGULAR_ATTRIBUTE, PLURAL_ATTRIBUTE, ENTITY_TYPE}*/
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            
            SingularAttribute<Manufacturer, Boolean> manufacturer_booleanObject = 
            entityManufacturer_.getDeclaredSingularAttribute("aBooleanObject", Boolean.class);
            assertNotNull(manufacturer_booleanObject);
            ListAttribute<Manufacturer, HardwareDesigner> manufacturer_hardwareDesignersList = 
            entityManufacturer_.getDeclaredList("hardwareDesigners", HardwareDesigner.class);
            assertNotNull(manufacturer_hardwareDesignersList);
            
            /**
             * Return the Java type of the represented object.
             * If the bindable type of the object is <code>PLURAL_ATTRIBUTE</code>,
             * the Java element type is returned. If the bindable type is
             * <code>SINGULAR_ATTRIBUTE</code> or <code>ENTITY_TYPE</code>, 
             * the Java type of the
             * represented entity or attribute is returned.
             * @return Java type
             */
            //Class<T> getBindableJavaType();
            // EntityTypeImpl
            assertEquals(entityManufacturer_.getJavaType(), entityManufacturer_.getBindableJavaType());
            // SingularAttribute
            assertEquals(manufacturer_booleanObject.getJavaType(), manufacturer_booleanObject.getBindableJavaType());
            // PluralAttribute
            Type<HardwareDesigner> manufacturer_hardwareDesignersType = manufacturer_hardwareDesignersList.getElementType();
            assertNotNull(manufacturer_hardwareDesignersType);
            assertNotNull(manufacturer_hardwareDesignersType.getJavaType());
            assertEquals(manufacturer_hardwareDesignersType.getJavaType(), manufacturer_hardwareDesignersList.getBindableJavaType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }    
    
    public void testEmbeddableType() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);

            // The EmbeddableType interface has no methods - however we can test EmbeddableTypeImpl INTERNAL functionality
            Type<EmbeddedPK> aType_ = metamodel.managedType(EmbeddedPK.class);
            assertNotNull("The EmbeddableType EmbeddedPK should not be null", aType_);
            EmbeddableTypeImpl embeddableType_ = (EmbeddableTypeImpl) aType_; 
            assertFalse("Expected EmbeddedPK.isEntity = false", embeddableType_.isEntity());
            assertFalse("Expected EmbeddedPK.isMappedSuperclass = false", embeddableType_.isMappedSuperclass());
            assertEquals(PersistenceType.EMBEDDABLE, embeddableType_.getPersistenceType());
            assertEquals(EmbeddedPK.class, embeddableType_.getJavaType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    // 300626
    public void testNestedEmbeddableType() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);

            // Get the containing Entity
            Type<GalacticPosition> containingEntity_ = metamodel.managedType(GalacticPosition.class);
            assertNotNull("The containing Entity GalacticPosition should not be null", containingEntity_);
            // Get the Embeddable on the Entity
            Type<Observation> rootEmbeddable_ = metamodel.embeddable(Observation.class);
            assertNotNull("The contained embeddable Observation should not be null", rootEmbeddable_);
            EmbeddableTypeImpl rootEmbeddableType_ = (EmbeddableTypeImpl) rootEmbeddable_;            
            assertFalse("Expected Observation.isEntity = false", rootEmbeddableType_.isEntity());
            assertFalse("Expected Observation.isMappedSuperclass = false", rootEmbeddableType_.isMappedSuperclass());
            assertEquals(PersistenceType.EMBEDDABLE, rootEmbeddable_.getPersistenceType());
            assertEquals(Observation.class, rootEmbeddable_.getJavaType());
            
            // Get the nested embeddable on the parent Embeddable
            Type<ObservationDetail> nestedEmbeddable_ = metamodel.embeddable(ObservationDetail.class);
            assertNotNull("The contained embeddable Observation should not be null", nestedEmbeddable_);
            EmbeddableTypeImpl nestedEmbeddableType_ = (EmbeddableTypeImpl) nestedEmbeddable_;            
            assertFalse("Expected ObservationData.isEntity = false", nestedEmbeddableType_.isEntity());
            assertFalse("Expected Observation.isMappedSuperclass = false", nestedEmbeddableType_.isMappedSuperclass());
            assertEquals(PersistenceType.EMBEDDABLE, nestedEmbeddable_.getPersistenceType());
            assertEquals(ObservationDetail.class, nestedEmbeddable_.getJavaType());
            // verify that the nested embeddable is a member of the root embeddable
            SingularAttribute<Observation, ObservationDetail> nestedEmbeddableAttribute_ = rootEmbeddableType_.getSingularAttribute("detail", ObservationDetail.class);
            assertNotNull("The nested embeddable ObservationDetail should be a member of the root embeddable Observation",
                    nestedEmbeddableAttribute_);
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }
    
    public void testEntityType() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull(metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);
            
            // Actual Test Case
            /**
             *  Return the entity name.
             *  @return entity name
             */
            //String getName();
            String entityNameWithNameAttribute = entityManufacturer_.getName();
            assertNotNull("EntityType.getName() should not be null", entityNameWithNameAttribute);
            // this should match the @Entity(name="ManuMetamodel")
            assertEquals("ManuMetamodel", entityNameWithNameAttribute);

            String entityNameWithoutAttribute = entityHardwareDesigner_.getName();
            assertNotNull("EntityType.getName() should not be null", entityNameWithoutAttribute);
            // this should match the @Entity(name="HardwareDesigner")
            assertEquals("HardwareDesigner", entityNameWithoutAttribute);
            
            // Normal use cases
            // Composite table FK's that include a MappedSuperclass
            // get an Attribute<Container, Type==String>
            Attribute nameAttribute = entityManufacturer_.getAttribute("name");
            assertTrue(null != nameAttribute);
            
            // get an Attribute<Container, Type==MappedSuperclass>
            Attribute employerAttribute = entityHardwareDesigner_.getAttribute("employer");
            assertTrue(null != employerAttribute);            
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }
    
    public void testIdentifiableType_getId_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull(metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            EntityTypeImpl<GalacticPosition> entityLocation_ = (EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);            
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);

            // Actual Test Case
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
            EntityType<Manufacturer> aManufacturerType = metamodel.entity(Manufacturer.class);
            assertNotNull(aManufacturerType.getId(Integer.class));
            // declared and valid
            MappedSuperclassTypeImpl<Person> msPerson1_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson1_.getId(Integer.class));
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    public void testIdentifiableType_getVersion_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull(metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            EntityTypeImpl<GalacticPosition> entityLocation_ = (EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);            
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);

            // Actual Test Case
            /**
             *  Return the attribute that corresponds to the version 
             *    attribute of the entity or mapped superclass.
             *  @param type  the type of the represented version attribute
             *  @return version attribute
             *  @throws IllegalArgumentException if version attribute of the 
             *          given type is not present in the identifiable type
             */
            //<Y> SingularAttribute<? super X, Y> getVersion(Class<Y> type);
//              EntityType<Enclosure> anEnclosureType = metamodel.entity(Enclosure.class);
//              assertNotNull(anEnclosureType.getVersion(Integer.class));
            // declared and inherited
            
            // declared 
            assertNotNull(entityManufacturer_.getVersion(int.class));
            // declared and valid
            // FUTURE : require MS version
            //assertNotNull(msPerson1_.getVersion(Integer.class)); - keep commented
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything yet.
    public void testIdentifiableType_getDeclaredId_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull(metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            EntityTypeImpl<GalacticPosition> entityLocation_ = (EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);            
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);

            // Actual Test Case
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
            // Not declared  - invalid
            // assertNotNull(aManufacturerType.getDeclaredId(Integer.class)); - keep commented
            // declared and valid
            
            //*********************************************/
            // FUTURE: Require a version on a MappedSuperclass
            //assertNotNull(msPerson1_.getDeclaredId(Integer.class));
        } catch (Exception e) {
            //e.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }
    
    public void testIdentifiableType_getDeclaredVersion_exists_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull(metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);

            // Actual Test Case
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
            // FUTURE: need an undeclared subclass
            // Is declared
            SingularAttribute<Manufacturer,?> anAttribute = entityManufacturer_.getDeclaredVersion(int.class); 
            assertNotNull(anAttribute);
            assertNotNull(anAttribute.getType());
            assertNotNull(anAttribute.getBindableType());
            assertNotNull(anAttribute.getBindableJavaType());
            assertNotNull(anAttribute.getDeclaringType());
            assertEquals(entityManufacturer_, anAttribute.getDeclaringType());
            assertFalse(anAttribute.isId());
            assertTrue(anAttribute.isVersion());
            assertEquals(PersistentAttributeType.BASIC, anAttribute.getPersistentAttributeType());
            assertTrue(anAttribute.isOptional());
            assertNotNull(anAttribute.getJavaType());
            assertFalse(anAttribute.isAssociation());
            assertFalse(anAttribute.isCollection());            
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    public void testIdentifiableType_getDeclaredVersion_exists_above_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull(metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);

            // Actual Test Case
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
            // Does not exist
//            EntityType<Enclosure> anEnclosureType = metamodel.entity(Enclosure.class);
//            assertNotNull(anEnclosureType.getDeclaredVersion(Integer.class));
            // Is declared
            assertNotNull(entityManufacturer_.getDeclaredVersion(int.class));
            
            // FUTURE: need an undeclared subclass
            // Is declared for assertNotNull(msPerson1_.getDeclaredVersion(Integer.class));
        } catch (IllegalArgumentException iae) {
            //iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything different?
    public void testIdentifiableType_getDeclaredVersion_does_not_exist_at_all_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull(metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);

            // Actual Test Case
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
            // Does not exist
//            EntityType<Enclosure> anEnclosureType = metamodel.entity(Enclosure.class);
//            assertNotNull(anEnclosureType.getDeclaredVersion(Integer.class));
            // Is declared
            assertNotNull(entityManufacturer_.getDeclaredVersion(int.class));
            
            // FUTURE: need an undeclared subclass
            // Is declared for assertNotNull(msPerson1_.getDeclaredVersion(Integer.class));
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything different?
    public void testIdentifiableType_getSupertype_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull(metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            EntityTypeImpl<GalacticPosition> entityLocation_ = (EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);            
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);

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
            superTypeManufacturer = entityManufacturer_.getSupertype();
        } catch (IllegalArgumentException iae) {
            // expecting no exception
            iae.printStackTrace();
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
            superTypeLocation = entityLocation_.getSupertype();
        } catch (IllegalArgumentException iae) {
            // expecting no exception
            iae.printStackTrace();
            expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);
            assertNull(superTypeLocation);
        } catch (IllegalArgumentException iae) {
            //iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    public void testIdentifiableType_hasSingleIdAttribute_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull(metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            EntityTypeImpl<GalacticPosition> entityLocation_ = (EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);            
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);

            /**
             *  Whether or not the identifiable type has an id attribute.
             *  Returns true for a simple id or embedded id; returns false
             *  for an idclass.
             *  @return boolean indicating whether or not the identifiable
             *           type has a single id attribute
             */
            // We do not need to test Embeddable to Basic as they do not implement IdentifiableType
            //boolean hasSingleIdAttribute();
            // verify false for "no" type of Id attribute
            // @Id - test normal path
            expectedIAExceptionThrown = false;
            boolean hasSingleIdAttribute = false;
        try {
            EntityType<Manufacturer> aType = metamodel.entity(Manufacturer.class);
            hasSingleIdAttribute = aType.hasSingleIdAttribute();
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);            
            assertTrue(hasSingleIdAttribute);

            // @EmbeddedId
            expectedIAExceptionThrown = false;
            hasSingleIdAttribute = false;
        try {
            EntityType<GalacticPosition> aType = metamodel.entity(GalacticPosition.class);
            hasSingleIdAttribute = aType.hasSingleIdAttribute();
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);            
            assertTrue(hasSingleIdAttribute);

            // @IdClass - test exception path
            expectedIAExceptionThrown = false;
            hasSingleIdAttribute = true;
        try {
            EntityType<Enclosure> aType = metamodel.entity(Enclosure.class);
            hasSingleIdAttribute = aType.hasSingleIdAttribute();
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);            
            assertFalse(hasSingleIdAttribute);
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    public void testIdentifiableType_hasVersionAttribute_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull(metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);

            // Actual Test Case
            /**
             *  Whether or not the identifiable type has a version attribute.
             *  @return boolean indicating whether or not the identifiable
             *           type has a version attribute
             */
            //boolean hasVersionAttribute();
            EntityType<Enclosure> anEnclosureType = metamodel.entity(Enclosure.class);
            assertFalse(anEnclosureType.hasVersionAttribute());
            assertTrue(entityManufacturer_.hasVersionAttribute());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    public void testIdentifiableType_getIdClassAttributes_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull(metamodel);
            // Actual Test Case
            /**
             *   Return the attributes corresponding to the id class of the
             *   identifiable type.
             *   @return id attributes
             *   @throws IllegalArgumentException if the identifiable type
             *           does not have an id class
             */
             //java.util.Set<SingularAttribute<? super X, ?>> getIdClassAttributes();
            // @IdClass - test normal path
            expectedIAExceptionThrown = false;
            boolean hasSingleIdAttribute = true;
            Set<SingularAttribute<? super Enclosure, ?>> idClassAttributes = null;
            EntityType<Enclosure> aType = metamodel.entity(Enclosure.class);
            hasSingleIdAttribute = aType.hasSingleIdAttribute();
            // We verify that an @IdClass exists - no single @Id or @EmbeddedId exists
            assertFalse(hasSingleIdAttribute);
            idClassAttributes = aType.getIdClassAttributes();
            assertNotNull(idClassAttributes);
            assertEquals(4, idClassAttributes.size());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    public void testIdentifiableType_getIdClassAttributesAcrossMappedSuperclassChain_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull(metamodel);
            
            // Actual Test Case
            /**
             *   Return the attributes corresponding to the id class of the
             *   identifiable type.
             *   @return id attributes
             *   @throws IllegalArgumentException if the identifiable type
             *           does not have an id class
             */
             //java.util.Set<SingularAttribute<? super X, ?>> getIdClassAttributes();
            // @IdClass - test normal path
            expectedIAExceptionThrown = false;
            boolean hasSingleIdAttribute = true;
            // 0 id attributes here
            Set<SingularAttribute<? super MS_MS_Entity_Leaf, ?>> idClassAttributesLeaf = null;            
            IdentifiableType<MS_MS_Entity_Leaf> aTypeLeaf = metamodel.entity(MS_MS_Entity_Leaf.class);
            // 1 id attribute here
            Set<SingularAttribute<? super MS_MS_Entity_Center, ?>> idClassAttributesCenter = null;
            MappedSuperclassType<MS_MS_Entity_Center> aTypeCenter = (MappedSuperclassType)metamodel.managedType(MS_MS_Entity_Center.class);
            // 3 id attributes here
            Set<SingularAttribute<? super MS_MS_Entity_Root, ?>> idClassAttributesRoot = null;
            MappedSuperclassType<MS_MS_Entity_Root> aTypeRoot = (MappedSuperclassType)metamodel.managedType(MS_MS_Entity_Root.class);
            
            hasSingleIdAttribute = aTypeLeaf.hasSingleIdAttribute();
            // We verify that an an @IdClass exists above
            assertFalse(hasSingleIdAttribute); // This tests the IdentifiableType part of the transaction for DI 78
            hasSingleIdAttribute = aTypeCenter.hasSingleIdAttribute();
            // We verify that an one part of an @IdClass exists 
            assertTrue(hasSingleIdAttribute); // This tests the IdentifiableType part of the transaction for DI 78
            hasSingleIdAttribute = aTypeRoot.hasSingleIdAttribute();
            // We verify that an @IdClass exists - no single @Id or @EmbeddedId exists
            assertFalse(hasSingleIdAttribute); // This tests the IdentifiableType part of the transaction for DI 78
            //idClassAttributesLeaf = aTypeLeaf.getIdClassAttributes(); // expected IAE - leave commented
            idClassAttributesCenter = aTypeCenter.getIdClassAttributes();
            assertNotNull(idClassAttributesCenter);
            assertEquals(1, idClassAttributesCenter.size());
            // The following call is not valid because the IdClass attribute is defined one level below
        try {
            idClassAttributesRoot = aTypeRoot.getIdClassAttributes();
        } catch (IllegalArgumentException iae) {
            expectedIAExceptionThrown = true;
            }
            assertTrue(expectedIAExceptionThrown);
            expectedIAExceptionThrown = false;
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }
    
    public void testIdentifiableType_getIdType_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull(metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            EntityTypeImpl<GalacticPosition> entityLocation_ = (EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);            
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);

            // Actual Test Case
            /**
             *  Return the type that represents the type of the id.
             *  @return type of id
             */
            //Type<?> getIdType();       
            
            // Test EntityType
            
            // Test normal path for an [Embeddable] type via @EmbeddedId
            expectedIAExceptionThrown = false;
            Type<?> locationIdType = null;
        try {
            locationIdType = entityLocation_.getIdType();
        } catch (IllegalArgumentException iae) {
            // expecting no exception
            iae.printStackTrace();
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
            Attribute locationIdAttribute = entityLocation_.getAttribute("primaryKey");
            assertNotNull(locationIdAttribute);
            assertTrue(locationIdAttribute instanceof SingularAttributeImpl);
            assertFalse(locationIdAttribute.isCollection());
            assertFalse(((AttributeImpl)locationIdAttribute).isPlural()); // non-spec.
            ManagedType locationIdAttributeManagedType = locationIdAttribute.getDeclaringType();
            assertEquals(entityLocation_, locationIdAttributeManagedType);
            ManagedTypeImpl locationIdAttributeManagedTypeImpl = ((SingularAttributeImpl)locationIdAttribute).getManagedTypeImpl();
            assertEquals(locationIdType.getJavaType(), ((SingularAttributeImpl)locationIdAttribute).getBindableJavaType());
            assertEquals(Bindable.BindableType.SINGULAR_ATTRIBUTE, ((SingularAttributeImpl)locationIdAttribute).getBindableType());
            assertEquals(locationIdType.getJavaType(), locationIdAttribute.getJavaType());
            Type embeddableType = ((SingularAttributeImpl)locationIdAttribute).getType();
            assertNotNull(embeddableType);
            assertNotSame(embeddableType, locationIdAttributeManagedType);

            // Test normal path for a [Basic] type
            expectedIAExceptionThrown = false;
            Type<?> computerIdType = null;
            try {
                computerIdType = entityComputer_.getIdType();
            } catch (IllegalArgumentException iae) {
                // expecting no exception
                iae.printStackTrace();
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
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);
            MappedSuperclassTypeImpl<Corporation> msCorporation_ = (MappedSuperclassTypeImpl)metamodel.managedType(Corporation.class);
            assertNotNull(msCorporation_);
            
            // Verify all types (entities, embeddables, mappedsuperclasses and basic)
            // get all 21 types (a non spec function - for testing introspection)
            Map<Class, TypeImpl<?>> typesMap = ((MetamodelImpl)metamodel).getTypes();
            // verify each one
            assertNotNull(typesMap);
            ((MetamodelImpl)metamodel).printAllTypes();
            // Note: Since BasicTypes are lazy - loaded into the metamodel-types Map - this test must preceed any test that verifies all BasicType objects like "testIdentifiableType_getIdType_Method"
            // You will get a lower number here - if only this single test is run via the Testing Browser
            assertEquals(METAMODEL_ALL_TYPES, typesMap.size());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything different?
    public void testListAttribute() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull(metamodel);

            // Actual Test Case
            
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }
    
    public void testManagedType_getAttributes_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);
            MappedSuperclassTypeImpl<Corporation> msCorporation_ = (MappedSuperclassTypeImpl)metamodel.managedType(Corporation.class);
            assertNotNull(msCorporation_);
            EntityTypeImpl<GalacticPosition> entityLocation_ =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);

            // Actual Test Case
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
            Set<Attribute<? super Manufacturer, ?>> attributeSet = entityManufacturer_.getAttributes();
            assertNotNull(attributeSet);
            // We should see 30 attributes (3 List, 3 Singular, 17 basic (java.lang, java.math) for Manufacturer (computers, hardwareDesigners, id(from the mappedSuperclass), 
            // version, name(from the mappedSuperclass) and corporateComputers from the Corporation mappedSuperclass)
            assertEquals(METAMODEL_MANUFACTURER_DECLARED_TYPES + 6, attributeSet.size());
            // for each managed entity we will see 2 entries (one for the Id, one for the Version)
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("id"))); // 
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("version"))); //
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("name"))); //
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("computers"))); //
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("hardwareDesigners"))); //
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("corporateComputers"))); //
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("hardwareDesignersMap"))); //
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("hardwareDesignersMapUC1a"))); //
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("hardwareDesignersMapUC2"))); //
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("hardwareDesignersMapUC4"))); //
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("hardwareDesignersMapUC7"))); //            
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("hardwareDesignersMapUC8"))); //            
            // ManyToMany Collection Attribute from Person MappedSuperclass
            assertTrue(attributeSet.contains(entityManufacturer_.getCollection("historicalEmps"))); //
            assertTrue(entityManufacturer_.getCollection("historicalEmps").isCollection()); //
            // Basic java.lang and java.math primitive and primitive object types
        //private Object anObject; - invalid
            //assertTrue(attributeSet.contains(entityManufacturer.getAttribute("anObject"))); //
            //assertNotNull(entityManufacturer.getAttribute("anObject"));
            //assertTrue(entityManufacturer.getAttribute("anObject").getPersistentAttributeType().equals(PersistentAttributeType.BASIC));
            //assertEquals(Object.class, entityManufacturer.getAttribute("anObject").getJavaType());
        //private Boolean aBooleanObject;
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("aBooleanObject"))); //
            assertNotNull(entityManufacturer_.getAttribute("aBooleanObject"));
            assertTrue(entityManufacturer_.getAttribute("aBooleanObject").getPersistentAttributeType().equals(PersistentAttributeType.BASIC));
            assertEquals(Boolean.class, entityManufacturer_.getAttribute("aBooleanObject").getJavaType());
        //private Byte aByteObject;
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("aByteObject"))); //
            assertNotNull(entityManufacturer_.getAttribute("aByteObject"));
            assertTrue(entityManufacturer_.getAttribute("aByteObject").getPersistentAttributeType().equals(PersistentAttributeType.BASIC));
            assertEquals(Byte.class, entityManufacturer_.getAttribute("aByteObject").getJavaType());
        //private Short aShortObject;    
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("aShortObject"))); //
            assertNotNull(entityManufacturer_.getAttribute("aShortObject"));
            assertTrue(entityManufacturer_.getAttribute("aShortObject").getPersistentAttributeType().equals(PersistentAttributeType.BASIC));
            assertEquals(Short.class, entityManufacturer_.getAttribute("aShortObject").getJavaType());
        //private Integer anIntegerObject;
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("anIntegerObject"))); //
            assertNotNull(entityManufacturer_.getAttribute("anIntegerObject"));
            assertTrue(entityManufacturer_.getAttribute("anIntegerObject").getPersistentAttributeType().equals(PersistentAttributeType.BASIC));
            assertEquals(Integer.class, entityManufacturer_.getAttribute("anIntegerObject").getJavaType());
        //private Long aLongObject;
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("aLongObject"))); //
            assertNotNull(entityManufacturer_.getAttribute("aLongObject"));
            assertTrue(entityManufacturer_.getAttribute("aLongObject").getPersistentAttributeType().equals(PersistentAttributeType.BASIC));
            assertEquals(Long.class, entityManufacturer_.getAttribute("aLongObject").getJavaType());
        //private BigInteger aBigIntegerObject;    
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("aBigIntegerObject"))); //
            assertNotNull(entityManufacturer_.getAttribute("aBigIntegerObject"));
            assertTrue(entityManufacturer_.getAttribute("aBigIntegerObject").getPersistentAttributeType().equals(PersistentAttributeType.BASIC));
            assertEquals(BigInteger.class, entityManufacturer_.getAttribute("aBigIntegerObject").getJavaType());
        //private Float aFloatObject;
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("aFloatObject"))); //
            assertNotNull(entityManufacturer_.getAttribute("aFloatObject"));
            assertTrue(entityManufacturer_.getAttribute("aFloatObject").getPersistentAttributeType().equals(PersistentAttributeType.BASIC));
            assertEquals(Float.class, entityManufacturer_.getAttribute("aFloatObject").getJavaType());
        //private Double aDoubleObject;
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("aDoubleObject"))); //
            assertNotNull(entityManufacturer_.getAttribute("aDoubleObject"));
            assertTrue(entityManufacturer_.getAttribute("aDoubleObject").getPersistentAttributeType().equals(PersistentAttributeType.BASIC));
            assertEquals(Double.class, entityManufacturer_.getAttribute("aDoubleObject").getJavaType());
        //private Character aCharacterObject;
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("aCharacterObject"))); //
            assertNotNull(entityManufacturer_.getAttribute("aCharacterObject"));
            assertTrue(entityManufacturer_.getAttribute("aCharacterObject").getPersistentAttributeType().equals(PersistentAttributeType.BASIC));
            assertEquals(Character.class, entityManufacturer_.getAttribute("aCharacterObject").getJavaType());
        //private Enum anEnum;
            // FUTURE: the following 4 lines are not implemented yet
            //assertTrue(attributeSet.contains(entityManufacturer.getAttribute("anEnum"))); //
            //assertNotNull(entityManufacturer.getAttribute("anEnum"));
            //assertTrue(entityManufacturer.getAttribute("anEnum").getPersistentAttributeType().equals(PersistentAttributeType.BASIC));
            //assertEquals(Enum.class, entityManufacturer.getAttribute("anEnum").getJavaType());
        //private boolean aBoolean;
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("aBoolean"))); //
            assertNotNull(entityManufacturer_.getAttribute("aBoolean"));
            assertTrue(entityManufacturer_.getAttribute("aBoolean").getPersistentAttributeType().equals(PersistentAttributeType.BASIC));
            assertEquals(boolean.class, entityManufacturer_.getAttribute("aBoolean").getJavaType());
        //private byte aByte;
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("aByte"))); //
            assertNotNull(entityManufacturer_.getAttribute("aByte"));
            assertTrue(entityManufacturer_.getAttribute("aByte").getPersistentAttributeType().equals(PersistentAttributeType.BASIC));
            assertEquals(byte.class, entityManufacturer_.getAttribute("aByte").getJavaType());
        //private short aShort;    
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("aShort"))); //
            assertNotNull(entityManufacturer_.getAttribute("aShort"));
            assertTrue(entityManufacturer_.getAttribute("aShort").getPersistentAttributeType().equals(PersistentAttributeType.BASIC));
            assertEquals(short.class, entityManufacturer_.getAttribute("aShort").getJavaType());
        //private int anInt;
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("anInt"))); //
            assertNotNull(entityManufacturer_.getAttribute("anInt"));
            assertTrue(entityManufacturer_.getAttribute("anInt").getPersistentAttributeType().equals(PersistentAttributeType.BASIC));
            assertEquals(int.class, entityManufacturer_.getAttribute("anInt").getJavaType());
        //private long aLong;
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("aLong"))); //
            assertNotNull(entityManufacturer_.getAttribute("aLong"));
            assertTrue(entityManufacturer_.getAttribute("aLong").getPersistentAttributeType().equals(PersistentAttributeType.BASIC));
            assertEquals(long.class, entityManufacturer_.getAttribute("aLong").getJavaType());
        //private float aFloat;
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("aFloat"))); //
            assertNotNull(entityManufacturer_.getAttribute("aFloat"));
            assertTrue(entityManufacturer_.getAttribute("aFloat").getPersistentAttributeType().equals(PersistentAttributeType.BASIC));
            assertEquals(float.class, entityManufacturer_.getAttribute("aFloat").getJavaType());
        //private double aDouble;
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("aDouble"))); //
            assertNotNull(entityManufacturer_.getAttribute("aDouble"));
            assertTrue(entityManufacturer_.getAttribute("aDouble").getPersistentAttributeType().equals(PersistentAttributeType.BASIC));
            assertEquals(double.class, entityManufacturer_.getAttribute("aDouble").getJavaType());
        //private char aChar;
            assertTrue(attributeSet.contains(entityManufacturer_.getAttribute("aChar"))); //
            assertNotNull(entityManufacturer_.getAttribute("aChar"));
            assertTrue(entityManufacturer_.getAttribute("aChar").getPersistentAttributeType().equals(PersistentAttributeType.BASIC));
            assertEquals(char.class, entityManufacturer_.getAttribute("aChar").getJavaType());
            
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
             *     +- historicalEmps : Manufacturer
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
            Set<Attribute<Manufacturer, ?>> declaredAttributesSet = entityManufacturer_.getDeclaredAttributes();
            assertNotNull(declaredAttributesSet);
            // We should see 8+17 declared out of 12 attributes for Manufacturer 
            assertEquals(METAMODEL_MANUFACTURER_DECLARED_TYPES + 1, declaredAttributesSet.size());
            // Id is declared 2 levels above
            assertFalse(declaredAttributesSet.contains(entityManufacturer_.getAttribute("id"))); //
            // name is declared 2 levels above
            assertFalse(declaredAttributesSet.contains(entityManufacturer_.getAttribute("name"))); //
            // corporateComputers is declared 1 level above
            assertFalse(declaredAttributesSet.contains(entityManufacturer_.getAttribute("corporateComputers"))); //
            // version is declared at this level
            assertTrue(declaredAttributesSet.contains(entityManufacturer_.getAttribute("version"))); //
            // computers is declared at this level
            assertTrue(declaredAttributesSet.contains(entityManufacturer_.getAttribute("computers"))); //
            // hardwareDesigners is declared at this level
            assertTrue(declaredAttributesSet.contains(entityManufacturer_.getAttribute("hardwareDesigners"))); //
            // hardwareDesignersMap is declared at this level
            assertTrue(declaredAttributesSet.contains(entityManufacturer_.getAttribute("hardwareDesignersMap"))); //
            assertTrue(declaredAttributesSet.contains(entityManufacturer_.getAttribute("hardwareDesignersMapUC1a"))); //
            assertTrue(declaredAttributesSet.contains(entityManufacturer_.getAttribute("hardwareDesignersMapUC2"))); //
            assertTrue(declaredAttributesSet.contains(entityManufacturer_.getAttribute("hardwareDesignersMapUC4"))); //
            assertTrue(declaredAttributesSet.contains(entityManufacturer_.getAttribute("hardwareDesignersMapUC7"))); //
            assertTrue(declaredAttributesSet.contains(entityManufacturer_.getAttribute("hardwareDesignersMapUC8"))); //            
            // historicalEmps is declared 2 levels above
            assertFalse(declaredAttributesSet.contains(entityManufacturer_.getAttribute("historicalEmps"))); //
            
            Set<Attribute<Corporation, ?>> declaredAttributesSetForCorporation = msCorporation_.getDeclaredAttributes();
            assertNotNull(declaredAttributesSetForCorporation);
            // We should see 2 declared out of 5 attributes for Computer 
            assertEquals(2, declaredAttributesSetForCorporation.size());
            // Id is declared 1 level above
            //assertFalse(declaredAttributesSetForCorporation.contains(msCorporation.getAttribute("id"))); //
            // name is declared 1 level above but is not visible in a ms-->ms hierarchy
            //assertFalse(declaredAttributesSetForCorporation.contains(msCorporation.getAttribute("name"))); //
            // corporateComputers is declared at this level
            // Note: internally EclipseLink treats a ONE_TO_MANY as a MANY_TO_MANY for the case of a unidirectional mapping on a MappedSuperclass            
            assertTrue(declaredAttributesSetForCorporation.contains(msCorporation_.getAttribute("corporateComputers"))); //
            // historicalEmps is declared 1 level above but is not visible in a ms-->ms hierarchy
            //assertFalse(declaredAttributesSetForCorporation.contains(msCorporation.getAttribute("historicalEmps"))); //            

            Set<Attribute<Person, ?>> declaredAttributesSetForPerson = msPerson_.getDeclaredAttributes();
            assertNotNull(declaredAttributesSetForPerson);
            // We should see 3 declared out of 20 attributes for Person 
            assertEquals(3, declaredAttributesSetForPerson.size());
            // Id is declared at this level
            assertTrue(declaredAttributesSetForPerson.contains(msPerson_.getAttribute("id"))); //
            // name is declared at this level
            assertTrue(declaredAttributesSetForPerson.contains(msPerson_.getAttribute("name"))); //
            // historicalEmps is declared at this level
            assertTrue(declaredAttributesSetForPerson.contains(msPerson_.getAttribute("historicalEmps"))); //

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);            
            
            // Test Entity-->Entity hierarchy
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
            // Test normal path
            expectedIAExceptionThrown = false;
            SingularAttribute<? super Manufacturer, Double> aManufacturer_DoubleAttribute = null;            
        try {
            aManufacturer_DoubleAttribute = entityManufacturer_.getSingularAttribute("aDoubleObject", Double.class);            
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);
            assertNotNull(aManufacturer_DoubleAttribute);
            
            expectedIAExceptionThrown = false;
            SingularAttribute<? super Manufacturer, ?> aManufacturer_doubleAttribute = null;
            // Test autoboxed rules relax throwing an IAE
        try {
            aManufacturer_doubleAttribute = entityManufacturer_.getSingularAttribute("aDoubleObject", double.class);            
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);
            assertNotNull(aManufacturer_doubleAttribute);

            // Test IAE handling on wrong type
            expectedIAExceptionThrown = false;
            SingularAttribute<? super Manufacturer, ?> aManufacturer_wrong_intAttribute = null;
            // Test autoboxed rules relax throwing an IAE
        try {
            aManufacturer_wrong_intAttribute = entityManufacturer_.getSingularAttribute("aDoubleObject", int.class);            
        } catch (IllegalArgumentException iae) {
            // expecting
            expectedIAExceptionThrown = true;            
            }
            assertTrue("Expected thrown IllegalArgumentException", expectedIAExceptionThrown);
            assertNull(aManufacturer_wrong_intAttribute);

            // Test IAE handling on wrong type
            expectedIAExceptionThrown = false;
            SingularAttribute<? super Manufacturer, ?> aManufacturer_wrong_IntegerAttribute = null;
            // Test autoboxed rules relax throwing an IAE
        try {
            aManufacturer_wrong_IntegerAttribute = entityManufacturer_.getSingularAttribute("aDoubleObject", Integer.class);            
        } catch (IllegalArgumentException iae) {
            // expecting
            expectedIAExceptionThrown = true;            
            }
            assertTrue("Expected thrown IllegalArgumentException", expectedIAExceptionThrown);
            assertNull(aManufacturer_wrong_IntegerAttribute);
            
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
            // Test normal path
            expectedIAExceptionThrown = false;
            SingularAttribute<? super Manufacturer, Double> aDeclaredManufacturer_DoubleAttribute = null;            
        try {
            aDeclaredManufacturer_DoubleAttribute = entityManufacturer_.getDeclaredSingularAttribute("aDoubleObject", Double.class);            
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);
            assertNotNull(aDeclaredManufacturer_DoubleAttribute);
            
            expectedIAExceptionThrown = false;
            SingularAttribute<? super Manufacturer, ?> aDeclaredManufacturer_doubleAttribute = null;
            // Test autoboxed rules relax throwing an IAE
        try {
            aDeclaredManufacturer_doubleAttribute = entityManufacturer_.getDeclaredSingularAttribute("aDoubleObject", double.class);            
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);
            assertNotNull(aDeclaredManufacturer_doubleAttribute);

            // Test IAE handling on wrong type
            expectedIAExceptionThrown = false;
            SingularAttribute<? super Manufacturer, ?> aDeclaredManufacturer_wrong_intAttribute = null;
            // Test autoboxed rules relax throwing an IAE
        try {
            aDeclaredManufacturer_wrong_intAttribute = entityManufacturer_.getDeclaredSingularAttribute("aDoubleObject", int.class);            
        } catch (IllegalArgumentException iae) {
            // expecting
            expectedIAExceptionThrown = true;            
            }
            assertTrue("Expected thrown IllegalArgumentException", expectedIAExceptionThrown);
            assertNull(aDeclaredManufacturer_wrong_intAttribute);

            // Test IAE handling on wrong type
            expectedIAExceptionThrown = false;
            SingularAttribute<? super Manufacturer, ?> aDeclaredManufacturer_wrong_IntegerAttribute = null;
            // Test autoboxed rules relax throwing an IAE
        try {
            aDeclaredManufacturer_wrong_IntegerAttribute = entityManufacturer_.getDeclaredSingularAttribute("aDoubleObject", Integer.class);            
        } catch (IllegalArgumentException iae) {
            // expecting
            expectedIAExceptionThrown = true;            
            }
            assertTrue("Expected thrown IllegalArgumentException", expectedIAExceptionThrown);
            assertNull(aDeclaredManufacturer_wrong_IntegerAttribute);
            
            // Test wrong hierarchy IAE failures
            
            
            /**
             *  Return the single-valued attributes of the managed type.
             *  @return single-valued attributes
             */
            //java.util.Set<SingularAttribute<? super X, ?>> getSingularAttributes();
            Set<SingularAttribute<? super Manufacturer, ?>> singularAttributeSet = entityManufacturer_.getSingularAttributes();
            assertNotNull(singularAttributeSet);
            // We should see 4+18 singular attributes for Manufacturer (id(from the mappedSuperclass), version, name(from the mappedSuperclass))
            assertEquals(METAMODEL_MANUFACTURER_DECLARED_TYPES - 6, singularAttributeSet.size());
            // for each managed entity we will see 2 entries (one for the Id, one for the Version)
            assertTrue(singularAttributeSet.contains(entityManufacturer_.getAttribute("id"))); // 
            assertTrue(singularAttributeSet.contains(entityManufacturer_.getAttribute("version"))); //
            assertTrue(singularAttributeSet.contains(entityManufacturer_.getAttribute("name"))); //

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
             *              attribute
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
             *              attribute
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
             *              attribute
             *  @return ListAttribute of the given name and element type
             *  @throws IllegalArgumentException if attribute of the given
             *          name and type is not present in the managed type
             */
            //<E> ListAttribute<? super X, E> getList(String name, Class<E> elementType);

            
            /**
             *  Return the Collection-valued attribute declared by the 
             *  managed type that corresponds to the specified name and Java 
             *  element type.
             *  @param name  the name of the represented attribute
             *  @param elementType  the element type of the represented 
             *              attribute
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
                entityManufacturer_.getDeclaredCollection("locations", entityLocation_.getJavaType());
        } catch (IllegalArgumentException iae) {
            // expecting
            //java.lang.IllegalArgumentException: The attribute [locations] from the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] is not present.
            expectedIAExceptionThrown = true;            
            }
            assertTrue("Expected thrown IllegalArgumentException", expectedIAExceptionThrown);
            
            expectedIAExceptionThrown = false;            
        try {
            //<E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);
            // UC2 - the attribute is on the managedType (but is the wrong type)
            // Also avoid a CCE on a List attribute
            //java.lang.ClassCastException: org.eclipse.persistence.internal.jpa.metamodel.ListAttributeImpl cannot be cast to javax.persistence.metamodel.CollectionAttribute
            CollectionAttribute<Manufacturer, ?> anAttribute = 
                entityManufacturer_.getDeclaredCollection("hardwareDesigners", entityManufacturer_.getJavaType());
        } catch (IllegalArgumentException iae) {
            // expecting
            //java.lang.IllegalArgumentException: Expected attribute return type [COLLECTION] on the existing attribute [hardwareDesigners] on the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] but found attribute return type [LIST].
            expectedIAExceptionThrown = true;            
            }
            assertTrue("Expected thrown IllegalArgumentException", expectedIAExceptionThrown);            
            
            
            // FUTURE: We need a Collection (computers is a Set)
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
            
    /*            // FUTURE: We need a Collection on a superclass (computers is a Set)            
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
            assertTrue("Expected thrown IllegalArgumentException", expectedIAExceptionThrown);
    */            
            
            //     private Collection<Computer> corporateComputers = new HashSet<Computer>();
            // http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_91:_20091008:_ManagedType.getDeclaredAttribute.28.29_does_not_throw_expected_IAE_for_Entity.28target.29-MappedSuperclass-MappedSuperclass.28attribute.29_Hierarchy
            expectedIAExceptionThrown = false;            
        try {
            //<E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);
            // Note: internally EclipseLink treats a ONE_TO_MANY as a MANY_TO_MANY for the case of a unidirectional mapping on a MappedSuperclass            
            // UC4 - the attribute is on the immediate superclass and is the correct COLLECTION - we still get an IAE
            CollectionAttribute<Manufacturer, Computer> anAttribute = 
                entityManufacturer_.getDeclaredCollection("corporateComputers", entityComputer_.getJavaType());
        } catch (IllegalArgumentException iae) {
            // expecting
            // java.lang.IllegalArgumentException: Expected attribute return type [COLLECTION] on the existing attribute [corporateComputers] on the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] but found attribute return type [LIST].
            expectedIAExceptionThrown = true;            
            }
            assertTrue("Expected thrown IllegalArgumentException", expectedIAExceptionThrown);
            
            /**
             *  Return the Set-valued attribute declared by the managed type 
             *  that corresponds to the specified name and Java element type.
             *  @param name  the name of the represented attribute
             *  @param elementType  the element type of the represented 
             *              attribute
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
             *              attribute
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
                entityManufacturer_.getDeclaredList("locations", entityLocation_.getJavaType());
        } catch (IllegalArgumentException iae) {
            // expecting
            //java.lang.IllegalArgumentException: The attribute [locations] from the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] is not present.
            expectedIAExceptionThrown = true;            
            }
            assertTrue("Expected thrown IllegalArgumentException", expectedIAExceptionThrown);
            
            expectedIAExceptionThrown = false;            
        try {
            //<E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);
            // UC2 - the attribute is on the managedType (but is the wrong type)
            ListAttribute<Manufacturer, ?> anAttribute = 
                entityManufacturer_.getDeclaredList("hardwareDesigners", entityManufacturer_.getJavaType());
        } catch (IllegalArgumentException iae) {
            // expecting
            //java.lang.IllegalArgumentException: Expected attribute type [class org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer] on the existing attribute [hardwareDesigners] on the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] but found attribute type [class org.eclipse.persistence.testing.models.jpa.metamodel.HardwareDesigner].
            expectedIAExceptionThrown = true;            
            }
            assertTrue("Expected thrown IllegalArgumentException", expectedIAExceptionThrown);            
            
            expectedIAExceptionThrown = false;            
        try {
            //<E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);
            // UC3 - the attribute is on the managedType (not on any superType)
            ListAttribute<Manufacturer, HardwareDesigner> anAttribute = 
                entityManufacturer_.getDeclaredList("hardwareDesigners", entityHardwareDesigner_.getJavaType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);            
            
            //     private Collection<Computer> corporateComputers = new HashSet<Computer>();
            // http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_91:_20091008:_ManagedType.getDeclaredAttribute.28.29_does_not_throw_expected_IAE_for_Entity.28target.29-MappedSuperclass-MappedSuperclass.28attribute.29_Hierarchy
            expectedIAExceptionThrown = false;            
        try {
            //<E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);
            // UC4 - the attribute is on the immediate superclass
            // Note: internally EclipseLink treats a ONE_TO_MANY as a MANY_TO_MANY for the case of a unidirectional mapping on a MappedSuperclass            
            CollectionAttribute<Manufacturer, Computer> anAttribute = 
                entityManufacturer_.getDeclaredCollection("corporateComputers", entityComputer_.getJavaType());
        } catch (IllegalArgumentException iae) {
            // expecting
            // java.lang.IllegalArgumentException: The declared attribute [corporateComputers] from the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] is not present - however, it is declared on a superclass.
            expectedIAExceptionThrown = true;            
            }
            assertTrue("Expected thrown IllegalArgumentException", expectedIAExceptionThrown);
            
            
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
                entityManufacturer_.getDeclaredPluralAttributes();
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
                entityManufacturer_.getDeclaredAttribute("locations");//, entityLocation.getJavaType());
        } catch (IllegalArgumentException iae) {
            // expecting
            //java.lang.IllegalArgumentException: The attribute [locations] from the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] is not present.
            expectedIAExceptionThrown = true;            
            }
            assertTrue("Expected thrown IllegalArgumentException", expectedIAExceptionThrown);
            
            expectedIAExceptionThrown = false;            
        try {
            //<E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);
            // UC3 - the attribute is on the managedType (not on any superType)
            Attribute<Manufacturer, ?> anAttribute = 
                entityManufacturer_.getDeclaredAttribute("hardwareDesigners");//, entityHardwareDesigner.getJavaType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);            
            
            expectedIAExceptionThrown = false;            
        try {
            //<E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);
            // UC4 - the attribute is on the immediate superclass
            // Note: internally EclipseLink treats a ONE_TO_MANY as a MANY_TO_MANY for the case of a unidirectional mapping on a MappedSuperclass            
            Attribute<Manufacturer, ?> anAttribute = 
                entityManufacturer_.getDeclaredAttribute("corporateComputers");//, entityComputer.getJavaType());
        } catch (IllegalArgumentException iae) {
            // expecting
            // java.lang.IllegalArgumentException: The declared attribute [corporateComputers] from the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] is not present - however, it is declared on a superclass.
            expectedIAExceptionThrown = true;            
            }
            assertTrue("Expected thrown IllegalArgumentException", expectedIAExceptionThrown);

            expectedIAExceptionThrown = false;            
            Attribute<Manufacturer, ?> aListAttribute = null;
        try {
            //<E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);
            // the attribute is on the class
            aListAttribute = entityManufacturer_.getDeclaredAttribute("hardwareDesigners");//, entityComputer.getJavaType());
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
            IdentifiableType person = entityManufacturer_.getSupertype().getSupertype();
            aCollectionAttribute = person.getDeclaredAttribute("historicalEmps");//, entityComputer.getJavaType());
        } catch (IllegalArgumentException iae) {
            // expecting
            // java.lang.IllegalArgumentException: The declared attribute [corporateComputers] from the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] is not present - however, it is declared on a superclass.
            //iae.printStackTrace();
            expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);
            assertNotNull(aCollectionAttribute);
            // check managed type
            assertEquals(msPerson_, aCollectionAttribute.getDeclaringType());            
            // check element type
            //assertEquals(entityManufacturer, aCollectionAttribute.getDeclaringType());

            // positive: check one level down from the root
            expectedIAExceptionThrown = false;            
            Attribute<Corporation,?> aCollectionAttribute2 = null;
        try {
            //<E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);
            // the attribute is on the class
            IdentifiableType corporation = entityManufacturer_.getSupertype();
            // Note: internally EclipseLink treats a ONE_TO_MANY as a MANY_TO_MANY for the case of a unidirectional mapping on a MappedSuperclass            
            aCollectionAttribute2 = corporation.getDeclaredAttribute("corporateComputers");//, entityComputer.getJavaType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
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
            IdentifiableType corporation = entityManufacturer_.getSupertype();            
            aCollectionAttribute2 = corporation.getDeclaredAttribute("notFound");//, entityComputer.getJavaType());
        } catch (IllegalArgumentException iae) {
            // expecting
            // java.lang.IllegalArgumentException: The declared attribute [corporateComputers] from the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] is not present - however, it is declared on a superclass.
            expectedIAExceptionThrown = true;            
            }
            // we expect an IAE on getAttribute(name) if name does not exist
            assertTrue("Expected thrown IllegalArgumentException", expectedIAExceptionThrown);
            
            
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
            // UC4 - the attribute is on the immediate superclass
            // Note: internally EclipseLink treats a ONE_TO_MANY as a MANY_TO_MANY for the case of a unidirectional mapping on a MappedSuperclass            
            CollectionAttribute<Manufacturer, ?> anAttribute = 
                entityManufacturer_.getDeclaredCollection("corporateComputers");
        } catch (IllegalArgumentException iae) {
            // expecting
            // java.lang.IllegalArgumentException: The declared attribute [corporateComputers] from the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] is not present - however, it is declared on a superclass.
            expectedIAExceptionThrown = true;            
            }
            // IAE because the attribute is declared one level above
            assertTrue("Expected thrown IllegalArgumentException", expectedIAExceptionThrown);

            expectedIAExceptionThrown = false;
        try {
            // Note: internally EclipseLink treats a ONE_TO_MANY as a MANY_TO_MANY for the case of a unidirectional mapping on a MappedSuperclass            
            CollectionAttribute<Corporation, ?> anAttribute = 
                msCorporation_.getDeclaredCollection("corporateComputers");
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);
            
            expectedIAExceptionThrown = false;            
        try {
            //<E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);
            // UC4 - the attribute is on the immediate superclass
            // Note: internally EclipseLink treats a ONE_TO_MANY as a MANY_TO_MANY for the case of a unidirectional mapping on a MappedSuperclass            
            CollectionAttribute<Manufacturer, Computer> anAttribute = 
                entityManufacturer_.getDeclaredCollection("corporateComputers", entityComputer_.getJavaType());
        } catch (IllegalArgumentException iae) {
            // expecting
            // java.lang.IllegalArgumentException: The declared attribute [corporateComputers] from the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] is not present - however, it is declared on a superclass.
            expectedIAExceptionThrown = true;            
            }
            assertTrue("Expected thrown IllegalArgumentException", expectedIAExceptionThrown);
            // reset state
            expectedIAExceptionThrown = false;
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
            
            
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything different?
    public void testManagedType_getDeclaredAttributes_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);
            MappedSuperclassTypeImpl<Corporation> msCorporation_ = (MappedSuperclassTypeImpl)metamodel.managedType(Corporation.class);
            assertNotNull(msCorporation_);
            EntityTypeImpl<GalacticPosition> entityLocation_ =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything different?
    public void testManagedType_getSingularAttribute_Type_param_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);
            MappedSuperclassTypeImpl<Corporation> msCorporation_ = (MappedSuperclassTypeImpl)metamodel.managedType(Corporation.class);
            assertNotNull(msCorporation_);
            EntityTypeImpl<GalacticPosition> entityLocation_ =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything different?
    public void testManagedType_getDeclaredSingularAttribute_Type_param_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);
            MappedSuperclassTypeImpl<Corporation> msCorporation_ = (MappedSuperclassTypeImpl)metamodel.managedType(Corporation.class);
            assertNotNull(msCorporation_);
            EntityTypeImpl<GalacticPosition> entityLocation_ =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything different?
    public void testManagedType_getSingularAttributes_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);
            MappedSuperclassTypeImpl<Corporation> msCorporation_ = (MappedSuperclassTypeImpl)metamodel.managedType(Corporation.class);
            assertNotNull(msCorporation_);
            EntityTypeImpl<GalacticPosition> entityLocation_ =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything different?
    public void testManagedType_getDeclaredSingularAttributes_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);
            MappedSuperclassTypeImpl<Corporation> msCorporation_ = (MappedSuperclassTypeImpl)metamodel.managedType(Corporation.class);
            assertNotNull(msCorporation_);
            EntityTypeImpl<GalacticPosition> entityLocation_ =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything different?
    public void testManagedType_getCollection_Type_param_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);
            MappedSuperclassTypeImpl<Corporation> msCorporation_ = (MappedSuperclassTypeImpl)metamodel.managedType(Corporation.class);
            assertNotNull(msCorporation_);
            EntityTypeImpl<GalacticPosition> entityLocation_ =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything different?
    public void testManagedType_getDeclaredCollection_Type_param_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);
            MappedSuperclassTypeImpl<Corporation> msCorporation_ = (MappedSuperclassTypeImpl)metamodel.managedType(Corporation.class);
            assertNotNull(msCorporation_);
            EntityTypeImpl<GalacticPosition> entityLocation_ =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything different?
    public void testManagedType_getSet_Type_param_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);
            MappedSuperclassTypeImpl<Corporation> msCorporation_ = (MappedSuperclassTypeImpl)metamodel.managedType(Corporation.class);
            assertNotNull(msCorporation_);
            EntityTypeImpl<GalacticPosition> entityLocation_ =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything different?
    public void testManagedType_getDeclaredSet_Type_param_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);
            MappedSuperclassTypeImpl<Corporation> msCorporation_ = (MappedSuperclassTypeImpl)metamodel.managedType(Corporation.class);
            assertNotNull(msCorporation_);
            EntityTypeImpl<GalacticPosition> entityLocation_ =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything different?
    public void testManagedType_getList_Type_param_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);
            MappedSuperclassTypeImpl<Corporation> msCorporation_ = (MappedSuperclassTypeImpl)metamodel.managedType(Corporation.class);
            assertNotNull(msCorporation_);
            EntityTypeImpl<GalacticPosition> entityLocation_ =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything different?
    public void testManagedType_getDeclaredList_Type_param_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);
            MappedSuperclassTypeImpl<Corporation> msCorporation_ = (MappedSuperclassTypeImpl)metamodel.managedType(Corporation.class);
            assertNotNull(msCorporation_);
            EntityTypeImpl<GalacticPosition> entityLocation_ =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }
    
    public void testManagedType_getMap_Type_param_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);
            MappedSuperclassTypeImpl<Corporation> msCorporation_ = (MappedSuperclassTypeImpl)metamodel.managedType(Corporation.class);
            assertNotNull(msCorporation_);
            EntityTypeImpl<GalacticPosition> entityLocation_ =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);

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
            expectedIAExceptionThrown = false;            
            MapAttribute<? super Manufacturer, ?, ?> anAttribute = 
            entityManufacturer_.getMap("hardwareDesignersMap", Integer.class,  HardwareDesigner.class);
            // verify the default key type is the not the Map key - rather that is is the managedType PK
            Class keyJavaType = anAttribute.getKeyJavaType();
            //@OneToMany(cascade=ALL, mappedBy="mappedEmployer")
            //private Map<String, HardwareDesigner> hardwareDesignersMap;// = new HashMap<String, HardwareDesigner>();
            // http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_63:_20090824:_Add_Map_support_for_.40MapKey_to_MapAttribute
            // Key is the primary key (PK) of the target entity - in this case HardwareDesigner which inherits its @Id from the Person @MappedSuperclass as '''Integer'''.
            Type keyType = anAttribute.getKeyType(); 
            assertEquals(String.class, keyJavaType); // When @MapKey(name="name") is present - or generics are set
            //assertEquals(Integer.class, keyJavaType); // When @MapKey or generics are not present - we default to the PK
            assertNotNull(keyType);
            assertTrue(keyType instanceof Type);
            assertEquals(Type.PersistenceType.BASIC, keyType.getPersistenceType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything?
    public void testManagedType_getDeclaredMap_Type_param_declared_above_Method() {
    
    }

    // TODO: This is not testing anything?
    public void testManagedType_getDeclaredMap_Type_param_not_found_iae_Method() {
        
    }

    public void testManagedType_getDeclaredMap_Type_param_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);
            MappedSuperclassTypeImpl<Corporation> msCorporation_ = (MappedSuperclassTypeImpl)metamodel.managedType(Corporation.class);
            assertNotNull(msCorporation_);
            EntityTypeImpl<GalacticPosition> entityLocation_ =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);

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
            expectedIAExceptionThrown = false;            
            MapAttribute<? super Manufacturer, ?, ?> anAttribute = 
            entityManufacturer_.getDeclaredMap("hardwareDesignersMap", Integer.class,  HardwareDesigner.class);
            // verify the default key type is the not the Map key - rather that is is the managedType PK
            Class keyJavaType = anAttribute.getKeyJavaType();
            //@OneToMany(cascade=ALL, mappedBy="mappedEmployer")
            //private Map<String, HardwareDesigner> hardwareDesignersMap;// = new HashMap<String, HardwareDesigner>();
            // http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_63:_20090824:_Add_Map_support_for_.40MapKey_to_MapAttribute
            // Key is the primary key (PK) of the target entity - in this case HardwareDesigner which inherits its @Id from the Person @MappedSuperclass as '''Integer'''.
            Type keyType = anAttribute.getKeyType(); 
            assertEquals(String.class, keyJavaType); // When @MapKey(name="name") is present - or generics are set
            //assertEquals(Integer.class, keyJavaType); // When @MapKey or generics are not present - we default to the PK
            assertNotNull(keyType);
            assertTrue(keyType instanceof Type);
            assertEquals(Type.PersistenceType.BASIC, keyType.getPersistenceType());            
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything?
    public void testManagedType_getPluralAttributes_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);
            MappedSuperclassTypeImpl<Corporation> msCorporation_ = (MappedSuperclassTypeImpl)metamodel.managedType(Corporation.class);
            assertNotNull(msCorporation_);
            EntityTypeImpl<GalacticPosition> entityLocation_ =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }
    
    public void testManagedType_getDeclaredPluralAttributes_internal_entity_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);
            MappedSuperclassTypeImpl<Corporation> msCorporation_ = (MappedSuperclassTypeImpl)metamodel.managedType(Corporation.class);
            assertNotNull(msCorporation_);
            EntityTypeImpl<GalacticPosition> entityLocation_ =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);

            // This also tests getCollections()
            // Here we start with 6 attributes in getAttributes() - this is reduced to 3 in getCollections before declared filtering
            // In declaredCollections we reduce this to 2 because one of the types "corporateComputers" is on a mappedSuperclass
            expectedIAExceptionThrown = false;            
        try {
            Set<PluralAttribute<Manufacturer, ?, ?>> collections = 
                entityManufacturer_.getDeclaredPluralAttributes();
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);            
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    public void testManagedType_getDeclaredPluralAttributes_root_entity_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            // This also tests getCollections()
            expectedIAExceptionThrown = false;            
            Set<PluralAttribute<Computer, ?, ?>> collections =
            entityComputer_.getDeclaredPluralAttributes();
            assertNotNull(collections);
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // Requires test model expansion before enabling test
    public void testManagedType_getDeclaredPluralAttributes_root_mappedSuperclass_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);

            // This also tests getCollections()
            expectedIAExceptionThrown = false;
            // historicalComputers is defined as a plural declared attribute on a root mappedSuperclass
            Set<PluralAttribute<Person, ?, ?>> collections = 
            msPerson_.getDeclaredPluralAttributes();
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }
    
    public void testManagedType_getAttribute_on_Entity_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);
            
            // Test case
            Attribute anAttribute = entityManufacturer_.getAttribute("hardwareDesignersMapUC4");
            assertNotNull(anAttribute);
            assertEquals(PersistentAttributeType.ONE_TO_MANY, anAttribute.getPersistentAttributeType());
            assertTrue(((AttributeImpl)anAttribute).isPlural());
            assertTrue(anAttribute instanceof MapAttribute);
            assertEquals(entityHardwareDesigner_, ((MapAttribute)anAttribute).getElementType());
            assertEquals(CollectionType.MAP, ((MapAttribute)anAttribute).getCollectionType());            
            assertEquals(String.class, ((MapAttribute)anAttribute).getKeyJavaType());
            
            assertTrue(((AttributeImpl)anAttribute).isPlural());
            assertTrue(anAttribute instanceof PluralAttribute);
            Bindable aPluralAttribute = entityManufacturer_.getMap("hardwareDesignersMapUC4");
            assertEquals(HardwareDesigner.class, aPluralAttribute.getBindableJavaType());
            assertEquals(Map.class, anAttribute.getJavaType());
            assertEquals(PersistentAttributeType.ONE_TO_MANY, anAttribute.getPersistentAttributeType());
            
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    public void testManagedType_getAttribute_on_MappedSuperclass_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            ManagedType<Corporation> msCorporation_  = metamodel.managedType(Corporation.class);
            assertNotNull(msCorporation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            
            
            // Test case
            // Note: internally EclipseLink treats a ONE_TO_MANY as a MANY_TO_MANY for the case of a unidirectional mapping on a MappedSuperclass
            Attribute anAttribute = msCorporation_.getAttribute("corporateComputers");
            assertNotNull(anAttribute);
            assertEquals(PersistentAttributeType.ONE_TO_MANY, anAttribute.getPersistentAttributeType());
            assertTrue(((AttributeImpl)anAttribute).isPlural());
            assertTrue(anAttribute instanceof CollectionAttribute);
            assertEquals(entityComputer_, ((CollectionAttribute)anAttribute).getElementType());
            assertEquals(CollectionType.COLLECTION, ((CollectionAttribute)anAttribute).getCollectionType());            
            
            assertTrue(((AttributeImpl)anAttribute).isPlural());
            assertTrue(anAttribute instanceof PluralAttribute);
            Bindable aPluralAttribute = msCorporation_.getCollection("corporateComputers");
            assertEquals(Computer.class, aPluralAttribute.getBindableJavaType());
            assertEquals(Collection.class, anAttribute.getJavaType());
            assertEquals(PersistentAttributeType.ONE_TO_MANY, anAttribute.getPersistentAttributeType());
            
            
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }
    
    public void testManagedType_getAttribute_doesNotExist_on_Entity_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
            Attribute anAttribute = null;            
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);
            
            // Test case
            anAttribute = entityManufacturer_.getAttribute("does_not_exist");
        } catch (IllegalArgumentException iae) {
            // We expect an exception
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertNull(anAttribute);            
            assertTrue("An IAE exception should have occured on a non-existent attribute.", expectedIAExceptionThrown);
        }
    }

    public void testManagedType_getAttribute_doesNotExist_on_MappedSuperclass_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
            Attribute anAttribute = null;            
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            ManagedType<Corporation> msCorporation_  = metamodel.managedType(Corporation.class);
            assertNotNull(msCorporation_);
            
            // Test case
            anAttribute = msCorporation_.getAttribute("does_not_exist");
        } catch (IllegalArgumentException iae) {
            // We expect an exception
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertNull(anAttribute);            
            assertTrue("An IAE exception should have occured on a non-existent attribute.", expectedIAExceptionThrown);
        }
    }
    
    public void testManagedType_getDeclaredAttribute_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);
            
            // Test case
            Attribute anAttribute = entityManufacturer_.getDeclaredAttribute("hardwareDesignersMapUC4");
            assertNotNull(anAttribute);
            assertEquals(PersistentAttributeType.ONE_TO_MANY, anAttribute.getPersistentAttributeType());
            assertTrue(((AttributeImpl)anAttribute).isPlural());
            assertTrue(anAttribute instanceof MapAttribute);
            assertEquals(entityHardwareDesigner_, ((MapAttribute)anAttribute).getElementType());
            assertEquals(CollectionType.MAP, ((MapAttribute)anAttribute).getCollectionType());            
            assertEquals(String.class, ((MapAttribute)anAttribute).getKeyJavaType());
            
            assertTrue(((AttributeImpl)anAttribute).isPlural());
            assertTrue(anAttribute instanceof PluralAttribute);
            Bindable aPluralAttribute = entityManufacturer_.getMap("hardwareDesignersMapUC4");
            assertEquals(HardwareDesigner.class, aPluralAttribute.getBindableJavaType());
            assertEquals(Map.class, anAttribute.getJavaType());
            assertEquals(PersistentAttributeType.ONE_TO_MANY, anAttribute.getPersistentAttributeType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    public void testManagedType_getDeclaredListAttribute_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);
            
            // Test case
            Attribute anAttribute = entityManufacturer_.getDeclaredAttribute("hardwareDesigners");
            assertNotNull(anAttribute);
            assertEquals(PersistentAttributeType.ONE_TO_MANY, anAttribute.getPersistentAttributeType());
            assertTrue(((AttributeImpl)anAttribute).isPlural());
            assertTrue(anAttribute instanceof ListAttribute);
            assertEquals(entityHardwareDesigner_, ((ListAttribute)anAttribute).getElementType());
            assertEquals(CollectionType.LIST, ((ListAttribute)anAttribute).getCollectionType());            
            
            assertTrue(((AttributeImpl)anAttribute).isPlural());
            assertTrue(anAttribute instanceof PluralAttribute);
            Bindable aPluralAttribute = entityManufacturer_.getDeclaredList("hardwareDesigners");
            assertEquals(HardwareDesigner.class, aPluralAttribute.getBindableJavaType());
            assertEquals(List.class, anAttribute.getJavaType());
            assertEquals(PersistentAttributeType.ONE_TO_MANY, anAttribute.getPersistentAttributeType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }
    
    public void testManagedType_getDeclaredAttribute_above_throws_iae_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
            Attribute anAttribute = null;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);

            // Test case
            // attribute is declared on a mappedsuperclass 
            // Note: internally EclipseLink treats a ONE_TO_MANY as a MANY_TO_MANY for the case of a unidirectional mapping on a MappedSuperclass            
            anAttribute = entityManufacturer_.getDeclaredAttribute("corporateComputers ");
        } catch (IllegalArgumentException iae) {
            // We expect and exception
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertNull(anAttribute);            
            assertTrue("An IAE exception should have occured on an attribute declared above this entity.", expectedIAExceptionThrown);
        }
    }

    public void testManagedType_getDeclaredAttribute_doesNotExist_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
            Attribute anAttribute = null;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);

            // Test case
            anAttribute = entityManufacturer_.getDeclaredAttribute("does_not_exist");
        } catch (IllegalArgumentException iae) {
            // We expect and exception
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertNull(anAttribute);            
            assertTrue("An IAE exception should have occured on a non-existent attribute.", expectedIAExceptionThrown);
        }
    }
    
    
    public void testManagedType_getSingularAttribute_BASIC_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<GalacticPosition> entityLocation_ =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            
            // Test case
            Attribute anAttribute = entityComputer_.getSingularAttribute("version");
            assertNotNull(anAttribute);
            assertEquals(PersistentAttributeType.BASIC, anAttribute.getPersistentAttributeType());
            assertFalse(((AttributeImpl)anAttribute).isPlural());
            assertTrue(anAttribute instanceof SingularAttribute);
            assertEquals(int.class, anAttribute.getJavaType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything?
    public void testManagedType_getSingularAttribute_EMBEDDED_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<GalacticPosition> entityLocation_ =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            
            // not implemented
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }
    
    public void testManagedType_getDeclaredSingularAttribute_on_Entity_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<GalacticPosition> entityLocation_ =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            
            // Test case
            Attribute anAttribute = entityComputer_.getSingularAttribute("location");
            assertNotNull(anAttribute);
            assertEquals(PersistentAttributeType.ONE_TO_ONE, anAttribute.getPersistentAttributeType());
            assertFalse(((AttributeImpl)anAttribute).isPlural());
            assertTrue(anAttribute instanceof SingularAttribute);
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    public void testManagedType_getDeclaredSingularAttribute_on_MappedSuperclass_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);
            
            // Test case
            Attribute anAttribute = msPerson_.getSingularAttribute("name");
            assertNotNull(anAttribute);
            assertEquals(PersistentAttributeType.BASIC, anAttribute.getPersistentAttributeType());
            assertFalse(((AttributeImpl)anAttribute).isPlural());
            assertTrue(anAttribute instanceof SingularAttribute);
            assertEquals(String.class, anAttribute.getJavaType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything?
    public void testManagedType_getCollection_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);
            MappedSuperclassTypeImpl<Corporation> msCorporation_ = (MappedSuperclassTypeImpl)metamodel.managedType(Corporation.class);
            assertNotNull(msCorporation_);
            EntityTypeImpl<GalacticPosition> entityLocation_ =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything?
    public void testManagedType_getDeclaredCollection_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);
            MappedSuperclassTypeImpl<Corporation> msCorporation_ = (MappedSuperclassTypeImpl)metamodel.managedType(Corporation.class);
            assertNotNull(msCorporation_);
            EntityTypeImpl<GalacticPosition> entityLocation_ =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }
    
    public void testManagedType_getSet_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);
            MappedSuperclassTypeImpl<Corporation> msCorporation_ = (MappedSuperclassTypeImpl)metamodel.managedType(Corporation.class);
            assertNotNull(msCorporation_);
            EntityTypeImpl<GalacticPosition> entityLocation_ =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);
            SetAttribute<? super Manufacturer, Computer> computersAttribute = 
            entityManufacturer_.getSet("computers", Computer.class);
            assertNotNull("computers SetAttribute should not be null in Manufacturer_", computersAttribute);
            EntityTypeImpl<MultiCoreCPU> entityMultiCoreCPU_ = (EntityTypeImpl)metamodel.entity(MultiCoreCPU.class);
            assertNotNull(entityMultiCoreCPU_);
            SetAttribute<? super MultiCoreCPU, Core> processorsAttribute = 
                entityMultiCoreCPU_.getSet("cores", Core.class);
            assertNotNull("cores SetAttribute should not be null in MultiCoreCPU_", processorsAttribute);

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything?
    public void testManagedType_getDeclaredSet_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);
            MappedSuperclassTypeImpl<Corporation> msCorporation_ = (MappedSuperclassTypeImpl)metamodel.managedType(Corporation.class);
            assertNotNull(msCorporation_);
            EntityTypeImpl<GalacticPosition> entityLocation_ =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything?
    public void testManagedType_getList_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);
            MappedSuperclassTypeImpl<Corporation> msCorporation_ = (MappedSuperclassTypeImpl)metamodel.managedType(Corporation.class);
            assertNotNull(msCorporation_);
            EntityTypeImpl<GalacticPosition> entityLocation_ =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything?
    public void testManagedType_getDeclaredList_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);
            MappedSuperclassTypeImpl<Corporation> msCorporation_ = (MappedSuperclassTypeImpl)metamodel.managedType(Corporation.class);
            assertNotNull(msCorporation_);
            EntityTypeImpl<GalacticPosition> entityLocation_ =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything?
    public void testManagedType_getMap_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);
            MappedSuperclassTypeImpl<Corporation> msCorporation_ = (MappedSuperclassTypeImpl)metamodel.managedType(Corporation.class);
            assertNotNull(msCorporation_);
            EntityTypeImpl<GalacticPosition> entityLocation_ =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything?
    public void testManagedType_getDeclaredMap_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);
            MappedSuperclassTypeImpl<Corporation> msCorporation_ = (MappedSuperclassTypeImpl)metamodel.managedType(Corporation.class);
            assertNotNull(msCorporation_);
            EntityTypeImpl<GalacticPosition> entityLocation_ =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }
    
    public void testManagedType_variantCases() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            MappedSuperclassTypeImpl<Person> msPerson_ = (MappedSuperclassTypeImpl)metamodel.managedType(Person.class);
            assertNotNull(msPerson_);
            MappedSuperclassTypeImpl<Corporation> msCorporation_ = (MappedSuperclassTypeImpl)metamodel.managedType(Corporation.class);
            assertNotNull(msCorporation_);
            EntityTypeImpl<GalacticPosition> entityLocation_ =(EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);
            EntityTypeImpl<Memory> entityMemory_ = (EntityTypeImpl)metamodel.entity(Memory.class);
            assertNotNull(entityMemory_);

            // Actual Test Case
            // try a getAttribute on a missing attribute - should cause an IAE
            boolean iae1thrown = false;
        try {
            entityManufacturer_.getAttribute("_unknownAttribute");
        } catch (IllegalArgumentException expectedIAE) {
            // java.lang.IllegalArgumentException: The attribute [_unknownAttribute] from the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] is not present.
            iae1thrown = true;
            }
            // verify that we got an expected exception
            assertTrue("Expected thrown IllegalArgumentException", iae1thrown);

            // try a getSet on an unknown Set attribute - should still cause a IAE
            iae1thrown = false;
        try {
            entityManufacturer_.getSet("_unknownAttribute");
        } catch (IllegalArgumentException expectedIAE) {
            // java.lang.IllegalArgumentException: The attribute [_unknownAttribute] from the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] is not present.
            iae1thrown = true;
            }
            // verify that we got an expected exception
            assertTrue("Expected thrown IllegalArgumentException", iae1thrown);
            
            // try a getSet on an unknown Set attribute - but with the right type (but how do we really know the type) - should still cause the same IAE
            iae1thrown = false;
        try {
            entityManufacturer_.getSet("_unknownSet", entityComputer_.getJavaType());
        } catch (IllegalArgumentException expectedIAE) {
            // java.lang.IllegalArgumentException: The attribute [_unknownSet] from the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] is not present.
            iae1thrown = true;
            }
            // verify that we got an expected exception
            assertTrue("Expected thrown IllegalArgumentException", iae1thrown);

            // try a getSet on a known Set attribute - but with the wrong type like another EntityType Memory - should cause a different IAE
            iae1thrown = false;
        try {
            entityManufacturer_.getSet("computers", entityMemory_.getJavaType());
        } catch (IllegalArgumentException expectedIAE) {
            //expectedIAE.printStackTrace();
            //java.lang.IllegalArgumentException: Expected attribute type [class org.eclipse.persistence.testing.models.jpa.metamodel.Memory] on the existing attribute [computers] on the managed type [ManagedTypeImpl[RelationalDescriptor(org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer --> [DatabaseTable(CMP3_MM_MANUF)])]] but found attribute type [org.eclipse.persistence.testing.models.jpa.metamodel.Computer].
            iae1thrown = true;
        } catch (Exception unexpectedException) {
            unexpectedException.printStackTrace();
            }
            // verify that we got an expected exception
            assertTrue("Expected thrown IllegalArgumentException", iae1thrown);

            expectedIAExceptionThrown = false;
        try {
            // Ask for a Collection using a String type - invalid
            entityManufacturer_.getDeclaredCollection("name", String.class);
        } catch (Exception e) {
            // This exception is expected here
            expectedIAExceptionThrown = true;            
            }
            assertTrue("Expected thrown IllegalArgumentException", expectedIAExceptionThrown);
            // reset exception flag
            expectedIAExceptionThrown = false;
            
            // get Mapped Superclass objects
            //Map<Class, MappedSuperclassTypeImpl<?>> mappedSuperclasses = ((MetamodelImpl)metamodel).getMappedSuperclasses();        
            Set<MappedSuperclassTypeImpl<?>> mappedSuperclasses = ((MetamodelImpl)metamodel).getMappedSuperclasses();
            int count = 0;
            for (MappedSuperclassTypeImpl msType_ : mappedSuperclasses) {            
                ClassDescriptor descriptor = msType_.getDescriptor();
                for (DatabaseMapping mapping : descriptor.getMappings()) {
                    count++;
                }
            }
            
            // we should have had a non-zero number of mappings on the descriptors
            if(count < 1) {
            fail("No mappedSuperclass mappings were found");
            }
            assertTrue(count > 0);
            
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }
    
    /**
     * This test will verify that MapAttribute instance have their elementType set correctly.
     * The elementType corresponds to the 3rd V parameter on the class definition - which is the Map value.
     * MapAttributeImpl<X, K, V> 
     */
    public void testMapAtributeElementTypeWhenMapKeySetButNameAttributeIsDefaulted() {
        boolean exceptionThrown = false;
        EntityManager em = null;            
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            
            EntityType<Manufacturer> entityManufacturer = metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer);            
            Attribute hardwareDesignersMap = entityManufacturer.getAttribute("hardwareDesignersMapUC8");
            assertNotNull(hardwareDesignersMap);
            assertTrue(hardwareDesignersMap.isCollection());
            assertTrue(hardwareDesignersMap instanceof MapAttributeImpl);
            MapAttribute<? super Manufacturer, ?, ?> manufactuerHardwareDesignersMap = entityManufacturer.getMap("hardwareDesignersMapUC8");            
            // Verify owning type
            assertNotNull(manufactuerHardwareDesignersMap);
            assertEquals(entityManufacturer, manufactuerHardwareDesignersMap.getDeclaringType());            
            // Verify Map Key - should be PK of owning type
            assertEquals(Integer.class, manufactuerHardwareDesignersMap.getKeyJavaType());            
            // Verify Map Value
            assertEquals(HardwareDesigner.class, manufactuerHardwareDesignersMap.getElementType().getJavaType());            
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    public void testMapAtributeElementTypeWhenMapKeySetAndNameAttributeSet() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            
            EntityType<Manufacturer> entityManufacturer = metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer);            
            Attribute hardwareDesignersMap = entityManufacturer.getAttribute("hardwareDesignersMapUC4");
            assertNotNull(hardwareDesignersMap);
            assertTrue(hardwareDesignersMap.isCollection());
            assertTrue(hardwareDesignersMap instanceof MapAttributeImpl);
            MapAttribute<? super Manufacturer, ?, ?> manufactuerHardwareDesignersMap = entityManufacturer.getMap("hardwareDesignersMapUC4");            
            // Verify owning type
            assertNotNull(manufactuerHardwareDesignersMap);
            assertEquals(entityManufacturer, manufactuerHardwareDesignersMap.getDeclaringType());            
            // Verify Map Key - should not be PK of owning type
            assertEquals(String.class, manufactuerHardwareDesignersMap.getKeyJavaType());            
            // Verify Map Value
            assertEquals(HardwareDesigner.class, manufactuerHardwareDesignersMap.getElementType().getJavaType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }
    
    public void testMapAttribute_getKeyJavaType_UC0_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);

            // Actual Test Case
            /**
             * Return the Java type of the map key.
             * @return Java key type
             */
            //Class<K> getKeyJavaType();
            MapAttribute<? super Manufacturer, ?, ?> anAttribute = 
            entityManufacturer_.getMap("hardwareDesignersMap");
            // verify the default key type is the not the Map key - rather that is is the managedType PK
            Class keyJavaType = anAttribute.getKeyJavaType();
            //@OneToMany(cascade=ALL, mappedBy="mappedEmployer")
            //private Map<String, HardwareDesigner> hardwareDesignersMap;// = new HashMap<String, HardwareDesigner>();
            // http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_63:_20090824:_Add_Map_support_for_.40MapKey_to_MapAttribute
            // Key is the primary key (PK) of the target entity - in this case HardwareDesigner which inherits its @Id from the Person @MappedSuperclass as '''Integer'''.
            Type keyType = anAttribute.getKeyType(); 
            assertEquals(String.class, keyJavaType); // When @MapKey(name="name") is present - or generics are set
            assertNotNull(keyType);
            assertTrue(keyType instanceof Type);
            assertEquals(Type.PersistenceType.BASIC, keyType.getPersistenceType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    public void testMapAttribute_getKeyJavaType_UC1a_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);

            // Actual Test Case
            /**
             * Return the Java type of the map key.
             * @return Java key type
             */
            //Class<K> getKeyJavaType();
            MapAttribute<? super Manufacturer, ?, ?> anAttribute = 
            entityManufacturer_.getMap("hardwareDesignersMapUC1a");
            // verify the key type is the Map key - not the managedType PK
            Class keyJavaType = anAttribute.getKeyJavaType();
            // UC 1a: Generics KV set, no @MapKey present, PK is singular field
            //@OneToMany(cascade=ALL, mappedBy="mappedEmployerUC1a")
            //private Map<String, HardwareDesigner> hardwareDesignersMapUC1a;
            Type keyType = anAttribute.getKeyType(); 
            assertEquals(String.class, keyJavaType); // When @MapKey(name="name") is present - or generics are set
            assertNotNull(keyType);
            assertTrue(keyType instanceof Type);
            assertEquals(Type.PersistenceType.BASIC, keyType.getPersistenceType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    public void testMapAttribute_getKeyJavaType_UC2_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);

            // Actual Test Case
            /**
             * Return the Java type of the map key.
             * @return Java key type
             */
            //Class<K> getKeyJavaType();
            MapAttribute<? super Manufacturer, ?, ?> anAttribute = 
            entityManufacturer_.getMap("hardwareDesignersMapUC2");
            // verify the key type is the Map key - not the managedType PK
            Class keyJavaType = anAttribute.getKeyJavaType();
            // UC 2: Generics KV set, @MapKey is present
            //@OneToMany(cascade=ALL, mappedBy="mappedEmployerUC2")
            //@MapKey(name="name")
            //private Map<String, HardwareDesigner> hardwareDesignersMapUC2;
            Type keyType = anAttribute.getKeyType(); 
            assertEquals(String.class, keyJavaType); // When @MapKey(name="name") is present
            assertNotNull(keyType);
            assertTrue(keyType instanceof Type);
            assertEquals(Type.PersistenceType.BASIC, keyType.getPersistenceType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    public void testMapAttribute_getKeyJavaType_UC4_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);

            // Actual Test Case
            /**
             * Return the Java type of the map key.
             * @return Java key type
             */
            //Class<K> getKeyJavaType();
            MapAttribute<? super Manufacturer, ?, ?> anAttribute = 
            entityManufacturer_.getMap("hardwareDesignersMapUC4");
            // verify the key type is the Map key - not the managedType PK
            Class keyJavaType = anAttribute.getKeyJavaType();
            // UC 4: No Generics KV set, @MapKey is present
            //@OneToMany(targetEntity=HardwareDesigner.class, cascade=ALL, mappedBy="mappedEmployerUC4")
            //@MapKey(name="name")
            //private Map hardwareDesignersMapUC4;
            Type keyType = anAttribute.getKeyType(); 
            assertEquals(String.class, keyJavaType); // When @MapKey(name="name") is present
            assertNotNull(keyType);
            assertTrue(keyType instanceof Type);
            assertEquals(Type.PersistenceType.BASIC, keyType.getPersistenceType());

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    public void testMapAttribute_getKeyJavaType_UC7_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);

            // Actual Test Case
            /**
             * Return the Java type of the map key.
             * @return Java key type
             */
            //Class<K> getKeyJavaType();
            MapAttribute<? super Manufacturer, ?, ?> anAttribute = 
            entityManufacturer_.getMap("hardwareDesignersMapUC7");
            // verify the key type is the Map key - not the managedType PK
            Class keyJavaType = anAttribute.getKeyJavaType();
            // UC 7: Generics KV set, targetEntity is also set, @MapKey is *(set/unset)
            //@OneToMany(targetEntity=HardwareDesigner.class, cascade=ALL, mappedBy="mappedEmployerUC7")
            // Same as UC1a - that is missing the @MapKey
            //private Map<String, HardwareDesigner> hardwareDesignersMapUC7;
            Type keyType = anAttribute.getKeyType(); 
            assertEquals(String.class, keyJavaType); // When @MapKey(name="name") is present - or generics are set
            assertNotNull(keyType);
            assertTrue(keyType instanceof Type);
            assertEquals(Type.PersistenceType.BASIC, keyType.getPersistenceType());

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }
    
    public void testMapAttribute_getKeyJavaType_UC8_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);

            // Actual Test Case
            /**
             * Return the Java type of the map key.
             * @return Java key type
             */
            //Class<K> getKeyJavaType();
            MapAttribute<? super Manufacturer, ?, ?> anAttribute = 
            entityManufacturer_.getMap("hardwareDesignersMapUC8");
            // verify the key type is the Map key - not the managedType PK
            Class keyJavaType = anAttribute.getKeyJavaType();
            // UC 8: Generics KV set, targetEntity not set, @MapKey is set but name attribute is defaulted
            //@OneToMany(targetEntity=HardwareDesigner.class, cascade=ALL, mappedBy="mappedEmployerUC8")
            // Same as UC1a - that is missing the @MapKey name attribute
            //private Map<String, HardwareDesigner> hardwareDesignersMapUC8;
            Type keyType = anAttribute.getKeyType(); 
            assertEquals(Integer.class, keyJavaType); // When @MapKey or generics are not present - we default to the PK
            assertNotNull(keyType);
            assertTrue(keyType instanceof Type);
            assertEquals(Type.PersistenceType.BASIC, keyType.getPersistenceType());

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    // See
    // http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_86:_20090921:_Handle_Embeddable_Type_keyType_in_MapAttributeImpl_constructor
    public void testMapAttribute_getKeyJavaType_UC9_DI86_Embeddable_IdClass_keyType_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);

            // Actual Test Case
            /**
             * Return the Java type of the map key.
             * @return Java key type
             */
            //Class<K> getKeyJavaType();
            MapAttribute<? super Manufacturer, ?, ?> anAttribute = 
            entityManufacturer_.getMap("enclosureByBoardMapUC9");
            // verify the key type is the Map key - not the managedType PK
            Class keyJavaType = anAttribute.getKeyJavaType();
            // UC9: no targetEntity, no MapKey, but generics are set (MapKey has an IdClass with an Embeddable)
            //@OneToMany(cascade=CascadeType.ALL, mappedBy="mappedManufacturerUC9")
            //private Map<Board, Enclosure> enclosureByBoardMapUC9;
            Type keyType = anAttribute.getKeyType(); 
            assertEquals(Board.class, keyJavaType); // When @MapKey(name="name") is present or we use generics
            assertNotNull(keyType);
            assertTrue(keyType instanceof Type);
            assertEquals(Type.PersistenceType.ENTITY, keyType.getPersistenceType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }
    
    public void testMapAttribute_getKeyType_UC0_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);

            // Actual Test Case
            /**
             * Return the type representing the key type of the map.
             * @return type representing key type
             */
            //Type<K> getKeyType();
            MapAttribute<? super Manufacturer, ?, ?> anAttribute = 
            entityManufacturer_.getMap("hardwareDesignersMap");
            // verify the default key type is the not the Map key - rather that is is the managedType PK
            Class keyJavaType = anAttribute.getKeyJavaType();
            //@OneToMany(cascade=ALL, mappedBy="mappedEmployer")
            //private Map<String, HardwareDesigner> hardwareDesignersMap;// = new HashMap<String, HardwareDesigner>();
            // http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_63:_20090824:_Add_Map_support_for_.40MapKey_to_MapAttribute
            // Key is the primary key (PK) of the target entity - in this case HardwareDesigner which inherits its @Id from the Person @MappedSuperclass as '''Integer'''.
            Type keyType = anAttribute.getKeyType(); 
            assertEquals(String.class, keyJavaType); // When @MapKey(name="name") is present - or generics are set
            assertNotNull(keyType);
            assertTrue(keyType instanceof Type);
            assertEquals(Type.PersistenceType.BASIC, keyType.getPersistenceType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    public void testMapAttribute_getKeyType_UC1a_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);

            // Actual Test Case
            /**
             * Return the type representing the key type of the map.
             * @return type representing key type
             */
            //Type<K> getKeyType();
            MapAttribute<? super Manufacturer, ?, ?> anAttribute = 
            entityManufacturer_.getMap("hardwareDesignersMapUC1a");
            // verify the key type is the Map key - not the managedType PK
            Class keyJavaType = anAttribute.getKeyJavaType();
            // UC 1a: Generics KV set, no @MapKey present, PK is singular field
            //@OneToMany(cascade=ALL, mappedBy="mappedEmployerUC1a")
            //private Map<String, HardwareDesigner> hardwareDesignersMapUC1a;
            Type keyType = anAttribute.getKeyType(); 
            assertEquals(String.class, keyJavaType); // When @MapKey(name="name") is present - or generics are set
            assertNotNull(keyType);
            assertTrue(keyType instanceof Type);
            assertEquals(Type.PersistenceType.BASIC, keyType.getPersistenceType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }
    
    public void testMapAttribute_getKeyType_UC2_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);

            // Actual Test Case
            /**
             * Return the type representing the key type of the map.
             * @return type representing key type
             */
            //Type<K> getKeyType();
            MapAttribute<? super Manufacturer, ?, ?> anAttribute = 
            entityManufacturer_.getMap("hardwareDesignersMapUC2");
            // verify the key type is the Map key - not the managedType PK
            Class keyJavaType = anAttribute.getKeyJavaType();
            // UC 2: Generics KV set, @MapKey is present
            //@OneToMany(cascade=ALL, mappedBy="mappedEmployerUC2")
            //@MapKey(name="name")
            //private Map<String, HardwareDesigner> hardwareDesignersMapUC2;
            Type keyType = anAttribute.getKeyType(); 
            assertEquals(String.class, keyJavaType); // When @MapKey(name="name") is present
            assertNotNull(keyType);
            assertTrue(keyType instanceof Type);
            assertEquals(Type.PersistenceType.BASIC, keyType.getPersistenceType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }
    
    public void testMapAttribute_getKeyType_UC4_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);

            // Actual Test Case
            /**
             * Return the type representing the key type of the map.
             * @return type representing key type
             */
            //Type<K> getKeyType();
            MapAttribute<? super Manufacturer, ?, ?> anAttribute = 
            entityManufacturer_.getMap("hardwareDesignersMapUC4");
            // verify the key type is the Map key - not the managedType PK
            Class keyJavaType = anAttribute.getKeyJavaType();
            // UC 4: No Generics KV set, @MapKey is present
            //@OneToMany(targetEntity=HardwareDesigner.class, cascade=ALL, mappedBy="mappedEmployerUC4")
            //@MapKey(name="name")
            //private Map hardwareDesignersMapUC4;
            Type keyType = anAttribute.getKeyType(); 
            assertEquals(String.class, keyJavaType); // When @MapKey(name="name") is present
            assertNotNull(keyType);
            assertTrue(keyType instanceof Type);
            assertEquals(Type.PersistenceType.BASIC, keyType.getPersistenceType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }
    
    public void testMapAttribute_getKeyType_UC7_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);

            // Actual Test Case
            /**
             * Return the type representing the key type of the map.
             * @return type representing key type
             */
            //Type<K> getKeyType();
            MapAttribute<? super Manufacturer, ?, ?> anAttribute = 
            entityManufacturer_.getMap("hardwareDesignersMapUC7");
            // verify the key type is the Map key - not the managedType PK
            Class keyJavaType = anAttribute.getKeyJavaType();
            // UC 7: Generics KV set, targetEntity is also set, @MapKey is *(set/unset)
            //@OneToMany(targetEntity=HardwareDesigner.class, cascade=ALL, mappedBy="mappedEmployerUC7")
            // Same as UC1a - that is missing the @MapKey
            //private Map<String, HardwareDesigner> hardwareDesignersMapUC7;
            Type keyType = anAttribute.getKeyType(); 
            assertEquals(String.class, keyJavaType); // When @MapKey(name="name") is present - or generics are set
            assertNotNull(keyType);
            assertTrue(keyType instanceof Type);
            assertEquals(Type.PersistenceType.BASIC, keyType.getPersistenceType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }
    
    public void testMapAttribute_getKeyType_UC8_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);

            // Actual Test Case
            /**
             * Return the type representing the key type of the map.
             * @return type representing key type
             */
            //Type<K> getKeyType();
            MapAttribute<? super Manufacturer, ?, ?> anAttribute = 
            entityManufacturer_.getMap("hardwareDesignersMapUC8");
            // verify the key type is the Map key - not the managedType PK
            Class keyJavaType = anAttribute.getKeyJavaType();
            // UC 8: Generics KV set, targetEntity not set, @MapKey is set but name attribute is defaulted
            //@OneToMany(targetEntity=HardwareDesigner.class, cascade=ALL, mappedBy="mappedEmployerUC8")
            // Same as UC1a - that is missing the @MapKey name attribute
            //private Map<String, HardwareDesigner> hardwareDesignersMapUC8;
            Type keyType = anAttribute.getKeyType(); 
            assertEquals(Integer.class, keyJavaType); // When @MapKey is not present or missing name attribute - we default to the PK
            assertNotNull(keyType);
            assertTrue(keyType instanceof Type);
            assertEquals(Type.PersistenceType.BASIC, keyType.getPersistenceType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    // see
    // http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_86:_20090921:_Handle_Embeddable_Type_keyType_in_MapAttributeImpl_constructor
    public void testMapAttribute_getKeyType_UC9_DI86_Embeddable_IdClass_keyType_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);

            // Actual Test Case
            /**
             * Return the type representing the key type of the map.
             * @return type representing key type
             */
            //Type<K> getKeyType();
            MapAttribute<? super Manufacturer, ?, ?> anAttribute = 
            entityManufacturer_.getMap("enclosureByBoardMapUC9");
            // verify the key type is the Map key - not the managedType PK
            Class keyJavaType = anAttribute.getKeyJavaType();
            // UC9: no targetEntity, no MapKey, but generics are set (MapKey has an IdClass with an Embeddable)
            //@OneToMany(cascade=CascadeType.ALL, mappedBy="mappedManufacturerUC9")
            //private Map<Board, Enclosure> enclosureByBoardMapUC9;
            Type keyType = anAttribute.getKeyType(); 
            assertEquals(Board.class, keyJavaType); // When @MapKey(name="name") is present or we use generics
            assertNotNull(keyType);
            assertTrue(keyType instanceof Type);
            assertEquals(Type.PersistenceType.ENTITY, keyType.getPersistenceType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    // This test verifies the workaround for 294811
    public void testMapAttribute_getKeyType_294811_UC10_DI86_Embeddable_IdClass_keyType_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);

            // Actual Test Case
            /**
             * Return the type representing the key type of the map.
             * @return type representing key type
             */
            //Type<K> getKeyType();
            MapAttribute<? super Computer, ?, ?> anAttribute = 
            entityComputer_.getMap("enclosuresUC10");
            // verify the key type is the Map key - not the managedType PK
            Class keyJavaType = anAttribute.getKeyJavaType();
            // UC10: no targetEntity, no MapKey attribute, but generics are set (MapKey has an IdClass with an Embeddable)
            //@OneToMany(mappedBy="computerUC10", cascade=ALL, fetch=EAGER)
            //@MapKey // key defaults to an instance of the composite pk class
            //private Map<EnclosureIdClassPK, Enclosure> enclosuresUC10;
            Type keyType = anAttribute.getKeyType(); 
            assertEquals(EnclosureIdClassPK.class, keyJavaType); // When @MapKey(name="name") is present or we use generics
            assertNotNull(keyType);
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    // This test verifies the workaround for 294811
    public void testMapAttribute_getKeyType_294811_UC13_DI86_Embedded_keyType_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);

            // Actual Test Case
            /**
             * Return the type representing the key type of the map.
             * @return type representing key type
             */
            //Type<K> getKeyType();
            MapAttribute<? super Computer, ?, ?> anAttribute = 
            entityComputer_.getMap("positionUniUC13");
            // verify the key type is the Map key - not the managedType PK
            Class keyJavaType = anAttribute.getKeyJavaType();
            // UC13:  mapKey defined via generics and is an Embeddable (EmbeddedId) java class defined as an IdClass on the element(value) class
            // However, here we make the owning OneToMany - unidirectional and an effective ManyToMany
            //@MapKey // key defaults to an instance of the composite pk class
            //private Map<EmbeddedPK, GalacticPosition> positionUniUC13;
            Type keyType = anAttribute.getKeyType(); 
            assertEquals(EmbeddedPK.class, keyJavaType); // When @MapKey(name="name") is present or we use generics
            assertNotNull(keyType);
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    // This test verifies the workaround for 294811
    public void testMapAttribute_getKeyType_294811_UC12_DI86_Embedded_keyType_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);

            // Actual Test Case
            /**
             * Return the type representing the key type of the map.
             * @return type representing key type
             */
            //Type<K> getKeyType();
            MapAttribute<? super Computer, ?, ?> anAttribute = 
            entityComputer_.getMap("positionUC12");
            // verify the key type is the Map key - not the managedType PK
            Class keyJavaType = anAttribute.getKeyJavaType();
            // UC12:  mapKey defined via generics and is an Embeddable (EmbeddedId) java class defined as an IdClass on the element(value) class
            //@OneToMany(mappedBy="computerUC12", cascade=ALL, fetch=EAGER)
            //@MapKey // key defaults to an instance of the composite pk class
            //private Map<EmbeddedPK, GalacticPosition> positionUC12;
            Type keyType = anAttribute.getKeyType(); 
            assertEquals(EmbeddedPK.class, keyJavaType); // When @MapKey(name="name") is present or we use generics
            assertNotNull(keyType);
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    // TODO: This is not testing anything?
    public void testMapAttribute_getKeyType_UC9_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);

            // Actual Test Case
            /**
             * Return the type representing the key type of the map.
             * @return type representing key type
             */
            //Type<K> getKeyType();
            
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    // TODO: This is not testing anything?
    public void testMappedSuperclassType() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);

            // Actual Test Case
            // Interface is empty - however we will test native functionality
            
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    public void testMetamodel_managedType_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);

            // Actual Test Case
            /**
             *  Return the metamodel managed type representing the 
             *  entity, mapped superclass, or embeddable class.
             *  @param cls  the type of the represented managed class
             *  @return the metamodel managed type
             *  @throws IllegalArgumentException if not a managed class
             */
            //<X> ManagedType<X> managedType(Class<X> cls);
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
            Type<EmbeddedPK> aType = metamodel.managedType(EmbeddedPK.class);
            assertFalse(((TypeImpl)aType).isEntity());
            assertFalse(((TypeImpl)aType).isMappedSuperclass());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);            
            
            // test normal path: (subtype = Entity)
            expectedIAExceptionThrown = false;            
        try {
            Type<Manufacturer> aType = metamodel.managedType(Manufacturer.class);
            assertTrue(((TypeImpl)aType).isEntity());
            assertFalse(((TypeImpl)aType).isMappedSuperclass());            
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);            

            // test normal path: (subtype = MappedSuperclass)
            expectedIAExceptionThrown = false;            
        try {
            Type<Person> aType = metamodel.managedType(Person.class);
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);

            // test variant path: null does not cause an IAE in this case because its return type cannot be checked for isManagedType
            expectedIAExceptionThrown = false;
            // Set type to a temporary type - to verify that we get null and not confuse a return of null with an "unset" null.
            Type<?> aTypeFromNullClass = metamodel.managedType(Manufacturer.class);
        try {
            aTypeFromNullClass = metamodel.managedType(null);
        } catch (IllegalArgumentException iae) {
            //iae.printStackTrace();
            expectedIAExceptionThrown = true;            
            }
            //assertNull(aTypeFromNullClass);
            assertTrue("IllegalArgumentException expected on Metamodel.managedType(null)",expectedIAExceptionThrown);            

            // Type is basic - throw IAE
            expectedIAExceptionThrown = false;            
        try {
            Type<Integer> aType = metamodel.managedType(Integer.class);            
        } catch (IllegalArgumentException iae) {
            //iae.printStackTrace();
            expectedIAExceptionThrown = true;            
            }
            assertTrue("IllegalArgumentException expected on Metamodel.managedType(Integer.class)",expectedIAExceptionThrown);
            
            // test variant path: wrong type (java simple type)
            expectedIAExceptionThrown = false;            
        try {
            Type<?> aType = metamodel.embeddable(Integer.class);
        } catch (IllegalArgumentException iae) {
            //iae.printStackTrace();
            expectedIAExceptionThrown = true;            
            }
            assertTrue(expectedIAExceptionThrown);
            // reset exception flag
            expectedIAExceptionThrown = false;

            // test variant path: wrong type (BasicType)

            
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
//            entityManufacturer.getCollection("computers", Computer.class);
//            javax.persistence.metamodel.CollectionAttribute<? super Manufacturer, Computer> computersAttribute2 = 
//            entityManufacturer.getCollection("computers", Computer.class);
            javax.persistence.metamodel.SetAttribute<? super Manufacturer, Computer> computersAttribute = 
            entityManufacturer_.getSet("computers", Computer.class);
            
            //version=Attribute[org.eclipse.persistence.mappings.DirectToFieldMapping[version-->CMP3_MM_MANUF.MANUF_VERSION]], 
            //name=Attribute[org.eclipse.persistence.mappings.DirectToFieldMapping[name-->CMP3_MM_MANUF.NAME]], 
            //id=Attribute[org.eclipse.persistence.mappings.DirectToFieldMapping[id-->CMP3_MM_MANUF.PERSON_ID]]
            
        } catch (IllegalArgumentException iae) {
            //iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything?
    public void testMetamodel_getEmbeddables_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);

            // Actual Test Case
            /**
             * Return the metamodel embeddable types.
             * @return the metamodel embeddable types
             */
            //java.util.Set<EmbeddableType<?>> getEmbeddables();            
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything?
    public void testMetamodel_getEntities_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);

            // Actual Test Case
            /**
             * Return the metamodel entity types.
             * @return the metamodel entity types
             */
            //java.util.Set<EntityType<?>> getEntities();
            
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything?
    public void testMetamodel_getManagedTypes_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);

            // Actual Test Case

            /**
             *  Return the metamodel managed types.
             *  @return the metamodel managed types
             */
            //java.util.Set<ManagedType<?>> getManagedTypes();

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    public void testMetamodel_embeddable_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);

            // Actual Test Case
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
            expectedIAExceptionThrown = true;            
            }
            assertTrue("Expected thrown IllegalArgumentException", expectedIAExceptionThrown);            

            // test variant path: wrong type (subtype = Entity)
            expectedIAExceptionThrown = false;            
        try {
            EmbeddableType<Manufacturer> aType = metamodel.embeddable(Manufacturer.class);
        } catch (IllegalArgumentException iae) {
            expectedIAExceptionThrown = true;            
            }
            assertTrue("Expected thrown IllegalArgumentException", expectedIAExceptionThrown);            

            // test variant path: wrong type (java simple type)
            expectedIAExceptionThrown = false;            
        try {
            EmbeddableType<?> aType = metamodel.embeddable(Integer.class);
        } catch (IllegalArgumentException iae) {
            expectedIAExceptionThrown = true;            
            }
            assertTrue("Expected thrown IllegalArgumentException", expectedIAExceptionThrown);
            // reset state
            expectedIAExceptionThrown = false;
        } catch (IllegalArgumentException iae) {
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }
    
    
    public void testMetamodel_entity_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);

            // Actual Test Case
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
            iae.printStackTrace();
            expectedIAExceptionThrown = true;            
            }
            assertFalse(expectedIAExceptionThrown);            
            
            // test variant path: null causes IAE
            expectedIAExceptionThrown = false;            
        try {
            EntityType<Manufacturer> aType = metamodel.entity(null);
        } catch (IllegalArgumentException iae) {
            expectedIAExceptionThrown = true;            
            }
            assertTrue("Expected thrown IllegalArgumentException", expectedIAExceptionThrown);            

            // test variant path: wrong type (java simple type)
            expectedIAExceptionThrown = false;            
        try {
            EntityType<Integer> aType = metamodel.entity(Integer.class);
        } catch (IllegalArgumentException iae) {
            expectedIAExceptionThrown = true;            
            }
            assertTrue("Expected thrown IllegalArgumentException", expectedIAExceptionThrown);
            // reset exception flag
            expectedIAExceptionThrown = false;
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything?
    public void testPluralAttribute_CollectionType_enum() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);

            // Actual Test Case
            //public static enum CollectionType {COLLECTION, SET, LIST, MAP}
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    // TODO: This is not testing anything?
    public void testPluralAttribute_getCollectionType_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);

            // Actual Test Case
            //public static enum CollectionType {COLLECTION, SET, LIST, MAP}
            
            /**
             * Return the collection type.
             * @return collection type
             */
            //CollectionType getCollectionType();
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    // TODO: This is not testing anything?
    public void testPluralAttribute_getElementType_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);

            // Actual Test Case
            /**
             * Return the type representing the element type of the 
             * collection.
             * @return element type
             */
            //Type<E> getElementType();
            
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    // TODO: This is not testing anything?
    public void testSetAttribute() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);

            // Actual Test Case
            // The interface is empty - we will test native functionality
            
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    public void testSingularAttribute_isOptional_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);
            // Composite table FK's that include a MappedSuperclass
            // get an Attribute<Container, Type==String>
            Attribute nameAttribute = entityManufacturer_.getAttribute("name");
            assertTrue(null != nameAttribute);

            /** 
             *  Can the attribute be null.
             *  @return boolean indicating whether the attribute can
             *          be null
             */
            //boolean isOptional();            
            assertFalse(((AttributeImpl)nameAttribute).isPlural());
            assertFalse(((SingularAttributeImpl)nameAttribute).isVersion());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }
    
    public void testSingularAttribute_isId_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);
            // Composite table FK's that include a MappedSuperclass
            // get an Attribute<Container, Type==String>
            Attribute nameAttribute = entityManufacturer_.getAttribute("name");
            assertTrue(null != nameAttribute);

            /**
             *  Is the attribute an id attribute.  This method will return
             *  true if the attribute is an attribute that corresponds to
             *  a simple id, an embedded id, or an attribute of an id class.
             *  @return boolean indicating whether the attribute is an id
             */
            //boolean isId();
            assertFalse(((AttributeImpl)nameAttribute).isPlural());
            assertFalse(((SingularAttributeImpl)nameAttribute).isVersion());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }
    
    public void testSingularAttribute_isVersion_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            EntityTypeImpl<HardwareDesigner> entityHardwareDesigner_ = (EntityTypeImpl)metamodel.entity(HardwareDesigner.class);
            assertNotNull(entityHardwareDesigner_);
            // Composite table FK's that include a MappedSuperclass
            // get an Attribute<Container, Type==String>
            Attribute nameAttribute = entityManufacturer_.getAttribute("name");
            assertTrue(null != nameAttribute);

            /**
             *  Is the attribute a version attribute.
             *  @return boolean indicating whether or not attribute is 
             *          a version attribute
             */
            //public boolean isVersion() {
            assertFalse(((AttributeImpl)nameAttribute).isPlural());
            assertFalse(((SingularAttributeImpl)nameAttribute).isVersion());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything?
    public void testSingularAttribute_getBindableType_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            //public static enum BindableType {SINGULAR_ATTRIBUTE, PLURAL_ATTRIBUTE, ENTITY_TYPE}

            /**
             *  Return the bindable type of the represented object.
             *  @return bindable type
             */ 
            //BindableType getBindableType();
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }
    
    // Same as Entity unless used on Basic
    public void testSingularAttribute_getBindableJavaType_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<GalacticPosition> entityLocation_ = (EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            
            /**
             * Return the Java type of the represented object.
             * If the bindable type of the object is <code>PLURAL_ATTRIBUTE</code>,
             * the Java element type is returned. If the bindable type is
             * <code>SINGULAR_ATTRIBUTE</code> or <code>ENTITY_TYPE</code>, 
             * the Java type of the
             * represented entity or attribute is returned.
             * @return Java type
             */
            //Class<T> getBindableJavaType();
            Attribute anAttribute = entityComputer_.getSingularAttribute("location");
            assertNotNull(anAttribute);
            assertEquals(PersistentAttributeType.ONE_TO_ONE, anAttribute.getPersistentAttributeType());
            assertFalse(((AttributeImpl)anAttribute).isPlural());
            assertTrue(anAttribute instanceof SingularAttribute);
            assertEquals(Type.PersistenceType.ENTITY, entityComputer_.getPersistenceType());
            Bindable aSingularAttribute = entityComputer_.getSingularAttribute("location");
            assertEquals(GalacticPosition.class, aSingularAttribute.getBindableJavaType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // same as Singular unless used on Basic
    public void testEntityAttribute_getBindableJavaType_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            EntityTypeImpl<GalacticPosition> entityLocation_ = (EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            EntityTypeImpl<Computer> entityComputer_ = (EntityTypeImpl)metamodel.entity(Computer.class);
            assertNotNull(entityComputer_);
            
            /**
             * Return the Java type of the represented object.
             * If the bindable type of the object is <code>PLURAL_ATTRIBUTE</code>,
             * the Java element type is returned. If the bindable type is
             * <code>SINGULAR_ATTRIBUTE</code> or <code>ENTITY_TYPE</code>, 
             * the Java type of the
             * represented entity or attribute is returned.
             * @return Java type
             */
            //Class<T> getBindableJavaType();
            Attribute anAttribute = entityComputer_.getSingularAttribute("location");
            assertNotNull(anAttribute);
            assertEquals(PersistentAttributeType.ONE_TO_ONE, anAttribute.getPersistentAttributeType());
            assertFalse(((AttributeImpl)anAttribute).isPlural());
            assertTrue(anAttribute instanceof SingularAttribute);
            assertEquals(Type.PersistenceType.ENTITY, entityComputer_.getPersistenceType());
            Bindable aSingularAttribute = entityComputer_.getSingularAttribute("location");
            assertEquals(GalacticPosition.class, aSingularAttribute.getBindableJavaType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }
    
    public void testPluralAttribute_getBindableJavaType_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            /**
             * Return the Java type of the represented object.
             * If the bindable type of the object is <code>PLURAL_ATTRIBUTE</code>,
             * the Java element type is returned. If the bindable type is
             * <code>SINGULAR_ATTRIBUTE</code> or <code>ENTITY_TYPE</code>, 
             * the Java type of the
             * represented entity or attribute is returned.
             * @return Java type
             */
            //Class<T> getBindableJavaType();
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            assertEquals(Type.PersistenceType.ENTITY, entityManufacturer_.getPersistenceType());
            Attribute anAttribute = entityManufacturer_.getAttribute("computers");
            assertEquals(PersistentAttributeType.ONE_TO_MANY, anAttribute.getPersistentAttributeType());
            assertNotNull(anAttribute);
            assertTrue(((AttributeImpl)anAttribute).isPlural());
            assertTrue(anAttribute instanceof PluralAttribute);
            Bindable aPluralAttribute = entityManufacturer_.getSet("computers");
            assertEquals(Computer.class, aPluralAttribute.getBindableJavaType());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything?
    public void testSingularAttribute_getJavaType_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            /**
             *  Return the Java type of the represented attribute.
             *  @return Java type
             */
            //public Class<T> getJavaType() {
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything?
    public void testSingularAttribute_getType_Method() {
        EntityManager em = null;
        boolean expectedIAExceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);

            /**
             * Return the type that represents the type of the attribute.
             * @return type of attribute
             */
             //public Type<T> getType() {
            
            /**
             * Return the String representation of the receiver.
             */
            //public String toString() {
            
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            expectedIAExceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", expectedIAExceptionThrown);
        }
    }

    // TODO: This is not testing anything?
    public void testType_PersistenceType_enum() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);

            // Actual Test Case
            //public static enum PersistenceType { ENTITY,  EMBEDDABLE, MAPPED_SUPERCLASS, BASIC }

             /**
              *  Return the persistence type.
              *  @return persistence type
              */ 
             //PersistenceType getPersistenceType();

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    // TODO: This is not testing anything?
    public void testType_getPersistenceType_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);

            // Actual Test Case
            //public static enum PersistenceType { ENTITY,  EMBEDDABLE, MAPPED_SUPERCLASS, BASIC }

             /**
              *  Return the persistence type.
              *  @return persistence type
              */ 
             //PersistenceType getPersistenceType();

             /**
              *  Return the represented Java type.
              *  @return Java type
              */
             //Class<X> getJavaType();
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    // TODO: This is not testing anything?
    public void testType_getJavaType_Method() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);

            // Actual Test Case
            //public static enum PersistenceType { ENTITY,  EMBEDDABLE, MAPPED_SUPERCLASS, BASIC }
             /**
              *  Return the represented Java type.
              *  @return Java type
              */
             //Class<X> getJavaType();
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    public void testOutOfSpecificationInternalAPI() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull(metamodel);

            // Actual Test Case
            EntityTypeImpl<ArrayProcessor> entityArrayProcessor_ =(EntityTypeImpl) metamodel.entity(ArrayProcessor.class);
            assertNotNull(entityArrayProcessor_);
            EntityTypeImpl<Processor> entityProcessor_ = (EntityTypeImpl) metamodel.entity(Processor.class);
            assertNotNull(entityProcessor_);
            
            // verify all Types have their javaClass set
            Collection<TypeImpl<?>> types = ((MetamodelImpl)metamodel).getTypes().values();
            assertNotNull(types);
            for(TypeImpl type : types) {
            assertNotNull(type);
            assertNotNull(type.getJavaType());
            }            
            
            // verify all embeddables are only embeddables
            Set<EmbeddableType<?>> embeddables = metamodel.getEmbeddables();
            assertNotNull(embeddables);
            for(EmbeddableType embeddable : embeddables) {
            // This method works only on EntityType
            assertNotNull(embeddable);
            assertTrue(embeddable instanceof EmbeddableTypeImpl);
            }
            
            // verify all entities are only entities
            Set<EntityType<?>> entities = metamodel.getEntities();
            assertNotNull(entities);
            for(EntityType entity : entities) {
            // This method works only on EntityType
            assertNotNull(entity.getName());
            assertTrue(entity instanceof EntityTypeImpl);
            }
            
            // Verify all Attributes and their element and declaring/managed types
            List<Attribute> allAttributes = ((MetamodelImpl)metamodel).getAllManagedTypeAttributes();
            assertNotNull(allAttributes);
            assertEquals(METAMODEL_ALL_ATTRIBUTES_SIZE, allAttributes.size());
            // Why do we have this function? So we can verify attribute integrity
            for(Attribute anAttribute : allAttributes) {
            ManagedType declaringType = anAttribute.getDeclaringType();
            assertNotNull(declaringType);
            Type elementType = null;
            if(((AttributeImpl)anAttribute).isPlural()) {
                elementType = ((PluralAttributeImpl)anAttribute).getElementType();
            } else {
                elementType = ((SingularAttributeImpl)anAttribute).getType();
            }
            assertNotNull("elementType should not be null", elementType);
            // Since the javaType may be computed off the elementType - it must not be null or we will get a NPE below
            Class javaType = anAttribute.getJavaType();
            }
            
        boolean expectedIAExceptionThrown = false;
            // Check entity-->entity hierarchy
            // Processor:entity (Board boards)
            //  +--VectorProcessor
            
            Set<Attribute<ArrayProcessor, ?>> entityArrayProcessorDeclaredAttributes = entityArrayProcessor_.getDeclaredAttributes();
            assertEquals(1, entityArrayProcessorDeclaredAttributes.size());
            // verify getting the attribute directly
            Attribute<ArrayProcessor, ?> entityArrayProcessorDeclaredAttribute = entityArrayProcessor_.getDeclaredAttribute("speed");
            // verify we do get an IAE on declared type above
        try {
            Attribute<ArrayProcessor, ?> entityArrayProcessorDeclaredAttributeThatIsNonExistent = entityArrayProcessor_.getDeclaredAttribute("non-existent");
        } catch (IllegalArgumentException iae) {
            // expecting no exception
            expectedIAExceptionThrown = true;            
            }
            assertTrue("Expected thrown IllegalArgumentException", expectedIAExceptionThrown);

            // Verify we get an IAE on a type declared above
        try {
            Attribute<ArrayProcessor, ?> entityArrayProcessorDeclaredAttributeThatIsDeclaredAbove = entityArrayProcessor_.getDeclaredAttribute("id");
        } catch (IllegalArgumentException iae) {
            // expecting no exception
            expectedIAExceptionThrown = true;            
            }
            assertTrue("Expected thrown IllegalArgumentException", expectedIAExceptionThrown);

            Set<Attribute<Processor, ?>> entityProcessorDeclaredAttributes = entityProcessor_.getDeclaredAttributes();
            assertEquals(3, entityProcessorDeclaredAttributes.size());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }
    
    public void testToStringOverrides() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull(metamodel);

            // Actual Test Case
            // Test toString() overrides
            EntityTypeImpl<Manufacturer> entityManufacturer_ = (EntityTypeImpl)metamodel.entity(Manufacturer.class);
            assertNotNull(entityManufacturer_);
            assertNotNull(metamodel.toString());
            assertNotNull(entityManufacturer_.getSingularAttribute("aBooleanObject").toString());
            // In the absence of a getPluralAttribute()
            assertNotNull(((PluralAttribute)entityManufacturer_.getAttribute("computers")).toString());
            assertNotNull(entityManufacturer_.getList("hardwareDesigners").toString());
            assertNotNull(entityManufacturer_.getMap("hardwareDesignersMap").toString());
            assertNotNull(entityManufacturer_.getSet("computers").toString());
        } catch (IllegalArgumentException iae) {
            //iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    // 316991: regression test getJavaMember() on field level access Entity attribute @OneToOne on an Entity
    /**
     * This test will verify that a transient superclass (non-entity, non-mappedSuperclass)
     * exists as a BasicType (it has no attributes), and that any inheriting Entity either
     * directly subclassing or indirectly subclassing via a MappedSuperclass inheritance chain
     * - does not pick up non-persistence fields that normally would be inherited.
     * (The fields exist in Java but not in ORM:Metamodel)
     * The transient class must have no JPA annotations.
     */
    public void testTransientNonEntityNonMappedSuperclass_SuperclassOfEntity_Exists_as_BasicType() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            // Verify that the non-Entity/non-MappedSuperclass is a BasicType
            Type positionNonEntity = ((MetamodelImpl)metamodel).getType(Position.class);
            assertNotNull(positionNonEntity);
            assertEquals(Type.PersistenceType.BASIC, positionNonEntity.getPersistenceType());
            // Get direct inheriting subclass
            EntityTypeImpl<GalacticPosition> entityLocation_ = (EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);
            
            // Verify it is a BasicType in other INTERNAL API ways
            assertFalse(((TypeImpl)positionNonEntity).isMappedSuperclass());
            assertFalse(((TypeImpl)positionNonEntity).isEntity());
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            exceptionThrown = true;
        } finally {
            cleanup(em);
            assertFalse("An IAE exception should not occur here.", exceptionThrown);
        }
    }

    // Test that we are getting an Illegal argument exception when trying to access non-persistent fields from transient classes
    public void testAttribute_getAttribute_of_TransientNonEntityNonMappedSuperclass_SuperclassOfEntity_throws_IAE() {
        EntityManager em = null;
        boolean exceptionThrown = false;
        try {
            em = privateTestSetup();
            assertNotNull(em);
            Metamodel metamodel = em.getMetamodel();
            assertNotNull("The metamodel should never be null after an em.getMetamodel() call here.", metamodel);
            // Verify that the non-Entity/non-MappedSuperclass is a BasicType
            Type positionNonEntity = ((MetamodelImpl)metamodel).getType(Position.class);
            assertNotNull(positionNonEntity);
            assertEquals(Type.PersistenceType.BASIC, positionNonEntity.getPersistenceType());
            // Get direct inheriting subclass
            EntityTypeImpl<GalacticPosition> entityLocation_ = (EntityTypeImpl) metamodel.entity(GalacticPosition.class);
            assertNotNull(entityLocation_);

            // We will be testing that the non-persistent fields 
            Attribute anAttributeThatShouldNotHaveBeenInherited = entityLocation_.getAttribute("nonPersistentObject");
            // we should never get to the following line - go directly to catch block
            assertTrue("IllegalArgumentException expected on transient type attribute should not be in subclass for managedType.getAttribute()",exceptionThrown);
        } catch (IllegalArgumentException iae) {
            //iae.printStackTrace();
            exceptionThrown = true;
            assertTrue("IllegalArgumentException expected on transient type attribute should not be in subclass for managedType.getAttribute()",exceptionThrown);            
        } finally {
            cleanup(em);
        }
    }
    
    /**
    * Disclaimer:
        *    The following work may still need to be fully implemented - subject to available time.
        *    - proper and fully optimized test cases
        *    - full exception handling
        *    - full rollback handling
        *    - better documented assertion failures
        *    - fully described test model with links to design document
        *    - traceability back to use cases
    */
}
