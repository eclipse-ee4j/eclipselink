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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;

/**
 * List any files that have any EOLs that are not the standard
 * DOS/Windows CR-LF (0x0D0A).
 */
public class CheckEOL {

	public static final int cr = 0x0D;
	public static final int lf = 0x0A;

	public static void main(String[] args) {
		new CheckEOL().exec();
	}

	public void exec() {
		String rootDirectoryName = "C:/dev/main";
		Iterator invalidFiles = this.javaFilesInTreeWithInvalidWinEOL(rootDirectoryName);
		Iterator invalidFileNames = CollectionTools.sortedSet(this.fileNames(invalidFiles)).iterator();
		int count = 0;
		System.out.println("Files with bogus EOL:");
		while (invalidFileNames.hasNext()) {
			count++;
			System.out.println("\t" + invalidFileNames.next());
		}
		System.out.println("*** total = " + count + " ***");
		System.exit(0);
	}

	/**
	 * Return an iterator on all the .java files under the specified
	 * directory that have invalid EOL character combinations
	 * for DOS/Windows, recursing into subdirectories.
	 * If any errors occur while processing the files, a
	 * RuntimeException is thrown.
	 */
	public Iterator javaFilesInTreeWithInvalidWinEOL(String directoryName) {
		return this.javaFilesInTreeWithInvalidWinEOL(new File(directoryName));
	}

	/**
	 * Return an iterator on all the .java files under the specified
	 * directory that have invalid EOL character combinations
	 * for DOS/Windows, recursing into subdirectories.
	 * If any errors occur while processing the files, a
	 * RuntimeException is thrown.
	 */
	public Iterator javaFilesInTreeWithInvalidWinEOL(File directory) {
		return this.filesWithInvalidWinEOL(this.javaFiles(FileTools.filesInTree(directory)));
	}

	/**
	 * Return an iterator on all the .java files among the specified files.
	 */
	private Iterator javaFiles(Iterator files) {
		return new FilteringIterator(files) {
			protected boolean accept(Object next) {
				return ((File) next).getName().toLowerCase().endsWith(".java");
			}
		};
	}

	/**
	 * Return an iterator on all the files under the specified
	 * directory that have invalid EOL character combinations
	 * for DOS/Windows, recursing into subdirectories.
	 * If any errors occur while processing the files, a
	 * RuntimeException is thrown.
	 */
	public Iterator filesInTreeWithInvalidWinEOL(String directoryName) {
		return this.filesInTreeWithInvalidWinEOL(new File(directoryName));
	}

	/**
	 * Return an iterator on all the files under the specified
	 * directory that have invalid EOL character combinations
	 * for DOS/Windows, recursing into subdirectories.
	 * If any errors occur while processing the files, a
	 * RuntimeException is thrown.
	 */
	public Iterator filesInTreeWithInvalidWinEOL(File directory) {
		return this.filesWithInvalidWinEOL(FileTools.filesInTree(directory));
	}
	
	/**
	 * Return an iterator on all the files under the specified
	 * directory that have invalid EOL character combinations
	 * for DOS/Windows, recursing into subdirectories.
	 * If any errors occur while processing the files, a
	 * RuntimeException is thrown.
	 */
	public Iterator filesWithInvalidWinEOL(Iterator files) {
		return new FilteringIterator(files) {
			protected boolean accept(Object next) {
				try {
					return CheckEOL.this.fileHasInvalidWinEOL((File) next);
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}
		};
	}
	
	/**
	 * Return whether the specified file contains any invalid
	 * EOL character combinations for DOS/Windows.
	 * Every EOL must be a CR-LF (0x0D0A). Any standalone
	 * CR or LF is invalid.
	 */
	public boolean fileHasInvalidWinEOL(File file) throws FileNotFoundException, IOException {
		return this.streamHasInvalidWinEOL(new BufferedInputStream(new FileInputStream(file), 262144));	// 256K
	}

	/**
	 * Return whether the specified stream contains any invalid
	 * EOL character combinations for DOS/Windows.
	 * Every EOL must be a CR-LF (0x0D0A). Any standalone
	 * CR or LF is invalid.
	 */
	public boolean streamHasInvalidWinEOL(InputStream stream) throws IOException {
		// prime the characters
		int previous = -1;
		int current = stream.read();
		int next = stream.read();
		while (current != -1) {		// empty stream is OK
			if (this.charsAreInvalidWinEOL(previous, current, next)) {
				stream.close();
				return true;
			}
			previous = current;
			current = next;
			next = stream.read();
		}
		stream.close();
		return false;
	}
	
	private boolean charsAreInvalidWinEOL(int previous, int current, int next) {
		if (current == cr) {
			return next != lf;
		}
		if (current == lf) {
			return previous != cr;
		}
		return false;
	}

	private Iterator fileNames(Iterator files) {
		return new TransformationIterator(files) {
			protected Object transform(Object next) {
				return ((File) next).getAbsolutePath();
			}
		};
	}

}
