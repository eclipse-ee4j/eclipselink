/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.uitools.SwitcherPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;

/**
 *
 */
public class SwitcherPanelTests extends TestCase
{
	
	public static Test suite() {
		return new TestSuite(SwitcherPanelTests.class);
	}
	
	public SwitcherPanelTests(String name)
	{
		super(name);
	}

	private PropertyValueModel buildPropertyAdapter(ModelTest modelTest)
	{
		return new PropertyAspectAdapter(ModelTest.NAME_PROPERTY, modelTest)
		{
			protected Object getValueFromSubject()
			{
				return ((ModelTest) this.subject).getName();
			}
		};
	}

	private TransformationPropertyValueModel buildTransformationPropertyAdapter1(ModelTest modelTest)
	{
		return new TransformationPropertyValueModel(buildPropertyAdapter(modelTest))
		{
			protected Object transform(Object value)
			{
				if (value == null)
					return null;

				if ("label".equals(value))
					return new JLabel("A label");

				if ("button".equals(value))
					return new JButton("A button");

				throw new IllegalArgumentException("The value is unknown");
			}
		};
	}

	private TransformationPropertyValueModel buildTransformationPropertyAdapter2(ModelTest modelTest)
	{
		final JLabel label = new JLabel("A label");
		final JButton button = new JButton("A button");

		return new TransformationPropertyValueModel(buildPropertyAdapter(modelTest))
		{
			protected Object transform(Object value)
			{
				if (value == null)
					return null;

				if ("label".equals(value))
					return label;

				if ("button".equals(value))
					return button;

				throw new IllegalArgumentException("The value is unknown");
			}
		};
	}

	public void testNestedSwitching1()
	{
		ModelTest modelTest = new ModelTest();

		TransformationPropertyValueModel holder = buildTransformationPropertyAdapter2(modelTest);
		new SwitcherPanel(holder);

	}

	public void testNullPropertyHolder()
	{
		try
		{
			new SwitcherPanel(null);
			assertTrue("The property holder cannot be null and no exception was thrown", false);
		}
		catch (NullPointerException e)
		{
			// Good
		}
	}

	public void testSwitching1()
	{
		ModelTest modelTest = new ModelTest();

		TransformationPropertyValueModel holder = buildTransformationPropertyAdapter1(modelTest);
		SwitcherPanel panel = new SwitcherPanel(holder);

		// First there is no children since the panel is not a child of any component
		assertTrue(panel.getComponentCount() == 0);

		// This will engage the listeners on the model
		JPanel container = new JPanel();
		container.add(panel);

		// The value that is been listened is still null
		assertTrue(panel.getComponentCount() == 0);
	}

	public void testSwitching2()
	{
		ModelTest modelTest = new ModelTest();

		TransformationPropertyValueModel holder = buildTransformationPropertyAdapter1(modelTest);
		SwitcherPanel panel = new SwitcherPanel(holder);

		// First there is no children since the panel is not a child of any component
		assertTrue(panel.getComponentCount() == 0);

		modelTest.setName("label");

		// This will engage the listener on the model and should add a children
		JPanel container = new JPanel();

		// We need a peer in order for addNotify() to be called
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(container);
		frame.setVisible(true);

		// Now we can do the test
		container.add(panel);
		assertTrue(panel.getComponentCount() == 1);
		assertTrue(panel.getComponent(0) instanceof JLabel);

		// This should switch the children
		modelTest.setName("button");
		assertTrue(panel.getComponentCount() == 1);
		assertTrue(panel.getComponent(0) instanceof JButton);

		// This should simply remove the children
		modelTest.setName(null);
		assertTrue(panel.getComponentCount() == 0);

		frame.setVisible(false);
	}

	public void testSwitching3()
	{
		ModelTest modelTest = new ModelTest();

		TransformationPropertyValueModel holder = buildTransformationPropertyAdapter2(modelTest);
		SwitcherPanel panel = new SwitcherPanel(holder);

		// First there is no children since the panel is not a child of any component
		assertTrue(panel.getComponentCount() == 0);

		// Changing the value in the model should not affect the PanelSwitcherAdapter
		// yet since it does not have a parent yet
		modelTest.setName("label");

		// This will engage the listener on the model and should add a children
		JPanel container = new JPanel();

		// We need a peer in order for addNotify() to be called
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(container);
		frame.setVisible(true);

		// Now we can do the test
		container.add(panel);
		assertEquals(1, panel.getComponentCount());
		assertTrue(panel.getComponent(0) instanceof JLabel);
		JLabel label = (JLabel) panel.getComponent(0);

		// This should switch the children
		modelTest.setName("button");
		assertTrue(panel.getComponentCount() == 1);
		assertTrue(panel.getComponent(0) instanceof JButton);
		JButton button = (JButton) panel.getComponent(0);

		// This should simply remove the children
		modelTest.setName(null);
		assertTrue(panel.getComponentCount() == 0);

		// Switch again
		modelTest.setName("label");
		assertTrue(panel.getComponentCount() == 1);
		assertTrue(panel.getComponent(0) == label);

		// Switch again
		modelTest.setName("button");
		assertTrue(panel.getComponentCount() == 1);
		assertTrue(panel.getComponent(0) == button);

		// Switch again
		modelTest.setName("label");
		assertTrue(panel.getComponentCount() == 1);
		assertTrue(panel.getComponent(0) == label);

		// Switch again
		modelTest.setName(null);
		assertTrue(panel.getComponentCount() == 0);

		frame.setVisible(false);
	}

	private static class ModelTest extends AbstractModel
	{
		private String name;
		public static final String NAME_PROPERTY = "name";

		public String getName()
		{
			return this.name;
		}

		public void setName(String name)
		{
			String oldName = getName();
			this.name = name;
			firePropertyChanged(NAME_PROPERTY, oldName, name);
		}
	}

}
