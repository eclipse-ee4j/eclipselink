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
import org.eclipse.persistence.testing.oxm.mappings.anyobjectandanycollection.objobj.Customer;
import org.eclipse.persistence.testing.oxm.mappings.anyobjectandanycollection.PhoneNumber;
import org.eclipse.persistence.testing.oxm.mappings.anyobjectandanycollection.Address;

/*
 * See B5259059: NPE on anyObject mapping XPath null, anyCollection mapping XPath=filled
 * The use cases are described in the doc b5259059_jaxb_factory_npe_DesignSpec_v2006nnnn.doc
 */
public class AnyObjectAndAnyCollectionUC02NoDefaultRootComplexChildrenTestCases extends XMLMappingTestCases {
    public static final String XML_RESOURCE_PATH = "org/eclipse/persistence/testing/oxm/mappings/anyobjectandanycollection/Customer-data_uc02.xml";
    public static final String MAPPING_XPATH = "contact-method";
    public static final String MAPPING_XPATH_OBJ = "object-method";
    private static final boolean firstMappingIsAnyCollection = false;
    private static final boolean firstMappingXPathSet = false;
    private static final boolean secondMappingIsAnyCollection = false;
    private static final boolean secondMappingXPathSet = true;

    public AnyObjectAndAnyCollectionUC02NoDefaultRootComplexChildrenTestCases(String name) throws Exception {
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
        customer.setContactMethods(anAddress1);
        customer.setAnyObject(anAddress1);
        return customer;
    }

    /*
     * Returns the object to be used in a comparison on a read
     * This will typically be the same object used to write
     */
    public Object getReadControlObject() {
        Customer customer = new Customer();
        Address anAddress1 = new Address();
        anAddress1.setContent("Ottawa");
        customer.setAnyObject(anAddress1);
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
        descriptor.setDefaultRootElement("address");
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("address_content");
        mapping.setGetMethodName("getContent");
        mapping.setSetMethodName("setContent");
        mapping.setXPath("text()");
        descriptor.addMapping(mapping);
        return descriptor;
    }
}
