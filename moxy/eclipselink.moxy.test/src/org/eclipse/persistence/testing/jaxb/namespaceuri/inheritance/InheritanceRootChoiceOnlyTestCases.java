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
 *     Oracle - December 2011
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

import org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance.package2.AnotherPackageSubType;


public class InheritanceRootChoiceOnlyTestCases extends JAXBTestCases {
    private static final String  XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/inheritance/choicecollection.xml";

    public InheritanceRootChoiceOnlyTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setClasses(new Class[] {RootChoiceOnly.class});
    }

    protected Object getControlObject() {
    	RootChoiceOnly root = new RootChoiceOnly();
		SubType subType = new SubType();
		subType.subTypeProp = 10;
		
		SubTypeLevel2 subTypeLevel2 = new SubTypeLevel2();
		subTypeLevel2.baseProp = "boo";
		
		AnotherPackageSubType anotherPackageSubType = new AnotherPackageSubType();
		
		List choiceList = new ArrayList();
		choiceList.add(anotherPackageSubType);
		choiceList.add(subTypeLevel2);
		choiceList.add(new String("choice string test"));
		choiceList.add(new Integer(500));
		root.choiceList = choiceList;
		return root;
    }
    
    public void testCompareOutput() throws Exception{
    	Marshaller m = jaxbContext.createMarshaller();
		System.out.println(jaxbContext.getClass());

    	m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    	m.marshal(getWriteControlObject(), new File("InheritanceRootChoiceOnlyTestCases_moxyOutput_equalNR.xml"));
    	
    	    	
    	JAXBContext riContext = JAXBContext.newInstance(new Class[] {RootChoiceOnly.class});
		System.out.println(riContext.getClass());

    	Marshaller riMarshaller = riContext.createMarshaller();
    	riMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    	riMarshaller.marshal(getWriteControlObject(), new File("InheritanceRootChoiceOnlyTestCases_riOutput.xml"));
    }
    
}