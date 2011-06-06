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
package org.eclipse.persistence.testing.oxm.converter;

import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.sessions.Project;


/**
 *  @version $Header: ConverterProject.java 02-nov-2006.10:56:43 gyorke Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
public class ConverterProject extends Project 
{
  public ConverterProject() 
  {
    super();
    addDescriptor(getEmployeeDescriptor());
  }
  public ClassDescriptor getEmployeeDescriptor() 
  {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setDefaultRootElement("employee");
    descriptor.setJavaClass(Employee.class);
    
    XMLDirectMapping firstNameMapping = new XMLDirectMapping();
    firstNameMapping.setAttributeName("firstName");
    firstNameMapping.setXPath("first-name/text()");
	descriptor.addMapping(firstNameMapping);
    
    XMLDirectMapping lastNameMapping = new XMLDirectMapping();
    lastNameMapping.setAttributeName("lastName");
    lastNameMapping.setXPath("last-name/text()");
    descriptor.addMapping(lastNameMapping);
    
    XMLDirectMapping genderMapping = new XMLDirectMapping();
    genderMapping.setAttributeName("gender");
    genderMapping.setXPath("gender/text()");
    ObjectTypeConverter converter = new ObjectTypeConverter(genderMapping);
    converter.addConversionValue("M", "Male");
    converter.addConversionValue("F", "Female");
    genderMapping.setConverter(converter);
    descriptor.addMapping(genderMapping);
    return descriptor;
    
  }
}
