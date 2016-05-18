import sys
import re
import os
import fileinput

MAX_LINE_AMOUNT = 20000;

outputfile = "suppression.xml"

info = "<?xml version="+'"'+"1.0"+'"'+"?><!DOCTYPE suppressions PUBLIC"+'"-//Puppy Crawl//DTD Suppressions 1.1//EN"'+'"http://www.puppycrawl.com/dtds/suppressions_1_1.dtd"'+">"

start = "<suppression>"
changed_files = []
currentfile = ""
changes_in_file = []

# This function takes the filename and the changes as a list of lists and prints the suppression for that file.
def suppressfile(file, changes):
	supressed_lines = range(1, MAX_LINE_AMOUNT)
	start = "<suppress checks="+'"'+".*"+'"'
	files = "files="+file
	lines = ""
	if changes:
		print(changes)
		for i in changes:
			lines_to_check = range(i[0], i[0]+i[1])
			supressed_lines = [x for x in supressed_lines if x not in lines_to_check]
		segments = []
		segment = (supressed_lines[0],supressed_lines[0])
		pos = 0
		for i in supressed_lines:
			if len(supressed_lines) <= pos + 1:
				segments.append((segment[0], MAX_LINE_AMOUNT))
			elif i + 1 == supressed_lines[pos + 1]:
				segment = (segment[0], i+1)
			else: 
				segments.append(segment)
				segment = (supressed_lines[pos + 1],supressed_lines[pos + 1])
			pos = pos + 1
				
		#lines = ""
		for i in segments:
			lines += str(i[0])+"-"+str(i[1])+" "
		lines = 'lines="'+lines+'"'
	return start +" "+ files +" "+ lines +"/>"
	

for line in sys.stdin:
	if re.findall("diff --git", line):
		if not currentfile:
			currentfile = re.findall("\sb/.*\s", line)[0].replace(" b/","").replace(" ","").replace("\n","")
		newfile = re.findall("\sb/.*\s", line)[0].replace(" b/","").replace(" ","").replace("\n","")
		if(newfile != currentfile):
			changed_files.append((currentfile, changes_in_file))
			changes_in_file = []
		currentfile = newfile
	elif re.findall("@@\s.*\s@@",line):
		changed = re.findall("\+.*\s", re.findall("@@\s.*\s@@",line)[0])[0]
		changes_in_file.append([int(x) for x in changed.replace("+","").split(",")])
		
changed_files.append((currentfile,changes_in_file))


java_files = []
for root, dirs, files in os.walk("./"):
	for file in files:
		if file.endswith(".java"):
			java_files.append(os.path.join(root, file))
java_files = [x.replace("./","").replace("\\","/") for x in java_files]
suppressed_files = [(file,False) for file in [y for y in java_files] if not(file in [y[0] for y in changed_files])]
changed_files.extend(suppressed_files)

content = "".join([suppressfile(x[0],x[1])+"\n" for x in changed_files])
		
end = "</suppression>"
open(outputfile, "w").write(info+"\n"+start+"\n"+content+end)

