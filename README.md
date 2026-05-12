# Hummingbird Catapult
Java program built for catapult kit, hummingbird.


### Built For: Intro to Java 2026
(Those who learn Java)

**Note:** All helpful information on running this program can be found in ```src/Catapult.java```'s prologue. This README is more of a quickstart guide to see from GitHub.

## How to Run:
* Use file in /src titled ```Catapult.java```, instructions can be found in the prologue there.
* Make sure you have the catapult set built, and the BlueBird connector application loaded on your device. Connect to your hummingbird with MicroBit and run ```Catapult.java```.
* If the program crashes while running (very low probability unless of connection error, not programs fault at that point), use the program ```stopAll.java``` to cease all functions on the catapult.

## Reference Chart
Can also be found in the prologue of ```Catapult.java```

|Notch Setting|~Distance (cm)|
|-------------|--------------|
|1|150|
|2|125|
|3|120|
|4|100|
|5|75|
|6|25|

## Changelog:
|Version|Description|
|-------|-----------|
|1.0.0|Initial creation and implementation of catapult controls (A/B Controls)|
|1.1.0|Added distance sensing and refined notches on catapult, added input for notches for maximum accuracy on each shot, added trap set indicator|
|1.1.1|Made distance more comprehensive on MicroBit with grid lights closing in on the center to demonstrate distance to firing|
|1.2.0|Made target and target sensing, added prompts to ask user for setup on if they wanted prompts, if they had target, etc.|
|1.2.1|Added LED control with Red and Green lights to show readiness and activity, as well as clean input statements to allow best setup|
|1.2.2|Added Keyboard Inturrupt stopping so that all hummingbird functions sucessfully stop when terminating with ^C.|
|1.3.0|Added timer functionality for shots, allows countdowns for proximity and firing. Updated docs to match|
|1.3.1|Turned notch adjuster into a class for optimization and versatility|

All code by: [USSyorktown10](https://github.com/USSyorktown10)

And yes, I have learned to comment my code.
