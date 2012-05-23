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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project;

import java.util.prefs.Preferences;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;



public final class ZeroArgConstructorPreference 
{
	public static final String MAINTAIN_ZERO_ARGUMENT_CONSTRUCTOR_PREFERENCE = "mantain zero argument constructor";
		public static final String YES 		= "true";
		public static final String NO 		= "false";
		public static final String PROMPT 	= "prompt";
		public static final String DEFAULT 	= PROMPT;


	private ZeroArgConstructorPreference() {
		super();
	}

	public static void optionallyAddZeroArgumentConstructor(MWClass mwClass, WorkbenchContext context) {
		String maintainZeroArgCon = 
			context.getApplicationContext().getPreferences().get(MAINTAIN_ZERO_ARGUMENT_CONSTRUCTOR_PREFERENCE, DEFAULT).intern();
		
		if (maintainZeroArgCon == YES || (maintainZeroArgCon == PROMPT && promptForResponse(mwClass, context))) {	
			mwClass.addZeroArgumentConstructor();
		}
	}
	
	private static boolean promptForResponse(MWClass mwClass, WorkbenchContext context) {
		Preferences preferences = context.getApplicationContext().getPreferences();
		ResourceRepository resourceRepository = context.getApplicationContext().getResourceRepository();
		
		// build dialog panel
		String title = 
			resourceRepository.getString("PREFERENCES.MAPPINGS.CLASS.MAINTAIN_ZERO_ARGUMENT_CONSTRUCTOR_DIALOG.title");
		String message = 
			resourceRepository.getString(
				"PREFERENCES.MAPPINGS.CLASS.MAINTAIN_ZERO_ARGUMENT_CONSTRUCTOR_DIALOG.message",
				new Object[] {mwClass.getName(), StringTools.CR}
			);
		PropertyValueModel dontAskAgainHolder = new SimplePropertyValueModel(new Boolean(false));
		JComponent dontAskAgainPanel = 
			SwingComponentFactory.buildDoNotAskAgainPanel(message, dontAskAgainHolder, resourceRepository);
		
		// prompt user for response
		int response = 
			JOptionPane.showConfirmDialog(
				context.getCurrentWindow(),
				dontAskAgainPanel,
				title,
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE
			);
		
		if (dontAskAgainHolder.getValue().equals(Boolean.TRUE)) {
			String preference = PROMPT;
			
			if (response == JOptionPane.YES_OPTION) {
				preference = YES;
			}
			else if (response == JOptionPane.NO_OPTION) {
				preference = NO;
			}
			
			preferences.put(MAINTAIN_ZERO_ARGUMENT_CONSTRUCTOR_PREFERENCE, preference);
		}
		
		return response == JOptionPane.YES_OPTION;
	}
}
