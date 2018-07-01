/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.mappings.transformers;

import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;

/**
 * <p><b>Purpose</b>: Provides an empty implementation of AttributeTransformer.
 * Users who do not require the full AttributeTransformer API can subclass this class
 * and implement only the methods required.
 *  @see AttributeTransformer
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
