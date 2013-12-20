#LIMES-Service

A web service to configure and launch LIMES

##Building

Tested in Tomcat7

To deploy the service to your Tomcat server run:

	mvn tomcat:deploy
	
or to redeploy:

	mvn tomcat:redeploy
	
##Directories
	
The Limes-Service webapp folder should have the following subdirectories:

	/config
	/result
	/examples

##HTTP Calls

###Running Limes

####Load a LIMES XML configuration file

POST a params array to /Loadfile

	params: {
	          file : filename
	          }

This class will read the files from the GeoKnow Generator (https://github.com/GeoKnow/GeoKnowGeneratorUI) upload 
folder, which should be in the same Tomcat server:

	/webapps/generator/uploads
	
If a different behaviour is desired this class must be edited.

The class will respond with the parameters read from the file.
				 	
####Start the linking process

POST a params array to /LimesRun

	params = { 
					 SourceServiceURI: 
					 TargetServiceURI: 
					 SourceVar: 
					 TargetVar: 
					 SourceSize: 
					 TargetSize: 
					 SourceRestr: 
					 TargetRestr: 
					 SourceProp: 
					 TargetProp: 
					 Metric: 
					 OutputFormat: 
					 ExecType: 
					 AcceptThresh: 
					 ReviewThresh: 
					 AcceptRelation: 
					 ReviewRelation: 
					 };
	
The class will output the results to the webapp/result folder
	
####Open the output from the enrichment process

POST a params array to /LimesReview

No parameters necessary, the class will automatically open the files create by LimesRun and return the models.

	
####Save the output to a SPARQL endpoint
	
POST a params array to /ImportRDF

	parameters = { 
        endpoint: 
   		  uriBase :
		};
		
Will save the results to the endpoint in two graphs (review and accepted results) using the URI base and unique IDs.

##Licence

The source code of this repo is published under the Apache License Version 2.0
