/*
 * Copyright (c) 2006, 2008, Oracle. All rights reserved.
 *
 * This software is the proprietary information of Oracle Corporation.
 * Use is subject to license terms.
 */
package org.eclipse.persistence.tools.workbench.uitools.swing;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import org.eclipse.persistence.tools.workbench.uitools.ComponentVisibilityEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport;

/**
 * This pane can be used to group some widgets together and allow the user to
 * expand or collapse in order to show or hide those widgets, respectively.
 *
 * @version 11.0.0
 * @since 11.0.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class ExpandablePane extends JPanel
{
	private Icon buttonCollapsedDisabledImage;
	private Icon buttonCollapsedFocusImage;
	private Icon buttonCollapsedImage;
	private Icon buttonCollapsedPressedImage;
	private String buttonCollapsedText;
	private Icon buttonExpandedDisabledImage;
	private Icon buttonExpandedFocusImage;
	private Icon buttonExpandedImage;
	private Icon buttonExpandedPressedImage;
	private String buttonExpandedText;
	private ChangeSupport changeSupport;
	private JToggleButton expandButton;
	private PropertyValueModel expandedHolder;

	/**
	 * This constant is used to tweak the painting of any components. If its
	 * boolean value associated with this property is true, then the component
	 * should paint as selected.
	 */
	String FAKE_FOCUS = "fakeFocus";

	/**
	 * Creates a new <code>ExpandablePanel</code>.
	 */
	public ExpandablePane(String buttonExpandedText,
	                      String buttonCollapsedText,
	                      Icon buttonExpandedImage,
	                      Icon buttonCollapsedImage,
	                      Icon buttonExpandedDisabledImage,
	                      Icon buttonCollapsedDisabledImage,
	                      Icon buttonExpandedFocusImage,
	                      Icon buttonCollapsedFocusImage,
	                      Icon buttonExpandedPressedImage,
	                      Icon buttonCollapsedPressedImage,
	                      JComponent internalPane,
	                      boolean expanded)
	{
		super(new GridBagLayout());

		initialize(buttonExpandedText,
		           buttonCollapsedText,
		           buttonExpandedImage,
		           buttonCollapsedImage,
                 buttonExpandedDisabledImage,
                 buttonCollapsedDisabledImage,
                 buttonExpandedFocusImage,
                 buttonCollapsedFocusImage,
                 buttonExpandedPressedImage,
                 buttonCollapsedPressedImage,
                 internalPane,
		           expanded);
	}

	/**
	 * Creates a new <code>ExpandablePanel</code>.
	 */
	public ExpandablePane(String buttonExpandedText,
	                      String buttonCollapsedText,
	                      Icon buttonExpandedImage,
	                      Icon buttonCollapsedImage,
	                      JComponent internalPane,
	                      boolean expanded)
	{
		this(buttonExpandedText,
		     buttonCollapsedText,
		     buttonExpandedImage,
		     buttonCollapsedImage,
		     null,
		     null,
		     null,
		     null,
		     null,
		     null,
		     internalPane,
		     true);
	}

	/**
	 * Creates a new <code>ExpandablePanel</code>. The internal pane will be
	 * shown by default.
	 *
	 * @param buttonExpandedText The text of the toggle button when the button is
	 * selected
	 * @param buttonCollapsedText The text of the toggle button when the button
	 * is unselected
	 * @param internalPane The pane that will be shown when the button is set as
	 * expanded or hidden when the button is set as collapsed
	 */
	public ExpandablePane(String buttonExpandedText,
	                      String buttonCollapsedText,
	                      JComponent internalPane)
	{
		this(buttonExpandedText,
		     buttonCollapsedText,
		     null,
		     null,
		     null,
		     null,
		     null,
		     null,
		     null,
		     null,
		     internalPane,
		     true);
	}

	/**
	 * Creates a new <code>ExpandablePanel</code>.
	 *
	 * @param buttonExpandedText The text of the toggle button when the button is
	 * selected
	 * @param buttonCollapsedText The text of the toggle button when the button
	 * is unselected
	 * @param internalPane The pane that will be shown when the button is set as
	 * expanded or hidden when the button is set as collapsed
	 * @param expanded <code>true</code> if the internal pane should be shown or
	 * <code>false</code> to hide it
	 */
	public ExpandablePane(String buttonExpandedText,
	                      String buttonCollapsedText,
	                      JComponent internalPane,
	                      boolean expanded)
	{
		this(buttonExpandedText,
		     buttonCollapsedText,
		     null,
		     null,
		     internalPane,
		     expanded);
	}

	/**
	 * Registers the given listener to be notified when the internal pane is
	 * either shown or hidden.
	 *
	 * @param listener The <code>PropertyChangeListener</code> receiving the new
	 * state of the expansion, which is <code>Boolean</code> value
	 */
	public void addExpansionPropertyChangeListener(PropertyChangeListener listener)
	{
		changeSupport.addPropertyChangeListener(ValueModel.VALUE, listener);
	}

	private ActionListener buildActionListener()
	{
		return new ActionListener() { public void actionPerformed(ActionEvent e)
		{
			expandedHolder.setValue(!((Boolean)expandedHolder.getValue()).booleanValue());
			updateExpandButtonText();
			updatePressedIcon();
			changeSupport.firePropertyChanged(ValueModel.VALUE, !((Boolean)expandedHolder.getValue()).booleanValue(), ((Boolean)expandedHolder.getValue()).booleanValue());
		}};
	}

	private void initialize(String buttonExpandedText,
		                     String buttonCollapsedText,
		                     Icon buttonExpandedImage,
		                     Icon buttonCollapsedImage,
		                     Icon buttonExpandedDisabledImage,
		                     Icon buttonCollapsedDisabledImage,
		                     Icon buttonExpandedFocusImage,
		                     Icon buttonCollapsedFocusImage,
		                     Icon buttonExpandedPressedImage,
		                     Icon buttonCollapsedPressedImage,
		                     JComponent internalPane,
		                     boolean expanded)
	{
		if (internalPane == null) {
			throw new IllegalStateException("The internal panel cannot be null");
		}

		this.buttonExpandedText           = buttonExpandedText;
		this.buttonCollapsedText          = buttonCollapsedText;
		this.buttonExpandedImage          = buttonExpandedImage;
		this.buttonCollapsedImage         = buttonCollapsedImage;
		this.buttonExpandedDisabledImage  = buttonExpandedDisabledImage;
		this.buttonCollapsedDisabledImage = buttonCollapsedDisabledImage;
		this.buttonExpandedFocusImage     = buttonExpandedFocusImage;
		this.buttonCollapsedFocusImage    = buttonCollapsedFocusImage;
		this.buttonExpandedPressedImage   = buttonExpandedPressedImage;
		this.buttonCollapsedPressedImage  = buttonCollapsedPressedImage;

		changeSupport = new ChangeSupport(this);
		expandedHolder = new SimplePropertyValueModel(expanded);
		initializeLayout(internalPane);
		new ComponentVisibilityEnabler(expandedHolder, internalPane);

		setExpanded(expanded);
		setOpaque(false);
	}

	/**
	 * Initializes the layout of this pane.
	 *
	 * @param expanded <code>true</code> if the internal pane should be shown or
	 * <code>false</code> to hide it
	 * @param internalPane The pane that will be shown when the button is set as
	 * expanded or hidden when the button is set as collapsed
	 */
	private void initializeLayout(JComponent internalPane)
	{
		GridBagConstraints constraints = new GridBagConstraints();
		boolean expanded = isExpanded();

		// Expandable button
		expandButton = new ToggleButton();
		expandButton.setBorderPainted(false);
		expandButton.setContentAreaFilled(false);
		expandButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		expandButton.setSelected(expanded);
		expandButton.addActionListener(buildActionListener());
		expandButton.putClientProperty(FAKE_FOCUS, false);

		Insets margin = expandButton.getInsets();
		margin.left = margin.right = 4;
		expandButton.setMargin(margin);

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		add(expandButton, constraints);
		updateExpandButtonIcon();
		updateExpandButtonText();

		// Internal pane
		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.BOTH;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 0, 0, 0);

		add(internalPane, constraints);
	}

	/**
	 * Determines if the internal pane is shown.
	 *
	 * @return <code>true</code> if the toggle button is selected and the
	 * internal pane is visible or <code>false</code> if the internal pane is
	 * hidden
	 */
	public boolean isExpanded()
	{
		return ((Boolean)expandedHolder.getValue()).booleanValue();
	}

	/**
	 * Removes the given listener from being notified when the internal pane is
	 * either shown or hidden.
	 *
	 * @param listener The <code>PropertyChangeListener</code> to be deregistered
	 */
	public void removeExpansionPropertyChangeListener(PropertyChangeListener listener)
	{
		changeSupport.removePropertyChangeListener(ValueModel.VALUE, listener);
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		expandButton.setEnabled(enabled);
	}

	/**
	 * Sets whether the internal pane should be shown or hidden and the toggle
	 * button to be selected or unselected.
	 *
	 * @param expanded <code>true</code> to select the toggle button and for the
	 * internal pane to be visible or <code>false</code> unselect the toggle
	 * button and to hide the internal pane
	 */
	public void setExpanded(boolean expanded)
	{
		expandButton.setSelected(expanded);
	}

	private void updateExpandButtonIcon()
	{
		expandButton.setIcon(buttonExpandedImage);
		expandButton.setSelectedIcon(buttonCollapsedImage);
		expandButton.setPressedIcon(buttonExpandedPressedImage);

		expandButton.setDisabledIcon(buttonExpandedDisabledImage);
		expandButton.setDisabledSelectedIcon(buttonCollapsedDisabledImage);

		expandButton.setRolloverIcon(buttonExpandedFocusImage);
		expandButton.setRolloverSelectedIcon(buttonCollapsedFocusImage);
	}

	private void updateExpandButtonText()
	{
		expandButton.setText(isExpanded() ? buttonCollapsedText : buttonExpandedText);
	}

	private void updatePressedIcon()
	{
		if (expandButton.isSelected())
		{
			expandButton.setPressedIcon(buttonCollapsedPressedImage);
		}
		else
		{
			expandButton.setPressedIcon(buttonExpandedPressedImage);
		}
	}

	/**
	 * This extension of the Swing's toggle button simply updates the cursor.
	 */
	private static class ToggleButton extends JToggleButton
	{
		private final Rectangle iconRect;
		private final Rectangle textRect;
		private final Rectangle viewRect;

		/**
		 * Creates a new <code>ToggleButton</code>.
		 */
		ToggleButton()
		{
			super();
			viewRect = new Rectangle();
			textRect = new Rectangle();
			iconRect = new Rectangle();
		}

		/*
		 * (non-Javadoc)
		 */
		@Override
		public void paint(Graphics g)
		{
			super.paint(g);

			if (isEnabled())
			{
				paintUnderline(g);
			}
		}

		private void paintUnderline(Graphics graphics)
		{
			Insets insets = getInsets();
			viewRect.x = insets.left;
			viewRect.y = insets.top;
			viewRect.width = getWidth() - (insets.right + viewRect.x);
			viewRect.height = getHeight() - (insets.bottom + viewRect.y);

			textRect.x = textRect.y = textRect.width = textRect.height = 0;
			iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;

			SwingUtilities.layoutCompoundLabel
			(
				this,
				getFontMetrics(getFont()),
				getText(),
				getIcon(),
				getVerticalAlignment(),
				getHorizontalAlignment(),
				getVerticalTextPosition(),
				getHorizontalTextPosition(),
				viewRect,
				iconRect,
				textRect,
				getText() == null ? 0 : getIconTextGap()
			);

			graphics.setColor(getForeground());
			graphics.drawLine(textRect.x, textRect.y + textRect.height - 2, textRect.x + textRect.width, textRect.y + textRect.height - 2);
		}

		/*
		 * (non-Javadoc)
		 */
		@Override
		public void setEnabled(boolean enabled)
		{
			super.setEnabled(enabled);

			if (enabled)
			{
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
			else
			{
				setCursor(Cursor.getDefaultCursor());
			}
		}
	}
}