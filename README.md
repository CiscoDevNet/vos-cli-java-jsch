# vos-cli-java-jsch

## Overview

This sample Visual Studio Code project demonstrates basic CLI automation/scraping for Cisco Voice Operating Systems (e.g. CUCM) using the [Java Secure Channel (JSch)](http://www.jcraft.com/jsch/) SSH library.

> **Note:** Operating the VOS CLI via programmatic SSH commands is not supported by Cisco, as the CLI is intended for human (not machine) interaction, and does not represent a stable/documented interface.  However, this technique can be useful in non-production/development/testing scenarios.

## Requirements

* Java 1.8+
* [JSch library](http://www.jcraft.com/jsch/) file, e.g. `jsch-X.X.XX.jar` (This project contains the `jsch-0.1.55.jar` version.)

**Tested with:**

* Ubuntu 22.04
* OpenJDK 1.8.0 / 11.0.21
* JSCH 0.1.55
* CUCM 14

## Getting started

* Configure the `VOS_XXX` environment variables for your target VOS host:

  **If running the sample in VS Code:**
  
  * Edit `.vscode/launch.json` and configure the `VOS_XXX` environment variables for your target host.

    (Be sure to save the file).

  **If running from the terminal:**

  *  Set the environment variables in the terminal, e.g. on Linux:

     ```bash
     export VOS_USER_NAME=
     export VOS_USER_PASSWORD=
     export VOS_HOST_NAME=
     ```

     (Be sure to inlude the values specific to your VOS host.)

* (Optional) You may wish to download the latest version of the [JSch library](http://www.jcraft.com/jsch/) if it is newer than the one included in this project.

  Place it in `lib/`

* Launch the sample:

  * **From VS Code:**:
  
    Select the the **Run and Debug** tab, then click the green arrow on the **Launch vos_cli_java_jsch** drop-down.

    Or, just press **F5**

  * **From the terminal**:

    ```bash
    java -cp bin:lib/jsch-0.1.55.jar vos_cli_java_jsch
    ```

## Hints

* Modify `src/vos_cli_java_jsch.java` to change, add or remove `expect()` and `send()` function calls to construct your desired automation sequence.

  * `send(String command)` - Send the string to the CLI, plus a linefeed (`\ln`).

  * `expect(String regex, int timeout)` - Wait for the indicated string to appear in the SSH response output; the string supports  [Java regular expression syntax](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html), e.g.:
    
    ```java
    expect("admin:$", 30);
    ```

    A timeout in seconds must be specified.  If this timeout is reached, an `ExpectTimedOutException` is thrown.


* TODO: CLI response output is currently just printed to the console.  It would be useful in some scenarios to have cleaner access to that text output for the purposes of parsing data from responses.
