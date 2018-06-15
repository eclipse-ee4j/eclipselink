/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Guy Pelletier, dclarke - initial API and implementation
package org.eclipse.persistence.testing.tests.jpa.config;

import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_DATABASE_GENERATION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_GENERATION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_GENERATION_MODE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DROP_AND_CREATE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_DRIVER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_PASSWORD;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_URL;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_USER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_LEVEL;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.internal.jpa.config.persistenceunit.PersistenceUnitImpl;
import org.eclipse.persistence.jpa.config.Basic;
import org.eclipse.persistence.jpa.config.ElementCollection;
import org.eclipse.persistence.jpa.config.Embeddable;
import org.eclipse.persistence.jpa.config.Embedded;
import org.eclipse.persistence.jpa.config.Entity;
import org.eclipse.persistence.jpa.config.Id;
import org.eclipse.persistence.jpa.config.JoinTable;
import org.eclipse.persistence.jpa.config.ManyToMany;
import org.eclipse.persistence.jpa.config.ManyToOne;
import org.eclipse.persistence.jpa.config.Mappings;
import org.eclipse.persistence.jpa.config.NamedStoredProcedureQuery;
import org.eclipse.persistence.jpa.config.ObjectTypeConverter;
import org.eclipse.persistence.jpa.config.PersistenceUnit;
import org.eclipse.persistence.jpa.config.RuntimeFactory;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;
import org.eclipse.persistence.testing.tests.jpa.advanced.AdvancedJPAJunitTest;

/**
 * JPA scripting API implementation helper class.
 *
 * @author Guy Pelletier, Doug Clarke
 * @since EclipseLink 2.5.1
 */
public class ConfigPUTestSuite extends JUnitTestCase {
    private static EntityManagerFactory emf;

    public ConfigPUTestSuite() {
        super();
    }

    public ConfigPUTestSuite(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("ConfigPUTestSuite");

        suite.addTest(new ConfigPUTestSuite("testCreateConfigPU"));
        suite.addTest(new ConfigPUTestSuite("testVerifyConfigPU"));
        suite.addTest(new ConfigPUTestSuite("testCRUDConfigPU"));

        return suite;
    }

    public void testCreateConfigPU() {

        PersistenceUnit pu = new PersistenceUnitImpl("ConfigPUTestSuite", Thread.currentThread().getContextClassLoader());

        // Need to get the user specified database properties.
        Map properties = JUnitTestCaseHelper.getDatabaseProperties();
        pu.setProperty(JDBC_DRIVER, properties.get(JDBC_DRIVER));
        pu.setProperty(JDBC_URL, properties.get(JDBC_URL));
        pu.setProperty(JDBC_USER, properties.get(JDBC_USER));
        pu.setProperty(JDBC_PASSWORD, properties.get(JDBC_PASSWORD));

        pu.setProperty(DDL_GENERATION_MODE, DDL_DATABASE_GENERATION);
        pu.setProperty(DDL_GENERATION, DROP_AND_CREATE);

        pu.setProperty(LOGGING_LEVEL, "FINE");

        Mappings mappings = pu.addMappings();
        mappings.setPersistenceUnitMetadata().setXmlMappingMetadataComplete(true).setPersitenceUnitDefault().setAccess("VIRTUAL");
        mappings.setPackage("org.eclipse.persistence.testing.tests.jpa.config");
        mappings.addTypeConverter().setName("String2String").setDataType("String").setObjectType("String");

        Entity person = mappings.addEntity().setClass("Person");
        person.setTable().setName("CFGPU_PERSON");
        Id personId = person.addId();
        personId.setName("id").setAttributeType("Integer").setColumn().setName("P_ID");
        personId.setGeneratedValue().setStrategy("AUTO");
        person.addBasic().setName("name").setAttributeType("String");

        /********************************************************************/
        /************************** EMPLOYEE ENTITY *************************/
        /********************************************************************/
        Entity emp = mappings.addEntity().setClass("Employee").setExistenceChecking("CHECK_DATABASE");
        emp.setTable().setName("CFGPU_EMPLOYEE");
        emp.addSecondaryTable().setName("CFGPU_SALARY").addPrimaryKeyJoinColumn().setName("E_ID").setReferencedColumnName("EMP_ID");
        emp.setTableGenerator().setName("CFGPU_EMPLOYEE_TABLE_GENERATOR").setTable("CFGPU_EMPLOYEE_SEQ").setPKColumnName("SEQ_NAME").setValueColumnName("SEQ_COUNT").setPKColumnValue("EMP_SEQ");
        emp.addNamedQuery().setName("findAllEmployeesByFirstName").setQuery("SELECT OBJECT(employee) FROM Employee employee WHERE employee.firstName = :firstname");
        emp.setChangeTracking().setType("AUTO");
        emp.setExcludeDefaultListeners(true);
        emp.setExcludeSuperclassListeners(true);
        emp.setOptimisticLocking().setType("VERSION_COLUMN").setCascade(true);
        emp.setCache().setType("SOFT_WEAK").setSize(730).setShared(true).setDisableHits(true).setAlwaysRefresh(false).setCoordinationType("INVALIDATE_CHANGED_OBJECTS").setExpiry(1000);
        emp.addEntityListener().setClass("EmployeeListener").setPrePersist("prePersist").setPostPersist("postPersist").setPreRemove("preRemove").setPostRemove("postRemove").setPreUpdate("preUpdate").setPostUpdate("postUpdate").setPostLoad("postLoad");
        ObjectTypeConverter sexConverter = emp.addObjectTypeConverter().setName("sex").setDataType("String").setObjectType("Gender");
        sexConverter.addConversionValue().setDataValue("F").setObjectValue("Female");
        sexConverter.addConversionValue().setDataValue("M").setObjectValue("Male");
        emp.addProperty().setName("entityName").setValue("Employee");
        emp.addProperty().setName("entityIntegerProperty").setValue("1").setValueType("Integer");

        Id employeeId = emp.addId();
        employeeId.setName("id").setGeneratedValue().setGenerator("CFGPU_EMPLOYEE_TABLE_GENERATOR").setStrategy("TABLE");
        employeeId.setColumn().setName("EMP_ID");
        employeeId.setConvert("String2String");

        emp.addBasic().setName("firstName").setAttributeType(String.class.getName()).setColumn().setName("F_NAME");
        emp.addBasic().setName("lastName").setAttributeType(String.class.getName()).setColumn().setName("L_NAME");
        emp.addBasic().setName("sin").setAttributeType("String").setColumn().setName("SIN");
        // Access methods?
        emp.addBasic().setName("gender").setAttributeType("Gender").setConvert("sex").setColumn().setName("GENDER");
        emp.addBasic().setName("salary").setAttributeType(Integer.class.getName()).setColumn().setTable("CFGPU_SALARY");
        emp.addBasic().setName("payScale").setAttributeType("SalaryRate").setEnumerated().setType("STRING");

        emp.addVersion().setName("version").setAttributeType(Integer.class.getName()).setColumn().setName("VERSION");

        ManyToOne addressMapping = emp.addManyToOne().setName("address").setTargetEntity("Address").setFetch("LAZY").setJoinFetch("OUTER");
        addressMapping.setCascade().setCascadePersist();
        addressMapping.addJoinColumn().setName("ADDR_ID");

        ManyToOne managerMapping = emp.addManyToOne().setName("manager").setTargetEntity("Employee").setFetch("LAZY");
        managerMapping.setCascade().setCascadePersist();
        managerMapping.addJoinColumn().setName("MANAGER_EMP_ID").setReferencedColumnName("EMP_ID");

        emp.addOneToMany().setName("phoneNumbers").setTargetEntity("PhoneNumber").setMappedBy("owner").setAttributeType("java.util.Collection").setCascade().setCascadeAll();
        emp.addOneToMany().setName("managedEmployees").setTargetEntity("Employee").setMappedBy("manager").setAttributeType("java.util.Collection");

        ManyToMany projectsMapping = emp.addManyToMany().setName("projects").setTargetEntity("Project").setOrderBy("name").setAttributeType("java.util.Collection");
        projectsMapping.setCascade().setCascadePersist();
        JoinTable joinTable = projectsMapping.setJoinTable().setName("CFGPU_PROJ_EMP");
        joinTable.addJoinColumn().setName("EMP_ID").setReferencedColumnName("EMP_ID");
        joinTable.addInverseJoinColumn().setName("PROJ_ID").setReferencedColumnName("PROJ_ID");

        // CreditCards??
        // Responsibilities??

        ElementCollection creditLinesMapping = emp.addElementCollection().setName("creditLines").setFetch("EAGER").setMapKeyConvert("CreditLine").setConvert("Long2String").setAttributeType("java.util.Map").setTargetClass("Long").setMapKeyClass("String");
        creditLinesMapping.addProperty().setName("attributeName").setValue("creditLines");
        creditLinesMapping.setColumn().setName("ACCOUNT");
        creditLinesMapping.setMapKeyColumn().setName("BANK");
        creditLinesMapping.setCollectionTable().setName("CFGPU_EMP_CREDITLINES").addJoinColumn().setName("EMP_ID");
        creditLinesMapping.setTypeConverter().setName("Long2String").setDataType("String").setObjectType("Long");

        ObjectTypeConverter creditLinesConverter = creditLinesMapping.setObjectTypeConverter().setName("CreditLine");
        creditLinesConverter.addConversionValue().setDataValue("RBC").setObjectValue("RoyalBank");
        creditLinesConverter.addConversionValue().setDataValue("CIBC").setObjectValue("CanadianImperial");
        creditLinesConverter.addConversionValue().setDataValue("SB").setObjectValue("Scotiabank");
        creditLinesConverter.addConversionValue().setDataValue("TD").setObjectValue("TorontoDominion");

        Embedded periodMapping = emp.addEmbedded().setName("period").setAttributeType("EmploymentPeriod");
        periodMapping.addAttributeOverride().setName("startDate").setColumn().setName("START_DATE").setNullable(false);
        periodMapping.addAttributeOverride().setName("endDate").setColumn().setName("END_DATE").setNullable(true);
        periodMapping.addProperty().setName("attributeName").setValue("period");

        // Transformation.

        /********************************************************************/
        /************************** ADDRESS ENTITY **************************/
        /********************************************************************/
        Entity address = mappings.addEntity().setClass("Address");

        address.setTable().setName("CFGPU_ADDRESS");
        address.setChangeTracking().setType("DEFERRED");
        address.setCacheInterceptor().setInterceptorClassName("CacheAuditor");
        address.setQueryRedirectors().setAllQueriesRedirector("DoNotRedirect").setReadAllRedirector("DoNotRedirect").setReadObjectRedirector("DoNotRedirect").setReportRedirector("DoNotRedirect").setInsertRedirector("DoNotRedirect").setUpdateRedirector("DoNotRedirect").setDeleteRedirector("DoNotRedirect");
        address.setSequenceGenerator().setName("CFGPU_ADDRESS_SEQUENCE_GENERATOR").setSequenceName("CFGPU_ADDRESS_SEQ").setAllocationSize(25);

        NamedStoredProcedureQuery query = address.addNamedStoredProcedureQuery().setName("SProcAddress").addResultClass("Address").setProcedureName("SProc_Read_Address");
        query.addParameter().setMode("IN_OUT").setName("address_id_v").setQueryParameter("ADDRESS_ID").setType("Integer");
        query.addParameter().setMode("OUT").setName("street_v").setQueryParameter("STREET").setType("String");
        query.addParameter().setMode("OUT").setName("city_v").setQueryParameter("CITY").setType("String");
        query.addParameter().setMode("OUT").setName("country_v").setQueryParameter("COUNTRY").setType("String");
        query.addParameter().setMode("OUT").setName("province_v").setQueryParameter("PROVINCE").setType("String");
        query.addParameter().setMode("OUT").setName("p_code_v").setQueryParameter("P_CODE").setType("String");

        address.addNamedNativeQuery().setName("findAllAddresses").setResultClass("Address").setQuery("SELECT * FROM ADDRESS");

        Id addressId = address.addId();
        addressId.setName("id").setAttributeType("Integer").setColumn().setName("ADDRESS_ID");
        addressId.setGeneratedValue().setStrategy("SEQUENCE").setGenerator("CFGPU_ADDRESS_SEQUENCE_GENERATOR");

        address.addBasic().setName("postalCode").setAttributeType("String");
        address.addBasic().setName("street").setAttributeType("String");
        address.addBasic().setName("city").setAttributeType("String");
        address.addBasic().setName("province").setAttributeType("String");
        address.addBasic().setName("country").setAttributeType("String");

        /********************************************************************/
        /************************ PHONENUMBER ENTITY ************************/
        /********************************************************************/
        Entity phoneNumber = mappings.addEntity().setClass("PhoneNumber").setIdClass("PhoneNumberPK");
        phoneNumber.setTable().setName("CFGPU_PHONENUMBER");

        phoneNumber.addId().setName("id").setAttributeType("Integer").setColumn().setName("OWNER_ID").setInsertable(false).setUpdatable(false);
        phoneNumber.addId().setName("type").setAttributeType("String").setColumn().setName("TYPE");
        phoneNumber.addBasic().setName("number").setAttributeType("String").setColumn().setName("NUMB");
        phoneNumber.addBasic().setName("areaCode").setAttributeType("String").setColumn().setName("AREA_CODE");

        phoneNumber.addManyToOne().setName("owner").setTargetEntity("Employee").addJoinColumn().setName("OWNER_ID").setReferencedColumnName("EMP_ID");

        /************************** LARGEPROJECT ENTITY **************************/
        Entity largeProject = mappings.addEntity().setClass("LargeProject").setParentClass("Project").setExistenceChecking("ASSUME_NON_EXISTENCE");
        largeProject.setTable().setName("CFGPU_LPROJECT");
        largeProject.setDiscriminatorValue("L");

        /********************************************************************/
        /*********************** SMALLPROJECT ENTITY ************************/
        /********************************************************************/
        Entity smallProject = mappings.addEntity().setClass("SmallProject").setParentClass("Project").setExistenceChecking("ASSUME_EXISTENCE");
        smallProject.setTable().setName("CFGPU_PROJECT");
        smallProject.setDiscriminatorValue("S");

        /********************************************************************/
        /************************** PROJECT ENTITY **************************/
        /********************************************************************/
        Entity project = mappings.addEntity().setClass("Project").setExistenceChecking("CHECK_CACHE");
        project.setTable().setName("CFGPU_PROJECT");
        project.setChangeTracking().setType("OBJECT");
        project.setOptimisticLocking().setType("SELECTED_COLUMNS").addSelectedColumn().setName("VERSION");
        project.setInheritance().setStrategy("JOINED");
        project.setDiscriminatorValue("P").setDiscriminatorColumn().setName("PROJ_TYPE");
        project.setSequenceGenerator().setName("CFGPU_PROJECT_SEQUENCE_GENERATOR").setSequenceName("CFGPU_PROJECT_SEQ").setAllocationSize(10);

        // TODO: not supported using VIRTUAL.
        //project.setPrePersist("prePersist");
        //project.setPostPersist("postPersist");
        //project.setPreRemove("preRemove");
        //project.setPostRemove("postRemove");
        //project.setPreUpdate("preUpdate");
        //project.setPostUpdate("postUpdate");
        //project.setPostLoad("postLoad");

        Id projectId = project.addId().setName("id").setAttributeType("Integer");
        projectId.setColumn().setName("PROJ_ID");
        projectId.setGeneratedValue().setStrategy("SEQUENCE").setGenerator("CFGPU_PROJECT_SEQUENCE_GENERATOR");

        project.addBasic().setName("name").setAttributeType("String").setColumn().setName("PROJ_NAME");
        project.addBasic().setName("description").setAttributeType("String").setColumn().setName("DESCRIP");
        project.addVersion().setName("version").setAttributeType("Integer").setColumn().setName("VERSION");

        project.addOneToOne().setName("teamLeader").setTargetEntity("Employee").addJoinColumn().setName("LEADER_ID");

        project.addManyToMany().setName("teamMembers").setTargetEntity("Employee").setAttributeType("java.util.List").setMappedBy("projects");

        /********************************************************************/
        /******************* EMPLOYMENT PERIOD EMBEDDABLE *******************/
        /********************************************************************/
        Embeddable employmentPeriod = mappings.addEmbeddable().setClass("EmploymentPeriod");
        Basic startDate = employmentPeriod.addBasic().setName("startDate").setAttributeType("java.util.Date");
        startDate.setColumn().setName("S_DATE");
        startDate.setTemporal().setType("DATE");

        Basic endDate = employmentPeriod.addBasic().setName("endDate").setAttributeType("java.util.Date");
        endDate.setColumn().setName("E_DATE");
        endDate.setTemporal().setType("DATE");

        emf = RuntimeFactory.getInstance().createEntityManagerFactory(pu);
    }

    public void testVerifyConfigPU() {
        Assert.assertNotNull(emf);

        JPADynamicHelper helper = new JPADynamicHelper(emf);

        DynamicType personType = helper.getType("Person");

        Assert.assertNotNull(personType);
    }

    public void testCRUDConfigPU() {
        EntityManager em = emf.createEntityManager();

        try {
            JPADynamicHelper helper = new JPADynamicHelper(em);

            DynamicType personType = helper.getType("Person");

            DynamicEntity person = personType.newDynamicEntity();
            person.set("name", "Test");

            em.getTransaction().begin();
            em.persist(person);
            em.getTransaction().commit();

        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            em.close();
            emf.close();
        }
    }
}
