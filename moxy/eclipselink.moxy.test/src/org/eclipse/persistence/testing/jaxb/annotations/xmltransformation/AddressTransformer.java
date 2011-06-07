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
 * Denise Smith - 2.3
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmltransformation;

import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

public class AddressTransformer implements FieldTransformer, AttributeTransformer{

	@Override
	public void initialize(AbstractTransformationMapping mapping) {
		
	}

	@Override
	public Object buildFieldValue(Object instance, String fieldName, Session session) {
		if(instance instanceof EmployeeWithAddress){
		    if(fieldName.contains("street")){
		        return ((EmployeeWithAddress)instance).address.street;	
    		}else if(fieldName.contains("city")){
    			return ((EmployeeWithAddress)instance).address.city;
	    	}
		}
		if(instance instanceof EmployeeWithAddressAndTransformer){
		    if(fieldName.contains("street")){
		        return ((EmployeeWithAddressAndTransformer)instance).address.street;	
    		}else if(fieldName.contains("city")){
    			return ((EmployeeWithAddressAndTransformer)instance).address.city;
	    	}
		}
		return null;
	}

	@Override
	public Object buildAttributeValue(Record record, Object object, Session session) {
		String street = (String)record.get("address/street/text()");		
		String city =(String)record.get("address/city/text()");
		return new AddressNoCtor(street, city);
	}

}
