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
package org.eclipse.persistence.tools.workbench.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * This class will take a Beyond Compare "Comparison Report" and generate
 * two scripts:
 * 
 * 1. A Unix/Linux shell script that contains the appropriate ADE commands
 * 	for removing, checking out, and adding the appropriate files necessary
 * 	to synchronize the ADE source tree with the tree it was compared to.
 * 
 * 2. A Unix/Linux shell script that contains the appropriate file copy
 * 	commands to copy the files over to the ADE source tree once the
 * 	files have been checked out and created by the script described
 * 	above.
 * 
 * The file name of the Beyond Compare "Comparison Report" can be specified
 * via a command-line parm. The default value is ~/cvs-to-ade/bc.txt.
 * 
 * We expect the Beyond Compare "Comparison Report" to have the ADE
 * source tree on the "Left Side".
 */
public class ADEScriptGenerator3 {

	private static final String WORK_DIR_NAME = System.getProperty("user.home") + "/cvs-to-ade/";

	// inputs
	private static final String DEFAULT_BEYOND_COMPARE_REPORT_FILE_NAME = WORK_DIR_NAME + "bc.txt";

	// outputs
	private static final String ADE_SCRIPT_FILE_NAME = WORK_DIR_NAME + "check_out.sh";
	private static final String UPLOAD_SCRIPT_FILE_NAME = WORK_DIR_NAME + "upload.sh";

	// links to the appropriate directories
	private static final String ADE_DIRECTORY_NAME = WORK_DIR_NAME + "ade-base";
	private static final String CVS_DIRECTORY_NAME = WORK_DIR_NAME + "cvs-base";

	private static final String TODAY = DateFormat.getDateInstance().format(new Date());
	private static final String ADE_COMMENT = "Raleigh " + TODAY;


	private String[] leftSideNewerFileNames;
	private String[] rightSideNewerFileNames;
	private String[] leftSideOrphanFileNames;
	private String[] rightSideOrphanFileNames;
	private String[] contentsDifferFileNames;


	public static void main(String[] args) throws Exception {
		new ADEScriptGenerator3().execute(args);
	}

	public ADEScriptGenerator3() throws Exception {
		super();
	}

	public void execute(String[] args) throws Exception {
		System.out.println("*** BEGIN ***");
		File beyondCompareReportFile = this.buildBeyondCompareReportFile(args);
		this.readBeyondCompareReport(new BufferedReader(new FileReader(beyondCompareReportFile)));
		this.writeADEScriptOn(new FileWriter(ADE_SCRIPT_FILE_NAME));
		this.writeUploadScriptOn(new FileWriter(UPLOAD_SCRIPT_FILE_NAME));
		this.printStatistics(beyondCompareReportFile);
		System.out.println("*** END ***");
		System.exit(0);
	}

	private File buildBeyondCompareReportFile(String[] args) {
		return new File(this.buildBeyondCompareReportFileName(args));
	}

	/**
	 * allow the user to specify a Beyond Compare report file on the command-line
	 */
	private String buildBeyondCompareReportFileName(String[] args) {
		if ((args == null) || (args.length == 0)) {
			return DEFAULT_BEYOND_COMPARE_REPORT_FILE_NAME;
		}
		return args[0];
	}

	private void readBeyondCompareReport(BufferedReader bcReportReader) throws Exception {
		this.extractFileNames(bcReportReader, "Left Side Newer Files");	// skip header
		this.leftSideNewerFileNames = this.extractFileNames(bcReportReader, "Right Side Newer Files");
		this.rightSideNewerFileNames = this.extractFileNames(bcReportReader, "Left Side Orphans");
		this.leftSideOrphanFileNames = this.extractFileNames(bcReportReader, "Right Side Orphans");
		this.rightSideOrphanFileNames = this.extractFileNames(bcReportReader, "Contents Differ");
		this.contentsDifferFileNames = this.extractFileNames(bcReportReader, null);
		bcReportReader.close();
	}

	private String[] extractFileNames(BufferedReader bcReportReader, String terminator) throws Exception {
		List fileNames = new ArrayList();
		for (String line = bcReportReader.readLine(); this.lineIsNotATerminator(line, terminator); line = bcReportReader.readLine()) {
			line = line.replace('\\', '/');	// convert to unix-style file names
			if (line.indexOf('/') == -1) {
				continue;	// skip report headings and filler lines
			}
			if ( ! this.lineIsMW(line)) {
				continue;	// skip non-MW lines
			}
			if (this.lineIsMWJlib(line)) {
				continue;	// skip MW jlib lines
			}
			if (this.lineIsMWDevDocs(line)) {
				continue;	// skip MW dev-docs lines
			}
			fileNames.add(line);
		}
		return (String[]) fileNames.toArray(new String[fileNames.size()]);
	}

	private boolean lineIsMW(String line) {
		// return whether the line is for an MW file
		return line.startsWith("mwdev/")
			|| line.startsWith("mwtest/");
	}

	private boolean lineIsMWJlib(String line) {
		// return whether the line is for an MW jlib file
		return line.startsWith("mwdev/jlib/")
			|| line.startsWith("mwtest/jlib/");
	}
	
	private boolean lineIsMWDevDocs(String line) {
		//return whether the line is for an MW dev-doc file
		return line.startsWith("mwdev/dev-docs/");
	}

	private boolean lineIsNotATerminator(String line, String terminator) {
		if (line == null) {
			return false;		// EOF
		}
		if (terminator == null) {
			return true;		// the last section will stop at EOF
		}
		if (line.startsWith(terminator)) {
			return false;		// next section of report
		}
		return true;
	}

	private void writeADEScriptOn(Writer writer) throws Exception {
		writer.write("\n");

 		this.writeADECheckOutCommandsOn(this.rightSideNewerFileNames, writer);
 		this.writeADECheckOutCommandsOn(this.leftSideNewerFileNames, writer);

		this.writeADECommandsOn("rm", this.leftSideOrphanFileNames, writer);

		this.writeADEMakeElementCommandsOn(this.rightSideOrphanFileNames, writer);

		this.writeADECheckOutCommandsOn(this.contentsDifferFileNames, writer);

 		writer.close();
	}

	private void writeADECheckOutCommandsOn(String[] fileNames, Writer writer) throws Exception {
 		this.writeADECommandsOn("co -c \"" + ADE_COMMENT + "\"", fileNames, writer);
	}

	private void writeADEMakeElementCommandsOn(String[] fileNames, Writer writer) throws Exception {
		this.checkDirs(fileNames, writer);	// check for new directories first
		writer.write("\n");
		this.writeADECommandsOn("mkelem -c \"" + ADE_COMMENT + "\"", fileNames, writer);
	}

	private void checkDirs(String[] fileNames, Writer writer) throws Exception {
		Collection alreadyAddedDirs = new HashSet();
		for (int i = 0; i < fileNames.length; i++) {
			this.checkDir(fileNames[i], alreadyAddedDirs, writer);
		}
	}

	private void checkDir(String fileName, Collection alreadyAddedDirs, Writer writer) throws Exception {
		File file = new File(ADE_DIRECTORY_NAME, fileName);
		File dir = file.getParentFile();
		if (( ! dir.exists()) && ( ! alreadyAddedDirs.contains(dir))) {
			alreadyAddedDirs.add(dir);
			String relativeDirName = new File(fileName).getParent();
			this.writeADECommandOn("mkdir -p", relativeDirName, writer);
		}
	}

	private void writeADECommandsOn(String command, String[] fileNames, Writer writer) throws Exception {
		for (int i = 0; i < fileNames.length; i++) {
			this.writeADECommandOn(command, fileNames[i], writer);
		}
		writer.write("\n");
	}

	private void writeADECommandOn(String command, String fileName, Writer writer) throws Exception {
		writer.write("ade ");
		writer.write(command);
		writer.write(" ");
		writer.write(fileName);
		writer.write("\n");		// use unix-style LF
	}

	private void writeUploadScriptOn(Writer writer) throws Exception {
		this.writeCopyCommandsOn(this.leftSideNewerFileNames, writer);

		writer.write("\n");
		this.writeCopyCommandsOn(this.rightSideNewerFileNames, writer);

		writer.write("\n");
		this.writeCopyCommandsOn(this.rightSideOrphanFileNames, writer);

		writer.write("\n");
		this.writeCopyCommandsOn(this.contentsDifferFileNames, writer);

		writer.close();
	}

	private void writeCopyCommandsOn(String[] fileNames,Writer writer) throws Exception {
		for (int i = 0; i < fileNames.length; i++) {
			this.writeCopyCommandOn(fileNames[i], writer);
		}
	}

	private void writeCopyCommandOn(String fileName, Writer writer) throws Exception {
		File source = new File(CVS_DIRECTORY_NAME, fileName);
		File destination = new File(ADE_DIRECTORY_NAME, fileName);
		writer.write("cp -v ");
		writer.write("\"");  //double quote the path
		writer.write(source.getPath());
		writer.write("\"");  //double quote the path
		writer.write(" ");
		writer.write("\"");  //double quote the path
		writer.write(destination.getPath());
		writer.write("\"");  //double quote the path
		writer.write("\n");
	}

	private void printStatistics(File beyondCompareReportFile) {
		System.out.println("Inputs");
		System.out.println("	Beyond Compare Comparison Report: " + beyondCompareReportFile.getPath());
		System.out.println("Outputs");
		System.out.println("	ADE script: " + ADE_SCRIPT_FILE_NAME);
		System.out.println("	Upload script: " + UPLOAD_SCRIPT_FILE_NAME);
		System.out.println("Statistics");
		System.out.println("	ADE file newer: " + this.leftSideNewerFileNames.length + " (check these files)");
		System.out.println("	local file newer: " + this.rightSideNewerFileNames.length);
		System.out.println("	removed: " + this.leftSideOrphanFileNames.length);
		System.out.println("	added: " + this.rightSideOrphanFileNames.length);
		System.out.println("	contents differ: " + this.contentsDifferFileNames.length);
	}

}

