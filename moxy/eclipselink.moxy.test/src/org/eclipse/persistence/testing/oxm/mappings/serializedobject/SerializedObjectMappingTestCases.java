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
package org.eclipse.persistence.testing.oxm.mappings.serializedobject;
import java.io.FileOutputStream;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.oxm.*;

public class SerializedObjectMappingTestCases extends XMLMappingTestCases {
	
	public SerializedObjectMappingTestCases(String name) throws Exception {
		super(name);
		setProject(new SerializedObjectProject());
		setControlDocument("org/eclipse/persistence/testing/oxm/mappings/serializedobject/employee1.xml");
	}

	public Object getControlObject() {
		Employee emp = new Employee();
		SerializableAddress hexAddress = new SerializableAddress();
		hexAddress.setTheAddress("50 O'Connor Street, Ottawa ON");
		emp.setHexAddress(hexAddress);

		SerializableAddress base64Address = new SerializableAddress();
		base64Address.setTheAddress("2001 Odessy Drive, Ottawa ON");
		emp.setBase64Address(base64Address);

    return emp;
  }
}
