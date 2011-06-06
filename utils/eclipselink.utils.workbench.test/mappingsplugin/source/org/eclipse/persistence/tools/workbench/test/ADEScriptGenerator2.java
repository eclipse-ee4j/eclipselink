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
 * 	This shell script is written to the root directory of the ADE source tree.
 * 
 * 2. A Windows command script that contains the appropriate file copy
 * 	commands to copy the files over to the ADE source tree once the
 * 	files have been checked out and created by the script described
 * 	above. This command script is written to the same directory that
 * 	holder the Beyond Compare "Comparison Report".
 * 
 * The file name of the Beyond Compare "Comparison Report" can be specified
 * via a command-line parm. The default value is C:\temp\bc.txt.
 * 
 * We expect the Beyond Compare "Comparison Report" to have the ADE
 * source tree on the "Left Side".
 */
public class ADEScriptGenerator2 {

	private static final String ADE_SCRIPT_FILE_NAME = "check_out.sh";
	private static final String DEFAULT_ADE_SCRIPT_FILE_LOCATION = "C:/temp/check_out.sh";
	private static final String UPLOAD_SCRIPT_FILE_NAME = "upload.cmd";
	private static final String UPLOAD_LOG_FILE_NAME = "upload.log";

	private static final String TODAY = DateFormat.getDateInstance().format(new Date());
	private static final String ADE_COMMENT = "Raleigh " + TODAY;


	private File beyondCompareReportFile;
		private static final String DEFAULT_BEYOND_COMPARE_REPORT_FILE_NAME = "C:/temp/bc.txt";
	private File adeDirectory;
	private File localDirectory;

	private String[] matchingFileNames;
	private String[] leftSideNewerFileNames;
	private String[] rightSideNewerFileNames;
	private String[] leftSideOrphanFileNames;
	private String[] rightSideOrphanFileNames;
	private String[] contentsDifferFileNames;

	private final static String CR = System.getProperty("line.separator");

	public static void main(String[] args) throws Exception {
		new ADEScriptGenerator2().execute(args);
	}

	public ADEScriptGenerator2() throws Exception {
		super();
	}

	public void execute(String[] args) throws Exception {
		this.beyondCompareReportFile = this.buildBeyondCompareReportFile(args);
		this.readBeyondCompareReport(new BufferedReader(new FileReader(this.beyondCompareReportFile)));
		this.generateADEScript(new FileWriter(this.buildADEScriptFile()));
		this.generateUploadScript(new FileWriter(this.buildUploadScriptFile()));
		this.printStatistics();
		System.out.println("*** EOJ ***");
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
		} else {
			return args[0];
		}
	}

	/**
	 * put the ADE script in the ADE root
	 */
	private File buildADEScriptFile() {
//		File root = adeDirectory;
//		while (root.getParentFile() != null) {
//			root = root.getParentFile();
//		}
		return new File(DEFAULT_ADE_SCRIPT_FILE_LOCATION);
	}

	/**
	 * put the upload script in the same directory as the Beyond Compare report
	 */
	private File buildUploadScriptFile() {
		return new File(this.beyondCompareReportFile.getParentFile(), UPLOAD_SCRIPT_FILE_NAME);
	}

	private void readBeyondCompareReport(BufferedReader bcReportReader) throws Exception {
		this.extractDirectoryNames(bcReportReader);
		this.extractFileNames(bcReportReader);	// skip remainder of heading
		this.matchingFileNames = this.extractFileNames(bcReportReader);
		this.leftSideNewerFileNames = this.extractFileNames(bcReportReader);
		this.rightSideNewerFileNames = this.extractFileNames(bcReportReader);
		this.leftSideOrphanFileNames = this.extractFileNames(bcReportReader);
		this.rightSideOrphanFileNames = this.extractFileNames(bcReportReader);
		this.contentsDifferFileNames = this.extractFileNames(bcReportReader);
	}

	private void extractDirectoryNames(BufferedReader bcReportReader) throws Exception {
		String firstLine = bcReportReader.readLine();
		String start = "Folder Comparison of ";
		if ( ! firstLine.startsWith(start)) {
			throw new IllegalStateException("invalid first line: " + firstLine);
		}
		int end = firstLine.indexOf(' ', start.length());
		this.adeDirectory = new File(firstLine.substring(start.length(), end));

		String secondHalf = firstLine.substring(end);
		String conjunction = " and ";
		if ( ! secondHalf.startsWith(conjunction)) {
			throw new IllegalStateException("invalid first line: " + firstLine);
		}
		this.localDirectory = new File(secondHalf.substring(conjunction.length()).trim());
	}

	private String[] extractFileNames(BufferedReader bcReportReader) throws Exception {
		List fileNames = new ArrayList();
		for (String line = bcReportReader.readLine(); this.lineIsNotATerminator(line); line = bcReportReader.readLine()) {
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
				continue; //skip MW dev-docs lines
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

	private boolean lineIsNotATerminator(String line) {
		if (line == null) {
			// EOF
			return false;
		}
		if (line.equals("--------------------------")) {
			// next section of report
			return false;
		}
		return true;
	}

	private void generateADEScript(Writer writer) throws Exception {
//		writer.write("cd ~");	// start in the user's home directory
		writer.write("\n");	// use unix-style LF in the ADE script

//		writer.write("cd ");
//		writer.write(this.buildRelativeADEViewDirectoryName());
//		writer.write("\n");
//		writer.write("\n");

 		this.writeADECheckOutCommandsOn(this.rightSideNewerFileNames, writer);
 		this.writeADECheckOutCommandsOn(this.leftSideNewerFileNames, writer);

		this.writeADECommandsOn("rm", this.leftSideOrphanFileNames, writer);
		writer.write("\n");

		this.writeADEMakeElementCommandsOn(this.rightSideOrphanFileNames, writer);
		writer.close();
	}

	private String buildRelativeADEViewDirectoryName() {
		if (this.adeDirectory.getParentFile() == null) {
			return "~";	// the ADE directory is the root - unlikely...
		}

		// gather up all the directory names, dropping the Windows root
		File temp = this.adeDirectory;
		List dirNames = new ArrayList();
		while (temp.getParentFile() != null) {
			dirNames.add(temp.getName());
			temp = temp.getParentFile();
		}

		StringBuffer sb = new StringBuffer();
		for (int i = dirNames.size(); i-- > 0; ) {
			sb.append(dirNames.get(i));
			if (i != 0) {
				sb.append('/');	// convert to unix-style file name
			}
		}
		return sb.toString();
	}

	private void writeADECheckOutCommandsOn(String[] fileNames, Writer writer) throws Exception {
 		this.writeADECommandsOn("co -c \"" + ADE_COMMENT + "\"", fileNames, writer);
		writer.write("\n");
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
		File file = new File(this.adeDirectory, fileName);
		File dir = file.getParentFile();
		if (( ! dir.exists()) && ( ! alreadyAddedDirs.contains(dir))) {
			alreadyAddedDirs.add(dir);
			String relativeDirName = new File(fileName).getParent();
			relativeDirName = relativeDirName.replace('\\', '/');	// convert back to unix-style file names
			this.writeADECommandOn("mkdir -p", relativeDirName, writer);
		}
	}

	private void writeADECommandsOn(String command, String[] fileNames, Writer writer) throws Exception {
		for (int i = 0; i < fileNames.length; i++) {
			this.writeADECommandOn(command, fileNames[i], writer);
		}
	}

	private void writeADECommandOn(String command, String fileName, Writer writer) throws Exception {
		writer.write("ade ");
		writer.write(command);
		writer.write(" ");
		writer.write(fileName);
		writer.write("\n");	// use unix-style LF
	}

	private void generateUploadScript(Writer writer) throws Exception {
		File logFile = this.buildUploadLogFile();
		writer.write("del ");
		writer.write(logFile.getPath());
		writer.write(CR);

		writer.write(CR);
		this.writeCopyCommandsOn(this.leftSideNewerFileNames, logFile, writer);

		writer.write(CR);
		this.writeCopyCommandsOn(this.rightSideNewerFileNames, logFile, writer);

		writer.write(CR);
		this.writeCopyCommandsOn(this.rightSideOrphanFileNames, logFile, writer);

		writer.write(CR);
		writer.write("notepad ");
		writer.write(logFile.getPath());
		writer.write(CR);

		writer.close();
	}

	/**
	 * put the upload log in the same directory as the Beyond Compare report
	 */
	private File buildUploadLogFile() {
		return new File(this.beyondCompareReportFile.getParentFile(), UPLOAD_LOG_FILE_NAME);
	}

	private void writeCopyCommandsOn(String[] fileNames, File logFile, Writer writer) throws Exception {
		for (int i = 0; i < fileNames.length; i++) {
			this.writeCopyCommandOn(fileNames[i], logFile, writer);
		}
	}

	private void writeCopyCommandOn(String fileName, File logFile, Writer writer) throws Exception {
		File source = new File(this.localDirectory, fileName);
		File destination = new File(this.adeDirectory, fileName);
		writer.write("copy ");
		writer.write("\"");  //double quote the path
		writer.write(source.getPath());
		writer.write("\"");  //double quote the path
		writer.write(" ");
		writer.write(destination.getPath());
		writer.write(" >> ");
		writer.write(logFile.getPath());
		writer.write(CR);
	}

	private void printStatistics() {
		System.out.println("Inputs");
		System.out.println("	Beyond Compare Comparison Report: " + this.beyondCompareReportFile.getPath());
		System.out.println("Outputs");
		System.out.println("	ADE script: " + this.buildADEScriptFile().getPath());
		System.out.println("	Upload script: " + this.buildUploadScriptFile().getPath());
		System.out.println("Statistics");
		System.out.println("	matching: " + this.matchingFileNames.length);
		System.out.println("	ADE file newer: " + this.leftSideNewerFileNames.length + " (check these files)");
		System.out.println("	local file newer: " + this.rightSideNewerFileNames.length);
		System.out.println("	removed: " + this.leftSideOrphanFileNames.length);
		System.out.println("	added: " + this.rightSideOrphanFileNames.length);
		System.out.println("	contents differ: " + this.contentsDifferFileNames.length);
	}

}
