package com.pi4j.demo.telegraph;
/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: DEMO  :: Telegraph Demo
 * FILENAME      :  Telegraph.java
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
import com.pi4j.event.ShutdownListener;
import com.pi4j.io.binding.OnOffBinding;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalStateChangeListener;
import com.pi4j.io.group.OnOffGroup;
import com.pi4j.io.pwm.Pwm;

import java.io.InputStream;

/**
 * <h2>TelegraphUsingAutoInjection Sample</h2>
 * <p>This example also uses standard and straight-forward/plain-old Java code to utilize Pi4J;
 *    however, it loads much of the I/O configuration from a properties file opposed to having
 *    the configuration hard-coded in your source code.</p>
 * <p>This project is available on <a href="https://github.com/Pi4J/pi4j-demo-telegraph">GitHub</a></p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class TelegraphUsingAutoInjection {

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link String} objects.
     * @throws Exception if any.
     */
    public static void main(String[] args) throws Exception {
        // configure default lolling level, accept a log level as the fist program argument
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "INFO");

        // get properties files/stream from embedded resource file
        InputStream propertiesStream = TelegraphUsingAutoInjection.class.getClassLoader()
                .getResourceAsStream("pi4j.properties");

        // Initialize Pi4J with an auto context and load properties into context
        var pi4j = Pi4J.newContextBuilder()
                .autoDetect()
                .autoInject()  // <--THIS WILL ATTEMPT TO AUTO REGISTER AND INJECT I/O
                               //    INSTANCES FROM PROPERTIES INTO Pi4J CONTEXT/REGISTRY
                .properties(propertiesStream)
                .build();

        // get the existing registered I/O instances from the Pi4J IO Registry
        DigitalInput key      = pi4j.io("key");
        DigitalOutput sounder = pi4j.io("sounder");
        DigitalOutput led     = pi4j.io("led");
        Pwm left              = pi4j.io("left-audio-channel");
        Pwm right             = pi4j.io("right-audio-channel");

        // create a group of IO instances that can all be controlled together
        OnOffGroup signal = OnOffGroup.newInstance(sounder, led, left, right);

        // bind the input changes from the Telegraph Key to the output Signal group
        key.bind(OnOffBinding.newInstance(signal));

        // add event listener for the Telegraph Key input changes
        key.addListener((DigitalStateChangeListener) event -> System.out.println("TELEGRAPH DEMO :: " + event));

        System.out.println("---------------------------------------------------");
        System.out.println(" [Pi4J V.2 DEMO] TELEGRAPH (Using Auto Injection)");
        System.out.println("---------------------------------------------------");
        pi4j.registry().describe().print(System.out);
        System.out.println("---------------------------------------------------");
        System.out.println(" Press the telegraph key when ready.");

        // add a shutdown listener
        pi4j.addListener((ShutdownListener) shutdownEvent -> {
            System.out.println("---------------------------------------------------");
            System.out.println("[Pi4J V.2 DEMO] SHUTTING DOWN");
            System.out.println("---------------------------------------------------");
        });

        // keep the program running until we see user input
        System.in.read();

        // shutdown Pi4J context now
        pi4j.shutdown();
    }
}
