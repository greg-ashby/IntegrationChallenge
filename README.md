# Greg Ashby's Integration Challenge

This is a web application to demonstrate integration with App Direct's marketplace. It implements the main subscription APIs (create, cancel, change, and status), as well as openId single-sign-on.

If you'd like to see it tie into more integrations, please let me know - I'm happy to do more if needed.

## Source Code Overview

This application is based on the SparkJava web application web framework and uses FreeMarker for generating the pages. It also requires a database, but should work with either MySQL or postgresql.

NOTE: this utilizes Java 8 features (e.g. lambdas) so needs to be run with a 1.8 JRE

#### Code Structure

*   com.gregashby.challenge.**MyApp** <--main application class, defines the SparkJava routes

*   com.gregashby.challenge.**accounts** <-- contains classes for interacting with the accounts database table

*   com.gregashby.challenge.**db** code for initializing the database for deployment and unit tests

*   com.gregashby.challenge.**handlers** <-- the code for actually handling requests. See below for an introduction

*   com.gregashby.challenge.**json** <-- classes for parsing json responses from AppDirect's APIs and generating json responses

*   com.gregashby.challenge.**utils** <-- a few utility methods

*   src/**test**/java <-- a few unit tests

#### Handlers

The main handler classes are:

- RequestHandlerForJson <-- base class for all handlers that expect to return json

  - SignedFetchHandler <-- base class for all handlers that need to perform a signed fetch to an API
  
    - all the subscription handlers
    
- RequestHandlerForFreeMarker <-- base class for all handlers that expect their results to be rendered by a FreeMarkerEngine class


## Getting Started

- Clone the application from Github and cd into the folder

- Setup environment variables:
  - **consumer-key** = the oauth consumer key for App Direct Integrations
  - **consumer-secret** = the oauth secret
  - **JDBC_DATABASE_URL** = the jdbc url to either a postgresql or mysql database
    - e.g. jdbc:mysql://localhost:3306/_db_name_?user=_db_user_id_&password=_pwd_
    - note that I didn't mock out the db for unit tests, so they require this to run, which will cause builds to fail if they require passing tests first
    - the application has code to initialize the database. You just need to ensure the user the database exists and the user you provide has rights to create and drop tables
    
- run Maven to build the war file (war file will be under /target)
  - mvn clean
  - mvn -Dmaven.test.skip=true package
 
- To deploy to heroku (for example)
  - ensure you have a heroku account and have installed and logged in to the heroku toolbelt
    - great tutorial here if you need it [https://devcenter.heroku.com/articles/getting-started-with-java#introduction]
  - create the app
    - heroku create _name_ 
  - configure the same environment variables as per above
    - heroku config:set NAME="VALUE"
  - deploy the app
    - git push heroku master
  - open the app
    - heroku open

- To test with App Direct
  - create a test product and configure editions
  - configure end points
    - _baseurl_to_your_app_/subscription/create?eventUrl={eventUrl}
    - _baseurl_to_your_app_/subscription/change?eventUrl={eventUrl}
    - _baseurl_to_your_app_/subscription/cancel?eventUrl={eventUrl}
    - _baseurl_to_your_app_/subscription/status?eventUrl={eventUrl}
  - configure authentication
    - _baseurl_to_your_app_/login?openid_identifier={openid}
	- _baseurl_to_your_app_/login*






