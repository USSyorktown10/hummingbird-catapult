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
 * 1.3.1               Turned notch adjuster into a class for optimization and versatility
 * 1.3.2               Lots of optimization and condensing, cleaning up a ton of code and making methods
 */

import java.util.Scanner;

public class Catapult {
    private static final int[] OFF = new int[25];
    private static final int[] SHOOT = {0,0,0,0,0, 0,0,0,0,0, 0,0,1,0,0, 0,0,0,0,0, 0,0,0,0,0};
    private static final int[] BORDER = {1,1,1,1,1, 1,0,0,0,1, 1,0,0,0,1, 1,0,0,0,1, 1,1,1,1,1};
    private static final int[] CLOSER = {0,0,0,0,0, 0,1,1,1,0, 0,1,0,1,0, 0,1,1,1,0, 0,0,0,0,0};
    
    // Countdown stages
    private static final int[][] STAGES = {
        {0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 1,1,1,1,1}, // Tiny
        {0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 1,1,1,1,1, 1,1,1,1,1}, // Less
        {0,0,0,0,0, 0,0,0,0,0, 1,1,1,1,1, 1,1,1,1,1, 1,1,1,1,1}, // Mid
        {0,0,0,0,0, 1,1,1,1,1, 1,1,1,1,1, 1,1,1,1,1, 1,1,1,1,1}, // High
        {1,1,1,1,1, 1,1,1,1,1, 1,1,1,1,1, 1,1,1,1,1, 1,1,1,1,1}  // Full
    };

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

    // Helper class to input strings cleanly
    public static String inputString(Scanner sc, String prompt, boolean ynBounds) {
        String response;
        while (true) {
            try {
                System.out.println(prompt);
                response = sc.nextLine();
                if (ynBounds && (!response.equals("y") && !response.equals("n"))) {
                    System.out.println("\"" + response + "\" is an invalid input. Input either \"y\" or \"n\".");
                } else {
                    break;
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please try again.");
                sc.nextLine();
            }
        }
        return response;
    }

    // Helper class to input ints cleanly
    public static int inputInt(Scanner sc, String prompt, int min, int max, boolean setNotch) {
        int response;
        while (true) {
            try {
                System.out.println(prompt);
                response = sc.nextInt();
                sc.nextLine();
                if (setNotch) {
                    if (response != 0 && response != 9 && (response >=7 || response <= 1)) {
                        System.out.println("Invalid input.");
                    } else {
                        break;
                    }
                } else {
                    if (response < min || response > max) {
                        System.out.println("Invalid input. Please enter a number between " + min + " and " + max + ".");
                    } else {
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please try again.");
                sc.nextLine();
            }
        }
        return response;
    }

    public static int[] setNotch(Scanner sc, Hummingbird catapult, boolean useTime, int timer, int timeSetting, int pos, boolean aborted) {
        int option;
        if (aborted){
            option = inputInt(sc, "Shot aborted. Set another notch (1-6), keep current (0), or exit (9).", 0, 9, true);
        } else {
            option = inputInt(sc, "Shot fired. Set another notch (1-6), keep current (0), or exit (9).", 0, 9, true);
        }
        // Exit option
        if (option == 9){
            catapult.stopAll();
            catapult.disconnect();
            sc.close();
            System.exit(0);
        // Change notch
        } else if (option != 0 && option != 9 && option <=7 && option >= 1){
            pos = Catapult.setTarget(option);
            System.out.println("\nNotch set to " + option + ", distance of " + pos + ".");
        } 
        // Edit timer
        if (useTime) {
            int timerOption;
            timerOption = inputInt(sc, "Follow same timer settings (0), set new timer (1), or set new timer mode + time (2).", 0, 2, false);
            if (timerOption == 0) {
                System.out.println("Following same timer settings.");
            } else if (timerOption == 1) {
                timer = inputInt(sc, "Set new timer in seconds:", 0, 3600, false);
                System.out.println("New timer set: " + timer + " seconds.");
            } else if (timerOption == 2) {
                timeSetting = inputInt(sc, "Set time setting: Delay proximity sensing to start after threshold time (1), Fire when timer ends (2), or start timer to fire when object enters range (3).", 0, 3, false);
                System.out.println("Time setting set to " + timeSetting + ".");
                timer = inputInt(sc, "Set new timer in seconds:", 0, 3600, false);
                System.out.println("New timer set: " + timer + " seconds.");
            } else {
                System.out.println(timerOption + " is an invalid option.");
            }
        }
        System.out.println("\nSetup complete. Press A to lock catapult when ready.");
        return new int[] { pos, timer, timeSetting };
    }

    // Condensed and optimized countdown for display
    private static void runCountdown(Hummingbird h, int seconds) {
        for (int i = seconds; i >= 0; i--) {
            int stageIdx = Math.min(4, (i * 5) / (seconds == 0 ? 1 : seconds));
            h.setDisplay(STAGES[stageIdx]);
            if (i != 0) {
                try { Thread.sleep(1000); } catch (Exception e) {}
            }
        }
    }

    public static void main(String[] args) {
        // Initialize Hummingbird catapult and scanner
        Hummingbird catapult = new Hummingbird();
        Scanner scan = new Scanner(System.in);
        boolean locked = true;

        // Stops all hummingbird functions if ^C is used to stop
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Keyboard interrupt");
            catapult.stopAll();
            catapult.disconnect();
            scan.close();
        }));

        // Setup for user with sanitized inputs
        int notch = inputInt(scan, "Input shot notch setting (1-6): ", 1, 6, false);
        String editNotch = inputString(scan, "Want to edit notch/timer (if using timer) after each shot? (y/n)", true);
        String useTarget = inputString(scan, "Using Target? (y/n)", true);
        String useTimer = inputString(scan, "Turn on timer mode? (y/n)", true);
        boolean useTime = useTimer.equals("y");
        int timeSetting;
        int timer;
        if (useTime) {
            timeSetting = inputInt(scan, "Set time setting: Delay proximity sensing to start after threshold time (1), Fire when timer ends (2), or start timer to fire when object enters range (3).", 1, 3, false);
            timer = inputInt(scan, "Set timer in seconds.", 0, 3600, false);
        } else {
            timeSetting = 0;
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

                // Time setting 1 - Distance sensors start after timer ends
                if (useTime && timeSetting == 1){
                    System.out.println("Timer is set: " + timer + " seconds.");
                    runCountdown(catapult, timer);
                    System.out.println("Timer finished. Catapult armed.");
                }
                int distance = catapult.getDistance(1);

                // Time setting 2 - Shot fires when timer ends
                if (useTime && timeSetting == 2){
                    System.out.println("Shot will be fired in: " + timer + " seconds.");
                    runCountdown(catapult, timer);
                    System.out.println("Timer finished. Firing.");
                } else {
                    catapult.setDisplay(BORDER);

                    // Loop to check distance and update LED pattern until target is within range
                    while (distance > pos){
                        distance = catapult.getDistance(1);
                        if ((pos * 1.5) > distance) {
                            catapult.setDisplay(CLOSER);
                        } else {
                            catapult.setDisplay(BORDER);
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
                    // Time Setting 3 - Timer starts when object enters proximity, will fire when timer ends
                    if (useTime && timeSetting == 3){
                        System.out.println("Object has entered range. Timer is set: " + timer + " seconds.");
                        runCountdown(catapult, timer);
                        System.out.println("Timer finished. firing.");
                    }

                    // Display shoot pattern and turn off LEDs
                    catapult.setDisplay(SHOOT);
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
                        int[] results = setNotch(scan, catapult, useTime, timer, timeSetting, pos, false);
                        pos = results[0];
                        timer = results[1];
                        timeSetting = results[2];
                    } else if (useTarget.equals("n")) {
                        System.out.println("\nShot fired.");
                    }
                } else {
                    // Aborted section: This section runs when the shot is aborted by pressing "A" before shot is fired.
                    // Check if user wants to edit notch when prompt mode is active
                    if (editNotch.equals("y")){
                        if (editNotch.equals("y")){
                        int[] results = setNotch(scan, catapult, useTime, timer, timeSetting, pos, true);
                        pos = results[0];
                        timer = results[1];
                        timeSetting = results[2];
                    } else if (useTarget.equals("n")) {
                        System.out.println("\nShot aborted.");
                    }
                }
                catapult.setDisplay(OFF);
            }
        }
    }
}
}