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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db;

import java.awt.Component;
import java.awt.event.MouseEvent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.DoubleClickMouseListener;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.platformsplugin.ui.PlatformComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


final class DatabasePlatformChooserDialog 
	extends AbstractDialog 
{
	//initial focus component
	JList list;
	private PropertyValueModel selectionHolder;

	DatabasePlatformChooserDialog(WorkbenchContext context, PropertyValueModel selectionHolder) {
		super(context, context.getApplicationContext().getResourceRepository().getString("DATABASE_PLATFORMS_DIALOG.title"));
		this.selectionHolder = selectionHolder;
	}

	private DoubleClickMouseListener buildDoubleClickMouseListener() {
		return new DoubleClickMouseListener() {
			public void mouseDoubleClicked(MouseEvent e) {
				DatabasePlatformChooserDialog.this.clickOK();
			}
		};
	}

	private ListSelectionListener buildListSelectionListener() {
		return new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if ( ! e.getValueIsAdjusting()) {
					JList list = (JList) e.getSource();
					DatabasePlatformChooserDialog.this.getOKAction().setEnabled(list.getSelectedValue() != null);
				}
			}
		};
	}

	protected Component buildMainPanel() {
		// Create the list
		this.list = PlatformComponentFactory.buildPlatformList(selectionHolder);
		this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.list.addListSelectionListener(buildListSelectionListener());

		// setup double clicking in the list
		SwingComponentFactory.addDoubleClickMouseListener(this.list, buildDoubleClickMouseListener());

		JScrollPane scrollPane = new JScrollPane(this.list);
		scrollPane.getViewport().setPreferredSize(this.list.getPreferredSize());
		return scrollPane;
	}

	protected String helpTopicId() {
		return "dialog.databasePlatform";
	}

	protected Component initialFocusComponent() {
		return this.list;
	}

	protected void initialize() {
		super.initialize();
		getOKAction().setEnabled(false);
	}
}
