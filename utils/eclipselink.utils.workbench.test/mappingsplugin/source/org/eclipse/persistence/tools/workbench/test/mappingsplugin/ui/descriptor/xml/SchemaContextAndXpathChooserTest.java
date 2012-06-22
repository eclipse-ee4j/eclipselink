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
package org.eclipse.persistence.tools.workbench.test.mappingsplugin.ui.descriptor.xml;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.test.framework.TestWorkbenchContext;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.MappingsModelTestTools;

import org.eclipse.persistence.tools.workbench.framework.context.DefaultWorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.uitools.UIToolsIconResourceFileNameMap;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWOXProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchemaRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWNamespace;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathSpec;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.UiCommonBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.SchemaContextChooser;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.SchemaRepositoryValue;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.UiXmlBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.XpathChooser;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;

public class SchemaContextAndXpathChooserTest 
{
	/** Change this to change what schema you're test-loading */
	private final String schemaLocation = "file://C:/Paul/Documents/MappingWorkbench/JipProjects/XMLEmployee/config/employee.xsd";
		/**
		 choices:
		 	http://www.w3.org/2001/XMLSchema.xsd
		 	file://C:/Paul/XMLSpy/Examples/ipo.xsd
		 	file://C:/Paul/XMLSpy/Examples/OrgChart.xsd
		 	file://C:/Paul/Documents/MappingWorkbench/JipProjects/XMLEmployee/config/employee.xsd
		 	file://C:/Development/TopLink/Main/mwdev-schema/resource/schema/BasicAttributeGroup.xsd
		 	file://C:/Development/TopLink/Main/mwdev-schema/resource/schema/BasicContentModels.xsd
		 	file://C:/Development/TopLink/Main/mwdev-schema/resource/schema/BasicSubstitutionGroup.xsd
			file://C:/Development/TopLink/Main/mwdev-schema/resource/schema/SchemaWithIdentityConstraints.xsd
			file://C:/Development/TopLink/Main/mwdev-schema/resource/schema/TempForTesting.xsd
		*/
	
	public static void main(String[] args) throws Exception {
		new SchemaContextAndXpathChooserTest().exec(args);
	}
	
	
	private WorkbenchContextHolder contextHolder;
	private MWXmlSchemaRepository schemaRepository;
	private PropertyValueModel schemaContextComponentHolder;
	private PropertyValueModel multiElementSelectableHolder;
	private PropertyValueModel directFieldSelectableHolder;
	private PropertyValueModel complexFieldSelectableHolder;
	
	
	
	private SchemaContextAndXpathChooserTest() {
		super();
	}
	
	private void exec(String[] args) 
		throws Exception 
	{
		this.contextHolder = this.buildWorkbenchContextHolder();
		this.schemaRepository = this.buildSchemaRepository();
		this.schemaContextComponentHolder = this.buildSchemaContextComponentHolder();
		this.multiElementSelectableHolder = this.buildMultiElementSelectableHolder();
		this.directFieldSelectableHolder = this.buildDirectFieldSelectableHolder();
		this.complexFieldSelectableHolder = this.buildComplexFieldSelectableHolder();
		this.openWindow();
	}
	
	private WorkbenchContextHolder buildWorkbenchContextHolder() {
		return new DefaultWorkbenchContextHolder(
			new TestWorkbenchContext(
				UiXmlBundle.class, UIToolsIconResourceFileNameMap.class.getName()
			).buildExpandedResourceRepositoryContext(UiCommonBundle.class)
		);
	}
	
	private MWXmlSchemaRepository buildSchemaRepository() {
		return new MWOXProject(this.getClass().getName(), MappingsModelTestTools.buildSPIManager()).getSchemaRepository();
	}
	
	private PropertyValueModel buildSchemaContextComponentHolder() {
		return new SimplePropertyValueModel();
	}
	
	private PropertyValueModel buildMultiElementSelectableHolder() {
		return new SimplePropertyValueModel(new Boolean(false));
	}
	
	private PropertyValueModel buildDirectFieldSelectableHolder() {
		return new SimplePropertyValueModel(new Boolean(false));
	}
	
	private PropertyValueModel buildComplexFieldSelectableHolder() {
		return new SimplePropertyValueModel(new Boolean(false));
	}
	
	private void openWindow() {
		JFrame window = new JFrame(this.getClass().getName());
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(this.buildWindowListener());
		window.getContentPane().add(this.buildMainPanel(), "Center");
		window.setSize(500, 300);
		window.setVisible(true);
	}
	
	private WindowListener buildWindowListener() {
		return new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().setVisible(false);
				System.exit(0);
			}
		};
	}
	
	private Component buildMainPanel() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(this.buildPage(), BorderLayout.CENTER);
		mainPanel.add(this.buildControlPanel(), BorderLayout.SOUTH);
		return mainPanel;
	}
	
	private Component buildPage() {
		JPanel page = new JPanel(new GridBagLayout());
		
		GridBagConstraints constraints = new GridBagConstraints();

		JLabel label = new JLabel("Schema Context:");
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(5, 5, 0, 0);
		page.add(label, constraints);
		
		constraints.gridx		= 1;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 5, 0, 5);
		page.add(this.buildSchemaContextChooser(label), constraints);
		
		JLabel xpathLabel = new JLabel("XPath:");
		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(5, 5, 0, 0);
		page.add(xpathLabel, constraints);
		
		constraints.gridx		= 1;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 5, 0, 5);
		page.add(this.buildXpathChooser(xpathLabel), constraints);
		
		return page;
	}
	
	private Component buildSchemaContextChooser(JLabel label) {
		return new SchemaContextChooser(this.contextHolder, this.buildSchemaRepositoryValue(), this.schemaContextComponentHolder, label);
	}
	
	private SchemaRepositoryValue buildSchemaRepositoryValue() {
		return new SchemaRepositoryValue() {
			public Iterator schemas() {
				return SchemaContextAndXpathChooserTest.this.schemaRepository.schemas();
			}
		};
	}
	
	private Component buildXpathChooser(JLabel label) {
		XpathChooser chooser = new XpathChooser(this.contextHolder, this.buildXmlFieldHolder());
		chooser.setAccessibleLabeler(label);
		return chooser;
	}
	
	private PropertyValueModel buildXmlFieldHolder() {
		return new SimplePropertyValueModel(this.buildXmlField());
	}
	
	private MWXmlField buildXmlField() {
		return new MWXmlField(this.buildXpathContext());
	}
	
	private MWXpathContext buildXpathContext() {
		return new LocalXpathContext();
	}
	
	private Component buildControlPanel() {
		JPanel controlPanel = new JPanel(new GridLayout(2, 2, 2, 2));
		controlPanel.add(this.buildMultiElementSelectableToggle());
		controlPanel.add(this.buildDirectFieldSelectableToggle());
		controlPanel.add(this.buildComplexFieldSelectableToggle());
		controlPanel.add(this.buildLoadSchemaButton());
		controlPanel.add(this.buildChangePrefixesButton());
		return controlPanel;
	}
	
	private Component buildLoadSchemaButton() {
		return new JButton(this.buildLoadSchemaAction());
	}
	
	private Action buildLoadSchemaAction() {
		return new AbstractAction("Load Schema") {
			public void actionPerformed(ActionEvent e) {
				SchemaContextAndXpathChooserTest.this.loadSchema();
				this.setEnabled(false);
			}
		};
	}
	
	private void loadSchema() {
//		try {
//			this.schemaRepository.createSchemaFromUrl(schemaLocation, schemaLocation);
//		}
//		catch (Exception e) {
//			throw new RuntimeException(e);
//		}
	}
	
	private Component buildChangePrefixesButton() {
		return new JButton(this.buildChangePrefixesAction());
	}
	
	private Action buildChangePrefixesAction() {
		return new AbstractAction("Change Prefixes") {
			public void actionPerformed(ActionEvent e) {
				SchemaContextAndXpathChooserTest.this.changePrefixes();
			}
		};
	}
	
	private void changePrefixes() {
		for (Iterator stream = this.allNamespaces(); stream.hasNext(); ) {
			MWNamespace namespace = (MWNamespace) stream.next();
			namespace.setNamespacePrefixFromUser(String.valueOf(Math.random()).substring(2, 4));
		}
	}
	
	private Iterator allNamespaces() {
		return new CompositeIterator(this.namespaceIterators());
	}
	
	private Iterator namespaceIterators() {
		return new TransformationIterator(this.schemaRepository.schemas()) {
			protected Object transform(Object next) {
				return ((MWXmlSchema) next).declaredNamespaces();
			}
		};
	}
	
	private Component buildMultiElementSelectableToggle() {
		JCheckBox checkBox = new JCheckBox("Multi element selectable");
		checkBox.setModel(new CheckBoxModelAdapter(this.multiElementSelectableHolder));
		return checkBox;
	}
	
	private Component buildDirectFieldSelectableToggle() {
		JCheckBox checkBox = new JCheckBox("Direct field selectable");
		checkBox.setModel(new CheckBoxModelAdapter(this.directFieldSelectableHolder));
		return checkBox;
	}
	
	private Component buildComplexFieldSelectableToggle() {
		JCheckBox checkBox = new JCheckBox("Complex field selectable");
		checkBox.setModel(new CheckBoxModelAdapter(this.complexFieldSelectableHolder));
		return checkBox;
	}
	
	
	private class LocalXpathContext
		extends MWModel
		implements MWXpathContext
	{
		private LocalXpathContext() {
			super();
		}
		
		public MWSchemaContextComponent schemaContext(MWXmlField xmlField) {
			return (MWSchemaContextComponent) SchemaContextAndXpathChooserTest.this.schemaContextComponentHolder.getValue();
		}
		
		public MWXpathSpec xpathSpec(MWXmlField xmlField) {
			return new LocalXpathSpec();
		}
	}
	
	private class LocalXpathSpec
		implements MWXpathSpec
	{	
		public boolean mayUseCollectionData() {
			return ((Boolean) SchemaContextAndXpathChooserTest.this.multiElementSelectableHolder.getValue()).booleanValue();
		}
		
		public boolean mayUseComplexData() {
			return ((Boolean) SchemaContextAndXpathChooserTest.this.complexFieldSelectableHolder.getValue()).booleanValue();
		}
		
		public boolean mayUseSimpleData() {
			return ((Boolean) SchemaContextAndXpathChooserTest.this.directFieldSelectableHolder.getValue()).booleanValue();
		}
	}
}
