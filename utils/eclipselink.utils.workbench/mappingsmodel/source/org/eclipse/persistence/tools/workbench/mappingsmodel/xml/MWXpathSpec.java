/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsmodel.xml;

/**
 * A simple interface that describes what xpaths are allowable
 * in a particular context (usually a MWXpathContext)
 */
public interface MWXpathSpec
{
    /** Return whether xpaths for this spec may use a collection of nodes */
    boolean mayUseCollectionData();

    /** Return whether xpaths for this spec may use a simple (text only) node */
    boolean mayUseSimpleData();

    /** Return whether xpaths for this spec may use a complex (non-text) node */
    boolean mayUseComplexData();
}
