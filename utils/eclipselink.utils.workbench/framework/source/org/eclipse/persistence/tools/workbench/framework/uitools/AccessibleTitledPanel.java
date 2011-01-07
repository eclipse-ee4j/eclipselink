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

import java.awt.Container;
import java.awt.LayoutManager;
import javax.accessibility.AccessibleContext;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;

/**
 * This panel is meant to traverse more than just the direct parent in order
 * to retrieve the accessible name, which is usually the title of a
 * <code>GroupBox</code>. Typically, this panel should be used inside of a
 * <code>GroupBox</code> only, otherwise a <code>JPanel</code> should be used.
 */
public class AccessibleTitledPanel
	extends JPanel
{

	public AccessibleTitledPanel() {
		super();
	}

	public AccessibleTitledPanel(LayoutManager layoutManager) {
		super(layoutManager);
	}

	public AccessibleContext getAccessibleContext() {
		if (this.accessibleContext == null) {
			this.accessibleContext = new AccessibleAccessibleTitledPane();
		}
		return this.accessibleContext;
	}


	// ********** member class **********

	/**
	 * The <code>AccessibleContext</code> for <code>AccessibleTitledPanel</code>.
	 */
	protected class AccessibleAccessibleTitledPane
		extends AccessibleJPanel
	{
		/**
		 * Return whether the container is a <code>JComponent</code>
		 * that might have a titled border.
		 */
		private boolean containerIsValid(Container container) {
			return (container instanceof JComponent) &&
			 		! (container instanceof JScrollPane) &&
					! (container instanceof JSplitPane) &&
					! (container instanceof JTabbedPane);
		}

		/**
		 * Recursively search through the border hierarchy (if it exists) for a
		 * TitledBorder with a non-null title. This does a depth first search on
		 * first the inside borders then the outside borders. The assumption is
		 * that titles make really pretty inside borders but not very pretty
		 * outside borders in compound border situations. It's rather arbitrary,
		 * but hopefully decent UI programmers will not create multiple titled
		 * borders for the same component.
		 */
		private String findTitle(Border border) {
			if (border instanceof TitledBorder) {
				return ((TitledBorder) border).getTitle();
			}

			if (border instanceof CompoundBorder) {
				CompoundBorder compoundBorder = (CompoundBorder) border;
				String title = this.findTitle(compoundBorder.getInsideBorder());

				if (title == null) {
					title = this.findTitle(compoundBorder.getOutsideBorder());
				}
				return title;
			}
			return null;
		}

		/**
		 * Return the titled border text.
		 */
		public String getTitledBorderText() {
			return traverseForTitleText(AccessibleTitledPanel.this);
		}

		/**
		 * Checks to see if the given container has a titled border as its border
		 * and to see if it has a title.
		 */
		private String traverseForTitleText(Container container) {
			if (this.containerIsValid(container)) {
				JComponent jComponent = (JComponent) container;
				String title = this.findTitle(jComponent.getBorder());

				if (title != null) {
					return title;
				}
				return this.traverseForTitleText(jComponent.getParent());
			}
			return null;
		}

		/**
		 * Recursively search through the border hierarchy (if it exists) and also
		 * through the parent hierarchy for a <code>TitledBorder</code> with a
		 * non-<code>null</code> title. This does a depth first search on first
		 * the inside borders then the outside borders. The assumption is that
		 * titles make really pretty inside borders but not very pretty outside
		 * borders in compound border situations. It's rather arbitrary.
		 */
		protected String getBorderTitle(Border border) {
			return this.getTitledBorderText();
		}

	}

}
