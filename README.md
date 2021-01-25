# MDP Module

This is the open source software project `MBT-module` for testing under uncertainty.
The repo contains sources and working examples.

The version in the current branch does not use the `rJava` package.

## How do I get set up?

To build the application execute:
```
./gradlew clean build
```

The above line requires the [Gradle build tool](https://gradle.org/).
If you don't have it you can use the [Gradle wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) supplied inside the project.

## How do I run it?

After `build`, you can run the application by executing the following line:
```
gradle run -PappArgs="['-i', 'src/main/resources/safehome_u50.jmdp']"
```

By default, this runs the MBT module using **distance** test selection strategy within the SafeHome running example.

To change the test selection strategy and the adopted scenario,
change:
* the *model* file (i.e., `safehome_u50.jmdp`); and
* the *test harness* file (i.e., `EventHandler.aj`).

All the models and test harness files used to produce the experimental data presented in the paper are supplied inside the `src/main/resources` directory.

## License

See the [LICENSE](LICENSE.txt) file for license rights and limitations (GNU GPLv3).

## Who do I talk to?

Anonymous Author(s) for double-blind review process.
