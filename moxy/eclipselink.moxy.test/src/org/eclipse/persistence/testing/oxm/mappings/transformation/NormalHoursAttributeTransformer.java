/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.Record;
import java.util.Calendar;
import org.eclipse.persistence.mappings.transformers.*;
/**
 *  @version $Header: NormalHoursAttributeTransformer.java 07-oct-2005.21:46:09 pkrogh Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
public class NormalHoursAttributeTransformer extends AttributeTransformerAdapter 
{
  public Object buildAttributeValue(Record record, Object instance, Session session) 
  {
    String[] hours = new String[2];
    hours[0] = (String)record.get("normal-hours/start-time/text()");
    hours[1] = (String)record.get("normal-hours/end-time/text()");
    return hours;
  }
}
