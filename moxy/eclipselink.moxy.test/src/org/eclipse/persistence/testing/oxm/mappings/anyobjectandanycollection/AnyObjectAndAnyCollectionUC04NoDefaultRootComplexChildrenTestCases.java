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
package org.eclipse.persistence.testing.oxm.mappings.anyobjectandanycollection;

import java.util.Vector;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLAnyObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.anyobjectandanycollection.objcol.Customer;
import org.eclipse.persistence.testing.oxm.mappings.anyobjectandanycollection.PhoneNumber;
import org.eclipse.persistence.testing.oxm.mappings.anyobjectandanycollection.Address;

/*
 * See B5259059: NPE on anyObject mapping XPath null, anyCollection mapping XPath=filled
 * The use cases are described in the doc b5259059_jaxb_factory_npe_DesignSpec_v2006nnnn.doc
 */
public class AnyObjectAndAnyCollectionUC04NoDefaultRootComplexChildrenTestCases extends XMLMappingTestCases {
    public static final String XML_RESOURCE_PATH = "org/eclipse/persistence/testing/oxm/mappings/anyobjectandanycollection/Customer-data_uc04.xml";
    public static final String MAPPING_XPATH = "contact-method";
    public static final String MAPPING_XPATH_OBJ = "object-method";
    private static final boolean firstMappingIsAnyCollection = false;
    private static final boolean firstMappingXPathSet = false;
    private static final boolean secondMappingIsAnyCollection = true;
    private static final boolean secondMappingXPathSet = true;

    public AnyObjectAndAnyCollectionUC04NoDefaultRootComplexChildrenTestCases(String name) throws Exception {
        super(name);
        Project p = new Project();

        // manipulate project for specific use-case (encapsulate normal *Project.java functionality)
        p.addDescriptor(buildRootDescriptor(//
        firstMappingIsAnyCollection,//
        firstMappingXPathSet,//
        secondMappingIsAnyCollection,//    		
        secondMappingXPathSet));

        p.addDescriptor(buildAddressDescriptor());
        p.addDescriptor(buildPhoneDescriptor());
        setProject(p);
        setControlDocument(XML_RESOURCE_PATH);

    }

    public Object getControlObject() {
        Customer customer = new Customer();
        Address anAddress1 = new Address();
        Address anAddress2 = new Address();
        PhoneNumber aPhoneNumber = new PhoneNumber();
        anAddress1.setContent("Ottawa");
        anAddress2.setContent("Montreal");
        aPhoneNumber.setContent("5551111");
        Vector contactMethods = new Vector();

        contactMethods.addElement(anAddress1);
        contactMethods.addElement(anAddress2);
        contactMethods.addElement(aPhoneNumber);
        contactMethods.addElement("joe@example.com");
        customer.setContactMethods(contactMethods);
        return customer;
    }

    protected ClassDescriptor buildRootDescriptor(//
    boolean firstMappingIsAnyCollection,//
    boolean firstMappingXPathSet,//
    boolean secondMappingIsAnyCollection,//    		
    boolean secondMappingXPathSet) {//    		
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Customer.class);
        descriptor.setDefaultRootElement("customer");

        DatabaseMapping anyMapping1 = null;

        // First Mapping
        if (!firstMappingIsAnyCollection) {
            anyMapping1 = new XMLAnyObjectMapping();
            ((XMLAnyObjectMapping)anyMapping1).setAttributeName("anyObject");

            //((XMLAnyObjectMapping) anyMapping1).setGetMethodName("getAnyObject");
            //((XMLAnyObjectMapping) anyMapping1).setSetMethodName("setAnyObject");
            if (firstMappingXPathSet) {
                // set first mapping XPath
                ((XMLAnyObjectMapping)anyMapping1).setXPath(MAPPING_XPATH_OBJ);
            }
            descriptor.addMapping((XMLAnyObjectMapping)anyMapping1);
        } else {
            anyMapping1 = new XMLAnyCollectionMapping();
            ((XMLAnyCollectionMapping)anyMapping1).setAttributeName("contactMethods");

            //((XMLAnyCollectionMapping) anyMapping1).setGetMethodName("getAnyCollection");
            //((XMLAnyCollectionMapping) anyMapping1).setSetMethodName("setAnyCollection");
            if (firstMappingXPathSet) {
                // set first mapping XPath
                ((XMLAnyCollectionMapping)anyMapping1).setXPath(MAPPING_XPATH);
            }
            descriptor.addMapping((XMLAnyCollectionMapping)anyMapping1);
        }

        // Second Mapping
        DatabaseMapping anyMapping2 = null;
        if (!secondMappingIsAnyCollection) {
            anyMapping2 = new XMLAnyObjectMapping();
            ((XMLAnyObjectMapping)anyMapping2).setAttributeName("anyObject");

            //((XMLAnyObjectMapping) anyMapping2).setGetMethodName("getAnyObject");
            //((XMLAnyObjectMapping) anyMapping2).setSetMethodName("setAnyObject");
            if (secondMappingXPathSet) {
                // set second mapping XPath
                ((XMLAnyObjectMapping)anyMapping2).setXPath(MAPPING_XPATH_OBJ);
            }
            descriptor.addMapping((XMLAnyObjectMapping)anyMapping2);
        } else {
            anyMapping2 = new XMLAnyCollectionMapping();
            ((XMLAnyCollectionMapping)anyMapping2).setAttributeName("contactMethods");

            //((XMLAnyCollectionMapping) anyMapping2).setGetMethodName("getAnyCollection");
            //((XMLAnyCollectionMapping) anyMapping2).setSetMethodName("setAnyCollection");
            if (secondMappingXPathSet) {
                // set second mapping XPath
                ((XMLAnyCollectionMapping)anyMapping2).setXPath(MAPPING_XPATH);
            }
            descriptor.addMapping((XMLAnyCollectionMapping)anyMapping2);
        }
        return descriptor;
    }

    protected ClassDescriptor buildPhoneDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(PhoneNumber.class);

        /*
         * B5112171: 25 Apr 2006
         * During marshalling - XML AnyObject and AnyCollection
         * mappings throw a NullPointerException when the
         * "document root element" on child object descriptors are not
         * all defined.  These nodes will be ignored with a warning.
         * Root descriptor above must be anyObject|Collection mapping
         */
        // make default root element undefined to invoke warning log
        descriptor.setDefaultRootElement("phone");
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("phone_content");
        mapping.setGetMethodName("getContent");
        mapping.setSetMethodName("setContent");
        mapping.setXPath("text()");
        descriptor.addMapping(mapping);
        return descriptor;
    }

    protected ClassDescriptor buildAddressDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Address.class);

        /*
         * B5112171: 25 Apr 2006
         * During marshalling - XML AnyObject and AnyCollection
         * mappings throw a NullPointerException when the
         * "document root element" on child object descriptors are not
         * all defined.  These nodes will be ignored with a warning.
         * Root descriptor above must be anyObject|Collection mapping
         */
        // make default root element undefined to invoke warning log
        descriptor.setDefaultRootElement("address");
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("address_content");
        mapping.setGetMethodName("getContent");
        mapping.setSetMethodName("setContent");
        mapping.setXPath("text()");
        descriptor.addMapping(mapping);
        return descriptor;
    }

    // override superclass testcase since it is invalid here
    public void testXMLToObjectFromInputStream() throws Exception {
    }

    // override superclass testcase since it is invalid here
    public void testXMLToObjectFromURL() throws Exception {
    }

    // override superclass testcase since it is invalid here
    public void testUnmarshallerHandler() throws Exception {
    }

    /*
    Exception Description: A descriptor with default root element contact-method was not found in the project

    Local Exception Stack: Exception [TOPLINK-25008] (Oracle TopLink - 10g Release 3 (10.1.3.1.0) (Build 060606)): org.eclipse.persistence.exceptions.XMLMarshalExceptionException Description: A descriptor with default root element contact-method was not found in the project at org.eclipse.persistence.exceptions.XMLMarshalException.noDescriptorWithMatchingRootElement(XMLMarshalException.java:110) at org.eclipse.persistence.oxm.mappings.XMLAnyObjectMapping.getDescriptor(XMLAnyObjectMapping.java:325) at org.eclipse.persistence.oxm.mappings.XMLAnyObjectMapping.buildObjectValuesFromDOMRecord(XMLAnyObjectMapping.java:282) at org.eclipse.persistence.oxm.mappings.XMLAnyObjectMapping.valueFromRow(XMLAnyObjectMapping.java:263) at org.eclipse.persistence.mappings.DatabaseMapping.readFromRowIntoObject(DatabaseMapping.java:1021) at org.eclipse.persistence.internal.descriptors.ObjectBuilder.buildAttributesIntoObject(ObjectBuilder.java:244) at org.eclipse.persistence.internal.oxm.XMLObjectBuilder.buildObject(XMLObjectBuilder.java:136) at org.eclipse.persistence.internal.oxm.record.DOMUnmarshaller.xmlToObject(DOMUnmarshaller.java:287) at org.eclipse.persistence.internal.oxm.record.DOMUnmarshaller.xmlToObject(DOMUnmarshaller.java:267) at org.eclipse.persistence.internal.oxm.record.DOMUnmarshaller.unmarshal(DOMUnmarshaller.java:104) at org.eclipse.persistence.oxm.XMLUnmarshaller.unmarshal(XMLUnmarshaller.java:203) at org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases.testXMLToObjectFromInputStream(Unknown Source)
     */
}
