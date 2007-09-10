/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
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
            return new Boolean(session.getSequencing() != null);
        }
    }

    public static class WhenShouldAcquireValueForAll extends SimpleFunctionCall {
        protected Object execute(AbstractSession session) {
            return new Integer(session.getSequencing().whenShouldAcquireValueForAll());
        }
    }

    public static class ShouldAcquireValueAfterInsert extends SimpleFunctionCall {
        public ShouldAcquireValueAfterInsert(Class cls) {
            this.cls = cls;
        }

        protected Class cls;

        protected Object execute(AbstractSession session) {
            return new Boolean(session.getSequencing().shouldAcquireValueAfterInsert(cls));
        }
    }

    public static class ShouldOverrideExistingValue extends SimpleFunctionCall {
        public ShouldOverrideExistingValue(Class cls, Object existingValue) {
            this.cls = cls;
            this.existingValue = existingValue;
        }

        protected Class cls;
        protected Object existingValue;

        protected Object execute(AbstractSession session) {
            return new Boolean(session.getSequencing().shouldOverrideExistingValue(cls, existingValue));
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