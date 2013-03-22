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
package org.eclipse.persistence.internal.sessions.remote;

import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p>
 * <b>Purpose</b>: This class declares sequencing remote function calls classes
 * <p>
 * <b>Description</b>: Provides the implementation for sequencing remote function calls
 * <p>
 */
public class SequencingFunctionCall {
    public static class DoesExist extends SimpleFunctionCall {
        protected Object execute(AbstractSession session) {
            return Boolean.valueOf(session.getSequencing() != null);
        }
    }

    public static class WhenShouldAcquireValueForAll extends SimpleFunctionCall {
        protected Object execute(AbstractSession session) {
            return Integer.valueOf(session.getSequencing().whenShouldAcquireValueForAll());
        }
    }

    public static class GetNextValue extends SimpleFunctionCall {
        public GetNextValue(Class cls) {
            this.cls = cls;
        }

        protected Class cls;

        protected Object execute(AbstractSession session) {
            return session.getSequencing().getNextValue(cls);
        }
    }
}
