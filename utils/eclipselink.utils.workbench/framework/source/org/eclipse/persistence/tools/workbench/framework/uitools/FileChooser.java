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
package org.eclipse.persistence.tools.workbench.framework.uitools;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.resources.DefaultResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


/**
 * This subclass of <code>JFileChooser</code> helps the user specify a relative path
 * to a file. If a root file is specified and the user wants to make the selection relative to that
 * file, then {@link JFileChooser#getSelectedFile()} and {@link JFileChooser#getSelectedFiles()}
 * will return files specified relative to the root.
 */
public class FileChooser extends JFileChooser {

	/** Determines whether the selected file(s) should be relative. */
	boolean convertToRelativePath;

	/** The directory to which the selected files are relative. */
	private File rootFile;

	/**
	 * If the dialog is NOT visible #get/setSelectedFile() and
	 * #get/setSelectedFiles() will return/take a relative file specification.
	 */
	private boolean dialogIsVisible;


	// ********** constructors **********

	/**
	 * Construct a file chooser at the user's "home" directory.
	 */
	public FileChooser() {
		this(null, null);
	}

	/**
	 * Construct a file chooser at the specified directory.
	 */
	public FileChooser(File currentDirectory) {
		this(currentDirectory, null);
	}

	/**
	 * Construct a file chooser at the specified directory with the
	 * specified "root" directory to which the user can choose a file
	 * relative to. If a "root" directory is not specified here, the
	 * check box will not be displayed.
	 */
	public FileChooser(File currentDirectory, File rootFile) {
		super(currentDirectory);
		this.rootFile = rootFile;
		// if a root file is specified, default to true
		this.convertToRelativePath = (this.rootFile != null);
	}


	// ********** "make relative" check box **********

	/**
	 * override to add the "make relative" check box if appropriate
	 */
	protected JDialog createDialog(Component parent) throws HeadlessException {
		JDialog dialog = super.createDialog(parent);
		// we only add the check box if a root file is specified
		if (this.rootFile != null) {
			this.addRelativeCheckBoxTo(dialog.getContentPane());
		}
		return dialog;
	}

	private void addRelativeCheckBoxTo(Container contentPane) {
		ResourceRepository rr = new DefaultResourceRepository(UIToolsResourceBundle.class);

		JCheckBox relativeCheckBox = new JCheckBox(rr.getString("FILECHOOSER_MAKE_RELATIVE_CHECKBOX", this.rootFile.getPath()));
		relativeCheckBox.setSelected(this.convertToRelativePath);
		relativeCheckBox.setMnemonic(rr.getMnemonic("FILECHOOSER_MAKE_RELATIVE_CHECKBOX"));
		relativeCheckBox.setDisplayedMnemonicIndex(rr.getMnemonicIndex("FILECHOOSER_MAKE_RELATIVE_CHECKBOX"));
		relativeCheckBox.addActionListener(this.buildActionListener());

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		panel.add(relativeCheckBox, BorderLayout.LINE_START);
		contentPane.add(panel, BorderLayout.PAGE_END);
	}

	private ActionListener buildActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileChooser.this.convertToRelativePath = ((JCheckBox) e.getSource()).isSelected();
			}
		};
	}


	// ********** public API **********

	/**
	 * override to set internal flag indicating whether the dialog is visible
	 */
	public int showDialog(Component parent, String approveButtonText) throws HeadlessException {
		this.dialogIsVisible = true;
		int result = super.showDialog(parent, approveButtonText);
		this.dialogIsVisible = false;
		return result;
	}

	/**
	 * override to make the selected file relative if appropriate
	 */
	public File getSelectedFile() {
		File selectedFile = super.getSelectedFile();
		selectedFile = checkForDuplicateEntry(selectedFile);
		return this.convertToRelativeFile(selectedFile);
	}

	/**
	 * override to make the selected files relative if appropriate
	 */
	public File[] getSelectedFiles() {
		File[] files = super.getSelectedFiles();

		if (files.length == 1) {
			files[0] = checkForDuplicateEntry(files[0]);
		}

		return this.convertToRelativeFiles(files);
	}

	/**
	 * TODO: Is this patching a JDK bug ??? Basically double clicking on a
	 * directory shows that directory in the File Name field and then
	 * clicking Open will append that directory to the end of the current
	 * directory, also, writing something in the File Name that does not
	 * exist gets appended
	 */
	private File checkForDuplicateEntry(File file) {
		if (getFileSelectionMode() == JFileChooser.FILES_AND_DIRECTORIES ||
	       getFileSelectionMode() == JFileChooser.DIRECTORIES_ONLY)
		{
			File currentDirectory = getCurrentDirectory();
	
			if ((file != null) && file.isAbsolute() && ! file.exists()) {
				file = currentDirectory;
			}
		}
		return file;
	}

	/**
	 * override to make the new selected file absolute if appropriate
	 */
	public void setSelectedFile(File file) {
		super.setSelectedFile(this.convertToAbsoluteFile(file));
	}

	/**
	 * override to make the new selected files absolute if appropriate
	 */
	public void setSelectedFiles(File[] files) {
		super.setSelectedFiles(this.convertToAbsoluteFiles(files));
	}


	// ********** conversion methods **********

	/**
	 * convert the specified files to be relative to the root directory if appropriate;
	 * NB: we munge the array passed in
	 */
	private File[] convertToRelativeFiles(File[] files) {
		for (int i = files.length; i-- > 0; ) {
			files[i] = this.convertToRelativeFile(files[i]);
		}
		return files;
	}

	/**
	 * convert the specified file to be relative to the root directory if appropriate
	 */
	private File convertToRelativeFile(File file) {
		// conversions only take place when the dialog is not visible
		if (this.dialogIsVisible) {
			return file;
		}
		// no need to convert null
		if (file == null) {
			return file;
		}
		// the check box is checked
		if (this.convertToRelativePath) {
			// workaround for Windows: depending on how the user navigates to a
			// file, the file returned might be a Win32ShellFolder and the "parent"
			// hierarchy causes problems when trying to convert the file to a file
			// that is relative to the "root" file; e.g. the file C:\foo will return the
			// following parents:
			//     C:\foo => C:\ => My Computer => C:\Documents and Settings\<user>\Desktop => null
			// so we force the file to be a simple File instead of a Win32ShellFolder  ~bjv
			file = new File(file.getPath());
			// rootFile cannot be null if convertToRelativePath is true
			return FileTools.convertToRelativeFile(file, this.rootFile);
		}
		return file;
	}

	/**
	 * convert the specified files to be absolute from the root directory;
	 * NB: we munge the array passed in
	 */
	private File[] convertToAbsoluteFiles(File[] files) {
		// no need to convert null
		if (files == null) {
			return files;
		}
		for (int i = files.length; i-- > 0; ) {
			files[i] = this.convertToAbsoluteFile(files[i]);
		}
		return files;
	}

	/**
	 * convert the specified file to be absolute from the root directory
	 */
	private File convertToAbsoluteFile(File file) {
		// conversions only take place when the dialog is not visible
		if (this.dialogIsVisible) {
			return file;
		}
		// no need to convert null
		if (file == null) {
			return file;
		}
		// the check box is checked
		if (this.convertToRelativePath) {
			// rootFile cannot be null if convertToRelativePath is true
			return FileTools.convertToAbsoluteFile(file, this.rootFile);
		}
		return file;
	}

}
