# pub-temp

Repository for temporary shared code

## demo project

There is no purpose about this project. It must be done.

### Prerequisites

The following packages must be installed:

* maven:3.6.3
* java(openjdk):11.0.23

Compatibility note: everything should be compatible for MacOsX/Windows users.
Take care to the binary path.

### Download the Chrome webdriver

Download the chromedriver binary from googleapis:

	$ wget https://storage.googleapis.com/chrome-for-testing-public/125.0.6422.141/linux64/chromedriver-linux64.zip

If you are using another OS or an outdated version of Chrome, please download the related chromedriver.
Finally, unzip the archive:

	$ unzip chromedriver-linux64.zip

### Define the path of the binary

Edit the following file to change the binary path to the chromedriver: demo/src/main/java/com/qadl/CommonLibs.java 
The default binary is set to: /usr/bin/chromedriver

### Compile & Run the targets

From the repository, execute the following commands:

	$ cd demo/
	$ mvn compile

#### Exercice 1

Execute the following command to run the exercise 1:

	$ mvn exec:java -Dexec.mainClass=com.qadl.FillUpCart

#### Exercice 2

Execute the following command to run the exercise 2:

	$ mvn exec:java -Dexec.mainClass=com.qadl.AmazonCrawler

#### Exercice 3

Execute the following command to run the exercise 3:

	$ mvn exec:java -Dexec.mainClass=com.qadl.ApiTest
