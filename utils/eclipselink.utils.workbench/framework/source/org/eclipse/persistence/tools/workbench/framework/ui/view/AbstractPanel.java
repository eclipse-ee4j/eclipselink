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
package org.eclipse.persistence.tools.workbench.framework.ui.view;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Window;
import java.util.prefs.Preferences;
import javax.accessibility.Accessible;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.Application;
import org.eclipse.persistence.tools.workbench.framework.NodeManager;
import org.eclipse.persistence.tools.workbench.framework.app.NavigatorSelectionModel;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.DefaultWorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.context.PreferencesContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.help.HelpManager;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.uitools.AccessibleTitledPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.ComponentAligner;
import org.eclipse.persistence.tools.workbench.framework.uitools.Pane;
import org.eclipse.persistence.tools.workbench.framework.uitools.Spacer;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;

/**
 * AbstractPanel holds a context and provides 
 * convenience methods for the NodeManager, ResourceRepository,
 * HelpManager, etc.
 * 
 * This abstract panel contains convenience methods for building
 * buttons, labels, check boxes, and radio buttons with their text, mnemonic,
 * and mnemonic index. To build some components you must pass in the 
 * associated model. This class ensures that the mnemonic and model are
 * set in the proper order to avoid a Swing bug.
 * 
 * See  #AbstractPropertiesPage and #AbstractSubjectPanel before subclassing #AbstractPanel.
 * Note: There is no initializeLayout method in this abstract class
 */
public abstract class AbstractPanel extends AccessibleTitledPanel {
		
	/**
	 * This <code>ComponentAligner</code> is responsible to properly align left all
	 * the components added to the group.
	 */
	private ComponentAligner alignLeftGroup;

	/**
	 * This <code>ComponentAligner</code> is responsible to properly align right
	 * all the components added to the group.
	 */
	private ComponentAligner alignRightGroup;

	/**
	 * The workbench context holder provides a level of indirection that allows us
	 * to swap in a different workbench context when the panel is installed in a
	 * particular window. When the panel is first being built the workbench context
	 * will be empty except for its application context (i.e. the current window,
	 * the navigator selection model, and anything else tied to the window the
	 * panel is displayed in will be unavailable).
	 */
	private WorkbenchContextHolder workbenchContextHolder;


	/**
	 * some panels only need an app context; accessing the workbench
	 * context will trigger a runtime exception
	 */
	protected AbstractPanel(ApplicationContext context) {
		this(new GridBagLayout(), context);
	}

	/**
	 * some panels only need an app context; accessing the workbench
	 * context will trigger a runtime exception
	 */
	protected AbstractPanel(LayoutManager layoutManager, ApplicationContext context) {
		this(layoutManager, new DefaultWorkbenchContextHolder(context));
	}

	/**
	 * the workbench context must be accessed indirectly so it can be
	 * switched whenever the panel is displayed in a different window
	 */
	protected AbstractPanel(WorkbenchContextHolder contextHolder) {
		this(new GridBagLayout(), contextHolder);
	}

	/**
	 * the workbench context must be accessed indirectly so it can be
	 * switched whenever the panel is displayed in a different window
	 */
	protected AbstractPanel(LayoutManager layoutManager, WorkbenchContextHolder contextHolder) {
		super(layoutManager);
		this.workbenchContextHolder = contextHolder;
	}



	/**
	 * Adds the given group for left alignment. Its preferred width will be used
	 * along with the width of all the other components in order to get the
	 * widest component and use its width as the width for all the components.
	 * 
	 * @param component The group containing component to be aligned with the
	 * component contained in this panel's group
	 */
	protected final void addAlignLeft(ComponentAligner group)
	{
		getAlignLeftGroup().add(group);
	}

	/**
	 * Adds the given component for left alignment. Its preferred width will be
	 * used along with the width of all the other components in order to get the
	 * widest component and use its width as the width for all the components.
	 * 
	 * @param component The component to be properly aligned
	 */
	protected final void addAlignLeft(JComponent component)
	{
		getAlignLeftGroup().add(component);
	}

	/**
	 * Adds the given group for left alignment. Its preferred width will be used
	 * along with the width of all the other components in order to get the
	 * widest component and use its width as the width for all the components.
	 * 
	 * @param component The group containing component to be aligned with the
	 * component contained in this panel's group
	 */
	protected final void addAlignRight(ComponentAligner group)
	{
		getAlignRightGroup().add(group);
	}

	/**
	 * Adds the given component for left alignment. Its preferred width will be
	 * used along with the width of all the other components in order to get the
	 * widest component and use its width as the width for all the components.
	 * 
	 * @param component The component to be properly aligned
	 */
	protected final void addAlignRight(JComponent component)
	{
		getAlignRightGroup().add(component);
	}
	
	protected final void addHelpTopicId(Component component, String topicId) {
		helpManager().addTopicID(component, topicId);
	}	

	/**
	 * Adds the given component for left alignment. Its preferred width will be
	 * used along with the width of all the other components in order to get the
	 * widest component and use its width as the width for all the components.
	 * 
	 * @param panel The panel to be properly aligned with this panel
	 */
	protected final void addPaneForAlignLeft(AbstractPanel page)
	{
		getAlignLeftGroup().add(page.getAlignLeftGroup());
	}

	/**
	 * Adds the given component for left alignment. Its preferred width will be
	 * used along with the width of all the other components in order to get the
	 * widest component and use its width as the width for all the components.
	 * 
	 * @param panel The panel to be properly aligned with this panel
	 */
	protected final void addPaneForAlignment(AbstractPanel panel)
	{
		addPaneForAlignLeft(panel);
		addPaneForAlignRight(panel);
	}

	/**
	 * Adds the given component for left alignment. Its preferred width will be
	 * used along with the width of all the other components in order to get the
	 * widest component and use its width as the width for all the components.
	 * 
	 * @param panel The panel to be properly aligned with this panel
	 */
	protected final void addPaneForAlignRight(AbstractPanel page)
	{
		getAlignRightGroup().add(page.getAlignRightGroup());
	}
	
	/**
	 * Creates a new <code>JButton</code> and sets  the text,
	 * mnemonic and mnemonic index using the given resource key.
	 */
	protected final JButton buildButton(String key)
	{
		return SwingComponentFactory.buildButton(key, resourceRepository());
	}
	
	/**
	 * Creates a new <code>JButton</code> and sets the text, mnemonic and
	 * mnemonic index using the given resource key.
	 * It also uses the given labeler to augment the accessible context of the 
	 * button.
	 */
	protected final JButton buildBrowseButton(String key, Accessible labeler) {
		return SwingComponentFactory.buildBrowseButton(key, this.resourceRepository(), labeler);
	}
	
	/**
	 * Creates a new <code>JCheckBox</code> and sets the text, mnemonic, and
	 * mnemonic index using the given resource key.
	 * Must pass in a buttonModel because of a bug in Swing.
	 * The mnemonic will only work properly if set after the model is set.
	 */
	protected final JCheckBox buildCheckBox(String key, ButtonModel buttonModel)
	{
		return SwingComponentFactory.buildCheckBox(key, buttonModel, resourceRepository());
	}

	/**
	 * Creates a new <code>JLabel</code> and sets at the text,
	 * mnemonic and mnemonic index using the given resource key.
	 */
	protected final JLabel buildLabel(String key)
	{
		return SwingComponentFactory.buildLabel(key, resourceRepository());
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @return A new component containing a label and a combo box
	 */
	protected final JComponent buildLabeledComboBox(String key, ComboBoxModel model)
	{
		return buildLabeledComboBoxImp(key, model, null, false);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @return A new component containing a label and a combo box
	 */
	protected final JComponent buildLabeledComboBox(String key, ComboBoxModel model, ListCellRenderer renderer)
	{
		return buildLabeledComboBoxImp(key, model, renderer, false);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @return A new component containing a label and a combo box
	 */
	private JComponent buildLabeledComboBoxImp(String key, ComboBoxModel model, ListCellRenderer renderer, boolean editable)
	{
		JComboBox comboBox = new JComboBox(model);
		comboBox.setEditable(editable);
	
		if (renderer != null)
			comboBox.setRenderer(renderer);
	
		return buildLabeledComponent(key, comboBox);
	}

	/**
	 * Creates a container where a label and the given component are laid out
	 *
	 * @param key The key used to retrieve the localized string
	 * @param component The component that is labeled
	 * @return A new component containing a label and a text field
	 */
	protected final JComponent buildLabeledComponent(String key, JComponent component)
	{
		return buildLabeledComponent(key, component, new Spacer());
	}

	/**
	 * Creates a container that has a label and the given chooser.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param component The component that is labeled
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @return A new component containing a label and the chooser
	 */
	protected JComponent buildLabeledComponent(String key, JComponent component, JComponent rightComponent)
	{
		GridBagConstraints constraints = new GridBagConstraints();
		Pane pane = new Pane(new GridBagLayout());

		// Left component
		JLabel label = buildLabel(key);

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		pane.add(label, constraints);
		addAlignLeft(label);

		// Center component
		constraints.gridx      = 1;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 5, 0, 0);

		pane.add(component, constraints);
		component.setName(key);
		label.setLabelFor(component);

		// Right component
		if (rightComponent != null)
		{
			constraints.gridx      = 2;
			constraints.gridy      = 0;
			constraints.gridwidth  = 1;
			constraints.gridheight = 1;
			constraints.weightx    = 0;
			constraints.weighty    = 0;
			constraints.fill       = GridBagConstraints.NONE;
			constraints.anchor     = GridBagConstraints.LINE_START;
			constraints.insets     = new Insets(0, 5, 0, 0);
	
			pane.add(rightComponent, constraints);
			rightComponent.setName(key);
			addAlignRight(rightComponent);

			if (rightComponent instanceof AbstractButton)
			{
				SwingComponentFactory.updateButtonAccessibleName(label, (AbstractButton) rightComponent);
			}
		}
	
		return pane;
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @return A new component containing a label and a combo box
	 */
	protected final JComponent buildLabeledEditableComboBox(String key, ComboBoxModel model)
	{
		return buildLabeledComboBoxImp(key, model, null, true);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @return A new component containing a label and a combo box
	 */
	protected final JComponent buildLabeledEditableComboBox(String key, ComboBoxModel model, ListCellRenderer renderer)
	{
		return buildLabeledComboBoxImp(key, model, renderer, true);
	}

	/**
	 * Creates a container that has a label and a spinner.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The spinner's model, which only handles number
	 * @return A new component containing a label and a spin button
	 */
	protected final JComponent buildLabeledSpinnerNumber(String key, SpinnerNumberModel model)
	{
		return buildLabeledSpinnerNumber(key, model, 4);
	}

	/**
	 * Creates a container that has a label and a spinner.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The spinner's model, which only handles number
	 * @param columns The number of columns the spinner's editor should have, the
	 * default value is 4, even though on screen the width is greater than 4, a
	 * column count of 3 makes it too narrow
	 * @return A new component containing a label and a spin button
	 */
	protected final JComponent buildLabeledSpinnerNumber(String key, SpinnerNumberModel model, int columns)
	{
		Pane pane = new Pane(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		// Left component
		JLabel label = buildLabel(key);
		pane.add(label, constraints);
		addAlignLeft(label);

		// Center component
		JSpinner spinner = SwingComponentFactory.buildSpinnerNumber(model, columns);
		spinner.setName(key);

		constraints.weightx = 1;
		constraints.fill    = GridBagConstraints.NONE;
		constraints.anchor  = GridBagConstraints.LINE_START;
		constraints.insets  = new Insets(0, 5, 0, 0);

		pane.add(spinner, constraints);
		label.setLabelFor(spinner);

		// Right component
		Spacer spacer = new Spacer();

		constraints.weightx = 0;
		constraints.fill    = GridBagConstraints.NONE;

		pane.add(spacer, constraints);
		addAlignRight(spacer);

		return pane;
	}

	/**
	 * Creates a container that has a label and a text field.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param document The document of the text field
	 * @return A new component containing a label and a text field
	 */
	protected final JComponent buildLabeledTextField(String key, Document document)
	{
		return buildLabeledTextField(key, document, new Spacer());
	}

	/**
	 * Creates a container that has a label and a text field.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param document The document of the text field
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed
	 * @return A new component containing a label and a text field
	 */
	protected final JComponent buildLabeledTextField(String key, Document document, JComponent rightComponent) {
		JTextField textField = new JTextField(document, null, 1);
		return buildLabeledComponent(key, textField, rightComponent);
	}

	/**
	 * Creates a new <code>JRadioButton</code> and sets the text, mnemonic, and
	 * mnemonic index using the given resource key.  
	 * Must pass in a buttonModel because of a bug in Swing.
	 * The mnemonic will only work properly if set after the model is set.
	 */
	protected final JRadioButton buildRadioButton(String key, ButtonModel buttonModel) {
		return SwingComponentFactory.buildRadioButton(key, buttonModel, resourceRepository());
	}


	/**
	 * Creates a new titled border with the text retrieved from the resource
	 * repository using the given key.
	 *
	 * @param key The key used to retrieve the localized string
	 * @return A new <code>TitledBorder</code>
	 */
	protected final Border buildTitledBorder(String key) {
		return SwingComponentFactory.buildTitledBorder(resourceRepository(), key);
	}

	/**
	 * Returns the standard 2-pixel, empty border
	 * for placing around panels etc.
	 */
	protected final Border buildStandardEmptyBorder() {
		return SwingComponentFactory.buildStandardEmptyBorder();
	}

	/**
	 * Returns the <code>ComponentAligner</code> that is responsible to align all
	 * the components that are on the left side of a pane, usually those
	 * components are labels.
	 *
	 * @return The <code>ComponentAligner</code> that makes sure all components
	 * with it have the same width
	 */
	public ComponentAligner getAlignLeftGroup() {
		if (this.alignLeftGroup == null) {
			this.alignLeftGroup = new ComponentAligner();
		}
		return this.alignLeftGroup;
	}

	/**
	 * Returns the <code>ComponentAligner</code> that is responsible to align all
	 * the components that are on the right side of a pane, usually those
	 * components are browse buttons.
	 *
	 * @return The <code>ComponentAligner</code> that makes sure all components
	 * with it have the same width
	 */
	public ComponentAligner getAlignRightGroup() {
		if (this.alignRightGroup == null) {
			this.alignRightGroup = new ComponentAligner();
		}
		return this.alignRightGroup;
	}

	/**
	 * Build a spinner that handles Dates.
	 */
	protected final JSpinner buildSpinnerDate(SpinnerDateModel model) {
		JSpinner spinner = new JSpinner();
		spinner.setModel(model);
		return spinner;
	}


	public final ApplicationContext getApplicationContext() {
		return this.getWorkbenchContext().getApplicationContext();
	}
	
	public final Application application() {
		return this.getApplicationContext().getApplication();
	}
	
	public final Preferences preferences() {
		return this.getApplicationContext().getPreferences();
	}
	
	public final ResourceRepository resourceRepository() {
		return this.getApplicationContext().getResourceRepository();
	}

	public final HelpManager helpManager() {
		return this.getApplicationContext().getHelpManager();
	}

	public final WorkbenchContextHolder getWorkbenchContextHolder() {
		return this.workbenchContextHolder;
	}
	
	public final WorkbenchContext getWorkbenchContext() {
		return this.workbenchContextHolder.getWorkbenchContext();
	}
	
    public final NavigatorSelectionModel navigatorSelectionModel() {
        return this.getWorkbenchContext().getNavigatorSelectionModel();
    }
	
	/**
	 * Return the current window. This will be needed to open dialogs.
	 */
	public final Window currentWindow() {
		return this.getWorkbenchContext().getCurrentWindow();
	}

	public final NodeManager nodeManager() {
		return this.getApplicationContext().getNodeManager();
	}
	

	public final PreferencesContext getPreferencesContext() {
		return (PreferencesContext) this.getApplicationContext();
	}

}
