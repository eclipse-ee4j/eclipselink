/*******************************************************************************
 * Copyright (c) 2010-2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  dclarke - initial sig compare utility (Bug 352151)
 ******************************************************************************/
package eclipselink.utils.sigcompare;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassSignature {

    private String name;

    private String[] interfaces;

    private String parentName;

    private ClassSignature parent;

    private Map<String, List<String>> methods;

    private Map<String, String> fields;

    /**
     * 
     */
    public static final String SEPARATOR = "::";

    public ClassSignature(String name, String parentName, String[] interfaces) {
        this.name = name;
        this.interfaces = interfaces;
        this.parentName = parentName;
        this.methods = new HashMap<String, List<String>>();
        this.fields = new HashMap<String, String>();
    }

    public String getName() {
        return name;
    }

    public String[] getInterfaces() {
        return interfaces;
    }

    public void initialzeParent(Map<String, ClassSignature> sigs) {
        this.parent = sigs.get(this.parentName);
    }

    public void addMethod(String name, String desc) {
        List<String> methodDescs = this.methods.get(name);

        if (methodDescs == null) {
            methodDescs = new ArrayList<String>();
            this.methods.put(name, methodDescs);
        }
        methodDescs.add(desc);
    }

    public void addField(String name, String desc) {
        if (this.fields.containsKey(name)) {
            throw new RuntimeException("Duplicate field: " + name + " on class: " + getName());
        }
        this.fields.put(name, desc);
    }

    public boolean containsField(String fieldName, String desc) {
        String fieldDesc = this.fields.get(fieldName);
        return desc.equals(fieldDesc);
    }

    public boolean containsMethod(String methodName, String desc) {
        List<String> methodDescs = this.methods.get(methodName);

        if (methodDescs != null) {
            if (methodDescs.contains(desc)) {
                return true;
            }
        }

        if (this.parent != null) {
            return this.parent.containsMethod(methodName, desc);
        }
        return false;
    }

    public static String newline = System.getProperty("line.separator");

    public void compare(ClassSignature targetSig, Writer writer, ExcludePatterns excludes) throws IOException {
        
        for (Map.Entry<String, String> fieldEntry : this.fields.entrySet()) {
            if (!excludes.exclude(getName(), fieldEntry.getKey(), fieldEntry.getValue()) && !targetSig.containsField(fieldEntry.getKey(), fieldEntry.getValue())) {
                writer.write(getName() + SEPARATOR + fieldEntry.getKey() + SEPARATOR + fieldEntry.getValue() + newline);
            }
        }

        for (Map.Entry<String, List<String>> methodEntry : this.methods.entrySet()) {
            for (String desc : methodEntry.getValue()) {
                if (!excludes.exclude(getName(), methodEntry.getKey(), desc) && !targetSig.containsMethod(methodEntry.getKey(), desc)) {
                    writer.write(getName() + SEPARATOR + methodEntry.getKey() + SEPARATOR + desc + newline);
                }
            }
        }
    }

}
