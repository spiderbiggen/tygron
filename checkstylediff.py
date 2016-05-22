# By Levi van Aanholt 2016
# A script that takes piped input from git diff and outputs an xml file with all the lines that need to be suppressed for Checkstyle.
# Checkstyle wants a suppression file where all the files that should be suppressed are defined, this script provides this
# Use by running git diff | python checkstylediff.py

import sys
import re
import os
import fileinput

# this has to be a magic number, otherwise all .java files need to be
# checked to see what the max line number is
MAX_LINE_AMOUNT = 20000;

# name of the file that is outputted
outputfile = "suppressions.xml"

info = "<?xml version="+'"'+"1.0"+'"'+"?><!DOCTYPE suppressions PUBLIC"+"\n"+'"-//Puppy Crawl//DTD Suppressions 1.0//EN"'+"\n"+'"http://www.puppycrawl.com/dtds/suppressions_1_0.dtd"'+">"+"\n"

start = "<suppressions>"
changed_files = []
currentfile = ""
changes_in_file = []

# This function takes the filename and the changes as a list of lists and prints the suppression for that file.
def suppressfile(file, changes):
	suppressed_lines = range(1, MAX_LINE_AMOUNT)
	start = "<suppress checks="+'"'+".*"+'"'
	files = "files="+'"'+file+'"'
	lines = ""
	# if there are changes
	if changes:
		# create a list of numbers where all not suppressed line numbers are omitted
		for i in changes:
			lines_to_check = range(i[0], i[0]+i[1])
			suppressed_lines = [x for x in suppressed_lines if x not in lines_to_check]
		segments = []
		segment = (suppressed_lines[0],suppressed_lines[0])
		pos = 0
		# from the number list derive which lines are suppressed
		for i in suppressed_lines:
			# if the last suppressed entry is reached
			if len(suppressed_lines) <= pos + 1:
				segments.append((segment[0], MAX_LINE_AMOUNT))
			# if the next number in the list is expected number + 1
			elif i + 1 == suppressed_lines[pos + 1]:
				segment = (segment[0], i+1)
			# there are some lines that are not suppressed, our suppression segment must be added
			else: 
				segments.append(segment)
				segment = (suppressed_lines[pos + 1],suppressed_lines[pos + 1])
			pos = pos + 1
				
		for i in segments:
			lines += str(i[0])+"-"+str(i[1])+","
		lines = 'lines="'+lines+'"'
	return start +" "+ files +" "+ lines +"/>"
	
currentfile = ""
# the program takes the piped input and processes it line for line
for line in sys.stdin:
	# the current file the changes are in
	checkfile = re.findall("\sb/.*\\n", line)
	if re.findall("diff --git", line) and checkfile and re.findall("\.java", checkfile[0]):
		# when the first file is found
		if not currentfile:
			# the changed file name
			currentfile = re.findall("\sb/.*\\n", line)[0].replace(" b/","").replace(" ","").replace("\n","").replace("/","[\\\/]")
		# the changed file name
		newfile = re.findall("\sb/.*\\n", line)[0].replace(" b/","").replace(" ","").replace("\n","").replace("/","[\\\/]")
		# if these are not equal then git diff is talking about a different file and the registered changes have to be put in
		# the list
		if(newfile != currentfile):
			changed_files.append((currentfile, changes_in_file))
			changes_in_file = []
		currentfile = newfile
	# the line changes in the file
	elif re.findall("@@\s.*\s@@",line):
		changed = re.findall("\+.*\s", re.findall("@@\s.*\s@@",line)[0])[0]
		line_range = [int(x) for x in changed.replace("+","").split(",")]
		# if there is only one number, then the range is 1 (one line)
		if len(line_range) < 2:
			line_range.append(1)
		changes_in_file.append(line_range)

# when finished add the last file changes in the list
if currentfile:
	changed_files.append((currentfile,changes_in_file))

java_files = []
# take all .java files that are in the directory
for root, dirs, files in os.walk("./"):
	for file in files:
		if file.endswith(".java"):
			java_files.append(os.path.join(root, file))
# make the file names the same convention as the files read in git diff
java_files = [x.replace("./","").replace("\\","[\\\/]") for x in java_files]
# all files that are not changed can be fully suppressed and have no changes
suppressed_files = [(file,False) for file in [y for y in java_files] if not(file in [y[0] for y in changed_files])]

if changed_files:
	print("*The current files are checkstyle*")
else:
	print("*There are no checkstyled lines*")
for changed_file in changed_files:
	lines_to_check = []
	for i in changed_file[1]:
		lines_to_check = lines_to_check.append(range(i[0], i[0]+i[1]))
	print("Checkstyled lines for "+changed_file[0]+":"+str(lines_to_check))

# add both file lists together
changed_files.extend(suppressed_files)



# run the suppressfile function on all these files
content = "".join([suppressfile(x[0],x[1])+"\n" for x in changed_files])
		
end = "</suppressions>"
open(outputfile, "w").write(info+"\n"+start+"\n"+content+end)

