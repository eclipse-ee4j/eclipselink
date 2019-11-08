/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Dmitry Kornilov - Initial implementation
package weblogic.invocation;

/**
 * Mock for ComponentInvocationContextManager.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public class ComponentInvocationContextManager {

    public static ComponentInvocationContextManager getInstance() {
        return new ComponentInvocationContextManager();
    }

    public ComponentInvocationContext getCurrentComponentInvocationContext() {
        return new ComponentInvocationContext();
    }
}
