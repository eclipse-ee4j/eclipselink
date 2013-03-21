/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm.record;

import org.eclipse.persistence.core.queries.CoreAttributeGroup;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;

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

    public char getNamespaceSeparator();

    public ABSTRACT_SESSION getSession();

    public boolean isNamespaceAware();


}