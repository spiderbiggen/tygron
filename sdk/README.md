Tygron SDK Wrapper
============

This module is a wrapper around the tygron-sdk.zip file that we receive from Tygron.
This project creates a complete maven artefact from tygron sdk code and their sdk.jar file.

This artefact is working properly for developing code and debugging calls to the tygron sdk.

The wrapper is completely built automatically from the sources provided by Tygron.

The jar and actual source code should always match. This project is NOT a proper 
compiler for source code. So 
```
ALWAYS CHANGE JAR AND SOURCE CODE IN TANDEM
```

#Usage

After this has been built, other projects can use it with something like this (version nr may differ)

```
		<dependency>
			<groupId>nl.tygron</groupId>
			<artifactId>sdk</artifactId>
			<version>2016.4.1.3</version>
		</dependency>
```

The project that uses this also must have a README.txt file that contains the license info received from Tygron
(user name, account, version). You need this license as it comes with a password that you need to contact 
the tygron server. 

#Updating the project
When Tygron provides a new zip file, the following steps are needed

 * unzip the zip file. 
 * locate the lib directory 
 * unzip the tygron-sdk-source.zip file. Copy the unzipped files into the src/main/java directory (replaces ALL source except login package)
 * copy the tygron-sdk.jar file into the src/jars directory.
 * Check the README.txt in the zip file and check the VERSION, something like this ```VERSION:XXX``` 
 where XX is the version we are looking for.
 * Update the POM to version XXX.  
 
 After this, the Tygron sdk is ready for building. Of course, after this, the projects using this also need
 to be updated to use version XXX.
 