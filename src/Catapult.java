/*
 * Project Name:     Catapult
 * File Name:        Catapult.java
 * Author:           Joe March
 * Date:             May 5, 2026
 * Description:      This class controls the hummingbird catapult mechanism, allowing for adjustable shot settings and target acquisition.
 * Methods:          setTarget, main
 * Hardware Setup:   Red light connected to LED1, Green light connected to LED2, Yellow light connected to LED3, Position servo connected to Servo1, Distance Sensor connected to Sensor1, Light Sensor connected to Sensor2
 * Quickstart:       Put the catapult arm in the down position, as after the setup prompts are complete, the arm will lock. 
 *                   Start the program, and input the notch number based on where the wood bar that controls the distance is set. 
 *                    - Each notch represents how far you want the ball (projectile) to go, as well as the lift you want on the ball.
 *                   Notches are numbered from left to right when viewing the side-profile with the distance sensor on the left. The farthest left notch is 1, the farthest right is 6. 
 *                    - Distance in cm for each notch is provided below in the "Reference Chart."
 *                   If using the white target/basket, set the notch to 6 (as well as on the physical catapult), and set "Using target" to "y" on the input prompt.
 *                   If dont use the target/basket, set the notch to whatever you desire. Remember that 1 has the farthest range! (and set "Using Target" to "n")
 *                    - Note: If you set "Using Target" to "n," you can disconnect both Sensor 2 (light sensor) and LED3 to allow you to move the catapult more places without the bulk of carrying the target with you.
 *                   If you want to change the notch after each shot or have an easy command to exit, set "Edit Notch/Timer" to "y" on the input prompt.
 *                    - Note:
 *                       - If you set "Edit Notch" to "y", you will be prompted to enter a new notch value after each shot. This means that you cant reset the catapult to locked position until entering "0" to continue on current setting, or inputing another notch number. If you set the timer to be on, it will also prompt you for new timer settings.
 *                       - If you set "Edit Notch" to "n", you can reset the catapult whenever. However, you must use ^C (Keyboard inturrupt) to stop the program
 *                   If you want to use a timer for countdown shots, set "Turn on timer mode" to "y".
 *                    - Mode 1: Waits for the specified time, and will then start distance sensors. Once an object enters the range (see reference chart), the catapult will fire.
 *                    - Mode 2: Waits for the specified time, and will then immediately fire.
 *                    - Mode 3: Waits until an object enters range (see reference chart), and will start the timer. Once the timer ends, shot is fired.
 *                   Once setup is complete, the arm will lock in place in the down position. Load your ball.
 *                   When ready to arm the catapult, press "B" on the MicroBit. The Green LED will turn off, and the Red LED will turn on. The outer ring of the MicroBit LEDs will illuminate too.
 *                    - Make sure nothing is in front of your depth sensor when pressing B, or the catapult will fire.
 *                   TO ABORT THE FIRING AND UN-ARM THE SYSTEM: Press the "A" button while armed, and the shot will disarm.
 *                   The MicroBit LED array demonstrates how close the target is to the catapult, and how close to firing range it is. 
 *                    - When the outer ring is on, the target is far.
 *                    - When the middle ring is on, the target is nearing firing range.
 *                    - When the middle LED dot is on, the target is in range, and the catapult will fire.
 *                   When an object is within range, the shot will be fired, and the Red LED will turn off, and the Green LED will turn on.
 *                   After the shot, depending on which mode you have active, many things could happen:
 *                    - If you are using the target, either you make the basket and the program continues, or if you missed, press button "A" to continue. If prompt mode is on, you will continue to the prompt, otherwise, the catapult will wait for the "A" button to be pressed to lock the catapult arm in place once more.
 *                    - If you have prompts after every shot enabled, you will be prompted. If you want to change the notch, input the new number. If you want to continue with the current notch, input 0. If you want to exit, input 9. The catapult will wait for the "A" button to be pressed to lock the catapult arm in place once more.
 *                    - If both target and prompt mode are disabled, the catapult will wait for the "A" button to be pressed to lock the catapult arm in place once more.
 *                   The program will wait for the "B" button to be pressed again to arm the catapult.
 *                   To exit the program at any time, KeyboardInturrupt the program (^C).
 * 
 * Reference Chart:
 * Notch Setting   ~Distance (cm)
 *      1               150
 *      2               125
 *      3               120
 *      4               100
 *      5               75
 *      6               25
 *                   
 * Instructor:       Mark Bianchi
 * History:
 * Version             Description
 * 1.0.0               Initial creation and implementation of catapult controls (A/B Controls)
 * 1.1.0               Added distance sensing and refined notches on catapult, added input for notches for maximum accuracy on each shot, added trap set indicator
 * 1.1.1               Made distance more comprehensive on MicroBit with grid lights closing in on the center to demonstrate distance to firing
 * 1.2.0               Made target and target sensing, added prompts to ask user for setup on if they wanted prompts, if they had target, etc.
 * 1.2.1               Added LED control with Red and Green lights to show readiness and activity, as well as clean input statements to allow best setup
 * 1.2.2               Added Keyboard Inturrupt stopping so that all hummingbird functions sucessfully stop when terminating with ^C.
 * 1.3.0               Added timer functionality for shots, allows countdowns for proximity and firing. Updated docs to match
 */

import java.util.Scanner;

public class Catapult {
    // Sets target distance based on notch setting
    public static int setTarget(int setting) {
      int target;
      switch (setting) {
         case 1 -> target = 150;
         case 2 -> target = 125;
         case 3 -> target = 125;
         case 4 -> target = 100;
         case 5 -> target = 75;
         case 6 -> target = 25;
         default -> target = 150;
      }

      return target;
    }

    public static void main(String[] args) {
        // Initialize Hummingbird catapult and scanner
        Hummingbird catapult = new Hummingbird();
        Scanner scan = new Scanner(System.in);
        boolean locked = true;
        String red = "\u001B[31m";
        String reset = "\u001B[0m";

        // Stops all hummingbird functions if ^C is used to stop
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Keyboard interrupt");
            catapult.stopAll();
            catapult.disconnect();
            scan.close();
        }));

        // Setup for user with sanatized inputs
        int notch;
        while (true) {
            try {
                System.out.println("Input shot notch setting (1-6): ");
                notch = scan.nextInt();
                
                if (notch > 6 || notch < 1) {
                    System.out.println(red + notch + " is an invalid notch setting. Please input a number between 1 and 6." + reset);
                } else {
                    break;
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please try again.");
                scan.next();
            }
        }
        System.out.println("Want to edit notch/timer (if using timer) after each shot? (y/n)");
        String editNotch;
        while(true){
            try {
                editNotch = scan.next();
                if (!editNotch.equals("y") && !editNotch.equals("n")){
                    while(!editNotch.equals("y") && !editNotch.equals("n")){
                        System.out.println("\"" + editNotch + "\" is an invalid input. Please input \"y\" or \"n\".");
                        editNotch = scan.next();
                    }
                }
                break;
            } catch (Exception e){
                System.out.println("Invalid input. Please try again.");
                editNotch = "n";
            }
        }
        System.out.println("Using Target? (y/n)");
        String useTarget;
        while (true){
            try {
                useTarget = scan.next();
                if (!useTarget.equals("y") && !useTarget.equals("n")){
                    while(!useTarget.equals("y") && !useTarget.equals("n")){
                        System.out.println("\"" + useTarget + "\" is an invalid input. Please input \"y\" or \"n\".");
                        useTarget = scan.next();
                    }
                }
                break;
            } catch (Exception e){
                System.out.println("Invalid input. Please try again.");
                useTarget = "n";
            }
        }
        System.out.println("Turn on timer mode? (y/n)");
        String useTimer;
        while (true) { 
            try {
                useTimer = scan.next();
                if (!useTimer.equals("y") && !useTimer.equals("n")){
                    while(!useTimer.equals("y") && !useTimer.equals("n")){
                        System.out.println("\"" + useTimer + "\" is an invalid input. Please input \"y\" or \"n\".");
                        useTimer = scan.next();
                    }
                }
                break;
            } catch (Exception e) {
                System.out.println("Invalid input. Please try again.");
                useTarget = "n";
            }
        }
        boolean useTime = useTimer.equals("y");
        int timeSetting;
        int timer;
        if (useTime) {
            while (true) {
                try {
                    System.out.println("Set time setting: Delay proximity sensing to start after threshold time (1), Fire when timer ends (2), or start timer to fire when object enters range (3).");
                    timeSetting = scan.nextInt();
                    
                    if (timeSetting > 3 || timeSetting < 1) {
                        System.out.println(timeSetting + " is an invalid setting. Please input a number between 1 and 3.");
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input. Please try again.");
                    scan.next();
                }
            }
        } else {
            timeSetting = 0;
        }
        if (useTime) {
            while (true) {
                try {
                    System.out.println("Set timer in seconds.");
                    timer = scan.nextInt();
                    
                    if (timer > 3600 || timer < 1) {
                        System.out.println("Timer cannot be set greater than one hour or less than 0 seconds. Please enter a valid number.");
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input. Please try again.");
                    scan.next();
                }
            }
        } else {
            timer = 0;
        }
        // Ready servos and LEDs
        int pos = Catapult.setTarget(notch);
        if (useTarget.equals("y")){
            catapult.setLED(3, 100);
        }
        catapult.setLED(1, 0);
        catapult.setLED(2, 100);

        // Confirmation message to user
        if (notch > 7 || notch < 1){
            System.out.println("Invalid notch set. Defaulting to 1, distance of 150.");
        } else {
            System.out.println("Notch set to " + notch + ", distance of " + pos + ".");
        }

        // Lock catapult arm
        catapult.setPositionServo(1, 68);

        // Start of loop
        while (true) {
            // A - Lock catapult arm, set green LED on
            if (catapult.getButton("A") && !locked){
                catapult.setPositionServo(1,68);
                catapult.setLED(1, 0);
                catapult.setLED(2, 100);
                locked = true;
            // B - Arm catapult arm, set red LED on
            } else if (catapult.getButton("B") && locked) {
                catapult.setLED(1, 100);
                catapult.setLED(2, 0);
                locked = false;

                // Define all the time stages
                int[] fullTime = {
                    1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1
                };
                int[] highTime = {
                    0, 0, 0, 0, 0,
                    1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1
                };
                int[] midTime = {
                    0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0,
                    1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1
                };
                int[] lessTime = {
                    0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0,
                    1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1
                };
                int[] tinyTime = {
                    0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0,
                    1, 1, 1, 1, 1
                };
                // Time setting 1 - Distance sensors start after timer ends
                if (useTime && timeSetting == 1){
                    System.out.println("Timer is set: " + timer + " seconds.");
                    for(int i = timer; i >= 0; i--){
                        int stage = (i * 5) / timer;
                        switch(stage) {
                            // Light array follows stages as time decreases
                            case 5 -> catapult.setDisplay(fullTime);
                            case 4 -> catapult.setDisplay(highTime);
                            case 3 -> catapult.setDisplay(midTime);
                            case 2 -> catapult.setDisplay(lessTime);
                            case 1 -> catapult.setDisplay(tinyTime);
                            default -> catapult.setDisplay(tinyTime);
                        }
                        if (i != 0) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                    System.out.println("Timer finished. Catapult armed.");
                }
                int distance = catapult.getDistance(1);

                // Time setting 2 - Shot fires when timer ends
                if (useTime && timeSetting == 2){
                    System.out.println("Shot will be fired in: " + timer + " seconds.");
                    for(int i = timer; i >= 0; i--){
                        int stage = (i * 5) / timer;
                        switch(stage) {
                            // Light array follows stages as time decreases
                            case 5 -> catapult.setDisplay(fullTime);
                            case 4 -> catapult.setDisplay(highTime);
                            case 3 -> catapult.setDisplay(midTime);
                            case 2 -> catapult.setDisplay(lessTime);
                            case 1 -> catapult.setDisplay(tinyTime);
                            default -> catapult.setDisplay(tinyTime);
                        }
                        if (i != 0) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                    System.out.println("Timer finished. Firing.");
                } else {
                    // Sets initial LED pattern on MicroBit
                    int[] borderPattern = {
                        1, 1, 1, 1, 1,
                        1, 0, 0, 0, 1,
                        1, 0, 0, 0, 1,
                        1, 0, 0, 0, 1,
                        1, 1, 1, 1, 1
                    };
                    catapult.setDisplay(borderPattern);
                    
                    // Define the closer pattern when an object is closer
                    int[] closerPattern = {
                        0, 0, 0, 0, 0,
                        0, 1, 1, 1, 0,
                        0, 1, 0, 1, 0, 
                        0, 1, 1, 1, 0,
                        0, 0, 0, 0, 0
                    };

                    // Loop to check distance and update LED pattern until target is within range
                    while (distance > pos){
                        distance = catapult.getDistance(1);
                        if ((pos * 1.5) > distance) {
                            catapult.setDisplay(closerPattern);
                        } else {
                            catapult.setDisplay(borderPattern);
                        }
                        if (catapult.getButton("A")){
                            System.out.println("Over-riding arm status and unlocking.");
                            locked = true;
                            catapult.setLED(1, 0);
                            catapult.setLED(2, 100);
                            break;
                        }
                    }
                }
                if (locked == false) {
                    // Define pattern for when projectile is shot
                    int[] shootPattern = {
                        0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0,
                        0, 0, 1, 0, 0,
                        0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0
                    };
                    // Time Setting 3 - Timer starts when object enters proximity, will fire when timer ends
                    if (useTime && timeSetting == 3){
                    System.out.println("Object has entered range. Timer is set: " + timer + " seconds.");
                        for(int i = timer; i >= 0; i--){
                            int stage = (i * 5) / timer;
                            switch(stage) {
                                // Light array follows stages as time decreases
                                case 5 -> catapult.setDisplay(fullTime);
                                case 4 -> catapult.setDisplay(highTime);
                                case 3 -> catapult.setDisplay(midTime);
                                case 2 -> catapult.setDisplay(lessTime);
                                case 1 -> catapult.setDisplay(tinyTime);
                                default -> catapult.setDisplay(tinyTime);
                            }
                            if (i != 0) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            }
                        }
                        System.out.println("Timer finished. firing.");
                    }
                    // Display shoot pattern and turn off LEDs
                    catapult.setDisplay(shootPattern);
                    catapult.setLED(1, 0);
                    catapult.setLED(2, 100);

                    // Fires the catapult
                    catapult.setPositionServo(1, 78);

                    // Target mode
                    if (useTarget.equals("y")){
                        System.out.println("\nShot fired.");

                        // Checking loop for target hit
                        while(true){
                            if(catapult.getLight(2) < 25){
                                System.out.println("Target hit!");
                                break;
                            }
                            // Check if button A is pressed to escape
                            if (catapult.getButton("A")){
                                System.out.println("Target missed. Resetting servo.");
                                break;
                            }
                        }
                    }

                    // Check if user wants to edit notch when prompt mode is active
                    if (editNotch.equals("y")){
                        int option;
                        while (true){
                            try {
                                System.out.println("Shot fired. Set another notch (1-6), keep current (0), or exit (9).");
                                option = scan.nextInt();
                                if (option < 0 || (option > 6 && option < 9) || option > 9) {
                                    System.out.println(option + " is an invalid notch setting. Please input a number between 1 and 6.");
                                } else {
                                    break;
                                }
                            } catch (Exception e) {
                                System.out.println("Invalid input. Please try again.");
                                scan.next();
                            }
                        }

                        // Exit option
                        if (option == 9){
                            catapult.stopAll();
                            catapult.disconnect();
                            scan.close();
                            System.exit(0);
                        // Change notch
                        } else if (option != 0 && option != 9 && option <=7 && option >= 1){
                            pos = Catapult.setTarget(option);
                            System.out.println("\nNotch set to " + option + ", distance of " + pos + ".");
                        } 
                        // Edit timer
                        if (useTime) {
                            while (true) { 
                                try {
                                    System.out.println("Follow same timer settings (0), set new timer (1), or set new timer mode + time (2).");
                                    int timerOption = scan.nextInt();
                                    if (timerOption == 0) {
                                        System.out.println("Following same timer settings.");
                                        break;
                                    } else if (timerOption == 1) {
                                        // A lot of sanitation for good inputs, sets new timer
                                        while (true) {
                                            try {
                                                System.out.println("Set new timer in seconds:");
                                                timer = scan.nextInt();
                                                
                                                if (timer > 3600 || timer < 1) {
                                                    System.out.println("Timer cannot be set greater than one hour or less than 0 seconds. Please enter a valid number.");
                                                } else {
                                                    break;
                                                }
                                            } catch (Exception e) {
                                                System.out.println("Invalid input. Please try again.");
                                                scan.next();
                                            }
                                        }
                                        System.out.println("New timer set: " + timer + " seconds.");
                                        break;
                                    } else if (timerOption == 2) {
                                        // Sets new timer and mode
                                        while (true) {
                                            try {
                                                System.out.println("Set time setting: Delay proximity sensing to start after threshold time (1), Fire when timer ends (2), or start timer to fire when object enters range (3).");
                                                timeSetting = scan.nextInt();
                                                
                                                if (timeSetting > 3 || timeSetting < 1) {
                                                    System.out.println(timeSetting + " is an invalid setting. Please input a number between 1 and 3.");
                                                } else {
                                                    break;
                                                }
                                            } catch (Exception e) {
                                                System.out.println("Invalid input. Please try again.");
                                                scan.next();
                                            }
                                        }
                                        System.out.println("Time setting set to " + timeSetting + ".");
                                        while (true) {
                                            try {
                                                System.out.println("Set new timer in seconds:");
                                                timer = scan.nextInt();
                                                
                                                if (timer > 3600 || timer < 1) {
                                                    System.out.println("Timer cannot be set greater than one hour or less than 0 seconds. Please enter a valid number.");
                                                } else {
                                                    break;
                                                }
                                            } catch (Exception e) {
                                                System.out.println("Invalid input. Please try again.");
                                                scan.next();
                                            }
                                        }
                                        System.out.println("New timer set: " + timer + " seconds.");
                                        break;
                                    } else {
                                        System.out.println(timerOption + " is an invalid option.");
                                    }
                                } catch (Exception e) {
                                    System.out.println("Invalid input. Please try again.");
                                    scan.next();
                                }
                            }
                        }
                    } else if (useTarget.equals("n")) {
                        System.out.println("\nShot fired.");
                    }
                } else {
                    // Check if user wants to edit notch when prompt mode is active
                    if (editNotch.equals("y")){
                        int option;
                        while (true){
                            try {
                                System.out.println("Shot aborted. Set another notch (1-6), keep current (0), or exit (9).");
                                option = scan.nextInt();
                                if (option < 0 || (option > 6 && option < 9) || option > 9) {
                                    System.out.println(option + " is an invalid notch setting. Please input a number between 1 and 6.");
                                } else {
                                    break;
                                }
                            } catch (Exception e) {
                                System.out.println("Invalid input. Please try again.");
                                scan.next();
                            }
                        }

                        // Exit option
                        if (option == 9){
                            catapult.stopAll();
                            catapult.disconnect();
                            scan.close();
                            System.exit(0);
                        // Change notch
                        } else if (option != 0 && option != 9 && option <=7 && option >= 1){
                            pos = Catapult.setTarget(option);
                            System.out.println("\nNotch set to " + option + ", distance of " + pos + ".");
                        } 
                        // Edit timer settings
                        if (useTime) {
                            while (true) { 
                                try {
                                    System.out.println("Follow same timer settings (0), set new timer (1), or set new timer mode + time (2).");
                                    int timerOption = scan.nextInt();
                                    if (timerOption == 0) {
                                        System.out.println("Following same timer settings.");
                                        break;
                                    } else if (timerOption == 1) {
                                        // Sets new timer
                                        while (true) {
                                            try {
                                                System.out.println("Set new timer in seconds:");
                                                timer = scan.nextInt();
                                                
                                                if (timer > 3600 || timer < 1) {
                                                    System.out.println("Timer cannot be set greater than one hour or less than 0 seconds. Please enter a valid number.");
                                                } else {
                                                    break;
                                                }
                                            } catch (Exception e) {
                                                System.out.println("Invalid input. Please try again.");
                                                scan.next();
                                            }
                                        }
                                        System.out.println("New timer set: " + timer + " seconds.");
                                        break;
                                    } else if (timerOption == 2) {
                                        // Edits mode and timer
                                        while (true) {
                                            try {
                                                System.out.println("Set time setting: Delay proximity sensing to start after threshold time (1), Fire when timer ends (2), or start timer to fire when object enters range (3).");
                                                timeSetting = scan.nextInt();
                                                
                                                if (timeSetting > 3 || timeSetting < 1) {
                                                    System.out.println(timeSetting + " is an invalid setting. Please input a number between 1 and 3.");
                                                } else {
                                                    break;
                                                }
                                            } catch (Exception e) {
                                                System.out.println("Invalid input. Please try again.");
                                                scan.next();
                                            }
                                        }
                                        System.out.println("Time setting set to " + timeSetting + ".");
                                        while (true) {
                                            try {
                                                System.out.println("Set new timer in seconds:");
                                                timer = scan.nextInt();
                                                
                                                if (timer > 3600 || timer < 1) {
                                                    System.out.println("Timer cannot be set greater than one hour or less than 0 seconds. Please enter a valid number.");
                                                } else {
                                                    break;
                                                }
                                            } catch (Exception e) {
                                                System.out.println("Invalid input. Please try again.");
                                                scan.next();
                                            }
                                        }
                                        System.out.println("New timer set: " + timer + " seconds.");
                                        break;
                                    } else {
                                        System.out.println(timerOption + " is an invalid option.");
                                    }
                                } catch (Exception e) {
                                    System.out.println("Invalid input. Please try again.");
                                    scan.next();
                                }
                            }
                        }
                    } else if (useTarget.equals("n")) {
                        System.out.println("\nShot aborted.");
                    }
                }
                // Turns off MicroBit LED array
                int[] turnOff = {
                    0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0
                };
                catapult.setDisplay(turnOff);
            }
        }
    }
}
