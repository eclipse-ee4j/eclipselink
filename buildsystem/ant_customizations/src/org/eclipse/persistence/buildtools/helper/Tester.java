package org.eclipse.persistence.buildtools.helper;

import org.eclipse.persistence.buildtools.helper.Version;

public class Tester {

    public static void main(String[] args) {
        Version testversion = null;

        try {
            testversion = new Version(".9.7");
            System.out.println("test4 Version: '" + testversion.getIdentifier() + "'.");
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

        try {
            testversion = new Version("5.");
            System.out.println("test4 Version: '" + testversion.getIdentifier() + "'.");
        } catch ( VersionException e){
            System.out.println("Error: " + e);
        }

        try {
            testversion = new Version("56.98.0.34.001");
            System.out.println("test4 Version: '" + testversion.getIdentifier() + "'.");
        } catch ( VersionException e){
            System.out.println("Error: " + e);
        }

        try {
            testversion = new Version("s6.6y98.0r.0p01");
            System.out.println("test4 Version: '" + testversion.getIdentifier() + "'.");
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

        try {
            testversion = new Version("6.y98.0r.0p01");
            System.out.println("test4 Version: '" + testversion.getIdentifier() + "'.");
        } catch ( VersionException e){
            System.out.println("Error: " + e);
        }

        try {
            testversion = new Version("6.98.0r.0p01");
            System.out.println("test4 Version: '" + testversion.getIdentifier() + "'.");
        } catch ( VersionException e){
            System.out.println("Error: " + e);
        }

        try {
            testversion = new Version("6.98.0.0p01");
            System.out.println("test4 Version: '" + testversion.getIdentifier() + "'.");
        } catch ( VersionException e){
            System.out.println("Error: " + e);
        }

        try {
            testversion = new Version("0.0.350");
            System.out.println("test Version : '" + testversion.getIdentifier() + "'.");
        } catch ( VersionException e){
            System.out.println("Error: " + e);
        }

        try {
            testversion = new Version("1.16.4");
            System.out.println("test2 Version: '" + testversion.getIdentifier() + "'.");
        } catch ( VersionException e){
            System.out.println("Error: " + e);
        }

        try {
            testversion = new Version("3.00");
            System.out.println("test4 Version: '" + testversion.getIdentifier() + "'.");
        } catch ( VersionException e){
            System.out.println("Error: " + e);
        }

        try {
            testversion = new Version("90.828.04.HooYah");
            System.out.println("test3 Version: '" + testversion.getIdentifier() + "'.");
        } catch ( VersionException e){
            System.out.println("Error: " + e);
        }
    }

}
