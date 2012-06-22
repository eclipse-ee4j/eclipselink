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
 * Denise Smith - September 10 /2009
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmladapter.packagelevel;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ClassAtoClassBAdapter extends XmlAdapter<ClassA, ClassB> {

	public ClassA marshal(ClassB classBObject) throws Exception {
		String s = classBObject.getSomeValue();
		ClassA classA = new ClassA();
		classA.setTheValue(s);
		return classA;
	}

	public ClassB unmarshal(ClassA classAObject) throws Exception {
		String s = classAObject.getTheValue();
		ClassB classB = new ClassB();
		classB.setSomeValue(s);
		return classB;
	}
	
	
}
