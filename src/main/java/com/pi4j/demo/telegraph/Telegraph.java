package com.pi4j.demo.telegraph;
/*-
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: DEMO  :: Telegraph Demo version 9.0.H
 * FILENAME      :  Telegraph.java
 * Run with:  sudo java --module-path . --module com.pi4j.demo.telegraph/com.pi4j.demo.telegraph.Telegraph
 * Transfer directory to Pi with: scp -r "${PWD}" pi@raspberrypi.local:
 * Clear directory with: rm -r "${PWD}"
 * Files in Pi are at: /Users/vicwintriss/git/pi4j-example-telegraph/target/distribution
 * !!!!!!Don't lose this...easy start for Maven app:
 * mvn archetype:generate -DarchetypeGroupId=org.apache.maven.archetypes -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4
 * **********************************************************************
 */
import com.pi4j.Pi4J;
import com.pi4j.event.ShutdownListener;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
public class Telegraph
{
    public static final int STEP_PIN = 27;  // DIGITAL OUTPUT STEP PIN IO27 (connector pin 13)
    public static final int DIR_PIN = 17;  // DIGITAL OUTPUT DIR PIN IO17 (connector pin 11)
    public static final int ENABLE_PIN = 22;  // DIGITAL OUTPUT DIR PIN IO17 (connector pin 11)
    public static void main(String[] args) throws Exception
    {
        // configure default lolling level, accept a log level as the fist program argument
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "INFO");
        // instruct PIGPIO which remote Raspberry Pi to connect to
        //System.setProperty("pi4j.host", "127.0.0.1");
        //System.setProperty("pi4j.pigpio.remote", "false");
        // Initialize Pi4J with an auto context
        var pi4j = Pi4J.newAutoContext();
        var ledConfig = DigitalOutput.newConfigBuilder(pi4j)
                .id("step")
                .name("Telegraph LED Flasher")
                .address(STEP_PIN)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output");
        var stepConfig = DigitalOutput.newConfigBuilder(pi4j)
                .id("dir")
                .name("Telegraph LED Flasher")
                .address(DIR_PIN)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output");

        var enableConfig = DigitalOutput.newConfigBuilder(pi4j)
                .id("enable")
                .name("Telegraph LED Flasher")
                .address(DIR_PIN)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output");
        var step = pi4j.create(ledConfig);
        var dir = pi4j.create(stepConfig);
        var enable = pi4j.create(enableConfig);
        System.out.println("---------------------------------------------------");
        System.out.println(" Version 9.0.H-VERSION");
        System.out.println("---------------------------------------------------");
        pi4j.registry().describe().print(System.out);
        System.out.println("---------------------------------------------------");
        step.high();
        dir.high();
        enable.low();//Enable stepper motor
        for (int i = 0; i < 500; i++)
        {
            System.out.print(i + " ");
            if (step.equals(DigitalState.HIGH))
            {
                step.low();
                System.out.print(" STEP low ");
            }
            else
            {
                step.high();
                System.out.println(" STEP high");
            }
            Thread.sleep(10);
            if (i % 10 == 0)
            {
                System.out.println("dir change,  " );
                dir.toggle();
            }
        }
        // add a shutdown listener
        pi4j.addListener((ShutdownListener) shutdownEvent -> {
            enable.high();//Remove power from stepper motor
            System.out.println("---------------------------------------------------");
            System.out.println("[Pi4J V.2 DEMO] SHUTTING DOWN");
            System.out.println("---------------------------------------------------");
        });
        // shutdown Pi4J context now
        pi4j.shutdown();
    }
}
