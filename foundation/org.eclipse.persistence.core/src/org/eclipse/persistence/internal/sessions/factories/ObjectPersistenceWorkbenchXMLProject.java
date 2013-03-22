/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.sessions.factories;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

/**
 * INTERNAL:
 * Amend the EclipseLink project and descriptor information to write out an EclipseLink project
 * to an XML file, replacing actual .class references with String equivalents
 */
public class ObjectPersistenceWorkbenchXMLProject extends EclipseLinkObjectPersistenceRuntimeXMLProject {

    /**
     * INTERNAL:
     * Return a new descriptor project.
     */
    public ObjectPersistenceWorkbenchXMLProject() {
        super();
    }

    protected ClassDescriptor buildAggregateMappingDescriptor() {
        ClassDescriptor descriptor = super.buildAggregateMappingDescriptor();

        XMLDirectMapping referenceClassMapping = 
        	(XMLDirectMapping)descriptor.getMappingForAttributeName("referenceClass");
        referenceClassMapping.setGetMethodName("getReferenceClassName");
        referenceClassMapping.setSetMethodName("setReferenceClassName");

        return descriptor;
    }

    protected ClassDescriptor buildForeignReferenceMappingDescriptor() {
        ClassDescriptor descriptor = super.buildForeignReferenceMappingDescriptor();

        XMLDirectMapping referenceClassMapping = 
        	(XMLDirectMapping)descriptor.getMappingForAttributeName("referenceClass");
        referenceClassMapping.setGetMethodName("getReferenceClassName");
        referenceClassMapping.setSetMethodName("setReferenceClassName");
        
        return descriptor;
    }

    protected ClassDescriptor buildInheritancePolicyDescriptor() {
        ClassDescriptor descriptor = super.buildInheritancePolicyDescriptor();

        XMLDirectMapping referenceClassMapping = 
        	(XMLDirectMapping)descriptor.getMappingForAttributeName("parentClass");
        referenceClassMapping.setGetMethodName("getParentClassName");
        referenceClassMapping.setSetMethodName("setParentClassName");
        
        return descriptor;
    }

    protected ClassDescriptor buildInstantiationPolicyDescriptor() {
        ClassDescriptor descriptor = super.buildInstantiationPolicyDescriptor();

        XMLDirectMapping referenceClassMapping = 
        	(XMLDirectMapping)descriptor.getMappingForAttributeName("factoryClass");
        referenceClassMapping.setGetMethodName("getFactoryClassName");
        referenceClassMapping.setSetMethodName("setFactoryClassName");

        return descriptor;
    }

    protected ClassDescriptor buildInterfacePolicyDescriptor() {
        ClassDescriptor descriptor = super.buildInterfacePolicyDescriptor();

        XMLCompositeDirectCollectionMapping referenceClassMapping = 
        	(XMLCompositeDirectCollectionMapping)descriptor.getMappingForAttributeName("parentInterfaces");
        referenceClassMapping.setGetMethodName("getParentInterfaceNames");
        referenceClassMapping.setSetMethodName("setParentInterfaceNames");

        XMLDirectMapping implementorDescriptorMapping = 
        	(XMLDirectMapping)descriptor.getMappingForAttributeName("implementorDescriptor");
        implementorDescriptorMapping.setGetMethodName("getImplementorDescriptorClassName");
        implementorDescriptorMapping.setSetMethodName("setImplementorDescriptorClassName");
                
        return descriptor;
    }

    protected ClassDescriptor buildInterfaceContainerPolicyDescriptor() {
        ClassDescriptor descriptor = super.buildInterfaceContainerPolicyDescriptor();

        XMLDirectMapping referenceClassMapping = 
        	(XMLDirectMapping)descriptor.getMappingForAttributeName("containerClass");
        referenceClassMapping.setGetMethodName("getContainerClassName");
        referenceClassMapping.setSetMethodName("setContainerClassName");

        return descriptor;
    }

    protected ClassDescriptor buildSortedCollectionContainerPolicyDescriptor() {
        ClassDescriptor descriptor = super.buildSortedCollectionContainerPolicyDescriptor();

        XMLDirectMapping referenceClassMapping = 
        	(XMLDirectMapping)descriptor.getMappingForAttributeName("comparatorClass");
        referenceClassMapping.setGetMethodName("getComparatorClassName");
        referenceClassMapping.setSetMethodName("setComparatorClassName");

        return descriptor;
    }
    
    
    protected ClassDescriptor buildMethodBaseQueryRedirectorDescriptor() {
        ClassDescriptor descriptor = super.buildMethodBaseQueryRedirectorDescriptor();

        XMLDirectMapping referenceClassMapping = 
        	(XMLDirectMapping)descriptor.getMappingForAttributeName("methodClass");
        referenceClassMapping.setGetMethodName("getMethodClassName");
        referenceClassMapping.setSetMethodName("setMethodClassName");

        return descriptor;
    }

    protected ClassDescriptor buildObjectLevelReadQueryDescriptor() {
        ClassDescriptor descriptor = super.buildObjectLevelReadQueryDescriptor();

        XMLDirectMapping referenceClassMapping = 
        	(XMLDirectMapping)descriptor.getMappingForAttributeName("referenceClass");
        referenceClassMapping.setGetMethodName("getReferenceClassName");
        referenceClassMapping.setSetMethodName("setReferenceClassName");
        
        return descriptor;
    }

    protected ClassDescriptor buildQueryArgumentDescriptor() {
        ClassDescriptor descriptor = super.buildQueryArgumentDescriptor();

        XMLDirectMapping referenceClassMapping = 
        	(XMLDirectMapping)descriptor.getMappingForAttributeName("type");     
        referenceClassMapping.setGetMethodName("getTypeName");
        referenceClassMapping.setSetMethodName("setTypeName");

        return descriptor;
    }

    protected ClassDescriptor buildRelationshipQueryKeyDescriptor() {
        ClassDescriptor descriptor = super.buildRelationshipQueryKeyDescriptor();

        XMLDirectMapping referenceClassMapping = 
        	(XMLDirectMapping)descriptor.getMappingForAttributeName("referenceClass");
        referenceClassMapping.setGetMethodName("getReferenceClassName");
        referenceClassMapping.setSetMethodName("setReferenceClassName");

        return descriptor;
    }

    protected ClassDescriptor buildReturningFieldInfoDescriptor() {
        ClassDescriptor descriptor = super.buildReturningFieldInfoDescriptor();

        XMLDirectMapping referenceClassMapping = 
        	(XMLDirectMapping)descriptor.getMappingForAttributeName("referenceClass");
        referenceClassMapping.setGetMethodName("getReferenceClassName");
        referenceClassMapping.setSetMethodName("setReferenceClassName");

        return descriptor;
    }

    protected ClassDescriptor buildClassDescriptorDescriptor() {
        ClassDescriptor descriptor = super.buildClassDescriptorDescriptor();

        XMLDirectMapping referenceClassMapping = 
        	(XMLDirectMapping)descriptor.getMappingForAttributeName("javaClass");
        referenceClassMapping.setGetMethodName("getJavaClassName");
        referenceClassMapping.setSetMethodName("setJavaClassName");
        
        XMLDirectMapping amendmentClassMapping = 
        	(XMLDirectMapping)descriptor.getMappingForAttributeName("amendmentClass");
        amendmentClassMapping.setGetMethodName("getAmendmentClassName");
        amendmentClassMapping.setSetMethodName("setAmendmentClassName");
        
        return descriptor;
    }

    protected ClassDescriptor buildTypedAssociationDescriptor() {
        ClassDescriptor descriptor = super.buildTypedAssociationDescriptor();

        XMLDirectMapping keyMapping = (XMLDirectMapping)descriptor.getMappingForAttributeName("key");
        keyMapping.setAttributeClassification(null);
        keyMapping.setGetMethodName("getKey");
        keyMapping.setSetMethodName("setKey");

        return descriptor;
    }

    protected ClassDescriptor buildTypeConversionConverterDescriptor() {
        ClassDescriptor descriptor = super.buildTypeConversionConverterDescriptor();

        XMLDirectMapping objectClassMapping = 
        	(XMLDirectMapping)descriptor.getMappingForAttributeName("objectClass");
        objectClassMapping.setGetMethodName("getObjectClassName");
        objectClassMapping.setSetMethodName("setObjectClassName");
        
        XMLDirectMapping dataClassNameMapping = 
        	(XMLDirectMapping)descriptor.getMappingForAttributeName("dataClass");
        dataClassNameMapping.setGetMethodName("getDataClassName");
        dataClassNameMapping.setSetMethodName("setDataClassName");

        return descriptor;
    }    

    protected ClassDescriptor buildAbstractDirectMappingDescriptor() {
        ClassDescriptor descriptor = super.buildAbstractDirectMappingDescriptor();

        XMLDirectMapping attributeClassificationNameMapping = 
            (XMLDirectMapping)descriptor.getMappingForAttributeName("attributeClassification");
        attributeClassificationNameMapping.setGetMethodName("getAttributeClassificationName");
        attributeClassificationNameMapping.setSetMethodName("setAttributeClassificationName");

        return descriptor;
    }

    protected ClassDescriptor buildAbstractTransformationMappingDescriptor() {
        ClassDescriptor descriptor = super.buildAbstractTransformationMappingDescriptor();

        XMLDirectMapping objectClassMapping = 
        	(XMLDirectMapping)descriptor.getMappingForAttributeName("attributeTransformerClass");
        objectClassMapping.setGetMethodName("getAttributeTransformerClassName");
        objectClassMapping.setSetMethodName("setAttributeTransformerClassName");

        return descriptor;
    }
    
    protected ClassDescriptor buildTransformerBasedFieldTransformationDescriptor() {
        ClassDescriptor descriptor = super.buildTransformerBasedFieldTransformationDescriptor();

        XMLDirectMapping objectClassMapping = 
        	(XMLDirectMapping)descriptor.getMappingForAttributeName("transformerClass");
        objectClassMapping.setGetMethodName("getTransformerClassName");
        objectClassMapping.setSetMethodName("setTransformerClassName");
        
        return descriptor;
    }

    @Override
    protected ClassDescriptor buildPLSQLCollectionDescriptor() {
        
        ClassDescriptor descriptor = super.buildPLSQLCollectionDescriptor();
        XMLDirectMapping javaTypeMapping = 
            (XMLDirectMapping)descriptor.getMappingForAttributeName("javaType");
        javaTypeMapping.setGetMethodName("getJavaTypeName");
        javaTypeMapping.setSetMethodName("setJavaTypeName");
        
        return descriptor;
    }

    @Override
    protected ClassDescriptor buildPLSQLrecordDescriptor() {
        
        ClassDescriptor descriptor = super.buildPLSQLrecordDescriptor();
        XMLDirectMapping javaTypeMapping = 
            (XMLDirectMapping)descriptor.getMappingForAttributeName("javaType");
        javaTypeMapping.setGetMethodName("getJavaTypeName");
        javaTypeMapping.setSetMethodName("setJavaTypeName");
        
        return descriptor;
    }

    @Override
    protected ClassDescriptor buildStoredProcedureArgumentDescriptor() {
        
        ClassDescriptor descriptor = super.buildStoredProcedureArgumentDescriptor();

        XMLDirectMapping argumentTypeMapping = 
            (XMLDirectMapping)descriptor.getMappingForAttributeName("argumentType");
        argumentTypeMapping.setGetMethodName("getArgumentTypeName");
        argumentTypeMapping.setSetMethodName("setArgumentTypeName");
        
        return descriptor;
    }
}
