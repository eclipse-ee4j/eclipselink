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
package org.eclipse.persistence.tools.workbench.platformsplugin.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;

/**
 * Factory class that creates a platform combo box or list that shows the
 * database platform in which the Oracle platforms are shown first.
 */
public final class PlatformComponentFactory
{
	private PlatformComponentFactory() {
		super();
	}

	/**
	 * for now, this is a static list that does not stay in synch with
	 * the platform model; the list contains all the platforms in the
	 * platform repository, sorted in a "marketing-sensitive" fashion
	 * and with a null separating the Oracle platforms from the
	 * competition; the renderer will need to handle the null appropriately
	 */
	static ListValueModel buildDatabasePlatformCollectionHolder() {
		// first sort the platforms in a "marketing-sensitive" fashion
		List platforms = new ArrayList(DatabasePlatformRepository.getDefault().platformsSize());
		CollectionTools.addAll(platforms, DatabasePlatformRepository.getDefault().platforms());
		CollectionTools.sort(platforms, new PlatformComparator());

		// add a separator (null) between the Oracle platforms and the others
		for (int i = 0; i < platforms.size(); i++) {
			DatabasePlatform platform = (DatabasePlatform) platforms.get(i);
			String platformName = platform.getName().toLowerCase();

			if (platformName.indexOf("oracle") == -1) {
				platforms.add(i, null);
				break;
			}
		}
		return new SimpleListValueModel(platforms);
	}

	public static JComboBox buildPlatformChooser(PropertyValueModel selectionHolder) {
		return new PlatformChooser(selectionHolder);
	}

	public static JList buildPlatformList(PropertyValueModel selectionHolder) {
		return new PlatformList(selectionHolder);
	}

	// ********** inner classes **********

	/**
	 * list the platforms with the Oracle platforms at the top (most recent
	 * version first) and with a line separating the Oracle platforms from
	 * the competition
	 */
	private static class PlatformChooser extends JComboBox {

		/**
		 * Construct a platform chooser for the specified selection.
		 */
		public PlatformChooser(PropertyValueModel platformSelectionHolder) {
			super();
			this.setModel(new PlatformModelAdapter(buildDatabasePlatformCollectionHolder(), platformSelectionHolder));
			this.setRenderer(new PlatformCellRenderer());
		}
	}

	/**
	 * render a null value as a separator
	 */
	private static class PlatformCellRenderer extends SimpleListCellRenderer {

		private String buildSeparator(JList list) {
			// calculate the separator's length
			int width = list.getSize().width - list.getInsets().left - list.getInsets().right;
			int length = width / SwingUtilities.computeStringWidth(list.getFontMetrics(list.getFont()), "\u2212") - 2;

			StringBuffer sb = new StringBuffer(" ");
			for (int i = 0; i < length; i++) {
				sb.append("\u2212");
			}
			return sb.toString();
		}

		protected String buildText(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			return (value == null) ? this.buildSeparator(list) : ((DatabasePlatform) value).getName();
		}
	}

	/**
	 * sort the original list of database platforms in a "marketing-sensitive" fashion
	 * (i.e. Oracle platforms first, latest Oracle version first).
	 */
	private static class PlatformComparator implements Comparator {

		public int compare(Object o1, Object o2) {
			DatabasePlatform platform1 = (DatabasePlatform) o1;
			DatabasePlatform platform2 = (DatabasePlatform) o2;

			String platformName1 = platform1.getName().toLowerCase();
			String platformName2 = platform2.getName().toLowerCase();

			boolean oraclePlatform1 = platformName1.indexOf("oracle") != -1;
			boolean oraclePlatform2 = platformName2.indexOf("oracle") != -1;

			// Oracle platforms come first (!)
			if (oraclePlatform1 && ! oraclePlatform2)
				return -1;

			// non-Oracle platforms come after all the Oracle platforms
			if ( ! oraclePlatform1 && oraclePlatform2)
				return 1;

			// Oracle platforms are sorted in reverse numerical order (10, 9, 8,...)
			if (oraclePlatform1) {
				return this.oracleVersionNumber(platformName2) - this.oracleVersionNumber(platformName1);
			}

			// non-Oracle platforms are sorted alphabetically
			return platform1.compareTo(platform2);
		}

		/**
		 * extract the version number from the specified Oracle platform
		 * name (e.g. Oracle10g -> 10)
		 */
		private int oracleVersionNumber(String oraclePlatformName) {
			StringBuffer sb = new StringBuffer();
			int len = oraclePlatformName.length();
			for (int i = 0; i < len; i++) {
				char c = oraclePlatformName.charAt(i);
				if (Character.isDigit(c)) {
					sb.append(c);
				}
			}
			return (sb.length() == 0) ? 0 : Integer.parseInt(sb.toString());
		}
	}

	/**
	 * list the platforms with the Oracle platforms at the top (most recent
	 * version first) and with a line separating the Oracle platforms from
	 * the competition
	 */
	private static class PlatformList extends SwingComponentFactory.AccessibleList {

		/**
		 * Construct a platform chooser for the specified selection.
		 */
		public PlatformList(PropertyValueModel platformSelectionHolder) {
			super();
			this.setModel(new PlatformModelAdapter(buildDatabasePlatformCollectionHolder(), platformSelectionHolder));
			this.setCellRenderer(new PlatformCellRenderer());
			this.addListSelectionListener(buildListSelectionListener(platformSelectionHolder));
		}

		/**
		 * JList does persist the selection model into its model so we need to add
		 * a ListSelectionListener.
		 */
		private ListSelectionListener buildListSelectionListener(final PropertyValueModel platformSelectionHolder) {
			return new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (!e.getValueIsAdjusting()) {
						platformSelectionHolder.setValue(getSelectedValue());
					}
				}
			};
		}
	}

	/**
	 * tweak the standard adapter to prevent selection of the divider,
	 * which is represented by a null in the list
	 */
	private static class PlatformModelAdapter extends ComboBoxModelAdapter {

		PlatformModelAdapter(ListValueModel listHolder, PropertyValueModel selectionHolder) {
			super(listHolder, selectionHolder);
		}

		public int indexOf(Object item) {
			Iterator iter = (Iterator) this.listHolder.getValue();
			int index = 0;
			boolean found = false;

			while (iter.hasNext()) {
				if (item == iter.next()) {
					found = true;
					break;
				}

				index++;
			}

			if (!found) {
				index = -1;
			}

			return index;
		}

		public void setSelectedItem(Object item) {
			// If null is to be selected because it's the separator, then select
			// the next item in the list: which could be either the item above or
			// below the separator, this is required when traversing the combo box
			// using the keyboard
			if (item == null) {
				int newIndex = indexOf(item);
				int oldIndex = indexOf(getSelectedItem());

				if (newIndex > oldIndex)
					item = getElementAt(++newIndex);
				else
					item = getElementAt(--newIndex);
			}

			super.setSelectedItem(item);
		}
	}
}
