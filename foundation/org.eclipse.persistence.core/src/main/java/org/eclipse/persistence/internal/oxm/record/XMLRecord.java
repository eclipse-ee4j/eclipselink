/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
    XMLRecord.Nil NIL = new XMLRecord.Nil();

    CoreAttributeGroup DEFAULT_ATTRIBUTE_GROUP = new CoreAttributeGroup() {
        @Override
        public boolean containsAttributeInternal(String attribute) {
            return true;
        }
    };

    /**
     * @since EclipseLink 2.6.0
     */
    ConversionManager getConversionManager();

    char getNamespaceSeparator();

    ABSTRACT_SESSION getSession();

    boolean isNamespaceAware();


}
