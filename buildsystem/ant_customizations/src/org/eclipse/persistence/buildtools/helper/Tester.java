/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/
package org.eclipse.persistence.buildtools.helper;

import org.eclipse.persistence.buildtools.helper.Version;

public class Tester {

    public static void main(String[] args) {
        Version testversion  = null;
        Version testversion2 = null;
        int testnum = 0;
        String testVersion;
        

        System.out.println("Constructor Testing: *** Expected failure testing...");

        // Test version empty
        try {
            testVersion = "";
            testnum+=1;
            System.out.println("=========================");
            System.out.println("= Test initial token missing");
            System.out.println("Test" + testnum + " versionString='" + testVersion + "':");
            testversion = new Version(testVersion);
        } catch ( VersionException e){
            System.out.println("Error: " + e);
            System.out.println("-------------------------");
            Throwable t=e.getCause();
            System.out.println("cause: '" + t + "'");
            t=e.getException();
            System.out.println("exception: '" + t + "'");
            System.out.println("-------------------------");
            e.printStackTrace();
        }
        System.out.println("=========================");

        // Test initial token missing
        try {
            testVersion = ".9.7";
            testnum+=1;
            System.out.println("=========================");
            System.out.println("= Test initial token missing");
            System.out.println("Test" + testnum + " versionString='" + testVersion + "':");
            testversion = new Version(testVersion);
        } catch ( VersionException e){
            System.out.println("Error: " + e);
            System.out.println("-------------------------");
            Throwable t=e.getCause();
            System.out.println("cause: '" + t + "'");
            t=e.getException();
            System.out.println("exception: '" + t + "'");
            System.out.println("-------------------------");
            e.printStackTrace();
        }
        System.out.println("=========================");

        // Test expected token missing 
        try {
            testVersion = "5.";
            testnum+=1;
            System.out.println("=========================");
            System.out.println("= Test expected token missing");
            System.out.println("Test" + testnum + " versionString='" + testVersion + "':");
            testversion = new Version(testVersion);
            System.out.println("Test" + testnum + " Version: '" + testversion.getIdentifier() + "'.");
        } catch ( VersionException e){
            System.out.println("Error: " + e);
        }
        System.out.println("=========================");

        // Test exceed max tokens 
        try {
            testVersion = "56.98.0.34.001";
            testnum+=1;
            System.out.println("=========================");
            System.out.println("= Test exceed max tokens");
            System.out.println("Test" + testnum + " versionString='" + testVersion + "':");
            testversion = new Version(testVersion);
            System.out.println("Test" + testnum + " Version: '" + testversion.getIdentifier() + "'.");
        } catch ( VersionException e){
            System.out.println("Error: " + e);
        }
        System.out.println("=========================");

        // Test invalid numeric tokens
        try {
            testVersion = "s6.6y98.0r.0p01";
            testnum+=1;
            System.out.println("=========================");
            System.out.println("= Test invalid numeric tokens");
            System.out.println("Test" + testnum + " versionString='" + testVersion + "':");
            testversion = new Version(testVersion);
            System.out.println("Test" + testnum + " Version: '" + testversion.getIdentifier() + "'.");
        } catch ( VersionException e){
            System.out.println("Error: " + e);
            System.out.println("-------------------------");
            Throwable t=e.getCause();
            System.out.println("cause: '" + t + "'");
            t=e.getException();
            System.out.println("exception: '" + t + "'");
            System.out.println("-------------------------");
            e.printStackTrace();
        }
        System.out.println("=========================");

        // Test invalid numeric tokens
        try {
            testVersion = "6x.698.0.0p01";
            testnum+=1;
            System.out.println("=========================");
            System.out.println("= Test invalid numeric tokens");
            System.out.println("Test" + testnum + " versionString='" + testVersion + "':");
            testversion = new Version(testVersion);
            System.out.println("Test" + testnum + " Version: '" + testversion.getIdentifier() + "'.");
        } catch ( VersionException e){
            System.out.println("Error: " + e);
            System.out.println("-------------------------");
            Throwable t=e.getCause();
            System.out.println("cause: '" + t + "'");
            t=e.getException();
            System.out.println("exception: '" + t + "'");
            System.out.println("-------------------------");
            e.printStackTrace();
        }
        System.out.println("=========================");

        // Test invalid numeric tokens
        try {
            testVersion = "7.6x98.0.0p01";
            testnum+=1;
            System.out.println("=========================");
            System.out.println("= Test invalid numeric tokens");
            System.out.println("Test" + testnum + " versionString='" + testVersion + "':");
            testversion = new Version(testVersion);
            System.out.println("Test" + testnum + " Version: '" + testversion.getIdentifier() + "'.");
        } catch ( VersionException e){
            System.out.println("Error: " + e);
            System.out.println("-------------------------");
            Throwable t=e.getCause();
            System.out.println("cause: '" + t + "'");
            t=e.getException();
            System.out.println("exception: '" + t + "'");
            System.out.println("-------------------------");
            e.printStackTrace();
        }
        System.out.println("=========================");

        // Test invalid numeric tokens
        try {
            testVersion = "8.0698.0x00.0p01";
            testnum+=1;
            System.out.println("=========================");
            System.out.println("= Test invalid numeric tokens");
            System.out.println("Test" + testnum + " versionString='" + testVersion + "':");
            testversion = new Version(testVersion);
            System.out.println("Test" + testnum + " Version: '" + testversion.getIdentifier() + "'.");
        } catch ( VersionException e){
            System.out.println("Error: " + e);
            System.out.println("-------------------------");
            Throwable t=e.getCause();
            System.out.println("cause: '" + t + "'");
            t=e.getException();
            System.out.println("exception: '" + t + "'");
            System.out.println("-------------------------");
            e.printStackTrace();
        }
        System.out.println("=========================");

        // Test space as version
        try {
            testVersion = " ";
            testnum+=1;
            System.out.println("=========================");
            System.out.println("= Test space as version");
            System.out.println("Test" + testnum + " versionString='" + testVersion + "':");
            testversion = new Version(testVersion);
            System.out.println("Test" + testnum + " Version: '" + testversion.getIdentifier() + "'.");
        } catch ( VersionException e){
            System.out.println("Error: " + e);
        }
        System.out.println("=========================");

        // Test space in version (failure)
        try {
            testVersion = "3. 3.0 ";
            testnum+=1;
            System.out.println("=========================");
            System.out.println("= Test space as version");
            System.out.println("Test" + testnum + " versionString='" + testVersion + "':");
            testversion = new Version(testVersion);
            System.out.println("Test" + testnum + " Version: '" + testversion.getIdentifier() + "'.");
        } catch ( VersionException e){
            System.out.println("Error: " + e);
        }
        System.out.println("=========================");

        System.out.println("Constructor Testing: *** Expected Success testing...");
        // Test initial and trailing spaces in version
        try {
            testVersion = "    1.1.3    ";
            testnum+=1;
            System.out.println("=========================");
            System.out.println("= Test initial and trailing spaces in version");
            System.out.println("Test" + testnum + " versionString='" + testVersion + "':");
            testversion = new Version(testVersion);
            System.out.println("Test" + testnum + " Version: '" + testversion.getIdentifier() + "'.");
        } catch ( VersionException e){
            System.out.println("Error: " + e);
        }
        System.out.println("=========================");

        // Test initial and trailing tabs in version
        try {
            testVersion = "				1.1.3			";
            testnum+=1;
            System.out.println("=========================");
            System.out.println("= Test initial and trailing tabs in version");
            System.out.println("Test" + testnum + " versionString='" + testVersion + "':");
            testversion = new Version(testVersion);
            System.out.println("Test" + testnum + " Version: '" + testversion.getIdentifier() + "'.");
        } catch ( VersionException e){
            System.out.println("Error: " + e);
        }
        System.out.println("=========================");

        // Test mixed initial and trailing tabs and spaces in version
        try {
            testVersion = " 	 	 	 	1.1.3 	 	 	 ";
            testnum+=1;
            System.out.println("=========================");
            System.out.println("= Test mixed initial and trailing tabs and spaces in version");
            System.out.println("Test" + testnum + " versionString='" + testVersion + "':");
            testversion = new Version(testVersion);
            System.out.println("Test" + testnum + " Version: '" + testversion.getIdentifier() + "'.");
        } catch ( VersionException e){
            System.out.println("Error: " + e);
        }
        System.out.println("=========================");

        // Test Micro as double 0 and fourth token as string ok
        try {
            testVersion = "6.98.00.0p01";
            testnum+=1;
            System.out.println("=========================");
            System.out.println("= Test Micro as double 0 and fourth token as string ok");
            System.out.println("Test" + testnum + " versionString='" + testVersion + "':");
            testversion = new Version(testVersion);
            System.out.println("Test" + testnum + " Version: '" + testversion.getIdentifier() + "'.");
        } catch ( VersionException e){
            System.out.println("Error: " + e);
        }
        System.out.println("=========================");

        // Test whole number version
        try {
            testVersion = "2";
            testnum+=1;
            System.out.println("=========================");
            System.out.println("= Test whole number version");
            System.out.println("Test" + testnum + " versionString='" + testVersion + "':");
            testversion = new Version(testVersion);
            System.out.println("Test" + testnum + " Version: '" + testversion.getIdentifier() + "'.");
        } catch ( VersionException e){
            System.out.println("Error: " + e);
        }
        System.out.println("=========================");

        // Test 'normal' version
        try {
            testVersion = "1.16.4";
            testnum+=1;
            System.out.println("=========================");
            System.out.println("= Test 'normal' version");
            System.out.println("Test" + testnum + " versionString='" + testVersion + "':");
            testversion = new Version(testVersion);
            System.out.println("Test" + testnum + " Version: '" + testversion.getIdentifier() + "'.");
        } catch ( VersionException e){
            System.out.println("Error: " + e);
        }
        System.out.println("=========================");

        // Test initial 0 in Major and Minor version
        try {
            testVersion = "0.0.350";
            testnum+=1;
            System.out.println("=========================");
            System.out.println("= Test initial 0 in Major and Minor version");
            System.out.println("Test" + testnum + " versionString='" + testVersion + "':");
            testversion = new Version(testVersion);
            System.out.println("Test" + testnum + " Version: '" + testversion.getIdentifier() + "'.");
        } catch ( VersionException e){
            System.out.println("Error: " + e);
        }
        System.out.println("=========================");

        // Test 00 in Minor and no micro version
        try {
            testVersion = "3.00";
            testnum+=1;
            System.out.println("=========================");
            System.out.println("= Test 00 in Minor and no micro version");
            System.out.println("Test" + testnum + " versionString='" + testVersion + "':");
            testversion = new Version(testVersion);
            System.out.println("Test" + testnum + " Version: '" + testversion.getIdentifier() + "'.");
        } catch ( VersionException e){
            System.out.println("Error: " + e);
        }
        System.out.println("=========================");

       // Test leading 0s in version
        try {
            testVersion = "090.0828.04.HooYah";
            testnum+=1;
            System.out.println("=========================");
            System.out.println("= Test leading 0s in version");
            System.out.println("Test" + testnum + " versionString='" + testVersion + "':");
            testversion = new Version(testVersion);
            System.out.println("Test" + testnum + " Version: '" + testversion.getIdentifier() + "'.");
        } catch ( VersionException e){
            System.out.println("Error: " + e);
        }
        System.out.println("=========================");

       // Test 0 in Minor and decimal digit retention in micro version
        try {
            testVersion = "2.0.100.v201202140850";
            testnum+=1;
            System.out.println("=========================");
            System.out.println("= Test 0 in Minor and decimal digit retention in micro version");
            System.out.println("Test" + testnum + " versionString='" + testVersion + "':");
            testversion = new Version(testVersion);
            System.out.println("Test" + testnum + " Version: '" + testversion.getIdentifier() + "'.");
        } catch ( VersionException e){
            System.out.println("Error: " + e);
        }
        System.out.println("=========================");

        System.out.println("Equivilency Testing: *** Expected Success testing...");
        // Setup 
        try {
            testVersion = "2.0.100.v201202140850";
            String testVersion2 = "2.0.4.v201110091424";
            testversion  = new Version(testVersion);
            testversion2 = new Version(testVersion2);
        } catch ( VersionException e){
            System.out.println("Error: " + e);
        }
        // Test lt 
        testnum+=1;
        System.out.println("=========================");
        System.out.println("= Test lt with objects");
        System.out.println("Test" + testnum + " versionObject(" + testversion.getIdentifier() + ") < versionObject(" + testversion2.getIdentifier() + ")");
        if( testversion.lt(testversion2) )
            System.out.println("    True.");            
        else
            System.out.println("    False.");            
        System.out.println("      versionObject(" + testversion2.getIdentifier() + ") < versionObject(" + testversion.getIdentifier() + ")");
        if( testversion2.lt(testversion) )
            System.out.println("    True.");            
        else
            System.out.println("    False.");            
        System.out.println("=========================");
        // Test le 
        testnum+=1;
        System.out.println("=========================");
        System.out.println("= Test le with objects");
        System.out.println("Test" + testnum + " versionObject(" + testversion.getIdentifier() + ") <= versionObject(" + testversion2.getIdentifier() + ")");
        if( testversion.le(testversion2) )
            System.out.println("    True.");            
        else
            System.out.println("    False.");            
        System.out.println("      versionObject(" + testversion2.getIdentifier() + ") <= versionObject(" + testversion.getIdentifier() + ")");
        if( testversion2.le(testversion) )
            System.out.println("    True.");            
        else
            System.out.println("    False.");            
        System.out.println("=========================");
        // Test gt 
        testnum+=1;
        System.out.println("=========================");
        System.out.println("= Test gt with objects");
        System.out.println("Test" + testnum + " versionObject(" + testversion.getIdentifier() + ") > versionObject(" + testversion2.getIdentifier() + ")");
        if( testversion.gt(testversion2) )
            System.out.println("    True.");            
        else
            System.out.println("    False.");            
        System.out.println("      versionObject(" + testversion2.getIdentifier() + ") > versionObject(" + testversion.getIdentifier() + ")");
        if( testversion2.gt(testversion) )
            System.out.println("    True.");            
        else
            System.out.println("    False.");            
        System.out.println("=========================");
        // Test ge 
        testnum+=1;
        System.out.println("=========================");
        System.out.println("= Test ge with objects");
        System.out.println("Test" + testnum + " versionObject(" + testversion.getIdentifier() + ") >= versionObject(" + testversion2.getIdentifier() + ")");
        if( testversion.ge(testversion2) )
            System.out.println("    True.");            
        else
            System.out.println("    False.");            
        System.out.println("      versionObject(" + testversion2.getIdentifier() + ") >= versionObject(" + testversion.getIdentifier() + ")");
        if( testversion2.ge(testversion) )
            System.out.println("    True.");            
        else
            System.out.println("    False.");            
        System.out.println("=========================");
        // Test eq 
        testnum+=1;
        System.out.println("=========================");
        System.out.println("= Test eq with objects");
        System.out.println("Test" + testnum + " versionObject(" + testversion.getIdentifier() + ") < versionObject(" + testversion2.getIdentifier() + ")");
        if( testversion.eq(testversion2) )
            System.out.println("    True.");            
        else
            System.out.println("    False.");            
        System.out.println("=========================");
       
    }
}
