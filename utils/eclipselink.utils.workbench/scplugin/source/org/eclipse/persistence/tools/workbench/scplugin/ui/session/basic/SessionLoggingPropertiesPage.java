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
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.basic;

// JDK
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Collections;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.FileChooserPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.GroupBox;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DefaultSessionLogAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.JavaLogAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.LogAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.NoLogAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerLogAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.events.ChangeNotifier;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

// Mapping Workbench

/**
 * This pane shows the information about the {@link SessionAdapter}'s logging
 * property.
 * <p>
 * Here the layout of this pane:
 * <pre>
 * ___________________________________________________________
 * |                                                         |
 * |  o No Logging                                           |
 * |                                                         |
 * |  o Server                                               |
 * |                                                         |
 * |  o Java                                                 |
 * |                                                         |
 * | _o Standard____________________________________________ |
 * | |                ______________________               | |
 * | | Logging Level: |                  |v|               | |<- {@link DefaultSessionLogAdapter#VALID_LOG_LEVEL}
 * | |                ����������������������               | |
 * | | o Console                                           | |
 * | |                                                     | |
 * | | o File                                              | |
 * | |                ______________________ _____________ | |
 * | | Location:      | I                  | | Browse... | | |<- Enabled if File is selected
 * | |                ���������������������� ������������� | |
 * | ������������������������������������������������������� |
 * | _x Options_____________________________________________ |<- Enabled if Java or Standard is selected
 * | |                                                     | |
 * | |  LoggingOptionsPane                                 | |
 * | |                                                     | |
 * | ������������������������������������������������������� |
 * �����������������������������������������������������������</pre>
 *
 * @see SessionAdapter
 * @see DefaultSessionLogAdapter
 * @see NoLogAdapter
 * @see JavaLogAdapter
 * @see ServerLogAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public final class SessionLoggingPropertiesPage extends ScrollablePropertiesPage
{
	/**
	 * This model is used to transform 2 properties into a boolean property.
	 */
	private PseudoLogginOptionsModel pseudoModel;

	/**
	 * One of the choice for Logging Type, used by the <code>RadioButtonModelAdapter</code>.
	 */
	private static final Object LOGGING_TYPE_JAVA_CHOICE = JavaLogAdapter.class;

	/**
	 * One of the choice for Logging Type, used by the <code>RadioButtonModelAdapter</code>.
	 */
	private static final Object LOGGING_TYPE_NO_LOGGING_CHOICE = NoLogAdapter.class;

	/**
	 * One of the choice for Logging Type, used by the <code>RadioButtonModelAdapter</code>.
	 */
	private static final Object LOGGING_TYPE_SERVER_CHOICE = ServerLogAdapter.class;

	/**
	 * One of the choice for Logging Type, used by the <code>RadioButtonModelAdapter</code>.
	 */
	private static final Object LOGGING_TYPE_STANDARD_CHOICE = DefaultSessionLogAdapter.class;

	/**
	 * Creates a new <code>SessionLoggingPropertiesPage</code>.
	 *
	 * @param nodeHolder The holder of {@link SessionAdapter}
	 */
	public SessionLoggingPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder)
	{
		super(nodeHolder, contextHolder);
	}

	/**
	 * Creates the <code>PropertyValueModel</code> that will be the value
	 * holder of the <code>JavaLogAdapter</code> or
	 * <code>DefaultSessionLogAdapter</code>.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildLogAdapterHolder()
	{
		return new PropertyAspectAdapter(PseudoLogginOptionsModel.LOG_OPTIONS_PROPERTY, this.pseudoModel)
		{
			protected Object getValueFromSubject()
			{
				PseudoLogginOptionsModel model = (PseudoLogginOptionsModel) subject;
				return model.getLogOptions();
			}
		};
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to return a
	 * <code>Boolean</code> value based on the Logging Type. Where
	 * {@link #LOGGING_TYPE_STANDARD_CHOICE} or {@link #LOGGING_TYPE_JAVA_CHOICE}
	 * is returned as <code>Boolean.TRUE<code>.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildLoggingOptionsCheckBoxEnablerHolder()
	{
		return new TransformationPropertyValueModel(buildLoggingTypeSelectionHolder())
		{
			protected Object transform(Object value)
			{
				if (value == null)
					return null;

				return Boolean.valueOf(value == LOGGING_TYPE_STANDARD_CHOICE ||
				                       value == LOGGING_TYPE_JAVA_CHOICE);
			}
		};
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to listen to
	 * changes made in the model that will determine if the pane's component need
	 * to be enabled or not.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildLoggingOptionsPaneEnablerHolder()
	{
		return new TransformationPropertyValueModel(buildLogAdapterHolder())
		{
			protected Object transform(Object value)
			{
				return Boolean.valueOf(value != null);
			}
		};
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to listen to
	 * changes made to the type of session's Logging value.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildLoggingTypeHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), SessionAdapter.LOG_CONFIG_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				SessionAdapter session = (SessionAdapter) subject;
				return session.getLog();
			}

			protected void setValueOnSubject(Object value)
			{
				SessionAdapter session = (SessionAdapter) subject;
				LogAdapter log = session.getLog();

				if ((value == LOGGING_TYPE_JAVA_CHOICE) &&
							!(log instanceof JavaLogAdapter))
				{
					session.setJavaLogging();
				}
				else if ((value == LOGGING_TYPE_SERVER_CHOICE) &&
							!(log instanceof ServerLogAdapter))
				{
					session.setServerLogging();
				}
				else if ((value == LOGGING_TYPE_STANDARD_CHOICE) &&
							!(log instanceof DefaultSessionLogAdapter))
				{
					session.setDefaultLogging();
				}
			}
		};
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to listen to
	 * changes made to the type of session's Logging value.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildLoggingTypeSelectionHolder()
	{
		return new TransformationPropertyValueModel(buildLoggingTypeHolder())
		{
			protected Object transform(Object value)
			{
				return (value != null) ? value.getClass() : null;
			}
		};
	}

	/**
	 * Creates the <code>PropertyValueModel</code> that will be the value
	 * holder of the session's log.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildLogHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), SessionAdapter.LOG_CONFIG_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				SessionAdapter session = (SessionAdapter) subject;
				return session.getLog();
			}
		};
	}

	/**
	 * Creates the <code>ButtonModel</code> that keeps the selected state from
	 * the check box in sync with the value in the model and vice versa.
	 * 
	 * @return A new <code>CheckBoxModelAdapter</code>
	 */
	private ButtonModel buildOptionsCheckBoxModelAdapter()
	{
		return new CheckBoxModelAdapter(buildOptionsHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> that will be the value
	 * holder of the Options usage.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildOptionsHolder()
	{
		PropertyAspectAdapter adapter = new PropertyAspectAdapter(buildLogHolder(), LogAdapter.LOG_OPTIONS_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				LogAdapter log = (LogAdapter) subject;

				if (log.optionsIsEnable())
					return Boolean.TRUE; // Return anything but null

				return null;
			}

			protected void setValueOnSubject(Object value)
			{
				LogAdapter log = (LogAdapter) subject;

				if (Boolean.TRUE.equals(value) && !log.optionsIsEnable())
				{
					log.enableOptions();
				}
				else if (Boolean.FALSE.equals(value) && log.optionsIsEnable())
				{
					log.disableOptions();
				}
			}
		};

		return new TransformationPropertyValueModel(adapter)
		{
			protected Object transform(Object value)
			{
				return Boolean.valueOf(value != null);
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
		PropertyValueModel loggingTypeHolder = buildLoggingTypeSelectionHolder();

		// Create the container
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// No Logging
		//JRadioButton noLoggingRadioButton = buildRadioButton
		//(
			//"DATABASE_SESSION_LOGGING_TYPE_NO_LOGGING",
			//new RadioButtonModelAdapter(loggingTypeHolder, LOGGING_TYPE_NO_LOGGING_CHOICE)
		//);
		//addHelpTopicId(noLoggingRadioButton, "session.logging.noLogging");

		// Server Logging
		JRadioButton serverLoggingRadioButton = buildRadioButton
		(
			"DATABASE_SESSION_LOGGING_TYPE_SERVER",
			new RadioButtonModelAdapter(loggingTypeHolder, LOGGING_TYPE_SERVER_CHOICE)
		);
		addHelpTopicId(serverLoggingRadioButton, "session.logging.server");

		// Java Logging
		JRadioButton javaLoggingRadioButton = buildRadioButton
		(
			"DATABASE_SESSION_LOGGING_TYPE_JAVA",
			new RadioButtonModelAdapter(loggingTypeHolder, LOGGING_TYPE_JAVA_CHOICE)
		);
		addHelpTopicId(javaLoggingRadioButton, "session.logging.java");

		// Standard Logging
		JRadioButton standardLoggingRadioButton = buildRadioButton
		(
			"DATABASE_SESSION_LOGGING_TYPE_STANDARD",
			new RadioButtonModelAdapter(loggingTypeHolder, LOGGING_TYPE_STANDARD_CHOICE)
		);
		addHelpTopicId(standardLoggingRadioButton, "session.logging.standard");

		StandardPane standardPane = new StandardPane(buildStandardLogHolder());
		addPaneForAlignment(standardPane);
		new ComponentEnabler(buildStandardPaneEnableStateHolder(), standardPane.getComponents());
		addHelpTopicId(standardPane, "session.logging.standard");

		// Add the widgets to the container
		GroupBox groupBox = new GroupBox
		(
			new AbstractButton[] { //noLoggingRadioButton,
										  serverLoggingRadioButton,
										  javaLoggingRadioButton,
										  standardLoggingRadioButton },
			standardPane
		);

		constraints.gridx      = 0;
		constraints.gridy      = 3;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 0, 0, 0);

		panel.add(groupBox, constraints);

		// Logging Options pane
		JCheckBox optionsCheckBox = buildCheckBox
		(
			"LOGGING_OPTIONS_CHECK_BOX",
			buildOptionsCheckBoxModelAdapter()
		);

		LoggingOptionsPane loggingOptionsPane = new LoggingOptionsPane
		(
			buildLogAdapterHolder(),
			getApplicationContext()
		);

		groupBox = new GroupBox
		(
			optionsCheckBox,
			loggingOptionsPane
		);

		constraints.gridx      = 0;
		constraints.gridy      = 4;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(5, 0, 0, 0);

		panel.add(groupBox, constraints);
		new ComponentEnabler(buildLoggingOptionsCheckBoxEnablerHolder(), optionsCheckBox);
		new ComponentEnabler(buildLoggingOptionsPaneEnablerHolder(), loggingOptionsPane.getComponents());
		addHelpTopicId(groupBox, "session.logging.options");

		addHelpTopicId(this, "session.logging");
		return panel;
	}

	/**
	 * Creates the <code>PropertyChangeListener</code> to keep the parent object
	 * of the {@link PseudoLogginOptionsEnableModel} in sync with the selection
	 * object.
	 *
	 * @return A new <code>PropertyChangeListener</code>
	 */
	private PropertyChangeListener buildSelectionHolderListener ()
	{
		return new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				SessionLoggingPropertiesPage.this.pseudoModel.setParentNode((AbstractNodeModel) e.getNewValue());
			}
		};
	}

	/**
	 * Creates the <code>PropertyValueModel</code> that will be the value
	 * holder of the <code>DefaultSessionLogAdapter</code>.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildStandardLogHolder()
	{
		return new TransformationPropertyValueModel(buildLogHolder())
		{
			protected Object transform(Object value)
			{
				if (value == null)
					return null;

				if (value instanceof DefaultSessionLogAdapter)
					return value;

				return null;
			}
		};
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to return a
	 * <code>Boolean</code> value based on the Logging Type. Where
	 * {@link #LOGGING_TYPE_STANDARD_CHOICE} is returned as <code>Boolean.TRUE<code>.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildStandardPaneEnableStateHolder()
	{
		return new TransformationPropertyValueModel(buildLoggingTypeSelectionHolder())
		{
			protected Object transform(Object value)
			{
				if (value == null)
					return null;

				return Boolean.valueOf(value == LOGGING_TYPE_STANDARD_CHOICE);
			}
		};
	}

	/**
	 * Initializes this <code>SessionLogginPropertiesPage</code>.
	 *
	 * @param nodeHolder The holder of the subject, which is <code>SessionAdapter</code>
	 */
	protected void initialize(PropertyValueModel nodeHolder)
	{
		super.initialize(nodeHolder);

		getSelectionHolder().addPropertyChangeListener(PropertyValueModel.VALUE, buildSelectionHolderListener());

		this.pseudoModel = new PseudoLogginOptionsModel();
		this.pseudoModel.setParentNode((AbstractNodeModel) selection());
	}

	/**
	 * A pseudo model merging two properties into a single property. Basically,
	 * the enable state of the Options pane is dedictated by the
	 * logging type and if the options are enabled or not.
	 */
	private class PseudoLogginOptionsModel extends AbstractNodeModel
	{
		private ChangeNotifier changeNotifier;
		private PropertyChangeListener listener;
		private LogAdapter logOptions;
		private Node parentNode;
		private Validator validator;

		public static final String LOG_OPTIONS_PROPERTY = "logOptions";

		private PropertyChangeListener buildPropertyChangeListener()
		{
			return new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent e)
				{
					if (SessionAdapter.LOG_CONFIG_PROPERTY.equals(e.getPropertyName()))
					{
						LogAdapter oldAdapter = (LogAdapter) e.getOldValue();
						oldAdapter.removePropertyChangeListener(LogAdapter.LOG_OPTIONS_PROPERTY, this);

						LogAdapter newAdapter = (LogAdapter) e.getNewValue();
						newAdapter.addPropertyChangeListener(LogAdapter.LOG_OPTIONS_PROPERTY, this);
					}

					updateLogOptions();
				}
			};
		}

		protected void checkParent(Node parent)
		{
			// The parent is set/unset dynamically
		}

		private void disengageListeners()
		{
			SessionAdapter session = (SessionAdapter) this.parentNode;
			session.removePropertyChangeListener(SessionAdapter.LOG_CONFIG_PROPERTY, this.listener);
			session.getLog().removePropertyChangeListener(LogAdapter.LOG_OPTIONS_PROPERTY, this.listener);
		}

		public String displayString()
		{
			return null;
		}

		private void engageListeners()
		{
			SessionAdapter session = (SessionAdapter) this.parentNode;
			session.addPropertyChangeListener(SessionAdapter.LOG_CONFIG_PROPERTY, this.listener);
			session.getLog().addPropertyChangeListener(LogAdapter.LOG_OPTIONS_PROPERTY, this.listener);
		}

		public ChangeNotifier getChangeNotifier()
		{
			return this.changeNotifier;
		}

		public LogAdapter getLogOptions()
		{
			return this.logOptions;
		}

		public Validator getValidator()
		{
			return this.validator;
		}

		protected void initialize()
		{
			super.initialize();
			this.listener = buildPropertyChangeListener();
		}

		private void setLogOptions(LogAdapter logOptions)
		{
			LogAdapter oldLogOptions = getLogOptions();
			this.logOptions = logOptions;
			firePropertyChanged(LOG_OPTIONS_PROPERTY, oldLogOptions, logOptions);
		}

		public void setParentNode(Node parentNode)
		{
			if (this.parentNode != null)
			{
				disengageListeners();
				setLogOptions(null);
				this.validator = null;
				this.changeNotifier = null;
			}

			this.parentNode = parentNode;

			if (parentNode != null)
			{
				this.validator = parentNode.getValidator();
				this.changeNotifier = parentNode.getChangeNotifier();
				engageListeners();
				updateLogOptions();
			}
		}

		private void updateLogOptions()
		{
			SessionAdapter session = (SessionAdapter) this.parentNode;
			LogAdapter log = session.getLog();

			if (!log.optionsIsEnable())
				log = null;

			setLogOptions(log);
		}
	}

	/**
	 * This pane shows the information for {@link DefaultSessionLogAdapter}.
	 * <p>
	 * Here the layout of this pane:
	 * <pre>
	 * ________________________________________________________
	 * |                _______________________               |
	 * | Logging Level: |                   |v|               |<- {@link DefaultSessionLogAdapter#VALID_LOG_LEVEL}
	 * |                �����������������������               |
	 * |                _______________________               |   ___________
	 * | Destination:   | File              |v|               |<- | File    |
	 * |                �����������������������               |   | Console |
	 * |                _______________________ _____________ |   �����������
	 * | Log Location:  | I                   | | Browse... | |<- Shows the File chooser,
	 * |                ����������������������� ������������� |   Disabled if Destination is Console
	 * ��������������������������������������������������������</pre>
	 */
	private class StandardPane extends AbstractSubjectPanel
	{
		/**
		 * Creates a new <code>StandardPane</code>.
		 *
		 * @param subjectHolder The holder of {@link DefaultSessionLogAdapter}
		 */
		private StandardPane(PropertyValueModel subjectHolder)
		{
			super(subjectHolder, SessionLoggingPropertiesPage.this.getApplicationContext());
			addHelpTopicId(this, "session.logging.standard");
		}

		/**
		 * Creates the <code>PropertyValueModel</code> responsible to handle the
		 * File Name property.
		 *
		 * @return A new <code>PropertyValueModel</code>
		 */
		private PropertyValueModel buildDestinationSelectionHolder()
		{
			return new PropertyAspectAdapter(getSubjectHolder(), DefaultSessionLogAdapter.FILE_NAME_PROPERTY)
			{
				protected Object getValueFromSubject()
				{
					DefaultSessionLogAdapter log = (DefaultSessionLogAdapter) subject;
					String fileName = log.getFileName();

					return Boolean.valueOf(DefaultSessionLogAdapter.DEFAULT_LOG_FILE.equals(fileName));
				}

				protected void setValueOnSubject(Object value)
				{
					DefaultSessionLogAdapter log = (DefaultSessionLogAdapter) subject;
					String currentFileName = log.getFileName();

					if (Boolean.TRUE.equals(value) &&
					    !DefaultSessionLogAdapter.DEFAULT_LOG_FILE.equals(currentFileName))
					{
						log.setFileName(DefaultSessionLogAdapter.DEFAULT_LOG_FILE);
					}
					else if (Boolean.FALSE.equals(value) &&
					         DefaultSessionLogAdapter.DEFAULT_LOG_FILE.equals(currentFileName))
					{
						log.setFileName("");
					}
				}
			};
		}

		/**
		 * Creates a new <code>ComponentEnabler</code> that will keep the enable
		 * state of the given button in sync with the underlying model property;
		 * which is enable when the Destination is File.
		 *
		 * @param component The component to have its enable state up to date, as
		 * long as its children
		 * with the model's property
		 */
		private void buildLocationWidgetsEnabler(JComponent components)
		{
			new ComponentEnabler(buildLocationWidgetsEnablerHolder(),
										Collections.singleton(components),
										true); // This will enable the components when File Name is null
		}

		/**
		 * Creates the <code>ComponentEnabler</code>'s boolean holder.
		 *
		 * @return A new <code>PropertyValueModel</code>
		 */
		private PropertyValueModel buildLocationWidgetsEnablerHolder()
		{
			return new TransformationPropertyValueModel(buildLogFileHolder())
			{
				protected Object transform(Object value)
				{
					if (value == null)
						return null;

					return Boolean.valueOf(! (DefaultSessionLogAdapter.DEFAULT_LOG_FILE.equals(value)));
				}
			};
		}

		/**
		 * Creates the <code>PropertyValueModel</code> responsible to handle the
		 * Log File Location property.
		 *
		 * @return A new <code>PropertyValueModel</code>
		 */
		private PropertyValueModel buildLogFileHolder()
		{
			return new PropertyAspectAdapter(getSubjectHolder(), DefaultSessionLogAdapter.FILE_NAME_PROPERTY)
			{
				protected Object getValueFromSubject()
				{
					DefaultSessionLogAdapter log = (DefaultSessionLogAdapter) subject;
					String fileName = log.getFileName();
					//this needs to be localized since its being displayed to the user
					if (DefaultSessionLogAdapter.DEFAULT_LOG_FILE.equals(fileName)) {
						fileName = resourceRepository().getString("SESSIONS_LOGGING_PROPERTIES_PAGE_DEFAULT_LOG_FILENAME");
					}
					return fileName;
				}

				protected void setValueOnSubject(Object value)
				{
					DefaultSessionLogAdapter log = (DefaultSessionLogAdapter) subject;
					String fileName = (String) value;

					if (fileName != null)
						fileName = fileName.trim();

					log.setFileName(fileName);

					if (!StringTools.stringIsEmpty(fileName))
					{
						File fileLocation = new File(fileName);
						fileLocation = fileLocation.getParentFile();

						if (fileLocation != null)
							preferences().get("location", fileLocation.getPath());
					}
				}
			};
		}

		/**
		 * Creates the <code>ComboBoxModelAdapter</code> that keeps the selected
		 * item from the combo box in sync with the Log Level value in the model and
		 * vice versa.
		 * 
		 * @param subjectHolder The holder of {@link DefaultSessionLogAdapter}
		 * @return A new <code>ComboBoxModel</code>
		 */
		private ComboBoxModel buildLogLevelComboAdapter()
		{
			return new ComboBoxModelAdapter(buildLogLevelListHolder(),
													  buildLogLevelSelectionHolder());
		}

		/**
		 * Creates the <code>ListValueModel</code> containing all the items to be
		 * shown in the Log Level combo box.
		 *
		 * @return A new <code>ListValueModel</code>
		 */
		private ListValueModel buildLogLevelListHolder()
		{
			return new SimpleListValueModel
			(
				CollectionTools.list(DefaultSessionLogAdapter.VALID_LOG_LEVEL)
			);
		}

		/**
		 * Creates the <code>PropertyValueModel</code> responsible to handle the
		 * Log Level property.
		 *
		 * @return A new <code>PropertyValueModel</code>
		 */
		private PropertyValueModel buildLogLevelSelectionHolder()
		{
			return new PropertyAspectAdapter(getSubjectHolder(), DefaultSessionLogAdapter.LOG_LEVEL_PROPERTY)
			{
				protected Object getValueFromSubject()
				{
					DefaultSessionLogAdapter log = (DefaultSessionLogAdapter) subject;
					return log.getLogLevel();
				}

				protected void setValueOnSubject(Object value)
				{
					DefaultSessionLogAdapter log = (DefaultSessionLogAdapter) subject;
					log.setLogLevel((String) value);
				}
			};
		}

		/**
		 * Initializes the layout of this pane.
		 */
		protected void initializeLayout()
		{
			GridBagConstraints constraints = new GridBagConstraints();

			// Log Level widgets
			JComboBox comboBox = new JComboBox(buildLogLevelComboAdapter());
			comboBox.setPrototypeDisplayValue("warningmmm");

			JComponent logLevelWidgets = buildLabeledComponent
			(
				"DATABASE_SESSION_LOGGING_LEVEL_COMBO_BOX",
				comboBox
			);

			constraints.gridx      = 0;
			constraints.gridy      = 0;
			constraints.gridwidth  = 1;
			constraints.gridheight = 1;
			constraints.weightx    = 0;
			constraints.weighty    = 0;
			constraints.fill       = GridBagConstraints.NONE;
			constraints.anchor     = GridBagConstraints.LINE_START;
			constraints.insets     = new Insets(0, 0, 0, 0);

			add(logLevelWidgets, constraints);

			// Console radio button
			JRadioButton consoleRadioButton = buildRadioButton
			(
				"DATABASE_SESSION_DESTINATION_CONSOLE",
				new RadioButtonModelAdapter(buildDestinationSelectionHolder(), Boolean.TRUE)
			);

			constraints.gridx      = 0;
			constraints.gridy      = 1;
			constraints.gridwidth  = 1;
			constraints.gridheight = 1;
			constraints.weightx    = 0;
			constraints.weighty    = 0;
			constraints.fill       = GridBagConstraints.NONE;
			constraints.anchor     = GridBagConstraints.LINE_START;
			constraints.insets     = new Insets(8, 0, 0, 0);

			add(consoleRadioButton, constraints);

			// File radio button
			JRadioButton fileRadioButton = buildRadioButton
			(
				"DATABASE_SESSION_DESTINATION_FILE",
				new RadioButtonModelAdapter(buildDestinationSelectionHolder(), Boolean.FALSE)
			);

			constraints.gridx      = 0;
			constraints.gridy      = 2;
			constraints.gridwidth  = 1;
			constraints.gridheight = 1;
			constraints.weightx    = 0;
			constraints.weighty    = 0;
			constraints.fill       = GridBagConstraints.NONE;
			constraints.anchor     = GridBagConstraints.LINE_START;
			constraints.insets     = new Insets(0, 0, 0, 0);

			add(fileRadioButton, constraints);

			// Log Location widgets
			FileChooserPanel logFileChooser = new CustomizedFileChooserPanel();

			constraints.gridx      = 0;
			constraints.gridy      = 3;
			constraints.gridwidth  = 1;
			constraints.gridheight = 1;
			constraints.weightx    = 1;
			constraints.weighty    = 0;
			constraints.fill       = GridBagConstraints.HORIZONTAL;
			constraints.anchor     = GridBagConstraints.CENTER;
			constraints.insets     = new Insets(5, 0, 0, 0);

			add(logFileChooser, constraints);
			addPaneForAlignment(logFileChooser);
			buildLocationWidgetsEnabler(logFileChooser);
		}

		/**
		 * This customized <code>FileChooserPanel</code> changes the key to
		 * retrieve the localized string for the Browse button and make sure the
		 * enabled state is accurate.
		 */
		private class CustomizedFileChooserPanel extends FileChooserPanel
		{
			private CustomizedFileChooserPanel()
			{
				super(StandardPane.this.getApplicationContext(),
						buildLogFileHolder(),
						"DATABASE_SESSION_LOG_FILE_LOCATION_FIELD",
						"SESSION_LOGGING_BROWSE_BUTTON",
						JFileChooser.FILES_ONLY);
			}

			protected File getFileChooserDefaultDirectory()
			{
				DefaultSessionLogAdapter log = (DefaultSessionLogAdapter) subject();
				String fileName = log.getFileName();

				if (!StringTools.stringIsEmpty(fileName))
				{
					File fileLocation = new File(fileName);

					if (fileLocation.isFile())
						fileLocation = fileLocation.getParentFile();

					return fileLocation;
				}
				SessionAdapter session = (SessionAdapter) log.getParent();
				TopLinkSessionsAdapter topLinkSessions = (TopLinkSessionsAdapter) session.getParent();
				File saveDirectory = topLinkSessions.getSaveDirectory();

				// This happens when the sessions.xml is an untitled file
				if (saveDirectory == null)
					saveDirectory = FileTools.userHomeDirectory();

				return new File(preferences().get("location", saveDirectory.getPath()));
			}

			public void setEnabled(boolean enabled)
			{
				DefaultSessionLogAdapter log = (DefaultSessionLogAdapter) subject();

				if (enabled)
				{
					if (log != null)
						enabled &= !(DefaultSessionLogAdapter.DEFAULT_LOG_FILE.equals(log.getFileName()));
					else
						enabled = false;
				}

				super.setEnabled(enabled);
			}
		}
	}
}
