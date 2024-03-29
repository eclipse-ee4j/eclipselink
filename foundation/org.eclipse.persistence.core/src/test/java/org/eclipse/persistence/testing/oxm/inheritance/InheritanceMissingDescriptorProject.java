/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.inheritance;

import org.eclipse.persistence.oxm.NamespaceResolver;
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
