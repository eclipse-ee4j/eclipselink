/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.mapping;

import java.util.Vector;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.identitymaps.FullIdentityMap;

import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.TransformationMapping;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.mappings.converters.SerializedObjectConverter;
import org.eclipse.persistence.mappings.converters.TypeConversionConverter;

import org.eclipse.persistence.sessions.Project;

import org.eclipse.persistence.testing.models.mapping.Address;
import org.eclipse.persistence.testing.models.mapping.Computer;
import org.eclipse.persistence.testing.models.mapping.Employee;
import org.eclipse.persistence.testing.models.mapping.Monitor;
import org.eclipse.persistence.testing.models.mapping.Shipment;

public class MappingProject extends Project {
    public MappingProject() {
        setName("ComplexMappingSystem");
        
        buildAddressDescriptor();
        buildCompanyCardDescriptor();
        buildComputerDescriptor();
        buildCubicleDescriptor();
        buildEmergencyExitDescriptor();
        buildEmployeeDescriptor();
        buildHardwareDescriptor();
        buildMonitorDescriptor();
        buildPhoneDescriptor();
        buildShipmentDescriptor();
        buildSecureSystemDescriptor();
        buildIdentificationDescriptor();
    }

    protected void buildAddressDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(Address.class);
        Vector vector = new Vector();
        vector.addElement("MAP_ADD");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("MAP_ADD.A_ID");

        // SECTION: PROPERTIES
        descriptor.setSequenceNumberName("SEQ_ID");
        descriptor.setSequenceNumberFieldName("A_ID");

        // SECTION: COPY POLICY
        descriptor.createCopyPolicy("constructor");

        descriptor.useWeakIdentityMap();
        
        // SECTION: INSTANTIATION POLICY
        descriptor.createInstantiationPolicy("constructor");

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping = new DirectToFieldMapping();
        directtofieldmapping.setAttributeName("id");
        directtofieldmapping.setIsReadOnly(false);
        directtofieldmapping.setFieldName("MAP_ADD.A_ID");
        descriptor.addMapping(directtofieldmapping);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping1 = new DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("location");
        directtofieldmapping1.setIsReadOnly(false);
        directtofieldmapping1.setFieldName("MAP_ADD.LOCATION");
        descriptor.addMapping(directtofieldmapping1);

        // SECTION: ONETOONEMAPPING
        OneToOneMapping onetoonemapping = new OneToOneMapping();
        onetoonemapping.setAttributeName("employee");
        onetoonemapping.setIsReadOnly(false);
        onetoonemapping.setUsesIndirection(false);
        onetoonemapping.setReferenceClass(Employee.class);
        onetoonemapping.setIsPrivateOwned(false);
        onetoonemapping.addTargetForeignKeyFieldName("MAP_EMP.A_ID", "MAP_ADD.A_ID");
        descriptor.addMapping(onetoonemapping);

		// SECTION: TRANSFORMATIONMAPPING
		TransformationMapping transformationmapping = new TransformationMapping();
		transformationmapping.setAttributeName("province");
		transformationmapping.setIsReadOnly(false);
		transformationmapping.setUsesIndirection(false);
		transformationmapping.setAttributeTransformation("getProvinceFromRow");
		transformationmapping.addFieldTransformation("MAP_ADD.PROVINCE", "getProvinceFromObject");
		descriptor.addMapping(transformationmapping);
        addDescriptor(descriptor);
    }

    protected void buildCompanyCardDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(CompanyCard.class);
        Vector vector = new Vector();
        vector.addElement("MAP_CARD");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("MAP_CARD.COM_ID");

        // SECTION: PROPERTIES
        descriptor.setIdentityMapClass(FullIdentityMap.class);
        descriptor.setExistenceChecking("Check cache");
        descriptor.setIdentityMapSize(100);

        // SECTION: COPY POLICY
        descriptor.createCopyPolicy("constructor");

        // SECTION: INSTANTIATION POLICY
        descriptor.createInstantiationPolicy("constructor");

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping1 = new DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("number");
        directtofieldmapping1.setIsReadOnly(false);
        directtofieldmapping1.setFieldName("MAP_CARD.CARDNUMBER");
        descriptor.addMapping(directtofieldmapping1);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping2 = new DirectToFieldMapping();
        directtofieldmapping2.setAttributeName("limit");
        directtofieldmapping2.setIsReadOnly(false);
        directtofieldmapping2.setFieldName("MAP_CARD.AMOUNT_LIMIT");
        descriptor.addMapping(directtofieldmapping2);

        // SECTION: ONETOONEMAPPING
        OneToOneMapping oneToOneMapping = new OneToOneMapping();
        oneToOneMapping.setReferenceClass(Address.class);
        oneToOneMapping.setAttributeName("owner");
        oneToOneMapping.setIsReadOnly(false);
        oneToOneMapping.addForeignKeyFieldName("MAP_CARD.COM_ID", "MAP_ADD.A_ID");
        oneToOneMapping.dontUseIndirection();
        descriptor.addMapping(oneToOneMapping);

        addDescriptor(descriptor);
    }

    protected void buildComputerDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(Computer.class);
        descriptor.getInheritancePolicy().setParentClass(Hardware.class);
        Vector vector = new Vector();
        vector.addElement("MAP_COM");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("MAP_COM.ID");

        // SECTION: PROPERTIES
        descriptor.getInheritancePolicy().setShouldReadSubclasses(false);

        // SECTION: COPY POLICY
        descriptor.createCopyPolicy("constructor");

        // SECTION: INSTANTIATION POLICY
        descriptor.createInstantiationPolicy("static method");
        descriptor.getInstantiationPolicy().setMethodName("createNew");

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping = new DirectToFieldMapping();
        directtofieldmapping.setAttributeName("description");
        directtofieldmapping.setIsReadOnly(false);
        directtofieldmapping.setFieldName("MAP_COM.DESCRIP");
        descriptor.addMapping(directtofieldmapping);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping1 = new DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("id");
        directtofieldmapping1.setIsReadOnly(false);
        directtofieldmapping1.setFieldName("MAP_COM.ID");
        descriptor.addMapping(directtofieldmapping1);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping2 = new DirectToFieldMapping();
        directtofieldmapping2.setAttributeName("serialNumber");
        directtofieldmapping2.setIsReadOnly(false);
        directtofieldmapping2.setGetMethodName("getSerialNumber");
        directtofieldmapping2.setSetMethodName("setSerialNumber");
        directtofieldmapping2.setFieldName("MAP_COM.SERL_NO");
        descriptor.addMapping(directtofieldmapping2);

        // SECTION: OBJECTTYPEMAPPING
        DirectToFieldMapping objecttypemapping = new DirectToFieldMapping();
        ObjectTypeConverter objecttypeconverter = new ObjectTypeConverter();
        objecttypemapping.setAttributeName("isMacintosh");
        objecttypemapping.setIsReadOnly(false);
        objecttypemapping.setFieldName("MAP_COM.IS_MAC");
        objecttypeconverter.addConversionValue("No", new Boolean(false));
        objecttypeconverter.addConversionValue("Yes", new Boolean(true));
        objecttypemapping.setNullValue(new Boolean(false));
        objecttypemapping.setConverter(objecttypeconverter);
        descriptor.addMapping(objecttypemapping);

        // SECTION: ONETOONEMAPPING
        OneToOneMapping onetoonemapping = new OneToOneMapping();
        onetoonemapping.setAttributeName("monitor");
        onetoonemapping.setIsReadOnly(false);
        onetoonemapping.setUsesIndirection(false);
        onetoonemapping.setGetMethodName("getMonitor");
        onetoonemapping.setSetMethodName("setMonitor");
        onetoonemapping.setReferenceClass(Monitor.class);
        onetoonemapping.setIsPrivateOwned(true);
        onetoonemapping.addForeignKeyFieldName("MAP_COM.MON_SER", "MAP_MON.SERL_NO");
        descriptor.addMapping(onetoonemapping);
        addDescriptor(descriptor);
    }

    protected void buildCubicleDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(Cubicle.class);
        Vector vector = new Vector();
        vector.addElement("MAP_CUB");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("MAP_CUB.C_ID");

        // SECTION: PROPERTIES
        descriptor.setSequenceNumberName("SEQ_ID");
        descriptor.setSequenceNumberFieldName("C_ID");

        // SECTION: COPY POLICY
        descriptor.createCopyPolicy("constructor");

        // SECTION: INSTANTIATION POLICY
        descriptor.createInstantiationPolicy("constructor");

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping typeConversionMapping = new DirectToFieldMapping();
        TypeConversionConverter typeConversionConverter = new TypeConversionConverter();
        typeConversionMapping.setAttributeName("id");
        typeConversionMapping.setIsReadOnly(false);
        typeConversionMapping.setFieldName("MAP_CUB.C_ID");
        typeConversionConverter.setObjectClass(java.math.BigDecimal.class);
        typeConversionConverter.setDataClass(String.class);
        typeConversionMapping.setConverter(typeConversionConverter);
        descriptor.addMapping(typeConversionMapping);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping1 = new DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("location");
        directtofieldmapping1.setIsReadOnly(false);
        directtofieldmapping1.setFieldName("MAP_CUB.LOCATION");
        descriptor.addMapping(directtofieldmapping1);

        // SECTION: MANYTOMANYMAPPING
        ManyToManyMapping manytomanymapping = new ManyToManyMapping();
        manytomanymapping.setAttributeName("emergencyExits");
        manytomanymapping.setIsReadOnly(false);
        manytomanymapping.setUsesIndirection(false);
        manytomanymapping.setReferenceClass(EmergencyExit.class);
        manytomanymapping.setIsPrivateOwned(false);
        manytomanymapping.setRelationTableName("CUBICLE_EMERGENCYEXIT");
        manytomanymapping.addSourceRelationKeyFieldName("CUBICLE_EMERGENCYEXIT.CUBICLE_LOCATION", "MAP_CUB.LOCATION");
        manytomanymapping.addTargetRelationKeyFieldName("CUBICLE_EMERGENCYEXIT.EXIT_ID", "MAP_EMERGENCYEXIT.EXIT_ID");
        descriptor.addMapping(manytomanymapping);

        addDescriptor(descriptor);
    }

    protected void buildEmergencyExitDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(EmergencyExit.class);
        Vector vector = new Vector();
        vector.addElement("MAP_EMERGENCYEXIT");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("MAP_EMERGENCYEXIT.EXIT_ID");

        // SECTION: PROPERTIES
        descriptor.setSequenceNumberName("SEQ_ID");
        descriptor.setSequenceNumberFieldName("EXIT_ID");

        // SECTION: COPY POLICY
        descriptor.createCopyPolicy("constructor");

        // SECTION: INSTANTIATION POLICY
        descriptor.createInstantiationPolicy("constructor");

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping = new DirectToFieldMapping();
        directtofieldmapping.setAttributeName("id");
        directtofieldmapping.setIsReadOnly(false);
        directtofieldmapping.setFieldName("MAP_EMERGENCYEXIT.EXIT_ID");
        descriptor.addMapping(directtofieldmapping);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping1 = new DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("location");
        directtofieldmapping1.setIsReadOnly(false);
        directtofieldmapping1.setFieldName("MAP_EMERGENCYEXIT.LOCATION");
        descriptor.addMapping(directtofieldmapping1);

        addDescriptor(descriptor);
    }

    protected void buildEmployeeDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(Employee.class);
        Vector vector = new Vector();
        vector.addElement("MAP_EMP");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("MAP_EMP.FNAME");
        descriptor.addPrimaryKeyFieldName("MAP_EMP.LNAME");
        descriptor.addPrimaryKeyFieldName("MAP_EMP.SEX");

        // SECTION: COPY POLICY
        descriptor.createCopyPolicy("constructor");
        
        descriptor.useSoftIdentityMap();
        
        // SECTION: INSTANTIATION POLICY
        descriptor.createInstantiationPolicy("constructor");

        // SECTION: DIRECTCOLLECTIONMAPPING
        DirectCollectionMapping directcollectionmapping = new DirectCollectionMapping();
        directcollectionmapping.setAttributeName("policies");
        directcollectionmapping.setIsReadOnly(false);
        directcollectionmapping.setUsesIndirection(true);
        directcollectionmapping.setIsPrivateOwned(false);
        directcollectionmapping.setDirectFieldName("MAP_POL.POLICY");
        directcollectionmapping.setReferenceTableName("MAP_POL");
        directcollectionmapping.addReferenceKeyFieldName("MAP_POL.LNAME", "MAP_EMP.LNAME");
        directcollectionmapping.addReferenceKeyFieldName("MAP_POL.FNAME", "MAP_EMP.FNAME");
        descriptor.addMapping(directcollectionmapping);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping = new DirectToFieldMapping();
        directtofieldmapping.setAttributeName("firstName");
        directtofieldmapping.setIsReadOnly(false);
        directtofieldmapping.setFieldName("MAP_EMP.FNAME");
        descriptor.addMapping(directtofieldmapping);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping1 = new DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("lastName");
        directtofieldmapping1.setIsReadOnly(false);
        directtofieldmapping1.setFieldName("MAP_EMP.LNAME");
        descriptor.addMapping(directtofieldmapping1);

        // SECTION: ONETOONEMAPPING
        OneToOneMapping onetoonemapping1 = new OneToOneMapping();
        onetoonemapping1.setAttributeName("cubicle");
        onetoonemapping1.setIsReadOnly(false);
        onetoonemapping1.setUsesIndirection(false);
        onetoonemapping1.setReferenceClass(Cubicle.class);
        onetoonemapping1.setIsPrivateOwned(true);
        onetoonemapping1.addForeignKeyFieldName("MAP_EMP.C_ID", "MAP_CUB.C_ID");
        descriptor.addMapping(onetoonemapping1);

        // SECTION: MANYTOMANYMAPPING
        ManyToManyMapping manytomanymapping = new ManyToManyMapping();
        manytomanymapping.setAttributeName("phoneNumbers");
        manytomanymapping.setIsReadOnly(false);
        manytomanymapping.setUsesIndirection(true);
        manytomanymapping.setReferenceClass(Phone.class);
        manytomanymapping.setIsPrivateOwned(true);
        manytomanymapping.setRelationTableName("MAP_EMPH");
        manytomanymapping.addSourceRelationKeyFieldName("MAP_EMPH.LNAME", "MAP_EMP.LNAME");
        manytomanymapping.addSourceRelationKeyFieldName("MAP_EMPH.FNAME", "MAP_EMP.FNAME");
        manytomanymapping.addTargetRelationKeyFieldName("MAP_EMPH.P_ID", "MAP_PHO.P_ID");
        descriptor.addMapping(manytomanymapping);

        // SECTION: MANYTOMANYMAPPING
        ManyToManyMapping manytomanymapping1 = new ManyToManyMapping();
        manytomanymapping1.setAttributeName("shipments");
        manytomanymapping1.setIsReadOnly(false);
        manytomanymapping1.setUsesIndirection(false);
        manytomanymapping1.setReferenceClass(Shipment.class);
        manytomanymapping1.setIsPrivateOwned(false);
        manytomanymapping1.setRelationTableName("MAP_EMSP");
        manytomanymapping1.addSourceRelationKeyFieldName("MAP_EMSP.EMP_LNAME", "MAP_EMP.LNAME");
        manytomanymapping1.addSourceRelationKeyFieldName("MAP_EMSP.EMP_FNAME", "MAP_EMP.FNAME");
        manytomanymapping1.addTargetRelationKeyFieldName("MAP_EMSP.SP_TSMIL", "MAP_SHIP.SP_TSMIL");
        manytomanymapping1.addTargetRelationKeyFieldName("MAP_EMSP.SP_TS", "MAP_SHIP.SP_TS");
        descriptor.addMapping(manytomanymapping1);

        // SECTION: OBJECTTYPEMAPPING
        DirectToFieldMapping objecttypemapping = new DirectToFieldMapping();
        ObjectTypeConverter objecttypeconverter = new ObjectTypeConverter();
        objecttypemapping.setAttributeName("sex");
        objecttypemapping.setIsReadOnly(false);
        objecttypemapping.setFieldName("MAP_EMP.SEX");
        objecttypeconverter.setDefaultAttributeValue("female");
        objecttypeconverter.addConversionValue("F", "female");
        objecttypeconverter.addConversionValue("M", "male");
        objecttypemapping.setConverter(objecttypeconverter);
        descriptor.addMapping(objecttypemapping);

        // SECTION: ONETOMANYMAPPING
        OneToManyMapping onetomanymapping = new OneToManyMapping();
        onetomanymapping.setAttributeName("managedEmployees");
        onetomanymapping.setIsReadOnly(false);
        onetomanymapping.setUsesIndirection(false);
        onetomanymapping.setGetMethodName("getManagedEmployeesForTOPLink");
        onetomanymapping.setSetMethodName("setManagedEmployeesFromTOPLink");
        onetomanymapping.setReferenceClass(Employee.class);
        onetomanymapping.setIsPrivateOwned(false);
        onetomanymapping.addTargetForeignKeyFieldName("MAP_EMP.M_LNAME", "MAP_EMP.LNAME");
        onetomanymapping.addTargetForeignKeyFieldName("MAP_EMP.M_FNAME", "MAP_EMP.FNAME");
        descriptor.addMapping(onetomanymapping);

        // SECTION: ONETOONEMAPPING
        OneToOneMapping onetoonemapping = new OneToOneMapping();
        onetoonemapping.setAttributeName("computer");
        onetoonemapping.setIsReadOnly(false);
        onetoonemapping.setUsesIndirection(false);
        onetoonemapping.setGetMethodName("getComputer");
        onetoonemapping.setSetMethodName("setComputer");
        onetoonemapping.setReferenceClass(Hardware.class);
        onetoonemapping.setIsPrivateOwned(true);
        onetoonemapping.addTargetForeignKeyFieldName("MAP_HRW.EMP_FNAME", "MAP_EMP.FNAME");
        onetoonemapping.addTargetForeignKeyFieldName("MAP_HRW.EMP_LNAME", "MAP_EMP.LNAME");
        descriptor.addMapping(onetoonemapping);

        // SECTION: ONETOONEMAPPING
        OneToOneMapping onetoonemapping2 = new OneToOneMapping();
        onetoonemapping2.setAttributeName("manager");
        onetoonemapping2.setIsReadOnly(false);
        onetoonemapping2.setUsesIndirection(false);
        onetoonemapping2.setGetMethodName("getManager");
        onetoonemapping2.setSetMethodName("setManager");
        onetoonemapping2.setReferenceClass(Employee.class);
        onetoonemapping2.setIsPrivateOwned(false);
        onetoonemapping2.addForeignKeyFieldName("MAP_EMP.M_FNAME", "MAP_EMP.FNAME");
        onetoonemapping2.addForeignKeyFieldName("MAP_EMP.M_LNAME", "MAP_EMP.LNAME");
        descriptor.addMapping(onetoonemapping2);

        // SECTION: SERIALIZEDMAPPING
        DirectToFieldMapping serializedmapping = new DirectToFieldMapping();
        SerializedObjectConverter serializedconverter = new SerializedObjectConverter();
        serializedmapping.setAttributeName("jobDescription");
        serializedmapping.setIsReadOnly(false);
        serializedmapping.setGetMethodName("getJobDescription");
        serializedmapping.setSetMethodName("setJobDescription");
        serializedmapping.setFieldName("MAP_EMP.JDESC");
        serializedmapping.setConverter(serializedconverter);
        descriptor.addMapping(serializedmapping);

        // SECTION: TRANSFORMATIONMAPPING
        TransformationMapping transformationmapping = new TransformationMapping();
        transformationmapping.setAttributeName("dateAndTimeOfBirth");
        transformationmapping.setIsReadOnly(false);
        transformationmapping.setUsesIndirection(false);
        transformationmapping.setAttributeTransformation("setDateAndTime");
        transformationmapping.addFieldTransformation("MAP_EMP.BDAY", "getDate");
        transformationmapping.addFieldTransformation("MAP_EMP.BTIME", "getTime");
        descriptor.addMapping(transformationmapping);

        // SECTION: TRANSFORMATIONMAPPING
        TransformationMapping transformationmapping1 = new TransformationMapping();
        transformationmapping1.setAttributeName("designation");
        transformationmapping1.setIsReadOnly(false);
        transformationmapping1.setUsesIndirection(true);
        transformationmapping1.setGetMethodName("getDesignation");
        transformationmapping1.setSetMethodName("setDesignation");
        transformationmapping1.setAttributeTransformation("getRankFromRow");
        transformationmapping1.addFieldTransformation("MAP_EMP.RANK", "getRankFromObject");
        descriptor.addMapping(transformationmapping1);

        // SECTION: TYPECONVERSIONMAPPING
        DirectToFieldMapping typeconversionmapping = new DirectToFieldMapping();
        TypeConversionConverter typeconversionconverter = new TypeConversionConverter();
        typeconversionmapping.setAttributeName("joiningDate");
        typeconversionmapping.setIsReadOnly(false);
        typeconversionmapping.setFieldName("MAP_EMP.JDAY");
        typeconversionconverter.setObjectClass(java.util.Date.class);
        typeconversionconverter.setDataClass(java.sql.Date.class);
        typeconversionmapping.setConverter(typeconversionconverter);
        descriptor.addMapping(typeconversionmapping);

        addDescriptor(descriptor);
    }

    protected void buildIdentificationDescriptor(){
        RelationalDescriptor descriptor = new RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(Identification.class);
        Vector vector = new Vector();
        vector.addElement("MAP_IDENTIFICATION");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("MAP_IDENTIFICATION.ID");

        // SECTION: PROPERTIES
        descriptor.setSequenceNumberName("SEQ_ID");
        descriptor.setSequenceNumberFieldName("ID");

        // SECTION: COPY POLICY
        descriptor.createCopyPolicy("constructor");

        // SECTION: INSTANTIATION POLICY
        descriptor.createInstantiationPolicy("constructor");

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping = new DirectToFieldMapping();
        directtofieldmapping.setAttributeName("id");
        directtofieldmapping.setIsReadOnly(false);
        directtofieldmapping.setFieldName("MAP_IDENTIFICATION.ID");
        descriptor.addMapping(directtofieldmapping);

        addDescriptor(descriptor);
    }
    
    protected void buildHardwareDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(Hardware.class);
        Vector vector = new Vector();
        vector.addElement("MAP_HRW");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("MAP_HRW.ID");

        // SECTION: PROPERTIES
        descriptor.setSequenceNumberName("SEQ");
        descriptor.setSequenceNumberFieldName("ID");
        descriptor.getInheritancePolicy().setShouldReadSubclasses(true);
        descriptor.getInheritancePolicy().setClassIndicatorFieldName("TYPE");
        descriptor.getInheritancePolicy().setShouldUseClassNameAsIndicator(false);
        descriptor.getInheritancePolicy().addClassIndicator(Computer.class, "C");
        descriptor.getInheritancePolicy().addClassIndicator(Monitor.class, "M");

        // SECTION: COPY POLICY
        descriptor.createCopyPolicy("constructor");

        // SECTION: INSTANTIATION POLICY
        descriptor.createInstantiationPolicy("constructor");

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping = new DirectToFieldMapping();
        directtofieldmapping.setAttributeName("distibuted");
        directtofieldmapping.setIsReadOnly(false);
        directtofieldmapping.setFieldName("MAP_HRW.DIST");
        descriptor.addMapping(directtofieldmapping);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping1 = new DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("id");
        directtofieldmapping1.setIsReadOnly(false);
        directtofieldmapping1.setFieldName("MAP_HRW.ID");
        descriptor.addMapping(directtofieldmapping1);

        // SECTION: ONETOONEMAPPING
        OneToOneMapping onetoonemapping = new OneToOneMapping();
        onetoonemapping.setAttributeName("employee");
        onetoonemapping.setIsReadOnly(false);
        onetoonemapping.setUsesIndirection(false);
        onetoonemapping.setReferenceClass(Employee.class);
        onetoonemapping.setIsPrivateOwned(false);
        onetoonemapping.addForeignKeyFieldName("MAP_HRW.EMP_FNAME", "MAP_EMP.FNAME");
        onetoonemapping.addForeignKeyFieldName("MAP_HRW.EMP_LNAME", "MAP_EMP.LNAME");
        descriptor.addMapping(onetoonemapping);
        
        addDescriptor(descriptor);
    }

    protected void buildMonitorDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(Monitor.class);
        descriptor.getInheritancePolicy().setParentClass(Hardware.class);
        Vector vector = new Vector();
        vector.addElement("MAP_MON");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("MAP_MON.ID");

        // SECTION: PROPERTIES
        descriptor.getInheritancePolicy().setShouldReadSubclasses(false);

        // SECTION: COPY POLICY
        descriptor.createCopyPolicy("constructor");

        // SECTION: INSTANTIATION POLICY
        descriptor.createInstantiationPolicy("constructor");

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping = new DirectToFieldMapping();
        directtofieldmapping.setAttributeName("brand");
        directtofieldmapping.setIsReadOnly(false);
        directtofieldmapping.setGetMethodName("getBrand");
        directtofieldmapping.setSetMethodName("setBrand");
        directtofieldmapping.setFieldName("MAP_MON.BRAND");
        descriptor.addMapping(directtofieldmapping);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping1 = new DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("serialNumber");
        directtofieldmapping1.setIsReadOnly(false);
        directtofieldmapping1.setGetMethodName("getSerialNumber");
        directtofieldmapping1.setSetMethodName("setSerialNumber");
        directtofieldmapping1.setFieldName("MAP_MON.SERL_NO");
        descriptor.addMapping(directtofieldmapping1);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping2 = new DirectToFieldMapping();
        directtofieldmapping2.setAttributeName("size");
        directtofieldmapping2.setIsReadOnly(false);
        directtofieldmapping2.setGetMethodName("getSize");
        directtofieldmapping2.setSetMethodName("setSize");
        directtofieldmapping2.setFieldName("MAP_MON.MSIZE");
        descriptor.addMapping(directtofieldmapping2);

        // SECTION: ONETOONEMAPPING
        OneToOneMapping onetoonemapping = new OneToOneMapping();
        onetoonemapping.setAttributeName("computer");
        onetoonemapping.setIsReadOnly(false);
        onetoonemapping.setUsesIndirection(false);
        onetoonemapping.setGetMethodName("getComputer");
        onetoonemapping.setSetMethodName("setComputer");
        onetoonemapping.setReferenceClass(Computer.class);
        onetoonemapping.setIsPrivateOwned(false);
        onetoonemapping.addForeignKeyFieldName("MAP_MON.COM_SER", "MAP_COM.SERL_NO");
        descriptor.addMapping(onetoonemapping);

        addDescriptor(descriptor);
    }

    protected void buildPhoneDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(Phone.class);
        Vector vector = new Vector();
        vector.addElement("MAP_PHO");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("MAP_PHO.P_ID");

        // SECTION: PROPERTIES
        descriptor.setSequenceNumberName("SEQ_ID");
        descriptor.setSequenceNumberFieldName("P_ID");

        // SECTION: COPY POLICY
        descriptor.createCopyPolicy("constructor");

        descriptor.useHardCacheWeakIdentityMap();
        
        // SECTION: INSTANTIATION POLICY
        descriptor.createInstantiationPolicy("constructor");

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping = new DirectToFieldMapping();
        directtofieldmapping.setAttributeName("areaCode");
        directtofieldmapping.setIsReadOnly(false);
        directtofieldmapping.setFieldName("MAP_PHO.AREACODE");
        descriptor.addMapping(directtofieldmapping);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping1 = new DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("id");
        directtofieldmapping1.setIsReadOnly(false);
        directtofieldmapping1.setFieldName("MAP_PHO.P_ID");
        descriptor.addMapping(directtofieldmapping1);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping2 = new DirectToFieldMapping();
        directtofieldmapping2.setAttributeName("number");
        directtofieldmapping2.setIsReadOnly(false);
        directtofieldmapping2.setFieldName("MAP_PHO.PNUMBER");
        descriptor.addMapping(directtofieldmapping2);
        addDescriptor(descriptor);
    }

    protected void buildSecureSystemDescriptor(){
        RelationalDescriptor descriptor = new RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(SecureSystem.class);
        Vector vector = new Vector();
        vector.addElement("MAP_SECURE");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("MAP_SECURE.MANUFACTURER");
        descriptor.addPrimaryKeyFieldName("MAP_SECURE.IDENTIFICATION_ID");

        // SECTION: PROPERTIES
        // SECTION: COPY POLICY
        descriptor.createCopyPolicy("constructor");

        // SECTION: INSTANTIATION POLICY
        descriptor.createInstantiationPolicy("constructor");

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping = new DirectToFieldMapping();
        directtofieldmapping.setAttributeName("manufacturer");
        directtofieldmapping.setIsReadOnly(false);
        directtofieldmapping.setFieldName("MAP_SECURE.MANUFACTURER");
        descriptor.addMapping(directtofieldmapping);

        // SECTION: ONETOONEMAPPING
        OneToOneMapping onetoonemapping1 = new OneToOneMapping();
        onetoonemapping1.setAttributeName("id");
        onetoonemapping1.setIsReadOnly(false);
        onetoonemapping1.setUsesIndirection(false);
        onetoonemapping1.setReferenceClass(Identification.class);
        onetoonemapping1.setIsPrivateOwned(false);
        onetoonemapping1.addForeignKeyFieldName("MAP_SECURE.IDENTIFICATION_ID", "MAP_IDENTIFICATION.ID");
        descriptor.addMapping(onetoonemapping1);

        addDescriptor(descriptor);
    }
    
    protected void buildShipmentDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(Shipment.class);
        Vector vector = new Vector();
        vector.addElement("MAP_SHIP");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("MAP_SHIP.SP_TS");
        descriptor.addPrimaryKeyFieldName("MAP_SHIP.SP_TSMIL");

        // SECTION: EVENT MANAGER
        descriptor.getEventManager().setPreInsertSelector("prepareForInsert");

        // SECTION: PROPERTIES
        // SECTION: COPY POLICY
        descriptor.createCopyPolicy("constructor");

        // SECTION: INSTANTIATION POLICY
        descriptor.createInstantiationPolicy("constructor");

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping = new DirectToFieldMapping();
        directtofieldmapping.setAttributeName("creationTimestamp");
        directtofieldmapping.setIsReadOnly(false);
        directtofieldmapping.setFieldName("MAP_SHIP.SP_TS");
        descriptor.addMapping(directtofieldmapping);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping1 = new DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("creationTimestampMillis");
        directtofieldmapping1.setIsReadOnly(false);
        directtofieldmapping1.setFieldName("MAP_SHIP.SP_TSMIL");
        descriptor.addMapping(directtofieldmapping1);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping2 = new DirectToFieldMapping();
        directtofieldmapping2.setAttributeName("quantityShipped");
        directtofieldmapping2.setIsReadOnly(false);
        directtofieldmapping2.setFieldName("MAP_SHIP.QUANTITY");
        descriptor.addMapping(directtofieldmapping2);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping3 = new DirectToFieldMapping();
        directtofieldmapping3.setAttributeName("shipMode");
        directtofieldmapping3.setIsReadOnly(false);
        directtofieldmapping3.setFieldName("MAP_SHIP.SHP_MODE");
        descriptor.addMapping(directtofieldmapping3);

        // SECTION: MANYTOMANYMAPPING
        ManyToManyMapping manytomanymapping = new ManyToManyMapping();
        manytomanymapping.setAttributeName("employees");
        manytomanymapping.setIsReadOnly(true);
        manytomanymapping.setUsesIndirection(false);
        manytomanymapping.setReferenceClass(Employee.class);
        manytomanymapping.setIsPrivateOwned(false);
        manytomanymapping.setRelationTableName("MAP_EMSP");
        manytomanymapping.addSourceRelationKeyFieldName("MAP_EMSP.SP_TSMIL", "MAP_SHIP.SP_TSMIL");
        manytomanymapping.addSourceRelationKeyFieldName("MAP_EMSP.SP_TS", "MAP_SHIP.SP_TS");
        manytomanymapping.addTargetRelationKeyFieldName("MAP_EMSP.EMP_LNAME", "MAP_EMP.LNAME");
        manytomanymapping.addTargetRelationKeyFieldName("MAP_EMSP.EMP_FNAME", "MAP_EMP.FNAME");
        descriptor.addMapping(manytomanymapping);
        addDescriptor(descriptor);
    }
}
