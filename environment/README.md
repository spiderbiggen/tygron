Tygron EIS connector
============

This connector puts an EIS layer on top of the Tygron SDK.

The connector allows you to control the Tygron engine in Planning mode.


Documentation
----
The environment provides actions and percepts as an exact copy of the SDK percepts and actions.

The percepts are staight translations of events coming in from the Tygron SDK. Please refer to EntityEventHandler javadoc. The incoming events are translated by the translators in the tygronenv.translators package. Only a small subset of the Tygron data objects are currently supported.  Also, not all contents in events are translated. Therefore, new translators will have to be added and existing translators probably will have to be extended. These extensions will be done as needed.

The actions are a straightforward implementation of the tygron SDK ParticipantEventType. Please refer to the tygron SDK javadoc for details on each action.

percept and action names are translated to lower case, to avoid ambiguities with prolog variables.

Some more documentation can be found here

http://support.tygron.com/wiki/Software_Development_Kit

