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
package org.eclipse.persistence.testing.oxm.mappings.transformation;

import org.eclipse.persistence.mappings.transformers.FieldTransformerAdapter;
import org.eclipse.persistence.sessions.Session;
/**
 *  @version $Header: StartTimeTransformer.java 07-oct-2005.21:46:10 pkrogh Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

public class StartTimeTransformer extends FieldTransformerAdapter
{
  @Override
  public Object buildFieldValue(Object instance, String fieldName, Session session)
  {
    Employee emp = (Employee)instance;
    return emp.getStartTime();
  }
}
