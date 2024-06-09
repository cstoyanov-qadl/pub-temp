# pub-temp

Repository for temporary shared code

## demo project

### Prerequisites

The following packages must be installed:

* maven:3.6.3
* java:11.0.23


### Download the Chrome webdriver

Download the chromedriver binary from googleapis:

$ wget https://storage.googleapis.com/chrome-for-testing-public/125.0.6422.141/linux64/chrome-linux64.zip
$ wget https://storage.googleapis.com/chrome-for-testing-public/125.0.6422.141/linux64/chromedriver-linux64.zip

If you are using another OS or an outdated version of Chrome, please download the related chromedriver.
Finally, unzip the archive:

$ unzip chrome-linux64.zip
$ unzip chromedriver-linux64.zip

### Define the path of the binary

Edit the following file to change the binary path to the chromedriver: demo/src/main/java/com/qadl/CommonLibs.java 

### Compile & Run the targets

$ cd demo/
$ mvn compile

#### Exercice 1

$ mvn exec:java -Dexec.mainClass=com.qadl.FillUpCart


#### Exercice 2

$ mvn exec:java -Dexec.mainClass=com.qadl.AmazonCrawler

#### Exercice 3

$ mvn exec:java -Dexec.mainClass=com.qadl.FillUpCart
