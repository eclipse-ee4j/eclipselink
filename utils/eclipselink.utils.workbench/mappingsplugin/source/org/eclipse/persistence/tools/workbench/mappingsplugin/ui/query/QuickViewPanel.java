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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;
import org.eclipse.persistence.tools.workbench.utility.node.Node;


public abstract class QuickViewPanel extends AbstractSubjectPanel {
	
	private QuickViewModel quickViewModel;

	protected QuickViewPanel(ValueModel subjectHolder, WorkbenchContextHolder contextHolder) {
		super(subjectHolder, contextHolder);
	}

	protected void initialize(ValueModel subjectHolder) {
		super.initialize(subjectHolder);

		this.quickViewModel = new QuickViewModel();

		// Listen to the selection holder's selection in order to keep the pseudo
		// model's parent up to date
		getSubjectHolder().addPropertyChangeListener(PropertyValueModel.VALUE, buildSubjectHolderListener());

		// Set the pseudo model's parent
		this.quickViewModel.setParentNode((AbstractNodeModel) subject());
	}

	protected void initializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();

		// Label
		JLabel quickViewLabel = buildLabel("QUICK_VIEW_LABEL");

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		add(quickViewLabel, constraints);

		// Quick View list
		JList list = SwingComponentFactory.buildList(buildQuickViewListAdapter());
		list.setCellRenderer(new QuickViewRenderer());
		list.addListSelectionListener(buildListSelectionListener());
		list.addFocusListener(buildListFocusListener());
		list.addMouseListener(buildMouseListener());

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.BOTH;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(1, 0, 0, 0);

		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.getViewport().setPreferredSize(new Dimension(0, 0));
		add(scrollPane, constraints);

		// Button Panel
		JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0));

		constraints.gridx      = 0;
		constraints.gridy      = 2;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_END;
		constraints.insets     = new Insets(5, 0, 0, 0);

		add(buttonPanel, constraints);

		// Remove button
		JButton removeButton = buildButton("QUICK_VIEW_REMOVE_BUTTON");
		removeButton.putClientProperty("list", list);
		removeButton.addActionListener(buildRemoveActionListener());
		removeButton.setEnabled(false);

		buttonPanel.add(removeButton);
		list.putClientProperty("remove", removeButton);
	}

	/** On single click, the QuickViewItem will select the appropriate components **/
	private MouseListener buildMouseListener() {
		return new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				JList list = (JList) e.getComponent();
				int index = list.locationToIndex(e.getPoint());
				Rectangle cellBounds = list.getCellBounds(index, index);

				if (cellBounds != null && (cellBounds.width > 0) && (cellBounds.height > 0)) {
					((QuickViewItem) list.getModel().getElementAt(index)).select();
				}
			}
		};
	}

	/**
	 * This repaints the list when the focus is lost of gained. When there are
	 * items selected, the color of the selection is changed, which is not done
	 * by default.
	 */
	private FocusListener buildListFocusListener()
	{
		return new FocusListener()
		{
			public void focusGained(FocusEvent e)
			{
				((JList) e.getSource()).repaint();
			}

			public void focusLost(FocusEvent e)
			{
				((JList) e.getSource()).repaint();
			}
		};
	}

	private ListSelectionListener buildListSelectionListener()
	{
		return new ListSelectionListener()
		{

			private void updateRemoveButtonEnablement(JButton removeButton,
			                                          Object[] values)
			{
				// Remove button is enabled if all the selected items are removable
				boolean enabled = (values.length > 0);

				if (enabled)
				{
					for (int index = 0; index < values.length; index++)
					{
						QuickViewItem item = (QuickViewItem) values[index];
						enabled &= item.isRemovable();
					}
				}

				removeButton.setEnabled(enabled);
			}

			public void valueChanged(ListSelectionEvent e)
			{
				if (e.getValueIsAdjusting())
					return;

				JList list = (JList) e.getSource();
				Object[] values = list.getSelectedValues();

				JButton removeButton = (JButton) list.getClientProperty("remove");

				updateRemoveButtonEnablement(removeButton, values);
			}
		};
	}

	private ListModel buildQuickViewListAdapter()
	{
		return new ListModelAdapter(buildQuickViewListValueModel());
	}

	private ListValueModel buildQuickViewListValueModel()
	{
		return new ListAspectAdapter(QuickViewModel.ITEMS_LIST, this.quickViewModel)
		{
			protected ListIterator getValueFromSubject()
			{
				return ((QuickViewModel) this.subject).items();
			}

			protected int sizeFromSubject()
			{
				return ((QuickViewModel) this.subject).itemsSize();
			}
		};
	}

	private ActionListener buildRemoveActionListener()
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JButton button = (JButton) e.getSource();
				JList list = (JList) button.getClientProperty("list");
				Object[] values = list.getSelectedValues();

				for (int index = 0; index < values.length; index++) {
					((QuickViewItem) values[index]).remove();
				}
			}
		};
	}

	/**
	 * Ask the subclass to create the factory that will return the sections to
	 * be displayed in the list for the given query.
	 */
	protected abstract QuickViewSectionFactory buildSectionFactory(Node node);

	private PropertyChangeListener buildSubjectHolderListener ()
	{
		return new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				quickViewModel.setParentNode((Node) subject());
			}
		};
	}



	/**
	 * Any items in the list are an instance of this interface.
	 */
	public interface QuickViewItem {
		public void select();

		public Object getValue();
		
		public boolean isRemovable();
		public void remove();
		
		public String displayString();
		public Icon icon();
		public String accessibleName();
	}

	/**
	 * A section regroups items of the same type into a category.
	 */
	protected interface QuickViewSection extends QuickViewItem
	{
		public ListValueModel buildItemsHolder();
	}

	/**
	 * This factory creates all the available sections of the list based on the
	 * query that is been given.
	 */
	protected interface QuickViewSectionFactory
	{
		public QuickViewSection[] buildSections();
	}


	/**
	 * The renderer that formats a <code>IQuickViewItem</code>.
	 */
	private class QuickViewItemRenderer extends SimpleListCellRenderer
	{
		public String buildAccessibleName(Object value)
		{
			return ((QuickViewItem) value).accessibleName();
		}

		protected Icon buildIcon(Object value)
		{
			return ((QuickViewItem) value).icon();
		}

		protected String buildText(Object value)
		{
			return ((QuickViewItem) value).displayString();
		}

		public Component getListCellRendererComponent(JList list,
		                                              Object value,
		                                              int index,
		                                              boolean selected,
		                                              boolean hasFocus)
		{
			QuickViewItemRenderer renderer = new QuickViewItemRenderer();

			renderer.getListCellRendererComponentInternal(list, value, index, selected, hasFocus);
			renderer.updateBackground(list, selected);
			renderer.updateBorder();

			return renderer;
		}

		private Component getListCellRendererComponentInternal(JList list,
		                                                       Object value,
		                                                       int index,
		                                                       boolean selected,
		                                                       boolean hasFocus)
		{
			return super.getListCellRendererComponent(list, value, index, selected, hasFocus);
		}

		private void updateBackground(JList list, boolean selected)
		{
			if (selected)
			{
				if (list.hasFocus())
				{
					setBackground(list.getSelectionBackground());
				}
				else
				{
					setBackground(UIManager.getColor("Panel.background"));
				}
			}
			else
			{
				setBackground(list.getBackground());
			}
		}

		private void updateBorder()
		{
			setBorder(BorderFactory.createCompoundBorder
			(
				BorderFactory.createMatteBorder(0, 5, 0, 0, UIManager.getColor("List.background")),
				getBorder())
			);
		}
	}

	/**
	 * This pseudo-model aggregates lists into a single list and manages the
	 * synchronization of the list model with the underlying lists that are
	 * shown on screen with the sections.
	 */
	private class QuickViewModel extends AbstractNodeModel
	{
		public List items;
		private List sectionSynchronizers;
		public static final String ITEMS_LIST = "items";

		protected final void checkParent(Node parent)
		{
			// The parent is set/unset dynamically
		}

		private void disengageListeners()
		{
			for (Iterator iter = this.sectionSynchronizers.iterator(); iter.hasNext();)
			{
				SectionSynchronizer synchronizer = (SectionSynchronizer) iter.next();
				synchronizer.disengageListeners();
			}
		}

		public final String displayString()
		{
			return null;
		}


		protected void initialize()
		{
			super.initialize();

			this.items = new Vector();
			this.sectionSynchronizers = new Vector();
		}

		public ListIterator items()
		{
			return this.items.listIterator();
		}

		public int itemsSize()
		{
			return this.items.size();
		}

		public final void setParentNode(Node parentNode)
		{
			if (getParent() != null)
			{
				disengageListeners();
				removeItemsFromList(0, items.size(), items, ITEMS_LIST);

				sectionSynchronizers.clear();
				items.clear();
			}

			super.setParent(parentNode);

			if (parentNode != null)
			{
				updateItems();
			}
		}

		private void updateItems()
		{
			QuickViewSection[] sections = buildSectionFactory(getParent()).buildSections();

			for (int index = 0; index < sections.length; index++)
			{
				// Adds the section as an item
				QuickViewSection section = sections[index];
				items.add(section);

				// Creates a synchronizer that will keep this model in sync with
				// the items from the section
				SectionSynchronizer synchronizer = new SectionSynchronizer(section, items.size());
				sectionSynchronizers.add(synchronizer);

				// Retrieves all the children and adds them to the collection
				CollectionTools.addAll(items, synchronizer.items());
			}

			fireItemsAdded(ITEMS_LIST, 0, items);
		}

		/**
		 * This <code>SectionSynchronizer</code> keeps the pseudo-model in sync
		 * with its section it represents. Basically, upon changes in the
		 * underlying model's list, the event is updated and forwarded to the
		 * pseudo-model.
		 */
		private class SectionSynchronizer
		{
			private int index;
			private ListValueModel itemsHolder;
			private ListChangeListener listener;

			SectionSynchronizer(QuickViewSection section, int index)
			{
				super();
				initialize(section, index);
			}

			public void disengageListeners()
			{
				itemsHolder.removeListChangeListener(ValueModel.VALUE, listener);
			}

			private void initialize(QuickViewSection section, int index)
			{
				this.index = index;

				listener = new ListChangeHandler();

				itemsHolder = section.buildItemsHolder();
				itemsHolder.addListChangeListener(ValueModel.VALUE, listener);
			}

			Iterator items()
			{
				return (Iterator) itemsHolder.getValue();

			}

			int itemsSize()
			{
				return this.itemsHolder.size();
			}

			private void updateSectionsIndex(SectionSynchronizer previousSynchronizer)
			{
				int index = 1 + sectionSynchronizers.indexOf(previousSynchronizer);

				for (; index < sectionSynchronizers.size(); index++)
				{
					SectionSynchronizer synchronizer = (SectionSynchronizer) sectionSynchronizers.get(index);
					synchronizer.index = previousSynchronizer.itemsSize() + previousSynchronizer.index + 1;
					previousSynchronizer = synchronizer;
				}
			}

			private class ListChangeHandler implements ListChangeListener
			{
				public void itemsAdded(ListChangeEvent e)
				{
					// Notify the model new items have been added
					QuickViewModel.this.addItemsToList(index + e.getIndex(), CollectionTools.list(e.items()), items, ITEMS_LIST);

					// Update the index of all the sections after this one
					updateSectionsIndex(SectionSynchronizer.this);
				}

				public void itemsRemoved(ListChangeEvent e)
				{
					int size = itemsHolder.size();

					// Remove the deleted items
					QuickViewModel.this.removeItemsFromList(index + e.getIndex(), e.size(), items, ITEMS_LIST);

					// Update the index of all the sections after this one
					updateSectionsIndex(SectionSynchronizer.this);
				}

				public void itemsReplaced(ListChangeEvent e)
				{
					QuickViewModel.this.removeItemsFromList(index + e.getIndex(), e.size(), items, ITEMS_LIST);
					QuickViewModel.this.addItemsToList(index + e.getIndex(), CollectionTools.list(e.items()), items, ITEMS_LIST);
				}

				public void listChanged(ListChangeEvent e)
				{
					// Nothing to do
				}
			}
		}
	}


	
	/**
	 * The main <code>ListCellRenderer</code> which asks a sub cell renderer to
	 * format the items of the list.
	 */
	private class QuickViewRenderer implements ListCellRenderer
	{
		private ListCellRenderer[] cachedRenderers;
		private static final int ITEM_RENDERER = 0;
		private static final int SECTION_RENDERER = 1;

		QuickViewRenderer()
		{
			super();
			initialize();
		}

		public Component getListCellRendererComponent(JList list,
		                                              Object value,
		                                              int index,
		                                              boolean selected,
		                                              boolean hasFocus)
		{
			ListCellRenderer renderer = retrieveCellRenderer(value);
			return renderer.getListCellRendererComponent(list, value, index, selected, hasFocus);
		}

		private void initialize()
		{
			this.cachedRenderers = new ListCellRenderer[3];
			this.cachedRenderers[ITEM_RENDERER]       = new QuickViewItemRenderer();
			this.cachedRenderers[SECTION_RENDERER]    = new QuickViewSectionRenderer();
		}

		private ListCellRenderer retrieveCellRenderer(Object value)
		{
			if (value instanceof QuickViewSection) {
				return this.cachedRenderers[SECTION_RENDERER];
			}


			return this.cachedRenderers[ITEM_RENDERER];
		}
	}

	/**
	 * The renderer that formats a <code>IQuickViewSection</code>.
	 */
	private class QuickViewSectionRenderer implements ListCellRenderer
	{
		private Color getBackground(JList list,
		                            boolean selected,
		                            boolean hasFocus)
		{
			if (selected)
			{
				if (hasFocus)
				{
					return list.getSelectionBackground();
				}
				else
				{
					return UIManager.getColor("Panel.background");
				}
			}
			else
			{
				return list.getBackground();
			}
		}

		private Border getBorder(JList list,
		                         int index,
	                            boolean selected,
	                            boolean hasFocus)
		{
			Border border;

			if (selected)
			{
				if (hasFocus)
				{
					border = UIManager.getBorder("List.focusCellHighlightBorder");
				}
				// Background border with the underline
				else
				{
					border = BorderFactory.createCompoundBorder
					(
						BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Panel.background").darker()),
						BorderFactory.createMatteBorder(1, 1, 0, 1, UIManager.getColor("Panel.background"))
					);
				}
			}
			else
			{
				// Only the underline
				border = BorderFactory.createCompoundBorder
				(
					BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Panel.background")),
					BorderFactory.createEmptyBorder(1, 1, 0, 1)
				);
			}

			return updateBorder(border, index);
		}

		private Color getForeground(JList list,
		                            boolean selected,
		                            boolean hasFocus)
		{
			if (selected)
			{
				if (hasFocus)
				{
					return list.getSelectionForeground();
				}
				else
				{
					return UIManager.getColor("Panel.foreground");
				}
			}
			else
			{
				return list.getForeground();
			}
		}

		public Component getListCellRendererComponent(JList list,
		                                              Object value,
		                                              int index,
		                                              boolean selected,
		                                              boolean hasFocus)
		{
			JLabel label = new JLabel();
			updateUI(label, (QuickViewSection) value, list, index, selected, hasFocus);
			return label;
		}

		private Border updateBorder(Border border, int index)
		{
			// Add space before the IQuickViewItems from the previous
			// section and the new section
			if (index > 0)
			{
				return BorderFactory.createCompoundBorder
				(
					BorderFactory.createMatteBorder(5, 0, 0, 0, UIManager.getColor("List.background")),
					border
				);
			}

			return border;
		}

		private void updateUI(JLabel label,
		                      QuickViewSection section,
		                      JList list,
		                      int index,
		                      boolean selected,
		                      boolean hasFocus)
		{
			Color background = getBackground(list, selected, hasFocus);
			Color foreground = getForeground(list, selected, hasFocus);
			Border border = getBorder(list, index, selected, hasFocus);

			label.setOpaque(true);
			label.setBackground(background);
			label.setBorder(border);
			label.setComponentOrientation(list.getComponentOrientation());
			label.setEnabled(list.isEnabled());
			label.setForeground(foreground);
			label.setIcon(section.icon());
			label.setText(section.displayString());
			label.getAccessibleContext().setAccessibleName(section.accessibleName());

			Font listFont = list.getFont();
			label.setFont(new Font(listFont.getName(), Font.BOLD, listFont.getSize()));
		}
	}
}
