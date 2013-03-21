/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      Oracle - initial impl
 ******************************************************************************/
package org.eclipse.persistence.sessions.serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.sessions.Session;

/**
 * Uses Kryo to serialize the object.
 * @author James Sutherland
 */
public class KryoSerializer implements Serializer {
    Object kryo;
    Constructor outputConstructor;
    Constructor inputConstructor;
    Method writeMethod;
    Method readMethod;
    Method inputCloseMethod;
    Method outputCloseMethod;
    
    public KryoSerializer() {
        try {
            Class kryoClass = Class.forName("com.esotericsoftware.kryo.Kryo");
            this.kryo = kryoClass.newInstance();
            Class inputClass = Class.forName("com.esotericsoftware.kryo.io.Input");
            this.inputConstructor = inputClass.getConstructor(InputStream.class);
            Class outputClass = Class.forName("com.esotericsoftware.kryo.io.Output");
            this.outputConstructor = outputClass.getConstructor(OutputStream.class);
            this.writeMethod = kryoClass.getMethod("writeClassAndObject", outputClass, Object.class);
            this.readMethod = kryoClass.getMethod("readClassAndObject", inputClass);
            this.inputCloseMethod = inputClass.getMethod("close");
            this.outputCloseMethod = outputClass.getMethod("close");
        } catch (Exception exception) {
            throw ValidationException.reflectiveExceptionWhileCreatingClassInstance("com.esotericsoftware.kryo.Kryo", exception);
        }
    }
    
    public byte[] serialize(Object object, Session session) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Object output = this.outputConstructor.newInstance(stream);
            this.writeMethod.invoke(this.kryo, output, object);
            this.outputCloseMethod.invoke(output);
            return stream.toByteArray();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
    
    public Object deserialize(byte[] bytes, Session session) {
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            Object input = this.inputConstructor.newInstance(stream);
            Object result = this.readMethod.invoke(this.kryo, input);
            this.inputCloseMethod.invoke(input);
            return result;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
    
    public String toString() {
        return getClass().getSimpleName();
    }
}
