/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - May 05 2010
 *       fix for https://bugs.eclipse.org/bugs/show_bug.cgi?id=307897
 ******************************************************************************/

package org.eclipse.persistence.tools.dbws;

//javase imports
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.Arrays;

//java eXtension imports
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject.Kind;

/**
 * <p>
 * <b>PRIVATE:</b> DBWSProviderCompiler wraps a {@link javax.tools.JavaCompiler}.
 * Only supports compiling a single file <tt>DBWSProvider.java</tt> in-memory.
 * 
 * @author mnorman
 *
 */
public class DBWSProviderCompiler {

    static final String PROVIDER_NAME = "_dbws.DBWSProvider";
    static final Iterable<String> OPTIONS = Arrays.asList("-g:source,lines,vars");

    protected JavaCompiler compiler;
    protected DiagnosticCollector<JavaFileObject> diagnosticsCollector;
    protected JavaFileManager fileManager;

    public DBWSProviderCompiler() {
        compiler = ToolProvider.getSystemJavaCompiler();
        diagnosticsCollector = new DiagnosticCollector<JavaFileObject>();
    }

    public JavaCompiler getCompiler() {
        return compiler;
    }

    public DiagnosticCollector<JavaFileObject> getDiagnosticsCollector() {
        return diagnosticsCollector;
    }
    
    public byte[] compile(CharSequence source) {
        JavaFileObject dbwsProviderSource = 
            new JavaSourceFromString(PROVIDER_NAME, source.toString());
        ByteArrayJavaFileObject dbwsProviderClass = 
            new ByteArrayJavaFileObject(PROVIDER_NAME);
        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(dbwsProviderSource);
        ByteArrayFileManager bafm = new ByteArrayFileManager(
            compiler.getStandardFileManager(null, null, null), dbwsProviderClass);
        StringWriter sw = new StringWriter();
        CompilationTask task = compiler.getTask(sw, bafm, diagnosticsCollector, OPTIONS, null,
            compilationUnits);
        task.call();
        return dbwsProviderClass.toByteArray();
    }
    
    // wrapper class - not really a 'FileObject', uses in-memory string 'source'
    class JavaSourceFromString extends SimpleJavaFileObject {
        CharSequence source;
        JavaSourceFromString(String name, CharSequence source) {
            // URI trick from SimpleJavaFileObject constructor - it only recognizes
            // ('file:/' or 'jar:/'); anything else forces the 'getCharContent' callback for Kind.SOURCE
            super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension),
                Kind.SOURCE);
            this.source = source;
        }
        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return source;
        }
    }

    // wrapper class - not really a 'FileObject', uses in-memory ByteArrayOutputStream 'bos'
    class ByteArrayJavaFileObject extends SimpleJavaFileObject {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        public ByteArrayJavaFileObject(String name) {
            // URI trick from SimpleJavaFileObject constructor - it only recognizes
            // ('file:' or 'jar:'); anything else forces the 'openOutputStream' callback for Kind.CLASS
            super(URI.create("string:///" + name.replace('.', '/') + Kind.CLASS.extension),
                Kind.CLASS);
        }
        public byte[] toByteArray() {
            return bos.toByteArray();
        }
        @Override
        public OutputStream openOutputStream() throws IOException {
            return bos;
        }
    }
    // wrapper class - compiler will use this 'FileManager' to manage compiler output
    class ByteArrayFileManager extends ForwardingJavaFileManager<JavaFileManager> {
        ByteArrayJavaFileObject bajfo;
        ByteArrayFileManager(StandardJavaFileManager sjfm, ByteArrayJavaFileObject bajfo) {
            super(sjfm);
            this.bajfo = bajfo;
        }
        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String name, Kind kind,
            FileObject sibling) throws IOException {
            return bajfo;
        }
    }
}