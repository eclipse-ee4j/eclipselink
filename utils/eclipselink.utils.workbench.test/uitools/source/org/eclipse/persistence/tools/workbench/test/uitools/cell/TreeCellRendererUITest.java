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
package org.eclipse.persistence.tools.workbench.test.uitools.cell;

/**
 * make the tree read-only so only the renderers are used
 */
public class TreeCellRendererUITest extends TreeCellEditorUITest {

    public static void main(String[] args) throws Exception {
        new TreeCellRendererUITest().exec(args);
    }

    protected TreeCellRendererUITest() {
        super();
    }

    protected boolean isEditable() {
        return false;
    }

}
