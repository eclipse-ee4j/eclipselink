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
package org.eclipse.persistence.tools.workbench.test.framework.ui.tools;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.framework.uitools.ComponentAligner;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;

public class ComponentAlignerTest extends TestCase
{
	public ComponentAlignerTest(String name)
	{
		super(name);
	}

	public static Test suite()
	{
		return new TestSuite(ComponentAlignerTest.class);
	}

	public void testAddAll() throws Exception
	{
		ComponentAligner aligner = new ComponentAligner();

		JLabel label = new JLabel("Name:");

		JButton button = new JButton("Session Name:");

		JPanel panel = new JPanel(new BorderLayout());
		JComboBox comboBox = new JComboBox(new Object[] { "Item1", "Item2", "Item3 takes a lot more space" });
		comboBox.setSelectedIndex(2); // Last item
		panel.add(comboBox, BorderLayout.CENTER);

		Vector components = new Vector();
		components.add(label);
		components.add(button);
		components.add(panel);
		aligner.addAll(components);

		// Test 1
		int width = aligner.getMaximumWidth();

		int width1 = label.getPreferredSize().width;
		assertEquals(width, width1);

		int width2 = button.getPreferredSize().width;
		assertEquals(width, width2);

		int width3 = panel.getPreferredSize().width;
		assertEquals(width, width3);
	}

	public void testAddComponentAlignerToItself() throws Exception
	{
		try
		{
			ComponentAligner aligner = new ComponentAligner();
			aligner.add(aligner);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// We expected an exception
		}
	}

	public void testAlign1() throws Exception
	{
		ComponentAligner aligner = new ComponentAligner();

		JLabel label1 = new JLabel("Label1");
		aligner.add(label1);

		int width = aligner.getMaximumWidth();

		int width1 = label1.getPreferredSize().width;
		assertEquals(width, width1);
	}

	public void testAlign2() throws Exception
	{
		ComponentAligner aligner = new ComponentAligner();

		JLabel label1 = new JLabel("Label1");
		aligner.add(label1);

		JLabel label2 = new JLabel("Label2 is longer than Label1");
		aligner.add(label2);

		// Test 1
		int width = aligner.getMaximumWidth();

		int width1 = label1.getPreferredSize().width;
		assertEquals(width, width1);

		int width2 = label2.getPreferredSize().width;
		assertEquals(width, width2);

		// Test 2
		label2.setText("a");
		int realWidth = new JLabel(label2.getText()).getPreferredSize().width;
		
		width = aligner.getMaximumWidth();

		width1 = label1.getPreferredSize().width;
		assertEquals(width, width1);

		width2 = label2.getPreferredSize().width;
		assertEquals(width, width2);

		assertFalse(width == realWidth);
	}

	public void testAlign3() throws Exception
	{
		ComponentAligner aligner = new ComponentAligner();

		JLabel label = new JLabel("Name:");
		aligner.add(label);

		JButton button = new JButton("Session Name:");
		aligner.add(button);

		int width = aligner.getMaximumWidth();

		int width1 = label.getPreferredSize().width;
		assertEquals(width, width1);

		int width2 = button.getPreferredSize().width;
		assertEquals(width, width2);
	}

	public void testAlign4() throws Exception
	{
		ComponentAligner aligner = new ComponentAligner();

		JLabel label = new JLabel("Name:");
		aligner.add(label);

		JButton button = new JButton("Session Name:");
		aligner.add(button);

		JPanel panel = new JPanel(new BorderLayout());
		JComboBox comboBox = new JComboBox(new Object[] { "Item1", "Item2", "Item3 takes a lot more space" });
		comboBox.setSelectedIndex(2); // Last item
		panel.add(comboBox, BorderLayout.CENTER);
		aligner.add(panel);

		// Test 1
		int width = aligner.getMaximumWidth();

		int width1 = label.getPreferredSize().width;
		assertEquals(width, width1);

		int width2 = button.getPreferredSize().width;
		assertEquals(width, width2);

		int width3 = panel.getPreferredSize().width;
		assertEquals(width, width3);

		// Test 2
		aligner.remove(panel);

		int newWidth = aligner.getMaximumWidth();
		assertFalse(newWidth == width);

		int newWidth1 = label.getPreferredSize().width;
		assertFalse(width == newWidth1);
		assertEquals(newWidth, newWidth1);

		int newWidth2 = button.getPreferredSize().width;
		assertFalse(width == newWidth2);
		assertEquals(newWidth, newWidth2);

		// Test 3
		aligner.remove(button);
		aligner.remove(label);
		assertEquals(aligner.getMaximumWidth(), -1);
	}

	public void testAlignAutoValidateFalse() throws Exception
	{
		ComponentAligner aligner = new ComponentAligner(false);

		JLabel label = new JLabel("Name:");
		int originalWidth1 = label.getPreferredSize().width;
		aligner.add(label);
		assertTrue(originalWidth1 == label.getPreferredSize().width);

		JButton button = new JButton("Session Name:");
		int originalWidth2 = button.getPreferredSize().width;
		aligner.add(button);
		assertTrue(originalWidth2 == button.getPreferredSize().width);

		JPanel panel = new JPanel(new BorderLayout());
		JComboBox comboBox = new JComboBox(new Object[] { "Item1", "Item2", "Item3 takes a lot more space" });
		comboBox.setSelectedIndex(2); // Last item
		panel.add(comboBox, BorderLayout.CENTER);
		int originalWidth3 = panel.getPreferredSize().width;
		aligner.add(panel);
		assertTrue(originalWidth3 == panel.getPreferredSize().width);

		// Test 1
		int maxWidth1 = aligner.getMaximumWidth();
		assertEquals(maxWidth1, -1);

		aligner.revalidatePreferredSize();

		int newMaxWidth = aligner.getMaximumWidth();
		assertFalse(maxWidth1 == newMaxWidth);

		int width1 = label.getPreferredSize().width;
		assertEquals(newMaxWidth, width1);
		assertFalse(originalWidth1 == width1);

		int width2 = button.getPreferredSize().width;
		assertEquals(newMaxWidth, width2);
		assertFalse(originalWidth2 == width2);

		int width3 = panel.getPreferredSize().width;
		assertEquals(newMaxWidth, width3);
		assertTrue(originalWidth3 == width3); // Since it's the widest component

		// Test 2
		aligner.remove(panel);

		int newMaxWidth2 = aligner.getMaximumWidth();
		assertTrue(newMaxWidth2 == newMaxWidth);

		int newWidth1 = label.getPreferredSize().width;
		assertEquals(width1, newWidth1);

		int newWidth2 = button.getPreferredSize().width;
		assertEquals(width2, newWidth2);

		int newWidth3 = panel.getPreferredSize().width;
		assertEquals(width3, newWidth3);

		aligner.revalidatePreferredSize();
		int newMaxWidth3 = aligner.getMaximumWidth();

		int newNewWidth1 = label.getPreferredSize().width;
		assertEquals(newMaxWidth3, newNewWidth1);

		int newNewWidth2 = button.getPreferredSize().width;
		assertEquals(newMaxWidth3, newNewWidth2);
	}

	public void testHierarchyOfComponentAligners() throws Exception
	{
		// Aligner1
		//  ^
		//  |-Aligner2
		//     ^
		//     |-Aligner3
		ComponentAligner aligner1 = new ComponentAligner();

		ComponentAligner aligner2 = new ComponentAligner();
		aligner1.add(aligner2);

		ComponentAligner aligner3 = new ComponentAligner();
		aligner2.add(aligner3);

		// Test 1
		JLabel label1 = new JLabel("This is a label widget");
		int labelWidth1 = label1.getPreferredSize().width;
		aligner3.add(label1);

		assertEquals(aligner3.getMaximumWidth(), labelWidth1);
		assertEquals(aligner2.getMaximumWidth(), labelWidth1);
		assertEquals(aligner1.getMaximumWidth(), labelWidth1);

		// Test 2
		JLabel label2 = new JLabel("ShortLabel");
		aligner2.add(label2);

		int newLabelWidth1 = label1.getPreferredSize().width;
		int newLabelWidth2 = label2.getPreferredSize().width;

		assertEquals(aligner3.getMaximumWidth(), aligner2.getMaximumWidth());
		assertEquals(aligner2.getMaximumWidth(), aligner1.getMaximumWidth());
		assertEquals(newLabelWidth1, newLabelWidth2);
		assertEquals(newLabelWidth1, aligner1.getMaximumWidth());

		// Test 3
		JLabel label3 = new JLabel("A very long label that takes a lot of horizontal space");
		aligner1.add(label3);

		newLabelWidth1 = label1.getPreferredSize().width;
		newLabelWidth2 = label2.getPreferredSize().width;
		int newLabelWidth3 = label3.getPreferredSize().width;

		assertEquals(aligner3.getMaximumWidth(), aligner2.getMaximumWidth());
		assertEquals(aligner2.getMaximumWidth(), aligner1.getMaximumWidth());
		assertEquals(newLabelWidth1, newLabelWidth2);
		assertEquals(newLabelWidth2, newLabelWidth3);
		assertEquals(newLabelWidth1, aligner1.getMaximumWidth());

		// Make sure all the locked are removed
		assertEquals(ClassTools.attemptToGetFieldValue(aligner1, "locked"), Boolean.FALSE);
		assertEquals(ClassTools.attemptToGetFieldValue(aligner2, "locked"), Boolean.FALSE);
		assertEquals(ClassTools.attemptToGetFieldValue(aligner3, "locked"), Boolean.FALSE);

		// Change the text of label2
		label2.setText("mm");

		newLabelWidth1 = label1.getPreferredSize().width;
		newLabelWidth2 = label2.getPreferredSize().width;
		newLabelWidth3 = label3.getPreferredSize().width;

		assertEquals(aligner3.getMaximumWidth(), aligner2.getMaximumWidth());
		assertEquals(aligner2.getMaximumWidth(), aligner1.getMaximumWidth());
		assertEquals(newLabelWidth1, newLabelWidth2);
		assertEquals(newLabelWidth2, newLabelWidth3);
		assertEquals(newLabelWidth1, aligner1.getMaximumWidth());

		assertEquals(ClassTools.attemptToGetFieldValue(aligner1, "locked"), Boolean.FALSE);
		assertEquals(ClassTools.attemptToGetFieldValue(aligner2, "locked"), Boolean.FALSE);
		assertEquals(ClassTools.attemptToGetFieldValue(aligner3, "locked"), Boolean.FALSE);

		// Change the text of label1
		label1.setText("a");
		int realWidth = new JLabel("a").getPreferredSize().width;

		newLabelWidth1 = label1.getPreferredSize().width;
		newLabelWidth2 = label2.getPreferredSize().width;
		newLabelWidth3 = label3.getPreferredSize().width;

		assertEquals(aligner3.getMaximumWidth(), aligner2.getMaximumWidth());
		assertEquals(aligner2.getMaximumWidth(), aligner1.getMaximumWidth());
		assertEquals(newLabelWidth1, newLabelWidth2);
		assertEquals(newLabelWidth2, newLabelWidth3);
		assertEquals(newLabelWidth1, aligner1.getMaximumWidth());
		assertFalse(newLabelWidth1 == realWidth);

		assertEquals(ClassTools.attemptToGetFieldValue(aligner1, "locked"), Boolean.FALSE);
		assertEquals(ClassTools.attemptToGetFieldValue(aligner2, "locked"), Boolean.FALSE);
		assertEquals(ClassTools.attemptToGetFieldValue(aligner3, "locked"), Boolean.FALSE);

		// Change the text of label1
		label1.setText("Yes another big long long text so that all the labels will have to take the size of this label to make sure ComponentAligner works correctly");
		realWidth = new JLabel(label1.getText()).getPreferredSize().width;

		newLabelWidth1 = label1.getPreferredSize().width;
		newLabelWidth2 = label2.getPreferredSize().width;
		newLabelWidth3 = label3.getPreferredSize().width;

		assertEquals(aligner3.getMaximumWidth(), aligner2.getMaximumWidth());
		assertEquals(aligner2.getMaximumWidth(), aligner1.getMaximumWidth());
		assertEquals(newLabelWidth1, newLabelWidth2);
		assertEquals(newLabelWidth2, newLabelWidth3);
		assertEquals(newLabelWidth1, aligner1.getMaximumWidth());
		assertEquals(newLabelWidth1, realWidth);

		assertEquals(ClassTools.attemptToGetFieldValue(aligner1, "locked"), Boolean.FALSE);
		assertEquals(ClassTools.attemptToGetFieldValue(aligner2, "locked"), Boolean.FALSE);
		assertEquals(ClassTools.attemptToGetFieldValue(aligner3, "locked"), Boolean.FALSE);
	}
}
