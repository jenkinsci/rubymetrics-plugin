# 1.6.1 (work in progress)

* Report value within empty range as 100%
* Files ending with spec are counted into the tests pool  

# 1.6.0 (August 29, 2014)

* Upgrade to newer version of rake (1.8.0) and org.jenkins-ci.plugins (1.532.3) fixing JENKINS-22293 issue
* Migrate project configuration from Hudson to Jenkins style

# 1.5.0 (Mar 10, 2011)

* Do not abort when the build is unstable.
* Graphs for ratios, classes, loc; no upper bounds

# 1.4.6 (Nov 27, 2010)

* Fix notes chart
* Use an expandable textbox to set the flog directories

# 1.4.5 (Nov 18, 2010)

* Fix: Flog graph doesn't track over time

# 1.4.4 (Oct 28, 2010)

* Add rake version range

# 1.4 (05-01-2010)

* Flog support added
* Fixes rcov parser support issues

# 1.3 (30-10-2009)

* Rails notes support added
* Rails stats publisher allows to choose base directory.

# 1.2.3 (04-05-2009)

* Fixes some bugs.

# 1.2.2 (24-09-2008)

* Choose different rake version when we configure Rails stats publisher.
* Added rcov metrics configuration and health report.

# 1.2 (18-09-2008)

* Rails stats support added
* Refactored to use common classes

# 1.1 (12-09-2008)

* Solves a bug with the rcov report percentages.