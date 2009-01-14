package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JCheckBox;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.GroupBox;
import org.eclipse.persistence.tools.workbench.framework.uitools.Pane;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAbstractCompositeMapping;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;

public final class UseContainerAccessorPanel extends AbstractSubjectPanel {

	private PropertyValueModel usesContainerAccessorHolder;

	public UseContainerAccessorPanel(ValueModel subjectHolder, WorkbenchContextHolder contextHolder) {
		super(subjectHolder, contextHolder);
	}
	
	@Override
	protected void initialize(ValueModel subjectHolder) {
	    super.initialize(subjectHolder);
        this.usesContainerAccessorHolder = buildUsesContainerAccessorHolder();
	}

	@Override
	protected void initializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();
		
        Collection components = new ArrayList();
        
		// Add container accessor
		JCheckBox containerAccessorCheckBox = buildUsesContainerAccessorCheckbox();
		Pane containerAccessorPanel = new Pane(new GridBagLayout());

		GroupBox groupBox = new GroupBox(containerAccessorCheckBox, containerAccessorPanel);

		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 0, 0);

		add(groupBox, constraints);
		
			// container accessor
			ContainerAccessorPanel containerAccessorSubpanelPanel = this.buildContainerAccessorSubPanel();
			components.add(containerAccessorSubpanelPanel);
			constraints.gridx		= 0;
			constraints.gridy		= 0;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 1;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.CENTER;
			constraints.insets		= new Insets(0, 5, 0, 5);
			containerAccessorPanel.add(containerAccessorSubpanelPanel, constraints);
			addPaneForAlignment(containerAccessorSubpanelPanel);


		addHelpTopicId(this, "mapping.containerAccessor");
        
        new ComponentEnabler(this.usesContainerAccessorHolder, components);
	}
	
	// **************** Container Accessor sub panel ***************************
	
	private ContainerAccessorPanel buildContainerAccessorSubPanel() {
		return new ContainerAccessorPanel(this.getSubjectHolder(), this.getWorkbenchContextHolder());
	}

	// ************* use container accessor ************
	
	private JCheckBox buildUsesContainerAccessorCheckbox() {
		JCheckBox checkBox = buildCheckBox("USE_CONTAINER_ACCESSOR_CHECK_BOX", new CheckBoxModelAdapter(this.usesContainerAccessorHolder));
		return checkBox;
	}
	
	private PropertyValueModel buildUsesContainerAccessorHolder() {
		return new PropertyAspectAdapter(getSubjectHolder(), MWAbstractCompositeMapping.USES_CONTAINER_ACCESSOR_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWAbstractCompositeMapping) this.subject).usesContainerAccessor();
			}
			protected void setValueOnSubject(Object value) {
				((MWAbstractCompositeMapping) this.subject).setUsesContainerAccessor((Boolean)value);
			}
		};
	}


}
