# tempest
Team Git Good's Sky Software Engineering Academy group project. "GetYourWay.com" travel web app.

to clone the repo, use the following command:

````
git clone git@github.com:glp02/tempest_server
````

Make sure start branch names and commits with the code of the ticket you're working on, e.g. for ticket TMP-4:

````
git checkout -b TMP-4-login-research
````

or

````
git commit -m "TMP-4 <message>"
````

***

Make sure you've setup your database according to
<a href="https://github.com/glp02/tempest/wiki/Database-Setup-Entry">these</a>
instructions.

***

To run the application, go into `src/main/java/com.sky.tempest_server/TempestServerApplication.java` and run the class.

To shut down, there should be a red stop button at the top of your IDE.