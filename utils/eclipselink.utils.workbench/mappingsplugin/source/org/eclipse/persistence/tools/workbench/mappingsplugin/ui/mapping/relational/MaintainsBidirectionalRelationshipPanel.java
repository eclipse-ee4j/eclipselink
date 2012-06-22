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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational;

import java.awt.BorderLayout;
import java.util.Iterator;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooser;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.GroupBox;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWReferenceObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAbstractTableReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.MappingCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ProjectNode;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.*;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.uitools.chooser.NodeSelector;


final class MaintainsBidirectionalRelationshipPanel extends AbstractSubjectPanel
{
	MaintainsBidirectionalRelationshipPanel(ValueModel subjectHolder,
														 WorkbenchContextHolder contextHolder)
	{
		super(new BorderLayout(), subjectHolder, contextHolder);
	}

	private ButtonModel buildMaintainsBidirectionalityRelationshipCheckBoxAdapter()
	{
		return new CheckBoxModelAdapter(buildMaintainsBidirectionalityRelationshipHolder());
	}

	private PropertyValueModel buildMaintainsBidirectionalityRelationshipHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), MWAbstractTableReferenceMapping.MAINTAINS_BIDIRECTIONAL_RELATIONSHIP_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				MWAbstractTableReferenceMapping mapping = (MWAbstractTableReferenceMapping) subject;
				return Boolean.valueOf(mapping.maintainsBidirectionalRelationship());
			}

			protected void setValueOnSubject(Object value)
			{
				MWAbstractTableReferenceMapping mapping = (MWAbstractTableReferenceMapping) subject;
				mapping.setMaintainsBidirectionalRelationship(Boolean.TRUE.equals(value));
			}
		};
	}

	private ListChooser buildRelationshipPartnerChooser() {
		ListChooser chooser =
			new DefaultListChooser(buildRelationshipPartnerComboAdapter(), getWorkbenchContextHolder(), buildMappingNodeSelector());
		chooser.setRenderer(new AdaptableListCellRenderer(new MappingCellRendererAdapter(this.resourceRepository())));
		return chooser;
	}

	private CollectionValueModel buildRelationshipPartnerCollectionHolder()
	{
		PropertyAspectAdapter referenceDescriptorHolder = new PropertyAspectAdapter(getSubjectHolder(), MWReferenceObjectMapping.REFERENCE_DESCRIPTOR_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				MWAbstractTableReferenceMapping mapping = (MWAbstractTableReferenceMapping) subject;
				return mapping.getReferenceDescriptor();
			}
		};

		CollectionAspectAdapter mappingsHolder = new CollectionAspectAdapter(referenceDescriptorHolder, MWMappingDescriptor.MAPPINGS_COLLECTION)
		{
			protected Iterator getValueFromSubject()
			{
				return ((MWDescriptor) subject).mappings();
			}

			protected int sizeFromSubject()
			{
				return ((MWDescriptor) subject).mappingsSize();
			}
		};
		
		return new ListCollectionValueModelAdapter(new ItemPropertyListValueModelAdapter(mappingsHolder, MWMapping.NAME_PROPERTY));
	}

	private ComboBoxModel buildRelationshipPartnerComboAdapter()
	{
		return new ComboBoxModelAdapter(buildRelationshipPartnerListModel(),
												  buildRelationshipPartnerHolder());
	}

	private PropertyValueModel buildRelationshipPartnerHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), MWAbstractTableReferenceMapping.RELATIONSHIP_PARTNER_MAPPING_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				MWAbstractTableReferenceMapping mapping = (MWAbstractTableReferenceMapping) subject;
				return mapping.getRelationshipPartnerMapping();
			}

			protected void setValueOnSubject(Object value)
			{
				MWAbstractTableReferenceMapping mapping = (MWAbstractTableReferenceMapping) subject;
				mapping.setRelationshipPartnerMapping((MWMapping) value);
			}
		};
	}

	private ListValueModel buildRelationshipPartnerListModel()
	{
		return new SortedListValueModelAdapter
		(
			new FilteringCollectionValueModel(buildRelationshipPartnerCollectionHolder())
			{
				protected boolean accept(Object value)
				{
					return value != subject();
				}
			}
		);
	}

    private NodeSelector buildMappingNodeSelector() {
        return new NodeSelector() {
            public void selectNodeFor(Object item) {
                ProjectNode projectNode = (ProjectNode) navigatorSelectionModel().getSelectedProjectNodes()[0];
                projectNode.selectMappingNodeFor((MWMapping) item, navigatorSelectionModel());       
               
            }
        };
    }
    
    
	protected void initializeLayout()
	{
		JCheckBox maintainsBidiRelationshipCheckBox = buildCheckBox
		(
			"MAINTAINS_BIDI_RELATIONSHIP_CHECK_BOX",
			buildMaintainsBidirectionalityRelationshipCheckBoxAdapter()
		);

		JComponent relationshipPartnerWidgets = buildLabeledComponent
		(
			"MAINTAINS_BIDI_RELATIONSHIP_RELATIONSHIP_PARTNER_CHOOSER",
			buildRelationshipPartnerChooser()
		);

		new ComponentEnabler(buildMaintainsBidirectionalityRelationshipHolder(), relationshipPartnerWidgets.getComponents());

		GroupBox groupBox = new GroupBox(maintainsBidiRelationshipCheckBox, relationshipPartnerWidgets);
		add(groupBox, BorderLayout.CENTER);
		addHelpTopicId(this, "mapping.maintainsBidirectionalRelationship");
	}
}
