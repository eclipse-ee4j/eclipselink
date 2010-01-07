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
package org.eclipse.persistence.testing.oxm.mappings.transformation;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.ObjectCopyingPolicy;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class TransformationMappingNullTestCases extends XMLMappingTestCases 
{
  public TransformationMappingNullTestCases(String name) throws Exception {
    super(name);
    setProject(new TransformationMappingTestProject());
    setControlDocument("org/eclipse/persistence/testing/oxm/mappings/transformation/employee_null.xml");
  }

  public Object getControlObject()
  {
    Employee emp = new Employee();
    emp.setName("John Smith");
    String[] hours = new String[2];
    hours[0] = null;
    hours[1] = null;
    emp.setNormalHours(hours);
    
    return emp;
  }

}
