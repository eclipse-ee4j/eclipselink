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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.DefaultWorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassChooserPanel;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractValidatingDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.Spacer;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQuery;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassChooserTools;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassRepositoryHolder;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


public class QueryParameterDialog extends AbstractValidatingDialog {

	private JTextField parameterNameField;
	private PropertyValueModel parameterNameHolder;
	private PropertyValueModel parameterTypeHolder;
	private MWQuery query;
	
	public QueryParameterDialog(MWQuery query, MWClass type, String name, AbstractDialog dialog, WorkbenchContext context, String title) {
		super(context, title, dialog);
		this.query = query;
		this.parameterTypeHolder = new SimplePropertyValueModel(type);
		this.parameterNameHolder = new SimplePropertyValueModel(name);
	}
	
	public QueryParameterDialog(MWQuery query, MWClass type, String name, WorkbenchContext context, String title) {
		super(context, title);
		this.query = query;
		this.parameterTypeHolder = new SimplePropertyValueModel(type);
		this.parameterNameHolder = new SimplePropertyValueModel(name);
	}	
	
	private ClassRepositoryHolder buildClassRepositoryHolder() {
		return new ClassRepositoryHolder() {
			public MWClassRepository getClassRepository() {
				return query.getRepository();
			}
		};
	}

	protected Component buildMainPanel() {
		return new MainPanel(new DefaultWorkbenchContextHolder(this.getWorkbenchContext()));
	}
	
	private Document buildParameterNameDocumentAdapter() {
		DocumentAdapter adapter = new DocumentAdapter(this.parameterNameHolder);
		adapter.addDocumentListener(buildParameterNameDocumentListener());
		return adapter;
	}

	private DocumentListener buildParameterNameDocumentListener() {
		return new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				if (isVisible()) {
					updateDialogState();
				}
			}

			public void insertUpdate(DocumentEvent e) {
				if (isVisible()) {
					updateDialogState();
				}
			}

			public void removeUpdate(DocumentEvent e) {
				if (isVisible()) {
					updateDialogState();
				}
			}
		};
	}

	public String getParameterName() {
		return (String) this.parameterNameHolder.getValue();
	}

	public MWClass getParameterType() {
		return (MWClass) this.parameterTypeHolder.getValue();
	}

	private String getPotentialParameterName() {
		return this.parameterNameField.getText();
	}

	protected String helpTopicId() {
		return "descriptor.queryManager.general.parameters.addParameter";
	}
	
	protected Component initialFocusComponent() {
		return parameterNameField;
	}
	
	private void updateDialogState() {
		String parameterName = getPotentialParameterName();
		String message = null;

		// No parameter name defined
		if (StringTools.stringIsEmpty(parameterName)) {
			message = resourceRepository().getString("QUERY_PARAMETER_DIALOG_NO_PARAMETER_NAME_SPECIFIED");
		}

		// The parameter already exist
		else if (CollectionTools.contains(this.query.parameterNames(), parameterName)) {
			message = resourceRepository().getString("QUERY_PARAMETER_DIALOG_CLASS_ALREADY_EXISTS", parameterName);
		}

		setErrorMessage(message);
		getOKAction().setEnabled(message == null);
	}
	
	private class MainPanel extends AbstractPanel {
		MainPanel(WorkbenchContextHolder contextHolder) {
			super(contextHolder);
			initializeLayout();
		}

		private void initializeLayout() {
			GridBagConstraints constraints = new GridBagConstraints();
			
			JLabel parameterTypeLabel = SwingComponentFactory.buildLabel("QUERY_PARAMETER_DIALOG.PARAMETER_TYPE_LABEL", resourceRepository());

			constraints.gridx      = 0;
			constraints.gridy   	  = 0;
			constraints.gridwidth  = 1;
			constraints.gridheight = 1;
			constraints.weightx    = 0;
			constraints.weighty    = 0;
			constraints.fill       = GridBagConstraints.NONE;
			constraints.anchor     = GridBagConstraints.LINE_START;
			constraints.insets     = new Insets(0, 0, 0, 0);

			add(parameterTypeLabel, constraints);

			ClassChooserPanel parameterTypeChooserPanel = ClassChooserTools.buildPanel(
							parameterTypeHolder,
							buildClassRepositoryHolder(),
							ClassChooserTools.buildDeclarableNonVoidFilter(),
							parameterTypeLabel,
							new DefaultWorkbenchContextHolder(this.getWorkbenchContext())
			);
			helpManager().addTopicID(parameterTypeChooserPanel, helpTopicId() + ".type");
			constraints.gridx      = 1;
			constraints.gridy      = 0;
			constraints.gridwidth  = 2;
			constraints.gridheight = 1;
			constraints.weightx    = 1;
			constraints.weighty    = 0;
			constraints.fill       = GridBagConstraints.HORIZONTAL;
			constraints.anchor     = GridBagConstraints.CENTER;
			constraints.insets     = new Insets(0, 5, 0, 0);
			add(parameterTypeChooserPanel, constraints);
			addPaneForAlignment(parameterTypeChooserPanel);
			
			// Data Type Label
			JLabel parameterNameLabel = SwingComponentFactory.buildLabel("QUERY_PARAMETER_DIALOG.PARAMETER_NAME_LABEL", resourceRepository());
			constraints.gridx      = 0;
			constraints.gridy      = 1;
			constraints.gridwidth  = 1;
			constraints.gridheight = 1;
			constraints.weightx    = 0;
			constraints.weighty    = 0;
			constraints.fill       = GridBagConstraints.NONE;
			constraints.anchor     = GridBagConstraints.LINE_START;
			constraints.insets     = new Insets(5, 0, 0, 0);
			add(parameterNameLabel, constraints);

			parameterNameField = new JTextField(buildParameterNameDocumentAdapter(), null, 1);
			constraints.gridx       = 1;
			constraints.gridy       = 1;
			constraints.gridwidth   = 1;
			constraints.gridheight  = 1;
			constraints.weightx     = 1;
			constraints.weighty     = 0;
			constraints.fill        = GridBagConstraints.HORIZONTAL;
			constraints.anchor      = GridBagConstraints.CENTER;
			constraints.insets      = new Insets(5, 5, 0, 0);
			add(parameterNameField, constraints);
			parameterNameLabel.setLabelFor(parameterNameField);

			Spacer spacer = new Spacer();

			constraints.gridx      = 2;
			constraints.gridy      = 1;
			constraints.gridwidth  = 1;
			constraints.gridheight = 1;
			constraints.weightx    = 0;
			constraints.weighty    = 0;
			constraints.fill       = GridBagConstraints.NONE;
			constraints.anchor     = GridBagConstraints.CENTER;
			constraints.insets     = new Insets(5, 5, 0, 0);
			add(spacer, constraints);
			addAlignRight(spacer);
		}
	}
}
