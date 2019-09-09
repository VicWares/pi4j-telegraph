package com.pi4j.demo.telegraph;
/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: DEMO  :: Telegraph Demo
 * FILENAME      :  TelegraphUsingDI.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2019 Pi4J
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.pi4j.Pi4J;
import com.pi4j.annotation.*;
import com.pi4j.context.Context;
import com.pi4j.io.binding.OnOffBinding;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.io.group.OnOffGroup;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmType;

import java.util.concurrent.Callable;

/**
 * <h2>TelegraphUsingAnnotatedDI Sample</h2>
 * <p>This example utilizes the new Pi4J annotation framework to perform runtime dependency injection
 * to wire up and configure the I/O interfaces with Pi4J.  This is a very declarative style/approach to using
 * to using the Pi4J APIs.</p>
 * <p>This project is available on <a href="https://github.com/Pi4J/pi4j-demo-telegraph">GitHub</a></p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class TelegraphUsingAnnotatedDI {

    public static final int PWM_PIN_RIGHT         = 18;  // PWM CHANNEL 0 (RIGHT)
    public static final int PWM_PIN_LEFT          = 19;  // PWM CHANNEL 1 (LEFT)
    public static final int TELEGRAPH_KEY_PIN     = 21;  // DIGITAL INPUT PIN
    public static final int TELEGRAPH_SOUNDER_PIN = 20;  // DIGITAL OUTPUT PIN
    public static final int LED_PIN               = 26;  // DIGITAL OUTPUT PIN

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link String} objects.
     * @throws Exception if any.
     */
    public static void main(String[] args) throws Exception {
        // configure default lolling level, accept a log level as the fist program argument
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "INFO");

        // instruct PIGPIO which remote Raspberry Pi to connect to
        System.setProperty("pi4j.host", "rpi3bp.savage.lan");
        System.setProperty("pi4j.pigpio.remote", "true");

        // Pi4J cannot perform dependency injection on static classes
        // we will create a container instance to run our example
        RuntimeContainer container = new TelegraphUsingAnnotatedDI.RuntimeContainer();
        Pi4J.newAutoContext().inject(container);
        container.call();
    }

    public static class RuntimeContainer implements Callable<Void> {

        @Inject
        Context pi4j;

        @Register("sounder")
        @Name("Telegraph Sounder")
        @Address(TELEGRAPH_SOUNDER_PIN)
        @ShutdownState(DigitalState.LOW)
        @InitialState(DigitalState.LOW)
        @WithProvider("pigpio-digital-output")
        private DigitalOutput sounder;

        @Register("led")
        @Name("Telegraph LED Flasher")
        @Address(LED_PIN)
        @ShutdownState(DigitalState.LOW)
        @InitialState(DigitalState.LOW)
        @WithProvider("pigpio-digital-output")
        private DigitalOutput led;

        @Register("left-audio-channel")
        @Name("Telegraph Speaker (LEFT)")
        @Address(PWM_PIN_LEFT)
        @WithPwmType(PwmType.HARDWARE)
        @DutyCycle(50)    // 50%
        @Frequency(800)   // 800Hz
        @ShutdownValue(0)
        @InitialValue(0)
        @WithProvider("pigpio-pwm")
        private Pwm left;

        @Register("right-audio-channel")
        @Name("Telegraph Speaker (RIGHT)")
        @Address(PWM_PIN_RIGHT)
        @WithPwmType(PwmType.HARDWARE)
        @DutyCycle(50)    // 50%
        @Frequency(800)   // 800Hz
        @ShutdownValue(0)
        @InitialValue(0)
        @WithProvider("pigpio-pwm")
        private Pwm right;

        @Register
        @AddMember("sounder")
        @AddMember("led")
        @AddMember("left-audio-channel")
        @AddMember("right-audio-channel")
        OnOffGroup signal;

        @Register
        @AddMember({"sounder", "led", "left-audio-channel", "right-audio-channel"})
        @AddMember("led")
        OnOffBinding onOffBinding;

        @Register("key")
        @Name("Telegraph Key")
        @Address(TELEGRAPH_KEY_PIN)
        @Debounce(300L)
        @Pull(PullResistance.PULL_DOWN)
        @WithProvider("pigpio-digital-input")
        @AddBinding("onOffBinding")
        private DigitalInput key;

        // setup a digital input event listener to listen for any value changes on the digital input
        // using a custom method with a single event parameter
        @OnEvent("key")
        private void onDigitalInputChange(DigitalChangeEvent event){
            System.out.println("TELEGRAPH DEMO :: " + event);
        }

        @Override
        public Void call() throws Exception {

            // bind the input changes from the Telegraph Key to the output Signal group
            //key.bind(OnOffBinding.newInstance(signal));
            //key.bind(onOffBinding);

            System.out.println("---------------------------------------------------");
            System.out.println(" [Pi4J V.2 DEMO] TELEGRAPH (Using DI/Annotations)");
            System.out.println("---------------------------------------------------");
            pi4j.registry().describe().print(System.out);
            System.out.println("---------------------------------------------------");
            System.out.println(" Press the telegraph key when ready.");

            // keep the program running
            System.in.read();


            System.out.println("---------------------------------------------------");
            System.out.println("[Pi4J V.2 DEMO] SHUTTING DOWN");
            System.out.println("---------------------------------------------------");

            // shutdown Pi4J context now
            pi4j.shutdown();

            return null;
        }
    }
}
