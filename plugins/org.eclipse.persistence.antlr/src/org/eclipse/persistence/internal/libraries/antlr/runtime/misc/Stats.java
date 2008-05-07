/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.libraries.antlr.runtime.misc;

import org.antlr.tool.ErrorManager;
import org.antlr.tool.GrammarReport;

import java.io.*;

/** Stats routines needed by profiler etc...

 // note that these routines return 0.0 if no values exist in the X[]
 // which is not "correct", but it is useful so I don't generate NaN
 // in my output

 */
public class Stats {
	/** Compute the sample (unbiased estimator) standard deviation following:
	 *
	 *  Computing Deviations: Standard Accuracy
	 *  Tony F. Chan and John Gregg Lewis
	 *  Stanford University
	 *  Communications of ACM September 1979 of Volume 22 the ACM Number 9
	 *
	 *  The "two-pass" method from the paper; supposed to have better
	 *  numerical properties than the textbook summation/sqrt.  To me
	 *  this looks like the textbook method, but I ain't no numerical
	 *  methods guy.
	 */
	public static double stddev(int[] X) {
		int m = X.length;
		if ( m<=1 ) {
			return 0;
		}
		double xbar = avg(X);
		double s2 = 0.0;
		for (int i=0; i<m; i++){
			s2 += (X[i] - xbar)*(X[i] - xbar);
		}
		s2 = s2/(m-1);
		return Math.sqrt(s2);
	}

	/** Compute the sample mean */
	public static double avg(int[] X) {
		double xbar = 0.0;
		int m = X.length;
		if ( m==0 ) {
			return 0;
		}
		for (int i=0; i<m; i++){
			xbar += X[i];
		}
		if ( xbar>=0.0 ) {
			return xbar / m;
		}
		return 0.0;
	}

	public static int min(int[] X) {
		int min = Integer.MAX_VALUE;
		int m = X.length;
		if ( m==0 ) {
			return 0;
		}
		for (int i=0; i<m; i++){
			if ( X[i] < min ) {
				min = X[i];
			}
		}
		return min;
	}

	public static int max(int[] X) {
		int max = Integer.MIN_VALUE;
		int m = X.length;
		if ( m==0 ) {
			return 0;
		}
		for (int i=0; i<m; i++){
			if ( X[i] > max ) {
				max = X[i];
			}
		}
		return max;
	}

	public static int sum(int[] X) {
		int s = 0;
		int m = X.length;
		if ( m==0 ) {
			return 0;
		}
		for (int i=0; i<m; i++){
			s += X[i];
		}
		return s;
	}

	public static void writeReport(String filename, String data) {
		String absoluteFilename = getAbsoluteFileName(filename);
		File f = new File(absoluteFilename);
		File parent = f.getParentFile();
		parent.mkdirs(); // ensure parent dir exists
		// write file
		try {
			FileOutputStream fos = new FileOutputStream(f, true); // append
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			PrintStream ps = new PrintStream(bos);
			ps.println(data);
			ps.close();
			bos.close();
			fos.close();
		}
		catch (IOException ioe) {
			ErrorManager.internalError("can't write stats to "+absoluteFilename,
									   ioe);
		}
	}

	public static String getAbsoluteFileName(String filename) {
		return System.getProperty("user.home")+File.separator+
					GrammarReport.ANTLRWORKS_DIR +File.separator+
					filename;
	}
}
