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
package org.eclipse.persistence.tools.workbench.framework.ui.chooser;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.CollationKey;
import java.text.Collator;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.FilteringListPanel;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.iterators.SingleElementIterator;
import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * This dialog presents a list of short class names to the user that
 * can be filtered down by typing in the text field.
 * When the user selects one of the short class names, we
 * present a list of packages that contain a class with the 
 * chosen short class name. The user selects a package
 * and there you go [gathering nuts in May].
 * 
 * Once the user presses the OK button, clients of this dialog 
 * can retrieve the selected class by calling #selection().
 * 
 * A "class" can be anything the resembles a Class and can
 * be adapted with a ClassDescriptionAdapter (e.g. java.lang.Class,
 * java.lang.String, org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ClassDescription).
 */
// TODO maybe filter inner classes based on their "local" names,
// then display their "outer" class in the "package" list alongside any packages
// e.g. "Entry" would have "java.util.Map" in the "package" list,
// possibly with a different icon alongside it
public class ClassChooserDialog extends AbstractDialog {

	/** The repository holding the list of classes. */
	private ClassDescriptionRepository repository;
	private ClassDescriptionAdapter adapter;
	private ShortClassNameEntry[] shortClassNameEntries;

	/** Hold this so we can give it the initial focus. */
	private FilteringListPanel filteringPanel;

	/** Hold this so we can manipulate it some. */
	private JList packageListBox;

	/** This listens to both list boxes. */
	private MouseListener doubleClickMouseListener;

	/** Cache the icons. */
	Icon classIcon;
	Icon packageIcon;

	/** Optional initial selection. */
	private Object initialSelection;

	/** Whether the user is allowed to clear the selection - default is false. */
	private boolean allowNullSelection;

	/** An empty package list for when no class is selected. */
	private static final Object[] EMPTY_PACKAGE_LIST = new Object[0];


	// ********** static methods **********

	/** Factory method - "class descriptions" are strings */
	public static ClassChooserDialog createDialog(ClassDescriptionRepository repository, WorkbenchContext context) {
		return createDialog(repository, DefaultClassDescriptionAdapter.instance(), context);
	}
	
	/** Factory method. */
	public static ClassChooserDialog createDialog(ClassDescriptionRepository repository, ClassDescriptionAdapter adapter, WorkbenchContext context) {
		Window window = context.getCurrentWindow();
		if (window instanceof Dialog) {
			return new ClassChooserDialog(repository, adapter, context, (Dialog) window);
		}
		return new ClassChooserDialog(repository, adapter, context);
	}

	private static String title(ApplicationContext context) {
		return context.getResourceRepository().getString("CLASS_CHOOSER_DIALOG.TITLE");
	}


	// ********** constructors **********
	
	private ClassChooserDialog(ClassDescriptionRepository repository, ClassDescriptionAdapter adapter, WorkbenchContext context) {
		super(context, title(context.getApplicationContext()));
		this.initialize(repository, adapter);
	}
	
	private ClassChooserDialog(ClassDescriptionRepository repository, ClassDescriptionAdapter adapter, WorkbenchContext context, Dialog owner) {
		super(context, title(context.getApplicationContext()), owner);
		this.initialize(repository, adapter);
	}
	
	
	// ********** initialization **********

	private void initialize(ClassDescriptionRepository cdr, ClassDescriptionAdapter cda) {
		this.repository = cdr;
		this.adapter = cda;
		this.shortClassNameEntries = this.buildShortClassNameEntries();

		this.doubleClickMouseListener = this.buildDoubleClickMouseListener();

		this.classIcon = this.resourceRepository().getIcon("class.public");
		this.packageIcon = this.resourceRepository().getIcon("package");

		this.allowNullSelection = false;
	}

	/**
	 * build a temporary map of the short class name entries keyed by
	 * short class name (this speeds up the population of the entries); but
	 * then return only the entries themselves
	 */
	private ShortClassNameEntry[] buildShortClassNameEntries() {
		Collator collator = Collator.getInstance();

		// short class name entries keyed by short class name
		Map shortClassNameEntryMap = new HashMap(20000);		// start big
		for (Iterator stream = this.repository.classDescriptions(); stream.hasNext(); ) {
			Object classDescription = stream.next();
			this.shortClassNameEntry(classDescription, shortClassNameEntryMap, collator).addPackageEntry(new PackageEntry(classDescription, this.adapter, collator));
		}
		Collection values = shortClassNameEntryMap.values();
		ShortClassNameEntry[] entries = (ShortClassNameEntry[]) values.toArray(new ShortClassNameEntry[values.size()]);
		Arrays.sort(entries);
//		this.reportMultiPackageClasses(entries);
		return entries;
	}

	/**
	 * get the short class name entry for the specified type from the specified map,
	 * creating it and adding it to the map if necessary
	 */
	private ShortClassNameEntry shortClassNameEntry(Object classDescription, Map shortClassNameEntryMap, Collator collator) {
		String shortClassName = this.adapter.shortClassName(classDescription).replace('$', '.');
		ShortClassNameEntry shortClassNameEntry = (ShortClassNameEntry) shortClassNameEntryMap.get(shortClassName);
		if (shortClassNameEntry == null) {
			shortClassNameEntry = new ShortClassNameEntry(shortClassName, collator);
			shortClassNameEntryMap.put(shortClassName, shortClassNameEntry);
		}
		return shortClassNameEntry;
	}

	/* use this method to find out just how many 
	 * classes belong to multiple packages
	 * (~3% in JDK 1.4.2)
	 */
//	private void reportMultiPackageClasses(ShortClassNameEntry[] entries) {
//		System.out.println("total classes: " + entries.length);
//		float multiPkgClasses = 0;
//		for (int i = entries.length; i-- > 0; ) {
//			if (entries[i].getPackageEntries().length > 1) {
//				multiPkgClasses++;
//			}
//		}
//		float percent = multiPkgClasses / entries.length * 100;
//		java.text.NumberFormat format = java.text.NumberFormat.getNumberInstance();
//		format.setMaximumFractionDigits(1);
//		System.out.println("multi-package classes: " + (int) multiPkgClasses + " (" + format.format(percent) + "%)");
//	}
//
	/**
	 * Build a listener that makes a double-click equivalent to
	 * clicking the OK button. This will listen to both the short class
	 * name list and the package list.
	 */
	private MouseListener buildDoubleClickMouseListener() {
		return new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					ClassChooserDialog.this.clickOK();
				}
			}
		};
	}

	private Action buildRefreshAction() {
		return new AbstractFrameworkAction(this.getWorkbenchContext()) {
			protected void initialize() {
				this.initializeTextAndMnemonic("CLASS_CHOOSER_DIALOG.REFRESH_BUTTON");
			}
			protected void execute() {
				ClassChooserDialog.this.refresh();
			}
		};
	}
	

	// ********** main panel **********

	protected Component buildMainPanel() {
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		GridBagConstraints constraints = new GridBagConstraints();

		// filtering panel
		this.filteringPanel = new FilteringListPanel(this.shortClassNameEntries, null, new ShortClassNameEntryStringConverter());
		this.configureLabel(this.filteringPanel.getTextFieldLabel(), "CLASS_CHOOSER_DIALOG.TEXT_FIELD_LABEL");
		this.configureLabel(this.filteringPanel.getListBoxLabel(), "CLASS_CHOOSER_DIALOG.CLASS_LIST_BOX_LABEL");
		this.filteringPanel.setListBoxCellRenderer(this.buildClassListCellRenderer());
		this.filteringPanel.getListBox().getSelectionModel().addListSelectionListener(this.buildClassListSelectionListener());
		this.filteringPanel.getListBox().addMouseListener(this.doubleClickMouseListener);

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 3;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(0, 0, 0, 0);
	
		mainPanel.add(this.filteringPanel, constraints);

		// package list box
		JPanel packageListPanel = new JPanel(new BorderLayout());
		JLabel packageListLabel = new JLabel();
		this.configureLabel(packageListLabel, "CLASS_CHOOSER_DIALOG.PACKAGE_LIST_BOX_LABEL");
		packageListLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		packageListPanel.add(packageListLabel, BorderLayout.PAGE_START);

		this.packageListBox = SwingComponentFactory.buildList();
		this.packageListBox.setDoubleBuffered(true);
		this.packageListBox.setCellRenderer(this.buildPackageListCellRenderer());
		this.packageListBox.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.packageListBox.getSelectionModel().addListSelectionListener(this.buildPackageListSelectionListener());
		this.packageListBox.addMouseListener(this.doubleClickMouseListener);
		packageListLabel.setLabelFor(this.packageListBox);
		packageListPanel.add(new JScrollPane(this.packageListBox), BorderLayout.CENTER);

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.anchor = GridBagConstraints.PAGE_END;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(0, 0, 0, 0);
	
		mainPanel.add(packageListPanel, constraints);

		return mainPanel;
	}

	/**
	 * Configure the specified label's text and mnemonic.
	 */
	private void configureLabel(JLabel label, String key) {
		label.setText(this.resourceRepository().getString(key));
		label.setDisplayedMnemonic(this.resourceRepository().getMnemonic(key));
	}

	private ListCellRenderer buildClassListCellRenderer() {
		return new SimpleListCellRenderer() {
			protected Icon buildIcon(Object value) {
				return ClassChooserDialog.this.classIcon;
			}
			protected String buildText(Object value) {
				return ((ShortClassNameEntry) value).getName();
			}
		};
	}

	private ListSelectionListener buildClassListSelectionListener() {
		return new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if ( ! e.getValueIsAdjusting()) {
					ClassChooserDialog.this.classSelectionChanged(e);
				}
			}
		};
	}

	private ListCellRenderer buildPackageListCellRenderer() {
		return new SimpleListCellRenderer() {
			protected Icon buildIcon(Object value) {
				return ClassChooserDialog.this.packageIcon;
			}
			protected String buildText(Object value) {
				return ((PackageEntry) value).getDisplayString();
			}
		};
	}

	private ListSelectionListener buildPackageListSelectionListener() {
		return new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if ( ! e.getValueIsAdjusting()) {
					ClassChooserDialog.this.packageSelectionChanged(e);
				}
			}
		};
	}


	// ********** AbstractDialog implementation/overrides **********

	/**
	 * nothing is selected initially - so disable the OK button
	 */
	protected Action buildOKAction() {
		Action action = super.buildOKAction();
		action.setEnabled(false);
		return action;
	}

	protected Iterator buildCustomActions() {
		return new SingleElementIterator(this.buildRefreshAction());
	}

	protected String helpTopicId() {
		return "dialog.classChooser";
	}

	protected Component initialFocusComponent() {
		return this.filteringPanel.getTextField();
	}

	protected void prepareToShow() {
		pack();
		Dimension size = getPreferredSize();
		size.width = Math.max(size.width, 350);
		size.height = Math.max(size.height, 566);
		setSize(size);
		this.setLocationRelativeTo(this.getParent());
		if (this.initialSelection != null) {
			this.setInitialSelectionInternal(this.initialSelection);
		}
	}

	/**
	 * increase visibility slightly for inner class
	 */
	protected void clickOK() {
		super.clickOK();
	}


	// ********** behavior **********

	private void setInitialSelectionInternal(Object classDescription) {
		ShortClassNameEntry[] scnEntries = this.shortClassNameEntries;
		for (int i = scnEntries.length; i-- > 0; ) {
			ShortClassNameEntry scnEntry = scnEntries[i];
			PackageEntry pEntry = scnEntry.packageEntryFor(classDescription);
			if (pEntry != null) {
				this.filteringPanel.setSelection(scnEntry);
				this.packageListBox.setSelectedValue(pEntry, true);
				break;
			}
		}
	}

	void classSelectionChanged(ListSelectionEvent e) {
		Object sel = this.filteringPanel.getSelection();
		if (sel == null) {
			this.packageListBox.setListData(EMPTY_PACKAGE_LIST);
		} else {
			this.packageListBox.setListData(((ShortClassNameEntry) sel).getPackageEntries());
			this.packageListBox.getSelectionModel().setAnchorSelectionIndex(0);
			this.packageListBox.getSelectionModel().setLeadSelectionIndex(0);
		}
	}

	void packageSelectionChanged(ListSelectionEvent e) {
		this.packageListBox.ensureIndexIsVisible(e.getFirstIndex());

		// enable the OK button only when the user has selected a package
		this.getOKAction().setEnabled(this.allowNullSelection || (this.packageListBox.getSelectedValue() != null));
	}

	void refresh() {
		this.repository.refreshClassDescriptions();
		this.shortClassNameEntries = this.buildShortClassNameEntries();
		this.filteringPanel.setCompleteList(this.shortClassNameEntries);
	}


	// ********** public API **********

	public void setAllowNullSelection(boolean allowNullSelection) {
		this.allowNullSelection = allowNullSelection;
	}

	public void setInitialSelection(Object classDescription) {
		this.initialSelection = classDescription;
	}

	/**
	 * Return the type selected by the user.
	 */
	public Object selection() {
		if ( ! this.wasConfirmed()) {
			throw new IllegalStateException();
		}
		// the package entry should only be null if allowNullSelection is true
		PackageEntry packageEntry = (PackageEntry) this.packageListBox.getSelectedValue();
		return (packageEntry == null) ? null : packageEntry.getClassDescription();
	}


	// ******************** inner classes ********************

	/**
	 * Associate a short class name with its packages.
	 */
	private static class ShortClassNameEntry implements Comparable {
		/** The type's short name. */
		private String name;
		private CollationKey collationKey;

		/** The packages containing a type the short name referenced above. */
		private PackageEntry[] packageEntries;


		ShortClassNameEntry(String name, Collator collator) {
			super();
			this.name = name;
			this.collationKey = collator.getCollationKey(name);
			// all classes will belong to at least one package; and very
			// few belong to more than one (~3% of the classes in JDK 1.4.2)
			this.packageEntries = new PackageEntry[1];
		}

		String getName() {
			return this.name;
		}

		PackageEntry[] getPackageEntries() {
			return this.packageEntries;
		}

		void addPackageEntry(PackageEntry packageEntry) {
			if (this.packageEntries[0] == null) {
				this.packageEntries[0] = packageEntry;
			} else {
				this.addSubsequentPackageEntry(packageEntry);
			}
		}

		private void addSubsequentPackageEntry(PackageEntry packageEntry) {
			String packageName = packageEntry.getName();
			PackageEntry[] oldEntries = this.packageEntries;
			int len = oldEntries.length;

			// if we have 2 packages with the same name, configure them to display their descriptions
			for (int i = len; i-- > 0; ) {
				if (oldEntries[i].getName().equals(packageName)) {
					oldEntries[i].setDisplaysAdditionalInfo(true);
					packageEntry.setDisplaysAdditionalInfo(true);
				}
			}

			PackageEntry[] newEntries = new PackageEntry[len + 1];
			System.arraycopy(oldEntries, 0, newEntries, 0, len);
			newEntries[len] = packageEntry;
			Arrays.sort(newEntries);
			this.packageEntries = newEntries;
		}

		PackageEntry packageEntryFor(Object classDescription) {
			PackageEntry[] entries = this.packageEntries;
			int len = entries.length;
			for (int i = len; i-- > 0; ) {
				if (entries[i].isEntryFor(classDescription)) {
					return entries[i];
				}
			}
			return null;
		}

		public int compareTo(Object o) {
			return this.collationKey.compareTo(((ShortClassNameEntry) o).collationKey);
		}

		public String toString() {
			return StringTools.buildToStringFor(this, this.name);
		}

	}


	/**
	 * Converts a short class name entry to a string in an obvious fashion.
	 */
	private class ShortClassNameEntryStringConverter implements StringConverter {
		public String convertToString(Object o) {
			return (o == null) ? null : ((ShortClassNameEntry) o).getName();
		}
	}


	/**
	 * Wrap a user-supplied "class description".
	 * This feels a little weird; but it *is* the selection of a package that
	 * determines which "class description" is to be returned to the client.
	 */
	private static class PackageEntry implements Comparable {
		private Object classDescription;
		private String name;
		private String additionalInfo;
		private boolean displaysAdditionalInfo;
		private String displayString;
		private Collator collator;

		PackageEntry(Object classDescription, ClassDescriptionAdapter adapter, Collator collator) {
			super();
			this.classDescription = classDescription;
			this.name = adapter.packageName(classDescription);
			this.additionalInfo = adapter.additionalInfo(this.classDescription);
			this.displaysAdditionalInfo = false;
			this.displayString = this.buildDisplayString();
			this.collator = collator;
		}

		private String buildDisplayString() {
			if (( ! this.displaysAdditionalInfo) || (this.additionalInfo == null) || (this.additionalInfo.length() == 0)) {
				return this.name;
			}
			return this.name + " - " + this.additionalInfo;
		}

		Object getClassDescription() {
			return this.classDescription;
		}

		String getName() {
			return this.name;
		}
	
		String getAdditionalInfo() {
			return this.additionalInfo;
		}
	
		boolean displaysAdditionalInfo() {
			return this.displaysAdditionalInfo;
		}
	
		void setDisplaysAdditionalInfo(boolean displaysAdditionalInfo) {
			if (this.displaysAdditionalInfo == displaysAdditionalInfo) {
				return;
			}
			this.displaysAdditionalInfo = displaysAdditionalInfo;
			// rebuild the display string
			this.displayString = this.buildDisplayString();
		}
	
		String getDisplayString() {
			return this.displayString;
		}

		boolean isEntryFor(Object otherClassDescription) {
			return this.classDescription == otherClassDescription;
		}

		public int compareTo(Object o) {
			// sort by name first...
			int result = this.collator.compare(this.name, ((PackageEntry) o).name);
			if (result != 0) {
				return result;
			}
			// ...then description
			return this.collator.compare(this.displayString, ((PackageEntry) o).displayString);
		}

		public String toString() {
			return StringTools.buildToStringFor(this, this.displayString);
		}

	}


	// ******************** static helper method ********************

	public static void gc() {
		Thread t = new Thread() {
			public void run() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException ex) {
					return;
				}
				System.gc();
			}
		};
		t.start();
	}

}
