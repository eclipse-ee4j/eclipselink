/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Denise Smith - September 22 /2009
package org.eclipse.persistence.testing.oxm.mappings.directtofield.converter;

import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.NullPolicy;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.sessions.Project;


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
    NullPolicy nullPolicy = new NullPolicy();
    nullPolicy.setSetPerformedForAbsentNode(false);
    genderMapping.setNullPolicy(nullPolicy);
    MyConverter converter = new MyConverter();

    genderMapping.setConverter(converter);
    descriptor.addMapping(genderMapping);
    return descriptor;

  }
}
