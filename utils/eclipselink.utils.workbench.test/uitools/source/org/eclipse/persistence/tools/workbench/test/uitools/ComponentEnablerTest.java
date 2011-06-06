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
package org.eclipse.persistence.tools.workbench.test.uitools;

import java.awt.Component;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;

public class ComponentEnablerTest extends TestCase
{
	public ComponentEnablerTest(String name)
	{
		super(name);
	}

	public static Test suite()
	{
		return new TestSuite(ComponentEnablerTest.class, "ComponentEnabler Test");
	}

	public void testUpdateEnableState1() throws Exception
	{
		JButton component1 = new JButton();
		component1.setEnabled(true);
		SimplePropertyValueModel booleanHolder = new SimplePropertyValueModel(Boolean.TRUE);

		new ComponentEnabler(booleanHolder, Collections.singleton(component1));

		assertTrue(component1.isEnabled());
	}

	public void testUpdateEnableState10() throws Exception
	{
		JButton component1 = new JButton();
		component1.setEnabled(true);

		JLabel component2 = new JLabel();
		component2.setEnabled(true);

		SimplePropertyValueModel booleanHolder = new SimplePropertyValueModel(Boolean.TRUE);

		new ComponentEnabler(booleanHolder, new Component[] { component1, component2 });

		booleanHolder.setValue(Boolean.FALSE);

		assertFalse(component1.isEnabled());
		assertFalse(component2.isEnabled());
	}

	public void testUpdateEnableState11() throws Exception
	{
		TestClass testClass = new TestClass();

		JButton component1 = new JButton();
		component1.setEnabled(true);
		testClass.addComponent(component1);

		JLabel component2 = new JLabel();
		component2.setEnabled(true);
		testClass.addComponent(component2);

		SimplePropertyValueModel booleanHolder = new SimplePropertyValueModel(Boolean.TRUE);
		new ComponentEnabler(booleanHolder, testClass.components());

		booleanHolder.setValue(Boolean.FALSE);

		assertFalse(component1.isEnabled());
		assertFalse(component2.isEnabled());
	}

	public void testUpdateEnableState12() throws Exception
	{
		TestClass testClass = new TestClass();

		JButton component1 = new JButton();
		component1.setEnabled(true);
		testClass.addComponent(component1);

		JLabel component2 = new JLabel();
		component2.setEnabled(true);
		testClass.addComponent(component2);

		SimplePropertyValueModel booleanHolder = new SimplePropertyValueModel(Boolean.TRUE);
		new ComponentEnabler(booleanHolder, testClass.components());

		booleanHolder.setValue(Boolean.FALSE);

		assertFalse(component1.isEnabled());
		assertFalse(component2.isEnabled());

		JLabel component3 = new JLabel();
		component2.setEnabled(true);
		testClass.addComponent(component3);

		assertFalse(component1.isEnabled());
	}

	public void testUpdateEnableState13() throws Exception
	{
		TestClass testClass = new TestClass();

		JButton component1 = new JButton();
		component1.setEnabled(true);
		testClass.addComponent(component1);

		JLabel component2 = new JLabel();
		component2.setEnabled(true);
		testClass.addComponent(component2);

		SimplePropertyValueModel booleanHolder = new SimplePropertyValueModel(Boolean.TRUE);
		new ComponentEnabler(booleanHolder, testClass.components());

		booleanHolder.setValue(Boolean.FALSE);

		assertFalse(component1.isEnabled());
		assertFalse(component2.isEnabled());

		testClass.clear();
	}

	public void testUpdateEnableState2() throws Exception
	{
		JButton component1 = new JButton();
		component1.setEnabled(false);
		SimplePropertyValueModel booleanHolder = new SimplePropertyValueModel(Boolean.TRUE);

		new ComponentEnabler(booleanHolder, Collections.singleton(component1));

		assertTrue(component1.isEnabled());
	}

	public void testUpdateEnableState3() throws Exception
	{
		JButton component1 = new JButton();
		component1.setEnabled(false);
		SimplePropertyValueModel booleanHolder = new SimplePropertyValueModel(Boolean.FALSE);

		new ComponentEnabler(booleanHolder, Collections.singleton(component1));

		assertFalse(component1.isEnabled());
	}

	public void testUpdateEnableState4() throws Exception
	{
		JButton component1 = new JButton();
		component1.setEnabled(true);
		SimplePropertyValueModel booleanHolder = new SimplePropertyValueModel(Boolean.FALSE);

		new ComponentEnabler(booleanHolder, Collections.singleton(component1));

		assertFalse(component1.isEnabled());
	}

	public void testUpdateEnableState5() throws Exception
	{
		JButton component1 = new JButton();
		component1.setEnabled(true);

		JLabel component2 = new JLabel();
		component2.setEnabled(true);

		SimplePropertyValueModel booleanHolder = new SimplePropertyValueModel(Boolean.TRUE);

		new ComponentEnabler(booleanHolder, new Component[] { component1, component2 });

		assertTrue(component1.isEnabled());
		assertTrue(component2.isEnabled());
	}

	public void testUpdateEnableState6() throws Exception
	{
		JButton component1 = new JButton();
		component1.setEnabled(false);

		JLabel component2 = new JLabel();
		component2.setEnabled(false);

		SimplePropertyValueModel booleanHolder = new SimplePropertyValueModel(Boolean.TRUE);

		new ComponentEnabler(booleanHolder, new Component[] { component1, component2 });

		assertTrue(component1.isEnabled());
		assertTrue(component2.isEnabled());
	}

	public void testUpdateEnableState7() throws Exception
	{
		JButton component1 = new JButton();
		component1.setEnabled(false);

		JLabel component2 = new JLabel();
		component2.setEnabled(false);

		SimplePropertyValueModel booleanHolder = new SimplePropertyValueModel(Boolean.FALSE);

		new ComponentEnabler(booleanHolder, new Component[] { component1, component2 });

		assertFalse(component1.isEnabled());
		assertFalse(component2.isEnabled());
	}

	public void testUpdateEnableState8() throws Exception
	{
		JButton component1 = new JButton();
		component1.setEnabled(true);

		JLabel component2 = new JLabel();
		component2.setEnabled(true);

		SimplePropertyValueModel booleanHolder = new SimplePropertyValueModel(Boolean.FALSE);

		new ComponentEnabler(booleanHolder, new Component[] { component1, component2 });

		assertFalse(component1.isEnabled());
		assertFalse(component2.isEnabled());
	}

	public void testUpdateEnableState9() throws Exception
	{
		JButton component1 = new JButton();
		component1.setEnabled(true);

		JLabel component2 = new JLabel();
		component2.setEnabled(true);

		SimplePropertyValueModel booleanHolder = new SimplePropertyValueModel(Boolean.FALSE);

		new ComponentEnabler(booleanHolder, new Component[] { component1, component2 });

		booleanHolder.setValue(Boolean.TRUE);

		assertTrue(component1.isEnabled());
		assertTrue(component2.isEnabled());
	}

	private class TestClass extends AbstractModel
	{
		private Vector components = new Vector();
		public static final String COMPONENT_COLLECTION = "components";

		public void addComponent(Component component)
		{
			addItemToCollection(component, this.components, COMPONENT_COLLECTION);
		}

		public void clear()
		{
			fireCollectionChanged(COMPONENT_COLLECTION);
		}

		public Iterator components()
		{
			return this.components.iterator();
		}

		public void removeComponent(Component component)
		{
			removeItemFromCollection(component, this.components, COMPONENT_COLLECTION);
		}
	}
}
