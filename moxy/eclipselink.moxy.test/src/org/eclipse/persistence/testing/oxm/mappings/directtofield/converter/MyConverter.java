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
 * Denise Smith - September 22 /2009
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.directtofield.converter;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;

public class MyConverter implements Converter {

	public static boolean HIT_CONVERTER;
	
	public MyConverter(){		
	}
	
	 public Object convertDataValueToObjectValue(Object fieldValue, Session session) {		 
		 HIT_CONVERTER = true;	 
		 return fieldValue;	
	 }

	public Object convertObjectValueToDataValue(Object objectValue,Session session) {
		return objectValue;
	}

	public void initialize(DatabaseMapping mapping, Session session) {	
	}

	public boolean isMutable() {
		return false;
	}

}
