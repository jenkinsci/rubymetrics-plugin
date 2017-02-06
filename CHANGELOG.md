# 1.6.5
February 7, 2017

* Add ability to use environmental variables in path to reports (#32)

# 1.6.4
September 22, 2016

* Add support for Pipeline jobs (#28)

# 1.6.3
November 24, 2014

* Enable plug-in to work with older and LTS Jenkins releases (#25).

# 1.6.2
September 2, 2014

* Changes RcovFileResult class to avoid retaining the source code of every file in the coverage report in memory for every build.

# 1.6.1
September 2, 2014

* Report value within empty range as 100%.
* Files ending with spec are counted into the tests pool.

# 1.6.0
August 29, 2014

* Upgrade to newer version of rake (1.8.0) and org.jenkins-ci.plugins (1.532.3) fixing JENKINS-22293 issue.
* Migrate project configuration from Hudson to Jenkins style.

# 1.5.0
March 10, 2011

* Do not abort when the build is unstable.
* Graphs for ratios, classes, loc; no upper bounds.

# 1.4.6
November 27, 2010

* Fix notes chart
* Use an expandable textbox to set the flog directories

# 1.4.5
November 18, 2010

* Fix: Flog graph doesn't track over time.

# 1.4.4
October 28, 2010

* Add rake version range

# 1.4
January 5, 2010

* Flog support added.
* Fixes rcov parser support issues.

# 1.3
October 30, 2009

* Rails notes support added.
* Rails stats publisher allows to choose base directory.

# 1.2.3
May 4, 2009

* Fixes some bugs.

# 1.2.2
September 24, 2008

* Choose different rake version when we configure Rails stats publisher.
* Added rcov metrics configuration and health report.

# 1.2
September 18, 2008

* Rails stats support added.
* Refactored to use common classes.

# 1.1
September 12, 2008

* Solves a bug with the rcov report percentages.
