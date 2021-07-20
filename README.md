# MDP Module

This is the open source software project `MBT-module` for testing under uncertainty.
The repo contains sources and working examples.

## How do I get set up?

The package contains a Java software project having the following external dependencies:
* the [R environment](https://www.r-project.org/)
* additional R packages: [rJava](https://cran.r-project.org/web/packages/rJava/index.html), [MCMCpack](https://cran.r-project.org/web/packages/MCMCpack/index.html), [HDInterval](https://cran.r-project.org/web/packages/HDInterval/index.html).

Once the dependencies have been installed, set up the following system-dependent variables:
* set your `JRI path` in the [build.gradle](build.gradle) file (line 48) as JVM option `java.library.path`;
* set your `R_HOME` environment variable (e.g., `export R_HOME=/my/r_home/path`).

To get your `JRI path` open a `R` console and execute `.libPaths()`.
The `rJava/jri` should be contained in one of the listed directories.
For instance,

```
# R console
> .libPaths()
[1] "/usr/local/lib/R/3.5/site-library"        
[2] "/usr/local/Cellar/r/3.5.2_2/lib/R/library"

# Bash
> cd /usr/local/lib/R/3.5/site-library/rJava/jri
> pwd
/usr/local/lib/R/3.5/site-library/rJava/jri <-- My JRI path!
```

To get your `R_HOME` open a `R` console and execute `R.home()`.
For instance,

```
# R console
> R.home()
[1] "/usr/local/Cellar/r/3.5.2_2/lib/R" <-- My R_HOME path!
```


To build the application execute:
```
gradle clean build
```

The above line requires the [Gradle build tool](https://gradle.org/).
If you don't have it you can use the [Gradle wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) supplied inside the project.

## How do I run it?

After `build`, you can run the application by executing the following line:
```
gradle run -PappArgs="['-i', 'src/main/resources/rescuerobot.jmdp']"
```

By default, this runs the MBT module using the **distance** strategy (and **bounds** termination condition) to test the RescueRobot running example.

To change the test selection strategy and the adopted scenario,
change:
* the *model* file (i.e., `rescuerobot.jmdp`); and
* the *test harness* file (i.e., `EventHandler.aj`).

Other models and test harness files are available inside the folder `src/main/resources`.


## License

See the [LICENSE](LICENSE.txt) file for license rights and limitations (GNU GPLv3).

## Who do I talk to?

[Matteo Camilli](matteo.camilli@unibz.it)
