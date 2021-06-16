/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.readonly;

import java.util.Calendar;
import java.util.Vector;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.mappings.transformers.*;
import org.eclipse.persistence.sessions.Record;

public class NormalHoursTransformer implements AttributeTransformer, FieldTransformer
{
    public void initialize(AbstractTransformationMapping mapping)
    {
    }
  public Object buildFieldValue(Object instance, String fieldName, Session session)
  {
    if(fieldName.equals("normal-hours/start-time"))
        {
            return ((Employee)instance).normalHours2.elementAt(0);
        }
        return ((Employee)instance).normalHours2.elementAt(1);
  }
  public Object buildAttributeValue(Record record, Object instance, Session session)
  {
    Vector normalHours = new Vector(2);
    normalHours.addElement(record.get("normal-hours/start-time"));
    normalHours.addElement(record.get("normal-hours/end-time"));
    return normalHours;
  }
}

