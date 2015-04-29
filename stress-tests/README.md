stress-tests
============

The OSIAM stress tests are meant to run for at least 2 weeks to measure the memory usage over time. 

Default setup: 10 users who run in parallel threads performing all operations in a random order.

The single operations are:

* create a new random user: about 32%
* search for random Users: about 26%
* get a single random User: about 26%
* update a random User: about 9 %
* replace a random User: about 6 %
* delete a random User: about 2 %

The different users running to different times. There are about 20 calls every second to the OSIAM server.

Every 12 h (7 p.m, 7 a.m) the memory usage is collected and saved.

Technology
----------

The OSIAM Stress Tests are written in Java and using the [quartz scheduler](http://quartz-scheduler.org).
The memory usage is collected by dropwizard metrics in the OSIAM server and retrieved via HTTP.

Setup
-----

After completing the [Installation](../INSTALLATION.md), you have to import an extension which will be need to run these tests.
Simple run the [sql file](https://github.com/osiam/test-suites/blob/master/stress-tests/src/main/resources/sql/extension_registration.sql).

How to run the stress tests
---------------------------

The stress-tests contains a main class called StressRequester. Simply run this class and the stress-tests will start.
You can also create the jar 

    $ mvn clean package

and run it with

    $ java -jar target/stress-tests-<VERSION>.jar

If you want to modify the test parameters like the number of users and the number of calls you can do this in this
StressRequester class.

The test results will be written into a 'metricData.csv' file. The Aggregator Job will start a 7 p.m. the day you
started. So don't wonder if no csv will be created until this time.
