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
package org.eclipse.persistence.tools.workbench.uitools.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.IllegalComponentStateException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Locale;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleIcon;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleStateSet;
import javax.swing.Icon;
import javax.swing.SwingConstants;

/**
 * An icon that displays a triangle pointing in one of the four
 * compass directions.
 * 
 * @see javax.swing.SwingConstants.NORTH
 * @see javax.swing.SwingConstants.EAST
 * @see javax.swing.SwingConstants.SOUTH
 * @see javax.swing.SwingConstants.WEST
 */
public class ArrowIcon
	implements Icon, Accessible
{

	private int width;
	private int height;

	/** The arrow's orientation: NORTH, EAST (default), SOUTH, or WEST. */
	private int orientation;

	/** The arrow's dimension, as determined by the icon's dimension. */
	private Dimension arrowDimension;

	/** The color is used only if the icon is opaque. */
	private Color color;
	private boolean opaque;

	private AccessibleContext accessibleContext;
	private String description;


	// ********** constructors **********

	/**
	 * Construct a black, 10x10 arrow icon with the specified orientation.
	 */
	public ArrowIcon(int orientation) {
		this();
		this.orientation = orientation;
	}

	/**
	 * Construct a black, 10x10 arrow icon with an orientation of EAST.
	 */
	public ArrowIcon() {
		this(10, 10);
	}

	/**
	 * Construct a black arrow icon with the specified size and
	 * orientation of EAST.
	 */
	public ArrowIcon(int width, int height) {
		this(width, height, SwingConstants.EAST);
	}

	/**
	 * Construct a black arrow icon with the specified size and orientation.
	 */
	public ArrowIcon(int width, int height, int orientation) {
		this(width, height, orientation, Color.black);
	}

	/**
	 * Construct an arrow icon with the specified size, orientation, and color.
	 */
	public ArrowIcon(int width, int height, int orientation, Color color) {
		super();
		this.width = width;
		this.height = height;
		this.orientation = orientation;
		this.color = color;
		this.opaque = (color != null);
	}


	// ********** accessors **********

	public int getIconWidth() {
		return this.width;
	}

	public void setIconWidth(int width) {
		if (width != this.width) {
			this.width = width;
			this.arrowDimension = null;
		}
	}

	public int getIconHeight() {
		return this.height;
	}

	public void setIconHeight(int height) {
		if (height != this.height) {
			this.height = height;
			this.arrowDimension = null;
		}
	}

	/**
	 * Return the orientation of icon's arrow.
	 * @see javax.swing.SwingConstants.NORTH
	 * @see javax.swing.SwingConstants.EAST
	 * @see javax.swing.SwingConstants.SOUTH
	 * @see javax.swing.SwingConstants.WEST
	 */
	public int getOrientation() {
		return this.orientation;
	}

	/**
	 * Set the orientation of icon's arrow.
	 * @see javax.swing.SwingConstants.NORTH
	 * @see javax.swing.SwingConstants.EAST
	 * @see javax.swing.SwingConstants.SOUTH
	 * @see javax.swing.SwingConstants.WEST
	 */
	public void setOrientation(int orientation) {
		if (orientation != this.orientation) {
			this.orientation = orientation;
			this.arrowDimension = null;
		}
	}

	/**
	 * Return the icon's color. The color is used only if the icon is opaque.
	 */
	public Color getColor() {
		return this.color;
	}

	/**
	 * Set the icon's color. The color is used only if the icon is opaque.
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Return whether the arrow should be painted with the background color of the
	 * component this arrow is painting on to look pressed. The interior of the
	 * triangle is transparent.
	 */
	public boolean isOpaque() {
		return this.opaque;
	}

	/**
	 * Set whether the arrow should be painted with the background color of the
	 * component this arrow is painting on to look pressed. The interior of the
	 * triangle is transparent.
	 */
	public void setOpaque(boolean opaque) {
		this.opaque = opaque;
	}

	public AccessibleContext getAccessibleContext() {
		if (this.accessibleContext == null) {
			this.accessibleContext = new AccessibleArrowIcon();
		}
		return this.accessibleContext;
	}

	/**
	 * Return the icon's description. This is meant to be a brief textual
	 * description of the object. For example, it might be presented to a blind
	 * user to give an indication of the purpose of the icon. The description
	 * may be null.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Set the icon's description. This is meant to be a brief textual
	 * description of the object. For example, it might be presented to a blind
	 * user to give an indication of the purpose of the icon. The description
	 * may be null.
	 */
	public void setDescription(String description) {
		this.description = description;
	}


	// ********** Icon implementation **********

	public void paintIcon(Component component, Graphics g, int x, int y) {
		// save the original graphics settings
		Color oldColor = g.getColor();
		if (y < 0) {
			y = 0;
		}
		Dimension arrowDim = this.getArrowDimension();
		int deltaX = (this.width - arrowDim.width) / 2;
		int deltaY = (this.height - arrowDim.height) / 2;
		g.translate(x + deltaX, y + deltaY);

		if (this.opaque) {
			if (component.isEnabled()) {
				g.setColor((this.color != null) ? this.color : component.getBackground().darker());
			} else {
				Color bgColor = component.getBackground();
				g.translate(1, 1);
				g.setColor(bgColor.brighter());
				this.paintOpaqueTriangle(g, arrowDim.width, arrowDim.height);
				g.translate(-1, -1);
				g.setColor(bgColor.darker());
			}
			this.paintOpaqueTriangle(g, arrowDim.width, arrowDim.height);
		} else {
			this.paintTransparentTriangle(g, arrowDim.width, arrowDim.height, component.getBackground());
		}

		// restore original graphics settings
		g.translate(-x - deltaX, -y - deltaY);
		g.setColor(oldColor);
	}


	// ********** internal methods **********

	/**
	 * Return the arrow's dimension(s), as determined by the size of the icon.
	 */
	private Dimension getArrowDimension() {
		if (this.arrowDimension == null) {
			this.arrowDimension = this.buildArrowDimension();
		}
		return this.arrowDimension;
	}

	/**
	 * Build the arrow's dimension(s), as determined by the size of the icon.
	 */
	private Dimension buildArrowDimension() {
		int w = 0;
		int h = 0;

		int iconWidth = this.width;
		int iconHeight = this.height;

		int arrowOrientation = this.orientation;

		if (iconWidth == iconHeight) {
			if (arrowOrientation == SwingConstants.EAST || arrowOrientation == SwingConstants.WEST) {
				h = iconHeight;
				if (h % 2 == 0) {	// force 'h' to be an odd number
					h--;
				}
				w = (h + 1) / 2;
			} else {
				w = iconWidth;
				if (w % 2 == 0) {	// force 'w' to be an odd number
					w--;
				}
				h = (w + 1) / 2;
			}
		} else if (iconWidth < iconHeight) {
			w = iconWidth;
			if (arrowOrientation == SwingConstants.NORTH || arrowOrientation == SwingConstants.SOUTH) {
				if (w % 2 == 0) {	// force 'w' to be an odd number
					w--;
				}
				h = (w + 1) / 2;
			} else {
				h = (w * 2) - 1;
			}
		} else {	// (iconWidth > iconHeight)
			h = iconHeight;
			if (arrowOrientation == SwingConstants.EAST || arrowOrientation == SwingConstants.WEST) {
				if (h % 2 == 0) {	// force 'h' to be an odd number
					h--;
				}
				w = (h + 1) / 2;
			} else {
				w = (h * 2) - 1;
			}
		}
		return new Dimension(w, h);
	}

	private void paintTransparentTriangle(Graphics g, int triangleWidth, int triangleHeight, Color triangleColor) {
		Color brighter = triangleColor.brighter();
		Color darker = triangleColor.darker();

		int arrowOrientation = this.orientation;

		triangleWidth--;
		triangleHeight--;
		if (arrowOrientation == SwingConstants.EAST) {
			g.setColor(brighter);
			g.drawLine(0, triangleHeight, triangleWidth, triangleHeight / 2);		// draw /
			g.setColor(darker);
			g.drawLine(0, 0, triangleWidth, triangleHeight / 2);		// draw \
			g.drawLine(0, 0, 0, triangleHeight);		// draw |

		} else if (arrowOrientation == SwingConstants.WEST) {
			g.setColor(brighter);
			g.drawLine(0, triangleHeight / 2, triangleWidth, triangleHeight);		// draw \
			g.drawLine(triangleWidth, 0, triangleWidth, triangleHeight);		// draw |
			g.setColor(darker);
			g.drawLine(triangleWidth, 0, 0, triangleHeight / 2);		// draw /

		} else if (arrowOrientation == SwingConstants.NORTH) {
			g.setColor(darker);
			g.drawLine(0, triangleHeight, triangleWidth / 2, 0);		// draw /
			g.drawLine(0, triangleHeight, triangleWidth, triangleHeight);		// draw _
			g.setColor(brighter);
			g.drawLine(triangleWidth / 2, 0, triangleWidth, triangleHeight);		// draw \

		} else if (arrowOrientation == SwingConstants.SOUTH) {
			g.setColor(brighter);
			g.drawLine(triangleWidth / 2, triangleHeight, triangleWidth, 0);		// draw /
			g.setColor(darker);
			g.drawLine(0, 0, triangleWidth, 0);		// draw -
			g.drawLine(0, 0, triangleWidth / 2, triangleHeight);		// draw \
		}
	}

	private void paintOpaqueTriangle(Graphics g, int triangleWidth, int triangleHeight) {
		int arrowOrientation = this.orientation;

		if (arrowOrientation == SwingConstants.EAST) {
			int maxY = triangleHeight - 1;
			for (int i = 0; i < triangleWidth; i++) {
				g.drawLine(i, i, i, maxY - i);
			}

		} else if (arrowOrientation == SwingConstants.WEST) {
			int maxY = triangleWidth - 1;
			triangleHeight--;
			for (int i = triangleWidth; i-- > 0; ) {
				int y = maxY - i;
				g.drawLine(i, y, i, triangleHeight - y);
			}

		} else if (arrowOrientation == SwingConstants.NORTH) {
			int maxX = triangleHeight - 1;
			triangleWidth--;
			for (int i = triangleHeight; i-- > 0; ) {
				int x = maxX - i;
				g.drawLine(x, i, triangleWidth - x, i);
			}

		} else if (arrowOrientation == SwingConstants.SOUTH) {
			int maxX = triangleWidth - 1;
			for (int i = 0; i < triangleHeight; i++) {
				g.drawLine(i, i, maxX - i, i);
			}
		}
	}


	// ********** accessibility support **********

	private class AccessibleArrowIcon
		extends AccessibleContext
		implements AccessibleIcon, Serializable
	{

		public AccessibleRole getAccessibleRole() {
			return AccessibleRole.ICON;
		}

		public AccessibleStateSet getAccessibleStateSet() {
			return null;
		}

		public Accessible getAccessibleParent() {
			return null;
		}

		public int getAccessibleIndexInParent() {
			return -1;
		}

		public int getAccessibleChildrenCount() {
			return 0;
		}

		public Accessible getAccessibleChild(int index) {
			return null;
		}

		public Locale getLocale() throws IllegalComponentStateException {
			return null;
		}

		public String getAccessibleIconDescription() {
			return ArrowIcon.this.getDescription();
		}

		public void setAccessibleIconDescription(String description) {
			ArrowIcon.this.setDescription(description);
		}

		public int getAccessibleIconHeight() {
			return ArrowIcon.this.getIconHeight();
		}

		public int getAccessibleIconWidth() {
			return ArrowIcon.this.getIconWidth();
		}

		private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
			s.defaultReadObject();
		}

		private void writeObject(ObjectOutputStream s) throws IOException {
			s.defaultWriteObject();
		}

	}

}
