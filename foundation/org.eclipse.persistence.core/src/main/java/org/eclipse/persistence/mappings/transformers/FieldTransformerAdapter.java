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
package org.eclipse.persistence.mappings.transformers;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;

/**
 * <p><b>Purpose</b>: Provides an empty implementation of FieldTransformer.
 * Users who do not require the full FieldTransformer API can subclass this class
 * and implement only the methods required.
 *  @see FieldTransformer
 *  @version $Header: FieldTransformerAdapter.java 11-jul-2006.10:33:44 gyorke Exp $
 *  @author  mmacivor
 *  @since   10
 */
public class FieldTransformerAdapter implements FieldTransformer {
    @Override
    public void initialize(AbstractTransformationMapping mapping) {
    }

    @Override
    public Object buildFieldValue(Object object, String fieldName, Session session) {
        return null;
    }
}
