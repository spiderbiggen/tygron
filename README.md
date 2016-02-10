# tygron 

tygron is an EIS environment that connects with the tygron http://www.tygron.com system for urban planning.


##Install instructions

1. Download or build a `tygron-connect` environment.
  * **Jenkins CI build**  
    Jenkins provides automatic building for this project. Download [the latest jar with dependencies](http://jenkins.buildwise.eu/job/tygron-connect/lastBuild//tygron-connect$tygron-connect-environment/).
  * **Manual build**  
    To build tygron-connect manually, run `mvn deploy`.
2. This jar should be used as the environment in your GOAL `mas2g` file ([example](https://github.com/tygron-virtual-humans/tygron-connect/blob/master/tygron-connect-environment/src/GOAL/GoalTestTygron.mas2g)).
3. Add a `configuration.cfg` in the same folder as the environment jar. The file should specify a username and password ([example](https://github.com/tygron-virtual-humans/tygron-connect/blob/master/tygron-connect-environment/src/main/resources/testconfiguration.cfg)).
