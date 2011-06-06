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
package org.eclipse.persistence.sdo.helper;

import org.eclipse.persistence.sdo.SDOType;

/**
 * <p><b>Purpose</b>: This class maintains information about a generated class and its corresponding interface
 * <p><b>Responsibilities</b>:<ul>
 * <li> Hold the StringBuffer contents of the class and interface
 * <li> When the close() method is called the attribute and method buffers are appended to the classbuffer
 * and both the interface buffer and class buffer have the closing brace appended
 * </ul>
 */
public class ClassBuffer {
    private static final String lsep = System.getProperty("line.separator");
    private static final String lsep2 = lsep + lsep;
    private String indent = "";
    private StringBuffer attributeBuffer;
    private StringBuffer methodBuffer;
    private StringBuffer classBuffer;
    private StringBuffer interfaceBuffer;
    private String interfaceName;//the generated interface name if applicable
    private String className;//the name of the generated class
    private String packageName;//the java package of the generated class and interface
    private String uri;//the uri the sdo type corresponding to this class
    private String sdoTypeName;//the sdo type name corresponding to this class
    private boolean closed;
    private boolean generateInterface;
    private SDOType sdoType;
    private SDOClassGeneratorListener sdoClassGeneratorListener;

    public ClassBuffer(SDOClassGeneratorListener sdoClassGeneratorListener) {
        setAttributeBuffer(new StringBuffer());
        setMethodBuffer(new StringBuffer());
        setClassBuffer(new StringBuffer());
        setInterfaceBuffer(new StringBuffer());
        this.sdoClassGeneratorListener = sdoClassGeneratorListener;
    }

    public void setAttributeBuffer(StringBuffer attributeBuffer) {
        this.attributeBuffer = attributeBuffer;
    }

    public StringBuffer getAttributeBuffer() {
        return attributeBuffer;
    }

    public void setMethodBuffer(StringBuffer methodBuffer) {
        this.methodBuffer = methodBuffer;
    }

    public StringBuffer getMethodBuffer() {
        return methodBuffer;
    }

    public void setClassBuffer(StringBuffer classBuffer) {
        this.classBuffer = classBuffer;
    }

    public StringBuffer getClassBuffer() {
        return classBuffer;
    }

    public void setInterfaceBuffer(StringBuffer interfaceBuffer) {
        this.interfaceBuffer = interfaceBuffer;
    }

    public StringBuffer getInterfaceBuffer() {
        return interfaceBuffer;
    }

    public void close() {
        if (!closed) {
            if (sdoClassGeneratorListener != null) {
                sdoClassGeneratorListener.preImplAttributes(classBuffer);
            }
            classBuffer.append(attributeBuffer);
            classBuffer.append(methodBuffer);

            classBuffer.append(lsep).append(this.indent).append("}").append(lsep2);
            closed = true;

            if (isGenerateInterface()) {
                interfaceBuffer.append(lsep).append(this.indent).append("}").append(lsep2);
            }
        }
    }

    public void setGenerateInterface(boolean generateInterface) {
        this.generateInterface = generateInterface;
    }

    public boolean isGenerateInterface() {
        return generateInterface;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setSdoTypeName(String sdoTypeName) {
        this.sdoTypeName = sdoTypeName;
    }

    public String getSdoTypeName() {
        return sdoTypeName;
    }

    public void setSdoType(SDOType sdoType) {
        this.sdoType = sdoType;
    }

    public SDOType getSdoType() {
        return sdoType;
    }
}
