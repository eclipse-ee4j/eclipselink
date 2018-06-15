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
package org.eclipse.persistence.tools.workbench.framework.app;


/**
 * Describes an implementing class that knows how to add and remove
 * <code>ComponentDescription</code> objects with inherited container behavior
 * specified by the <code>ComponentContainerDescriptor</code>.
 *
 * @see org.eclipse.persistence.tools.workbench.framework.app.ComponentContainerDescription
 * @see org.eclipse.persistence.tools.workbench.framework.app.ComponentDescription
 * @version 10.1.3
 */
public interface ComponentGroupDescription extends ComponentContainerDescription
{
    /**
     * Adds an instance of <code>ComponentDescription</code> which describes
     * how a component should be built to this group.
     *
     * @see org.eclipse.persistence.tools.workbench.framework.app.ComponentDescription
     * @param button
     */
    public void add(ComponentDescription description);

    /**
     * Removes an instance of <code>ComponentDescription</code> which describes
     * how a component should be built to this group.
     *
     * @param button
     */
    public void remove(ComponentDescription description);
}
