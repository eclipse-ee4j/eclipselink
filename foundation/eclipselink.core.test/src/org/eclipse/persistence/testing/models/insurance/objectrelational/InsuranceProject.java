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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.insurance.objectrelational;

import java.util.*;

import org.eclipse.persistence.testing.models.insurance.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.mappings.converters.*;
import org.eclipse.persistence.mappings.structures.*;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.tools.schemaframework.*;

/**
 * This project is provided to demonstate the usage of TopLink's object-relational features.
 */
public class InsuranceProject extends org.eclipse.persistence.sessions.Project {
    public InsuranceProject() {
        addDescriptor(buildAddressDescriptor());
        addDescriptor(buildClaimDescriptor());
        addDescriptor(buildHealthClaimDescriptor());
        addDescriptor(buildHealthPolicyDescriptor());
        addDescriptor(buildHouseClaimDescriptor());
        addDescriptor(buildHousePolicyDescriptor());
        addDescriptor(buildPhoneDescriptor());
        addDescriptor(buildPolicyDescriptor());
        addDescriptor(buildPolicyHolderDescriptor());
        addDescriptor(buildVehicleClaimDescriptor());
        addDescriptor(buildVehiclePolicyDescriptor());
        addDescriptor(buildBicyclePolicyDescriptor());
    }

    /**
     * Return the address descriptor.
     */
    public static ClassDescriptor buildAddressDescriptor() {
        ObjectRelationalDataTypeDescriptor descriptor = new ObjectRelationalDataTypeDescriptor();
        descriptor.setJavaClass(Address.class);
        descriptor.descriptorIsAggregate();

        descriptor.setStructureName("ADDRESS_TYPE");

        descriptor.addDirectMapping("street", "getStreet", "setStreet", "STREET");
        descriptor.addDirectMapping("city", "getCity", "setCity", "CITY");
        descriptor.addDirectMapping("state", "getState", "setState", "STATE");
        descriptor.addDirectMapping("country", "getCountry", "setCountry", "COUNTRY");
        descriptor.addDirectMapping("zipCode", "getZipCode", "setZipCode", "ZIPCODE");

        descriptor.setShouldOrderMappings(false);
        return descriptor;
    }

    public static TypeDefinition buildAddressTypeDefinition() {
        TypeDefinition definition = new TypeDefinition();

        definition.setName("Address_type");

        definition.addField("street", String.class, 30);
        definition.addField("city", String.class, 25);
        definition.addField("state", String.class, 2);
        definition.addField("country", String.class, 20);
        definition.addField("zipCode", String.class, 10);

        return definition;
    }

    /**
     * Return the claim descriptor.
     * This descriptor is a root descriptor.
     */
    public static ClassDescriptor buildClaimDescriptor() {
        ObjectRelationalDataTypeDescriptor descriptor = new ObjectRelationalDataTypeDescriptor();
        descriptor.setJavaClass(Claim.class);
        descriptor.setTableName("Claims");
        descriptor.setPrimaryKeyFieldName("ID");

        descriptor.setStructureName("CLAIM_TYPE");
        descriptor.addFieldOrdering("ID");
        descriptor.addFieldOrdering("POLICYREF");
        descriptor.addFieldOrdering("TYPE");
        descriptor.addFieldOrdering("AMOUNT");
        descriptor.addFieldOrdering("DISEASE");
        descriptor.addFieldOrdering("PART");
        descriptor.addFieldOrdering("PARTDESCRIPTION");

        descriptor.getDescriptorInheritancePolicy().addClassIndicator(HouseClaim.class, "H");
        descriptor.getDescriptorInheritancePolicy().addClassIndicator(HealthClaim.class, "E");
        descriptor.getDescriptorInheritancePolicy().addClassIndicator(VehicleClaim.class, "V");

        descriptor.getDescriptorInheritancePolicy().setClassIndicatorFieldName("TYPE");

        descriptor.addDirectMapping("id", "getId", "setId", "ID");
        descriptor.addDirectMapping("amount", "getAmount", "setAmount", "AMOUNT");

        ReferenceMapping policyMapping = new ReferenceMapping();
        policyMapping.setAttributeName("policy");
        policyMapping.setGetMethodName("getPolicy");
        policyMapping.setSetMethodName("setPolicy");
        policyMapping.setReferenceClass(Policy.class);
        policyMapping.setFieldName("POLICYREF");
        policyMapping.dontUseIndirection();
        descriptor.addMapping(policyMapping);

        return descriptor;
    }

    public static NestedTableDefinition buildClaimsTypeDefinition() {
        NestedTableDefinition definition = new NestedTableDefinition();

        definition.setName("Claims_type");
        definition.setTypeName("Ref Claim_type");

        return definition;
    }

    public static TypeTableDefinition buildClaimTableDefinition() {
        TypeTableDefinition definition = new TypeTableDefinition();

        definition.setName("Claims");
        definition.setTypeName("Claim_type");

        definition.addPrimaryKeyField("id", Long.class);
        definition.addForeignKeyConstraint("Claim_PolicyRef_FKey", "policyRef", "id", "Policies");

        return definition;
    }

    public static TypeDefinition buildClaimTypeDefinition() {
        TypeDefinition definition = new TypeDefinition();

        definition.setName("Claim_type");

        definition.addField("id", Long.class);
        definition.addField("policyRef", "Ref Policy_type");
        definition.addField("type", Character.class);
        definition.addField("amount", Float.class);
        definition.addField("disease", String.class, 50);
        definition.addField("area", Float.class);
        definition.addField("part", String.class, 30);
        definition.addField("partDescription", String.class, 2000);

        return definition;
    }

    /**
     * Return the health claim descriptor.
     * This descriptor inherits from the claim descriptor.
     */
    public static ClassDescriptor buildHealthClaimDescriptor() {
        ObjectRelationalDataTypeDescriptor descriptor = new ObjectRelationalDataTypeDescriptor();
        descriptor.setJavaClass(HealthClaim.class);
        descriptor.getDescriptorInheritancePolicy().setParentClass(Claim.class);

        descriptor.addDirectMapping("disease", "getDisease", "setDisease", "DISEASE");

        return descriptor;
    }

    /**
     * Return the health policy descriptor.
     * This descriptor inherits from the policy descriptor.
     */
    public static ClassDescriptor buildHealthPolicyDescriptor() {
        ObjectRelationalDataTypeDescriptor descriptor = new ObjectRelationalDataTypeDescriptor();
        descriptor.setJavaClass(HealthPolicy.class);
        descriptor.getDescriptorInheritancePolicy().setParentClass(Policy.class);

        descriptor.addDirectMapping("coverageRate", "getCoverageRate", "setCoverageRate", "COVERAGERATE");

        return descriptor;
    }

    /**
     * Return the house claim descriptor.
     * This descriptor inherits from the claim descriptor.
     */
    public static ClassDescriptor buildHouseClaimDescriptor() {
        ObjectRelationalDataTypeDescriptor descriptor = new ObjectRelationalDataTypeDescriptor();
        descriptor.setJavaClass(HouseClaim.class);
        descriptor.getDescriptorInheritancePolicy().setParentClass(Claim.class);

        descriptor.addDirectMapping("area", "getArea", "setArea", "AREA");

        return descriptor;
    }

    /**
     * Return the house policy descriptor.
     * This descriptor inherits from the policy descriptor.
     */
    public static ClassDescriptor buildHousePolicyDescriptor() {
        ObjectRelationalDataTypeDescriptor descriptor = new ObjectRelationalDataTypeDescriptor();
        descriptor.setJavaClass(HousePolicy.class);
        descriptor.getDescriptorInheritancePolicy().setParentClass(Policy.class);

        descriptor.addDirectMapping("dateOfConstruction", "getDateOfConstruction", "setDateOfConstruction", 
                                    "DATEOFCONSTRUCTION");
        return descriptor;
    }

    public static VarrayDefinition buildNameListTypeDefinition() {
        VarrayDefinition definition = new VarrayDefinition();

        definition.setName("NameList_type");
        definition.setSize(20);
        definition.setType(String.class);
        definition.setTypeSize(30);

        return definition;
    }

    /**
     * Return the phone device descriptor.
     */
    public static ClassDescriptor buildPhoneDescriptor() {
        ObjectRelationalDataTypeDescriptor descriptor = new ObjectRelationalDataTypeDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(Phone.class);
        Vector vector = new Vector();
        vector.addElement("PolicyHolders");
        descriptor.setTableNames(vector);

        // SECTION: PROPERTIES
        descriptor.descriptorIsAggregate();

        descriptor.setStructureName("PHONE_TYPE");
        descriptor.addFieldOrdering("PHONETYPE");
        descriptor.addFieldOrdering("AREACODE");
        descriptor.addFieldOrdering("PHONENUMBER");

        descriptor.addDirectMapping("type", "getType", "setType", "PHONETYPE");
        descriptor.addDirectMapping("areaCode", "getAreaCode", "setAreaCode", "AREACODE");
        descriptor.addDirectMapping("number", "getNumber", "setNumber", "PHONENUMBER");

        return descriptor;
    }

    public static ObjectVarrayDefinition buildPhoneListTypeDefinition() {
        ObjectVarrayDefinition definition = new ObjectVarrayDefinition();

        definition.setName("PhoneList_type");
        definition.setSize(20);
        definition.setType(Phone.class);
        definition.setTypeName("Phone_type");
        definition.setIsNullAllowed(true);

        return definition;
    }

    public static TypeDefinition buildPhoneTypeDefinition() {
        TypeDefinition definition = new TypeDefinition();

        definition.setName("Phone_type");

        definition.addField("type", String.class);
        definition.addField("areaCode", Long.class, 3);
        definition.addField("phoneNumber", Long.class, 7);

        return definition;
    }

    public static NestedTableDefinition buildPoliciesTypeDefinition() {
        NestedTableDefinition definition = new NestedTableDefinition();

        definition.setName("Policies_type");
        definition.setTypeName("Ref Policy_type");

        return definition;
    }

    /**
     * Return the policy descriptor.
     * This descriptor is a root descriptor.
     */
    public static ClassDescriptor buildPolicyDescriptor() {
        ObjectRelationalDataTypeDescriptor descriptor = new ObjectRelationalDataTypeDescriptor();
        descriptor.setJavaClass(Policy.class);
        descriptor.setTableName("Policies");
        descriptor.setPrimaryKeyFieldName("ID");

        descriptor.setStructureName("POLICY_TYPE");
        descriptor.addFieldOrdering("ID");
        descriptor.addFieldOrdering("POLICYHOLDERREF");
        descriptor.addFieldOrdering("DESCRIPTION");
        descriptor.addFieldOrdering("TYPE");
        descriptor.addFieldOrdering("MAXCOVERAGE");
        descriptor.addFieldOrdering("COVERAGERATE");
        descriptor.addFieldOrdering("DATEOFCONSTRUCTION");
        descriptor.addFieldOrdering("MODEL");
        descriptor.addFieldOrdering("COLOR");

        descriptor.getDescriptorInheritancePolicy().addClassIndicator(HousePolicy.class, "H");
        descriptor.getDescriptorInheritancePolicy().addClassIndicator(HealthPolicy.class, "E");
        descriptor.getDescriptorInheritancePolicy().addClassIndicator(VehiclePolicy.class, "V");
        descriptor.getDescriptorInheritancePolicy().addClassIndicator(BicyclePolicy.class, "B");

        descriptor.getDescriptorInheritancePolicy().setClassIndicatorFieldName("TYPE");

        descriptor.addDirectMapping("policyNumber", "getPolicyNumber", "setPolicyNumber", "ID");
        descriptor.addDirectMapping("description", "getDescription", "setDescription", "DESCRIPTION");
        descriptor.addDirectMapping("maxCoverage", "getMaxCoverage", "setMaxCoverage", "MAXCOVERAGE");

        ReferenceMapping holderMapping = new ReferenceMapping();
        holderMapping.setAttributeName("policyHolder");
        holderMapping.setGetMethodName("getPolicyHolder");
        holderMapping.setSetMethodName("setPolicyHolder");
        holderMapping.setReferenceClass(PolicyHolder.class);
        holderMapping.setFieldName("POLICYHOLDERREF");
        holderMapping.dontUseIndirection();
        descriptor.addMapping(holderMapping);

        NestedTableMapping claimMapping = new NestedTableMapping();
        claimMapping.setAttributeName("claims");
        claimMapping.setGetMethodName("getClaims");
        claimMapping.setSetMethodName("setClaims");
        claimMapping.setReferenceClass(Claim.class);
        claimMapping.dontUseIndirection();
        claimMapping.setStructureName("CLAIMS_TYPE");
        claimMapping.setFieldName("CLAIMS");
        claimMapping.privateOwnedRelationship();
        claimMapping.setSelectionSQLString("select c.* from policies p, table(p.claims) t, claims c where p.id=#ID and ref(c) = value(t)");
        descriptor.addMapping(claimMapping);

        return descriptor;
    }

    /**
     * Return the policy holder descriptor.
     */
    public static ClassDescriptor buildPolicyHolderDescriptor() {
        ObjectRelationalDataTypeDescriptor descriptor = new ObjectRelationalDataTypeDescriptor();
        descriptor.setJavaClass(PolicyHolder.class);
        descriptor.setTableName("PolicyHolders");
        descriptor.setPrimaryKeyFieldName("SSN");

        descriptor.setStructureName("POLICYHOLDER_TYPE");
        descriptor.addFieldOrdering("SSN");
        descriptor.addFieldOrdering("FIRSTNAME");
        descriptor.addFieldOrdering("LASTNAME");
        descriptor.addFieldOrdering("SEX");
        descriptor.addFieldOrdering("BIRTHDATE");
        descriptor.addFieldOrdering("OCCUPATION");
        descriptor.addFieldOrdering("ADDRESS");
        descriptor.addFieldOrdering("CHILDRENNAMES");
        descriptor.addFieldOrdering("PHONEDEVICES");
        descriptor.addFieldOrdering("POLICY_NESTED_T");

        descriptor.addDirectMapping("ssn", "getSsn", "setSsn", "SSN");
        descriptor.addDirectMapping("firstName", "getFirstName", "setFirstName", "FIRSTNAME");
        descriptor.addDirectMapping("lastName", "getLastName", "setLastName", "LASTNAME");
        descriptor.addDirectMapping("birthDate", "getBirthDate", "setBirthDate", "BIRTHDATE");
        descriptor.addDirectMapping("occupation", "getOccupation", "setOccupation", "OCCUPATION");

        DirectToFieldMapping typeMapping = new DirectToFieldMapping();
        typeMapping.setAttributeName("sex");
        typeMapping.setGetMethodName("getSex");
        typeMapping.setSetMethodName("setSex");
        typeMapping.setFieldName("SEX");
        ObjectTypeConverter typeConverter = new ObjectTypeConverter();
        typeConverter.addConversionValue("M", "Male");
        typeConverter.addConversionValue("F", "Female");
        typeMapping.setConverter(typeConverter);
        descriptor.addMapping(typeMapping);

        StructureMapping mapping = new StructureMapping();
        mapping.setAttributeName("address");
        mapping.setGetMethodName("getAddress");
        mapping.setSetMethodName("setAddress");
        mapping.setReferenceClass(Address.class);
        mapping.setFieldName("ADDRESS");
        descriptor.addMapping(mapping);

        ArrayMapping childrenNamesMapping = new ArrayMapping();
        childrenNamesMapping.setAttributeName("childrenNames");
        childrenNamesMapping.setGetMethodName("getChildrenNames");
        childrenNamesMapping.setSetMethodName("setChildrenNames");
        childrenNamesMapping.setStructureName("NAMELIST_TYPE");
        childrenNamesMapping.setFieldName("CHILDRENNAMES");
        descriptor.addMapping(childrenNamesMapping);

        ObjectArrayMapping phoneDevicesMapping = new ObjectArrayMapping();
        phoneDevicesMapping.setAttributeName("phones");
        phoneDevicesMapping.setGetMethodName("getPhones");
        phoneDevicesMapping.setSetMethodName("setPhones");
        phoneDevicesMapping.setStructureName("PHONELIST_TYPE");
        phoneDevicesMapping.setReferenceClass(Phone.class);
        phoneDevicesMapping.setFieldName("PHONES");
        descriptor.addMapping(phoneDevicesMapping);

        NestedTableMapping policiesMapping = new NestedTableMapping();
        policiesMapping.setAttributeName("policies");
        policiesMapping.setGetMethodName("getPolicies");
        policiesMapping.setSetMethodName("setPolicies");
        policiesMapping.setReferenceClass(Policy.class);
        policiesMapping.dontUseIndirection();
        policiesMapping.setStructureName("POLICIES_TYPE");
        policiesMapping.setFieldName("POLICIES");
        policiesMapping.privateOwnedRelationship();
        policiesMapping.setSelectionSQLString("select p.* from policyHolders ph, table(ph.policies) t, policies p where ph.ssn=#SSN and ref(p) = value(t)");
        descriptor.addMapping(policiesMapping);

        return descriptor;
    }

    public static TableDefinition buildPolicyHolderPoliciesTableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("PolicyHolders_Policies");

        definition.addPrimaryKeyField("PolicyHolder_ssn", Long.class);
        definition.addPrimaryKeyField("Policy_id", Long.class);

        return definition;
    }

    public static TypeTableDefinition buildPolicyHolderTableDefinition() {
        TypeTableDefinition definition = new TypeTableDefinition();

        definition.setName("PolicyHolders");
        definition.setTypeName("PolicyHolder_type");
        definition.setAdditional(" NESTED TABLE policies STORE AS POLICY_HOLDER_T"); //ugly code

        definition.addPrimaryKeyField("ssn", Long.class);

        return definition;
    }

    public static TypeDefinition buildPolicyHolderTypeDefinition() {
        TypeDefinition definition = new TypeDefinition();

        definition.setName("PolicyHolder_type");

        definition.addField("ssn", Long.class);
        definition.addField("firstName", String.class, 20);
        definition.addField("lastName", String.class, 20);
        definition.addField("sex", Character.class);
        definition.addField("birthDate", java.sql.Date.class);
        definition.addField("occupation", String.class, 20);
        definition.addField("address", "Address_type");
        definition.addField("childrenNames", "NameList_type");
        definition.addField("policies", "Policies_type");
        definition.addField("phones", "PhoneList_type");

        return definition;
    }

    public static TypeTableDefinition buildPolicyTableDefinition() {
        TypeTableDefinition definition = new TypeTableDefinition();

        definition.setName("Policies");
        definition.setTypeName("Policy_type");
        definition.setAdditional(" NESTED TABLE claims STORE AS CLAIMS_T"); //ugly code

        definition.addPrimaryKeyField("id", Long.class);
        definition.addForeignKeyConstraint("Policy_PolicyHolder_FKey", "policyHolderRef", "ssn", "PolicyHolders");

        return definition;
    }

    public static TypeDefinition buildPolicyTypeDefinition() {
        TypeDefinition definition = new TypeDefinition();

        definition.setName("Policy_type");

        definition.addField("id", Long.class);
        definition.addField("policyHolderRef", "Ref PolicyHolder_type");
        definition.addField("description", String.class, 100);
        definition.addField("type", Character.class);
        definition.addField("maxCoverage", Float.class, 12, 2);
        definition.addField("coverageRate", Float.class, 4, 2);
        definition.addField("dateOfConstruction", java.sql.Date.class);
        definition.addField("model", String.class, 20);
        definition.addField("color", String.class, 20);
        definition.addField("claims", "Claims_type");

        return definition;
    }

    /**
     * Return the vehicle claim descriptor.
     * This descriptor inherits from the claim descriptor.
     */
    public static ClassDescriptor buildVehicleClaimDescriptor() {
        ObjectRelationalDataTypeDescriptor descriptor = new ObjectRelationalDataTypeDescriptor();
        descriptor.setJavaClass(VehicleClaim.class);
        descriptor.getDescriptorInheritancePolicy().setParentClass(Claim.class);

        descriptor.addDirectMapping("part", "getPart", "setPart", "PART");

        return descriptor;
    }

    /**
     * Return the vehicle policy descriptor.
     * This descriptor inherits from the policy descriptor.
     */
    public static ClassDescriptor buildVehiclePolicyDescriptor() {
        ObjectRelationalDataTypeDescriptor descriptor = new ObjectRelationalDataTypeDescriptor();
        descriptor.setJavaClass(VehiclePolicy.class);
        descriptor.getDescriptorInheritancePolicy().setParentClass(Policy.class);

        descriptor.addDirectMapping("model", "getModel", "setModel", "MODEL");

        return descriptor;
    }

    /**
     * Return the bicycle policy descriptor.
     * This descriptor inherits from the vehicle policy descriptor.
     */
    public static ClassDescriptor buildBicyclePolicyDescriptor() {
        ObjectRelationalDataTypeDescriptor descriptor = new ObjectRelationalDataTypeDescriptor();
        descriptor.setJavaClass(BicyclePolicy.class);
        descriptor.getDescriptorInheritancePolicy().setParentClass(VehiclePolicy.class);

        descriptor.addDirectMapping("color", "getColor", "setColor", "COLOR");

        return descriptor;
    }

    /**
     * Return all of the descriptors required.
     */
    public static Vector getAllDescriptors() {
        Vector descriptors = new Vector();

        descriptors.addElement(buildClaimDescriptor());
        descriptors.addElement(buildHealthClaimDescriptor());
        descriptors.addElement(buildHouseClaimDescriptor());
        descriptors.addElement(buildVehicleClaimDescriptor());

        descriptors.addElement(buildPolicyDescriptor());
        descriptors.addElement(buildHealthPolicyDescriptor());
        descriptors.addElement(buildHousePolicyDescriptor());
        descriptors.addElement(buildVehiclePolicyDescriptor());
        descriptors.addElement(buildBicyclePolicyDescriptor());

        descriptors.addElement(buildPolicyHolderDescriptor());
        descriptors.addElement(buildAddressDescriptor());
        descriptors.addElement(buildPhoneDescriptor());

        return descriptors;
    }

    /**
     * Return all of the tables.
     */
    public Vector getTables() {
        Vector tables = new Vector();

        tables.addElement(buildClaimTableDefinition());
        tables.addElement(buildPolicyTableDefinition());
        tables.addElement(buildPolicyHolderTableDefinition());

        return tables;
    }

    /**
     * Return all of the types.
     */
    public Vector getTypes() {
        Vector types = new Vector();
        types.addElement(buildPhoneTypeDefinition());
        types.addElement(buildPhoneListTypeDefinition());
        types.addElement(buildNameListTypeDefinition());
        types.addElement(buildClaimTypeDefinition());
        types.addElement(buildClaimsTypeDefinition());
        types.addElement(buildPolicyTypeDefinition());
        types.addElement(buildPoliciesTypeDefinition());
        types.addElement(buildAddressTypeDefinition());
        types.addElement(buildPolicyHolderTypeDefinition());

        return types;
    }
}
