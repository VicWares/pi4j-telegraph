
 Pi4J V.2 :: Demo - Telegraph
==========================================================================

---

## DISCLAIMER:: EXPERIMENTAL VERSION

  This demo project is using Pi4J Version 2.0 which is still in development. This demo is provided as sample
  code to demonstrate some of the new features and usage of Pi4J Version 2.0.  Pi4J Version 2.0 is considered 
  experimental and is not ready for production environments at this time.  Pi4J Version 2.0 reserves the 
  right to change any APIs without notice until this version is finally released.
  
  **NOTE:** It is important to note that the pin numbering scheme for Pi4J Version 2.0 has changed.
  Pi4J now adopts the BCM pin numbering scheme as the default numbering scheme and no longer uses
  the WiringPi pin numbering scheme.  

---

## PROJECT INFORMATION

  Project website: https://v2.pi4j.com/ <br />
  Pi4J Community Forum (*new*): https://forum.pi4j.com/ <br />
  Version 2.0 Project Discussions (*new*): https://forum.pi4j.com/category/6/version-2-0 <br />
  <br />
  Pi4J Version 2.0 - Snapshot builds are available from:
   *  [Sonatype OSS] https://oss.sonatype.org/index.html#nexus-search;quick~pi4j

  Copyright (C) 2012-2019 Pi4J

## LICENSE

  Pi4J Version 2.0 and later is licensed under the Apache License,
  Version 2.0 (the "License"); you may not use this file except in
  compliance with the License.  You may obtain a copy of the License at:
      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.


## PROJECT OVERVIEW

  This demonstration project provides the following source files that all perform
  the exact same functionality.  Each of the source examples demonstrate
  different approaches/styles that you can use to utilize the Pi4J libraries
  to interact with the I/O capabilities of the Raspberry Pi.

  * **Telegraph** -- This example uses standard and straight-forward/plain-old 
  Java code to utilize Pi4J.

  * **TelegraphUsingAnnotatedDI** (Dependency Injection) -- This example utilizes the  
  new Pi4J annotation framework to perform runtime dependency injection to wire up I/O
  interfaces to Pi4J.  This is a very declarative style/approach to using the Pi4J
  APIs.  The `@Register` annotation is used to create each IO instance along with
  a series of additional annotations to configure the I/O instance. 

  * **TelegraphUsingProperties** -- This example uses standard and 
  straight-forward/plain-old Java code to utilize Pi4J; however, it loads
  much of the I/O configuration from a properties file opposed to having the
  configuration hard-coded in your source code.

  * **TelegraphUsingAutoInjection** -- This example uses an optional Pi4J feature to
  automatically register and inject I/O instances in the Pi4J Registry from
  declared I/O configurations defined in properties files.

  * **TelegraphUsingAnnotatedAI** (Auto Injection) -- This example uses an optional 
  Pi4J feature to automatically register and inject I/O instances in the Pi4J Registry
  from declared I/O configurations defined in properties files.  This example also
  injects the I/O instances into the program via `@Inject` annotations.

  This demonstration will use the following I/O capabilities of the Raspberry Pi:
  
  * **Digital Input**  -- This demo includes a single digital input that is wired
  up to a telegraph key to listen for input from the telegraph operator.
     
  * **Digital Output**  -- This demo includes two digital outputs that wire up to 
  a telegraph sounder (a physical electro-mechanical device) and a LED (light 
  emitting diode) to both audible and visually monitor the telegraph signals (morse
  code). 

  * **PWM (Pulse Width Modulation)** -- This demo includes two PWM outputs that
  wire up to a LEFT and RIGHT audio channel on an amplified speaker.  This creates 
  a tone to audible monitor the telegraph signals (morse code). 

## RUNTIME DEPENDENCIES

  This project used Pi4J V.2 which has the following runtime dependency requirements:
  - [**SLF4J (API)**](https://www.slf4j.org/)
  - [**SLF4J-SIMPLE**](https://www.slf4j.org/)
  - [**PIGPIO Library**](http://abyz.me.uk/rpi/pigpio) (for the Raspberry Pi) - This 
    dependency comes pre-installed on recent Raspbian images.  However, you can also 
    download and install it yourself using the instructions found 
    [here](http://abyz.me.uk/rpi/pigpio/download.html).  (*A minimum version of `71` 
    is recommended at the time of this writing*)

## BUILD DEPENDENCIES & INSTRUCTIONS

  This project is build using [Apache Maven](https://maven.apache.org/) 3.6 
  (or later) and Java 11 JDK (or later).  These prerequisites must be installed 
  prior to building this project.  The following command can be used to download 
  all project dependencies and compile the Java module.  You can build this 
  project on you workstation or directly on a Raspberry Pi.  
 
  ```text
  mvn clean install
  ```
  Once the build is complete and was successful, you can find the compiled 
  artifacts in the `target` folder.  Specifically all dependency modules (JARs)
  and a simple `run.sh` bash script will be located in the `target/distribution` 
  folder.  These are all the required files needed to distribute (copy) to your
  Raspberry Pi to run this project.  
  
  Alternatively, you can use one of the following commands to launch this program 
  from the folder where you copied all the distribution files on the Raspberry Pi:
  
  - `java --module-path . --module com.pi4j.demo.telegraph/com.pi4j.demo.telegraph.Telegraph`
  - `java --module-path . --module com.pi4j.demo.telegraph/com.pi4j.demo.telegraph.TelegraphUsingAnnotatedDI`
  - `java --module-path . --module com.pi4j.demo.telegraph/com.pi4j.demo.telegraph.TelegraphUsingProperties`
  - `java --module-path . --module com.pi4j.demo.telegraph/com.pi4j.demo.telegraph.TelegraphUsingAutoInjection`
  - `java --module-path . --module com.pi4j.demo.telegraph/com.pi4j.demo.telegraph.TelegraphUsingAnnotatedAI`

## WIRING DIAGRAM

  This diagram is using a Raspberry Pi 3B Plus; however any supported Raspberry Pi 
  model can be substituted.  You will just need to verify the GPIO pins and pin numbers 
  for the Raspberry Pi model you decide to use.  
  
  The LED used in this example is not a standard LED that can be driven directly from 
  the GPIO pins.  The LED used here is a **Super Bright LED** (8mm 3.3VDC) that requires 
  more current than the Raspberry Pi GPIO pins can sink/source.  Thus for both the telegraph 
  sounder and the super bright LED we are using a mosfet to switch/control these devices.  
  The mosfet is controlled via the Raspberry Pi GPIO pins and thus in turns allows or 
  restricts current to the device. 

  ![wiring-diagram](assets/wiring-diagram.png)

  **NOTE:** It is important to note that the pin numbering scheme for Pi4J Version 2.0 has changed.
  Pi4J now adopts the BCM pin numbering scheme as the default numbering scheme and no longer uses
  the WiringPi pin numbering scheme.  
