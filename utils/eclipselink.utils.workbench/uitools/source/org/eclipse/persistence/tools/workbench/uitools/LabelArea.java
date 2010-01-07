/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.uitools;

import java.awt.Color;
import java.awt.Dimension;
import javax.accessibility.AccessibleContext;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.BoxView;
import javax.swing.text.View;
import javax.swing.text.html.CSS;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

/**
 * This <code>LabelArea</code> allows the text to be wrapped automatically using
 * an HTML view. {@link #getOriginalText()} returns the text that was passed to
 * {@link #setText(String)}, which is the non-modified string; {@link #getText()}
 * actually returns the HTML formatted string.
 * <p>
 * By default, the text is not scrollable, which means it'll use the preferred
 * width to calculate its size. When scrollable is <code>true</code>, then the
 * text wraps based on the available width.
 *
 * @version 11.0.0
 * @since 9.0.4
 */
public class LabelArea extends JLabel
{
	/**
	 * Internal flag used to prevent an infinite loop between setText() and
	 * setDisplayMnemonicIndex().
	 */
	private boolean locked;

	/**
	 * The text before it has been formatted as an HTML string.
	 */
	private String originalText;

	/**
	 * The maximum width this label should have. The width is used to calculate
	 * the preferred height.
	 * @see #PREFERRED_WIDTH
	 */
	private int preferredWidth = PREFERRED_WIDTH;

	/**
	 * Determines if the text shoudl wrap based on the available width. The
	 * default value is <code>false</code>.
	 */
	private boolean scrollable;

	/**
	 * The default preferred width used to calculate the size of this label.
	 * The default value is 300.
	 */
	public static final int PREFERRED_WIDTH = 300;

	/**
	 * Creates a new <code>LabelArea</code>.
	 */
	public LabelArea()
	{
		super();
	}

	/**
	 * Creates a new <code>LabelArea</code>.
	 *
	 * @param icon The icon displayed by this label, <code>null</code> is also
	 * valid
	 */
	public LabelArea(Icon icon)
	{
		super(icon);
	}

	/**
	 * Creates a new <code>LabelArea</code>.
	 *
	 * @param icon The icon displayed by this label, <code>null</code> is also
	 * valid
	 * @param horizontalAlignment One of the following constants defined in
	 * <code>SwingConstants</code>: <code>LEFT</code>, <code>CENTER</code>,
	 * <code>RIGHT</code>, <code>LEADING</code> or <code>TRAILING</code>.
	 */
	public LabelArea(Icon icon, int horizontalAlignment)
	{
		super(icon, horizontalAlignment);
	}

	/**
	 * Creates a new <code>LabelArea</code>.
	 *
	 * @param text This label's text
	 */
	public LabelArea(String text)
	{
		super(text);
	}

	/**
	 * Creates a new <code>LabelArea</code>.
	 *
	 * @param text This label's text
	 * @param icon The icon displayed by this label, <code>null</code> is also
	 * valid
	 * @param horizontalAlignment One of the following constants defined in
	 * <code>SwingConstants</code>: <code>LEFT</code>, <code>CENTER</code>,
	 * <code>RIGHT</code>, <code>LEADING</code> or <code>TRAILING</code>.
	 */
	public LabelArea(String text, Icon icon, int horizontalAlignment)
	{
		super(text, icon, horizontalAlignment);
	}

	/**
	 * Creates a new <code>LabelArea</code>.
	 *
	 * @param text This label's text
	 * @param horizontalAlignment One of the following constants defined in
	 * <code>SwingConstants</code>: <code>LEFT</code>, <code>CENTER</code>,
	 * <code>RIGHT</code>, <code>LEADING</code> or <code>TRAILING</code>.
	 */
	public LabelArea(String text, int horizontalAlignment)
	{
		super(text, horizontalAlignment);
	}

	/**
	 * Formats the given string into HTML using the given properties.
	 *
	 * @param text The actual text to be formatted into HTML
	 * @return The HTML formatted text using the given properties or the given
	 * text if is already an HTML formatted string
	 */
	private String convertToHTML()
	{
		// Nothing to format
		if (StringTools.stringIsEmpty(originalText) ||
			 BasicHTML.isHTMLString(originalText))
		{
			return originalText;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("<HTML><BODY");

		if (!isEnabled())
		{
			Class<?>[] parameterTypes = new Class<?>[] { Color.class };
			Object[] parameterValues = new Object[] { getBackground().darker() };
			String foregroundColor = (String) ClassTools.invokeStaticMethod(CSS.class, "colorToHex", parameterTypes, parameterValues);

			sb.append(" TEXT=\"");
			sb.append(foregroundColor);
			sb.append("\"");
		}

		sb.append(">");

		// If the mnemonic is set, then wraps it with <u></u>
		int mnemonicIndex = getDisplayedMnemonicIndex();

		if (mnemonicIndex > -1)
		{
			sb.append(originalText.substring(0, mnemonicIndex));
			sb.append("<U>");
			sb.append(originalText.charAt(mnemonicIndex));
			sb.append("</U>");
			sb.append(originalText.substring(mnemonicIndex + 1));
		}
		else
		{
			sb.append(originalText);
		}

		sb.append("</BODY></HTML>");

		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 */
	private int findDisplayedMnemonicIndex()
	{
		int mnemonic = getDisplayedMnemonic();

		if ((originalText == null) || (mnemonic == '\0'))
			return -1;

		char uc = Character.toUpperCase((char) mnemonic);
		char lc = Character.toLowerCase((char) mnemonic);

		int uci = originalText.indexOf(uc);
		int lci = originalText.indexOf(lc);

		if (uci == -1)
			return lci;

		if (lci == -1)
			return uci;

		return (lci < uci) ? lci : uci;
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	public AccessibleContext getAccessibleContext()
	{
		if (accessibleContext == null)
			accessibleContext = new AccessibleLabelArea();

		return accessibleContext;
	}

	/**
	 * Returns the text prior to be formatted as an HTML string. If the string is
	 * already formatted, then this one is returned.
	 *
	 * @return The original string passed to {@link #setText(String)}
	 */
	public String getOriginalText()
	{
		return originalText;
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	public Dimension getMinimumSize()
	{
		return getPreferredSize();
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	public Dimension getPreferredSize()
	{
		View rootView = (View) getClientProperty(BasicHTML.propertyKey);

		if ((rootView != null) && !isPreferredSizeSet())
		{
			BoxView view = (BoxView) rootView.getView(0);

			if (scrollable)
			{
				int width  = (int) rootView.getMinimumSpan(View.X_AXIS);
				int height = (int) rootView.getMinimumSpan(View.Y_AXIS);

				Dimension size = super.getPreferredSize();
				size.width  = Math.min(width,  size.width);
				size.height = Math.max(height, size.height);
				return size;
			}
			else
			{
				float width = Math.min((int) view.getPreferredSpan(View.X_AXIS), preferredWidth);
				view.setSize(width, 0.0f);

				float height = view.getPreferredSpan(View.Y_AXIS);
				rootView.setSize(width, height);
			}
		}

		return super.getPreferredSize();
	}

	/**
	 * Returns the maximum width this label should have. The width is used to
	 * calculate the preferred height.
	 *
	 * @return A positive value that fixes the width of this label
	 * @see #PREFERRED_WIDTH
	 */
	public int getPreferredWidth()
	{
		return preferredWidth;
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	public void setDisplayedMnemonicIndex(int index) throws IllegalArgumentException
	{
		if (locked)
			return;

		try
		{
			locked = true;
			super.setDisplayedMnemonicIndex(index);
		}
		finally
		{
			locked = false;
		}

		setText(originalText);
	}

	/**
	 * Sets the maximum width this label should have. The width is used to
	 * calculate the preferred height. The default is value is 300.
	 *
	 * @return A positive value that fixes the width of this label
	 * @exception IllegalArgumentException The preferred width cannot be negative
	 */
	public void setPreferredWidth(int preferredWidth)
	{
		if (preferredWidth < 0)
		{
			throw new IllegalArgumentException("Preferred width cannot be negative");
		}

		this.preferredWidth = preferredWidth;
	}

   /**
	 * Determines if the text should wrap based on the available width. The
	 * default value is <code>false</code>.
	 *
	 * @param scrollabe <code>true</code> to let the text wrap automatically
	 * when the size changes; <code>false</code> otherwise to fix its width and
	 * height based on {@link #preferredWidth}
	 */
	public void setScrollable(boolean scrollable)
	{
		this.scrollable = scrollable;
	}

   /**
	 * Sets the text of this <code>LabelArea</code> to the specified value. The
	 * string will automatically be converted into an HTML string if it's not
	 * already one. The original string, not formatted, can be retrieved
	 * with {@link #getActualText()}.
	 *
	 * @param text The new text to be set
	 * @see javax.swing.JLabel#setText(String)
	 */
	@Override
	public void setText(String text)
	{
		this.originalText = text;
		text = convertToHTML();

		try
		{
			locked = true;
			super.setText(text);
			super.setDisplayedMnemonicIndex(findDisplayedMnemonicIndex());
		}
		finally
		{
			locked = false;
		}
	}

	/**
	 * This class implements accessibility support for the <code>LabelArea</code>
	 * class, the accessible name will be returned non-HTML formatted.
	 */
	protected class AccessibleLabelArea extends AccessibleJLabel
	{
		@Override
		public String getAccessibleName()
		{
			if (accessibleName != null)
				return accessibleName;

			if (LabelArea.this.originalText == null)
				return null;

			// The regular expression supports: <br>, <br />, <p>, </p>
			// and any cases of each letter
			return LabelArea.this.originalText.replaceAll("<[Bb][Rr](\\s)?(/)?>|<(/)?[Pp]>", " "); //$NON-NLS-2$
		}
	}
}
