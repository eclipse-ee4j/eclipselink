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
package org.eclipse.persistence.tools.workbench.test.framework.ui.tools;

import java.awt.Dimension;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.framework.uitools.Spacer;

public class SpacerTest extends TestCase
{
    public SpacerTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(SpacerTest.class);
    }

    public void testDefaultSize() throws Exception
    {
        Spacer spacer = new Spacer();

        Dimension size = spacer.getPreferredSize();
        assertEquals(size.width, 0);
        assertEquals(size.height, 0);

        spacer.setPreferredSize(new Dimension(99, 20));
        size = spacer.getPreferredSize();
        assertEquals(size.width, 99);
        assertEquals(size.height, 20);

        spacer.setPreferredSize(null);
        size = spacer.getPreferredSize();
        assertEquals(size.width, 0);
        assertEquals(size.height, 0);
    }

    public void testPredefinedWidth() throws Exception
    {
        Spacer spacer = new Spacer(23);

        Dimension size = spacer.getPreferredSize();
        assertEquals(size.width, 23);
        assertEquals(size.height, 0);

        spacer.setPreferredSize(new Dimension(99, 20));
        size = spacer.getPreferredSize();
        assertEquals(size.width, 99);
        assertEquals(size.height, 20);

        spacer.setPreferredSize(null);
        size = spacer.getPreferredSize();
        assertEquals(size.width, 23);
        assertEquals(size.height, 0);
    }
}
