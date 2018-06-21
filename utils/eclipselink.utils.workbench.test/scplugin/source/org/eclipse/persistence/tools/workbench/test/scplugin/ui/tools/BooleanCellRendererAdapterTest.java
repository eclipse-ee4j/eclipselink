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
package org.eclipse.persistence.tools.workbench.test.scplugin.ui.tools;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.BooleanCellRendererAdapter;

/**
 * @version 10.0.3
 * @author Pascal Filion
 */
public class BooleanCellRendererAdapterTest extends TestCase
{
    public BooleanCellRendererAdapterTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(BooleanCellRendererAdapterTest.class, "BooleanLabelDecorator Test");
    }

    public void testDefault() throws Exception
    {
        BooleanCellRendererAdapter decorator = new BooleanCellRendererAdapter("test1", "test2");

        String text = decorator.buildText(Boolean.TRUE);
        assertEquals(text, "test1");

        text = decorator.buildText(Boolean.FALSE);
        assertEquals(text, "test2");

        text = decorator.buildText(null);
        assertEquals(text, null);

        text = decorator.buildText(decorator);
        assertEquals(text, null);
    }
}
