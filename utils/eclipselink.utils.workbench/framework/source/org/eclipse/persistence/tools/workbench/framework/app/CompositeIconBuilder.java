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
package org.eclipse.persistence.tools.workbench.framework.app;

import java.io.Serializable;

import javax.swing.Icon;
import javax.swing.SwingConstants;

import org.eclipse.persistence.tools.workbench.uitools.swing.CompositeIcon;
import org.eclipse.persistence.tools.workbench.uitools.swing.EmptyIcon;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Build a composite icon that combines the icon from the
 * original icon builder with another icon, if appropriate.
 */
public class CompositeIconBuilder
	implements IconBuilder, Cloneable, Serializable
{
	private IconBuilder original;
	private boolean combine;
	private Icon icon;
	private int gap;
	private int orientation;
	private int alignment;
	private String description;

	private Icon emptyIcon;	// cache


	private static final long serialVersionUID = 1L;


	/**
	 * Construct an IconBuilder that will combine the specified
	 * icons if the "combine" flag is true; otherwise the
	 * icon will be replaced by an empty icon of the same size.
	 */
	public CompositeIconBuilder(IconBuilder original, boolean combine, Icon icon, int gap, int orientation, int alignment, String description) {
		super();
		if (original == null) {
			throw new NullPointerException();
		}
		this.original = original;
		this.combine = combine;
		this.icon = icon;
		this.gap = gap;
		this.orientation = orientation;
		this.alignment = alignment;
		this.description = description;
	}

	/**
	 * Construct an IconBuilder that will combine the specified
	 * icons if the "combine" flag is true; otherwise the
	 * icon will be replaced by an empty icon of the same size.
	 */
	public CompositeIconBuilder(IconBuilder original, boolean combine, Icon icon) {
		this(original, combine, icon, 0, SwingConstants.HORIZONTAL, SwingConstants.CENTER, null);
	}

	/**
	 * Build an Icon that will be a composite of the icon from the original
	 * icon builder and another icon.
	 * @see IconBuilder#buildIcon()
	 */
	public Icon buildIcon() {
		Icon icon1 = this.original.buildIcon();
		Icon icon2 = (this.combine) ? this.icon : this.getEmptyIcon();
		return this.buildCompositeIcon(icon1, icon2);
	}

	/**
	 * Build a composite icon with the specified icons
	 * and the builder's settings.
	 */
	protected Icon buildCompositeIcon(Icon icon1, Icon icon2) {
		CompositeIcon compositeIcon = new CompositeIcon(icon1, this.gap, icon2);
		compositeIcon.setOrientation(this.orientation);
		compositeIcon.setAlignment(this.alignment);
		compositeIcon.setDescription(this.description);
		return compositeIcon;
	}

	/**
	 * Only build the empty icon if necessary, then cache it.
	 */
	protected Icon getEmptyIcon() {
		if (this.emptyIcon == null) {
			this.emptyIcon = this.buildEmptyIcon();
		}
		return this.emptyIcon;
	}

	/**
	 * Build an empty icon the same size as the "normal"
	 * icon so the resulting composite icon is the same size overall.
	 */
	protected Icon buildEmptyIcon() {
		return new EmptyIcon(this.icon.getIconWidth(), this.icon.getIconHeight());
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
		if (o instanceof CompositeIconBuilder) {
			return this.equals((CompositeIconBuilder) o);
		}
		return false;
	}

	public boolean equals(CompositeIconBuilder other) {
		return this.original.equals(other.original) &&
			(this.combine == other.combine) &&
			((this.icon == null) ? other.icon == null : this.icon.equals(other.icon)) &&
			(this.gap == other.gap) &&
			(this.orientation == other.orientation) &&
			(this.alignment == other.alignment) &&
			((this.description == null) ? other.description == null : this.description.equals(other.description));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return this.original.hashCode() ^
			Boolean.valueOf(this.combine).hashCode() ^
			((this.icon == null) ? 0 : this.icon.hashCode()) ^
			this.gap ^
			this.orientation ^
			this.alignment ^
			((this.description == null) ? 0 : this.description.hashCode());
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return StringTools.buildToStringFor(this, this.original);
	}

}
