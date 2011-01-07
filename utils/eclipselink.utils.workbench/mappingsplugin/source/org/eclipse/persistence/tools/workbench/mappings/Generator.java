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
package org.eclipse.persistence.tools.workbench.mappings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.prefs.Preferences;

import org.eclipse.persistence.tools.workbench.framework.resources.DefaultStringRepository;
import org.eclipse.persistence.tools.workbench.framework.resources.StringRepository;
import org.eclipse.persistence.tools.workbench.mappingsio.ProjectIOManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;


/**
 * Simple command-line processor for exporting something from a
 * project.
 */
public class Generator {
	private Adapter adapter;
	private StringRepository stringRepository;


	public Generator(Adapter adapter) {
		super();
		this.adapter = adapter;
		this.stringRepository = new DefaultStringRepository(MappingsBundle.class);
	}

	/**
	 * this method should only be called by a #main(String[])
	 * method because it will write to the console and call System.exit(int)
	 */
	protected void execute(String args[]) {
		int status = 0;
		PrintStream log = null;
		if ((args.length < 2) || (args.length > 3)) {
			System.err.println(this.stringRepository.getString("help", this.adapter.getClass().getName()));
			System.exit(1);
		}
		if (args.length == 3) {
			try {
				log = new PrintStream(new FileOutputStream(args[2]));
			} catch (FileNotFoundException ex) {
				// print the stack trace, but continue...
				ex.printStackTrace();
				log = System.err;
				status = 1;
			}
		} else {
			log = System.err;
		}

		status = execute( args[0], args[1], log);
		System.exit(status);
	}

	/**
	 * Allows internal processing for exporting something from a project.
	 * @return 0 if no exeption occurred
	 */
	public int execute(String input, String output, PrintStream log) {
		int status = 0;
		File inputFile = new File(input);
		File outputFile = new File(output);

		try {
			MWProject project = new ProjectIOManager().read(inputFile, Preferences.userNodeForPackage(this.getClass()));
			project.validateBranch();
			if (project.hasBranchProblems()) {
				log.print(this.stringRepository.getString("generatingMight"));
			}
			this.adapter.export(project, outputFile);
		} catch (Throwable ex) {
			ex.printStackTrace(log);
			status = 1;
		}
		if( status == 0) 
			System.out.println(this.stringRepository.getString("EOJ"));
		else
		    System.out.println(this.stringRepository.getString("generationError"));
		return status;
	}

	/**
	 * Allows internal processing for exporting something from a project.
	 * @return 0 if no exeption occurred
	 */
	public int execute(String input, String output) {
	    
	    return execute(input, output, System.err);
	}

	/**
	 * adapter for performing the actual "export"
	 */
	public interface Adapter {
		void export(MWProject project, File outputFile);
	}

}
