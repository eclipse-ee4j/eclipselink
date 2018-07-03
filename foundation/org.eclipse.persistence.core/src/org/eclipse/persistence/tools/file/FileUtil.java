/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;

/**
 * INTERNAL:
 *
 * <b>Purpose</b>: Provide common file I/O utilities
 * @author Steven Vo
 * @since TopLink 4.5
 */
public class FileUtil {

    /* Copy file to another file or copy content of a directory to another directory
     *
     * @inputPath: path to a file or directory
     * @outputPath: path to a file or directory
     * @filteredExtensions: filter files that end with specified strings i.e {".java", ".class", ".xml"}
     */
    public static void copy(String inputPath, String outputPath, String[] filteredExtensions) throws IOException {
        File inputPathFile = new File(inputPath);

        if (!inputPathFile.exists()) {
            return;
        }

        File outputPathFile = new File(outputPath);

        if (!outputPathFile.exists()) {
            if (outputPathFile.isDirectory()) {
                if (!outputPathFile.mkdirs()) {
                    AbstractSessionLog.getLog().log(SessionLog.FINE, SessionLog.MISC,
                            "Cannot create directory '{0}'", new Object[] {outputPathFile}, false);
                }
            } else {
                if (!outputPathFile.getParentFile().mkdirs()) {
                    AbstractSessionLog.getLog().log(SessionLog.FINE, SessionLog.MISC,
                            "Cannot create directory '{0}'", new Object[] {outputPathFile}, false);
                }
            }
        }

        Vector files = findFiles(inputPath, filteredExtensions);

        for (int i = 0; i < files.size(); i++) {
            File in = (File)files.elementAt(i);

            String outFilePath = in.getAbsolutePath().substring(inputPath.length());
            outFilePath = outputPath + File.separator + outFilePath;

            File out = new File(outFilePath);
            File parent = new File(out.getParent());

            if (!parent.exists()) {
                if (!parent.mkdirs()) {
                    AbstractSessionLog.getLog().log(SessionLog.FINE, SessionLog.MISC,
                            "Cannot create directory '{0}'", new Object[] {outputPathFile}, false);
                }
            }

            copy(new FileInputStream(in), new FileOutputStream(out));
        }
    }

    /* Copy an input stream to an output stream
     *
     * @in:  input stream
     * @out: output stream
     */
    public static void copy(InputStream in, OutputStream out) throws IOException {
        try (InputStream i = in; OutputStream  o = out;) {
            byte[] buffer = new byte[512];
            while (true) {
                int bytesRead = i.read(buffer);
                if (bytesRead == -1) {
                    break;
                }

                o.write(buffer, 0, bytesRead);
            }
        }
    }

    /* Jar a given directory
     *
     * @jarFileName: path of the output jar
     * @jarDirectory: path to the input directory
     * @filteredExtensions: filter files that end with specified strings i.e {".java", ".class", ".xml"}
     */
    public static void createJarFromDirectory(String jarFileName, String jarDirectory, String[] filtertedExtensions) throws IOException {
        File directory = new File(jarDirectory);
        if (!directory.exists()) {
            return;
        }

        File jar = new File(jarFileName);
        if (!jar.exists()) {
            if (!jar.getParentFile().mkdirs()) {
                AbstractSessionLog.getLog().log(SessionLog.FINE, SessionLog.MISC,
                        "Cannot create directory '{0}'", new Object[] {jar.getParentFile()}, false);
            }
        }
        JarOutputStream jarOut = null;
        try {
            jarOut = new JarOutputStream(new FileOutputStream(jar), new Manifest());
            Vector files = findFiles(jarDirectory, filtertedExtensions);

            for (int i = 0; i < files.size(); i++) {
                File file = (File)files.elementAt(i);

                String relativePathToDirectory = file.getAbsolutePath().substring(directory.getAbsolutePath().length() + 1);
                String entryName = relativePathToDirectory.replace('\\', '/');

                FileInputStream inStream = null;
                ByteArrayOutputStream byteStream = null;

                try {
                    inStream = new FileInputStream(file);
                    byteStream = new ByteArrayOutputStream();

                    int length = 0;
                    byte[] buffer = new byte[1024];
                    while ((length = inStream.read(buffer)) > 0) {
                        byteStream.write(buffer, 0, length);
                    }
                    byte[] arr = byteStream.toByteArray();

                    JarEntry meta = new JarEntry(entryName);
                    jarOut.putNextEntry(meta);
                    meta.setSize(arr.length);
                    meta.setCompressedSize(arr.length);
                    CRC32 crc = new CRC32();
                    crc.update(arr);
                    meta.setCrc(crc.getValue());
                    meta.setMethod(ZipEntry.STORED);
                    jarOut.write(arr, 0, arr.length);
                    jarOut.closeEntry();
                } finally {
                    Helper.close(byteStream);
                    Helper.close(inStream);
                }
            }
        } finally {
            Helper.close(jarOut);
        }
    }

    /*
     * Return vector of all Files contained in a path.
     *
     * @filteredExtensions: filter files that end with specified strings i.e {".java", ".class", ".xml"}
     *      If filteredExtensions == null or empty then then return all instances of File contained in the directory and its sub directories
     * @Path: a directory path or a file path.
     *      If it's file path then return a single instance of File
     *      If it's directory path then return all instances of File contained in the directory and its sub directories
     */
    public static Vector findFiles(String path, String[] filteredExtensions) {
        Vector files = new Vector();

        findFilesHelper(new File(path), filteredExtensions, files);
        return files;
    }

    /*
     * INTERNAL: traverse the directory to find all files with filtered extensions.  The result is passed
     * around for each recursive call
     */
    private static void findFilesHelper(File file, String[] filteredExtensions, Vector result) {
        if (!file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            String[] entries = file.list();

            if (entries != null) {
                for (int i = 0; i < entries.length; i++) {
                    findFilesHelper(new File(file, entries[i]), filteredExtensions, result);
                }
            }
        } else {
            // add everything if no filtered extension
            if ((filteredExtensions == null) || (filteredExtensions.length == 0)) {
                result.addElement(file);
                return;
            }

            // add only filtered extensions
            for (int i = 0; i < filteredExtensions.length; i++) {
                if (file.getName().endsWith(filteredExtensions[i])) {
                    result.addElement(file);
                    return;
                }
            }
        }
    }

    /*
     * Delete a file or directory
     *
     * @File: directory or file
     */
    public static void delete(File file) {
        Objects.requireNonNull(file);
        if (!file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            String[] entries = file.list();

            if (entries == null || entries.length == 0) {
                if (!file.delete()) {
                    AbstractSessionLog.getLog().log(SessionLog.FINE, SessionLog.MISC,
                            "Cannot delete file '{0}'.", new Object[] {file}, false);
                }

            } else {
                for (int i = 0; i < entries.length; i++) {
                    delete(new File(file, entries[i]));
                }

                // delete directory after its containing files were deleted
                String[] content = file.list();
                if (content == null || content.length == 0) {
                    if (!file.delete()) {
                        AbstractSessionLog.getLog().log(SessionLog.FINE, SessionLog.MISC,
                                "Cannot delete file '{0}'.", new Object[] {file}, false);
                    }
                }
            }
        } else {
            if (!file.delete()) {
                AbstractSessionLog.getLog().log(SessionLog.FINE, SessionLog.MISC,
                        "Cannot delete file '{0}'.", new Object[] {file}, false);
            }
        }
    }
}
