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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.inheritance;

import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.sessions.Project;

public class InheritanceMissingDescriptorProject extends Project
{

  private NamespaceResolver namespaceResolver;

  public InheritanceMissingDescriptorProject()
  {
    super();

    namespaceResolver = new NamespaceResolver();
    namespaceResolver.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");

    //No descriptors are present in the project
  }

}
