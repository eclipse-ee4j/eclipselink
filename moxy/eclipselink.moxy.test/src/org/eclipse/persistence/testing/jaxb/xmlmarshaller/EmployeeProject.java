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
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import java.net.URL;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.oxm.schema.XMLSchemaURLReference;
import org.eclipse.persistence.oxm.schema.XMLSchemaFileReference;
import org.eclipse.persistence.sessions.Project;

public class EmployeeProject extends Project {

    private NamespaceResolver namespaceResolver;

    public EmployeeProject() {
        namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        addDescriptor(getEmployeeDescriptor());
        addDescriptor(getAddressDescriptor());
        addDescriptor(getPOBoxAddressDescriptor());
        addDescriptor(getPhoneDescriptor());
        addDescriptor(getBadgeDescriptor());

        addDescriptor(getJobDescriptor());
        addDescriptor(getJob2Descriptor());
    }

    private XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Employee.class);
        descriptor.setDefaultRootElement("employee");

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setGetMethodName("getID");
        idMapping.setSetMethodName("setID");
        idMapping.setXPath("id/text()");
        descriptor.addMapping(idMapping);

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setGetMethodName("getName");
        nameMapping.setSetMethodName("setName");
        nameMapping.setXPath("name/text()");
        descriptor.addMapping(nameMapping);

        XMLCompositeObjectMapping homeAddressMapping = new XMLCompositeObjectMapping();
        homeAddressMapping.setReferenceClass(Address.class);
        homeAddressMapping.setAttributeName("home-address");
        homeAddressMapping.setGetMethodName("getHomeAddress");
        homeAddressMapping.setSetMethodName("setHomeAddress");
        homeAddressMapping.setXPath("home-address");
        descriptor.addMapping(homeAddressMapping);

        XMLCompositeObjectMapping workAddressMapping = new XMLCompositeObjectMapping();
        workAddressMapping.setReferenceClass(Address.class);
        workAddressMapping.setAttributeName("work-address");
        workAddressMapping.setGetMethodName("getWorkAddress");
        workAddressMapping.setSetMethodName("setWorkAddress");
        workAddressMapping.setXPath("work-address");
        descriptor.addMapping(workAddressMapping);

        XMLCompositeObjectMapping phoneMapping = new XMLCompositeObjectMapping();
        phoneMapping.setReferenceClass(Phone.class);
        phoneMapping.setAttributeName("phone");
        phoneMapping.setGetMethodName("getPhone");
        phoneMapping.setSetMethodName("setPhone");
        phoneMapping.setXPath("phone");
        descriptor.addMapping(phoneMapping);

        XMLCompositeObjectMapping badgeMapping = new XMLCompositeObjectMapping();
        badgeMapping.setReferenceClass(Badge.class);
        badgeMapping.setAttributeName("badge");
        badgeMapping.setGetMethodName("getBadge");
        badgeMapping.setSetMethodName("setBadge");
        badgeMapping.setXPath("badge-id");
        descriptor.addMapping(badgeMapping);

        URL schemaURL = ClassLoader.getSystemResource("org/eclipse/persistence/testing/oxm/jaxb/Employee.xsd");
        XMLSchemaURLReference schemaRef = new XMLSchemaURLReference(schemaURL);
        schemaRef.setType(XMLSchemaReference.ELEMENT);
        schemaRef.setSchemaContext("/employee");
        descriptor.setSchemaReference(schemaRef);

        return descriptor;
    }

    private XMLDescriptor getAddressDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Address.class);

        descriptor.setNamespaceResolver(namespaceResolver);
        XMLField classIndicatorField = new XMLField("@xsi:type");
        descriptor.getInheritancePolicy().setClassIndicatorField(classIndicatorField);
        descriptor.getInheritancePolicy().addClassIndicator(Address.class, "address-type");
        descriptor.getInheritancePolicy().addClassIndicator(POBoxAddress.class, "po-box-address-type");

        XMLDirectMapping streetMapping = new XMLDirectMapping();
        streetMapping.setAttributeName("street");
        streetMapping.setGetMethodName("getStreet");
        streetMapping.setSetMethodName("setStreet");
        streetMapping.setXPath("street/text()");
        descriptor.addMapping(streetMapping);

        XMLDirectMapping cityMapping = new XMLDirectMapping();
        cityMapping.setAttributeName("city");
        cityMapping.setGetMethodName("getCity");
        cityMapping.setSetMethodName("setCity");
        cityMapping.setXPath("city/text()");
        descriptor.addMapping(cityMapping);

        XMLDirectMapping stateMapping = new XMLDirectMapping();
        stateMapping.setAttributeName("state");
        stateMapping.setGetMethodName("getState");
        stateMapping.setSetMethodName("setState");
        stateMapping.setXPath("state/text()");
        descriptor.addMapping(stateMapping);

        XMLDirectMapping zipCodeMapping = new XMLDirectMapping();
        zipCodeMapping.setAttributeName("zip-code");
        zipCodeMapping.setGetMethodName("getZipCode");
        zipCodeMapping.setSetMethodName("setZipCode");
        zipCodeMapping.setXPath("zip-code/text()");
        descriptor.addMapping(zipCodeMapping);

        URL schemaURL = ClassLoader.getSystemResource("org/eclipse/persistence/testing/oxm/jaxb/Employee.xsd");
        XMLSchemaURLReference schemaRef = new XMLSchemaURLReference(schemaURL);
        schemaRef.setType(XMLSchemaReference.COMPLEX_TYPE);
        schemaRef.setSchemaContext("/address-type");
        descriptor.setSchemaReference(schemaRef);

        return descriptor;
    }

    private XMLDescriptor getPOBoxAddressDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(POBoxAddress.class);
        descriptor.setNamespaceResolver(namespaceResolver);
        descriptor.getInheritancePolicy().setParentClass(Address.class);

        XMLDirectMapping rrMapping = new XMLDirectMapping();
        rrMapping.setAttributeName("rrnumber");
        rrMapping.setGetMethodName("getRRNumber");
        rrMapping.setSetMethodName("setRRNumber");
        rrMapping.setXPath("rrnum/text()");
        descriptor.addMapping(rrMapping);

        XMLDirectMapping poboxMapping = new XMLDirectMapping();
        poboxMapping.setAttributeName("pobox");
        poboxMapping.setGetMethodName("getPOBox");
        poboxMapping.setSetMethodName("setPOBox");
        poboxMapping.setXPath("po-box/text()");
        descriptor.addMapping(poboxMapping);

        URL schemaURL = ClassLoader.getSystemResource("org/eclipse/persistence/testing/oxm/jaxb/Employee.xsd");
        XMLSchemaURLReference schemaRef = new XMLSchemaURLReference(schemaURL);
        schemaRef.setType(XMLSchemaReference.COMPLEX_TYPE);
        schemaRef.setSchemaContext("/po-box-address-type");
        descriptor.setSchemaReference(schemaRef);

        return descriptor;
    }

    private XMLDescriptor getPhoneDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Phone.class);

        XMLDirectMapping numberMapping = new XMLDirectMapping();
        numberMapping.setAttributeName("number");
        numberMapping.setGetMethodName("getNumber");
        numberMapping.setSetMethodName("setNumber");
        numberMapping.setXPath("text()");
        descriptor.addMapping(numberMapping);

        URL schemaURL = ClassLoader.getSystemResource("org/eclipse/persistence/testing/oxm/jaxb/Employee.xsd");
        XMLSchemaURLReference schemaRef = new XMLSchemaURLReference(schemaURL);
        schemaRef.setType(XMLSchemaReference.SIMPLE_TYPE);
        schemaRef.setSchemaContext("/phone-type");
        descriptor.setSchemaReference(schemaRef);

        return descriptor;
    }

    private XMLDescriptor getBadgeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setDefaultRootElement("badge-id");
        descriptor.setJavaClass(Badge.class);

        XMLDirectMapping numberMapping = new XMLDirectMapping();
        numberMapping.setAttributeName("number");
        numberMapping.setGetMethodName("getNumber");
        numberMapping.setSetMethodName("setNumber");
        numberMapping.setXPath("text()");
        descriptor.addMapping(numberMapping);

        URL schemaURL = ClassLoader.getSystemResource("org/eclipse/persistence/testing/oxm/jaxb/Employee.xsd");
        XMLSchemaURLReference schemaRef = new XMLSchemaURLReference(schemaURL);
        schemaRef.setType(XMLSchemaReference.ELEMENT);
        schemaRef.setSchemaContext("/employee/badge-id");
        descriptor.setSchemaReference(schemaRef);

        return descriptor;
    }

    private XMLDescriptor getJobDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Job.class);
        descriptor.setDefaultRootElement("job");

        XMLDirectMapping titleMapping = new XMLDirectMapping();
        titleMapping.setAttributeName("title");
        titleMapping.setGetMethodName("getTitle");
        titleMapping.setSetMethodName("setTitle");
        titleMapping.setXPath("title/text()");
        descriptor.addMapping(titleMapping);

        URL schemaURL = ClassLoader.getSystemResource("org/eclipse/persistence/testing/oxm/jaxb/Employee.xsd");
        XMLSchemaURLReference schemaRef = new XMLSchemaURLReference(schemaURL);
        schemaRef.setType(XMLSchemaReference.ELEMENT);
        schemaRef.setSchemaContext("/employee/job");
        descriptor.setSchemaReference(schemaRef);

        return descriptor;
    }

    private XMLDescriptor getJob2Descriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Job2.class);
        descriptor.setDefaultRootElement("job2");

        XMLDirectMapping titleMapping = new XMLDirectMapping();
        titleMapping.setAttributeName("title");
        titleMapping.setGetMethodName("getTitle");
        titleMapping.setSetMethodName("setTitle");
        titleMapping.setXPath("title/text()");
        descriptor.addMapping(titleMapping);

        URL schemaURL = ClassLoader.getSystemResource("org/eclipse/persistence/testing/oxm/jaxb/Employee.xsd");
        XMLSchemaURLReference schemaRef = new XMLSchemaURLReference(schemaURL);
        schemaRef.setType(XMLSchemaReference.ELEMENT);
        schemaRef.setSchemaContext("/employee/job2");
        descriptor.setSchemaReference(schemaRef);

        return descriptor;
    }

}
