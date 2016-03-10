Tygron Java SDK Quick-Start
===========================

This document gives a short overview of the Java Tygron-SDK that you just downloaded. 
It is only meant to be a starting point, for further information visit the Tygron Support WIKI or 
contact us via support@tygron.com. We expect the reader to have a basic knowledge of software 
development and the Java programming language.

CONFIG & LICENSE
================

This Tygron-SDK is configured for User:

USER:Wouter Pasman
USERNAME:w.pasman@tudelft.nl
SERVER:preview.tygron.com
VERSION:2016.4.0 DEV 17

The User is allowed to use the Tygron-SDK ONLY for his/her OWN Application development/distribution. 
User must also confirm to the Tygron End User Conditions also packed in this zip file under end_user_conditions_en.txt.
Licensing information about libraries used in this Tygron-SDK can be viewed in LegalNotices.txt.

GETTING STARTED
===============

1. Download and install the "Java Development Kit (JDK)" version 8+ from: http://www.oracle.com/technetwork/java/javase/downloads/index.html

2. Download and install "Eclipse IDE for Java Developers" from: https://www.eclipse.org/downloads/

3. Start Eclipse got to the "Package Explorer" and right-mouse-select new Java Project, name it tygron-sdk.

4. Now select the project from the "Package Explorer" and right-mouse-select Import/General/Archive File to import this zip. 

5. When the project contains Errors; please make sure all project libraries are correctly detected (tygron-sdk.jar, junit en JDK8).

6. Run ExampleTest via Junit to test the SDK functionality and server connection.

7. Read the Overview below and Start coding your apps!

OVERVIEW
========

To help you get started hereby a short overview of the SDK-Server main classes:
1. SettingsManager stores recently used settings (like your login name, server IP) in the windows registry. For the SDK to work it needs to be
instantiated so the other components can use it.

2. ServicesManager is a singleton class that can be used to log into the Tygron Server with your given credentials. 
It can also fire "service events" that can be used to check your domain, user settings and start a session or delete an old project.
All available commands can be found in IOServiceEventType (related to projects) and UserServiceEventType (related to user management).

3. Projects & Sessions: A "Project" is name for all data related to a given project. So it contains the building locations, stakeholder setup etc.
The project is stored on the Server in a database and can be instantiated into a "Session". A session means that the project data is retrieved from the database
and loaded into the Server logic core on a slot. We have several types of sessions, e.g. the EDITOR session can be instantiated only once and can be used to setup
your project. E.g. define the budgets of the stakeholders and what they can do. After saving the EDITOR session the project data is stored again to the database. 
Now you can also start on ore more SINGLE or MULTI player session simultaneously. These sessions are used by the actual end-user and cannot change the basic setup 
of the project. Players/Clients take on the role of a stakeholder and can start negotiating on how the re-arange the area. So they can plan new buildings, but cannot
change e.g. their budget anymore (only an EDITOR session can do that). You can save a SINGLE/MULTI session but it is stored as a seperate entity from the original project.

4. SlotConnection can be used to connect to a Session. After setting up this class (e.g. provide it with the correct Slot ID to connect to) it will automatically start
updating your local data to the most recent server data version and fire events. You can also fire events to the Server logic using this class, e.g. to build a new building.
Note: SlotConnection is only to be used for this specific Session. When you run multiple sessions parallel you can also start multiple SlotConnections. All general purpose or 
non session related events (like changing your login password) are handled by ServiceManager events described above).

5. EventManager: after starting a SlotConnection you can listen to updates using the EventManager.addListener() functionality. E.g. listening to MapLink.BUILDINGS will fire 
a update event each time a building is changed by you or another player. You can also retrieve the latest data using EventManager.getItem().

6. MapLink and Items: MapLink is a enumerator listing all data types available. So for example MapLink.BUILDINGS references to a listing of all Building Items and 
MapLink.STAKEHOLDERS references a listing of all Stakeholder Items. Each data type extends the basic Item class which links the Items together. So e.g. if you ask
Building.getOwner() this will return the Stakeholder Object that owns the Building.

7. For GEO data we make use of the Java Topology Suite (JTS) classes. So when you ask the polygon data of a building you will get a JTS MultiPolygon. JTS provides also provides 
all basic polygon manipulation (intersections, unions, etc). More info on: http://tsusiatsoftware.net/jts/main.html

8. This is only a very short overview of all posibilities, please read ExampleTest carefully to see some examples of the things described above. More information about the content
of the Tygron Engine can be found on: http://support.tygron.com/wiki/ or via support@tygron.com.


UPDATES
=======

The Engine is regularly updated and this can sometimes break the old API calls. However the SDK it integrated into our development 
cycle and is thus always updated to the latest version. So please check you SDK version regularly via calling Engine.VERSION in 
your app and comparing it with the server version. Note: calling ServiceManager.testServerConnection() will also fail when your version is out of date!