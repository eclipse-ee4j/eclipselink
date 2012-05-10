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
package org.eclipse.persistence.tools.workbench.framework.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.Document;
import javax.swing.tree.TreeModel;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WindowWorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.help.HelpManager;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.ComponentAligner;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.CellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.uitools.swing.ExpandablePane;
import org.eclipse.persistence.tools.workbench.uitools.swing.TriStateCheckBox;
import org.eclipse.persistence.tools.workbench.uitools.swing.TriStateCheckBox.TriStateButtonModel;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.string.BidiStringConverter;


/**
 * Subclass this modal dialog if you need a custom dialog that follows the Oracle UI
 * standards found at
 * <a href="http://scout.us.oracle.com/desktopuiguide/dialogs.html">Oracle Dialogs</a>.
 * 
 * Subclass AbstractValidatingDialog if you need to display error messages
 * whenever the user enters invalid data in the dialog.
 * 
 * Use JOptionPane if you need a simple message or error notification.
 * 
 * Subclass JDialog directly if you need greater custom functionality.
 * 
 * Here is the layout of this dialog:
 * <pre>
 *   ________________________________________
 *   | Title                                |
 *   |--------------------------------------|
 *   | ____________________________________ |
 *   | |                                  | |
 *   | |                                  | |
 *   | |          Main panel              | |
 *   | |                                  | |
 *   | |                                  | |
 *   | ------------------------------------ |
 *   | ____________________________________ |
 *   | ______        ________ ____ ________ |
 *   | |Help|        |Custom| |OK| |Cancel| | <- custom buttons defined by subclass appear "left" of OK 
 *   | ------        -------- ---- -------- |
 *   ----------------------------------------
 * </pre>
 * 
 * In accordance with Oracle UI standards, the OK and Cancel buttons do
 * not have mnemonics.
 * 	
 * 
 * Subclasses must IMPLEMENT the following methods:
 * 	- buildMainPanel()
 * 	- helpTopicId()
 * 
 * Subclasses can OVERRIDE the following methods, if necessary:
 * 	- initialFocusComponent()
 * 	- prepareToShow()
 * 	- buildWindowListener() / windowOpened()
 * 	- buildOKAction() / buildOKText() / okPressed()
 * 	- preConfirm() / okConfirmed()
 * 	- buildCancelAction() / buildCancelText() / cancelPressed()
 * 	- buildHelpAction() / buildHelpText() / helpPressed()
 * 	- cancelButtonIsVisible()
 * 	- buildCustomActions()
 * 
 * Subclasses can CALL the following methods:
 *  	- getButtonForAction(Action)
 * 	- setButtonFont(Font)
 * 	- setDefaultAction(Action)
 * 	- click(Action) / clickOK() / clickCancel()
 * 	- various context convenience methods
 * 
 */
public abstract class AbstractDialog extends JDialog implements SwingConstants {

	/** supplies strings, mnemonics, accelerators, icons, etc. */
	private WorkbenchContext context;
	
	/**
	 * This container holds onto the content of this dialog, which is the
	 * description pane (if required) and the main panel.
	 */
	protected AbstractPanel container;

	/** contains any subclass-defined custom buttons */
	private JPanel customButtonPanel;

	/** contains the OK and Cancel buttons */
	private JPanel rightButtonPanel;

	/** the default actions/buttons */
	private Action okAction;
	private Action cancelAction;
	private Action helpAction;

	/**
	 * the dialog's buttons, keyed by action;
	 * allowing us to set the default button by specifying an action
	 * instead of a button
	 */
	private Map buttons;
	
	/**
	 * Indicates whether the dialog content pane has been built.
	 */
	private boolean built = false;

	/** true if the OK button was pressed */
	private boolean wasConfirmed;


	// ********** constructors **********

	/**
	 * Do not use this constructor if the owner is a Dialog.
	 * @see #AbstractDialog(WorkbenchContext, Dialog)
	 */
	protected AbstractDialog(WorkbenchContext context) {
		this(context, (String) null);
	}

	/**
	 * Do not use this constructor if the owner is a Dialog.
	 * @see #AbstractDialog(WorkbenchContext, String, Dialog)
	 */
	protected AbstractDialog(WorkbenchContext context, String title) {
		super((Frame) context.getCurrentWindow(), title, true);		// true = modal
		this.initialize(context);
	}

	/**
	 * This constructor should be used when the Dialog has another
	 * Dialog as its owner.
	 */
	protected AbstractDialog(WorkbenchContext context, Dialog owner) {
		this(context, null, owner);
	}

	/**
	 * This constructor should be used when the Dialog has another
	 * Dialog as its owner.
	 */
	protected AbstractDialog(WorkbenchContext context, String title, Dialog owner) {
		super(owner, title, true);		// true = modal
		this.initialize(context);
	}


	// ********** initialization **********

	protected void initialize(WorkbenchContext ctx) {
		this.context = new WindowWorkbenchContext(ctx, this);
		this.initialize();
	}

	protected void initialize() {
		this.buttons = new HashMap();
		this.wasConfirmed = false;
		container = new AbstractPanel(new GridBagLayout(), getApplicationContext()) {};
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.initializeActions();
		this.initializeDefaultKeyboardActions();
		this.addWindowListener(this.buildWindowListener());
		// if you override this method, don't forget to include:
		// super.initialize();
	}

	protected void initializeActions() {
		this.okAction = buildOKAction();
		this.cancelAction = buildCancelAction();
		this.helpAction = buildHelpAction();
	}
	
	
	// ********** GUI **********

	protected void initializeContentPane() {
		GridBagConstraints constraints = new GridBagConstraints();

		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		container.setOpaque(false);
		contentPane.add(container, BorderLayout.CENTER);
		
		// main panel
		constraints.gridx = 		0;
		constraints.gridy = 		0;
		constraints.gridwidth = 	1;
		constraints.gridheight = 	1;	
		constraints.weightx = 		1;
		constraints.weighty = 		1;
		constraints.fill = 			GridBagConstraints.BOTH;
		constraints.anchor = 		GridBagConstraints.NORTH;
		constraints.insets = 		new Insets(10, 5, 0, 5);
		
		Component mainPanel = this.buildMainPanel();
		this.helpManager().addTopicID(mainPanel, this.helpTopicId());
		container.add(mainPanel, constraints);

		// separator
		constraints.gridx = 		0;
		constraints.gridy = 		1;
		constraints.gridwidth = 	1;
		constraints.gridheight = 	1;	
		constraints.weightx = 		1;
		constraints.weighty = 		0;
		constraints.fill = 			GridBagConstraints.HORIZONTAL;
		constraints.anchor = 		GridBagConstraints.NORTH;
		constraints.insets = 		new Insets(10, 5, 10, 5);

		container.add(new JSeparator(), constraints);
		
		// button panel
		JPanel buttonPanel = this.buildButtonPanel();
		buttonPanel.setBorder(new EmptyBorder(0, 0, 5, 0));

		constraints.gridx = 		0;
		constraints.gridy = 		2;
		constraints.gridwidth = 	1;
		constraints.gridheight = 	1;	
		constraints.weightx = 		0;
		constraints.weighty = 		0;
		constraints.fill = 			GridBagConstraints.HORIZONTAL;
		constraints.anchor = 		GridBagConstraints.NORTH;
		constraints.insets = 		new Insets(0, 0, 0, 0);

		container.add(buttonPanel, constraints);
	}

	/** 
	 * The dialog is built when the show() method is called not
	 * when the Dialog is constructed
	 */
	protected abstract Component buildMainPanel();

	protected JPanel buildButtonPanel() {		
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		ComponentAligner aligner = new ComponentAligner();

		// left button panel
		JPanel leftButtonPanel = new JPanel(new GridLayout(1, 0, 5, 5));

		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 5, 0, 0);

			JButton helpButton = new JButton(this.helpAction);
			this.buttons.put(this.helpAction, helpButton);
			leftButtonPanel.add(helpButton);
			aligner.add(helpButton);
			
		buttonPanel.add(leftButtonPanel, constraints);

		// space between left and center button panels
		Box leftCenterButtonPanelSpace = Box.createHorizontalBox();

		constraints.gridx			= 1;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 10, 0, 0);

		buttonPanel.add(leftCenterButtonPanelSpace, constraints);

		// custom (center) button panel
		this.customButtonPanel = this.buildCustomButtonPanel(aligner);

		constraints.gridx			= 2;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.EAST;
		constraints.insets		= new Insets(0, 0, 0, 5);

		buttonPanel.add(this.customButtonPanel, constraints);

		// right button panel
		this.rightButtonPanel = new JPanel(new GridLayout(1, 0, 5, 5));

		constraints.gridx			= 4;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.EAST;
		constraints.insets		= new Insets(0, 0, 0, 5);

			JButton okButton = new JButton(this.okAction);
			this.buttons.put(this.okAction, okButton);
			this.rightButtonPanel.add(okButton);
			aligner.add(okButton);
			
			JButton cancelButton = new JButton(this.cancelAction);
			this.buttons.put(this.cancelAction, cancelButton);
			if (this.cancelButtonIsVisible()) {
				this.rightButtonPanel.add(cancelButton);
				aligner.add(cancelButton);
			}
					
		buttonPanel.add(this.rightButtonPanel, constraints);		
		
		this.setDefaultAction(this.okAction);

		return buttonPanel;
	}

	protected JPanel buildCustomButtonPanel(ComponentAligner aligner) {
		JPanel panel = new JPanel(new GridLayout(1, 0, 5, 5));
		for (Iterator stream = this.buildCustomActions(); stream.hasNext(); ) {
			this.addCustomAction((Action) stream.next(), panel, aligner);
		}
		return panel;
	}
	
	/**
	 * Override this method and return an iterator of Actions.  Buttons
	 * will be added to the custom button panel for each action.
	 * The buttons will be added directly to the left of the OK button on
	 * the button panel; with the first button added the furthest
	 * from the OK button. For example:
	 * 
	 * 		Collection actions = new ArrayList();
	 * 		actions.add(new AbstractAction("1") {...});
	 * 		actions.add(new AbstractAction("2") {...});
	 * 		return actions.iterator()
	 * 
	 * The button panel will appear as follows:
	 * <pre>
	 *   ________________________________________
	 *   | ______         ___ ___ ____ ________ |
	 *   | |Help|         |1| |2| |OK| |Cancel| |
	 *   | ------         --- --- ---- -------- |
	 *   ----------------------------------------
	 * </pre>
	 */
	protected Iterator buildCustomActions() {
		return NullIterator.instance();
	}
	
	private void addCustomAction(Action customAction, JPanel panel, ComponentAligner aligner) {
		JButton button = new JButton(customAction);
		panel.add(button);
		aligner.add(button);
		this.buttons.put(customAction, button);
	}
	
	/**
	 * Sets up default keyboard actions
	 * To add more actions, override this method and call super.initializeDefaultKeyBoardActions()
	 * 
	 * The default mappings are:
	 *    ESC key to "Cancel"
	 */
	protected void initializeDefaultKeyboardActions() {
		this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
		this.getRootPane().getActionMap().put("cancel", this.cancelAction);
	}


	// ********** OK action **********

	/**
	 * Return the action associated with the OK button.
	 * Do not set a mnemonic on this action - according to Oracle UI standards,
	 * dialog OK buttons are not supposed to have a mnemonic.
	 */
	protected Action getOKAction() {
		return this.okAction;
	}

	protected Action buildOKAction() {
		return new AbstractAction(this.buildOKText()) {
			public void actionPerformed(ActionEvent e) {
				AbstractDialog.this.okPressed();
			}
		};
	}	

	protected String buildOKText() {
		return this.resourceRepository().getString("DIALOG.OK_BUTTON_TEXT");
	}

	protected void okPressed() {
		if (this.preConfirm()) {
			this.okConfirmed();
		}
	}

	/**
	 * Override this if there is additional validation required
	 * before the dialog is confirmed (e.g. checking for the
	 * existence of a file).
	 * Return false to cancel the confirmation process.
	 * Alternatively, you could disable the OK action....
	 */
	protected boolean preConfirm() {
		return true;
	}

	/**
	 * The OK button was pressed and #preConfirm() has indicated that
	 * everything is hunky-dory. Update any settings that were changed
	 * by the dialog. Overriding and extending this method is usually
	 * preferrable to overriding #okPressed().
	 */
	protected void okConfirmed() {
		this.wasConfirmed = true;
		this.dispose();
		// super.okConfirmed();
	}


	// ********** Cancel action **********

	/**
	 * Return the action associated with the Cancel button.
	 * Do not set a mnemonic on this action - according to Oracle UI standards,
	 * dialog Cancel buttons are not supposed to have a mnemonic.
	 */
	protected Action getCancelAction() {
		return this.cancelAction;
	}

	protected Action buildCancelAction() {
		return new AbstractAction(this.buildCancelText()) {
			public void actionPerformed(ActionEvent e) {
				AbstractDialog.this.cancelPressed();
			}
		};
	}	

	protected String buildCancelText() {
		return this.resourceRepository().getString("DIALOG.CANCEL_BUTTON_TEXT");
	}

	protected void cancelPressed() {
		this.wasConfirmed = false;
		this.dispose();
	}

	/**
	 * By default the Cancel button is displayed.
	 * If you do not want it to be displayed, return false.
	 * 
	 * Pressing <Esc> will still perform the cancel action.
	 */
	protected boolean cancelButtonIsVisible() {
		return true;
	}


	// ********** Help action **********
	// TODO bjv I think the Help button should have a mnemonic ('H') and an accelerator (F1)...
	
	/**
	 * Return the action associated with the Help button.
	 */
	protected Action getHelpAction() {
		return this.helpAction;
	}
	
	protected abstract String helpTopicId();

	protected Action buildHelpAction() {
		return new AbstractAction(this.buildHelpText()) {
			public void actionPerformed(ActionEvent e) {
				AbstractDialog.this.helpPressed();
			}
		};
	}	

	protected String buildHelpText() {
		return this.resourceRepository().getString("DIALOG.HELP_BUTTON_TEXT");
	}

	protected void helpPressed() {
		this.helpManager().showTopic(this.getContentPane());
	}


	// **********  actions/buttons **********

	protected JButton getButtonFor(Action action) {
		return (JButton) this.buttons.get(action);
	}

	/**
	 * Set the action to be executed when the <Enter> key is pressed,
	 * regardless of whether the button has keyboard focus
	 * (unless there is another component within 
     * the dialog which consumes the activation event,
     * such as a JTextPane or another JButton that currently has focus). 
     * For default activation to work, the button must be an enabled
     * descendent of the root pane when activation occurs.
     * 
     * By default, the OK action is the default action.
 	 * 
 	 * @see JRootPane#setDefaultButton(JButton)
	 */
	protected void setDefaultAction(Action action) {
		this.getRootPane().setDefaultButton(this.getButtonFor(action));
	}


	// ********** opening **********

	/**
	 * Return the component to be given focus when the dialog is opened.
	 * For example, if the component is a JTextField, the cursor will
	 * appear in the text field when the dialog is opened.
	 */	
	protected Component initialFocusComponent() {
		return null;
	}

	/**
	 * Show the dialog, after "packing" it (i.e. resizing it
	 * to correctly fit its contents) and setting its
	 * location relative to its parent.
	 * Surround show() with calls to register and unregister
	 * the dialog with the help system.
	 */
	public void show() {
		if (! this.built) {
			this.initializeContentPane();
			this.built = true;
		}
		this.wasConfirmed = false; //reset this field in case a subclass shows the dialog more than once, for error handling
		this.helpManager().addTopicID(this.getContentPane(), this.helpTopicId());
		this.prepareToShow();
		super.show();
	}

	/**
	 * Override this method if you do not want to pack the dialog before
	 * showing it. For example when the dialog title is
	 * longer than the size of the packed dialog. Swing does not
	 * take the dialog title into account when packing, so it can be
	 * truncated. You will need to set a specific size to fix this problem.
	 */
	protected void prepareToShow() {
		this.pack();
		this.setLocationRelativeTo(this.getParent());
	}
	
	/**
	 * If necessary, set the focus once the dialog is open.
	 */
	protected WindowListener buildWindowListener() {
		return new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				AbstractDialog.this.windowOpened();
			}
		};
	}

	protected void windowOpened() {
		Component component = this.initialFocusComponent();
		if (component != null) {
			component.requestFocusInWindow();
		}
	}

	// ********** closing **********

	/**
	 * Unregister the dialog after disposing of it.
	 */
	public void dispose() {
		super.dispose();
	}

	/**
	 * The OK button was pressed.
	 */
	public boolean wasConfirmed() {
		return this.wasConfirmed;
	}

	/**
	 * The OK button was *not* pressed. Some other action caused
	 * the dialog to close. Typically, the Cancel button was pressed.
	 */
	public boolean wasCanceled() {
		return ! this.wasConfirmed();
	}


	// ********** convenience methods **********

	/**
	 * Programmatically press a button for a particular action on this dialog.
	 */
	protected void click(Action action) {
		this.getButtonFor(action).doClick();
	}

	/**
	 * Programmatically press the OK button on this dialog.
	 */
	protected void clickOK() {
		this.click(this.okAction);
	}

	/**
	 * Programmatically press the Cancel button on this dialog.
	 */
	protected void clickCancel() {
		this.click(this.cancelAction);
	}

	public ApplicationContext getApplicationContext() {
		return this.context.getApplicationContext();
	}

	public WorkbenchContext getWorkbenchContext() {
		return this.context;
	}

	public ResourceRepository resourceRepository() {
		return this.getApplicationContext().getResourceRepository();
	}

	public HelpManager helpManager() {
		return this.getApplicationContext().getHelpManager();
	}
	
	public Window currentWindow() {
		return this.getWorkbenchContext().getCurrentWindow();
	}
	
	public Preferences preferences() {
		return this.getApplicationContext().getPreferences();
	}

	/**
	 * Build a launcher that can be dispatched via the AWT EventQueue.
	 */
	public static Runnable buildDialogLauncher(final Dialog d) {
		return new Runnable() {
			public void run() {
				d.show();
			}
		};
	}

	/**
	 * Asynchronously launch the specified dialog via the AWT EventQueue.
	 * This is useful for opening a "wait" dialog while a long-running action is executing.
	 */
	public static void launchLater(Dialog d) {
		EventQueue.invokeLater(buildDialogLauncher(d));
	}

	/**
	 * Synchronously launch the specified dialog via the AWT EventQueue.
	 * This is useful for opening a dialog in another thread.
	 */
	public static void launchAndWait(Dialog d) throws InterruptedException, InvocationTargetException {
		EventQueue.invokeAndWait(buildDialogLauncher(d));
	}
	
	//*********************************  Swing Helpers ******************************************************\
	
	/**
	 * @see SwingComponentFactory#buildExpandablePanel(String, JComponent, ResourceRepository)
	 */
	protected final ExpandablePane buildExpandablePanel(String key,
	                                                     JComponent internalPane)
	{
		return SwingComponentFactory.buildExpandablePanel
		(
			key,
			internalPane,
			resourceRepository()
		);
	}

	/**
	 * @see SwingComponentFactory#buildExpandablePanel(String, JComponent, boolean, ResourceRepository)
	 */
	protected final ExpandablePane buildExpandablePanel(String key,
	                                                     JComponent internalPane,
	                                                     boolean expanded)
	{
		return SwingComponentFactory.buildExpandablePanel
		(
			key,
			internalPane,
			expanded,
			resourceRepository()
		);
	}

	/**
	 * @see SwingComponentFactory#buildExpandablePanel(String, String, JComponent, ResourceRepository)
	 */
	protected final ExpandablePane buildExpandablePanel(String expandedTextKey,
	                                                     String collapsedTextKey,
	                                                     JComponent internalPane)
	{
		return SwingComponentFactory.buildExpandablePanel
		(
			expandedTextKey,
			collapsedTextKey,
			internalPane,
			resourceRepository()
		);
	}

	/**
	 * @see SwingComponentFactory#buildExpandablePanel(String, String, JComponent, boolean, ResourceRepository)
	 */
	protected final ExpandablePane buildExpandablePanel(String expandedTextKey,
	                                                     String collapsedTextKey,
	                                                     JComponent internalPane,
	                                                     boolean expanded)
	{
		return SwingComponentFactory.buildExpandablePanel
		(
			expandedTextKey,
			collapsedTextKey,
			internalPane,
			expanded,
			resourceRepository()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabel(String, ResourceRepository)
	 */
	protected final JLabel buildLabel(String key)
	{
		return SwingComponentFactory.buildLabel(key, resourceRepository());
	}

	/**
	 * @see SwingComponentFactory#buildLabeledComboBox(String, ComboBoxModel, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledComboBox(String key,
	                                                ComboBoxModel model)
	{
		return SwingComponentFactory.buildLabeledComboBox
		(
			key,
			model,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledComboBox(String, ComboBoxModel, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledComboBox(String key,
	                                                ComboBoxModel model,
	                                                JComponent rightComponent)
	{
		return SwingComponentFactory.buildLabeledComboBox
		(
			key,
			model,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledComboBox(String, ComboBoxModel, ListCellRenderer, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledComboBox(String key,
	                                                ComboBoxModel model,
	                                                ListCellRenderer renderer)
	{
		return SwingComponentFactory.buildLabeledComboBox
		(
			key,
			model,
			renderer,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledComboBox(String, ComboBoxModel, ListCellRenderer, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledComboBox(String key,
	                                                ComboBoxModel model,
	                                                CellRendererAdapter renderer)
	{
		return SwingComponentFactory.buildLabeledComboBox
		(
			key,
			model,
			renderer,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledComboBox(String, ComboBoxModel, ListCellRenderer, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledComboBox(String key,
	                                                ComboBoxModel model,
	                                                ListCellRenderer renderer,
	                                                JComponent rightComponent)
	{
		return SwingComponentFactory.buildLabeledComboBox
		(
			key,
			model,
			renderer,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledComponent(JComponent, JComponent, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledComponent(JComponent leftComponent,
	                                                 JComponent centerComponent,
	                                                 JComponent rightComponent)
	{
		return SwingComponentFactory.buildLabeledComponent
		(
			leftComponent,
			centerComponent,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledComponent(String, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledComponent(String key, JComponent component)
	{
		return SwingComponentFactory.buildLabeledComponent
		(
			key,
			component,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledComponent(String, JComponent, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledComponent(String key,
	                                                 JComponent component,
	                                                 JComponent rightComponent)
	{
		return SwingComponentFactory.buildLabeledComponent
		(
			key,
			component,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableComboBox(String, ComboBoxModel, ValueModel, PropertyValueModel, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableComboBox(String key,
	                                                        ComboBoxModel model,
	                                                        ValueModel subjectHolder,
	                                                        PropertyValueModel valueHolder)
	{
		return SwingComponentFactory.buildLabeledEditableComboBox
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableComboBox(String, ComboBoxModel, ValueModel, PropertyValueModel, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableComboBox(String key,
	                                                        ComboBoxModel model,
	                                                        ValueModel subjectHolder,
	                                                        PropertyValueModel valueHolder,
	                                                        JComponent rightComponent)
	{
		return SwingComponentFactory.buildLabeledEditableComboBox
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableComboBox(String, ComboBoxModel, ValueModel, PropertyValueModel, ListCellRenderer, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableComboBox(String key,
	                                                        ComboBoxModel model,
	                                                        ValueModel subjectHolder,
	                                                        PropertyValueModel valueHolder,
	                                                        ListCellRenderer renderer)
	{
		return SwingComponentFactory.buildLabeledEditableComboBox
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			renderer,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableComboBox(String, ComboBoxModel, ValueModel, PropertyValueModel, ListCellRenderer, BidiStringConverter, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableComboBox(String key,
	                                                        ComboBoxModel model,
	                                                        ValueModel subjectHolder,
	                                                        PropertyValueModel valueHolder,
	                                                        ListCellRenderer renderer,
	                                                        BidiStringConverter editorValueConverter)
	{
		return SwingComponentFactory.buildLabeledEditableComboBox
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			renderer,
			editorValueConverter,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableComboBox(String, ComboBoxModel, ValueModel, PropertyValueModel, ListCellRenderer, BidiStringConverter, Object, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableComboBox(String key,
	                                                        ComboBoxModel model,
	                                                        ValueModel subjectHolder,
	                                                        PropertyValueModel valueHolder,
	                                                        ListCellRenderer renderer,
	                                                        BidiStringConverter editorValueConverter,
	                                                        Object nullValue)
	{
		return SwingComponentFactory.buildLabeledEditableComboBox
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			renderer,
			editorValueConverter,
			nullValue,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableComboBox(String, ComboBoxModel, ValueModel, PropertyValueModel, ListCellRenderer, BidiStringConverter, Object, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableComboBox(String key,
	                                                        ComboBoxModel model,
	                                                        ValueModel subjectHolder,
	                                                        PropertyValueModel valueHolder,
	                                                        ListCellRenderer renderer,
	                                                        BidiStringConverter editorValueConverter,
	                                                        Object nullValue,
	                                                        JComponent rightComponent)
	{
		return SwingComponentFactory.buildLabeledEditableComboBox
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			renderer,
			editorValueConverter,
			nullValue,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableComboBox(String, ComboBoxModel, ValueModel, PropertyValueModel, ListCellRenderer, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableComboBox(String key,
	                                                        ComboBoxModel model,
	                                                        ValueModel subjectHolder,
	                                                        PropertyValueModel valueHolder,
	                                                        ListCellRenderer renderer,
	                                                        JComponent rightComponent)
	{
		return SwingComponentFactory.buildLabeledEditableComboBox
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			renderer,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableComboBox(String, ComboBoxModel, ValueModel, PropertyValueModel, ListCellRenderer, Object, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableComboBox(String key,
	                                                        ComboBoxModel model,
	                                                        ValueModel subjectHolder,
	                                                        PropertyValueModel valueHolder,
	                                                        ListCellRenderer renderer,
	                                                        Object nullValue)
	{
		return SwingComponentFactory.buildLabeledEditableComboBox
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			renderer,
			nullValue,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableComboBox(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableComboBox(String key,
	                                                        ComboBoxModel model,
	                                                        ValueModel subjectHolder,
	                                                        PropertyValueModel valueHolder,
	                                                        ValueModel defaultValueHolder)
	{
		return SwingComponentFactory.buildLabeledEditableComboBox
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableComboBox(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableComboBox(String key,
	                                                        ComboBoxModel model,
	                                                        ValueModel subjectHolder,
	                                                        PropertyValueModel valueHolder,
	                                                        ValueModel defaultValueHolder,
	                                                        JComponent rightComponent)
	{
		return SwingComponentFactory.buildLabeledEditableComboBox
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableComboBox(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ListCellRenderer, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableComboBox(String key,
	                                                        ComboBoxModel model,
	                                                        ValueModel subjectHolder,
	                                                        PropertyValueModel valueHolder,
	                                                        ValueModel defaultValueHolder,
	                                                        ListCellRenderer renderer)
	{
		return SwingComponentFactory.buildLabeledEditableComboBox
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableComboBox(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ListCellRenderer, BidiStringConverter, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableComboBox(String key,
	                                                        ComboBoxModel model,
	                                                        ValueModel subjectHolder,
	                                                        PropertyValueModel valueHolder,
	                                                        ValueModel defaultValueHolder,
	                                                        ListCellRenderer renderer,
	                                                        BidiStringConverter editorValueConverter)
	{
		return SwingComponentFactory.buildLabeledEditableComboBox
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			editorValueConverter,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableComboBox(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ListCellRenderer, BidiStringConverter, Object, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableComboBox(String key,
	                                                        ComboBoxModel model,
	                                                        ValueModel subjectHolder,
	                                                        PropertyValueModel valueHolder,
	                                                        ValueModel defaultValueHolder,
	                                                        ListCellRenderer renderer,
	                                                        BidiStringConverter editorValueConverter,
	                                                        Object nullValue)
	{
		return SwingComponentFactory.buildLabeledEditableComboBox
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			editorValueConverter,
			nullValue,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableComboBox(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ListCellRenderer, BidiStringConverter, Object, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableComboBox(String key,
	                                                        ComboBoxModel model,
	                                                        ValueModel subjectHolder,
	                                                        PropertyValueModel valueHolder,
	                                                        ValueModel defaultValueHolder,
	                                                        ListCellRenderer renderer,
	                                                        BidiStringConverter editorValueConverter,
	                                                        Object nullValue,
	                                                        JComponent rightComponent)
	{
		return SwingComponentFactory.buildLabeledEditableComboBox
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			editorValueConverter,
			nullValue,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableComboBox(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ListCellRenderer, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableComboBox(String key,
	                                                        ComboBoxModel model,
	                                                        ValueModel subjectHolder,
	                                                        PropertyValueModel valueHolder,
	                                                        ValueModel defaultValueHolder,
	                                                        ListCellRenderer renderer,
	                                                        JComponent rightComponent)
	{
		return SwingComponentFactory.buildLabeledEditableComboBox
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableComboBox(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ListCellRenderer, Object, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableComboBox(String key,
	                                                        ComboBoxModel model,
	                                                        ValueModel subjectHolder,
	                                                        PropertyValueModel valueHolder,
	                                                        ValueModel defaultValueHolder,
	                                                        ListCellRenderer renderer,
	                                                        Object nullValue)
	{
		return SwingComponentFactory.buildLabeledEditableComboBox
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			nullValue,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableListChooser(String key,
	                                                           ComboBoxModel model,
	                                                           ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder)
	{
		return SwingComponentFactory.buildLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableListChooser(String key,
	                                                           ComboBoxModel model,
	                                                           ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder,
	                                                           JComponent rightComponent)
	{
		return SwingComponentFactory.buildLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ListCellRenderer, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableListChooser(String key,
	                                                           ComboBoxModel model,
	                                                           ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder,
	                                                           ListCellRenderer renderer)
	{
		return SwingComponentFactory.buildLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			renderer,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ListCellRenderer, BidiStringConverter, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableListChooser(String key,
	                                                           ComboBoxModel model,
	                                                           ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder,
	                                                           ListCellRenderer renderer,
	                                                           BidiStringConverter editorValueConverter)
	{
		return SwingComponentFactory.buildLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			renderer,
			editorValueConverter,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ListCellRenderer, BidiStringConverter, Object, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableListChooser(String key,
	                                                           ComboBoxModel model,
	                                                           ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder,
	                                                           ListCellRenderer renderer,
	                                                           BidiStringConverter editorValueConverter,
	                                                           Object nullValue)
	{
		return SwingComponentFactory.buildLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			renderer,
			editorValueConverter,
			nullValue,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ListCellRenderer, BidiStringConverter, Object, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableListChooser(String key,
	                                                           ComboBoxModel model,
	                                                           ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder,
	                                                           ListCellRenderer renderer,
	                                                           BidiStringConverter editorValueConverter,
	                                                           Object nullValue,
	                                                           JComponent rightComponent)
	{
		return SwingComponentFactory.buildLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			renderer,
			editorValueConverter,
			nullValue,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ListCellRenderer, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableListChooser(String key,
	                                                           ComboBoxModel model,
	                                                           ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder,
	                                                           ListCellRenderer renderer,
	                                                           JComponent rightComponent)
	{
		return SwingComponentFactory.buildLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			renderer,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ListCellRenderer, Object, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableListChooser(String key,
	                                                           ComboBoxModel model,
	                                                           ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder,
	                                                           ListCellRenderer renderer,
	                                                           Object nullValue)
	{
		return SwingComponentFactory.buildLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			renderer,
			nullValue,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, Object, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableListChooser(String key,
	                                                           ComboBoxModel model,
	                                                           ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder,
	                                                           Object nullValue)
	{
		return SwingComponentFactory.buildLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			nullValue,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, Object, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableListChooser(String key,
	                                                           ComboBoxModel model,
	                                                           ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder,
	                                                           Object nullValue,
	                                                           JComponent rightComponent)
	{
		return SwingComponentFactory.buildLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			nullValue,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableListChooser(String key,
	                                                           ComboBoxModel model,
	                                                           ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder,
	                                                           ValueModel defaultValueHolder)
	{
		return SwingComponentFactory.buildLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableListChooser(String key,
	                                                           ComboBoxModel model,
	                                                           ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder,
	                                                           ValueModel defaultValueHolder,
	                                                           JComponent rightComponent)
	{
		return SwingComponentFactory.buildLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ListCellRenderer, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableListChooser(String key,
	                                                           ComboBoxModel model,
	                                                           ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder,
	                                                           ValueModel defaultValueHolder,
	                                                           ListCellRenderer renderer)
	{
		return SwingComponentFactory.buildLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ListCellRenderer, BidiStringConverter, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableListChooser(String key,
	                                                           ComboBoxModel model,
	                                                           ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder,
	                                                           ValueModel defaultValueHolder,
	                                                           ListCellRenderer renderer,
	                                                           BidiStringConverter editorValueConverter)
	{
		return SwingComponentFactory.buildLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			editorValueConverter,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ListCellRenderer, BidiStringConverter, Object, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableListChooser(String key,
	                                                           ComboBoxModel model,
	                                                           ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder,
	                                                           ValueModel defaultValueHolder,
	                                                           ListCellRenderer renderer,
	                                                           BidiStringConverter editorValueConverter,
	                                                           Object nullValue)
	{
		return SwingComponentFactory.buildLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			editorValueConverter,
			nullValue,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ListCellRenderer, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableListChooser(String key,
	                                                           ComboBoxModel model,
	                                                           ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder,
	                                                           ValueModel defaultValueHolder,
	                                                           ListCellRenderer renderer,
	                                                           JComponent rightComponent)
	{
		return SwingComponentFactory.buildLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ListCellRenderer, Object, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableListChooser(String key,
	                                                           ComboBoxModel model,
	                                                           ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder,
	                                                           ValueModel defaultValueHolder,
	                                                           ListCellRenderer renderer,
	                                                           Object nullValue)
	{
		return SwingComponentFactory.buildLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			nullValue,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, Object, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableListChooser(String key,
	                                                           ComboBoxModel model,
	                                                           ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder,
	                                                           ValueModel defaultValueHolder,
	                                                           Object nullValue)
	{
		return SwingComponentFactory.buildLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			nullValue,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, Object, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledEditableListChooser(String key,
	                                                           ComboBoxModel model,
	                                                           ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder,
	                                                           ValueModel defaultValueHolder,
	                                                           Object nullValue,
	                                                           JComponent rightComponent)
	{
		return SwingComponentFactory.buildLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			nullValue,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledListChooser(String, ComboBoxModel, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledListChooser(String key,
	                                                   ComboBoxModel model)
	{
		return SwingComponentFactory.buildLabeledListChooser
		(
			key,
			model,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledListChooser(String, ComboBoxModel, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledListChooser(String key,
	                                                   ComboBoxModel model,
	                                                   JComponent rightComponent)
	{
		return SwingComponentFactory.buildLabeledListChooser
		(
			key,
			model,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledListChooser(String, ComboBoxModel, ListCellRenderer, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledListChooser(String key,
	                                                   ComboBoxModel model,
	                                                   ListCellRenderer renderer)
	{
		return SwingComponentFactory.buildLabeledListChooser
		(
			key,
			model,
			renderer,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledListChooser(String, ComboBoxModel, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledListChooser(String key,
	                                                   ComboBoxModel model,
	                                                   ListCellRenderer renderer,
	                                                   JComponent rightComponent)
	{
		return SwingComponentFactory.buildLabeledListChooser
		(
			key,
			model,
			renderer,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledNonEditableTextField(String, Document, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledNonEditableTextField(String key, Document document)
	{
		return SwingComponentFactory.buildLabeledNonEditableTextField
		(
			key,
			document,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledNonEditableTextField(String, Document, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledNonEditableTextField(String key,
	                                                            Document document,
	                                                            JComponent rightComponent)
	{
		return SwingComponentFactory.buildLabeledNonEditableTextField
		(
			key,
			document,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledSpinnerDate(String, SpinnerDateModel, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledSpinnerDate(String labelKey,
	                                                   SpinnerDateModel model)
	{
		return SwingComponentFactory.buildLabeledSpinnerDate
		(
			labelKey,
			model,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledSpinnerNumber(String, SpinnerNumberModel, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledSpinnerNumber(String key,
	                                                     SpinnerNumberModel model)
	{
		return SwingComponentFactory.buildLabeledSpinnerNumber
		(
			key,
			model,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledSpinnerNumber(String, SpinnerNumberModel, int, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledSpinnerNumber(String key,
	                                                     SpinnerNumberModel model,
	                                                     int columns)
	{
		return SwingComponentFactory.buildLabeledSpinnerNumber
		(
			key,
			model,
			columns,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledTextField(String, Document, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledTextField(String key,
	                                                 Document document)
	{
		return SwingComponentFactory.buildLabeledTextField
		(
			key,
			document,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledTextField(String, Document, int, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledTextField(String key,
	                                                 Document document,
	                                                 int columns)
	{
		return SwingComponentFactory.buildLabeledTextField
		(
			key,
			document,
			columns,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledTextField(String, Document, int, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledTextField(String key,
	                                                 Document document,
	                                                 int columns,
	                                                 JComponent rightComponent)
	{
		return SwingComponentFactory.buildLabeledTextField
		(
			key,
			document,
			columns,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledTextField(String, Document, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildLabeledTextField(String key,
	                                                 Document document,
	                                                 JComponent rightComponent)
	{
		return SwingComponentFactory.buildLabeledTextField
		(
			key,
			document,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}
	
	protected final JComponent buildLabeledTextField(String key, 
													Document document,
													JTextField field) 
	{
		return SwingComponentFactory.buildLabeledTextField
		(
			key,
			document,
			field,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildListChooser(ComboBoxModel)
	 */
	protected final ListChooser buildListChooser(ComboBoxModel model)
	{
		return SwingComponentFactory.buildListChooser(model);
	}

	/**
	 * @see SwingComponentFactory#buildListChooser(ComboBoxModel, ListCellRenderer)
	 */
	protected final ListChooser buildListChooser(ComboBoxModel model,
	                                             ListCellRenderer cellRenderer)
	{
		return SwingComponentFactory.buildListChooser(model, cellRenderer);
	}

	/**
	 * @see SwingComponentFactory#buildPaneTitledBorder(ResourceRepository, String)
	 */
	protected final Border buildPaneTitledBorder(String key)
	{
		return SwingComponentFactory.buildPaneTitledBorder(key, resourceRepository());
	}

	/**
	 * @see SwingComponentFactory#buildRadioButton(String, ButtonModel)
	 */
	protected final JRadioButton buildRadioButton(String key,
	                                              ButtonModel buttonModel)
	{
		return SwingComponentFactory.buildRadioButton
		(
			key,
			buttonModel,
			resourceRepository()
		);
	}

	/**
	 * @see SwingComponentFactory#buildSpinnerDate(SpinnerNumberModel)
	 */
	protected final JSpinner buildSpinnerDate(SpinnerDateModel model)
	{
		return SwingComponentFactory.buildSpinnerDate(model);
	}

	/**
	 * @see SwingComponentFactory#buildSpinnerNumber(SpinnerNumberModel)
	 */
	protected final JSpinner buildSpinnerNumber(SpinnerNumberModel model)
	{
		return SwingComponentFactory.buildSpinnerNumber(model);
	}

	/**
	 * @see SwingComponentFactory#buildSpinnerNumber(SpinnerNumberModel, int)
	 */
	protected final JSpinner buildSpinnerNumber(SpinnerNumberModel model, int columns)
	{
		return SwingComponentFactory.buildSpinnerNumber(model, columns);
	}

	/**
	 * @see SwingComponentFactory#buildTable(TableModel)
	 */
	protected final JTable buildTable(TableModel tableModel)
	{
		return SwingComponentFactory.buildTable(tableModel);
	}

	/**
	 * @see SwingComponentFactory#buildTable(TableModel, ListSelectionModel)
	 */
	protected final JTable buildTable(TableModel tableModel,
	                                  ListSelectionModel listSelectionModel)
	{
		return SwingComponentFactory.buildTable(tableModel, listSelectionModel);
	}

	/**
	 * @see SwingComponentFactory#buildTable(TableModel, TableColumnModel, ListSelectionModel)
	 */
	protected final JTable buildTable(TableModel tableModel,
	                                  TableColumnModel tableColumnModel,
	                                  ListSelectionModel listSelectionModel)
	{
		return SwingComponentFactory.buildTable(tableModel, tableColumnModel, listSelectionModel);
	}

	/**
	 * @see SwingComponentFactory#buildTextField(Document)
	 */
	protected final JTextField buildTextField(Document document)
	{
		return SwingComponentFactory.buildTextField(document);
	}

	/**
	 * @see SwingComponentFactory#buildTitledBorder(ResourceRepository, String)
	 */
	protected final Border buildTitledBorder(String key)
	{
		return SwingComponentFactory.buildTitledBorder(key, resourceRepository());
	}

	/**
	 * @see SwingComponentFactory#buildToggleButton(String, ResourceRepository)
	 */
	protected final JToggleButton buildToggleButton(String key)
	{
		return SwingComponentFactory.buildToggleButton(key, resourceRepository());
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledComboBox(String, ComboBoxModel, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledComboBox(String key,
	                                                   ComboBoxModel model)
	{
		return SwingComponentFactory.buildTopLabeledComboBox
		(
			key,
			model,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledComboBox(String, ComboBoxModel, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledComboBox(String key,
	                                                   ComboBoxModel model,
	                                                   JComponent rightComponent)
	{
		return SwingComponentFactory.buildTopLabeledComboBox
		(
			key,
			model,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledComboBox(String, ComboBoxModel, ListCellRenderer, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledComboBox(String key,
	                                                   ComboBoxModel model,
	                                                   ListCellRenderer renderer)
	{
		return SwingComponentFactory.buildTopLabeledComboBox
		(
			key,
			model,
			renderer,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledComboBox(String, ComboBoxModel, ListCellRenderer, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledComboBox(String key,
	                                                   ComboBoxModel model,
	                                                   ListCellRenderer renderer,
	                                                   JComponent rightComponent)
	{
		return SwingComponentFactory.buildTopLabeledComboBox
		(
			key,
			model,
			renderer,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledComponent(JComponent, JComponent, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledComponent(JComponent leftComponent,
	                                                    JComponent centerComponent,
	                                                    JComponent rightComponent)
	{
		return SwingComponentFactory.buildTopLabeledComponent
		(
			leftComponent,
			centerComponent,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledComponent(String, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledComponent(String key,
	                                                    JComponent component)
	{
		return SwingComponentFactory.buildTopLabeledComponent
		(
			key,
			component,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledComponent(String, JComponent, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledComponent(String key,
	                                                    JComponent component,
	                                                    JComponent rightComponent)
	{
		return SwingComponentFactory.buildTopLabeledComponent
		(
			key,
			component,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledEditableComboBox(String, ComboBoxModel, ValueModel, PropertyValueModel, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledEditableComboBox(String key,
	                                                           ComboBoxModel model,
		                                                        ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder)
	{
		return SwingComponentFactory.buildTopLabeledEditableComboBox
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledEditableComboBox(String, ComboBoxModel, ValueModel, PropertyValueModel, ListCellRenderer, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledEditableComboBox(String key,
	                                                           ComboBoxModel model,
		                                                        ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder,
	                                                           ListCellRenderer renderer)
	{
		return SwingComponentFactory.buildTopLabeledEditableComboBox
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			renderer,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledEditableComboBox(String, ComboBoxModel, ValueModel, PropertyValueModel, ListCellRenderer, BidiStringConverter, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledEditableComboBox(String key,
	                                                           ComboBoxModel model,
		                                                        ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder,
	                                                           ListCellRenderer renderer,
	                                                           BidiStringConverter editorValueConverter)
	{
		return SwingComponentFactory.buildTopLabeledEditableComboBox
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			renderer,
			editorValueConverter,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledEditableComboBox(String, ComboBoxModel, ValueModel, PropertyValueModel, ListCellRenderer, BidiStringConverter, Object, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledEditableComboBox(String key,
	                                                           ComboBoxModel model,
		                                                        ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder,
	                                                           ListCellRenderer renderer,
	                                                           BidiStringConverter editorValueConverter,
	                                                           Object nullValue)
	{
		return SwingComponentFactory.buildTopLabeledEditableComboBox
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			renderer,
			editorValueConverter,
			nullValue,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledEditableComboBox(String, ComboBoxModel, ValueModel, PropertyValueModel, ListCellRenderer, Object, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledEditableComboBox(String key,
	                                                           ComboBoxModel model,
		                                                        ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder,
	                                                           ListCellRenderer renderer,
	                                                           Object nullValue)
	{
		return SwingComponentFactory.buildTopLabeledEditableComboBox
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			renderer,
			nullValue,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledEditableComboBox(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledEditableComboBox(String key,
	                                                           ComboBoxModel model,
		                                                        ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder,
	                                                           ValueModel defaultValueHolder)
	{
		return SwingComponentFactory.buildTopLabeledEditableComboBox
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledEditableComboBox(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ListCellRenderer, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledEditableComboBox(String key,
	                                                           ComboBoxModel model,
		                                                        ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder,
	                                                           ValueModel defaultValueHolder,
	                                                           ListCellRenderer renderer)
	{
		return SwingComponentFactory.buildTopLabeledEditableComboBox
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledEditableComboBox(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ListCellRenderer, BidiStringConverter, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledEditableComboBox(String key,
	                                                           ComboBoxModel model,
		                                                        ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder,
	                                                           ValueModel defaultValueHolder,
	                                                           ListCellRenderer renderer,
	                                                           BidiStringConverter editorValueConverter)
	{
		return SwingComponentFactory.buildTopLabeledEditableComboBox
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			editorValueConverter,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledEditableComboBox(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ListCellRenderer, BidiStringConverter, Object, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledEditableComboBox(String key,
	                                                           ComboBoxModel model,
		                                                        ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder,
	                                                           ValueModel defaultValueHolder,
	                                                           ListCellRenderer renderer,
	                                                           BidiStringConverter editorValueConverter,
	                                                           Object nullValue)
	{
		return SwingComponentFactory.buildTopLabeledEditableComboBox
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			editorValueConverter,
			nullValue,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledEditableComboBox(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ListCellRenderer, Object, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledEditableComboBox(String key,
	                                                           ComboBoxModel model,
		                                                        ValueModel subjectHolder,
	                                                           PropertyValueModel valueHolder,
	                                                           ValueModel defaultValueHolder,
	                                                           ListCellRenderer renderer,
	                                                           Object nullValue)
	{
		return SwingComponentFactory.buildTopLabeledEditableComboBox
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			nullValue,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledEditableListChooser(String key,
	                                                              ComboBoxModel model,
	                                                              ValueModel subjectHolder,
	                                                              PropertyValueModel valueHolder)
	{
		return SwingComponentFactory.buildTopLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledEditableListChooser(String, ComboBoxModel, ValueModel, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledEditableListChooser(String key,
	                                                              ComboBoxModel model,
	                                                              ValueModel subjectHolder,
	                                                              PropertyValueModel valueHolder,
	                                                              JComponent rightComponent)
	{
		return SwingComponentFactory.buildTopLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, Object, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledEditableListChooser(String key,
	                                                              ComboBoxModel model,
	                                                              ValueModel subjectHolder,
	                                                              PropertyValueModel valueHolder,
	                                                              ListCellRenderer renderer)
	{
		return SwingComponentFactory.buildTopLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			renderer,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, BidiStringConverter, Object, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledEditableListChooser(String key,
	                                                              ComboBoxModel model,
	                                                              ValueModel subjectHolder,
	                                                              PropertyValueModel valueHolder,
	                                                              ListCellRenderer renderer,
	                                                              BidiStringConverter editorValueConverter)
	{
		return SwingComponentFactory.buildTopLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			renderer,
			editorValueConverter,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, BidiStringConverter, Object, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledEditableListChooser(String key,
	                                                              ComboBoxModel model,
	                                                              ValueModel subjectHolder,
	                                                              PropertyValueModel valueHolder,
	                                                              ListCellRenderer renderer,
	                                                              BidiStringConverter editorValueConverter,
	                                                              Object nullValue)
	{
		return SwingComponentFactory.buildTopLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ListCellRenderer, Object, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledEditableListChooser(String key,
	                                                              ComboBoxModel model,
	                                                              ValueModel subjectHolder,
	                                                              PropertyValueModel valueHolder,
	                                                              ListCellRenderer renderer,
	                                                              Object nullValue)
	{
		return SwingComponentFactory.buildTopLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			renderer,
			nullValue,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, Object, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledEditableListChooser(String key,
	                                                              ComboBoxModel model,
	                                                              ValueModel subjectHolder,
	                                                              PropertyValueModel valueHolder,
	                                                              Object nullValue)
	{
		return SwingComponentFactory.buildTopLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, Object, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledEditableListChooser(String key,
	                                                              ComboBoxModel model,
	                                                              ValueModel subjectHolder,
	                                                              PropertyValueModel valueHolder,
	                                                              Object nullValue,
	                                                              JComponent rightComponent)
	{
		return SwingComponentFactory.buildTopLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledEditableListChooser(String key,
	                                                              ComboBoxModel model,
	                                                              ValueModel subjectHolder,
	                                                              PropertyValueModel valueHolder,
	                                                              ValueModel defaultValueHolder)
	{
		return SwingComponentFactory.buildTopLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledEditableListChooser(String key,
	                                                              ComboBoxModel model,
	                                                              ValueModel subjectHolder,
	                                                              PropertyValueModel valueHolder,
	                                                              ValueModel defaultValueHolder,
	                                                              JComponent rightComponent)
	{
		return SwingComponentFactory.buildTopLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ListCellRenderer, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledEditableListChooser(String key,
	                                                              ComboBoxModel model,
	                                                              ValueModel subjectHolder,
	                                                              PropertyValueModel valueHolder,
	                                                              ValueModel defaultValueHolder,
	                                                              ListCellRenderer renderer)
	{
		return SwingComponentFactory.buildTopLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ListCellRenderer, BidiStringConverter, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledEditableListChooser(String key,
	                                                              ComboBoxModel model,
	                                                              ValueModel subjectHolder,
	                                                              PropertyValueModel valueHolder,
	                                                              ValueModel defaultValueHolder,
	                                                              ListCellRenderer renderer,
	                                                              BidiStringConverter editorValueConverter)
	{
		return SwingComponentFactory.buildTopLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			editorValueConverter,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ListCellRenderer, BidiStringConverter, Object, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledEditableListChooser(String key,
	                                                              ComboBoxModel model,
	                                                              ValueModel subjectHolder,
	                                                              PropertyValueModel valueHolder,
	                                                              ValueModel defaultValueHolder,
	                                                              ListCellRenderer renderer,
	                                                              BidiStringConverter editorValueConverter,
	                                                              Object nullValue)
	{
		return SwingComponentFactory.buildTopLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			editorValueConverter,
			nullValue,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, ListCellRenderer, Object, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledEditableListChooser(String key,
	                                                              ComboBoxModel model,
	                                                              ValueModel subjectHolder,
	                                                              PropertyValueModel valueHolder,
	                                                              ValueModel defaultValueHolder,
	                                                              ListCellRenderer renderer,
	                                                              Object nullValue)
	{
		return SwingComponentFactory.buildTopLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			nullValue,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, Object, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledEditableListChooser(String key,
	                                                              ComboBoxModel model,
	                                                              ValueModel subjectHolder,
	                                                              PropertyValueModel valueHolder,
	                                                              ValueModel defaultValueHolder,
	                                                              Object nullValue)
	{
		return SwingComponentFactory.buildTopLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			nullValue,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledEditableListChooser(String, ComboBoxModel, ValueModel, PropertyValueModel, ValueModel, Object, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledEditableListChooser(String key,
	                                                              ComboBoxModel model,
	                                                              ValueModel subjectHolder,
	                                                              PropertyValueModel valueHolder,
	                                                              ValueModel defaultValueHolder,
	                                                              Object nullValue,
	                                                              JComponent rightComponent)
	{
		return SwingComponentFactory.buildTopLabeledEditableListChooser
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			nullValue,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledListChooser(String, ComboBoxModel, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledListChooser(String key,
	                                                      ComboBoxModel model)
	{
		return SwingComponentFactory.buildTopLabeledListChooser
		(
			key,
			model,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledListChooser(String, ComboBoxModel, JComponnet, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledListChooser(String key,
	                                                      ComboBoxModel model,
	                                                      JComponent rightComponent)
	{
		return SwingComponentFactory.buildTopLabeledListChooser
		(
			key,
			model,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledListChooser(String, ComboBoxModel, ListCellRenderer, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledListChooser(String key,
	                                                      ComboBoxModel model,
	                                                      ListCellRenderer cellRenderer)
	{
		return SwingComponentFactory.buildTopLabeledListChooser
		(
			key,
			model,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledListChooser(String, ComboBoxModel, ListCellRenderer, JComponnet, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledListChooser(String key,
	                                                      ComboBoxModel model,
	                                                      ListCellRenderer renderer,
	                                                      JComponent rightComponent)
	{
		return SwingComponentFactory.buildTopLabeledListChooser
		(
			key,
			model,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledNonEditableTextField(String, Document, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledNonEditableTextField(String key,
	                                                               Document document,
	                                                               JComponent rightComponent,
	                                                               ResourceRepository repository)
	{
		return SwingComponentFactory.buildTopLabeledNonEditableTextField
		(
			key,
			document,
			rightComponent,
			repository,
			getAlignRightGroup(),
			getAlignLeftGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildLabeledSpinnerNumber(String, SpinnerNumberModel, int, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledSpinnerNumber(String key,
	                                                        SpinnerNumberModel model,
	                                                        int columns)
	{
		return SwingComponentFactory.buildLabeledSpinnerNumber
		(
			key,
			model,
			columns,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledTextField(String, Document, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledTextField(String key,
	                                                    Document document)
	{
		return SwingComponentFactory.buildTopLabeledTextField
		(
			key,
			document,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTopLabeledTextField(String, Document, JComponent, ResourceRepository, ComponentAligner, ComponentAligner)
	 */
	protected final JComponent buildTopLabeledTextField(String key,
	                                                    Document document,
	                                                    JComponent rightComponent)
	{
		return SwingComponentFactory.buildTopLabeledTextField
		(
			key,
			document,
			rightComponent,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}

	/**
	 * @see SwingComponentFactory#buildTree(TreeModel)
	 */
	protected final JTree buildTree(TreeModel model)
	{
		return SwingComponentFactory.buildTree(model);
	}

	/**
	 * @see SwingComponentFactory#buildTriStateCheckBox(String, TriStateButtonModel, ResourceRepository)
	 */
	protected final TriStateCheckBox buildTriStateCheckBox(String key, TriStateButtonModel model)
	{
		return SwingComponentFactory.buildTriStateCheckBox(key, model, resourceRepository());
	}

	/**
	 * Returns the <code>ComponentAligner</code> that is responsible to align all
	 * the components that are on the left side of a pane, usually those
	 * components are labels.
	 *
	 * @return The <code>ComponentAligner</code> that makes sure all components
	 * with it have the same width
	 */
	private ComponentAligner getAlignLeftGroup()
	{
		return container.getAlignLeftGroup();
	}

	/**
	 * Returns the <code>ComponentAligner</code> that is responsible to align all
	 * the components that are on the right side of a pane, usually those
	 * components are browse buttons.
	 *
	 * @return The <code>ComponentAligner</code> that makes sure all components
	 * with it have the same width
	 */
	private ComponentAligner getAlignRightGroup()
	{
		return container.getAlignRightGroup();
	}
	

}
