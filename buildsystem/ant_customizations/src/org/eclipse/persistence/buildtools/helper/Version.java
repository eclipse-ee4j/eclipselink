/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Version
 *   class defines OSGi-like behavior to a version object
 *   it has an identifier (Major.Minor.Micro.Qualifier) and can compare itself to others
 *   it can report its identifier, and its identifier can be set
 *
 * Contributors:
 *     egwin - initial conception and implementation
 */

package org.eclipse.persistence.buildtools.helper;

import java.lang.NumberFormatException;

import org.eclipse.persistence.buildtools.helper.VersionException;;

public class Version {
    private String  identifier = "0.0";
    private Integer major = 0;
    private Integer minor = 0;
    private Integer micro = 0;
    private String  qualifier = "";

    private int[] indexList = {0,0,0};               //holds the indexes of the "."s in the identifier string.

    // helper
    private String validateIdentifier(String identifier) throws VersionException{
        String validated=identifier;
        boolean error=false;

        // clear leading whitespace
        while ( validated.startsWith(" ") || validated.startsWith("\t") ) 
            validated=validated.substring(1);
        // clear trailing whitespace
        while ( validated.endsWith(" ") || validated.endsWith("\t") ) 
            validated=validated.substring(0,validated.length() - 1);
        
        if ( validated.startsWith(".") ) {
            throw new VersionException("Initial version tokenizer found (.): Invalid version string '" + validated + "'.");
        }
        else {
            if( validated.endsWith(".") ) {
                throw new VersionException("Terminating version tokenizer found (.): Invalid version string '" + validated + "'.");
            }
            else {
                int index = 0;
                int subindex = 0;
                int count = 0;
                int maxindex = validated.lastIndexOf(".");
                while( !error && ( (subindex = validated.substring(index).indexOf(".")) >0 || index<maxindex )) {
                    if(count>2 && subindex>0)
                        throw new VersionException("Maximum (4) tokens exceeded: Invalid version string '" + validated + "'.");
                    else {
                        index += subindex;
                        indexList[count] = index;
                        index++;                //increment to point to char after found '.'
                        count++;
                    }
                }
            }
        }
        return validated;
    }

    // setter
    private void setMajor(Integer major) {
        this.major = major;
    }
    private void setMinor(Integer minor) {
        this.minor = minor;
    }
    private void setMicro(Integer micro) {
        this.micro = micro;
    }
    private void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    private void setAll(String identifier) throws VersionException{
        // assumes basic validation completed
        try {
            if( identifier != "" ){
                if( indexList[0] > 0 ) {
                    setMajor(Integer.valueOf( identifier.substring(0, indexList[0])));
                    if( indexList[1] > 0 ) {
                        setMinor(Integer.valueOf( identifier.substring(indexList[0]+1, indexList[1])));
                        if( indexList[2] > 0 ) {
                            setMicro(Integer.valueOf( identifier.substring(indexList[1]+1, indexList[2])));
                            setQualifier(identifier.substring(indexList[2]+1));
                        }
                        else
                            setMicro(Integer.valueOf( identifier.substring(indexList[1]+1)));
                    }
                    else
                        setMinor(Integer.valueOf( identifier.substring(indexList[0]+1) ));
                }
                else
                    setMajor(Integer.valueOf(identifier));
            }
        } catch ( NumberFormatException e){
            throw new VersionException("Major.Minor.Micro tokens expected to be numeric. One or more is invalid. " + e.getMessage() + " in \"" + identifier + "\".", e);
        }
        String validIdentifier = getMajorStr() + "." + getMinorStr() + "." + getMicroStr();
        if(getQualifier() != null && getQualifier() != "" ) {
            validIdentifier+= "." + getQualifier();
        }
        this.identifier = validIdentifier;
    }

    // Public methods
    //     constructors
    public Version() {
    }
    public Version(String identifier) {
        setIdentifier(identifier);
    }

    // getters
    public Integer getMajorInt() {
        return this.major;
    }
    public Integer getMinorInt() {
        return this.minor;
    }
    public Integer getMicroInt() {
        return this.micro;
    }
    public String getMajorStr() {
        return this.major.toString();
    }
    public String getMinorStr() {
        return this.minor.toString();
    }
    public String getMicroStr() {
        return this.micro.toString();
    }
    public String getQualifier() {
        return this.qualifier;
    }
    public String getIdentifier() {
        return this.identifier;
    }

    // setters
    public void setIdentifier(String identifier) {
        setAll(validateIdentifier(identifier));
    }

    // Compare Versions
    public boolean gt( Version comp ) {
        boolean result=false;

        if(this.major > comp.getMajorInt()) result = true;
        else if ( (this.major == comp.getMajorInt()) && (this.minor > comp.getMinorInt()) ) result = true;
             else if ( (this.major == comp.getMajorInt()) && (this.minor == comp.getMinorInt()) && (this.micro > comp.getMicroInt()) ) result=true;
                  else if ( (this.major == comp.getMajorInt()) && (this.minor == comp.getMinorInt()) && (this.micro == comp.getMicroInt()) && (this.qualifier.compareTo(comp.getQualifier()) > 0) ) result=true;

        return result;
    }
    public boolean lt( Version comp ) {
        boolean result=false;

        if(this.major < comp.getMajorInt()) result = true;
        else if ( (this.major == comp.getMajorInt()) && (this.minor < comp.getMinorInt()) ) result = true;
             else if ( (this.major == comp.getMajorInt()) && (this.minor == comp.getMinorInt()) && (this.micro < comp.getMicroInt()) ) result=true;
                  else if ( (this.major == comp.getMajorInt()) && (this.minor == comp.getMinorInt()) && (this.micro == comp.getMicroInt()) && (this.qualifier.compareTo(comp.getQualifier()) < 0) ) result=true;

        return result;
    }
    public boolean eq( Version comp ) {
        boolean result=false;

        if( (this.major == comp.getMajorInt()) && (this.minor == comp.getMinorInt()) && (this.micro == comp.getMicroInt()) && (this.qualifier.compareTo(comp.getQualifier()) == 0) ) {
            result = true;
        }
        return result;
    }
    public boolean ge( Version comp ) {
        boolean result=false;

        if( (this.gt(comp) || this.eq(comp) ) ) result = true;
        return result;
    }
    public boolean le( Version comp ) {
        boolean result=false;

        if( (this.lt(comp) || this.eq(comp) ) ) result = true;
        return result;
    }

    public boolean empty() {
        boolean result=false;

        if( (this.major == 0) && (this.minor == 0) && (this.micro == 0) && (this.qualifier.compareTo("") == 0) ) {
            result = true;
        }
        return result;
    }

    // Compare version strings
    //public boolean gt( String comp ) {
    //    boolean result=false;
    //
    //    if(this.identifier.compareTo(comp) > 0) {
    //        result = true;
    //    }
    //    return result;
    //}
    //public boolean lt( String comp ) {
    //    boolean result=false;
    //
    //    if(this.identifier.compareTo(comp) < 0) {
    //        result = true;
    //    }
    //    return result;
    //}
    //public boolean ge( String comp ) {
    //    boolean result=false;
    //
    //    if(this.identifier.compareTo(comp) >= 0) {
    //        result = true;
    //    }
    //    return result;
    //}
    //public boolean le( String comp ) {
    //    boolean result=false;
    //
    //    if(this.identifier.compareTo(comp) <= 0) {
    //        result = true;
    //    }
    //    return result;
    //}
    //public boolean eq( String comp ) {
    //    boolean result=false;
    //
    //    if(this.identifier.equals(comp)) {
    //        result = true;
    //    }
    //    return result;
    //}
}
