/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.login;

// JDK
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingTools;
import org.eclipse.persistence.tools.workbench.framework.uitools.TriStateBooleanCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ReadOnlyCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.NumberSpinnerModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.CellRendererAdapter;
import org.eclipse.persistence.tools.workbench.utility.TriStateBoolean;

// Mapping Workbench

/**
 * This page shows the JDBC Options and Advanced Options for database login.
 * <p>
 * Here the layout:
 * <pre>
 * _____________________________________________
 * |                  ________________________ |
 * | Table Qualifier: | I                    | |
 * |                  ------------------------ |
 * | _JDBC Options____________________________ |
 * | |                                       | |
 * | | x Queries Should Bind All Parameters  | |
 * | |                                       | |
 * | | x Cache All Statements                | |
 * | |                                       | |
 * | | x Byte Array Binding                  | |
 * | |                                       | |
 * | | x Streams for Binding                 | |
 * | |                                       | |
 * | | x Native SQL                          | |
 * | |                    __________________ | |  ____________
 * | |   Batch Writing:   |              |v| | |<-| None     |
 * | |                    ------------------ | |  | JDBC     |
 * | |                          ____________ | |  | Buffered |
 * | | x String Binding   Size: | I      |I| | |  ------------
 * | |                          ------------ | |
 * | ----------------------------------------- |
 * | _Advanced Options________________________ |
 * | |                                       | |
 * | | x Force Field Names to Uppercase      | |
 * | |                                       | |
 * | | x Optimize Data Conversion            | |
 * | |                                       | |
 * | | x Trim Strings                        | |
 * | |                                       | |
 * | | x Use Properties                      | |<- Activate the Properties tab
 * | |                                       | |
 * | ----------------------------------------- |
 * ---------------------------------------------</pre>
 *
 * Known containers of this page:<br>
 * - {@link RdbmsOptionsPropertiesPage}
 *
 * @see DatabaseLoginAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public class RdbmsOptionsPropertiesPage extends AbstractLoginPropertiesPage
{
	/**
	 * Creates a new <code>RdbmsOptionsPropertiesPage</code>.
	 *
	 * @param nodeHolder The holder of <code>SessionNode</code>
	 */
	public RdbmsOptionsPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder)
	{
		super(nodeHolder, contextHolder);
	}

	/**
	 * Creates the pane that contains the Advanced Options.
	 *
	 * @return The fully initialized pane with its widgets
	 */
	private JComponent buildAdvancedOptionsPane()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		int index = 0;

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(0, 5, 0, 0);

		// Create the container
		JPanel panel = new JPanel(new GridBagLayout());

		// Force Field Names to Uppercase check box
		JCheckBox forceFieldNamesToUpperCaseCheckBox =
			buildCheckBox("LOGIN_FORCE_FIELD_NAMES_TO_UPPERCASE_CHECK_BOX",
							  buildForceFieldNamesToUppercaseCheckBoxAdapter());

		constraints.gridy = index++;
		panel.add(forceFieldNamesToUpperCaseCheckBox, constraints);

		// Optimize Data Conversion check box
		JCheckBox optimizeDataConversionCheckBox =
			buildCheckBox("LOGIN_OPTIMIZE_DATA_CONVERSION_CHECK_BOX",
							  buildOptimizeDataConversionCheckBoxAdapter());

		constraints.gridy = index++;
		panel.add(optimizeDataConversionCheckBox, constraints);

		// Trim String check box
		JCheckBox trimStringCheckBox =
			buildCheckBox("LOGIN_TRIM_STRING_CHECK_BOX",
							  buildTrimStringsCheckBoxAdapter());

		constraints.gridy  = index++;
		panel.add(trimStringCheckBox, constraints);

		// Properties check box
		JCheckBox propertiesCheckBox =
			buildCheckBox("LOGIN_USE_PROPERTIES_CHECK_BOX",
							  buildUsePropertiesCheckBoxAdapter());

		constraints.gridy  = index++;
		constraints.insets = new Insets(0, 5, 0, 0);
		panel.add(propertiesCheckBox, constraints);

		addHelpTopicId(panel, "session.login.database.options.advanced");
		return panel;
	}

	/**
	 * Creates
	 *
	 * @return
	 */
	private CollectionValueModel buildBatchWritingCollectionHolder()
	{
		Vector booleanValues = new Vector();
		booleanValues.add(TriStateBoolean.UNDEFINED);
		booleanValues.add(TriStateBoolean.TRUE);
		booleanValues.add(TriStateBoolean.FALSE);

		return new ReadOnlyCollectionValueModel(booleanValues);
	}

	/**
	 * Creates the <code>ComboBoxModel</code> that keeps the selected state from
	 * the check box in sync with the Batch Writing value in the model and vice
	 * versa.
	 * 
	 * @return A new <code>ComboBoxModel</code>
	 */
	private ComboBoxModel buildBatchWritingComboBoxAdapter()
	{
		return new ComboBoxModelAdapter(buildBatchWritingCollectionHolder(),
												  buildBatchWritingSelectionHolder());
	}

	/**
	 * Creates the decorator responsible to format the <code>TriStateBoolean</code>
	 * values in the Batch Writing combo box.
	 * 
	 * @return {@link RdbmsOptionsPropertiesPage.TriStateBooleanLabelDecorator}
	 */
	private CellRendererAdapter buildBatchWritingLabelDecorator()
	{
        return new TriStateBooleanCellRendererAdapter(resourceRepository()) {
            protected String undefinedResourceKey() {
                return "LOGIN_BATCH_WRITING_NONE_CHOICE";
            }
            
            protected String trueResourceKey() {
                return "LOGIN_BATCH_WRITING_JDBC_CHOICE";
            }
            
            protected String falseResourceKey() {
                return "LOGIN_BATCH_WRITING_BUFFERED_CHOICE";
            }
        };
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Batch Writing and the type (JDBC or Buffered) property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildBatchWritingSelectionHolder()
	{
		String[] propertyNames =
		{
			DatabaseLoginAdapter.BATCH_WRITING_PROPERTY,
			DatabaseLoginAdapter.JDBC_BATCH_WRITING_PROPERTY,
		};

		return new PropertyAspectAdapter(getSelectionHolder(), propertyNames)
		{
			protected Object getValueFromSubject()
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				boolean batchWriting = login.usesBatchWriting();

				if (!batchWriting)
					return TriStateBoolean.UNDEFINED;

				return TriStateBoolean.valueOf(login.usesJdbcBatchWriting());
			}

			protected void setValueOnSubject(Object value)
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				TriStateBoolean batchWriting = (TriStateBoolean) value;

				login.setBatchWriting(!batchWriting.isUndefined());

				if (batchWriting.isTrue())
					login.setJdbcBatchWriting(true);
				else if (batchWriting.isFalse())
					login.setJdbcBatchWriting(false);
			}
		};
	}

	/**
	 * Creates the sub-panel that will contains the following widgets:
	 * <pre>
	 * __________________________________________
	 * |                    ___________________ |  ____________
	 * |   Batch Writing:   |               |v| |<-| None     |
	 * |                    ------------------- |  | JDBC     |
	 * |                          _____________ |  | Buffered |
	 * | x String Binding   Size: |         |I| |  ------------
	 * |                          ------------- |
	 * ------------------------------------------</pre>
	 *
	 * @return The fully initializes sub-pane with its widgets
	 */
	private JComponent buildBatchWritingStringBindingPane()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		int space = SwingTools.checkBoxIconWidth();

		// Create the container
		JPanel container = new JPanel(new GridBagLayout());
		container.getAccessibleContext().setAccessibleName(resourceRepository().getString("LOGIN_JDBC_OPTIONS_TITLE"));

		// Batch Writing label
		JLabel batchWritingLabel = buildLabel("LOGIN_BATCH_WRITING_COMBO_BOX");

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(2, space, 0, 0);

		container.add(batchWritingLabel, constraints);

		// Batch Writing combo box
		JComboBox batchWritingComboBox = new JComboBox(buildBatchWritingComboBoxAdapter());
		batchWritingComboBox.setRenderer(new AdaptableListCellRenderer(buildBatchWritingLabelDecorator()));
		batchWritingComboBox.setName("LOGIN_BATCH_WRITING_COMBO_BOX");

		constraints.gridx       = 1;
		constraints.gridy       = 0;
		constraints.gridwidth   = 2;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(2, 20, 0, 0);

		container.add(batchWritingComboBox, constraints);
		batchWritingLabel.setLabelFor(batchWritingComboBox);

		// String size label
		JLabel stringSizeLabel = buildLabel("LOGIN_MAX_BATCH_WRITING_SIZE_SPINNER");

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LAST_LINE_START;
		constraints.insets      = new Insets(2, 40, 0, 0);

		container.add(stringSizeLabel, constraints);

		// String size spinner
		JSpinner stringSizeSpinner = SwingComponentFactory.buildSpinnerNumber(buildStringSizeSpinnerAdapter());
		stringSizeSpinner.setName("LOGIN_MAX_BATCH_WRITING_SIZE_SPINNER");

		constraints.gridx       = 1;
		constraints.gridy       = 1;
		constraints.gridwidth   = 2;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(2, 5, 0, 0);

		container.add(stringSizeSpinner, constraints);
		stringSizeLabel.setLabelFor(stringSizeSpinner);

		// String Binding check box
		JCheckBox stringBindingCheckBox =
			buildCheckBox("LOGIN_STRING_BINDING_CHECK_BOX", buildStringBindingCheckBoxAdapter());

		constraints.gridx       = 0;
		constraints.gridy       = 2;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(4, 0, 0, 0);

		container.add(stringBindingCheckBox, constraints);

		return container;
	}

	/**
	 * Creates the <code>ButtonModel</code> that keeps the selected state from
	 * the check box in sync with the Byte Array Binding value in the model and
	 * vice versa.
	 * 
	 * @return A new <code>CheckBoxModelAdapter</code>
	 */
	private ButtonModel buildByteArrayBindingCheckBoxAdapter()
	{
		return new CheckBoxModelAdapter(buildByteArrayBindingHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Byte Array Binding property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildByteArrayBindingHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), DatabaseLoginAdapter.BYTE_ARRAY_BINDING_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				return Boolean.valueOf(login.usesByteArrayBinding());
			}

			protected void setValueOnSubject(Object value)
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				login.setByteArrayBinding(Boolean.TRUE.equals(value));
			}
		};
	}

	/**
	 * Creates the <code>ButtonModel</code> that keeps the selected state from
	 * the check box in sync with the Cache All Statements value in the model and
	 * vice versa.
	 * 
	 * @return A new <code>CheckBoxModelAdapter</code>
	 */
	private ButtonModel buildCacheAllStatementsCheckBoxAdapter()
	{
		return new CheckBoxModelAdapter(buildCacheAllStatementsHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Cache All Statements property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildCacheAllStatementsHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), DatabaseLoginAdapter.CACHE_ALL_STATEMENTS_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				return Boolean.valueOf(login.cachesAllStatements());
			}

			protected void setValueOnSubject(Object value)
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				login.setCacheAllStatements(Boolean.TRUE.equals(value));
			}
		};
	}
	
	private ButtonModel buildConnectionHealthValidatedOnErrorCheckBoxModel() {
		return new CheckBoxModelAdapter(buildConnectionHealthValidatedOnErrorHolder());
	}

	private PropertyValueModel buildConnectionHealthValidatedOnErrorHolder() {
		return new PropertyAspectAdapter(getSelectionHolder(), DatabaseLoginAdapter.CONNECTION_HEALTH_VALIDATE_ON_ERROR_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((DatabaseLoginAdapter)subject).isConnectionHealthValidatedOnError());
			}

			@Override
			protected void setValueOnSubject(Object value) {
				((DatabaseLoginAdapter)subject).setConnectionHealthValidatedOnError(((Boolean)value).booleanValue());
			}
		};
	}

	private ButtonModel buildNativeSequencingCheckBoxModel(){
		return new CheckBoxModelAdapter(buildNativeSequencingHolder());
	}

	private PropertyValueModel buildNativeSequencingHolder() {
		return new PropertyAspectAdapter(getSelectionHolder(), DatabaseLoginAdapter.NATIVE_SEQUENCING_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((DatabaseLoginAdapter)subject).isNativeSequencing());
			}

			@Override
			protected void setValueOnSubject(Object value) {
				((DatabaseLoginAdapter)subject).setIsNativeSequencing(((Boolean)value).booleanValue());
			}
		};
	}

	/**
	 * Creates the <code>ButtonModel</code> that keeps the selected state from
	 * the check box in sync with the Force Field Names to Uppercase value in the
	 * model and vice versa.
	 * 
	 * @return A new <code>CheckBoxModelAdapter</code>
	 */
	private ButtonModel buildForceFieldNamesToUppercaseCheckBoxAdapter()
	{
		return new CheckBoxModelAdapter(buildForceFieldNamesToUppercaseHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Force Field Names to Uppercase property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildForceFieldNamesToUppercaseHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), DatabaseLoginAdapter.FORCE_FIELD_NAMES_TO_UPPER_CASE_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				return Boolean.valueOf(login.forcesFieldNamesToUppercase());
			}

			protected void setValueOnSubject(Object value)
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				login.setForceFieldNamesToUppercase(Boolean.TRUE.equals(value));
			}
		};
	}

	/**
	 * Creates the pane that contains the JDBC Options options.
	 *
	 * @return The fully initialized pane with its widgets
	 */
	private JComponent buildJDBCOptionsPane()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		int index = 0;
		int space = SwingTools.checkBoxIconWidth() + 5;

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(0, space, 0, 0);

		// Create the container
		JPanel panel = new JPanel(new GridBagLayout());

		// Ping SQL widgets
		JComponent pingSQLWidgets = buildLabeledTextField("JDBC_OPTIONS_PANE_PING_SQL_LABEL", buildPingSQLDocument());
		
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.gridy 		= index++;
		panel.add(pingSQLWidgets, constraints);

		// Query Retry Attempts widgets
		JComponent queryRetryAttemptsWidgets = buildLabeledSpinnerNumber("JDBC_OPTIONS_PANE_QUERY_RETRY_ATTEMPTS_LABEL", buildQueryRetryAttemptsSpinnerModel(), 6);

		constraints.gridy 		= index++;
		constraints.insets      = new Insets(5, space, 0, 0);
		panel.add(queryRetryAttemptsWidgets, constraints);
		
		// Delay Between Connection Attempts widgets
		JComponent delayBetweenConnectionAttemptsWidgets = buildLabeledSpinnerNumber("JDBC_OPTIONS_PANE_DELAY_BETWEEN_CONNECTION_ATTEMPTS_LABEL", buildDelayBetweenConnectionAttemptsSpinnerModel(), 6);
		
		constraints.gridy		= index++;
		panel.add(delayBetweenConnectionAttemptsWidgets, constraints);

		// Connection Health Validated On Error check box
		JCheckBox connectionHealthValidatedOnErrorCheckBox = buildCheckBox("JDBC_OPTIONS_PANE_CONNECTION_HEALTH_VALIDATED_ON_ERROR_CHECK_BOX", buildConnectionHealthValidatedOnErrorCheckBoxModel());

		constraints.fill        = GridBagConstraints.NONE;
		constraints.gridy = index++;
		constraints.insets      = new Insets(0, 5, 0, 0);
		panel.add(connectionHealthValidatedOnErrorCheckBox, constraints);
		
		// Queries Should Bind All Parameters check box
		JCheckBox queriesShouldBindAllParametersCheckBox =
			buildCheckBox("LOGIN_QUERIES_SHOULD_BIND_ALL_PARAMETERS_CHECK_BOX",
							  buildQueriesShouldBindAllParametersCheckBoxAdapter());

		constraints.gridy = index++;
		panel.add(queriesShouldBindAllParametersCheckBox, constraints);

		// Cache All Statements check box
		JCheckBox cacheAllStatementsCheckBox =
			buildCheckBox("LOGIN_CACHE_ALL_STATEMENTS_CHECK_BOX",
							  buildCacheAllStatementsCheckBoxAdapter());

		constraints.gridy = index++;
		panel.add(cacheAllStatementsCheckBox, constraints);

		// Byte Array Binding check box
		JCheckBox byteArrayBindingCheckBox =
			buildCheckBox("LOGIN_BYTE_ARRAY_BINDING_CHECK_BOX",
							  buildByteArrayBindingCheckBoxAdapter());

		constraints.gridy = index++;
		panel.add(byteArrayBindingCheckBox, constraints);

		// Streams For Binding check box
		JCheckBox streamsForBindingCheckBox =
			buildCheckBox("LOGIN_STREAMS_FOR_BINDING_CHECK_BOX",
							  buildStreamsForBindingCheckBoxAdapter());

		constraints.gridy = index++;
		panel.add(streamsForBindingCheckBox, constraints);

		// Native Sequencing check box
		JCheckBox nativeSequencingCheckBox = buildCheckBox("JDBC_OPTIONS_PANE_NATIVE_SEQUENCING_CHECK_BOX", buildNativeSequencingCheckBoxModel());
		constraints.gridy = index++;
		panel.add(nativeSequencingCheckBox, constraints);
		
		// Native SQL check box
		JCheckBox nativeSQLCheckBox =
			buildCheckBox("LOGIN_NATIVE_SQL_CHECK_BOX",
							  buildNativeSQLCheckBoxAdapter());

		constraints.gridy = index++;
		panel.add(nativeSQLCheckBox, constraints);

		// Batch Reading label and combo box et
		// String Binding check box and spin button
		JComponent stringBindingPane = buildBatchWritingStringBindingPane();

		constraints.gridy  = index++;
		constraints.insets = new Insets(0, 5, 5, 0);
		panel.add(stringBindingPane, constraints);

		addHelpTopicId(panel, "session.login.database.options.jdbcOptions");
		return panel;
	}

	private SpinnerNumberModel buildQueryRetryAttemptsSpinnerModel() {
		return new NumberSpinnerModelAdapter(buildQueryRetryAttemptsHolder(), 0, Integer.MAX_VALUE, 1, 3);
	}
	
	private PropertyValueModel buildQueryRetryAttemptsHolder() {
		return new PropertyAspectAdapter(getSelectionHolder(), DatabaseLoginAdapter.QUERY_RETRY_ATTEMPT_COUNT_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				return ((DatabaseLoginAdapter)subject).getQueryRetryAttemptCount();
			}

			@Override
			protected void setValueOnSubject(Object value) {
				((DatabaseLoginAdapter)subject).setQueryRetryAttemptCount((Integer)value);
			}
		};
	}

	private SpinnerNumberModel buildDelayBetweenConnectionAttemptsSpinnerModel() {
		return new NumberSpinnerModelAdapter(buildDelayBetweenConnectionAttemptsHolder(), 0, Integer.MAX_VALUE, 1, 5000);
	}
	
	private PropertyValueModel buildDelayBetweenConnectionAttemptsHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), DatabaseLoginAdapter.DELAY_BETWEEN_CONNECTION_ATTEMPTS_PROPERTY)
		{
			@Override
			protected Object getValueFromSubject()
			{
				return ((DatabaseLoginAdapter)subject).getDelayBetweenConnectionAttempts();
			}

			@Override
			protected void setValueOnSubject(Object value)
			{
				((DatabaseLoginAdapter)subject).setDelayBetweenConnectionAttempts((Integer)value);
			}
		};
	}


	private Document buildPingSQLDocument() {
		return new DocumentAdapter(buildPingSQLHolder());
	}

	private PropertyValueModel buildPingSQLHolder() {
		return new PropertyAspectAdapter(getSelectionHolder(), DatabaseLoginAdapter.PING_SQL_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				return ((DatabaseLoginAdapter)subject).getPingSQL();
			}

			@Override
			protected void setValueOnSubject(Object value) {
				((DatabaseLoginAdapter)subject).setPingSQL((String)value);
			}
		};
	}

	/**
	 * Creates the <code>ButtonModel</code> that keeps the selected state from
	 * the check box in sync with the Native SQL value in the model and vice
	 * versa.
	 * 
	 * @return A new <code>CheckBoxModelAdapter</code>
	 */
	private ButtonModel buildNativeSQLCheckBoxAdapter()
	{
		return new CheckBoxModelAdapter(buildNativeSQLHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Native SQL property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildNativeSQLHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), DatabaseLoginAdapter.NATIVE_SQL_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				return Boolean.valueOf(login.usesNativeSQL());
			}

			protected void setValueOnSubject(Object value)
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				login.setNativeSQL(Boolean.TRUE.equals(value));
			}
		};
	}

	/**
	 * Creates the <code>ButtonModel</code> that keeps the selected state from
	 * the check box in sync with the Optimize Data Conversion value in the model
	 * and vice versa.
	 * 
	 * @return A new <code>CheckBoxModelAdapter</code>
	 */
	private ButtonModel buildOptimizeDataConversionCheckBoxAdapter()
	{
		return new CheckBoxModelAdapter(buildOptimizeDataConversionHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Optimize Data Conversion property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildOptimizeDataConversionHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), DatabaseLoginAdapter.OPTIMIZE_DATA_CONVERSION_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				return Boolean.valueOf(login.optimizesDataConversion());
			}

			protected void setValueOnSubject(Object value)
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				login.setOptimizeDataConversion(Boolean.TRUE.equals(value));
			}
		};
	}

	/**
	 * Initializes the layout of this pane.
	 *
	 * @return The container with all its widgets
	 */
	protected Component buildPage()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		// Create the container
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Create Table Qualifier label
		JComponent tableQualifierWidgets = buildLabeledTextField
		(
			"LOGIN_TABLE_QUALIFIER_FIELD",
			buildTableQualifierDocumentAdater()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(0, 0, 0, 0);

		panel.add(tableQualifierWidgets, constraints);
		addHelpTopicId(tableQualifierWidgets, "session.login.database.options.tableQualifier");

		// Create JDBC Options panel
		JComponent jdbcOptionsPanel = buildJDBCOptionsPane();
		jdbcOptionsPanel.setBorder(buildTitledBorder("LOGIN_JDBC_OPTIONS_TITLE"));

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(10, 0, 0, 0);

		panel.add(jdbcOptionsPanel, constraints);

		// Create Advanced Options panel
		JComponent advancedOptionsPanel = buildAdvancedOptionsPane();
		advancedOptionsPanel.setBorder(buildTitledBorder("LOGIN_ADVANCED_OPTIONS_TITLE"));

		constraints.gridx       = 0;
		constraints.gridy       = 2;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 1;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(5, 0, 0, 0);

		panel.add(advancedOptionsPanel, constraints);
		
		// Event Listeners sub-pane
		StructConvertersPane structConvertersPane = new StructConvertersPane(getSelectionHolder(), getWorkbenchContextHolder());

		constraints.gridx       = 0;
		constraints.gridy       = 3;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 1;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.PAGE_START;
		constraints.insets      = new Insets(5, 0, 0, 0);

		panel.add(structConvertersPane, constraints);
		addPaneForAlignment(structConvertersPane);

		addHelpTopicId(panel, "session.login.database.options");
		return panel;
	}

	/**
	 * Creates the <code>ButtonModel</code> that keeps the selected state from
	 * the check box in sync with the Queries Should Bind All Parameters value in
	 * the model and vice versa.
	 * 
	 * @return A new <code>CheckBoxModelAdapter</code>
	 */
	private ButtonModel buildQueriesShouldBindAllParametersCheckBoxAdapter()
	{
		return new CheckBoxModelAdapter(buildQueriesShouldBindAllParametersHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Queries Should Bind All Parameters property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildQueriesShouldBindAllParametersHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), DatabaseLoginAdapter.BIND_ALL_PARAMETERS_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				return Boolean.valueOf(login.bindsAllParameters());
			}

			protected void setValueOnSubject(Object value)
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				login.setBindAllParameters(Boolean.TRUE.equals(value));
			}
		};
	}

	/**
	 * Creates the <code>ButtonModel</code> that keeps the selected state from
	 * the check box in sync with the value in the model and vice versa.
	 * 
	 * @return A new <code>CheckBoxModelAdapter</code>
	 */
	private ButtonModel buildStreamsForBindingCheckBoxAdapter()
	{
		return new CheckBoxModelAdapter(buildStreamsForBindingHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Streams For Binding property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildStreamsForBindingHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), DatabaseLoginAdapter.STREAMS_FOR_BINDING_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				return Boolean.valueOf(login.usesStreamsForBinding());
			}

			protected void setValueOnSubject(Object value)
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				login.setStreamsForBinding(Boolean.TRUE.equals(value));
			}
		};
	}

	/**
	 * Creates the <code>ButtonModel</code> that keeps the selected state from
	 * the check box in sync with the String Binging value in the model and vice
	 * versa.
	 * 
	 * @return A new <code>CheckBoxModelAdapter</code>
	 */
	private ButtonModel buildStringBindingCheckBoxAdapter()
	{
		return new CheckBoxModelAdapter(buildStringBindingHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * String Binding property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildStringBindingHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), DatabaseLoginAdapter.STRING_BINDING_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				return Boolean.valueOf(login.usesStringBinding());
			}

			protected void setValueOnSubject(Object value)
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				login.setStringBinding(Boolean.TRUE.equals(value));
			}
		};
	}
	
	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * String size property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildStringSizeHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), DatabaseLoginAdapter.MAX_BATCH_WRITING_SIZE_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				DatabaseLoginAdapter adapter = (DatabaseLoginAdapter) subject;
				return new Integer(adapter.getMaxBatchWritingSize());
			}

			protected void setValueOnSubject(Object value)
			{
				DatabaseLoginAdapter adapter = (DatabaseLoginAdapter) subject;
				adapter.setMaxBatchWritingSize(((Integer) value).intValue());
			}
		};
	}

	/**
	 * Creates the <code>SpinnerModel</code> that keeps the value from the
	 * text field in sync with the String Size value in the model and vice versa.
	 *
	 * @return A new <code>SpinnerNumberModel</code>
	 */
	private SpinnerNumberModel buildStringSizeSpinnerAdapter()
	{
		return new NumberSpinnerModelAdapter(buildStringSizeHolder(), 0, Integer.MAX_VALUE, 1);
	}

	/**
	 * Creates the <code>DocumentAdapter</code> that keeps the value from the
	 * text field in sync with the Table Qualifier value in the model and vice
	 * versa.
	 *
	 * @return A new <code>DocumentAdapter</code>
	 */
	private Document buildTableQualifierDocumentAdater()
	{
		return new DocumentAdapter(buildTableQualifierHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Table Qualifier property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildTableQualifierHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), DatabaseLoginAdapter.TABLE_QUALIFIER_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				DatabaseLoginAdapter adapter = (DatabaseLoginAdapter) subject;
				return adapter.getTableQualifier();
			}

			protected void setValueOnSubject(Object value)
			{
				DatabaseLoginAdapter adapter = (DatabaseLoginAdapter) subject;
				adapter.setTableQualifier((String) value);
			}
		};
	}

	/**
	 * Creates the <code>ButtonModel</code> that keeps the selected state from
	 * the check box in sync with the Trim Strings value in the model and vice
	 * versa.
	 * 
	 * @return A new <code>CheckBoxModelAdapter</code>
	 */
	private ButtonModel buildTrimStringsCheckBoxAdapter()
	{
		return new CheckBoxModelAdapter(buildTrimStringsHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Trim Strings property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildTrimStringsHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), DatabaseLoginAdapter.TRIM_STRINGS_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				return Boolean.valueOf(login.trimsStrings());
			}

			protected void setValueOnSubject(Object value)
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				login.setTrimStrings(Boolean.TRUE.equals(value));
			}
		};
	}

	/**
	 * Creates the <code>ButtonModel</code> that keeps the selected state from
	 * the check box in sync with the Use Properties value in the model and vice
	 * versa. This activates the Table defined in the Properties tab.
	 * 
	 * @return A new <code>CheckBoxModelAdapter</code>
	 */
	private ButtonModel buildUsePropertiesCheckBoxAdapter()
	{
		return new CheckBoxModelAdapter(buildUsePropertiesHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Use Properties property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildUsePropertiesHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), DatabaseLoginAdapter.USE_PROPERTIES_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				return Boolean.valueOf(login.usesProperties());
			}

			protected void setValueOnSubject(Object value)
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				login.setUseProperties(Boolean.TRUE.equals(value));
			}
		};
	}
}
