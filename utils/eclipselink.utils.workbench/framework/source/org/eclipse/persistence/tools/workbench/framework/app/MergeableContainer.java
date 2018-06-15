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
 * Describes an UI component class that can be merged with another
 * based upon the <code>FrameworkAction</code> objects contained therein.
 *
 * @see org.eclipse.persistence.tools.workbench.framework.app.ActionContainer
 * @version 10.1.3
 */
public interface MergeableContainer {

    /**
     * Implementing class should represent merge behavior in this
     * method.
     */
    public void mergeWith(ActionContainer actionContainer);
}
