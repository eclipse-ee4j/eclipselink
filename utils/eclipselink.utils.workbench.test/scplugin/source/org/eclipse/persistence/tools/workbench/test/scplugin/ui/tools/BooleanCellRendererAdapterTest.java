/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
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
