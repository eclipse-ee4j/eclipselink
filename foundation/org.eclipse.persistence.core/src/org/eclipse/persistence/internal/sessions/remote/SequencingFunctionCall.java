/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
        @Override
        protected Object execute(AbstractSession session) {
            return Boolean.valueOf(session.getSequencing() != null);
        }
    }

    public static class WhenShouldAcquireValueForAll extends SimpleFunctionCall {
        @Override
        protected Object execute(AbstractSession session) {
            return Integer.valueOf(session.getSequencing().whenShouldAcquireValueForAll());
        }
    }

    public static class GetNextValue extends SimpleFunctionCall {
        public GetNextValue(Class cls) {
            this.cls = cls;
        }

        protected Class cls;

        @Override
        protected Object execute(AbstractSession session) {
            return session.getSequencing().getNextValue(cls);
        }
    }
}
