/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.framework.ui.tools;

import java.awt.Dimension;

import org.eclipse.persistence.tools.workbench.framework.uitools.Spacer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

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