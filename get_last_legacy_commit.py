# Levi van Aanholt 2016
# Rudimentary script that takes piped input list as <commitauthor> <commithash> for the commits
# of a develop/feature branch compared to a branch to merge to and finds the first and therefore latest commit 
# on the develop/feature branch that has been made by a legacy coder.

import sys
import re
import subprocess

LEGACY_CODERS_CONFIG_FILE = "legacycoders.cfg"
BRANCH_TO_MERGE_TO = "upstream/context"

legacy_coders = []
with open(LEGACY_CODERS_CONFIG_FILE) as f:
	legacy_coders = f.readlines()

latest_legacy_commit = subprocess.Popen("git rev-parse "+BRANCH_TO_MERGE_TO, stdout=subprocess.PIPE).stdout.read().replace("\n","")
legacy_coder_found = False

for line in sys.stdin:
	
	for coder in legacy_coders:
		coder = coder.replace("\n","")
		if re.findall(coder+"\s", line):
			latest_legacy_commit = line.replace(coder+" ","")
			legacy_coder_found = True
			break
	
	if legacy_coder_found:
		break

print(latest_legacy_commit)
	