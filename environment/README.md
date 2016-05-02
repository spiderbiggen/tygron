Tygron EIS connector
============

This connector puts an EIS layer on top of the Tygron SDK.

The connector allows you to control the Tygron engine in Planning mode.


Documentation
----
The environment provides actions and percepts as an exact copy of the SDK percepts and actions.

The percepts are staight translations of events coming in from the Tygron SDK. Please refer to EntityEventHandler javadoc. The incoming events are translated by the translators in the tygronenv.translators package. The current set of translators will need to be extended, the plan is to do this as needed.

The actions are a straightforward implementation of the tygron SDK ParticipantEventType. Please refer to the tygron SDK javadoc for details on each action.

percept and action names are translated to lower case, to avoid ambiguities with prolog variables.

Some more documentation can be found here

http://support.tygron.com/wiki/Software_Development_Kit


Testing
---
To test the environment, you need to put a configuration.cfg file with the following contents in src/test/resources. 

```
username: yourtygronusername
password: yourtygronpassword
server: preview.tygron.com
```





Use the GOAL example
---

 * download or build the env (```mvn packge```)
 * Place the resulting tygronenv-X.Y.Z-jar-with-dependencies.jar  in src/main/GOAL
 * Place your configuration.cfg file in src/main/GOAL along with the environment file
 * Run the Tygron.mas2g in GOAL
