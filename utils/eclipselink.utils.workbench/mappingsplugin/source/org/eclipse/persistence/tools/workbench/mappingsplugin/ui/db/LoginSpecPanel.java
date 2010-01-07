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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWLoginSpec;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.SingleElementIterator;


public class LoginSpecPanel
	extends AbstractPanel
{
	private PropertyValueModel selectedLoginSpecHolder;
	

	public LoginSpecPanel(PropertyValueModel selectedLoginSpecHolder, ApplicationContext context) {
		super(context);
		initialize(selectedLoginSpecHolder);
	}

	protected void initialize(PropertyValueModel selectedLoginSpecHolder) {
		this.selectedLoginSpecHolder = selectedLoginSpecHolder;
		GridBagConstraints constraints = new GridBagConstraints();

		// Create the Driver Class label
		JLabel driverLabel = buildLabel("DRIVER_CLASS_COMBO_BOX_LABEL");

		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);

		add(driverLabel, constraints);
		driverLabel.setEnabled(false);
		this.selectedLoginSpecHolder.addPropertyChangeListener(PropertyValueModel.VALUE, buildSelectedLoginInfoListener(driverLabel));
		addAlignLeft(driverLabel);

		// Create the Driver Class combo box	
		JComboBox driverClassComboBox = buildDriverClassComboBox();

		constraints.gridx			= 1;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 0, 0);

		add(driverClassComboBox, constraints);
		driverLabel.setLabelFor(driverClassComboBox);
		driverClassComboBox.setEnabled(false);
		this.selectedLoginSpecHolder.addPropertyChangeListener(PropertyValueModel.VALUE, buildSelectedLoginInfoListener(driverClassComboBox));

		// Create the Driver URL label
		JLabel urlLabel = buildLabel("URL_COMBO_BOX_LABEL");

		constraints.gridx			= 0;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(5, 0, 0, 0);

		add(urlLabel, constraints);
		urlLabel.setEnabled(false);
		this.selectedLoginSpecHolder.addPropertyChangeListener(PropertyValueModel.VALUE, buildSelectedLoginInfoListener(urlLabel));
		addAlignLeft(urlLabel);

		// Create the Driver URL combo box
		JComboBox urlComboBox = buildConnectionUrlComboBox();

		constraints.gridx			= 1;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 5, 0, 0);

		add(urlComboBox, constraints);
		urlLabel.setLabelFor(urlComboBox);
		urlComboBox.setEnabled(false);
		this.selectedLoginSpecHolder.addPropertyChangeListener(PropertyValueModel.VALUE, buildSelectedLoginInfoListener(urlComboBox));

		// Create the Username label
		JLabel userNameLabel = buildLabel("USERNAME_TEXT_FIELD_LABEL");

		constraints.gridx			= 0;
		constraints.gridy			= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(5, 0, 0, 0);

		add(userNameLabel, constraints);
		userNameLabel.setEnabled(false);
		this.selectedLoginSpecHolder.addPropertyChangeListener(PropertyValueModel.VALUE, buildSelectedLoginInfoListener(userNameLabel));
		addAlignLeft(userNameLabel);

		// Create the Username text field
		JTextField userNameField = buildUserNameTextField();

		constraints.gridx			= 1;
		constraints.gridy			= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 5, 0, 0);

		add(userNameField, constraints);
		userNameLabel.setLabelFor(userNameField);
		userNameField.setEnabled(false);
		this.selectedLoginSpecHolder.addPropertyChangeListener(PropertyValueModel.VALUE, buildSelectedLoginInfoListener(userNameField));

		// Create the Password label
		JLabel passwordLabel = buildLabel("PASSWORD_TEXT_FIELD_LABEL");

		constraints.gridx			= 0;
		constraints.gridy			= 3;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(5, 0, 0, 0);

		add(passwordLabel, constraints);
		passwordLabel.setEnabled(false);
		this.selectedLoginSpecHolder.addPropertyChangeListener(PropertyValueModel.VALUE, buildSelectedLoginInfoListener(passwordLabel));
		addAlignLeft(passwordLabel);

		// Create the Password field
		JPasswordField passwordField = buildPasswordField();

		constraints.gridx			= 1;
		constraints.gridy			= 3;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 5, 0, 0);

		add(passwordField, constraints);
		passwordLabel.setLabelFor(passwordField);
		passwordField.setEnabled(false);
		this.selectedLoginSpecHolder.addPropertyChangeListener(PropertyValueModel.VALUE, buildSelectedLoginInfoListener(passwordField));

		// Create the Save Password check box
		JCheckBox savePasswordCheckBox = buildSavePasswordCheckBox();

		constraints.gridx			= 1;
		constraints.gridy			= 4;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(2, 0, 0, 0);

		add(savePasswordCheckBox, constraints);
		savePasswordCheckBox.setEnabled(false);
		this.selectedLoginSpecHolder.addPropertyChangeListener(PropertyValueModel.VALUE, buildSelectedLoginInfoListener(savePasswordCheckBox));
	}


	// ********** driver class **********

	private JComboBox buildDriverClassComboBox() {
		JComboBox comboBox = new JComboBox(new ComboBoxModelAdapter(this.buildSortedDriverClassesHolder(), this.buildDriverClassAdapter()));
		comboBox.setEditable(true);
		return comboBox;
	}

	private ListValueModel buildSortedDriverClassesHolder() {
		return new SortedListValueModelAdapter(buildAllDriverClassesAdapter());
	}

	private CollectionValueModel buildAllDriverClassesAdapter() {
		return new CollectionAspectAdapter(selectedLoginSpecHolder) {
			protected Iterator getValueFromSubject() {
				return compositeDriverClassIterator(MWLoginSpec.commonDriverClassNames());
			}
			protected int sizeFromSubject() {
				return compositeDriverClassSize(MWLoginSpec.commonDriverClassNamesSize());
			}
		};
	}
	
	//if db driver class preference is selected return iterator including it, otherwise just return the defaults
	private Iterator compositeDriverClassIterator(Iterator commonDriverClassNames) {
		String name = preferences().get(MWLoginSpec.DB_DRIVER_CLASS_PREFERENCE, null);
		if (name != null) {
			return new CompositeIterator(commonDriverClassNames, 
				new SingleElementIterator(name));
		}
		return commonDriverClassNames;
	}
	private int compositeDriverClassSize(int originalSize) {
		String name = preferences().get(MWLoginSpec.DB_DRIVER_CLASS_PREFERENCE, null);
		if (name != null) {
			originalSize += 1;	
		}
		return originalSize;
	}

	private PropertyValueModel buildDriverClassAdapter() {
		return new PropertyAspectAdapter(this.selectedLoginSpecHolder, MWLoginSpec.DRIVER_CLASS_NAME_PROPERTY) {
			protected Object getValueFromSubject() {
				String name = ((MWLoginSpec) this.subject).getDriverClassName();
				if (name == null) {
					name = preferences().get(MWLoginSpec.DB_DRIVER_CLASS_PREFERENCE, null);
					if (name != null) {
						((MWLoginSpec) this.subject).setDriverClassName(name);
					}
				}
				return name;
			}	
			protected void setValueOnSubject(Object value) {
				((MWLoginSpec) this.subject).setDriverClassName((String) value);
			}
		};
	}


	// ********** connection url **********

	private JComboBox buildConnectionUrlComboBox() {
		JComboBox comboBox = new JComboBox(new ComboBoxModelAdapter(this.buildSortedConnectionUrlsHolder(), this.buildConnectionUrlAdapter()));
		comboBox.setEditable(true);
		return comboBox;
	}

	private ListValueModel buildSortedConnectionUrlsHolder() {
		return new SortedListValueModelAdapter(buildAllConnectionUrlsAdapter());
	}

	private CollectionValueModel buildAllConnectionUrlsAdapter() {
		return new CollectionAspectAdapter(this.selectedLoginSpecHolder, MWLoginSpec.CANDIDATE_URLS_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return compositeConnectionUrlIterator(((MWLoginSpec) this.subject).candidateURLs());
			}
			protected int sizeFromSubject() {
				return compositeConnectionUrlSize(((MWLoginSpec) this.subject).candidateURLsSize());
			}
		};
	}

	//if db connection url preference is selected return iterator including it, otherwise just return the defaults
	private Iterator compositeConnectionUrlIterator(Iterator connectionUrls) {
		String url = preferences().get(MWLoginSpec.DB_CONNECTION_URL_PREFERENCE, null);
		if (url != null) {
			return new CompositeIterator(connectionUrls, 
				new SingleElementIterator(url));
		}
		return connectionUrls;
	}
	private int compositeConnectionUrlSize(int originalSize) {
		String url = preferences().get(MWLoginSpec.DB_CONNECTION_URL_PREFERENCE, null);
		if (url != null) {
			originalSize += 1;	
		}
		return originalSize;
	}

	private PropertyValueModel buildConnectionUrlAdapter() {
		return new PropertyAspectAdapter(this.selectedLoginSpecHolder, MWLoginSpec.URL_PROPERTY) {
			protected Object getValueFromSubject() {
				String url = ((MWLoginSpec) this.subject).getURL();
				if (url == null) {
					url = preferences().get(MWLoginSpec.DB_CONNECTION_URL_PREFERENCE, null);
					if (url != null) {
						((MWLoginSpec) this.subject).setURL(url);
					}
				}
				return url;
			}	
			protected void setValueOnSubject(Object value) {
				String string = (String) value;
				MWLoginSpec loginSpec = (MWLoginSpec) this.subject;
				if ((string == null) || (string.length() == 0)) {
					loginSpec.setURL(null);
				} else {
					loginSpec.setURL(string);
				}
			}
		};
	}


	// ********** UserName Field **********
	
	private JTextField buildUserNameTextField() {
		return new JTextField(buildUserNameDocumentAdapter(), null, 1);
	}
	
	private Document buildUserNameDocumentAdapter() {
		return new DocumentAdapter(buildUserNameAdapter());
	}
	
	private PropertyValueModel buildUserNameAdapter() {
		return new PropertyAspectAdapter(this.selectedLoginSpecHolder, MWLoginSpec.USER_NAME_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWLoginSpec) this.subject).getUserName();
			}	
			protected void setValueOnSubject(Object value) {
				((MWLoginSpec) this.subject).setUserName((String) value);
			}
		};
	}


	// ********** Password Field **********
	
	//TODO JPasswordField.getText() is deprecated - need to look into this
	private JPasswordField buildPasswordField() {
		return new JPasswordField(buildPasswordDocumentAdapter(), null, 1);
	}
	
	private Document buildPasswordDocumentAdapter() {
		return new DocumentAdapter(buildPasswordAdapter());
	}
	
	private PropertyValueModel buildPasswordAdapter() {
		return new PropertyAspectAdapter(this.selectedLoginSpecHolder, MWLoginSpec.PASSWORD_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWLoginSpec) this.subject).getPassword();
			}	
			protected void setValueOnSubject(Object value) {
				((MWLoginSpec) this.subject).setPassword((String) value);
			}
		};
	}
	
	
	// ********** Save Password **********
	
	private JCheckBox buildSavePasswordCheckBox() {
		return buildCheckBox("SAVE_PASSWORD_CHECK_BOX", buildSavePasswordCheckBoxModelAdapter());
	}
	
	private ButtonModel buildSavePasswordCheckBoxModelAdapter() {
		return new CheckBoxModelAdapter(buildSavePasswordPropertyAdapter());
	}
	
	private PropertyValueModel buildSavePasswordPropertyAdapter() {
		return new PropertyAspectAdapter(this.selectedLoginSpecHolder, MWLoginSpec.SAVE_PASSWORD_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWLoginSpec) this.subject).isSavePassword());
			}	
			protected void setValueOnSubject(Object value) {
				((MWLoginSpec) this.subject).setSavePassword(((Boolean) value).booleanValue());
			}
		};
	}
	
	private PropertyChangeListener buildSelectedLoginInfoListener(final Component component) {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				component.setEnabled(evt.getNewValue() != null);
			}
		};
	}
}
