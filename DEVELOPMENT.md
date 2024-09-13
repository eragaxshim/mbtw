https://docs.fabricmc.net/develop/getting-started/launching-the-game

Install IntelliJ IDEA, Minecraft Development plugin

Download the JetBrains Runtime JDK:

https://github.com/JetBrains/JetBrainsRuntime/releases/tag/jbr-release-21.0.3b509.15

(use binaries for launching IntelliJ IDEA)

Extract it, then go to Project Structure -> JDK -> Add JDK from disk and select the folder containing bin, etc. from the extracted file.

Make sure to also select it for the Gradle JDK (Build Tools -> Gradle -> Gradle JVM)

Go to the Gradle tab on the right -> Reload All Gradle Projects

Now we will edit the run configurations, but we must find the Mixin library jar. Find in External Libraries the sponge-mixin jar, copy the absolute path. This could be:

`C:\Users\eragaxshim\.gradle\caches\modules-2\files-2.1\net.fabricmc\sponge-mixin\0.15.3+mixin.0.8.7\51ee0a44ab05f6fddd66b09e66b3a16904f9c55d\sponge-mixin-0.15.3+mixin.0.8.7.jar`

Then the VM argument is:

`-javaagent:"C:\Users\eragaxshim\.gradle\caches\modules-2\files-2.1\net.fabricmc\sponge-mixin\0.15.3+mixin.0.8.7\51ee0a44ab05f6fddd66b09e66b3a16904f9c55d\sponge-mixin-0.15.3+mixin.0.8.7.jar"`

Also add `-XX:+AllowEnhancedClassRedefinition`