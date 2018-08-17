// Copyright 2018 Tencent Inc. or its affiliates. All Rights Reserved.
package com.tencent.cos.versioning;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class Demo {
	// Main function
	public static void main(String[] args) {
	    if (args.length < 1) {
	    		System.out.println("No arguments provided");
	    }

	    usePosixParser(args);	    
	}
	
	// We use posix style CLI
	// E.g:
	// java -jar demo.jar -listBucket [arguments]
	public static void usePosixParser(final String[] commandLineArguments) {
		final CommandLineParser cmdLinePosixParser = new PosixParser();
	    final Options posixOptions = constructPosixOptions();
	    CommandLine commandLine;
	    try {
	    		commandLine = cmdLinePosixParser.parse(posixOptions, commandLineArguments);
	        if (commandLine.hasOption("listBucket")) {
		        Operation.listBucketsWithVersionStatus();
	        } else if (commandLine.hasOption("listObject")) {
	        		List<String> arg = commandLine.getArgList();
	        		Operation.listObject(arg.get(0));
	        } else if (commandLine.hasOption("enableBucketVersion")) {
        			List<String> arg = commandLine.getArgList();
    				Operation.enableBucketVersion(arg.get(0));
	        } else if (commandLine.hasOption("disableBucketVersion")) {
    				List<String> arg = commandLine.getArgList();
				Operation.disableBucketVersion(arg.get(0));
	        } else if (commandLine.hasOption("listVersion")) {
				List<String> arg = commandLine.getArgList();
				Operation.listVersions(arg.get(0));
	        } else if (commandLine.hasOption("deleteVersion")) {
	        		List<String> arg = commandLine.getArgList();
	        		Operation.deleteVersion(arg.get(0), arg.get(1), arg.get(2));
	        } else if (commandLine.hasOption("restoreVersion")) {
        			List<String> arg = commandLine.getArgList();
        			Operation.restoreVersion(arg.get(0), arg.get(1), arg.get(2));
	        } else if (commandLine.hasOption("setVersionLifecycle")) {
    				List<String> arg = commandLine.getArgList();
    				Operation.setVersionLifecycle(arg.get(0));
	        } else if (commandLine.hasOption("getVersionLifecycle")) {
    				List<String> arg = commandLine.getArgList();
    				Operation.getVersionLifecycle(arg.get(0));
	        }
	    } catch (ParseException parseException) {
	         System.err.println("Encountered exception while parsing using PosixParser:\n" + parseException.getMessage() );
	    }
	}
	
	public static Options constructPosixOptions() {
		final Options posixOptions = new Options();
	    posixOptions.addOption("listBucket", false, "Display the Bucket Info.");
	    posixOptions.addOption("listObject", false, "Display the Object Info.");
	    posixOptions.addOption("listVersionBucket", false, "Display the Versioned Bucket Info.");
	    posixOptions.addOption("enableBucketVersion", false, "Enable Bucket Versioning.");
	    posixOptions.addOption("disableBucketVersion", false, "Disable Bucket Versioning.");
	    posixOptions.addOption("listVersion", false, "List Versioning.");
	    posixOptions.addOption("deleteVersion", false, "Delete Versioning.");
	    posixOptions.addOption("restoreVersion", false, "Restore Versioning.");
	    posixOptions.addOption("setVersionLifecycle", false, "Set Versioning life cycle expire after 90 days.");
	    posixOptions.addOption("getVersionLifecycle", false, "Restore Versioning.");

	    return posixOptions;
	}	
}
