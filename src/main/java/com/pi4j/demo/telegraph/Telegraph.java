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
import com.pi4j.io.binding.OnOffBinding;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.io.group.OnOffGroup;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmType;
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalOutputProvider;

/**
 * <p>Main class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class Telegraph {

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

        // Initialize Pi4J with an auto context
        var pi4j = Pi4J.newAutoContext();

        // create PWM config
        var pwmConfig = Pwm.newConfigBuilder()
                .pwmType(PwmType.HARDWARE)
                .dutyCycle(50)  // 50%
                .frequency(800) // 800Hz
                .shutdown(0)
                .initial(0)
                .provider("pigpio-pwm");

        // create a DIN config for Telegraph Key
        var keyConfig = DigitalInput.newConfigBuilder()
                .id("key")
                .name("Telegraph Key")
                .address(TELEGRAPH_KEY_PIN)
                .pull(PullResistance.PULL_DOWN)
                .debounce(3000L)
                .provider("pigpio-digital-input");

        // create a digital input config Telegraph Sounder
        var sounderConfig = DigitalOutput.newConfigBuilder()
                .id("sounder")
                .name("Telegraph Sounder")
                .address(TELEGRAPH_SOUNDER_PIN)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output");


        // create a DIN config Telegraph Sounder
        var ledConfig = DigitalOutput.newConfigBuilder()
                .id("led")
                .name("Telegraph LED Flasher")
                .address(LED_PIN)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider(PiGpioDigitalOutputProvider.class);

        // create two hardware PWM instances (LEFT and RIGHT audio channels)
        var left  = pi4j.create(pwmConfig.address(PWM_PIN_LEFT).id("left-audio-channel"));
        var right = pi4j.create(pwmConfig.address(PWM_PIN_RIGHT).id("right-audio-channel"));

        // create a digital input pin instance for the Telegraph Key
        var key = pi4j.create(keyConfig);

        // create a digital input pin instance for the Telegraph Sounder
        var sounder = pi4j.create(sounderConfig);

        // create a digital output pin instance for the LED
        //var led = dout.create(ledConfig);
        var led = pi4j.create(ledConfig);

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
