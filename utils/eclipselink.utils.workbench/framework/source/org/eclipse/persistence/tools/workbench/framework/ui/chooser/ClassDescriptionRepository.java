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
package org.eclipse.persistence.tools.workbench.framework.ui.chooser;

import java.util.Iterator;

/**
 * Used by the UI to get and refresh a collection of "class descriptions"
 * that can be queried via a ClassDescriptionAdapter.
 */
public interface ClassDescriptionRepository {

    /**
     * Return an iterator on the collection of "class descriptions"
     * currently in the repository.
     */
    Iterator classDescriptions();

    /**
     * Refresh the collection of "class descriptions" in the repository.
     */
    void refreshClassDescriptions();

}
