# Levi van Aanholt 2016
# Rudimentary script that takes piped input list as <commitauthor> <commithash> for the commits
# of a develop/feature branch compared to a branch to merge to and finds the first and therefore latest commit 
# on the develop/feature branch that has been made by a legacy coder.

import sys
import re
import subprocess

LEGACY_CODERS_CONFIG_FILE = "legacycoders.cfg"
FALLBACK_COMMIT_TXT = "fallbackcommit.txt"

fallback_commit = ""
with open(FALLBACK_COMMIT_TXT) as f:
	fallback_commit = f.readlines()[0]

legacy_coders = []
with open(LEGACY_CODERS_CONFIG_FILE) as f:
	legacy_coders = f.readlines()

latest_legacy_commit = fallback_commit
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
	