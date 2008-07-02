 /**
 * Created on Jan 20, 2005
 * @author mnorman
 * 
 * Derived from org.objectweb.asm.commons.SerialVersionUIDAdder by
 * Rajendra Inamdar, Vishal Vishnoi
 * The serialVersionUID algorithm can be found in the Sun documentation
 * <a href="http://java.sun.com/j2se/1.4.2/docs/guide/serialization/spec/class.html"
 * http://java.sun.com/j2se/1.4.2/docs/guide/serialization/spec/class.html</a>
 * 
 * To conform with the ASM and asm.commons licensing ...
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2004 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.eclipse.persistence.internal.libraries.asm.commons;

import java.io.*;
import java.security.MessageDigest;
import java.util.*;

import org.eclipse.persistence.internal.libraries.asm.*;

/**
 * Helper class for generating a compatible serialization SUID.
 */
public class SerialVersionUIDAdder extends ClassAdapter {

    protected boolean computeSVUID;
    protected boolean hasSVUID;
    protected long ownSVUID = 0L;

    protected int access;
    protected String className;
    protected String[] interfaces;
    protected Collection svuidFields;
    protected boolean hasStaticInitializer;
    protected Collection svuidConstructors;
    protected Collection svuidMethods;

    public SerialVersionUIDAdder(ClassVisitor cv) {
        super(cv);
        svuidFields = new ArrayList();
        svuidConstructors = new ArrayList();
        svuidMethods = new ArrayList();
    }
       
    public void visit(final int version, final int access, final String name, final String superName, final String[] interfaces, final String sourceFile) {
        computeSVUID = (access & Constants.ACC_INTERFACE) == 0;
        if (computeSVUID) {
            this.className = name;
            this.access = access;
            this.interfaces = interfaces;
        }
    }
    
    public CodeVisitor visitMethod(final int access, final String methodName, final String desc, final String[] exceptions, final Attribute attrs) {
        if (computeSVUID) {
            if (methodName.equals("<clinit>")) {
                hasStaticInitializer = true;
            }
            int mods = access & (Constants.ACC_PUBLIC | Constants.ACC_PRIVATE | Constants.ACC_PROTECTED | Constants.ACC_STATIC | Constants.ACC_FINAL | Constants.ACC_SYNCHRONIZED | Constants.ACC_NATIVE | Constants.ACC_ABSTRACT | Constants.ACC_STRICT);
            if ((access & Constants.ACC_PRIVATE) == 0) {
                if (methodName.equals("<init>")) {
                    svuidConstructors.add(new Item(methodName, mods, desc));
                } else if (!methodName.equals("<clinit>")) {
                    svuidMethods.add(new Item(methodName, mods, desc));
                }
            }
        }
        return null;
    }

    public void visitField(final int access, final String fieldName, final String desc, final Object value, final Attribute attrs) {
        if (computeSVUID) {
            if (fieldName.equals("serialVersionUID")) {
                // since the class already has SVUID, we won't be computing it.
                computeSVUID = false;
                hasSVUID = true;
                ownSVUID = ((Long)value).longValue();
            }
            int mods = access & (Constants.ACC_PUBLIC | Constants.ACC_PRIVATE | Constants.ACC_PROTECTED | Constants.ACC_STATIC | Constants.ACC_FINAL | Constants.ACC_VOLATILE | Constants.ACC_TRANSIENT);
            if (((access & Constants.ACC_PRIVATE) == 0) || ((access & (Constants.ACC_STATIC | Constants.ACC_TRANSIENT)) == 0)) {
                svuidFields.add(new Item(fieldName, mods, desc));
            }
        }
    }
    public boolean hasSVUID() {
    	//bug 218556 - PWK
    	return hasSVUID;
    }

    public long computeSVUID() {
        try {
            if (hasSVUID) {
                return ownSVUID;
            }
    
            ByteArrayOutputStream bos = null;
            DataOutputStream dos = null;
            long svuid = 0;
            try {
                bos = new ByteArrayOutputStream();
                dos = new DataOutputStream(bos);
                dos.writeUTF(className.replace('/', '.'));
                dos.writeInt(access & (Constants.ACC_PUBLIC | Constants.ACC_FINAL | Constants.ACC_INTERFACE | Constants.ACC_ABSTRACT));
                Arrays.sort(interfaces);
                for (int i = 0; i < interfaces.length; i++) {
                    dos.writeUTF(interfaces[i].replace('/', '.'));
                }
                writeItems(svuidFields, dos, false);
                if (hasStaticInitializer) {
                    dos.writeUTF("<clinit>");
                    dos.writeInt(Constants.ACC_STATIC);
                    dos.writeUTF("()V");
                }
                writeItems(svuidConstructors, dos, true);
                writeItems(svuidMethods, dos, true);
                dos.flush();
                MessageDigest md = MessageDigest.getInstance("SHA");
                byte[] hashBytes = md.digest(bos.toByteArray());
                for (int i = Math.min(hashBytes.length, 8) - 1; i >= 0; i--) {
                    svuid = (svuid << 8) | (hashBytes[i] & 0xFF);
                }
            } finally {
                if (dos != null) {
                    dos.close();
                }
            }
            return svuid;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private void writeItems(final Collection itemCollection, final DataOutputStream dos, final boolean dotted) throws IOException {
        int size = itemCollection.size();
        Item items[] = (Item[])itemCollection.toArray(new Item[size]);
        Arrays.sort(items);
        for (int i = 0; i < size; i++) {
            dos.writeUTF(items[i].name);
            dos.writeInt(items[i].access);
            dos.writeUTF(dotted ? items[i].desc.replace('/', '.') : items[i].desc);
        }
    }

    static class Item implements Comparable {
        String name;
        int access;
        String desc;

        Item(final String name, final int access, final String desc) {
            this.name = name;
            this.access = access;
            this.desc = desc;
        }

        public int compareTo(final Object o) {
            Item other = (Item)o;
            int retVal = name.compareTo(other.name);
            if (retVal == 0) {
                retVal = desc.compareTo(other.desc);
            }
            return retVal;
        }
    }
}
