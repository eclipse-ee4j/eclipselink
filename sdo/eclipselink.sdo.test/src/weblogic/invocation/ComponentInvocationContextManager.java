/*******************************************************************************
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Dmitry Kornilov - Initial implementation
 ******************************************************************************/
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
