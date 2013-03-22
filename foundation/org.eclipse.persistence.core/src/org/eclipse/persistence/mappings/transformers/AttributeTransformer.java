/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.mappings.transformers;

import java.io.*;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;

/**
 * PUBLIC:
 *  @version $Header: AttributeTransformer.java 11-jul-2006.10:33:44 gyorke Exp $
 *  @author  mmacivor
 *  @since   10
 *  This interface is used by the Transformation Mapping to build the value for a
 *  the mapped attribute on a read. The user must provide an implementation of this interface to the
 *  Transformation Mapping.
 */
public interface AttributeTransformer extends Serializable {

    /**
     * @param mapping - The mapping associated with this transformer. Only used if some special information is required.
     */
    public void initialize(AbstractTransformationMapping mapping);

    /**
     * @param record - The metadata being used to build the object.
     * @param session - the current session
     * @param object - The current object that the attribute is being built for.
     * @return - The attribute value to be built into the object containing this mapping.
     */
    public Object buildAttributeValue(Record record, Object object, Session session);
}
