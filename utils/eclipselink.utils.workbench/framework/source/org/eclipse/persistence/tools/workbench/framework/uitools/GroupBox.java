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
package org.eclipse.persistence.tools.workbench.framework.uitools;

// JDK
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import javax.accessibility.AccessibleContext;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

/**
 * A <code>GroupBox</code> properly lays out a series of buttons (radio buttons
 * or check boxes) with a series of panes. When there is a pane associated with
 * a button, the button acts as its title.
 * <p>
 * Here the layout:<pre>
 * __________________________________
 * |                                |
 * |   o Button 1                   |
 * |   o Button 2                   |
 * |   o Button 3                   |
 * |   o ...                        |
 * | _ o Button n-1 _______________ |
 * | |                            | |
 * | |  Sub-pane n-1              | |
 * | |                            | |
 * | ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ |
 * | _ o Button n _________________ |
 * | |                            | |
 * | |  Sub-pane n                | |
 * | |                            | |
 * | ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯</pre>
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public class GroupBox extends AccessibleTitledPanel
{
	/**
	 * Internal flag used to determine if a pane needs to take the remaining
	 * vertical space or not.
	 *
	 * @see #fillVertical(JComponent)
	 */
	private static final String FILL_VERTICAL = "fillVertical";

	/**
	 * Used to specify no pane is associated with a button.
	 */
	public static final JComponent NO_PANE = new JComponent() {};

	/**
	 * Creates a new <code>GroupBox</code>.
	 */
	private GroupBox()
	{
		super(new GridBagLayout());
	}

	/**
	 * Creates a new <code>GroupBox</code>. The layout will be the following:<pre>
	 * __________________________________
	 * |                                |
	 * |   o Button 1                   |
	 * |   o Button 2                   |
	 * | _ o Button 3 _________________ |
	 * | |                            | |
	 * | |  Sub-pane                  | |
	 * | |                            | |
	 * | ------------------------------ |
	 * ----------------------------------</pre>
	 *
	 * @param button1 The first button to be shown at the top-left section of the
	 * group box
	 * @param button2 The second button to be shown at the top-left section of
	 * the group box
	 * @param button3 The third button to be shown at the top-left section of the
	 * group box, which will be the group box's title
	 * @param pane The pane to be shown as a group where the last button in the
	 * given list is its title
	 */
	public GroupBox(AbstractButton button1,
						 AbstractButton button2,
						 AbstractButton button3,
						 JComponent pane)
	{
		this(new AbstractButton[] { button1, button2, button3 },
			  new JComponent[]     { NO_PANE, NO_PANE, pane });
	}

	/**
	 * Creates a new <code>GroupBox</code>. The layout will be the following:<pre>
	 * __________________________________
	 * |                                |
	 * |   o Button 1                   |
	 * | _ o Button 2 _________________ |
	 * | |                            | |
	 * | |  Sub-pane                  | |
	 * | |                            | |
	 * | ------------------------------ |
	 * ----------------------------------</pre>
	 *
	 * @param button1 The first button to be shown at the top-left section of the
	 * group box
	 * @param button2 The second button to be shown at the top-left section of
	 * the group box, which will be the group box's title
	 * @param pane The pane to be shown as a group where the last button in the
	 * given list is its title
	 */
	public GroupBox(AbstractButton button1,
						 AbstractButton button2,
						 JComponent pane)
	{
		this(new AbstractButton[] { button1, button2, },
			  new JComponent[]     { NO_PANE, pane });
	}

	/**
	 * Creates a new <code>GroupBox</code>. The layout will be the following:<pre>
	 * __________________________________
	 * |                                |
	 * | _ o Button ___________________ |
	 * | |                            | |
	 * | |  Sub-pane                  | |
	 * | |                            | |
	 * | ------------------------------ |
	 * ----------------------------------</pre>
	 *
	 * @param button The button to be shown at the top-left section of the group
	 * box
	 * @param pane The pane to be shown as a group where the last button in the
	 * given list is its title
	 */
	public GroupBox(AbstractButton button,
						 JComponent pane)
	{
		this(new AbstractButton[] { button },
			  new JComponent[]     { pane   });
	}

	/**
	 * Creates a new <code>GroupBox</code>. The layout will be the following:<pre>
	 * __________________________________
	 * |                                |
	 * | _ o Button 1 _________________ |
	 * | |                            | |
	 * | |  Sub-pane 1                | |
	 * | |                            | |
	 * | ------------------------------ |
	 * | _ o Button 2 _________________ |
	 * | |                            | |
	 * | |  Sub-pane 2                | |
	 * | |                            | |
	 * | ------------------------------ |
	 * ----------------------------------</pre>
	 *
	 * @param button1 The first button to be shown at the top-left section of the
	 * group box
	 * @param button2 The second button to be shown at the top-left section of
	 * the group box, which will be the group box's title
	 * @param pane The pane to be shown as a group where the last button in the
	 * given list is its title
	 */
	public GroupBox(AbstractButton button1,
						 JComponent pane1,
						 AbstractButton button2,
						 JComponent pane2)
	{
		this(new AbstractButton[] { button1, button2, },
			  new JComponent[]     { pane1,   pane2 });
	}

	/**
	 * Creates a new <code>GroupBox</code>. The layout will be the following:<pre>
	 * __________________________________
	 * |                                |
	 * |   o Button 1                   |
	 * |   o Button 2                   |
	 * |   o Button 3                   |
	 * |   o ...                        |
	 * | _ o Button n _________________ |
	 * | |                            | |
	 * | |  Sub-pane                  | |
	 * | |                            | |
	 * | ------------------------------ |
	 * ----------------------------------</pre>
	 *
	 * @param buttons The buttons to be shown at the top-left section of
	 * the group box
	 * @param pane The pane to be shown as a group where the last button in the
	 * given list is its title
	 */
	public GroupBox(AbstractButton[] buttons,
						 JComponent pane)
	{
		this(buttons, componentArray(buttons.length, pane));
	}

	/**
	 * Creates a new <code>GroupBox</code>. The count of buttons can be equals or
	 * greater than the count of panes. The layout will be the following:<pre>
	 * __________________________________
	 * |                                |
	 * |   o Button 1                   |
	 * |   o Button 2                   |
	 * |   o Button 3                   |
	 * |   o ...                        |
	 * | _ o Button n-1 _______________ |
	 * | |                            | |
	 * | |  Sub-pane n-1              | |
	 * | |                            | |
	 * | ------------------------------ |
	 * | _ o Button n _________________ |
	 * | |                            | |
	 * | |  Sub-pane n                | |
	 * | |                            | |
	 * | ------------------------------ |
	 * ----------------------------------</pre>

	 *
	 * @param buttons The buttons to be shown at the top-left section of
	 * the group box
	 * @param panes The panes to be shown as a group under a button
	 */
	public GroupBox(AbstractButton[] buttons,
						 JComponent[] panes)
	{
		this();
		initializeLayout(buttons, panes);
	}

	/**
	 * Creates a new array of <code>JComponent</code>s that has the given length
	 * and sets the last item in the array to be the given pane.
	 *
	 * @param length The length of the array to create
	 * @param pane The pane to be set in the array as the last item
	 * @return A new array
	 */
	private static JComponent[] componentArray(int length,
															 JComponent pane)
	{
		JComponent[] components = new JComponent[length];
		Arrays.fill(components, NO_PANE);
		components[length - 1] = pane;
		return components;
	}

	/**
	 * Specify that the given component will take the remaining vertical space
	 * while creating the layout of this <code>GroupBox</code>.
	 *
	 * @param component One of the child of this <code>GroupBox</code>, which
	 * needs the vertical space
	 */
	public static void fillVertical(JComponent component)
	{
		component.putClientProperty(FILL_VERTICAL, Boolean.TRUE);
	}

	/**
	 * This is required in order to repaint the button that is over a pane,
	 * otherwise it's possible the pane will be painted over the button.
	 */
	private ItemListener buildRepainterHandler()
	{
		return new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				AbstractButton button = (AbstractButton) e.getSource();
				button.repaint();
			}
		};
	}

	/**
	 * Checks the integrity of the given array and throw an exception if
	 * something is wrong.
	 *
	 * @param buttons The array of buttons to test
	 * @param panes The array of panes to test
	 */
	private void checkIntegrity(AbstractButton[] buttons,
										 JComponent[] panes)
	{
		if (buttons.length == 0)
			throw new NullPointerException("At least one button has to be used to create a GroupBox");

		if (panes.length == 0)
			throw new NullPointerException("At least one pane has to be used to create a GroupBox");

		if (buttons.length != panes.length)
			throw new NullPointerException("The count of panes and buttons has to be the same, if no pane is associated with a button, null as to be set as for the pane");
	}

   /**
	 * Creates the titled border that will be used for a pane where the title
	 * will be replaced by a button.
	 *
	 * @param button The button is required to calculate the space required to
	 * prevent overlapping from the button (which is the title of the group) and
	 * the first component
	 */
	private Border createPaneBorder(AbstractButton button, JComponent pane)
	{
		return BorderFactory.createCompoundBorder
		(
			new AccessibleTitledBorder(button),
			BorderFactory.createEmptyBorder(0, 5, 5, 5)
		);
	}

	/**
	 * Returns the <code>AccessibleContext</code> associated with this
	 * <code>GroupBox</code>. For <code>GroupBox</code>s, the <code>AccessibleContext</code>
	 * takes the form of an <code>AccessibleGroupBox</code>. A new
	 * <code>AccessibleGroupBox</code> instance is created if necessary.
	 * 
	 * @return An <code>AccessibleGroupBox</code> that serves as the
	 * <code>AccessibleContext</code> of this <code>GroupBox</code>
	 */
	public AccessibleContext getAccessibleContext()
	{
		if (accessibleContext == null)
			accessibleContext = new AccessibleGroupBox();

		return accessibleContext;
	}

	/**
	 * Initializes the layout of this <code>GroupBox</code>.
	 *
	 * @param buttons The buttons to be shown at the top-left section of
	 * the group box
	 * @param panes The panes to be shown as a group where a button is shown as
	 * a pane's title
	 */
	protected void initializeLayout(AbstractButton[] buttons,
											  JComponent[] panes)
	{
		checkIntegrity(buttons, panes);

		GridBagConstraints constraints = new GridBagConstraints();

		for (int index = 0; index < buttons.length; index++)
		{
			boolean paneWasAddedBefore = ((index > 0) && (panes[index - 1] != NO_PANE));

			// First add the button
			AbstractButton button = buttons[index];
			button.setOpaque(false); // Requires to paint the titled border properly
			button.setBorder(BorderFactory.createCompoundBorder
			(
				BorderFactory.createEmptyBorder(0, 5, 0, 0),
				button.getBorder())
			);

			if (panes[index] != NO_PANE)
			{
				button.addItemListener(buildRepainterHandler());
			}

			constraints.gridx       = 0;
			constraints.gridy       = index;
			constraints.gridwidth   = 1;
			constraints.gridheight  = 1;
			constraints.weightx     = 0;
			constraints.weighty     = 0;
			constraints.fill        = GridBagConstraints.NONE;
			constraints.anchor      = GridBagConstraints.FIRST_LINE_START;
			constraints.insets      = new Insets(paneWasAddedBefore ? 5 : 0, 0, 0, 0);

			add(button, constraints);

			// Now add the pane if one is associated with the button
			JComponent pane = panes[index];

			if (pane != NO_PANE)
			{
				boolean fillVertical = shouldFillVertical(pane);
				int top = button.getPreferredSize().height / 2 - 6;

				pane.setBorder(BorderFactory.createCompoundBorder
				(
					createPaneBorder(button, pane),
					pane.getBorder())
				);

				constraints.gridx       = 0;
				constraints.gridy       = index;
				constraints.gridwidth   = 1;
				constraints.gridheight  = 1;
				constraints.weightx     = 1;
				constraints.weighty     = fillVertical ? 1 : 0;
				constraints.fill        = fillVertical ? GridBagConstraints.BOTH : GridBagConstraints.HORIZONTAL;
				constraints.anchor      = GridBagConstraints.CENTER;
				constraints.insets      = new Insets(paneWasAddedBefore ? top + 5 : top, 0, 0, 0);

				add(pane, constraints);
			}
		}
	}

	/**
	 * Determines whether the given component should take the remaining of the
	 * vertical space when initializing the layout.
	 *
	 * @param component The component to test for "fill vertical" property
	 * @return <code>true<code> if it needs to fill the vertical space;
	 * <code>false<code> to only have it uses its height and no more
	 */
	private boolean shouldFillVertical(JComponent component)
	{
		Boolean fillVertical = (Boolean) component.getClientProperty(FILL_VERTICAL);
		return (fillVertical != null) ? fillVertical.booleanValue() : false;
	}

	/**
	 * The <code>Accessible</code> for this <code>GroupBox</code>.
	 */
	protected class AccessibleGroupBox extends AccessibleAccessibleTitledPane
	{
	}

	/**
	 * This <code>TitledBorder</code> tweaks the border to make it work at the
	 * same time for JAWS and for focus traversal. Basically, no title makes the
	 * focus traversal works and a title makes JAWS works (JAWS says the title
	 * when describing a pane's widget).
	 */
	private static class AccessibleTitledBorder extends TitledBorder
	{
		private AbstractButton button;

		private AccessibleTitledBorder(AbstractButton button)
		{
			super(button.getText());
			initialize(button);
		}
		
		private void initialize(AbstractButton button) {
			PropertyChangeListener listener = buildPropertyChangeListener();
			this.button = button;
			this.button.addPropertyChangeListener("font", listener);
			this.button.addPropertyChangeListener(AbstractButton.TEXT_CHANGED_PROPERTY, listener);
		}
				
		private String buildWhitespaceTitle(AbstractButton button) {
			String text = button.getText();
			
			if (text == null) {
				text = StringTools.EMPTY_STRING;
			}
			
			FontMetrics fontMetrics = button.getFontMetrics(button.getFont());
			
			// Note:  It seems using the icon gap and not doing -5 
			// make the border start too far to the right of the text
			int width = fontMetrics.stringWidth(text) - 5;
			int charWidth = fontMetrics.charWidth(' ');
			
			if (button instanceof JRadioButton) {
				width += SwingTools.radioButtonIconWidth();
			} else if (button instanceof JCheckBox) {
				width += SwingTools.checkBoxIconWidth();
			}
			
			StringBuilder sb = new StringBuilder();
			int index = 0;
			
			while (index < width) {
				sb.append(' ');
				index += charWidth;
			}
			
			return sb.toString();
		}
		
		@Override
		public int getBaseline(Component c, int width, int height) {
			String oldText = getTitle();
			setTitle(buildWhitespaceTitle(button));
			setTitleFont(button.getFont());
			
			try {
				return super.getBaseline(c, width, height);
			} finally {
				setTitle(oldText);
			}
		}
		
		@Override
		public BaselineResizeBehavior getBaselineResizeBehavior(Component c) {
			String oldText = getTitle();
			setTitle(buildWhitespaceTitle(button));
			setTitleFont(button.getFont());
			
			try {		
				return super.getBaselineResizeBehavior(c);
			} finally {
				setTitle(oldText);
			}
		}
		
		@Override
		public Insets getBorderInsets(Component c, Insets insets) {
			String oldText = getTitle();
			setTitle(buildWhitespaceTitle(button));
			setTitleFont(button.getFont());
			
			try {
				return super.getBorderInsets(c, insets);
			} finally {
				setTitle(oldText);
			}
		}
		
		@Override
		public Insets getBorderInsets(Component c) {
			String oldText = getTitle();
			setTitle(buildWhitespaceTitle(button));
			setTitleFont(button.getFont());
			
			try {
				return super.getBorderInsets(c);
			} finally {
				setTitle(oldText);
			}
		}
		
		@Override
		public Rectangle getInteriorRectangle(Component c, int x, int y,
				int width, int height) {
			String oldText = getTitle();
			setTitle(buildWhitespaceTitle(button));
			setTitleFont(button.getFont());
			try {
				return super.getInteriorRectangle(c, x, y, width, height);
			} finally {
				setTitle(oldText);
			}
		}
		
		@Override
		public Dimension getMinimumSize(Component c) {
			String oldText = getTitle();
			setTitle(buildWhitespaceTitle(button));
			setTitleFont(button.getFont());
			
			try {
				return super.getMinimumSize(c);
			} finally {
				setTitle(oldText);
			}
		}
		
		@Override
		public void paintBorder(Component c, Graphics g, int x, int y,
				int width, int height) {
			String oldText = getTitle();
			setTitle(buildWhitespaceTitle(button));
			setTitleFont(button.getFont());
			
			try {
				super.paintBorder(c, g, x, y, width, height);
			} finally {
				setTitle(oldText);
			}
		}
		
		private PropertyChangeListener buildPropertyChangeListener() {
			return new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					AbstractButton button = (AbstractButton)evt.getSource();
					setTitleFont(button.getFont());
					setTitle(buildWhitespaceTitle(button));
				}
			};
		}
	}
}
