package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWProcedure;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWStoredProcedureQueryFormat;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;

/**
 * This panel appears on the Named Queries->QueryFormatPanel, if the user
 * chooses StoredProcedure for the query format. It contains a StoredProcedureProperiesPane
 *
 * @version 11.1.1.0
 * @since 11.1.1.0
 */
public class CustomStoredProcedureQueryFormatSubPanel extends AbstractSubjectPanel {

	CustomStoredProcedureQueryFormatSubPanel(ValueModel queryFormatHolder, WorkbenchContextHolder workbenchContextHolder) {
		super(queryFormatHolder, workbenchContextHolder);
	}
	
	@Override
	protected void initializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.BOTH;
		constraints.anchor     = GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);
		
		CustomStoredProcedurePropertiesPane storedProcedurePane = new CustomStoredProcedurePropertiesPane(buildStoredProcedureHolder(), getWorkbenchContextHolder());
		add(storedProcedurePane, constraints);

	}
	
	private PropertyValueModel buildStoredProcedureHolder() {
		return new PropertyAspectAdapter(getSubjectHolder(), MWStoredProcedureQueryFormat.PROCEDURE_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				return ((MWStoredProcedureQueryFormat)subject).getProcedure();
			}
			
			@Override
			protected void setValueOnSubject(Object value) {
				((MWStoredProcedureQueryFormat)subject).setProcedure((MWProcedure)value);
			}
		};
	}
}
