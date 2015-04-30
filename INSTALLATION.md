Installation to run the test-suites
===================================

**Info:** If you want to run the test-suites as "deployed" master version all 
dependencies will be loaded automatically from Maven. 

If you want to run the tests against your changed code you need to [install all required projects](#install-required-projects).

# Install this tests

Just clone this project:

    $ git clone https://github.com/osiam/test-suites.git

# Install required projects

To do this please follow the described steps:

Before running the tests you need to clone the following projects

* Scim-schema: [https://github.com/osiam/scim-schema](https://github.com/osiam/scim-schema)
* Sever: [https://github.com/osiam/server](https://github.com/osiam/server)
* connector4java: [https://github.com/osiam/connector4java](https://github.com/osiam/connector4java)

Next clone the projects:

    $ git clone https://github.com/osiam/scim-schema.git
    $ git clone https://github.com/osiam/server.git
    $ git clone https://github.com/osiam/connector4java.git
   
Then switch to the fetched folders and install the projects with
    
    $ mvn clean install
