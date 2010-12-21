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
package org.eclipse.persistence.tools.workbench.test.utility.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;

public class FileToolsTests extends TestCase {
	private File tempDir;
	
	public static Test suite() {
		return new TestSuite(FileToolsTests.class);
	}
	
	public FileToolsTests(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		this.tempDir = this.buildTempDir();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		this.deleteDir(this.tempDir);
	}
	
	public void testFilesIn() {
		Collection files = CollectionTools.collection(FileTools.filesIn(this.tempDir.getPath()));
		assertEquals("invalid file count", 3, files.size());
	}
	
	public void testDirectoriesIn() {
		Collection files = CollectionTools.collection(FileTools.directoriesIn(this.tempDir.getPath()));
		assertEquals("invalid directory count", 2, files.size());
	}
	
	public void testFilesInTree() {
		Collection files = CollectionTools.collection(FileTools.filesInTree(this.tempDir.getPath()));
		assertEquals("invalid file count", 9, files.size());
	}
	
	public void testDirectoriesInTree() {
		Collection files = CollectionTools.collection(FileTools.directoriesInTree(this.tempDir.getPath()));
		assertEquals("invalid directory count", 3, files.size());
	}
	
	public void testDeleteDirectory() throws IOException {
		// build another temporary directory just for this test
		File dir = this.buildTempDir();
		assertTrue("temporary directory not created", dir.exists());
		FileTools.deleteDirectory(dir.getPath());
		assertFalse("temporary directory not deleted", dir.exists());
	}
	
	public void testDeleteDirectoryContents() throws IOException {
		// build another temporary directory just for this test
		File dir = this.buildTempDir();
		assertTrue("temporary directory not created", dir.exists());
		FileTools.deleteDirectoryContents(dir.getPath());
		assertTrue("temporary directory should not have been deleted", dir.exists());
		assertTrue("temporary directory contents not deleted", dir.listFiles().length == 0);
		dir.delete();
	}

	public void testCopyToFile() throws IOException {
		File destFile = new File(this.tempDir, "destfile.txt");
		this.copyToFile(destFile, "testCopyToFile");
	}

	public void testCopyToPreExistingFile() throws IOException {
		File destFile = new File(this.tempDir, "destfile.txt");
		Writer writer = new OutputStreamWriter(new FileOutputStream(destFile));
		writer.write("this text should be replaced...");
		writer.close();
		this.copyToFile(destFile, "testCopyToPreExistingFile");
	}

	private void copyToFile(File destFile, String writeString) throws IOException {
		File sourceFile = new File(this.tempDir, "sourcefile.txt");
		char[] readBuffer = new char[writeString.length()];

		Writer writer = new OutputStreamWriter(new FileOutputStream(sourceFile));
		writer.write(writeString);
		writer.close();

		FileTools.copyToFile(sourceFile, destFile);

		Reader reader = new InputStreamReader(new FileInputStream(destFile));
		reader.read(readBuffer);
		reader.close();
		String readString = new String(readBuffer);
		assertEquals(writeString, readString);
	}

	public void testCopyToDirectory() throws IOException {
		File sourceFile = new File(this.tempDir, "sourcefile.txt");
		String writeString = "testCopyToDirectory";

		File destDir = new File(this.tempDir, "destdir");
		destDir.mkdir();
		File destFile = new File(destDir, "sourcefile.txt");
		char[] readBuffer = new char[writeString.length()];

		Writer writer = new OutputStreamWriter(new FileOutputStream(sourceFile));
		writer.write(writeString);
		writer.close();

		FileTools.copyToDirectory(sourceFile, destDir);

		Reader reader = new InputStreamReader(new FileInputStream(destFile));
		reader.read(readBuffer);
		reader.close();
		String readString = new String(readBuffer);
		assertEquals(writeString, readString);
	}

	public void testFilter() throws IOException {
		String prefix = "XXXtestFileXXX";
		File testFile1 = new File(this.tempDir, prefix + "1");
		testFile1.createNewFile();
		File testFile2 = new File(this.tempDir, prefix + "2");
		testFile2.createNewFile();

		FileFilter filter = this.buildFileFilter(prefix);
		Iterator filteredFilesIterator = FileTools.filter(FileTools.filesIn(this.tempDir), filter);
		Collection filteredFiles = CollectionTools.collection(filteredFilesIterator);
		assertEquals(2, filteredFiles.size());
		assertTrue(filteredFiles.contains(testFile1));
		assertTrue(filteredFiles.contains(testFile2));
	}

	private FileFilter buildFileFilter(final String prefix) {
		return new FileFilter() {
			public boolean accept(File file) {
				return file.getName().startsWith(prefix);
			}
		};
	}

	public void testStripExtension() {
		assertEquals("foo", FileTools.stripExtension("foo.xml"));
		assertEquals("foo.bar", FileTools.stripExtension("foo.bar.xml"));
		assertEquals("foo", FileTools.stripExtension("foo"));
		assertEquals("foo", FileTools.stripExtension("foo."));
	}

	public void testExtension() {
		assertEquals(".xml", FileTools.extension("foo.xml"));
		assertEquals(".xml", FileTools.extension("foo.bar.xml"));
		assertEquals("", FileTools.extension("foo"));
		assertEquals("", FileTools.extension("foo,xml"));
		assertEquals(".", FileTools.extension("foo."));
	}

	public void testEmptyTemporaryDirectory() throws IOException {
		File tempDir1 = FileTools.temporaryDirectory();
		File testFile1 = new File(tempDir1, "junk");
		testFile1.createNewFile();

		File tempDir2 = FileTools.emptyTemporaryDirectory();
		assertEquals(tempDir1, tempDir2);
		assertTrue(tempDir2.isDirectory());
		assertEquals(0, tempDir2.listFiles().length);
		tempDir2.delete();
	}

	public void testCanonicalFileName() {
		File file1 = new File("foo");
		file1 = new File(file1, "bar");
		file1 = new File(file1, "baz");
		file1 = new File(file1, "..");
		file1 = new File(file1, "..");
		file1 = new File(file1, "bar");
		file1 = new File(file1, "baz");
		File file2 = new File(System.getProperty("user.dir"));
		file2 = new File(file2, "foo");
		file2 = new File(file2, "bar");
		file2 = new File(file2, "baz");
		File file3 = FileTools.canonicalFile(file1);
		assertEquals(file2, file3);
	}

	private boolean isExecutingOnWindows() {
		return this.isExecutingOn("Windows");
	}

//	private boolean isExecutingOnLinux() {
//		return this.isExecutingOn("Linux");
//	}
//
	private boolean isExecutingOn(String osName) {
		return System.getProperty("os.name").indexOf(osName) != -1;
	}

	public void testPathFiles() {
		File[] expected;
		File[] actual;

		if (this.isExecutingOnWindows()) {
			expected = new File[] {new File("C:/"), new File("C:/foo"), new File("C:/foo/bar"), new File("C:/foo/bar/baz.txt")};
			actual = this.pathFiles(new File("C:/foo/bar/baz.txt"));
			assertTrue(Arrays.equals(expected, actual));
		}

		expected = new File[] {new File("/"), new File("/foo"), new File("/foo/bar"), new File("/foo/bar/baz.txt")};
		actual = this.pathFiles(new File("/foo/bar/baz.txt"));
		assertTrue(Arrays.equals(expected, actual));

		expected = new File[] {new File("foo"), new File("foo/bar"), new File("foo/bar/baz.txt")};
		actual = this.pathFiles(new File("foo/bar/baz.txt"));
		assertTrue(Arrays.equals(expected, actual));

		expected = new File[] {new File(".."), new File("../foo"), new File("../foo/bar"), new File("../foo/bar/baz.txt")};
		actual = this.pathFiles(new File("../foo/bar/baz.txt"));
		assertTrue(Arrays.equals(expected, actual));

		expected = new File[] {new File("."), new File("./foo"), new File("./foo/bar"), new File("./foo/bar/baz.txt")};
		actual = this.pathFiles(new File("./foo/bar/baz.txt"));
		assertTrue(Arrays.equals(expected, actual));
	}

	private File[] pathFiles(File file) {
		return (File[]) ClassTools.invokeStaticMethod(FileTools.class, "pathFiles", File.class, file);
	}

	public void testRelativeParentFile() {
		assertEquals(new File(".."), this.relativeParentFile(1));
		assertEquals(new File("../.."), this.relativeParentFile(2));
		assertEquals(new File("../../.."), this.relativeParentFile(3));

		boolean exCaught = false;
		try {
			File file = this.relativeParentFile(0);
			fail("invalid return: " + file);
		} catch (RuntimeException ex) {
			if (ex.getCause() instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) ex.getCause();
				if (ite.getTargetException() instanceof IllegalArgumentException) {
					exCaught = true;
				}
			}
		}
		assertTrue(exCaught);
	}

	private File relativeParentFile(int len) {
		return (File) ClassTools.invokeStaticMethod(FileTools.class, "relativeParentFile", int.class, new Integer(len));
	}

	public void testConvertToRelativeFile() {
		String prefix = this.isExecutingOnWindows() ? "C:" : "";
		File file;
		File dir;
		File relativeFile;

		if (this.isExecutingOnWindows()) {
			// on Windows, a drive must be specified for a file to be absolute (i.e. not relative)
			this.verifyUnchangedRelativeFile("/dir1/dir2/file.txt", "C:/dir1/dir2");
			// different drives
			this.verifyUnchangedRelativeFile("D:/dir1/dir2/file.txt", "C:/dir1/dir2");
		}
		this.verifyUnchangedRelativeFile("dir1/dir2/file.txt", prefix + "/dir1/dir2");
		this.verifyUnchangedRelativeFile("./dir1/dir2/file.txt", prefix + "/dir1/dir2");
		this.verifyUnchangedRelativeFile("../../dir1/dir2/file.txt", prefix + "/dir1/dir2");

		file = new File(prefix + "/dir1/dir2");
		dir = new File(prefix + "/dir1/dir2");
		relativeFile = FileTools.convertToRelativeFile(file, dir);
		assertEquals(new File("."), relativeFile);

		file = new File(prefix + "/dir1/dir2/file.txt");
		dir = new File(prefix + "/dir1/dir2");
		relativeFile = FileTools.convertToRelativeFile(file, dir);
		assertEquals(new File("file.txt"), relativeFile);

		file = new File(prefix + "/dir1/dir2/../dir2/file.txt");
		dir = new File(prefix + "/dir1/dir2");
		relativeFile = FileTools.convertToRelativeFile(file, dir);
		assertEquals(new File("file.txt"), relativeFile);

		file = new File(prefix + "/dir1/dir2/dir3/dir4/dir5/file.txt");
		dir = new File(prefix + "/dir1/dir2");
		relativeFile = FileTools.convertToRelativeFile(file, dir);
		assertEquals(new File("dir3/dir4/dir5/file.txt"), relativeFile);

		file = new File(prefix + "/dir1/dir2/file.txt");
		dir = new File(prefix + "/dir1/dir2/dir3/dir4/dir5");
		relativeFile = FileTools.convertToRelativeFile(file, dir);
		assertEquals(new File("../../../file.txt"), relativeFile);

		file = new File(prefix + "/dir1/dir2/file.txt");
		dir = new File(prefix + "/dir1/dir2/dir3");
		relativeFile = FileTools.convertToRelativeFile(file, dir);
		assertEquals(new File("../file.txt"), relativeFile);

		file = new File(prefix + "/dir1/dir2/dirA/dirB/dirC/file.txt");
		dir = new File(prefix + "/dir1/dir2/dir3/dir4/dir5");
		relativeFile = FileTools.convertToRelativeFile(file, dir);
		assertEquals(new File("../../../dirA/dirB/dirC/file.txt"), relativeFile);

		file = new File(prefix + "/dir1/dir2");
		dir = new File(prefix + "/dir1/dir2/dir3/dir4/dir5");
		relativeFile = FileTools.convertToRelativeFile(file, dir);
		assertEquals(new File("../../.."), relativeFile);

		file = new File(prefix + "/My Documents/My Workspace/Project 1/lib/toplink.jar");
		dir = new File(prefix + "/My Documents/My Workspace/Project 1");
		relativeFile = FileTools.convertToRelativeFile(file, dir);
		assertEquals(new File("lib/toplink.jar"), relativeFile);
	}

	private void verifyUnchangedRelativeFile(String fileName, String dirName) {
		File file = new File(fileName);
		File dir = new File(dirName);
		File relativeFile = FileTools.convertToRelativeFile(file, dir);
		assertEquals(file, relativeFile);
	}

	public void testConvertToAbsoluteFile() {
		String prefix = this.isExecutingOnWindows() ? "C:" : "";
		File file;
		File dir;
		File absoluteFile;

		if (this.isExecutingOnWindows()) {
			// on Windows, a drive must be specified for a file to be absolute (i.e. not relative)
			this.verifyUnchangedAbsoluteFile("C:/dir1/dir2/file.txt", "C:/dir1/dir2");
			// different drives
			this.verifyUnchangedAbsoluteFile("D:/dir1/dir2/file.txt", "C:/dir1/dir2");
		}
		this.verifyUnchangedAbsoluteFile(prefix + "/dir1/dir2/file.txt", prefix + "/dir1/dir2");
		this.verifyUnchangedAbsoluteFile(prefix + "/./dir1/dir2/file.txt", prefix + "/dir1/dir2");
		this.verifyUnchangedAbsoluteFile(prefix + "/dir1/dir2/../../dir1/dir2/file.txt", prefix + "/dir1/dir2");

		file = new File(".");
		dir = new File(prefix + "/dir1/dir2");
		absoluteFile = FileTools.convertToAbsoluteFile(file, dir);
		assertEquals(new File(prefix + "/dir1/dir2"), absoluteFile);

		file = new File("./file.txt");
		dir = new File(prefix + "/dir1/dir2");
		absoluteFile = FileTools.convertToAbsoluteFile(file, dir);
		assertEquals(new File(prefix + "/dir1/dir2/file.txt"), absoluteFile);

		file = new File("file.txt");
		dir = new File(prefix + "/dir1/dir2");
		absoluteFile = FileTools.convertToAbsoluteFile(file, dir);
		assertEquals(new File(prefix + "/dir1/dir2/file.txt"), absoluteFile);

		file = new File("../dir2/file.txt");
		dir = new File(prefix + "/dir1/dir2");
		absoluteFile = FileTools.convertToAbsoluteFile(file, dir);
		assertEquals(new File(prefix + "/dir1/dir2/file.txt"), absoluteFile);

		file = new File("dir3/dir4/dir5/file.txt");
		dir = new File(prefix + "/dir1/dir2");
		absoluteFile = FileTools.convertToAbsoluteFile(file, dir);
		assertEquals(new File(prefix + "/dir1/dir2/dir3/dir4/dir5/file.txt"), absoluteFile);

		file = new File("../../../file.txt");
		dir = new File(prefix + "/dir1/dir2/dir3/dir4/dir5");
		absoluteFile = FileTools.convertToAbsoluteFile(file, dir);
		assertEquals(new File(prefix + "/dir1/dir2/file.txt"), absoluteFile);

		// too many ".." directories will resolve to the root;
		// this is consistent with Windows and Linux command shells
		file = new File("../../../../../../../../file.txt");
		dir = new File(prefix + "/dir1/dir2");
		absoluteFile = FileTools.convertToAbsoluteFile(file, dir);
		assertEquals(new File(prefix + "/file.txt"), absoluteFile);

		file = new File("../file.txt");
		dir = new File(prefix + "/dir1/dir2/dir3");
		absoluteFile = FileTools.convertToAbsoluteFile(file, dir);
		assertEquals(new File(prefix + "/dir1/dir2/file.txt"), absoluteFile);

		file = new File("../../../dirA/dirB/dirC/file.txt");
		dir = new File(prefix + "/dir1/dir2/dir3/dir4/dir5");
		absoluteFile = FileTools.convertToAbsoluteFile(file, dir);
		assertEquals(new File(prefix + "/dir1/dir2/dirA/dirB/dirC/file.txt"), absoluteFile);

		file = new File("../../..");
		dir = new File(prefix + "/dir1/dir2/dir3/dir4/dir5");
		absoluteFile = FileTools.convertToAbsoluteFile(file, dir);
		assertEquals(new File(prefix + "/dir1/dir2"), absoluteFile);

		file = new File("lib/toplink.jar");
		dir = new File(prefix + "/My Documents/My Workspace/Project 1");
		absoluteFile = FileTools.convertToAbsoluteFile(file, dir);
		assertEquals(new File(prefix + "/My Documents/My Workspace/Project 1/lib/toplink.jar"), absoluteFile);
	}

	public void testFileNameIsReserved() {
		boolean expected = this.isExecutingOnWindows();
		assertEquals(expected, FileTools.fileNameIsReserved("CON"));
		assertEquals(expected, FileTools.fileNameIsReserved("con"));
		assertEquals(expected, FileTools.fileNameIsReserved("cON"));
		assertEquals(expected, FileTools.fileNameIsReserved("AUX"));
		assertEquals(expected, FileTools.fileNameIsReserved("COM3"));
		assertEquals(expected, FileTools.fileNameIsReserved("LPT3"));
		assertEquals(expected, FileTools.fileNameIsReserved("nUL"));
		assertEquals(expected, FileTools.fileNameIsReserved("Prn"));
	}

	public void testFileHasAnyReservedComponents() {
		boolean expected = this.isExecutingOnWindows();
		assertEquals(expected, FileTools.fileHasAnyReservedComponents(new File("C:/CON")));
		assertEquals(expected, FileTools.fileHasAnyReservedComponents(new File("/con/foo")));
		assertEquals(expected, FileTools.fileHasAnyReservedComponents(new File("c:/temp/cON")));
		assertEquals(expected, FileTools.fileHasAnyReservedComponents(new File("bar//baz//AUX")));
		assertEquals(expected, FileTools.fileHasAnyReservedComponents(new File("COM3//ttt")));
		assertEquals(expected, FileTools.fileHasAnyReservedComponents(new File("d:/LPT3/xxx")));
		assertEquals(expected, FileTools.fileHasAnyReservedComponents(new File("c:/my docs and stuff/tuesday/nUL")));
		assertEquals(expected, FileTools.fileHasAnyReservedComponents(new File("Prn")));
	}

	public void testShortenFileNameFile() {
		if (this.isExecutingOnWindows()) {
			this.verifyShortenFileNameFileWin();
		} else {
			this.verifyShortenFileNameFileNonWin();
		}
	}

	private void verifyShortenFileNameFileWin() {
		File file = new File("C:\\Documents and Settings\\Administrator\\Desktop\\Project\\Text.txt");
		String fileName = FileTools.shortenFileName(file);
		assertEquals("C:\\Documents and Settings\\...\\Desktop\\Project\\Text.txt", fileName);
		assertTrue(fileName.length() <= FileTools.MAXIMUM_SHORTENED_FILE_NAME_LENGTH);

		file = new File("C:/");
		fileName = FileTools.shortenFileName(file);
		assertEquals("C:\\", fileName);
		assertTrue(fileName.length() <= FileTools.MAXIMUM_SHORTENED_FILE_NAME_LENGTH);
	}

	private void verifyShortenFileNameFileNonWin() {
		File file = new File(	"/home/administrator/documents and settings/desktop/Project/Text.txt");
		String fileName = FileTools.shortenFileName(file);
		assertEquals("/home/administrator/.../desktop/Project/Text.txt", fileName);
		assertTrue(fileName.length() <= FileTools.MAXIMUM_SHORTENED_FILE_NAME_LENGTH);

		file = new File("/home");
		fileName = FileTools.shortenFileName(file);
		assertEquals("/home", fileName);
		assertTrue(fileName.length() <= FileTools.MAXIMUM_SHORTENED_FILE_NAME_LENGTH);
	}

	public void testShortenFileNameFileInt() {
		if (this.isExecutingOnWindows()) {
			this.verifyShortenFileNameFileIntWin();
		} else {
			this.verifyShortenFileNameFileIntNonWin();
		}
	}

	private void verifyShortenFileNameFileIntWin() {
		File file = new File("C:\\Documents and Settings\\Administrator\\Desktop\\Project\\Text.txt");
		String fileName = FileTools.shortenFileName(file, 31);
		assertEquals("C:\\...\\Desktop\\Project\\Text.txt", fileName);
		assertEquals(31, fileName.length());

		file = new File("C:/This is the file name.txt");
		fileName = FileTools.shortenFileName(file, 10);
		assertEquals("C:\\This is the file name.txt", fileName);
		assertEquals(28, fileName.length());
	}

	private void verifyShortenFileNameFileIntNonWin() {
		File file = new File(	"/home/administrator/documents and settings/desktop/Project/Text.txt");
		String fileName = FileTools.shortenFileName(file, 31);
		assertEquals("/home/.../desktop/Project/Text.txt", fileName);
		assertEquals(34, fileName.length());

		file = new File("/This is the file name.txt");
		fileName = FileTools.shortenFileName(file, 10);
		assertEquals("/This is the file name.txt", fileName);
		assertEquals(26, fileName.length());
	}

	public void testShortenFileNameURL() throws Exception {
		if (this.isExecutingOnWindows()) {
			this.verifyShortenFileNameURLWin();
		} else {
			this.verifyShortenFileNameURLNonWin();
		}
	}

	private void verifyShortenFileNameURLWin() throws Exception {
		URL url = new URL("file", "", "C:/Documents and Settings/Administrator/Desktop/Project/Text.txt");
		String fileName = FileTools.shortenFileName(url);
		assertEquals("C:\\Documents and Settings\\...\\Desktop\\Project\\Text.txt", fileName);
		assertTrue(fileName.length() <= FileTools.MAXIMUM_SHORTENED_FILE_NAME_LENGTH);
	}

	private void verifyShortenFileNameURLNonWin() throws Exception {
		URL url = new URL("file", "", "/home/administrator/documents and settings/desktop/Project/Text.txt");
		String fileName = FileTools.shortenFileName(url);
		assertEquals("/home/administrator/.../desktop/Project/Text.txt", fileName);
		assertTrue(fileName.length() <= FileTools.MAXIMUM_SHORTENED_FILE_NAME_LENGTH);
	}

	public void testShortenFileNameURLInt() throws Exception {
		if (this.isExecutingOnWindows()) {
			this.verifyShortenFileNameURLIntWin();
		} else {
			this.verifyShortenFileNameURLIntNonWin();
		}
	}

	private void verifyShortenFileNameURLIntWin() throws Exception {
		URL url = new URL("file", "", "/C:/Documents and Settings/Administrator/Desktop/Project/Text.txt");
		String fileName = FileTools.shortenFileName(url, 31);
		assertEquals("C:\\...\\Desktop\\Project\\Text.txt", fileName);
		assertEquals(31, fileName.length());
	}

	private void verifyShortenFileNameURLIntNonWin() throws Exception {
		URL url = new URL("file", "", "/home/administrator/documents and settings/desktop/Project/Text.txt");
		String fileName = FileTools.shortenFileName(url, 31);
		assertEquals("/home/.../desktop/Project/Text.txt", fileName);
		assertEquals(34, fileName.length());
	}

	private void verifyUnchangedAbsoluteFile(String fileName, String dirName) {
		File file = new File(fileName);
		File dir = new File(dirName);
		File absoluteFile = FileTools.convertToAbsoluteFile(file, dir);
		assertEquals(file, absoluteFile);
	}

	private File buildTempDir() throws IOException {
		// build a new directory for each test, to prevent any cross-test effects
		File dir = FileTools.newTemporaryDirectory(ClassTools.shortClassNameForObject(this) + "." + this.getName());
	
		File file0a = new File(dir, "file0a");
		file0a.createNewFile();
		File file0b = new File(dir, "file0b");
		file0b.createNewFile();
		File file0c = new File(dir, "file0c");
		file0c.createNewFile();
	
		File subdir1 = new File(dir, "subdir1");
		subdir1.mkdir();
		File file1a = new File(subdir1, "file1a");
		file1a.createNewFile();
		File file1b = new File(subdir1, "file1b");
		file1b.createNewFile();
	
		File subdir2 = new File(dir, "subdir2");
		subdir2.mkdir();
		File file2a = new File(subdir2, "file2a");
		file2a.createNewFile();
		File file2b = new File(subdir2, "file2b");
		file2b.createNewFile();
	
		File subdir3 = new File(subdir2, "subdir3");
		subdir3.mkdir();
		File file3a = new File(subdir3, "file3a");
		file3a.createNewFile();
		File file3b = new File(subdir3, "file3b");
		file3b.createNewFile();
	
		return dir;
	}
	
	private void deleteDir(File dir) {
		FileTools.deleteDirectory(dir);
	}
	
}
