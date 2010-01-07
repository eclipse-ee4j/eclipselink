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
package org.eclipse.persistence.tools.workbench.framework.app;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.Serializable;

import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.eclipse.persistence.tools.workbench.uitools.swing.CompositeIcon;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Build a icon that will be "dimmed" if appropriate if the
 * builder has been configured "inactive".
 */
public class ActiveIconBuilder
	implements IconBuilder, Cloneable, Serializable
{
	private IconBuilder original;
	private boolean active;

	private static final ImageFilter INACTIVE_ICON_FILTER = new GrayFilter(true, 50);
	private static final long serialVersionUID = 1L;


	/**
	 * Construct an IconBuilder that will "dim" the original icon
	 * if the "active" flag is false.
	 */
	public ActiveIconBuilder(IconBuilder original, boolean active) {
		super();
		if (original == null) {
			throw new NullPointerException();
		}
		this.original = original;
		this.active = active;
	}

	/**
	 * Dim the original icon if necessary.
	 * @see IconBuilder#buildIcon()
	 */
	public Icon buildIcon() {
		Icon originalIcon = this.original.buildIcon();
		return (this.active) ? originalIcon : this.buildInactiveIcon(originalIcon);
	}

	/**
	 * Return a dimmed version of the specified icon.
	 */
	protected Icon buildInactiveIcon(Icon icon) {
		if (icon instanceof ImageIcon) {
			return this.buildInactiveImageIcon((ImageIcon) icon);
		} else if (icon instanceof CompositeIcon) {
			return this.buildInactiveCompositeIcon((CompositeIcon) icon);
		} else {
			// return the icon unmodified
			return icon;
		}
	}

	protected Icon buildInactiveImageIcon(ImageIcon imageIcon) {
		Image image = imageIcon.getImage();
		Image inactiveImage = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), INACTIVE_ICON_FILTER));
		return new ImageIcon(inactiveImage);
	}

	protected Icon buildInactiveCompositeIcon(CompositeIcon compositeIcon) {
		// leave the original unchanged
		compositeIcon = (CompositeIcon) compositeIcon.clone();
		int cnt = compositeIcon.iconCount();
		for (int i = 0; i < cnt; i++) {
			compositeIcon.setIcon(i, this.buildInactiveIcon(compositeIcon.getIcon(i)));		// recurse
		}
		return compositeIcon;
	}

	/**
	 * @see Object#clone()
	 */
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object o) {
		if (o instanceof ActiveIconBuilder) {
			return this.equals((ActiveIconBuilder) o);
		}
		return false;
	}

	public boolean equals(ActiveIconBuilder other) {
		return this.original.equals(other.original) &&
			(this.active == other.active);
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return this.original.hashCode() ^
			Boolean.valueOf(this.active).hashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return StringTools.buildToStringFor(this, this.original);
	}

}
