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

import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;

/**
 * <p><b>Purpose</b>: Provides an empty implementation of AttributeTransformer.
 * Users who do not require the full AttributeTransformer API can subclass this class
 * and implement only the methods required.
 *  @see org.eclipse.persistence.mappings.AttributeTransformer
 *  @version $Header: AttributeTransformerAdapter.java 11-jul-2006.10:33:44 gyorke Exp $
 *  @author  mmacivor
 *  @since   10
 */
public class AttributeTransformerAdapter implements AttributeTransformer {
    public void initialize(AbstractTransformationMapping mapping) {
    }

    public Object buildAttributeValue(Record record, Object object, Session session) {
        return null;
    }
}
