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
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

/**
 * This class is used for detecting the occurence of a double-click. For the
 * purposes of this class, a "double-click" is the occurence of two
 * {@link MouseEvent}s corresponding to the mouse1 button without any modifier
 * keys being pressed.
 * <p>
 * The detection of double-click is mediated by the {@link #isDoubleClick(MouseEvent)}
 * method. All mouse events for a particular {@link java.awt.event.MouseListener
 * MouseListener} must be forwarded to {@link #isDoubleClick(MouseEvent)} for
 * processing in order for the proper sequence of clicks to be detected.
 * <p>
 * The implementation of {@link #isDoubleClick(MouseEvent)} is somewhat
 * unforgiving, since it will return <CODE>true</CODE> only when the click count
 * is a multiple of 2, so long as the last two clicks were mouse1 clicks without
 * modifier keys.
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
final class DoubleClickTrigger
{
	/**
	 * Keeps track of the last click and its state, whether it could be used as
	 * the first of the double clicks.
	 */
	private boolean lastClickOK;

	/**
	 * The mask used to determine if any ...
	 */
	private static final int CLICK_MASK = InputEvent.BUTTON1_MASK   |
													  InputEvent.ALT_MASK       |
													  InputEvent.ALT_GRAPH_MASK |
													  InputEvent.CTRL_MASK      |
													  InputEvent.META_MASK      |
													  InputEvent.SHIFT_MASK;

	/**
	 * This method is intended to serve as a delegate for the
	 * {@link MouseListener#mouseClicked(MouseEvent)} method.
	 *
	 * @return <code>true</code> if the specified {@link MouseEvent} is deemed to
	 * be the second click of a double-click sequence; as an additional side
	 * effect of returning <CODE>true</CODE>, the {@link MouseEvent} is also
	 * consumed
	 */
	public boolean isDoubleClick(MouseEvent e)
	{
		int clickCount = e.getClickCount();

		// Sanity check the click count
		if (clickCount <= 0)
		{
			lastClickOK = false;
			return false;
		}

		int modifiers = e.getModifiers();
		boolean currentClickOk = (modifiers & CLICK_MASK) == InputEvent.BUTTON1_MASK;

		if ((clickCount >= 2) && lastClickOK)
		{
			lastClickOK = false;

			if (currentClickOk)
				e.consume();

			return currentClickOk;
		}

		lastClickOK = currentClickOk;

		return false;
	}
}
