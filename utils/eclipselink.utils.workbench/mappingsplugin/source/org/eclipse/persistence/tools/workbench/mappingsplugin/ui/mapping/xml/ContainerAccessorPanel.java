package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAbstractCompositeMapping;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyCollectionValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;

public final class ContainerAccessorPanel extends AbstractSubjectPanel {

	JList accessorList;
	
	// **************** Constructors ******************************************
	
	public ContainerAccessorPanel(ValueModel compositeMappingHolder, WorkbenchContextHolder contextHolder) {
		super(compositeMappingHolder, contextHolder);
	}
	
	@Override
	protected void initializeLayout() {
		this.setLayout(new GridBagLayout());
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		// label
		JLabel label = this.buildContainerAccessorLabel();
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);
		this.add(label, constraints);
		this.addAlignLeft(label);

		// container accessor component
		Component component = this.buildContainerAccessorComponent();
		constraints.gridx		= 1;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 5, 0, 0);
		this.add(component, constraints);
		
		// edit button
		JButton button = this.buildContainerAccessorEditButton();
		constraints.gridx		= 2;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 5, 0, 0);
		this.add(button, constraints);
		this.addAlignRight(button);
		label.setLabelFor(button);
		SwingComponentFactory.updateButtonAccessibleName(label, button);
		
		this.addHelpTopicId(this, "mapping.containerAccessor");
	}
	
	private Component buildContainerAccessorComponent() {
		// Using a single element list in order to use JList's rendering ability
		accessorList = SwingComponentFactory.buildList(buildAccessorListModel());
		accessorList.setCellRenderer(buildAccessorListCellRenderer());
		accessorList.setDoubleBuffered(true);
		accessorList.setVisibleRowCount(1);
		accessorList.setPreferredSize(new Dimension(0, 0));
		// wrap in a scrollpane so that the border looks like a text field's border
		return new JScrollPane(accessorList, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}
	
	private ListModel buildAccessorListModel() {
		// Using a single element list model
		return new ListModelAdapter(buildAccessorCollectionValue());
	}

	private CollectionValueModel buildAccessorCollectionValue() {
		// Using a single element collection model
		return new PropertyCollectionValueModelAdapter(buildAccessorPropertyValue());
	}
	
	private PropertyValueModel buildAccessorPropertyValue() {
		return new PropertyAspectAdapter(this.getSubjectHolder(), MWAbstractCompositeMapping.CONTAINER_ACCESSOR_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWAbstractCompositeMapping) this.subject).getContainerAccessor();
			}
		};
	}

	private ListCellRenderer buildAccessorListCellRenderer() {
		return new AdaptableListCellRenderer(new ContainerAccessorCellRendererAdapter(this.resourceRepository()));
	}
	
	private JLabel buildContainerAccessorLabel() {
		return SwingComponentFactory.buildLabel("CONTAINER_ACCESSOR_LABEL", this.resourceRepository());
	}

	private JButton buildContainerAccessorEditButton() {
		JButton button = new JButton(this.resourceRepository().getString("CONTAINER_ACCESSOR_EDIT_BUTTON"));
		button.addActionListener(buildAttributeTransformerEditAction());
		return button;
	}
	
	private ActionListener buildAttributeTransformerEditAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				MWAbstractCompositeMapping compositeMapping = 
					(MWAbstractCompositeMapping) ContainerAccessorPanel.this.subject();
				WorkbenchContext context = 
					ContainerAccessorPanel.this.getWorkbenchContext();
				ContainerAccessorEditingDialog.promptToEditContainerAccessor(compositeMapping, context);
			}
		};
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		if (isEnabled() == enabled)
			return;

		super.setEnabled(enabled);
		updateEnableStateOfChildren(enabled);
	}
	
	/**
	 * Updates the enable state of the children of this panel.
	 *
    * @param enabled <code>true<code> if this pane's children should be enabled,
    * <code>false<code> otherwise
	 */
	protected void updateEnableStateOfChildren(boolean enabled)
	{
		for (int index = getComponentCount(); --index >= 0;)
			getComponent(index).setEnabled(enabled);
		
		this.accessorList.setEnabled(enabled);
	}
}
