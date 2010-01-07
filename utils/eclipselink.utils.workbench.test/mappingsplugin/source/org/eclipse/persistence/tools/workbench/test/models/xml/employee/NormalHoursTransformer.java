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
package org.eclipse.persistence.tools.workbench.test.models.xml.employee;

import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

/** 
 * These methods (and this interface) do nothing.  
 * Just used as a MWClass definition for the Employee.normalHours 
 * transformation mapping. 
 */
	
public final class NormalHoursTransformer
	implements AttributeTransformer, FieldTransformer
{
	public void initialize(AbstractTransformationMapping mapping) {}
	
	public Object buildAttributeValue(Record record, Object object, Session session) {
		return null;
	}
	
	public Object buildFieldValue(Object instance, String fieldName, Session session) {
		return null;
	}
}
