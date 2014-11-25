#LIMES-Service

A web service for [LIMES](http://aksw.org/Projects/LIMES.html): LInk discovery framework for MEtric Spaces.

This Project implemented LIMES and provides a REST API for discovering LINKS in the Web of Data. 

Documentation of LIMES can be found in its [repository](https://github.com/AKSW/LIMES).

This Web service is used in the [GeoKnow Generator Workbench](https://github.com/GeoKnow/GeoKnowGeneratorUI).


##Building

### From the LDStack Debian repository

### From source code

You can create the war file using `mvn package`. And you can deploy the `.war` file in a servlet container.
	
To avoid running tests use `-DskipTests` parameter.

	

##REST Services

Path     | Method |  Accept | Produces| Description
-------  | ------ | --------| ------- | -----------
\        | POST   | application/xml application/json | application/json | executes a LIMES with configuration passed in the payload
\\{uuid} | GET    |  |  | Reads the content of a TAB file generated in the LIMES process


### XML Congiguration

The services accepts LIMES XML files as defined in their configuration. It is validated with a [DTD document](https://github.com/GeoKnow/LIMES-Service/blob/master/limes.dtd).

### JSON Configuration
An example of JSON configuration file is presented here after. Most of the parametres are for LIMES to process, other parameters like saveendpoint, reviewgraph, acceptgraph are for the Service to save results into a accessible endpoint/graphs.
 
 	{
        "execution": "Simple",
        "output": "N3",
        "metric": "hausdorff(x.polygon, y.polygon)",
        "saveendpoint": "http://endpoint/to/save/results",
        "reviewgraph": "http://graph/to/save/review/results",
        "acceptgraph": "http://graph/to/save/accepted/results",
        "uribase": "http://generator.geoknow.eu/resource/",
        "prefix": [
            {
                "label": "geom",
                "namespace": "http://geovocab.org/geometry#"
            },
            {
                "label": "geos",
                "namespace": "http://www.opengis.net/ont/geosparql#"
            },
            {
                "label": "lgdo",
                "namespace": "http://linkedgeodata.org/ontology/"
            }
        ],
        "source": {
            "id": "linkedgeodata",
            "endpoint": "http://linkedgeodata.org/sparql",
            "var": "?x",
            "pagesize": "2000",
            "restriction": "?x a lgdo:RelayBox",
            "property": [
                "geom:geometry/geos:asWKT RENAME polygon"
            ]
        },
        "target": {
            "id": "linkedgeodata",
            "endpoint": "http://linkedgeodata.org/sparql",
            "var": "?y",
            "pagesize": "2000",
            "restriction": "?y a lgdo:RelayBox",
            "property": [
                "geom:geometry/geos:asWKT RENAME polygon"
            ]
        },
        "acceptance": {
            "threshold": "0.9",
            "relation": "lgdo:near",
            "file": "lgd_relaybox_verynear.nt"
        },
        "review": {
            "threshold": "0.5",
            "relation": "lgdo:near",
            "file": "lgd_relaybox_near.nt"
        }
    }



##Licence

The source code of this repo is published under the Apache License Version 2.0
