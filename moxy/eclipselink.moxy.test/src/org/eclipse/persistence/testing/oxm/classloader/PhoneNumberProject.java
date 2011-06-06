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
package org.eclipse.persistence.testing.oxm.classloader;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class PhoneNumberProject extends Project {

	public PhoneNumberProject() {
		super();
		this.addDescriptor(getPhoneNumberDescriptor());
	}
	
	private XMLDescriptor getPhoneNumberDescriptor() {
		try {
			ClassLoader phoneNumberClassLoader = new JARClassLoader("org/eclipse/persistence/testing/oxm/classloader/PhoneNumber.jar");
			Class phoneNumberClass = phoneNumberClassLoader.loadClass("org.eclipse.persistence.testing.oxm.classloader.PhoneNumber");
	
			XMLDescriptor xmlDescriptor = new XMLDescriptor();
			xmlDescriptor.setJavaClass(phoneNumberClass);
			xmlDescriptor.setDefaultRootElement("phone-number");
			
			XMLDirectMapping nameMapping = new XMLDirectMapping();
			nameMapping.setAttributeName("number");
			nameMapping.setXPath("text()");
			xmlDescriptor.addMapping(nameMapping);
			
			return xmlDescriptor;
		} catch(ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
}
