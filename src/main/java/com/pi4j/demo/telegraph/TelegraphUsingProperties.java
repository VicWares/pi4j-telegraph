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
import com.pi4j.io.IOType;
import com.pi4j.io.binding.OnOffBinding;
import com.pi4j.io.gpio.digital.DigitalChangeListener;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.group.OnOffGroup;
import com.pi4j.io.pwm.Pwm;

import java.io.InputStream;

/**
 * <p>Main class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class TelegraphUsingProperties {

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
        InputStream propertiesStream = TelegraphUsingProperties.class.getClassLoader()
                .getResourceAsStream("pi4j.properties");

        // Initialize Pi4J with an auto context and load properties into context
        var pi4j = Pi4J.newContextBuilder().autoDetect().properties(propertiesStream).build();

        // NOTE:  The following IO instances will be looked up in from the properties
        //        loaded into the Pi4J context.  Each defined "key" includes a series
        //        of property values each prefixed with the "key" identifier followed
        //        by a property name in dot notation. i.e. "{key}.{property}".  The
        //        IO instance will use these eky prefixed properties to auto-configure
        //        the created IO instance.

        // create two hardware PWM instances (LEFT and RIGHT audio channels)
        Pwm left  = pi4j.create("left-audio-channel");
        Pwm right = pi4j.create("right-audio-channel");

        // create a digital input pin instance for the Telegraph Key
        DigitalInput key = pi4j.create("key");

        // create a digital input pin instance for the Telegraph Sounder
        DigitalOutput sounder = pi4j.create("sounder");

        // create a digital output pin instance for the LED
        //var led = dout.create(ledConfig);
        DigitalOutput led = pi4j.create("led");

        // create a group of IO instances that can all be controlled together
        OnOffGroup signal = OnOffGroup.newInstance(sounder, led, left, right);

        // bind the input changes from the Telegraph Key to the output Signal group
        key.bind(OnOffBinding.newInstance(signal));

        // add event listener for the Telegraph Key input changes
        key.addListener((DigitalChangeListener) event -> System.out.println("TELEGRAPH DEMO :: " + event));

        System.out.println("---------------------------------------------------");
        System.out.println(" [Pi4J V.2 DEMO] MORSE KEY");
        System.out.println("---------------------------------------------------");
        System.out.println(" Press the telegraph key when ready.");

        // keep the program running
        System.in.read();

        System.out.println("---------------------------------------------------");
        System.out.println("[Pi4J V.2 DEMO] SHUTTING DOWN");
        System.out.println("---------------------------------------------------");

        // shutdown Pi4J context now
        pi4j.shutdown();
    }
}
