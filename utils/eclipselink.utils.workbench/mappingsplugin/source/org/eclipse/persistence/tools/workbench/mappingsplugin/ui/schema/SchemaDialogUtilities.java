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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.schema;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Frame;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.resources.DefaultResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.WaitDialog;
import org.eclipse.persistence.tools.workbench.mappingsmodel.resource.ResourceException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.resource.ResourceSpecification;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;
import org.eclipse.persistence.tools.workbench.uitools.LabelArea;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


final class SchemaDialogUtilities 
{
	private static ResourceRepository resourceRepository;
	
	
	private static ResourceRepository resourceRepository() {
		if (resourceRepository == null) {
			resourceRepository = buildResourceRepository();
		}
		
		return resourceRepository;
	}
	
	private static ResourceRepository buildResourceRepository() {
		return new DefaultResourceRepository(UiSchemaResourceBundle.class);
	}
	
	static void showUrlLoadFailure(Component parentComponent, String schemaName, ResourceException re, ResourceSpecification rs) {
		showUrlLoadFailure(parentComponent, schemaName, re, rs.getSourceKey(), rs.getLocation());
	}
	
	static void showUrlLoadFailure(Component parentComponent, String schemaName, ResourceException re, String resourceType, String resourceName) {
		String errorTitle = resourceRepository().getString("URL_LOAD_ERROR.TITLE");
		String errorCode = re.getCode();
		
		String errorMessage = resourceRepository().getString("URL_LOAD_ERROR.MESSAGE", schemaName);
		errorMessage += StringTools.CR;
		errorMessage += resourceRepository().getString("URL_LOAD_ERROR." + resourceType + "." + errorCode, 
															 new Object[] {resourceName});
		JOptionPane.showMessageDialog(parentComponent, errorMessage, errorTitle, JOptionPane.WARNING_MESSAGE);
	}
	
	/**
	 * This is placed here for 9.0.4 XDK support.  XSDExceptions are thrown in XDK versions
	 * beyond 9.0.4.
	 */
	static void showSchemaLoadFailure(Component parentComponent, String schemaName) {
		showSchemaLoadFailure(parentComponent, schemaName, null);
	}
	
	/**
	 * This is placed here for 9.0.4 XDK support.  RuntimeExceptions were the only exceptions
	 * thrown for v. 9.0.4.
	 */
	static void showSchemaLoadFailure(Component parentComponent, String schemaName, RuntimeException ex) {	
		showSchemaLoadFailure(parentComponent, schemaName, (Exception) ex);
	}
	
	/**
	 * This is placed here for 9.0.4 XDK support.  Internally we treat XSDExceptions the same
	 * as RuntimeExceptions, but we're leaving room for better treatment of XSDExceptions.
	 */
	private static void showSchemaLoadFailure(Component parentComponent, String schemaName, Exception ex) {	
		String errorTitle = resourceRepository().getString("SCHEMA_LOAD_ERROR.TITLE");
		
		String exceptionMessage = ex.getLocalizedMessage();
		String errorMessage = resourceRepository().getString("SCHEMA_LOAD_ERROR.MESSAGE", StringTools.CR, schemaName, exceptionMessage);
		
		LabelArea label = new LabelArea(errorMessage);
		
		JOptionPane.showMessageDialog(parentComponent, label, errorTitle, JOptionPane.WARNING_MESSAGE);
	}

	static void reloadSchemas(WorkbenchContext context, Iterator schemasIterator) {
		Thread thread = new Thread(new ReloadAllSchemasRunnable(context, schemasIterator), "XML Schema  Importation");
		thread.setPriority(Thread.NORM_PRIORITY - (Thread.NORM_PRIORITY - Thread.MIN_PRIORITY) /2);
		thread.start();
	}

	private static class ReloadAllSchemasRunnable implements Runnable {
		private WorkbenchContext context;
		private WaitDialog waitDialog;
		private Iterator schemasIterator;
		private MWXmlSchema[] currentSchema;

		ReloadAllSchemasRunnable(WorkbenchContext context, Iterator schemasIterator) {
			super();
			initialize(context, schemasIterator);
		}

		private void initialize(WorkbenchContext context, Iterator schemasIterator) {
			this.currentSchema = new MWXmlSchema[1];
			this.context = context;
			this.schemasIterator = schemasIterator;
			this.waitDialog = new WaitDialog(
					(Frame) context.getCurrentWindow(),
					context.getApplicationContext().getResourceRepository().getIcon("file.xml.large"),
					resourceRepository().getString("IMPORT_SCHEMA_DIALOG.WAIT_DIALOG.TITLE"),
					resourceRepository().getString("IMPORT_SCHEMA_DIALOG.WAIT_DIALOG.DESCRIPTION", "")
			);
		}

		private void showWaitCursor() {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					context.getCurrentWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				}
			});
		}

		private void hideWaitCursor() {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					context.getCurrentWindow().setCursor(Cursor.getDefaultCursor());
				}
			});
		}

		private void updateMessage() throws InvocationTargetException, InterruptedException {
			EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					String text = resourceRepository().getString("IMPORT_SCHEMA_DIALOG.WAIT_DIALOG.DESCRIPTION", currentSchema[0].getName());
					waitDialog.setMessage(text);
				}
			});
		}

		private Runnable buildWaitDialogLauncher() {
			return new Runnable() {
				public void run() {
					ReloadAllSchemasRunnable.this.waitDialog.setVisible(true);
				}
			};
		}

		public void run() {
			try {
				if (currentSchema[0] == null)
					showWaitCursor();

				EventQueue.invokeLater(this.buildWaitDialogLauncher());

				while (schemasIterator.hasNext()) {
					currentSchema[0] = (MWXmlSchema) schemasIterator.next();
					updateMessage();
					currentSchema[0].reload();
				}
				waitDialog.dispose();
			}
			catch (InvocationTargetException exception) {
				waitDialog.dispose();
				// we're probably in trouble if we get here...
				throw new RuntimeException(exception);
			}
			catch (InterruptedException exception) {
				waitDialog.dispose();
				// we're probably in trouble if we get here...
				throw new RuntimeException(exception);					
			}
			catch (final ResourceException re) {
				waitDialog.dispose();
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						SchemaDialogUtilities.showUrlLoadFailure(context.getCurrentWindow(), currentSchema[0].getName(), re, currentSchema[0].getSchemaSource());
					}
				});

				// Continue with the next schema to reload
				run();
			}
			// In XDK version 9.0.4 only RuntimeExceptons are thrown
			catch (final RuntimeException re) {
				waitDialog.dispose();
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						SchemaDialogUtilities.showSchemaLoadFailure(context.getCurrentWindow(), currentSchema[0].getName(), re);
					}
				});

				// Continue with the next schema to reload
				run();
			}

			hideWaitCursor();
		}
	}
}
