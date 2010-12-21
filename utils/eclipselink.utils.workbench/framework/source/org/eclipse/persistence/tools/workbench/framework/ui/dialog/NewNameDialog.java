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
package org.eclipse.persistence.tools.workbench.framework.ui.dialog;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.xml.bind.Validator;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.utility.events.AWTChangeNotifier;
import org.eclipse.persistence.tools.workbench.utility.events.ChangeNotifier;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

/**
 * This dialog can be used to prompt the user for the name of a new
 * object or for the new name of an existing object.
 * 
 * To instantiate this dialog: instantiate a Builder, configure it,
 * then build the dialog by calling Builder.buildDialog(WorkbenchContext).
 * 
 * <pre>
 *   ________________________________________
 *   | Title                                |
 *   |¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯|
 *   | Description:                         |
 *   |    ________________________          |
 *   |    |New name text field   |          |
 *   |    ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯          |
 *   | Error Message                        |
 *   | ____________________________________ |
 *   | ______        ________ ____ ________ |
 *   | |Help|        |Custom| |OK| |Cancel| |
 *   | ¯¯¯¯¯¯        ¯¯¯¯¯¯¯¯ ¯¯¯¯ ¯¯¯¯¯¯¯¯ |
 *   ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
 * </pre>
 * 
 * NB: If you subclass this dialog, you will need to subclass
 * the Builder class also.
 */
public class NewNameDialog extends AbstractValidatingDialog {

	/** Holds all the settings used by the dialog when editing the name. */
	private Builder builder;

	/** We need to set the text on this after it is built. */
	private JLabel textFieldLabel;

	/** After this is built, we need to set the text and select it. */
	protected JTextField textField;

	/**
	 * The holder of the state object used by this dialog.
	 */
	private PropertyValueModel subjectHolder;


	// ********** constructors/initialization **********

	/**
	 * Do not call this constructor directly; use a Builder to
	 * instantiate this dialog.
	 */
	protected NewNameDialog(WorkbenchContext context, Builder builder) {
		super(context, builder.getTitle());
		this.builder = builder;
	}

	@Override
	protected void initialize() {
		super.initialize();
		this.subjectHolder = new SimplePropertyValueModel();
	}
	
	protected Component buildMainPanel() {
		JPanel mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		// label
		this.textFieldLabel = new JLabel();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(5, 0, 5, 0);
		mainPanel.add(this.textFieldLabel, constraints);

		// text entry field
		this.textField = new JTextField(25);
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(5, 0, 5, 0);
		mainPanel.add(this.textField, constraints);

		this.textFieldLabel.setText(this.builder.getTextFieldDescription());
		Document document = this.buildDocument(this.builder.getOriginalName());
		document.addDocumentListener(this.buildDocumentListener());
		this.textField.setDocument(document);
		this.textField.selectAll();
		if (this.textField.getText().length() == 0) {
			this.getOKAction().setEnabled(false);
		}

		this.helpManager().addTopicID(mainPanel, this.helpTopicId());

		return mainPanel;
	}

	protected Builder getBuilder() {
		return this.builder;
	}

	protected Document buildDocument(String originalName) {
		Document document = this.buildDocument();
		try {
			document.insertString(0, originalName, null);
		} catch (BadLocationException ex) {
			throw new RuntimeException(ex);	// this should not happen
		}
		return document;
	}

	/**
	 * Override if you want to restrict the allowable characters
	 * entered in the text field (e.g. with a regular expression
	 * document).
	 */
	protected Document buildDocument() {
		return this.builder.getDocumentFactory().buildDocument();
	}
	
	protected Document buildDocumentWithStateObject() {
		return new DocumentAdapter(
			this.buildNameHolder(),
			this.builder.getDocumentFactory().buildDocument()
		);
	}
	
	protected final PropertyValueModel buildNameHolder() {
		return new PropertyAspectAdapter(getSubjectHolder(), StateObject.NAME_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				return ((StateObject)subject).getName();
			}

			@Override
			protected void setValueOnSubject(Object value) {
				((StateObject)subject).setName((String)value);
			}
		};
	}


	protected DocumentListener buildDocumentListener() {
		return new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {
				NewNameDialog.this.documentChanged();
			}
			public void insertUpdate(DocumentEvent e) {
				NewNameDialog.this.documentChanged();
			}
			public void changedUpdate(DocumentEvent e) {
				// this probably will never happen...
			}
		};
	}


	// ********** opening **********

	protected String helpTopicId() {
		return this.builder.getHelpTopicId();
	}

	protected Component initialFocusComponent() {
		return this.textField;
	}


	// ********** editing input **********

	protected Builder builder() {
		return this.builder;
	}

	/**
	 * The text field changed, edit it and update the error message.
	 */
	protected void documentChanged() {
		if (this.isVisible()) {
			this.editName();
		}
	}

	/**
	 * Edit the name, using the settings in the builder, and update
	 * the error message.
	 */
	protected void editName() {
		String text = this.textField.getText();

		// empty string might not be allowed
		if (this.builder.emptyNameIsIllegal() && (text.length() == 0)) {
			this.setErrorMessageKey("NEW_NAME_DIALOG.EMPTY_VALUE");
			return;
		}

		boolean nameIsSameAsOriginal = this.namesMatch(text, this.builder.getOriginalName());
		// original name might be "illegal"
		if (this.builder.originalNameIsIllegal() && nameIsSameAsOriginal) {
			this.setErrorMessageKey("NEW_NAME_DIALOG.ORIGINAL_VALUE");
			return;
		}

		// check for "existing" name
		if (this.nameIsAlreadyTaken(text, nameIsSameAsOriginal)) {
			this.setErrorMessageKey("NEW_NAME_DIALOG.DUPLICATE_VALUE");
			return;
		}

		// check for "illegal" name
		if (this.nameIsIllegal(text)) {
			this.setErrorMessageKey("NEW_NAME_DIALOG.ILLEGAL_VALUE");
			return;
		}

		// no problems...
		this.clearErrorMessage();
	}

	protected void setErrorMessageKey(String key) {
		super.setErrorMessageKey(key);
		this.getOKAction().setEnabled(false);
	}

	protected void clearErrorMessage() {
		super.clearErrorMessage();
		this.getOKAction().setEnabled(true);
	}

	protected boolean namesMatch(String name1, String name2) {
		return this.builder.comparisonIsCaseSensitive() ?
			name1.equals(name2)
		:
			name1.equalsIgnoreCase(name2);
	}

	protected boolean nameIsAlreadyTaken(String name, boolean nameIsSameAsOriginal) {
		for (Iterator stream = this.builder.existingNames(); stream.hasNext(); ) {
			if (this.namesMatch(name, (String) stream.next())) {
				if ( ! nameIsSameAsOriginal) {
					// if the name can be the same as the original and the original
					// is among the "existing" names, ignore it
					return true;
				}
			}
		}
		return false;
	}

	protected boolean nameIsIllegal(String name) {
		for (Iterator stream = this.builder.illegalNames(); stream.hasNext(); ) {
			if (this.namesMatch(name, (String) stream.next())) {
				// we may want to put a check for the "original" name here, also...
				// see above
				return true;
			}
		}
		return false;
	}


	// ********** client API **********

	/**
	 * Return the new name entered by the user.
	 * This will throw an exception the dialog was not "confirmed".
	 */
	public String getNewName() {
		if (this.wasConfirmed()) {
			return this.textField.getText();
		}
		throw new IllegalStateException("dialog was not confirmed");
	}

	protected String getNameInternal() {
		return this.textField.getText();
	}

	// ********** member classes **********

	protected final ValueModel getSubjectHolder()
	{
		return subjectHolder;
	}


	protected StateObject buildStateObject()
	{
		return null;
	}

	@Override
	public void show() {
		installSubject();
		super.show();
	}
	
	private void installSubject()
	{
		StateObject subject = this.buildStateObject();
		if (subject != null)
		{
			subject.setValidator(buildValidator());
			subject.setChangeNotifier(AWTChangeNotifier.instance());
		}
		this.subjectHolder.setValue(subject);
	}


	/**
	 * Returns the subject of this dialog.
	 *
	 * @return The subject of this dialog or <code>null</code> if no subject was
	 * used
	 */
	public StateObject subject()
	{
		return (StateObject)this.subjectHolder.getValue();
	}

	Node.Validator buildValidator()
	{
		return Node.NULL_VALIDATOR;
	}


	/**
	 * Configure an instance of this class to build a NewNameDialog.
	 * 
	 * Subclasses should probably override #buildDialog(WorkbenchContext, Builder)
	 * to return the appropriate subclass of NewNameDialog.
	 */
	public static class Builder implements Cloneable {
		private String title;
		private String textFieldDescription;
		private String originalName;
		private boolean emptyNameIsLegal;
		private boolean originalNameIsLegal;
		private ArrayList existingNames;
		private ArrayList illegalNames;
		private boolean comparisonIsCaseSensitive;
		private DocumentFactory documentFactory;
		private String helpTopicId;


		// ********** constructors/initialization **********

		public Builder() {
			super();
			this.initialize();
		}

		protected void initialize() {
			this.title = null;
			this.textFieldDescription = null;
			this.originalName = null;
			this.emptyNameIsLegal = false;
			this.originalNameIsLegal = true;
			this.existingNames = new ArrayList();
			this.illegalNames = new ArrayList();
			this.comparisonIsCaseSensitive = false;
			this.documentFactory = this.buildDefaultDocumentFactory();
			this.helpTopicId = "dialog.newName";
		}


		// ********** dialog instantiation **********

		public NewNameDialog buildDialog(WorkbenchContext context) {
			return this.buildDialog(context, (Builder) this.clone());
		}

		protected Object clone() {
			Builder clone;
			try {
				clone = (Builder) super.clone();
			} catch (CloneNotSupportedException ex) {
				throw new RuntimeException(ex);
			}
			// make copies of collections
			clone.existingNames = (ArrayList) this.existingNames.clone();
			clone.illegalNames = (ArrayList) this.illegalNames.clone();
			return clone;
		}

		protected NewNameDialog buildDialog(WorkbenchContext context, Builder clone) {
			return new NewNameDialog(context, clone);
		}


		// ********** settings **********

		/**
		 * The title of the dialog. The default is null.
		 */
		public void setTitle(String title) {
			this.title = title;
		}
		public String getTitle() {
			return this.title;
		}

		/**
		 * The description displayed above the text entry field.
		 * The default is null.
		 */
		public void setTextFieldDescription(String textFieldDescription) {
			this.textFieldDescription = textFieldDescription;
		}
		public String getTextFieldDescription() {
			return this.textFieldDescription;
		}

		/**
		 * The "original" name displayed in the text entry field.
		 * The default is null.
		 */
		public void setOriginalName(String originalName) {
			this.originalName = originalName;
		}
		public String getOriginalName() {
			return this.originalName;
		}

		/**
		 * Whether the name can be an empty string.
		 * The default is false.
		 */
		public void setEmptyNameIsLegal(boolean emptyNameIsLegal) {
			this.emptyNameIsLegal = emptyNameIsLegal;
		}
		public boolean emptyNameIsLegal() {
			return this.emptyNameIsLegal;
		}
		public boolean emptyNameIsIllegal() {
			return ! this.emptyNameIsLegal;
		}

		/**
		 * Whether the "original" name can be re-used as the "new" name.
		 * The default is true.
		 */
		public void setOriginalNameIsLegal(boolean originalNameIsLegal) {
			this.originalNameIsLegal = originalNameIsLegal;
		}
		public boolean originalNameIsLegal() {
			return this.originalNameIsLegal;
		}
		public boolean originalNameIsIllegal() {
			return ! this.originalNameIsLegal;
		}

		/**
		 * The set of "existing" names that are not allowed. In most
		 * situations duplicate names are not allowed.
		 */
		public void setExistingNames(Collection existingNames) {
			this.setExistingNames(existingNames.iterator());
		}
		public void setExistingNames(Iterator existingNames) {
			this.existingNames.clear();
			this.addExistingNames(existingNames);
		}
		public void addExistingNames(Collection names) {
			this.addExistingNames(names.iterator());
		}
		public void addExistingNames(Iterator names) {
			while (names.hasNext()) {
				this.existingNames.add(names.next());
			}
		}
		public Iterator existingNames() {
			return this.existingNames.iterator();
		}

		/**
		 * The set of "illegal" names that are not allowed. Typically this
		 * includes any reserved names.
		 */
		public void setIllegalNames(Collection illegalNames) {
			this.setIllegalNames(illegalNames.iterator());
		}
		public void setIllegalNames(Iterator illegalNames) {
			this.illegalNames.clear();
			this.addIllegalNames(illegalNames);
		}
		public void addIllegalNames(Collection names) {
			this.addIllegalNames(names.iterator());
		}
		public void addIllegalNames(Iterator names) {
			while (names.hasNext()) {
				this.illegalNames.add(names.next());
			}
		}
		public Iterator illegalNames() {
			return this.illegalNames.iterator();
		}

		/**
		 * Whether the comparison between the "new" name and the "illegal" names
		 * (or "original" name) is case-sensitive.
		 * The default is false.
		 */
		public void setComparisonIsCaseSensitive(boolean comparisonIsCaseSensitive) {
			this.comparisonIsCaseSensitive = comparisonIsCaseSensitive;
		}
		public boolean comparisonIsCaseSensitive() {
			return this.comparisonIsCaseSensitive;
		}

		/**
		 * The factory used by the NewNameDialog to build the text field's
		 * model Document. This can be used to build something besides a
		 * PlainDocument; e.g. when you want to prevent certain characters
		 * from being typed into the text field.
		 */
		public void setDocumentFactory(DocumentFactory documentFactory) {
			this.documentFactory = documentFactory;
		}
		public DocumentFactory getDocumentFactory() {
			return this.documentFactory;
		}
		protected DocumentFactory buildDefaultDocumentFactory() {
			return new DocumentFactory() {
				public Document buildDocument() {
					return new PlainDocument();
				}
			};
		}

		public String getHelpTopicId() {
			return this.helpTopicId;
		}
		public void setHelpTopicId(String helpTopidId) {
			this.helpTopicId = helpTopidId;
		}

	}


	/**
	 * Simple interface for building a Document model for use by
	 * the NewNameDialog's text field.
	 */
	public interface DocumentFactory {

		/**
		 * Build and return a new Document to be used as
		 * the model for the NewNameDialog's text field.
		 */
		Document buildDocument();

	}


	/**
		 * The model object used by this dialog to automatically validate the input
		 * name.
		 */
		public static class StateObject extends AbstractNodeModel
		{
			private Builder builder;
			private ChangeNotifier changeNotifier;
			private String name;
			private Validator validator;
	
			public static final String NAME_PROPERTY = "name";
			
			protected StateObject(Builder builder)
			{
				super();
				this.builder = builder;
				this.name = builder.getOriginalName();
	
				if (name == null) {
					name = "";
				}
			}
	
			/*
			 * (non-Javadoc)
			 */
			@Override
			protected void addProblemsTo(List currentProblems)
			{
				super.addProblemsTo(currentProblems);
				editName(currentProblems);
			}
	
			/*
			 * (non-Javadoc)
			 */
			public String displayString()
			{
				return name;
			}
	
			/**
			 * Edit the name, using the settings in the builder, and update
			 * the error message.
			 */
			protected void editName(List<Problem> currentProblems) {
				String text = this.name;
	
				// empty string is not allowed
				if (StringTools.stringIsEmpty(text)) {
					currentProblems.add(buildProblem("NEW_NAME_DIALOG.EMPTY_VALUE"));
					return;
				}
	
				boolean nameIsSameAsOriginal = this.namesMatch(text, this.builder.getOriginalName());
	
				// original name might be "illegal"
				if (this.builder.originalNameIsIllegal() && nameIsSameAsOriginal) {
					currentProblems.add(buildProblem("NEW_NAME_DIALOG.ORIGINAL_VALUE"));
					return;
				}
	
				// check for "existing" name
				if (this.nameIsAlreadyTaken(text, nameIsSameAsOriginal)) {
					currentProblems.add(buildProblem("NEW_NAME_DIALOG.DUPLICATE_VALUE"));
					return;
				}
	
				// check for "illegal" name
				if (this.nameIsIllegal(text)) {
					currentProblems.add(buildProblem("NEW_NAME_DIALOG.ILLEGAL_VALUE"));
					return;
				}
	
				// no problems...
				//this.parentDialog.clearErrorMessage();
			}
	
			/*
			 * (non-Javadoc)
			 */
			@Override
			public ChangeNotifier getChangeNotifier()
			{
				return changeNotifier;
			}
	
			public String getName()
			{
				return name;
			}
	
			/*
			 * (non-Javadoc)
			 */
			@Override
			public Validator getValidator()
			{
				return validator;
			}
	
			protected boolean nameIsAlreadyTaken(String name, boolean nameIsSameAsOriginal) {
				for (Iterator<String> stream = this.builder.existingNames(); stream.hasNext(); ) {
					if (this.namesMatch(name, stream.next())) {
						if ( ! nameIsSameAsOriginal) {
							// if the name can be the same as the original and the original
							// is among the "existing" names, ignore it
							return true;
						}
					}
				}
				return false;
			}
	
			protected boolean nameIsIllegal(String name) {
				for (Iterator<String> stream = this.builder.illegalNames(); stream.hasNext(); ) {
					if (this.namesMatch(name, stream.next())) {
						// we may want to put a check for the "original" name here, also...
						// see above
						return true;
					}
				}
				return false;
			}
	
			protected boolean namesMatch(String name1, String name2) {
				return this.builder.comparisonIsCaseSensitive() ?
					name1.equals(name2)
				:
					name1.equalsIgnoreCase(name2);
			}
	
			/*
			 * (non-Javadoc)
			 */
			@Override
			public void setChangeNotifier(ChangeNotifier changeNotifier)
			{
				this.changeNotifier = changeNotifier;
			}
	
			public void setName(String name)
			{
				String oldName = this.name;
				this.name = name;
				firePropertyChanged(NAME_PROPERTY, oldName, name);
			}
	
			/*
			 * (non-Javadoc)
			 */
			@Override
			public void setValidator(Validator validator)
			{
				this.validator = validator;
			}
		}
	
	

}
