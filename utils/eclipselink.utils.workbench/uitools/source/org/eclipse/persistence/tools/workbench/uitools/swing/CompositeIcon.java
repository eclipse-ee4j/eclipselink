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
package org.eclipse.persistence.tools.workbench.uitools.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleIcon;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleStateSet;
import javax.swing.Icon;
import javax.swing.SwingConstants;

import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


/**
 * This composite icon is just that: It allows clients to treat
 * a group of icons as a single icon.
 */
public class CompositeIcon
	implements Icon, Cloneable, Serializable, Accessible, SwingConstants
{
	/**
	 * The icons to be painted together as a single icon.
	 * The icons will be painted in the order in which they are stored
	 * in this array and separated by the gaps stored in the array below.
	 * Null icons are allowed.
	 */
	Icon[] icons;

	/**
	 * The gaps, in pixels, between the icons. A gap can be negative,
	 * which may result in the icons on either side of the gap overlapping
	 * each other. The number of gaps must be one less than the
	 * number of icons.
	 */
	private int[] gaps;

	/**
	 * The orientation of the icons. A setting of HORIZONTAL will cause
	 * the icons to be painted starting at the "leading" edge of the canvas,
	 * which is determined the component's orientation ("left-to-right" or
	 * "right-to-left"). A setting of VERTICAL will cause the icons to be
	 * painted starting at the top of the canvas. 
	 * The default orientation is HORIZONTAL.
	 * @see javax.swing.SwingConstants
	 */
	private int orientation;
		public static final int[] VALID_ORIENTATION_VALUES = new int[] {HORIZONTAL, VERTICAL};

	/**
	 * The alignment of the icons. If the orientation is HORIZONTAL,
	 * the alignment can be CENTER, TOP, or BOTTOM. If the orientation
	 * is VERTICAL, the alignment can be CENTER, LEADING, or TRAILING.
	 * For example, if the orientation is HORIZONTAL and the alignment
	 * is BOTTOM, the icons will be drawn horizontally with their bottom
	 * edges aligned.
	 * The default alignment is CENTER.
	 * @see javax.swing.SwingConstants
	 */
	private int alignment;
		public static final int[] VALID_HORIZONTAL_ALIGNMENT_VALUES = new int[] {CENTER, TOP, BOTTOM};
		public static final int[] VALID_VERTICAL_ALIGNMENT_VALUES = new int[] {CENTER, LEADING, TRAILING};

	/**
	 * Whether the icons are always painted from left to right or the LEADING
	 * edge is always on the left, ignoring the component's orientation.
	 * If the override is true, the component's orientation is ignored and a
	 * HORIZONTAL orientation will always paint the icons from left to right
	 * and a VERTICAL orientation will always it LEADING edge on the left.
	 * This allows explicit control of the icon's appearance across locales.
	 * The default override is false.
	 */
	private boolean overrideLeftToRight;

	/**
	 * A brief textual description of the icon used for accessibility.
	 */
	private String description;

	/**
	 * Accessibility information for the composite icon.
	 */
	private AccessibleContext accessibleContext;


	private static final Icon[] EMPTY_ICONS = new Icon[0];
	private static final int[] EMPTY_GAPS = new int[0];
	private static final long serialVersionUID = 1L;


	// ********** constructors **********

	/**
	 * Construct a composite icon with the specified
	 * and icons, gaps, and settings.
	 */
	public CompositeIcon(Icon[] icons, int[] gaps, int orientation, int alignment, String description) {
		super();
		if (icons.length == 0) {
			if (gaps.length != 0) {
				throw new IllegalArgumentException("if there are no icons, there can be no gaps either");
			}
		} else {
			if (gaps.length != icons.length - 1) {
				throw new IllegalArgumentException("if there are icons, there must be 1 fewer gaps than icons");
			}
		}
		this.icons = icons;
		this.gaps = gaps;
		this.checkOrientation(orientation);
		this.orientation = orientation;
		this.setAlignment(alignment);
		this.overrideLeftToRight = false;	// default
		this.setDescription(description);
	}

	/**
	 * Construct a composite icon with the specified icons and gaps
	 * and the default settings.
	 */
	public CompositeIcon(Icon[] icons, int[] gaps) {
		this(icons, gaps, HORIZONTAL, CENTER, null);
	}

	/**
	 * Construct a composite icon with no icons or gaps
	 * and the default settings.
	 */
	public CompositeIcon() {
		this(EMPTY_ICONS, EMPTY_GAPS);
	}

	/**
	 * Construct a composite icon with a single icon
	 * and the default settings.
	 */
	public CompositeIcon(Icon icon) {
		this(new Icon[] {icon}, EMPTY_GAPS);
	}

	/**
	 * Construct a composite icon with default settings and
	 * the two specified icons with the specified gap between them.
	 */
	public CompositeIcon(Icon icon1, int gap, Icon icon2) {
		this(new Icon[] {icon1, icon2}, new int[] {gap});
	}

	/**
	 * Construct a composite icon with default settings and
	 * the two specified icons with no gap between them.
	 */
	public CompositeIcon(Icon icon1, Icon icon2) {
		this(icon1, 0, icon2);
	}

	/**
	 * Construct a composite icon with default settings and
	 * the specified icons with the specified fixed gap between them all.
	 */
	public CompositeIcon(Icon[] icons, int gap) {
		this(icons, buildFixedGapsFor(icons.length, gap));
	}

	/**
	 * Construct a composite icon with default settings and
	 * the specified icons with no gaps between them.
	 */
	public CompositeIcon(Icon[] icons) {
		this(icons, 0);
	}

	/**
	 * Construct a composite icon with default settings and
	 * the specified icons with no gaps between them.
	 */
	public CompositeIcon(List icons, int gap) {
		this((Icon[]) icons.toArray(new Icon[icons.size()]), gap);
	}

	/**
	 * Construct a composite icon with default settings and
	 * the specified icons with no gaps between them.
	 */
	public CompositeIcon(List icons) {
		this(icons, 0);
	}


	// ********** Icon implementation **********

	/**
	 * HORIZONTAL: Add up all the widths and gaps
	 * (handling negative gaps appropriately).
	 * VERTICAL: The width of the widest icon.
	 * @see javax.swing.Icon#getIconWidth()
	 */
	public int getIconWidth() {
		Icon[] localIcons = this.icons;
		int iconsLength = localIcons.length;

		if (this.orientation == HORIZONTAL) {
			int[] localGaps = this.gaps;
			int position = 0;
			int min = 0;
			int max = 0;
			int gapsLength = localGaps.length;
			// this loop MUST ascend
			for (int i = 0; i < iconsLength; i++) {
				Icon icon = localIcons[i];
				if (icon != null) {
					position += icon.getIconWidth();
					max = (max >= position) ? max : position;
				}
				if (i < gapsLength) {
					position += localGaps[i];
					max = (max >= position) ? max : position;
					min = (min <= position) ? min : position;
				}
			}
			return max - min;
		}

		// VERTICAL
		int width = 0;
		for (int i = 0; i < iconsLength; i++) {
			Icon icon = localIcons[i];
			if (icon != null) {
				int iconWidth = icon.getIconWidth();
				width = (width >= iconWidth) ? width : iconWidth;
			}
		}
		return width;
	}

	/**
	 * VERTICAL: Add up all the heights and gaps.
	 * (handling negative gaps appropriately).
	 * HORIZONTAL: The height of the tallest icon.
	 * @see javax.swing.Icon#getIconHeight()
	 */
	public int getIconHeight() {
		Icon[] localIcons = this.icons;
		int iconsLength = localIcons.length;

		if (this.orientation == VERTICAL) {
			int[] localGaps = this.gaps;
			int position = 0;
			int min = 0;
			int max = 0;
			int gapsLength = localGaps.length;
			// this loop MUST ascend
			for (int i = 0; i < iconsLength; i++) {
				Icon icon = localIcons[i];
				if (icon != null) {
					position += icon.getIconHeight();
					max = (max >= position) ? max : position;
				}
				if (i < gapsLength) {
					position += localGaps[i];
					max = (max >= position) ? max : position;
					min = (min <= position) ? min : position;
				}
			}
			return max - min;
		}

		// HORIZONTAL
		int height = 0;
		for (int i = 0; i < iconsLength; i++) {
			Icon icon = localIcons[i];
			if (icon != null) {
				int iconHeight = icon.getIconHeight();
				height = (height >= iconHeight) ? height : iconHeight;
			}
		}
		return height;
	}

	/**
	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
	 */
	public void paintIcon(Component component, Graphics g, int x, int y) {
		if (this.orientation == HORIZONTAL) {
			this.paintIconHorizontally(component, g, x, y);
		} else {		// VERTICAL
			this.paintIconVertically(component, g, x, y);
		}
	}

	private void paintIconHorizontally(Component component, Graphics g, int x, int y) {
		if (this.isLeftToRight(component)) {
			this.paintIconLeftToRight(component, g, x, y);
		} else {
			this.paintIconRightToLeft(component, g, x, y);
		}
	}

	private void paintIconLeftToRight(Component component, Graphics g, int x, int y) {
		switch (this.alignment) {
			case CENTER:
				this.paintIconLeftToRightCenterAligned(component, g, x, y);
				break;
			case TOP:
				this.paintIconLeftToRightTopAligned(component, g, x, y);
				break;
			case BOTTOM:
				this.paintIconLeftToRightBottomAligned(component, g, x, y);
				break;
			default:
				throw new IllegalStateException("illegal alignment: " + this.alignment);
		}
	}

	private void paintIconLeftToRightCenterAligned(Component component, Graphics g, int x, int y) {
		Icon[] localIcons = this.icons;
		int iconsLength = localIcons.length;
		int[] localGaps = this.gaps;
		int gapsLength = localGaps.length;

		// cache the height of the composite icon so we can center the nested icons in it
		int totalHeight = this.getIconHeight();

		// start the sliding position on the left
		int position = x + this.calculateHorizontalStartOffset();

		for (int i = 0; i < iconsLength; i++) {
			Icon icon = localIcons[i];
			if (icon != null) {
				icon.paintIcon(component, g, position, y + ((totalHeight - icon.getIconHeight()) >> 1));
				position += icon.getIconWidth();
			}
			if (i < gapsLength) {
				position += localGaps[i];
			}
		}
	}

	// one of our two most performant options :-)
	private void paintIconLeftToRightTopAligned(Component component, Graphics g, int x, int y) {
		Icon[] localIcons = this.icons;
		int iconsLength = localIcons.length;
		int[] localGaps = this.gaps;
		int gapsLength = localGaps.length;

		// start the sliding position on the left
		int position = x + this.calculateHorizontalStartOffset();

		for (int i = 0; i < iconsLength; i++) {
			Icon icon = localIcons[i];
			if (icon != null) {
				icon.paintIcon(component, g, position, y);
				position += icon.getIconWidth();
			}
			if (i < gapsLength) {
				position += localGaps[i];
			}
		}
	}

	private void paintIconLeftToRightBottomAligned(Component component, Graphics g, int x, int y) {
		Icon[] localIcons = this.icons;
		int iconsLength = localIcons.length;
		int[] localGaps = this.gaps;
		int gapsLength = localGaps.length;

		// cache the bottom of the composite icon so we can position the nested icons there
		int bottom = y + this.getIconHeight();

		// start the sliding position on the left
		int position = x + this.calculateHorizontalStartOffset();

		for (int i = 0; i < iconsLength; i++) {
			Icon icon = localIcons[i];
			if (icon != null) {
				icon.paintIcon(component, g, position, bottom - icon.getIconHeight());
				position += icon.getIconWidth();
			}
			if (i < gapsLength) {
				position += localGaps[i];
			}
		}
	}

	/**
	 * typically, the offset is zero; but it can be non-zero if
	 * there are any excessively negative gaps
	 */
	private int calculateHorizontalStartOffset() {
		Icon[] localIcons = this.icons;
		int[] localGaps = this.gaps;
		int position = 0;
		int min = 0;
		int gapsLength = localGaps.length;

		// this loop MUST ascend; we can ignore the last icon
		for (int i = 0; i < gapsLength; i++) {
			Icon icon = localIcons[i];
			if (icon != null) {
				position += icon.getIconWidth();
			}
			position += localGaps[i];
			min = (min <= position) ? min : position;
		}
		return (min < 0) ? -min : min;
	}

	private void paintIconRightToLeft(Component component, Graphics g, int x, int y) {
		switch (this.alignment) {
			case CENTER:
				this.paintIconRightToLeftCenterAligned(component, g, x, y);
				break;
			case TOP:
				this.paintIconRightToLeftTopAligned(component, g, x, y);
				break;
			case BOTTOM:
				this.paintIconRightToLeftBottomAligned(component, g, x, y);
				break;
			default:
				throw new IllegalStateException("illegal alignment: " + this.alignment);
		}
	}

	private void paintIconRightToLeftCenterAligned(Component component, Graphics g, int x, int y) {
		Icon[] localIcons = this.icons;
		int iconsLength = localIcons.length;
		int[] localGaps = this.gaps;
		int gapsLength = localGaps.length;

		// cache the height of the composite icon so we can center the nested icons in it
		int totalHeight = this.getIconHeight();

		// start the sliding position on the right
		int position = x + this.getIconWidth() - this.calculateHorizontalStartOffset();

		for (int i = 0; i < iconsLength; i++) {
			Icon icon = localIcons[i];
			if (icon != null) {
				position -= icon.getIconWidth();
				icon.paintIcon(component, g, position, y + ((totalHeight - icon.getIconHeight()) >> 1));
			}
			if (i < gapsLength) {
				position -= localGaps[i];
			}
		}
	}

	private void paintIconRightToLeftTopAligned(Component component, Graphics g, int x, int y) {
		Icon[] localIcons = this.icons;
		int iconsLength = localIcons.length;
		int[] localGaps = this.gaps;
		int gapsLength = localGaps.length;

		// start the sliding position on the right
		int position = x + this.getIconWidth() - this.calculateHorizontalStartOffset();

		for (int i = 0; i < iconsLength; i++) {
			Icon icon = localIcons[i];
			if (icon != null) {
				position -= icon.getIconWidth();
				icon.paintIcon(component, g, position, y);
			}
			if (i < gapsLength) {
				position -= localGaps[i];
			}
		}
	}

	private void paintIconRightToLeftBottomAligned(Component component, Graphics g, int x, int y) {
		Icon[] localIcons = this.icons;
		int iconsLength = localIcons.length;
		int[] localGaps = this.gaps;
		int gapsLength = localGaps.length;

		// cache the bottom of the composite icon so we can position the nested icons there
		int bottom = y + this.getIconHeight();

		// start the sliding position on the right
		int position = x + this.getIconWidth() - this.calculateHorizontalStartOffset();

		for (int i = 0; i < iconsLength; i++) {
			Icon icon = localIcons[i];
			if (icon != null) {
				position -= icon.getIconWidth();
				icon.paintIcon(component, g, position, bottom - icon.getIconHeight());
			}
			if (i < gapsLength) {
				position -= localGaps[i];
			}
		}
	}

	private void paintIconVertically(Component component, Graphics g, int x, int y) {
		if (this.isLeftToRight(component)) {
			this.paintIconTopToBottomLeadingEdgeOnLeft(component, g, x, y);
		} else {
			this.paintIconTopToBottomLeadingEdgeOnRight(component, g, x, y);
		}
	}

	private void paintIconTopToBottomLeadingEdgeOnLeft(Component component, Graphics g, int x, int y) {
		switch (this.alignment) {
			case CENTER:
				this.paintIconTopToBottomCenterAligned(component, g, x, y);
				break;
			case LEADING:
				this.paintIconTopToBottomLeftAligned(component, g, x, y);
				break;
			case TRAILING:
				this.paintIconTopToBottomRightAligned(component, g, x, y);
				break;
			default:
				throw new IllegalStateException("illegal alignment: " + this.alignment);
		}
	}

	private void paintIconTopToBottomCenterAligned(Component component, Graphics g, int x, int y) {
		Icon[] localIcons = this.icons;
		int iconsLength = localIcons.length;
		int[] localGaps = this.gaps;
		int gapsLength = localGaps.length;

		// cache the width of the composite icon so we can center the nested icons in it
		int totalWidth = this.getIconWidth();

		// start the sliding position at the top
		int position = y + this.calculateVerticalStartOffset();

		for (int i = 0; i < iconsLength; i++) {
			Icon icon = localIcons[i];
			if (icon != null) {
				icon.paintIcon(component, g, x + ((totalWidth - icon.getIconWidth()) >> 1), position);
				position += icon.getIconHeight();
			}
			if (i < gapsLength) {
				position += localGaps[i];
			}
		}
	}

	// one of our two most performant options :-)
	private void paintIconTopToBottomLeftAligned(Component component, Graphics g, int x, int y) {
		Icon[] localIcons = this.icons;
		int iconsLength = localIcons.length;
		int[] localGaps = this.gaps;
		int gapsLength = localGaps.length;

		// start the sliding position at the top
		int position = y + this.calculateVerticalStartOffset();

		for (int i = 0; i < iconsLength; i++) {
			Icon icon = localIcons[i];
			if (icon != null) {
				icon.paintIcon(component, g, x, position);
				position += icon.getIconHeight();
			}
			if (i < gapsLength) {
				position += localGaps[i];
			}
		}
	}

	private void paintIconTopToBottomRightAligned(Component component, Graphics g, int x, int y) {
		Icon[] localIcons = this.icons;
		int iconsLength = localIcons.length;
		int[] localGaps = this.gaps;
		int gapsLength = localGaps.length;

		// cache the right edge of the composite icon so we can position the nested icons there
		int right = x + this.getIconWidth();

		// start the sliding position at the top
		int position = y + this.calculateVerticalStartOffset();

		for (int i = 0; i < iconsLength; i++) {
			Icon icon = localIcons[i];
			if (icon != null) {
				icon.paintIcon(component, g, right - icon.getIconWidth(), position);
				position += icon.getIconHeight();
			}
			if (i < gapsLength) {
				position += localGaps[i];
			}
		}
	}

	/**
	 * typically, the offset is zero; but it can be non-zero if
	 * there are any excessively negative gaps
	 */
	private int calculateVerticalStartOffset() {
		Icon[] localIcons = this.icons;
		int[] localGaps = this.gaps;
		int position = 0;
		int min = 0;
		int gapsLength = localGaps.length;

		// this loop MUST ascend; we can ignore the last icon
		for (int i = 0; i < gapsLength; i++) {
			Icon icon = localIcons[i];
			if (icon != null) {
				position += icon.getIconHeight();
			}
			position += localGaps[i];
			min = (min <= position) ? min : position;
		}
		return (min < 0) ? -min : min;
	}

	private void paintIconTopToBottomLeadingEdgeOnRight(Component component, Graphics g, int x, int y) {
		switch (this.alignment) {
			case CENTER:
				this.paintIconTopToBottomCenterAligned(component, g, x, y);
				break;
			case LEADING:
				this.paintIconTopToBottomRightAligned(component, g, x, y);
				break;
			case TRAILING:
				this.paintIconTopToBottomLeftAligned(component, g, x, y);
				break;
			default:
				throw new IllegalStateException("illegal alignment: " + this.alignment);
		}
	}


	// ********** Cloneable implementation **********

	/**
	 * Clone the arrays so they are not shared.
	 * @see Object#clone()
	 */
	public Object clone() {
		CompositeIcon clone;
		try {
			clone = (CompositeIcon) super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}
		clone.icons = new Icon[this.icons.length];
		System.arraycopy(this.icons, 0, clone.icons, 0, this.icons.length);
		clone.gaps = new int[this.gaps.length];
		System.arraycopy(this.gaps, 0, clone.gaps, 0, this.gaps.length);
		clone.accessibleContext = null;
		return clone;
	}


	// ********** Accessible implementation **********

	/**
	 * @see javax.accessibility.Accessible#getAccessibleContext()
	 */
	public AccessibleContext getAccessibleContext() {
		if (this.accessibleContext == null) {
			this.accessibleContext = new AccessibleCompositeIcon();
		}
		return this.accessibleContext;
	}


	// ********** Object overrides **********

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "CompositeIcon(" + this.icons.length + " icons)";
	}


	// ********** accessors **********

	/**
	 * Return the icon at the specified position.
	 * @see icons
	 */
	public Icon getIcon(int index) {
		return this.icons[index];
	}

	/**
	 * Set the icon at the specified position.
	 * @see icons
	 */
	public void setIcon(int index, Icon icon) {
		this.icons[index] = icon;
	}

	/**
	 * Return the number of icons.
	 * @see icons
	 */
	public int iconCount() {
		return this.icons.length;
	}

	/**
	 * Return the gap at the specified position.
	 * @see gaps
	 */
	public int getGap(int index) {
		return this.gaps[index];
	}

	/**
	 * Set the gap at the specified position.
	 * @see gaps
	 */
	public void setGap(int index, int gap) {
		this.gaps[index] = gap;
	}

	/**
	 * Return the number of gaps.
	 * @see gaps
	 */
	public int gapCount() {
		return this.gaps.length;
	}

	private void addIcons(Icon[] extraIcons) {
		Icon[] oldIcons = this.icons;
		int oldLen = oldIcons.length;
		int extraLen = extraIcons.length;
		Icon[] newIcons = new Icon[oldLen + extraLen];
		System.arraycopy(oldIcons, 0, newIcons, 0, oldLen);
		System.arraycopy(extraIcons, 0, newIcons, oldLen, extraLen);
		this.icons = newIcons;
	}

	private void addGaps(int[] extraGaps) {
		int[] oldGaps = this.gaps;
		int oldLen = oldGaps.length;
		int extraLen = extraGaps.length;
		int[] newGaps = new int[oldLen + extraLen];
		System.arraycopy(oldGaps, 0, newGaps, 0, oldLen);
		System.arraycopy(extraGaps, 0, newGaps, oldLen, extraLen);
		this.gaps = newGaps;
	}

	/**
	 * Add the specified icons to the end of the list of icons
	 * with the specified gaps.
	 */
	public void addAll(Icon[] extraIcons, int[] extraGaps) {
		if (extraIcons.length == 0) {
			return;
		}
		if (this.icons.length == 0) {
			if (extraGaps.length != extraIcons.length - 1) {
				throw new IllegalArgumentException("there must be 1 fewer gaps than icons with the first addition to the composite");
			}
		} else {
			if (extraGaps.length != extraIcons.length) {
				throw new IllegalArgumentException("there must be same number of gaps as icons on subsequent additions to the composite");
			}
		}
		this.addIcons(extraIcons);
		this.addGaps(extraGaps);
	}

	/**
	 * Add the specified icons to the end of the list of icons
	 * with the specified fixed gap between them.
	 */
	public void addAll(Icon[] extraIcons, int gap) {
		this.addAll(extraIcons, buildFixedGapsFor(extraIcons.length, gap));
	}

	/**
	 * Add the specified icons to the end of the list of icons
	 * with zero gaps between them.
	 */
	public void addAll(Icon[] extraIcons) {
		this.addAll(extraIcons, 0);
	}

	/**
	 * Add the specified icons to the end of the list of icons
	 * with the specified fixed gap between them.
	 */
	public void addAll(List extraIcons, int gap) {
		this.addAll((Icon[]) extraIcons.toArray(new Icon[extraIcons.size()]), gap);
	}

	/**
	 * Add the specified icons to the end of the list of icons
	 * with zero gaps between them.
	 */
	public void addAll(List extraIcons) {
		this.addAll(extraIcons, 0);
	}

	/**
	 * Add the specified icon to the end of the list of icons
	 * with the specified gap.
	 */
	public void add(Icon icon, int gap) {
		// ignore the gap if this is the first icon added
		if (this.icons.length == 0) {
			this.icons = new Icon[] {icon};
		} else {
			this.addAll(new Icon[] {icon}, new int[] {gap});
		}
	}

	/**
	 * Add the specified icon to the end of the list of icons
	 * with a gap of zero.
	 */
	public void add(Icon icon) {
		this.add(icon, 0);
	}

	/**
	 * Clear out all of the composite icon's icons and gaps.
	 */
	public void clear() {
		this.icons = EMPTY_ICONS;
		this.gaps = EMPTY_GAPS;
	}

	/**
	 * @see #orientation
	 */
	public int getOrientation() {
		return this.orientation;
	}

	/**
	 * @see #orientation
	 */
	public void setOrientation(int orientation) {
		if (this.orientation == orientation) {
			return;
		}
		this.checkOrientation(orientation);
		this.alignment = CENTER;
		this.orientation = orientation;
	}

	private void checkOrientation(int orient) {
		if ( ! CollectionTools.contains(VALID_ORIENTATION_VALUES, orient)) {
			throw new IllegalArgumentException("invalid orientation: " + orient);
		}
	}

	/**
	 * @see #alignment
	 */
	public int getAlignment() {
		return this.alignment;
	}

	/**
	 * @see #orientation
	 */
	public void setAlignment(int alignment) {
		this.checkAlignment(alignment);
		this.alignment = alignment;
	}

	private void checkAlignment(int align) {
		if (this.orientation == HORIZONTAL) {
			if ( ! CollectionTools.contains(VALID_HORIZONTAL_ALIGNMENT_VALUES, align)) {
				throw new IllegalArgumentException("invalid horizontal alignment: " + align);
			}
		} else {	// VERTICAL
			if ( ! CollectionTools.contains(VALID_VERTICAL_ALIGNMENT_VALUES, align)) {
				throw new IllegalArgumentException("invalid vertical alignment: " + align);
			}
		}
	}

	/**
	 * @see #overrideLeftToRight
	 */
	public boolean isOverrideLeftToRight() {
		return this.overrideLeftToRight;
	}

	/**
	 * @see #overrideLeftToRight
	 */
	public void setOverrideLeftToRight(boolean overrideLeftToRight) {
		this.overrideLeftToRight = overrideLeftToRight;
	}

	/**
	 * If the composite icon's orientation is HORIZONTAL,
	 * return whether the component icons are painted from left-to-right.
	 * If the composite icon's orientation is VERTICAL,
	 * return whether the LEADING edge is on the left.
	 */
	private boolean isLeftToRight(Component component) {
		return this.overrideLeftToRight || component.getComponentOrientation().isLeftToRight();
	}

	/**
	 * @see #description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @see #description
	 */
	public void setDescription(String description) {
		this.description = description;
	}


	// ********** static methods **********

	/**
	 * Return an array of gaps for a set of icons of
	 * the specified size, all of the same specified size.
	 */
	private static int[] buildFixedGapsFor(int iconsLength, int gap) {
		if (iconsLength == 0) {
			return EMPTY_GAPS;
		}
		return CollectionTools.fill(new int[iconsLength - 1], gap);
	}


	// ********** AccessibleContext implementation **********

	/**
	 * This class implements accessibility support for the CompositeIcon.
	 * It provides an implementation of the Java Accessibility API
	 * appropriate to array icon user-interface elements.
	 */
	protected class AccessibleCompositeIcon
		extends AccessibleContext
		implements AccessibleIcon, Serializable
	{
		/**
		 * @see javax.accessibility.AccessibleContext#getAccessibleRole()
		 */
		public AccessibleRole getAccessibleRole() {
			return AccessibleRole.ICON;
		}

		/**
		 * @see javax.accessibility.AccessibleContext#getAccessibleStateSet()
		 */
		public AccessibleStateSet getAccessibleStateSet() {
			return null;
		}

		/**
		 * @see javax.accessibility.AccessibleContext#getAccessibleParent()
		 */
		public Accessible getAccessibleParent() {
			return null;
		}

		/**
		 * @see javax.accessibility.AccessibleContext#getAccessibleIndexInParent()
		 */
		public int getAccessibleIndexInParent() {
			return -1;
		}

		/**
		 * @see javax.accessibility.AccessibleContext#getAccessibleChildrenCount()
		 */
		public int getAccessibleChildrenCount() {
			Icon[] localIcons = CompositeIcon.this.icons;
			int len = localIcons.length;
			int count = 0;
			for (int i = 0; i < len; i++) {
				if (localIcons[i] instanceof Accessible) {
					count++;
				}
			}
			return count;
		}

		/**
		 * @see javax.accessibility.AccessibleContext#getAccessibleChild(int)
		 */
		public Accessible getAccessibleChild(int index) {
			Icon[] localIcons = CompositeIcon.this.icons;
			int len = localIcons.length;
			int count = 0;
			for (int i = 0; i < len; i++) {
				Icon icon = localIcons[i];
				if (icon instanceof Accessible) {
					if (count == index) {
						return (Accessible) icon;
					}
					count++;
				}
			}
			return null;
		}

		/**
		 * @see javax.accessibility.AccessibleContext#getLocale()
		 */
		public Locale getLocale() {
			return null;
		}

		/**
		 * @see javax.accessibility.AccessibleIcon#getAccessibleIconDescription()
		 */
		public String getAccessibleIconDescription() {
			return CompositeIcon.this.getDescription();
		}

		/**
		 * @see javax.accessibility.AccessibleIcon#setAccessibleIconDescription(java.lang.String)
		 */
		public void setAccessibleIconDescription(String description) {
			CompositeIcon.this.setDescription(description);
		}

		/**
		 * @see javax.accessibility.AccessibleIcon#getAccessibleIconHeight()
		 */
		public int getAccessibleIconHeight() {
			return CompositeIcon.this.getIconHeight();
		}

		/**
		 * @see javax.accessibility.AccessibleIcon#getAccessibleIconWidth()
		 */
		public int getAccessibleIconWidth() {
			return CompositeIcon.this.getIconWidth();
		}

	}

}
