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
package org.eclipse.persistence.tools.workbench.scplugin.ui.tools;

// JDK
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassChooserDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassDescriptionRepository;
import org.eclipse.persistence.tools.workbench.framework.uitools.CursorConstants;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;

// Mapping Workbench

/**
 * This library helps to create a button that shows the Class chooser dialog.
 *
 * @version 10.0.3
 * @author Pascal Filion
 */
public final class ClassChooserTools
{
	/**
	 * Creates a new button that when clicked will show the Class chooser dialog
	 * and update the given selection holder with the selected value.
	 *
	 * @param context The context ...
	 * @param key The key used to retrieve the localized text from the given
	 * context's <code>ResourceRepository</code>
	 * @param classRepositoryHolder The holder of the
	 * {@link org.eclipse.persistence.tools.workbench.scplugin.model.meta.ClassRepository} used
	 * to retrieve the list of classes
	 * @param selectionHolder The holder of the selected item if set, otherwise
	 * the value will remain <code>null</code>
	 * @return The new <code>JButton</code>
	 */
	public static JButton buildBrowseButton(WorkbenchContextHolder contextHolder,
														 String key,
														 ValueModel classRepositoryHolder,
														 PropertyValueModel selectionHolder)
	{
		ResourceRepository repository = contextHolder.getWorkbenchContext().getApplicationContext().getResourceRepository();

		JButton browseButton = new JButton();
		browseButton.setText(repository.getString(key));
		browseButton.setMnemonic(repository.getMnemonic(key));
		browseButton.setDisplayedMnemonicIndex(repository.getMnemonicIndex(key));
		browseButton.setName(key);

		BrowseAction action = new BrowseAction(contextHolder, classRepositoryHolder, selectionHolder);
		browseButton.addActionListener(action);

		installBrowseButtonEnabler(classRepositoryHolder, browseButton);

		return browseButton;
	}

	/**
	 * Intalls a listener on the given holder so that the Browse button will be
	 * disabled when there is no class repository available.
	 */
	private static void installBrowseButtonEnabler(ValueModel classRepositoryHolder,
																  final JButton button)
	{
		classRepositoryHolder.addPropertyChangeListener(ValueModel.VALUE, new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				button.setEnabled(e.getNewValue() != null);
			}
		});
	}

	/**
	 * Prompts the user for a type from the specified class repository. Set the
	 * value of the specified selection holder to the type selected by the user.
	 * This method will not "clear" the selection; i.e. it will not set the value
	 * of the selectionHolder to null. The assumption is that once you get here,
	 * you require a type to be selected.
	 *
	 * @param context The context ...
	 * @param classRepository The repository used to retrieve the list of classes
	 * @param selectionHolder The holder of the selected item if set, otherwise
	 * the value will remain <code>null</code>
	 */
	public static void promptForType(WorkbenchContext context,
												ClassDescriptionRepository repository,
												PropertyValueModel selectionHolder)
	{
		try
		{
			context.getCurrentWindow().setCursor(CursorConstants.WAIT_CURSOR);

			// If the selection holder is not a SimplePropertyValueModel but a
			// PropertyAspectAdapter, we make sure it has a listener to it so that
			// the subject has been engaged and setValueFromSubject() is called
			PropertyChangeListener fakeListener = new PropertyChangeListener() { public void propertyChange(PropertyChangeEvent e) {} };
			selectionHolder.addPropertyChangeListener(PropertyValueModel.VALUE, fakeListener);

			ClassChooserDialog dialog = ClassChooserDialog.createDialog(repository, context);
			dialog.setVisible(true);

			if (dialog.wasConfirmed())
			{
				selectionHolder.setValue(dialog.selection());
			}

			selectionHolder.removePropertyChangeListener(PropertyValueModel.VALUE, fakeListener);
		}
		finally
		{
			context.getCurrentWindow().setCursor(CursorConstants.DEFAULT_CURSOR);
		}
	}

	/**
	 * This action simply calls {@link ClassChooserTools#promptForType(WorkbenchContext, MetaClassRepository, PropertyValueModel)}.
	 */
	private static class BrowseAction implements ActionListener
	{
		private final ValueModel classRepositoryHolder;
		private final WorkbenchContextHolder contextHolder;
		private final PropertyValueModel selectionHolder;

		/**
		 * Creates a new <code>BrowseAction</code>.
		 * 
		 * @param context
		 * @param classRepositoryHolder The holder of the
		 * {@link org.eclipse.persistence.tools.workbench.scplugin.model.meta.ClassRepository}
		 * used to retrieve the list of classes
		 * @param selectionHolder The holder of the selected item if set, otherwise
		 * the value will remain <code>null</code>
		 */
		private BrowseAction(WorkbenchContextHolder contextHolder,
									ValueModel classRepositoryHolder,
									PropertyValueModel selectionHolder)
		{
			super();

			this.classRepositoryHolder = classRepositoryHolder;
			this.contextHolder = contextHolder;
			this.selectionHolder = selectionHolder;
		}

		public void actionPerformed(ActionEvent e)
		{
			promptForType
			(
				contextHolder.getWorkbenchContext(),
				(ClassDescriptionRepository) classRepositoryHolder.getValue(),
				selectionHolder
			);
		}
	}
}
