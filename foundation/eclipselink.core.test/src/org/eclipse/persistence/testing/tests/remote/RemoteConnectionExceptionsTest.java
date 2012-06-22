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
package org.eclipse.persistence.testing.tests.remote;

import java.util.*;

import java.lang.reflect.*;

import org.eclipse.persistence.internal.sessions.remote.RemoteConnection;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.testing.framework.*;

public class RemoteConnectionExceptionsTest extends TestCase {

    public RemoteConnectionExceptionsTest(int mode, Class remoteConnectionClass) {
        this.remoteConnectionClass = remoteConnectionClass;
        generator = new TransporterGenerator(mode);
        setNameAndCheckMode();
        setNamesToExclude();
        setMethodsAndArgs();
        setKnownBugs();
    }

    public RemoteConnectionExceptionsTest(int mode, 
                                          String remoteConnectionClassName) throws ClassNotFoundException {
        this(mode, Class.forName(remoteConnectionClassName));
    }

    protected Class remoteConnectionClass;
    private RemoteConnection remoteConnection;
    protected TransporterGenerator generator;
    protected Object[] results;
    protected Throwable[] exceptions;

    protected Hashtable knownBugs;
    protected Vector namesToExclude;
    protected Vector methods;
    protected Vector args;

    protected void setRemoteConnection(RemoteConnection remoteConnection) {
        Class cls = remoteConnection.getClass();
        if (!remoteConnectionClass.equals(cls)) {
            throw new TestProblemException("remoteConnection's type is different from the type used to create the test");
        }
        this.remoteConnection = remoteConnection;
    }

    protected RemoteConnection getRemoteConnection() {
        return remoteConnection;
    }

    protected void setNamesToExclude() {
        namesToExclude = new Vector();
        String namesToExcludeArray[] = 
        { "createProxySession", "createRemoteSession", "cursoredStreamNextPage", "fixObjectReferences", 
          "getRemoteSessionController", "setRemoteSessionController", };
        for (int i = 0; i < namesToExcludeArray.length; i++) {
            namesToExclude.add(namesToExcludeArray[i]);
        }
    }

    protected void setNameAndCheckMode() throws TestProblemException {
        int mode = generator.getMode();
        String className = remoteConnectionClass.getName();
        if (mode == TransporterGenerator.THROW_REMOTE_EXCEPTION) {
            setName(getName().concat(" THROW_REMOTE_EXCEPTION"));
            setDescription("Verifies that CommunicationException are thrown by methods of " + className + 
                           " class");
        } else if (mode == TransporterGenerator.SET_EXCEPTION_INTO_TRANSPORTER) {
            setName(getName().concat(" SET_EXCEPTION_INTO_TRANSPORTER"));
            setDescription("Verifies that Transporter.getException() are thrown by methods of " + className + 
                           " class");
        } else {
            throw new TestProblemException("Unsupported mode");
        }
    }

    protected void setMethodsAndArgs() {
        methods = new Vector();
        args = new Vector();
        Method[] allMethods = remoteConnectionClass.getMethods();
        for (int i = 0; i < allMethods.length; i++) {
            Method method = allMethods[i];
            if (!method.getDeclaringClass().equals(remoteConnectionClass)) {
                continue;
            }
            if (!Modifier.isPublic(method.getModifiers())) {
                continue;
            }
            String name = method.getName();
            if (namesToExclude != null && namesToExclude.contains(name)) {
                continue;
            }
            methods.add(method);

            Class[] types = method.getParameterTypes();
            Object[] params = new Object[types.length];
            for (int j = 0; j < types.length; j++) {
                Class type = types[j];
                if (type.isPrimitive()) {
                    params[j] = getWrapperClassInstance(type);
                }
            }
            args.add(params);
        }
    }

    protected void setKnownBugs() {
        knownBugs = new Hashtable();
        if (generator.getMode() == TransporterGenerator.SET_EXCEPTION_INTO_TRANSPORTER) {
            for (int i = 0; i < methods.size(); i++) {
                Method method = (Method)methods.elementAt(i);
                Object[] params = (Object[])args.elementAt(i);
                String name = method.getName();
                if (name.equals("cursorSelectObjects")) {
                    if (params.length == 2) {
                        knownBugs.put(method, "bug 2909926");
                    }
                }
            }
        }
    }

    protected String getNameToBind() {
        return this.getClass().getName();
    }

    protected boolean isCorrect(int i) {
        Throwable exception = exceptions[i];
        boolean ok;
        int mode = generator.getMode();
        if (mode == TransporterGenerator.THROW_REMOTE_EXCEPTION) {
            ok = exception != null;
            if (ok) {
                ok = CommunicationException.class.isInstance(exception);
                if (ok) {
                    ok = 
 ((CommunicationException)exception).getErrorCode() == CommunicationException.ERROR_IN_INVOCATION;
                }
            }
            if (!ok) {
                if (((Method)methods.elementAt(i)).getName().equals("isConnected")) {
                    if (Boolean.class.isInstance(results[i])) {
                        ok = !((Boolean)results[i]).booleanValue();
                    }
                }
            }
        } else if (mode == TransporterGenerator.SET_EXCEPTION_INTO_TRANSPORTER) {
            ok = exception != null;
            if (ok) {
                ok = TestException.class.isInstance(exception);
                if (ok) {
                    ok = ((TestException)exception).getMessage().endsWith(generator.getMessage());
                }
            }
        } else {
            throw new TestException("Unsupported mode");
        }
        return ok;
    }

    public void test() throws Exception {
        results = new Object[methods.size()];
        exceptions = new Throwable[methods.size()];
        for (int i = 0; i < methods.size(); i++) {
            Method method = (Method)methods.elementAt(i);
            Object[] params = (Object[])args.elementAt(i);
            String name = method.getName();
            Object result = null;
            Throwable exception = null;
            try {
                result = method.invoke(getRemoteConnection(), params);
            } catch (InvocationTargetException ex) {
                exception = ex.getTargetException();
            }
            results[i] = result;
            exceptions[i] = exception;
        }
    }

    public void verify() {
        boolean errorHasOccured = false;
        String errorMessage = new String("Wrong exception/result thrown/returned by methods:");
        String unknownBug = new String("!!!UNKNOWN BUG!!!");
        for (int i = 0; i < methods.size(); i++) {
            Method method = (Method)methods.elementAt(i);
            String name = method.getName();
            if (!isCorrect(i)) {
                errorHasOccured = true;
                errorMessage = errorMessage.concat(" ");
                errorMessage = errorMessage.concat(name);
                errorMessage = errorMessage.concat("(");
                String bug = unknownBug;
                if (knownBugs != null && !knownBugs.isEmpty()) {
                    String knownBug = (String)knownBugs.get(methods.elementAt(i));
                    if (knownBug != null) {
                        bug = knownBug;
                    }
                }
                errorMessage = errorMessage.concat(bug);
                errorMessage = errorMessage.concat(")");
                errorMessage = errorMessage.concat(";");
            }
        }
        if (errorHasOccured) {
            throw new TestErrorException(errorMessage);
        }
    }

    public static Object getWrapperClassInstance(Class cls) {
        if (Integer.TYPE.equals(cls)) {
            return new Integer(0);
        } else if (Boolean.TYPE.equals(cls)) {
            return new Boolean(false);
        } else if (Character.TYPE.equals(cls)) {
            return new Character(' ');
        } else if (Byte.TYPE.equals(cls)) {
            byte b = 0;
            return new Byte(b);
        } else if (Short.TYPE.equals(cls)) {
            short s = 0;
            return new Short(s);
        } else if (Long.TYPE.equals(cls)) {
            return new Long(0);
        } else if (Float.TYPE.equals(cls)) {
            return new Float(0);
        } else if (Double.TYPE.equals(cls)) {
            return new Double(0);
        } else {
            return null;
        }
    }
}
