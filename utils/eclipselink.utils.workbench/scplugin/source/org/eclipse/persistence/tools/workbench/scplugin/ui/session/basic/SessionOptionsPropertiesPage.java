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
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.basic;

// JDK
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collections;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.RegexpDocument;
import org.eclipse.persistence.tools.workbench.framework.uitools.TriStateBooleanCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SCAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.ClassChooserTools;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ReadOnlyCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.CellRendererAdapter;
import org.eclipse.persistence.tools.workbench.utility.TriStateBoolean;

// Mapping Workbench

/**
 * This pane shows the information related to the Session Options....
 * <p>
 * Here the layout:
 * <pre>
 * ______________________________________________________________________
 * |                                  __________________                |  ___________________
 * | Profiler:                        |              |v|                |<-| No Profiler     |
 * |                                  ------------------                |  | DMS             |
 * |                                  __________________ ______________ |  | Standard        |
 * | Exception Handler:               | I              | | Browse...  | |  -------------------
 * |                                  ------------------ -------------- |
 * |                                  __________________ ______________ |
 * | Session Customizer Class:        | I              | | Browse...  | |
 * |                                  ------------------ -------------- |
 * | -Event Listeners-------------------------------------------------- |
 * | |                                                                | |
 * | | {@link org.eclipse.persistence.tools.workbench.scplugin.ui.session.EventListenersPane EventListenersPane}                                             | |
 * | |                                                                | |
 * | ------------------------------------------------------------------ |
 * ----------------------------------------------------------------------</pre>
 *
 * @see SessionAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public final class SessionOptionsPropertiesPage extends ScrollablePropertiesPage
{
	/**
	 * Creates a new <code>SessionOptionsPropertiesPage</code>.
	 *
	 * @param nodeHolder The holder of {@link SessionNode}
	 */
	public SessionOptionsPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder)
	{
		super(nodeHolder, contextHolder);
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to return the
	 * Class Repository.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private ValueModel buildClassRepositoryHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder())
		{
			protected Object getValueFromSubject()
			{
				SCAdapter adapter = (SCAdapter) subject;
				return adapter.getClassRepository();
			}
		};
	}

	/**
	 * Creates a Browse button that will take care to show the class chooser.
	 *
	 * @return A new <code>JButton</code>
	 */
	private JButton buildExceptionHandlerBrowseButton()
	{
		return ClassChooserTools.buildBrowseButton
		(
			getWorkbenchContextHolder(),
			"OPTIONS_EXCEPTION_HANDLER_BROWSE_BUTTON",
			buildClassRepositoryHolder(),
			buildExceptionHandlerHolder()
		);
	}

	/**
	 * Creates the <code>DocumentAdapter</code> that keeps the value from the
	 * text field in sync with the Exception Handler value in the model and vice
	 * versa.
	 *
	 * @return A new <code>DocumentAdapter</code>
	 */
	private Document buildExceptionHandlerDocumentAdapter()
	{
		return new DocumentAdapter(buildExceptionHandlerHolder(),
											new RegexpDocument(RegexpDocument.RE_FULLY_QUALIFIED_CLASS_NAME));
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Exception Handler property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildExceptionHandlerHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), SessionAdapter.EXCEPTION_HANDLER_CLASS_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				SessionAdapter adapter = (SessionAdapter) subject;
				return adapter.getExceptionHandlerClass();
			}

			protected void setValueOnSubject(Object value)
			{
				SessionAdapter adapter = (SessionAdapter) subject;
				adapter.setExceptionHandlerClass((String) value);
			}
		};
	}

	/**
	 * Initializes the layout of this page.
	 *
	 * @return The component where all the widgets have been installed.
	 */
	protected Component buildPage()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		Insets offset = BorderFactory.createTitledBorder("m").getBorderInsets(this);
		offset.right += 5; offset.left += 5;

		// Create the container of all the widgets
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Profiler widgets
		JComponent profilerWidgets = buildLabeledComboBox
		(
			"OPTIONS_PROFILER_COMBO_BOX",
			buildProfilerComboBoxAdapter(),
			new AdaptableListCellRenderer(buildProfilerLabelDecorator())
		);

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, offset.left, 0, offset.right);

		panel.add(profilerWidgets, constraints);
		addHelpTopicId(profilerWidgets, "session.options.profiler");

		// Exception Handler widgets
		JComponent exceptionHandlerWidgets = buildLabeledTextField
		(
			"OPTIONS_EXCEPTION_HANDLER_FIELD",
			buildExceptionHandlerDocumentAdapter(),
			buildExceptionHandlerBrowseButton()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, offset.left, 0, offset.right);

		panel.add(exceptionHandlerWidgets, constraints);
		addHelpTopicId(exceptionHandlerWidgets, "session.options.exceptionHandler");

		// Session Customizer Class widgets
		JComponent sessionCustomizerClassWidgets = buildLabeledTextField
		(
			"OPTIONS_SESSION_CUSTOMIZER_CLASS_FIELD",
			buildSessionCustomizerClassDocumentAdapter(),
			buildSessionCustomizerBrowseButton()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 2;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, offset.left, 0, offset.right);

		panel.add(sessionCustomizerClassWidgets, constraints);
		addHelpTopicId(sessionCustomizerClassWidgets, "session.options.sessionCustomizerClass");

		// Event Listeners sub-pane
		EventListenersPane eventListenersPane = new EventListenersPane(getSelectionHolder(), getWorkbenchContextHolder());

		constraints.gridx       = 0;
		constraints.gridy       = 3;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 1;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.PAGE_START;
		constraints.insets      = new Insets(5, 0, 0, 0);

		panel.add(eventListenersPane, constraints);
		addPaneForAlignment(eventListenersPane);

		addHelpTopicId(panel, "session.options");
		return panel;
	}

	/**
	 * Creates the <code>CollectionValueModel</code> containing the actual items
	 * to be shown in the Profiler combo box.
	 *
	 * @return {@link ReadOnlyCollectionValueModel}
	 */
	private CollectionValueModel buildProfilerCollectionHolder()
	{
		Vector booleanValues = new Vector();
		booleanValues.add(TriStateBoolean.UNDEFINED);

		if (isTopLinkProfilerFirst())
		{
			booleanValues.add(TriStateBoolean.TRUE);
			booleanValues.add(TriStateBoolean.FALSE);
		}
		else
		{
			booleanValues.add(TriStateBoolean.FALSE);
			booleanValues.add(TriStateBoolean.TRUE);
		}

		return new ReadOnlyCollectionValueModel(booleanValues);
	}

	/**
	 * Creates the <code>ComboBoxModelAdapter</code> that keeps the selected item
	 * in the combo box in sync with the value in the model and vice versa.
	 *
	 * @return The model 
	 */
	private ComboBoxModel buildProfilerComboBoxAdapter()
	{
		return new ComboBoxModelAdapter(buildProfilerCollectionHolder(),
												  buildProfilerSelectionHolder());
	}

	/**
	 * Creates the decorator responsible to format the <code>Boolean</code>
	 * values in the Profiler combo box.
	 * 
	 * @return {@link SessionOptionsPropertiesPage.BooleanLabelDecorator}
	 */
	private CellRendererAdapter buildProfilerLabelDecorator()
	{
        return new TriStateBooleanCellRendererAdapter(resourceRepository()) {
            protected String undefinedResourceKey() {
                return "OPTIONS_PROFILER_NO_PROFILER_CHOICE";
            }
            
            protected String trueResourceKey() {
                return "OPTIONS_PROFILER_TOPLINK_CHOICE";
            }
            
            protected String falseResourceKey() {
                return "OPTIONS_PROFILER_DMS_CHOICE";
            }
        };
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to listen to
	 * changes made to the type of clustering to be used, which is either Remote
	 * Command Manager or Cache Synchronization
	 *
	 * @return {@link PropertyAspectAdapter}
	 */
	private PropertyValueModel buildProfilerSelectionHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), SessionAdapter.PROFILER_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				SessionAdapter adapter = (SessionAdapter) subject;
				String profiler = adapter.getProfiler();

				if (profiler == null)
					return TriStateBoolean.UNDEFINED;

				boolean useTopLinkProfiler = SessionAdapter.LOG_PROFILER_TOPLINK.equals(profiler);
				return TriStateBoolean.valueOf(useTopLinkProfiler);
			}

			protected void setValueOnSubject(Object value)
			{
				SessionAdapter session = (SessionAdapter) subject;

				if (TriStateBoolean.UNDEFINED.equals(value))
				{
					session.setProfiler(null);
				}
				else if (TriStateBoolean.TRUE.equals(value))
				{
					session.setProfiler(SessionAdapter.LOG_PROFILER_TOPLINK);
				}
				else
				{
					session.setProfiler(SessionAdapter.LOG_PROFILER_DMS);
				}
			}
		};
	}

	/**
	 * Creates a Browse button that will take care to show the class chooser.
	 *
	 * @return A new <code>JButton</code>
	 */
	private JButton buildSessionCustomizerBrowseButton()
	{
		return ClassChooserTools.buildBrowseButton
		(
			getWorkbenchContextHolder(),
			"OPTIONS_SESSION_CUSTOMIZER_CLASS_BROWSE_BUTTON",
			buildClassRepositoryHolder(),
			buildSessionCustomizerClassHolder()
		);
	}

	/**
	 * Creates the <code>DocumentAdapter</code> that keeps the value from the
	 * text field in sync with the Session Customizer Class value in the model
	 * and vice versa.
	 *
	 * @return A new <code>DocumentAdapter</code>
	 */
	private Document buildSessionCustomizerClassDocumentAdapter()
	{
		return new DocumentAdapter(buildSessionCustomizerClassHolder(),
											new RegexpDocument(RegexpDocument.RE_FULLY_QUALIFIED_CLASS_NAME));
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Session Customizer Class property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildSessionCustomizerClassHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), SessionAdapter.SESSION_CUSTOMIZER_CLASS_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				SessionAdapter adapter = (SessionAdapter) subject;
				return adapter.getSessionCustomizerClass();
			}

			protected void setValueOnSubject(Object value)
			{
				SessionAdapter adapter = (SessionAdapter) subject;
				adapter.setSessionCustomizerClass((String) value);
			}
		};
	}

	/**
	 * Determines wheter in the profiler choices, Standard (TopLink) comes before
	 * DMS or not.
	 *
	 * @return <code>true<code> if Standard comes before DMS depending on the
	 * locale
	 */
	private boolean isTopLinkProfilerFirst()
	{
		Vector orderedList = new Vector();
		orderedList.add(resourceRepository().getString("OPTIONS_PROFILER_TOPLINK_CHOICE"));
		orderedList.add(resourceRepository().getString("OPTIONS_PROFILER_DMS_CHOICE"));
		Collections.sort(orderedList);

		return orderedList.firstElement().equals(resourceRepository().getString("OPTIONS_PROFILER_TOPLINK_CHOICE"));
	}
}
