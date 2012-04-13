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
* dmccann - Mar 2/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.schemamodelgenerator;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.mappings.converters.EnumTypeConverter;
import org.eclipse.persistence.mappings.converters.TypeConversionConverter;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.FixedMimeTypePolicy;
import org.eclipse.persistence.oxm.mappings.XMLAnyAttributeMapping;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLAnyObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.oxm.mappings.XMLChoiceCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLChoiceObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLCollectionReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLObjectReferenceMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.oxm.schema.XMLSchemaURLReference;
import org.eclipse.persistence.sessions.Project;

public class TestProject extends Project {
    NamespaceResolver nsr;
    boolean setSchemaContext, setDefaultRootElement;
    
    public TestProject() {
        nsr = new NamespaceResolver();
        setSchemaContext = true;
        setDefaultRootElement = true;
        addDescriptors();
    }
    
    public TestProject(boolean setSchemaContext, boolean setDefaultRootElement) {
        nsr = new NamespaceResolver();
        this.setSchemaContext = setSchemaContext;
        this.setDefaultRootElement = setDefaultRootElement;
        addDescriptors();
    }

    public TestProject(boolean setSchemaContext, boolean setDefaultRootElement, String defaultUri) {
        nsr = new NamespaceResolver();
        nsr.setDefaultNamespaceURI(defaultUri);
        this.setSchemaContext = setSchemaContext;
        this.setDefaultRootElement = setDefaultRootElement;
        addDescriptors();
    }

    public TestProject(NamespaceResolver resolver, boolean setSchemaContext, boolean setDefaultRootElement) {
        nsr = resolver;
        this.setSchemaContext = setSchemaContext;
        this.setDefaultRootElement = setDefaultRootElement;
        addDescriptors();
    }
    
    private void addDescriptors() {
        addDescriptor(getEmployeeDescriptor());
        addDescriptor(getAddressDescriptor());
        addDescriptor(getPhoneNumberDescriptor());
    }
    
    private XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Employee.class);
        descriptor.setAlias("Employee");
        descriptor.addPrimaryKeyFieldName("name/text()");
        descriptor.setNamespaceResolver(nsr);
        if (setSchemaContext) {
            XMLSchemaReference sRef = new XMLSchemaURLReference();
            sRef.setSchemaContext("/employee-type");
            sRef.setType(XMLSchemaReference.COMPLEX_TYPE);
            descriptor.setSchemaReference(sRef);
        }
        if (setDefaultRootElement) {
            descriptor.setDefaultRootElement("employee");
        }
        // create name mapping
        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setXPath("name/text()");
        descriptor.addMapping(nameMapping);
        // create address mapping
        XMLCompositeObjectMapping addressMapping = new XMLCompositeObjectMapping();
        addressMapping.setAttributeName("address");
        addressMapping.setXPath("address");
        addressMapping.setReferenceClass(Address.class);
        descriptor.addMapping(addressMapping);
        // create phoneNumbers mapping
        XMLCompositeCollectionMapping phonesMapping = new XMLCompositeCollectionMapping();
        phonesMapping.setAttributeName("phoneNumbers");
        phonesMapping.useCollectionClass(ArrayList.class);
        phonesMapping.setXPath("phone-numbers");
        phonesMapping.setReferenceClass(PhoneNumber.class);
        descriptor.addMapping(phonesMapping);
        // create projectIDs mapping
        XMLCompositeDirectCollectionMapping projectIdsMapping = new XMLCompositeDirectCollectionMapping();
        projectIdsMapping.setAttributeName("projectIDs");
        projectIdsMapping.useCollectionClass(ArrayList.class);
        projectIdsMapping.setXPath("project-id");
        TypeConversionConverter tcc = new TypeConversionConverter(projectIdsMapping);
        tcc.setObjectClass(BigDecimal.class);
        projectIdsMapping.setValueConverter(tcc);
        descriptor.addMapping(projectIdsMapping);
        // create stuff mapping
        XMLAnyCollectionMapping acMapping = new XMLAnyCollectionMapping();
        acMapping.setAttributeName("stuff");
        descriptor.addMapping(acMapping);
        
        // Enable Choice testing when Bug 269880 has been fixed
        // create choice mapping - choice
        XMLChoiceObjectMapping choiceMapping = new XMLChoiceObjectMapping();
        choiceMapping.setAttributeName("choice");
        choiceMapping.addChoiceElement("nickname/text()", String.class);
        choiceMapping.addChoiceElement("secondAddress", Address.class);
        choiceMapping.addChoiceElement("age/text()", Integer.class);
        //descriptor.addMapping(choiceMapping);
        
        // Enable ChoiceCollection testing when Bug 269880 has been fixed
        // create choices mapping
        XMLChoiceCollectionMapping choiceCMapping = new XMLChoiceCollectionMapping();
        choiceCMapping.setAttributeName("choices");
        choiceCMapping.addChoiceElement("badgeId/text()", Integer.class);
        choiceCMapping.addChoiceElement("alternateAddress", Address.class);
        choiceCMapping.addChoiceElement("codename/text()", String.class);
        //descriptor.addMapping(choiceCMapping);
        
        // create billingAddress mapping
        XMLObjectReferenceMapping orMapping = new XMLObjectReferenceMapping();
        orMapping.setAttributeName("billingAddress");
        orMapping.setReferenceClass(Address.class);
        orMapping.addSourceToTargetKeyFieldAssociation("@bill-address-id", "@aid");
        orMapping.addSourceToTargetKeyFieldAssociation("bill-address-city/text()", "city/text()");
        descriptor.addMapping(orMapping);
        
        // create data mapping
        XMLBinaryDataMapping dataMapping = new XMLBinaryDataMapping();
        dataMapping.setAttributeName("data");
        XMLField field = new XMLField("data");
        field.setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
        dataMapping.setField(field);
        dataMapping.setShouldInlineBinaryData(false);
        dataMapping.setSwaRef(true);
        dataMapping.setMimeType("application/binary");
        descriptor.addMapping(dataMapping);
      
        // create bytes mapping
        XMLBinaryDataCollectionMapping bytesMapping = new XMLBinaryDataCollectionMapping();
        bytesMapping.setAttributeName("bytes");
        XMLField theField = new XMLField("bytes");
        theField.setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
        bytesMapping.setField(theField);
        bytesMapping.setShouldInlineBinaryData(false);
        bytesMapping.setSwaRef(false);
        bytesMapping.setMimeType("text/plain");
        descriptor.addMapping(bytesMapping);

        // create URL mapping
        XMLDirectMapping urlMapping = new XMLDirectMapping();
        urlMapping.setAttributeName("aUrl");
        urlMapping.setXPath("aUrl/text()");
        descriptor.addMapping(urlMapping);

        return descriptor;
    }

    private XMLDescriptor getAddressDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Address.class);
        descriptor.addPrimaryKeyFieldName("@aid");
        descriptor.addPrimaryKeyFieldName("city/text()");
        descriptor.setAlias("Address");
        descriptor.setNamespaceResolver(nsr);
        if (setSchemaContext) {
            XMLSchemaReference sRef = new XMLSchemaURLReference();
            sRef.setSchemaContext("/address-type");
            sRef.setType(XMLSchemaReference.COMPLEX_TYPE);
            descriptor.setSchemaReference(sRef);
        }
        if (setDefaultRootElement) {
            descriptor.setDefaultRootElement("address");
        }
        // create id mapping
        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("@aid");
        idMapping.setAttributeClassification(Integer.class);
        descriptor.addMapping(idMapping);
        // create street mapping
        XMLDirectMapping streetMapping = new XMLDirectMapping();
        streetMapping.setAttributeName("street");
        streetMapping.setXPath("street/text()");
        descriptor.addMapping(streetMapping);
        // create city mapping
        XMLDirectMapping cityMapping = new XMLDirectMapping();
        cityMapping.setAttributeName("city");
        cityMapping.setXPath("city/text()");
        descriptor.addMapping(cityMapping);
        // create country mapping
        XMLDirectMapping countryMapping = new XMLDirectMapping();
        countryMapping.setAttributeName("country");
        countryMapping.setXPath("country/text()");
        descriptor.addMapping(countryMapping);
        // create postalCode mapping
        XMLDirectMapping postalMapping = new XMLDirectMapping();
        postalMapping.setAttributeName("postalCode");
        postalMapping.setXPath("@postal-code");
        descriptor.addMapping(postalMapping);
        // create thingy mapping
        XMLAnyObjectMapping anyMapping = new XMLAnyObjectMapping();
        anyMapping.setAttributeName("thingy");
        descriptor.addMapping(anyMapping);
        // create occupants mapping
        XMLCollectionReferenceMapping occupantsMapping = new XMLCollectionReferenceMapping();
        occupantsMapping.useCollectionClass(ArrayList.class);
        occupantsMapping.setAttributeName("occupants");
        occupantsMapping.setReferenceClass(Employee.class);
        occupantsMapping.addSourceToTargetKeyFieldAssociation("occupant/text()", "name/text()");
        descriptor.addMapping(occupantsMapping);
        return descriptor;
    }

    private XMLDescriptor getPhoneNumberDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(PhoneNumber.class);
        descriptor.setAlias("PhoneNumber");
        descriptor.setNamespaceResolver(nsr);
        if (setSchemaContext) {
            XMLSchemaReference sRef = new XMLSchemaURLReference();
            sRef.setSchemaContext("/phone-number-type");
            sRef.setType(XMLSchemaReference.COMPLEX_TYPE);
            descriptor.setSchemaReference(sRef);
        }
        if (setDefaultRootElement) {
            descriptor.setDefaultRootElement("phone-number");
        }
        // create number mapping
        XMLDirectMapping numberMapping = new XMLDirectMapping();
        numberMapping.setAttributeName("number");
        numberMapping.setXPath("number/text()");
        descriptor.addMapping(numberMapping);
        // create thing mapping
        XMLAnyAttributeMapping anyMapping = new XMLAnyAttributeMapping();
        anyMapping.setAttributeName("thing");
        descriptor.addMapping(anyMapping);
        // Uncomment the following when enumeration support is added
        /*
        // create type mapping
        XMLDirectMapping typeMapping = new XMLDirectMapping();
        typeMapping.setAttributeName("type");
        typeMapping.setXPath("@type");
        EnumTypeConverter converter = new EnumTypeConverter(typeMapping, PhoneNumberType.class, false);
        typeMapping.setConverter(converter);
        descriptor.addMapping(typeMapping);
        */
        return descriptor;
    }
}
