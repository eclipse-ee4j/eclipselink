/*
 * Copyright (c) 2006, 2007, Oracle. All rights reserved.
 *
 * This software is the proprietary information of Oracle Corporation.
 * Use is subject to license terms.
 */
package org.eclipse.persistence.tools.workbench.uitools.swing;

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeListener;

import org.eclipse.persistence.tools.workbench.utility.TriStateBoolean;

/**
 * This extension over the Swing's <code>JCheckBox</code> adds support for a
 * partially selected state.
 * <p>
 * This code was found at: <a
 * href="http://forum.java.sun.com/thread.jspa?threadID=593755&messageID=3116647">http://forum.java.sun.com/thread.jspa?threadID=593755&messageID=3116647</a>
 * <p>
 * The Sun's bug number is 4079882: <a
 * href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4079882">http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4079882</a>
 *
 * @version 11.0.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public class TriStateCheckBox extends JCheckBox
{
	/**
	 * Cache the check icon in order to compose it with a secondary icon.
	 */
	private Icon checkIcon;

	/**
	 * Constants used to determine to set the selection state as partially
	 * selected.
	 */
	public static TriStateBoolean PARTIALLY_SELECTED = TriStateBoolean.UNDEFINED;

	/**
	 * Constants used to determine to set the selection state as selected.
	 */
	public static TriStateBoolean SELECTED = TriStateBoolean.TRUE;

	/**
	 * Constants used to determine to set the selection state as partially
	 * selected.
	 */
	public static TriStateBoolean UNSELECTED = TriStateBoolean.FALSE;

	/*
	 * @see JCheckBox()
	 */
	public TriStateCheckBox()
	{
		this(null);
	}

	/*
	 * @see JCheckBox(String)
	 */
	public TriStateCheckBox(String text)
	{
		this(text, UNSELECTED);
	}

	/*
	 * @see JCheckBox(String, Icon, boolean)
	 */
	public TriStateCheckBox(String text, Icon icon, TriStateBoolean selectedState)
	{
		super(text, icon);
		initialize(selectedState);
	}

	/*
	 * @see JCheckBox(String, boolean)
	 */
	public TriStateCheckBox(String text, TriStateBoolean selectedState)
	{
		this(text, null, selectedState);
	}

	/**
	 * Returns the current state, which is determined by the selection status of
	 * the model.
	 */
	public TriStateBoolean getState()
	{
		return getTriStateModel().getState();
	}

	/**
	 * Returns this button's model.
	 * <p>
	 * <b>Note:</b> {@link javax.swing.AbstractButton#getModel()} is not
	 * overriden just in case the UI delegate calls it before we reset the model.
	 *
	 * @return This button's model
	 */
	public TriStateButtonModel getTriStateModel()
	{
		return (TriStateButtonModel) super.getModel();
	}

	/**
	 * Initializes the model and icon to support the partially selected state.
	 *
	 * @param selectedState The initial selection state
	 */
	protected void initialize(TriStateBoolean selectedState)
	{
		setOpaque(false);

		// Install the tri-state button model
		setModel(new TriStateButtonModel(getModel()));
		setState(selectedState);

	}

	/**
	 * Determines whether the selection state is set to be partially selected.
	 *
	 * @return <code>true</code> if the selection is set as partially selected or
	 * <code>false</code> if it is set as unselected or selected
	 */
	public boolean isPartiallySelected()
	{
		return getTriStateModel().isPartiallySelected();
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	public void setIcon(Icon icon)
	{
		setSecondaryIcon(icon);
	}

	/**
	 * Sets the secondary icon, which is shown after the check icon.
	 *
	 * @param icon The secondary icon or <code>null</code> to clear a previously
	 * set secondary icon
	 */
	public void setSecondaryIcon(Icon icon)
	{
		if (icon == null)
		{
			super.setIcon(checkIcon);
		}
		else
		{
			super.setIcon(new CompositeIcon(checkIcon, icon));
		}
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	public void setSelected(boolean selected)
	{
		setState(selected ? SELECTED : UNSELECTED);
	}

	/**
	 * Sets the new state to either {@link #SELECTED}, {@link #UNSELECTED} or
	 * {@link #PARTIALLY_SELECTED}. If <code>null</code>, then it is treated as
	 * {@link #PARTIALLY_SELECTED}.
	 *
	 * @param state The new selection state
	 */
	public void setState(TriStateBoolean state)
	{
		getTriStateModel().setState(state);
	}

	/**
	 * Exactly which Design Pattern is this? Is it an Adapter, a Proxy or a
	 * Decorator? In this case, my vote lies with the Decorator, because we are
	 * extending functionality and "decorating" the original model with a more
	 * powerful model.
	 */
	public static class TriStateButtonModel implements ButtonModel
	{
		/**
		 * The wrapped <code>ButtonModel</code> set by the UI delegate.
		 */
		private final ButtonModel delegate;

		/**
		 * The selection state supporting three states: selected, partially
		 * selected or unselected.
		 */
		private TriStateBoolean selectionState;

		/**
		 * Creates a new <code>TriStateButtonModel</code>.
		 *
		 * @param delegate The wrapped <code>ButtonModel</code> set by the UI
		 * delegate
		 */
		public TriStateButtonModel(ButtonModel delegate)
		{
			super();
			this.delegate = delegate;
			this.selectionState = TriStateBoolean.valueOf(delegate.isSelected());
		}

		/*
		 * (non-Javadoc)
		 */
		public void addActionListener(ActionListener listener)
		{
			delegate.addActionListener(listener);
		}

		/*
		 * (non-Javadoc)
		 */
		public void addChangeListener(ChangeListener listener)
		{
			delegate.addChangeListener(listener);
		}

		/*
		 * (non-Javadoc)
		 */
		public void addItemListener(ItemListener listener)
		{
			delegate.addItemListener(listener);
		}

		/*
		 * (non-Javadoc)
		 */
		public String getActionCommand()
		{
			return delegate.getActionCommand();
		}

		/**
		 * Returns the wrapped <code>ButtonModel</code> set by the UI delegate.
		 *
		 * @return The model used to store the actual properties
		 */
		protected final ButtonModel getDelegate()
		{
			return delegate;
		}

		/*
		 * (non-Javadoc)
		 */
		public int getMnemonic()
		{
			return delegate.getMnemonic();
		}

		/*
		 * (non-Javadoc)
		 */
		public Object[] getSelectedObjects()
		{
			return delegate.getSelectedObjects();
		}

		/**
		 * Returns the current selection state.
		 *
		 * @return One of the three possible selection states
		 */
		protected TriStateBoolean getState()
		{
			return selectionState;
		}

		/*
		 * (non-Javadoc)
		 */
		public boolean isArmed()
		{
			return delegate.isArmed();
		}

		/*
		 * (non-Javadoc)
		 */
		public boolean isEnabled()
		{
			return delegate.isEnabled();
		}

		/**
		 * Determines whether the selection state is set to be partially selected.
		 *
		 * @return <code>true</code> if the selection is set as partially selected or
		 * <code>false</code> if it is set as unselected or selected
		 */
		public boolean isPartiallySelected()
		{
			return getState() == PARTIALLY_SELECTED;
		}

		/*
		 * (non-Javadoc)
		 */
		public boolean isPressed()
		{
			return delegate.isPressed();
		}

		/*
		 * (non-Javadoc)
		 */
		public boolean isRollover()
		{
			return delegate.isRollover();
		}

		/*
		 * (non-Javadoc)
		 */
		public boolean isSelected()
		{
			return delegate.isSelected();
		}

		/**
		 * Rotates between {@link TriStateCheckBox#PARTIALLY_SELECTED},
		 * {@link TriStateCheckBox#SELECTED} and {@link TriStateCheckBox#UNSELECTED}.
		 */
		protected void nextState()
		{
			TriStateBoolean current = getState();

			if (current == UNSELECTED)
			{
				setState(SELECTED);
			}
			else if (current == SELECTED)
			{
				setState(PARTIALLY_SELECTED);
			}
			else if (current == PARTIALLY_SELECTED)
			{
				setState(UNSELECTED);
			}
		}

		/*
		 * (non-Javadoc)
		 */
		public void removeActionListener(ActionListener listener)
		{
			delegate.removeActionListener(listener);
		}

		/*
		 * (non-Javadoc)
		 */
		public void removeChangeListener(ChangeListener listener)
		{
			delegate.removeChangeListener(listener);
		}

		/*
		 * (non-Javadoc)
		 */
		public void removeItemListener(ItemListener listener)
		{
			delegate.removeItemListener(listener);
		}

		/*
		 * (non-Javadoc)
		 */
		public void setActionCommand(String actionCommand)
		{
			delegate.setActionCommand(actionCommand);
		}

		/*
		 * (non-Javadoc)
		 */
		public void setArmed(boolean armed)
		{
			delegate.setArmed(armed);
		}

		/*
		 * (non-Javadoc)
		 */
		public void setEnabled(boolean enabled)
		{
			delegate.setEnabled(enabled);
		}

		/*
		 * (non-Javadoc)
		 */
		public void setGroup(ButtonGroup group)
		{
			delegate.setGroup(group);
		}

		/*
		 * (non-Javadoc)
		 */
		public void setMnemonic(int mnemonic)
		{
			delegate.setMnemonic(mnemonic);
		}

		/*
		 * (non-Javadoc)
		 */
		public void setPressed(boolean pressed)
		{
			if ((isPressed() != pressed) && isEnabled())
			{
				if (!pressed && isArmed())
				{
					nextState();
				}

				// The temporary selected flag prevents the UI from showing the
				// partially selected state as selected
				boolean selected = isSelected();
				delegate.setPressed(pressed);
				delegate.setSelected(selected);
			}
		}

		/*
		 * (non-Javadoc)
		 */
		public void setRollover(boolean rollover)
		{
			delegate.setRollover(rollover);
		}

		/*
		 * (non-Javadoc)
		 */
		public void setSelected(boolean selected)
		{
			delegate.setSelected(selected);
		}

		/**
		 * Sets the new state to either {@link #SELECTED}, {@link #UNSELECTED} or
		 * {@link #PARTIALLY_SELECTED}. If <code>null</code>, then it is treated as
		 * {@link #PARTIALLY_SELECTED}.
		 *
		 * @param state The new selection state
		 */
		protected void setState(TriStateBoolean selectionState)
		{
			if (selectionState == null)
			{
				selectionState = PARTIALLY_SELECTED;
			}

			this.selectionState = selectionState;

			if (selectionState == PARTIALLY_SELECTED)
			{
				delegate.setSelected(false);
			}
			else
			{
				delegate.setSelected(selectionState.booleanValue());
			}
		}
	}
}