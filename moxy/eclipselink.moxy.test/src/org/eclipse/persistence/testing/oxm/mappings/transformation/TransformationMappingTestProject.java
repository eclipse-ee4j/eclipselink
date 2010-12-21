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


/**
 *  @version $Header: TransformationMappingTestProject.java 02-nov-2006.10:57:27 gyorke Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLAnyObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sessions.Project;


public class TransformationMappingTestProject extends Project {

  public TransformationMappingTestProject() {
    addDescriptor(getEmployeeDescriptor());
    addDescriptor(getRootWithAnyCollectionDescriptor());
    addDescriptor(getRootWithAnyObjectDescriptor());
    addDescriptor(getRootWithCompositeCollectionDescriptor());
    addDescriptor(getRootWithCompositeObjectDescriptor());
  }

  public XMLDescriptor getEmployeeDescriptor() {
    XMLDescriptor employeeDescriptor = new XMLDescriptor();
    employeeDescriptor.setJavaClass(org.eclipse.persistence.testing.oxm.mappings.transformation.Employee.class);
    employeeDescriptor.setDefaultRootElement("employee");
    employeeDescriptor.setIdentityMapClass(NoIdentityMap.class);
    employeeDescriptor.setExistenceChecking("Check database");

		XMLTransformationMapping nameMapping = new XMLTransformationMapping();
		nameMapping.setAttributeName("name");
		nameMapping.setGetMethodName("getName");
		nameMapping.setSetMethodName("setName");
		nameMapping.setAttributeTransformation("buildNameAttribute");
		nameMapping.addFieldTransformation("name/text()", "buildNameField");
		nameMapping.setIsMutable(false);
		employeeDescriptor.addMapping(nameMapping);
		
    XMLTransformationMapping mapping = new XMLTransformationMapping();
    mapping.setAttributeName("normalHours");
    mapping.setGetMethodName("getNormalHours");
    mapping.setSetMethodName("setNormalHours");    
    mapping.setAttributeTransformer(new org.eclipse.persistence.testing.oxm.mappings.transformation.NormalHoursAttributeTransformer());
    mapping.addFieldTransformer("normal-hours/start-time/text()", new StartTimeTransformer());      
    mapping.addFieldTransformer("normal-hours/end-time/text()", new org.eclipse.persistence.testing.oxm.mappings.transformation.EndTimeTransformer());
    employeeDescriptor.addMapping(mapping);
    return employeeDescriptor;
  }
  
  public XMLDescriptor getRootWithAnyCollectionDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(RootWithAnyCollection.class);
    descriptor.setDefaultRootElement("root-with-any-collection");

    XMLAnyCollectionMapping objectsMapping = new XMLAnyCollectionMapping();
    objectsMapping.setAttributeName("objects");
    descriptor.addMapping(objectsMapping);
    
    return descriptor;
  }
  
  public XMLDescriptor getRootWithAnyObjectDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(RootWithAnyObject.class);
    descriptor.setDefaultRootElement("root-with-any-object");
    
    XMLAnyObjectMapping objectMapping = new XMLAnyObjectMapping();
    objectMapping.setAttributeName("object");
    descriptor.addMapping(objectMapping);
    
    return descriptor;    
  }
  
  public XMLDescriptor getRootWithCompositeCollectionDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(RootWithCompositeCollection.class);
    descriptor.setDefaultRootElement("root-with-composite-collection");

    XMLCompositeCollectionMapping employeesMapping = new XMLCompositeCollectionMapping();
    employeesMapping.setAttributeName("employees");
    employeesMapping.setXPath("employee");
    employeesMapping.setReferenceClass(Employee.class);
    descriptor.addMapping(employeesMapping);
    
    return descriptor;    
  }
  
  public XMLDescriptor getRootWithCompositeObjectDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(RootWithCompositeObject.class);
    descriptor.setDefaultRootElement("root-with-composite-object");
    
    XMLCompositeObjectMapping employeeMapping = new XMLCompositeObjectMapping();
    employeeMapping.setAttributeName("employee");
    employeeMapping.setXPath("employee");
    employeeMapping.setReferenceClass(Employee.class);
    descriptor.addMapping(employeeMapping);
    
    return descriptor;
  }
  
}
