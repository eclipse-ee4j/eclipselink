/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.classloader;

import java.lang.reflect.Method;

import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.OXTestCase;

/**
 * Tests class loader usage in the following mappings:
 *  - XMLAnyAttributeMapping
 *  - XMLAnyCollectionMapping
 *  - XMLAnyObjectMapping
 *  - XMLBinaryDataCollectionMapping
 *  - XMLBinaryDataMapping
 *  - XMLChoiceCollectionMapping
 *  - XMLChoiceObjectMapping
 *  - XMLCollectionReferenceMapping
 *  - XMLCompositeCollectionMapping
 *  - XMLCompositeDirectCollectionMapping
 *  - XMLCompositeObjectMapping
 *  - XMLObjectReferenceMapping
 */
public class MappingTestProject extends Project {
    private Class rootObjectClass;
    private Class testObjectClass;
    private Class addressClass;
    private Class anyObjectClass;
    private Class listContainerPolicyClass;
    private Class mapContainerPolicyClass;
    private OXTestCase.Platform platform;
    private OXTestCase.Metadata metadata;

    public MappingTestProject(ClassLoader loader, OXTestCase.Platform platform, OXTestCase.Metadata metadata) {
        this.platform = platform;
        this.metadata = metadata;
        // load classes not on class path
        try {
            rootObjectClass = loader.loadClass("org.eclipse.persistence.testing.oxm.classloader.RootObject");
            testObjectClass = loader.loadClass("org.eclipse.persistence.testing.oxm.classloader.MappingTestObject");
            addressClass = loader.loadClass("org.eclipse.persistence.testing.oxm.classloader.Address");
            anyObjectClass = loader.loadClass("org.eclipse.persistence.testing.oxm.classloader.AnyObject");
            listContainerPolicyClass = loader.loadClass("org.eclipse.persistence.testing.oxm.classloader.CustomListContainerPolicy");
            mapContainerPolicyClass = loader.loadClass("org.eclipse.persistence.testing.oxm.classloader.CustomMapContainerPolicy");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // build descriptors
        this.addDescriptor(buildRootObjectDescriptor());
        this.addDescriptor(buildMappingTestObjectDescriptor());
        this.addDescriptor(buildAnyObjectDescriptor());
        this.addDescriptor(buildAddressDescriptor());
    }

    /**
     * The root descriptor contains a single mapping test object and a 
     * list of address objects 
     */
    public ClassDescriptor buildRootObjectDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(rootObjectClass);
        descriptor.setDefaultRootElement("root-object");
        // create test object mapping
        XMLCompositeObjectMapping testObjectMapping = new XMLCompositeObjectMapping();
        testObjectMapping.setAttributeName("testObject");
        testObjectMapping.setXPath("mapping-test-object");
        testObjectMapping.setReferenceClassName(testObjectClass.getName());
        testObjectMapping.setGetMethodName("getTestObject");
        testObjectMapping.setSetMethodName("setTestObject");
        descriptor.addMapping(testObjectMapping);
        // create address mapping
        XMLCompositeCollectionMapping addMapping = new XMLCompositeCollectionMapping();
        addMapping.setAttributeName("addresses");
        addMapping.setXPath("addresses/address");
        addMapping.setReferenceClassName(addressClass.getName());
        addMapping.setGetMethodName("getAddresses");
        addMapping.setSetMethodName("setAddresses");
        ContainerPolicy listPolicy;
        try {
            listPolicy = (ContainerPolicy) listContainerPolicyClass.newInstance();
            Method meth = listContainerPolicyClass.getDeclaredMethod("setContainerClassName", new Class[] { String.class });
            meth.invoke(listPolicy, new Object[] { "java.util.ArrayList" });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        addMapping.setContainerPolicy(listPolicy);
        descriptor.addMapping(addMapping);
        return descriptor;
    }

    public ClassDescriptor buildMappingTestObjectDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(testObjectClass);
        descriptor.setDefaultRootElement("mapping-test-object");
        // any attribute mapping
        XMLAnyAttributeMapping aamapping = new XMLAnyAttributeMapping();
        aamapping.setAttributeName("anyAttribute");
        aamapping.setGetMethodName("getAnyAttribute");
        aamapping.setSetMethodName("setAnyAttribute");
        ContainerPolicy mapPolicy;
        try {
            mapPolicy = (ContainerPolicy) mapContainerPolicyClass.newInstance();
            Method meth = mapContainerPolicyClass.getDeclaredMethod("setContainerClassName", new Class[] { String.class });
            meth.invoke(mapPolicy, new Object[] { "java.util.HashMap" });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        aamapping.setContainerPolicy(mapPolicy);
        descriptor.addMapping(aamapping);
        // any object mapping
        XMLAnyObjectMapping aomapping = new XMLAnyObjectMapping();
        aomapping.setAttributeName("anyObject");
        aomapping.setGetMethodName("getAnyObject");
        aomapping.setSetMethodName("setAnyObject");
        descriptor.addMapping(aomapping);
        // any collection mapping
        XMLAnyCollectionMapping acmapping = new XMLAnyCollectionMapping();
        acmapping.setXPath("any-collection");
        acmapping.setAttributeName("anyCollection");
        acmapping.setGetMethodName("getAnyCollection");
        acmapping.setSetMethodName("setAnyCollection");
        descriptor.addMapping(acmapping);

        // setup binary data and choice mapping, but only add the mappings in certain cases
        // binary data mapping
        XMLBinaryDataMapping photoMapping = new XMLBinaryDataMapping();
        photoMapping.setAttributeName("photo");
        XMLField field = new XMLField("photo");
        field.setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
        photoMapping.setField(field);
        photoMapping.setShouldInlineBinaryData(false);
        photoMapping.setSwaRef(false);
        photoMapping.setMimeType("image");
        // binary data collection mapping
        XMLBinaryDataCollectionMapping photosMapping = new XMLBinaryDataCollectionMapping();
        photosMapping.setAttributeName("photos");
        XMLField xfield = new XMLField("photos/photo");
        xfield.setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
        photosMapping.setField(xfield);
        photosMapping.setShouldInlineBinaryData(false);
        photosMapping.setSwaRef(false);
        photosMapping.setMimeType("image");
        photosMapping.setCollectionContentType(ClassConstants.APBYTE);
        // choice mapping
        XMLChoiceObjectMapping choiceMapping = new XMLChoiceObjectMapping();
        choiceMapping.setAttributeName("choice");
        choiceMapping.setGetMethodName("getChoice");
        choiceMapping.setSetMethodName("setChoice");
        choiceMapping.addChoiceElement("string-choice/text()", String.class);
        choiceMapping.addChoiceElement("int-choice/text()", Integer.class);
        // choice collection mapping
        XMLChoiceCollectionMapping choiceColMapping = new XMLChoiceCollectionMapping();
        choiceColMapping.setAttributeName("choices");
        choiceColMapping.setGetMethodName("getChoices");
        choiceColMapping.setSetMethodName("setChoices");
        choiceColMapping.addChoiceElement("street-choice/text()", String.class);
        choiceColMapping.addChoiceElement("address-choice", addressClass);
        choiceColMapping.addChoiceElement("integer-choice/text()", Integer.class);

        // 1- binary and choice are supported with SAX and Java project
        // 2- neither is supported with deployment XML
        // 3- binary is supported with DOM 
        if (metadata == OXTestCase.Metadata.JAVA) {
            descriptor.addMapping(photoMapping);
            descriptor.addMapping(photosMapping);
        }
        if (platform == OXTestCase.Platform.SAX) {
            descriptor.addMapping(choiceMapping);
            descriptor.addMapping(choiceColMapping);
        } 
        
        // object reference mapping
        XMLObjectReferenceMapping orMapping = new XMLObjectReferenceMapping();
        orMapping.setAttributeName("address");
        orMapping.setReferenceClassName(addressClass.getName());
        orMapping.addSourceToTargetKeyFieldAssociation("billing-address-ref/@address-id", "@aid");
        descriptor.addMapping(orMapping);
        // collection reference mapping
        XMLCollectionReferenceMapping addressesMapping = new XMLCollectionReferenceMapping();
        ContainerPolicy listPolicy;
        try {
            listPolicy = (ContainerPolicy) listContainerPolicyClass.newInstance();
            Method meth = listContainerPolicyClass.getDeclaredMethod("setContainerClassName", new Class[] { String.class });
            meth.invoke(listPolicy, new Object[] { "java.util.ArrayList" });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        addressesMapping.setContainerPolicy(listPolicy);
        addressesMapping.setAttributeName("addresses");
        addressesMapping.setReferenceClassName(addressClass.getName());
        addressesMapping.addSourceToTargetKeyFieldAssociation("shipping-address-ref/@address-id", "@aid");
        descriptor.addMapping(addressesMapping);
        // direct collection mapping
        XMLCompositeDirectCollectionMapping responsibilitiesMapping = new XMLCompositeDirectCollectionMapping();
        responsibilitiesMapping.setAttributeName("responsibilities");
        ContainerPolicy listPolicy1;
        try {
            listPolicy1 = (ContainerPolicy) listContainerPolicyClass.newInstance();
            Method meth = listContainerPolicyClass.getDeclaredMethod("setContainerClassName", new Class[] { String.class });
            meth.invoke(listPolicy1, new Object[] { "java.util.ArrayList" });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        responsibilitiesMapping.setContainerPolicy(listPolicy1);
        responsibilitiesMapping.setXPath("responsibilities/list/@responsibility");
        descriptor.addMapping(responsibilitiesMapping);
        return descriptor;
    }

    public ClassDescriptor buildAnyObjectDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(anyObjectClass);
        descriptor.setDefaultRootElement("any-object");
        // id mapping
        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setXPath("@id");
        idMapping.setAttributeName("id");
        idMapping.setGetMethodName("getId");
        idMapping.setSetMethodName("setId");
        descriptor.addMapping(idMapping);
        // name mapping
        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setXPath("name");
        nameMapping.setAttributeName("name");
        nameMapping.setGetMethodName("getName");
        nameMapping.setSetMethodName("setName");
        descriptor.addMapping(nameMapping);
        return descriptor;
    }

    public ClassDescriptor buildAddressDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(addressClass);
        // set primary key for reference mapping tests
        descriptor.addPrimaryKeyFieldName("@aid");
        // id mapping
        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("@aid");
        descriptor.addMapping(idMapping);
        // street mapping
        XMLDirectMapping streetMapping = new XMLDirectMapping();
        streetMapping.setAttributeName("street");
        streetMapping.setXPath("street/text()");
        streetMapping.setGetMethodName("getStreet");
        streetMapping.setSetMethodName("setStreet");
        descriptor.addMapping(streetMapping);
        // city mapping
        XMLDirectMapping cityMapping = new XMLDirectMapping();
        cityMapping.setAttributeName("city");
        cityMapping.setXPath("city/text()");
        cityMapping.setGetMethodName("getCity");
        cityMapping.setSetMethodName("setCity");
        descriptor.addMapping(cityMapping);
        return descriptor;
    }
}
