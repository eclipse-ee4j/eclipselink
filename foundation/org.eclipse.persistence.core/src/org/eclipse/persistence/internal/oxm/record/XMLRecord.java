/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.5 - initial implementation
package org.eclipse.persistence.internal.oxm.record;

import org.eclipse.persistence.core.queries.CoreAttributeGroup;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.ConversionManager;

public interface XMLRecord<ABSTRACT_SESSION extends CoreAbstractSession> {

    /**
     * Nil: This is used to indicate that this field represents xsi:nil="true"
     */
    public static class Nil {
        private Nil() {
        }
    }

    /**
     * Nil: This is used to indicate that this field represents xsi:nil="true"
     */
    public static final XMLRecord.Nil NIL = new XMLRecord.Nil();

    public static final CoreAttributeGroup DEFAULT_ATTRIBUTE_GROUP = new CoreAttributeGroup() {
        @Override
        public boolean containsAttributeInternal(String attribute) {
            return true;
        }
    };

    /**
     * @since EclipseLink 2.6.0
     */
    public ConversionManager getConversionManager();

    public char getNamespaceSeparator();

    public ABSTRACT_SESSION getSession();

    public boolean isNamespaceAware();


}
