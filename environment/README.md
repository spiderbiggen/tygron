Tygron EIS connector
============

This connector puts an EIS layer on top of the Tygron SDK.

The connector allows you to control the Tygron engine in Planning mode.

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
