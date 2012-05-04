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
package org.eclipse.persistence.tools.workbench.scplugin.ui.session;

// JDK
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ComponentBuilder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TabbedPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.basic.SessionConnectionPolicyPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.basic.SessionLoggingPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.basic.SessionMultipleProjectsPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.basic.SessionOptionsPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.basic.SessionProjectPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.basic.SessionServerPlatformPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.clustering.SessionClusteringPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.InfoPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.PropertiesPageContainer;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.SessionDisplayableTranslatorAdapter;
import org.eclipse.persistence.tools.workbench.uitools.DisplayableAdapter;
import org.eclipse.persistence.tools.workbench.uitools.SwitcherPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.Transformer;

// Mapping Wokbench

/**
 * This page shows the Session's information.
 *
 * @version 10.1.3
 * @author Tran Le
 * @author Pascal Filion
 */
abstract class SessionTabbedPropertiesPage extends TabbedPropertiesPage
{
	SessionTabbedPropertiesPage(WorkbenchContext context)
	{
		super(context);
	}

	protected Component buildClusteringPropertiesPage()
	{
		return buildPropertiesPage(SessionClusteringPropertiesPage.class,
											"SESSION_CLUSTERING_TAB_MESSAGE");
	}

	protected String buildClusteringPropertiesPageTitle()
	{
		return "SESSION_CLUSTERING_TAB_TITLE";
	}

	protected AbstractPropertiesPage buildConnectionPolicyPropertiesPage()
	{
		return new SessionConnectionPolicyPropertiesPage(buildSessionHolderForConnectionPolicyPage(), getWorkbenchContextHolder());
	}

	protected String buildConnectionPolicyPropertiesPageTitle()
	{
		return "SESSION_CONNECTION_POLICY_TITLE";
	}

	protected PropertyValueModel buildConnectionPolicyVisibleHolder()
	{
		return new TransformationPropertyValueModel(getSelectionHolder())
		{
			protected Object transform(Object value)
			{
				if (value == null)
					return Boolean.FALSE;

				return Boolean.valueOf(value instanceof ServerSessionAdapter);
			}
		};
	}

	protected DisplayableAdapter buildDisplayableAdapter()
	{
		return new SessionDisplayableTranslatorAdapter(resourceRepository());
	}

	protected AbstractPropertiesPage buildGeneralPropertiesPage()
	{
		return new GeneralTabbedPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
	}

	protected String buildGeneralPropertiesPageTitle()
	{
		return "SESSION_GENERAL_TAB_TITLE";
	}

	protected Component buildLoggingPropertiesPage()
	{
		return buildPropertiesPage(SessionLoggingPropertiesPage.class,
											"SESSION_LOGGING_TAB_MESSAGE");
	}

	protected String buildLoggingPropertiesPageTitle()
	{
		return "SESSION_LOGGING_TAB_TITLE";
	}

	protected PropertyValueModel buildMultipleProjectsHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), DatabaseSessionAdapter.USE_ADDITIONAL_PROJECTS_COLLECTION)
		{
			protected Object buildValue()
			{
				if (subject == null)
					return Boolean.FALSE;

				return getValueFromSubject();
			}

			public Object getValueFromSubject()
			{
				DatabaseSessionAdapter session = (DatabaseSessionAdapter) subject;
				return Boolean.valueOf(session.usesAdditionalProjects());
			}
		};
	}

	protected ComponentBuilder buildMultipleProjectsPageBuilder()
	{
		return new ComponentBuilder() 
		{
			private SessionMultipleProjectsPropertiesPage page;
			
			public Component buildComponent(PropertyValueModel nodeHolder)
			{
				if (page == null)
					page = new SessionMultipleProjectsPropertiesPage(nodeHolder, getWorkbenchContextHolder());

				return page;
			}
		};
	}

	protected String buildMultipleProjectsPropertiesPageTitle()
	{
		return "SESSION_MULTIPLE_PROJECTS_TAB_TITLE";
	}

	protected Component buildOptionsPropertiesPage()
	{
		return buildPropertiesPage(SessionOptionsPropertiesPage.class,
											"SESSION_OPTIONS_TAB_MESSAGE");
	}

	protected String buildOptionsPropertiesPageTitle()
	{
		return "SESSION_OPTIONS_TAB_TITLE";
	}

	/**
	 * Creates a new properties that will show either the page defined by the
	 * given class or will simply show a message.
	 * 
	 * @param propertiesPageClass The class of the properties page that will be
	 * used for instantiation when required
	 * @param messageKey The key used to retrieve the message to be displayed
	 * @return A new page
	 */
	protected AbstractPropertiesPage buildPropertiesPage(Class propertiesPageClass,
																		  String messageKey)
	{
		ComponentHolder componentHolder = new ComponentHolder(propertiesPageClass);
		return buildPropertiesPage(componentHolder, buildVisibleHolder(), messageKey);
	}

	private AbstractPropertiesPage buildPropertiesPage(ValueModel componentHolder,
																		PropertyValueModel visibleHolder,
																		String messageKey)
	{
		SwitcherPanel switcherPanel = buildSwitcherPanel(componentHolder, visibleHolder, messageKey);
		return new PropertiesPageContainer(getNodeHolder(), getWorkbenchContextHolder(), switcherPanel);
	}

	private PropertyValueModel buildSessionHolderForConnectionPolicyPage()
	{
		return new TransformationPropertyValueModel(getNodeHolder())
		{
			protected Object transform(Object value)
			{
				if (value == null)
					return null;

				SessionNode node = (SessionNode) value;

				if (node.getValue() instanceof ServerSessionAdapter)
					return value;

				return null;
			}
		};
	}

	/**
	 * Creates a new <code>SwitcherPanel</code> that will take care to update
	 * its content by either an info page or by the actual properties page upon
	 * the value returned by the visible holder.
	 * 
	 * @param componentHolder Holds onto the actual properties page
	 * @param visibleStateHolder The holder of the boolean value that will be used
	 * to determine which component needs to be displayed
	 * @param messageKey The key used to retrieve the message to be displayed
	 * @return A new <code>SwitcherPanel</code>
	 */
	private SwitcherPanel buildSwitcherPanel(ValueModel componentHolder,
														  PropertyValueModel visibleStateHolder,
														  String messageKey)
	{
		Transformer transformer = new TabContentTransformer(componentHolder, messageKey);
		return new SwitcherPanel(visibleStateHolder, transformer);
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * managed by broker property.
	 * 
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildVisibleHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), SessionAdapter.MANAGED_BY_BROKER)
		{
			protected Object getValueFromSubject()
			{
				return Boolean.valueOf(((SessionAdapter) subject).isManaged());
			}
		};
	}

	/**
	 * This <code>ValueModel</code> holds onto the actual properties page.
	 */
	private class ComponentHolder extends SimplePropertyValueModel
	{
		private Component page;

		private ComponentHolder(Class pageClass)
		{
			super(pageClass);
		}

		private Component buildPage()
		{
			Class pageClass = (Class) super.getValue();
			Class[] parameterTypes = new Class[] { PropertyValueModel.class, WorkbenchContextHolder.class };
			Object[] parameters = new Object[] { getNodeHolder(), getWorkbenchContextHolder() };
			return (Component) ClassTools.newInstance(pageClass, parameterTypes, parameters);
		}

		public Object getValue()
		{
			if (this.page == null)
				this.page = buildPage();

			return this.page;
		}
	}

	private class GeneralTabbedPropertiesPage extends TabbedPropertiesPage
	{
		private GeneralTabbedPropertiesPage(PropertyValueModel nodeHolder,
														WorkbenchContextHolder contextHolder)
		{
			super(nodeHolder, contextHolder);
		}

		private AbstractPropertiesPage buildProjectPropertiesPage()
		{
			return new SessionProjectPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
		}

		private String buildProjectPropertiesPageTitle()
		{
			return "SESSION_PROJECT_TAB_TITLE";
		}

		private AbstractPropertiesPage buildServerPlatformPropertiesPage()
		{
			return buildPropertiesPage(SessionServerPlatformPropertiesPage.class, "SESSION_SERVER_PLATFORM_TAB_MESSAGE");
		}

		private String buildServerPlatformPropertiesPageTitle()
		{
			return "SESSION_SERVER_PLATFORM_TAB_TITLE";
		}

		protected JTabbedPane buildTabbedPane()
		{
			JTabbedPane tabbedPane = super.buildTabbedPane();
			tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			return tabbedPane;
		}

		protected Component buildTitlePanel()
		{
			return new JComponent() {};
		}

		protected void initializeTabs()
		{
			addTab(buildProjectPropertiesPage(), 0, buildProjectPropertiesPageTitle());
			addTab(buildMultipleProjectsHolder(), 1, buildMultipleProjectsPageBuilder(), buildMultipleProjectsPropertiesPageTitle());
			addTab(buildServerPlatformPropertiesPage(), 2, buildServerPlatformPropertiesPageTitle());
		}
	}

	/**
	 * This <code>Transformer</code> is responsible to show the actual
	 * properties page when the value to be transform is <code>Boolean.TRUE</code>,
	 * when the value is <code>Boolean.FALSE</code>, which means the edited
	 * {@link SessionAdapter}is not managed by a {@link SessionBrokerAdapter},
	 * which in this case, an {@link InfoPropertiesPage}will be shown.
	 */
	private class TabContentTransformer implements Transformer
	{
		/**
		 * Holds onto the actual properties page.
		 */
		private ValueModel componentHolder;

		/**
		 * Caches the properties showing a message.
		 */
		private InfoPropertiesPage infoPage;

		/**
		 * The key used to retrieve the message to be displayed.
		 */
		private final String messageKey;

		/**
		 * Creates a new <code>TabContentTransformer</code>.
		 * 
		 * @param componentHolder Holds onto the actual properties page
		 * @param messageKey The key used to retrieve the message to be displayed
		 */
		TabContentTransformer(ValueModel componentHolder, String messageKey)
		{
			super();
			this.messageKey = messageKey;
			this.componentHolder = componentHolder;
		}

		/**
		 * Returns, and creates if not initialized, an page showing a message.
		 * 
		 * @return A new {@link InfoPropertiesPage}
		 */
		private InfoPropertiesPage getInfoPage()
		{
			if (this.infoPage == null)
				this.infoPage = new InfoPropertiesPage(getNodeHolder(), getWorkbenchContextHolder(), this.messageKey);

			return this.infoPage;
		}

		/**
		 * Transforms the given <code>Boolean</code> value into a <code>Component</code>.
		 * 
		 * @param object The <code>Boolean</code> object to be converted into a
		 * <code>Component</code>
		 * @return The component associated with the given <code>Boolean</code>
		 * value
		 */
		public Object transform(Object object)
		{
			if (Boolean.FALSE.equals(object))
				return this.componentHolder.getValue();

			if (Boolean.TRUE.equals(object))
				return getInfoPage();

			return null;
		}
	}
}
