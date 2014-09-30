package org.linkeddata.limes;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "id", "endpoint", "graph", "var", "pagesize",
	"restriction", "property", "type" })
public class Dataset {

    private String id;
    private String endpoint;
    private List<String> graph;
    private String var;
    private String pagesize;
    private String restriction;
    private List<String> property;
    private String type;

    @XmlElement(name = "ID")
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    @XmlElement(name = "ENDPOINT")
    public String getEndpoint() {
	return endpoint;
    }

    public void setEndpoint(String endpoint) {
	this.endpoint = endpoint;
    }

    @XmlElement(name = "GRAPH")
    public List<String> getGraph() {
	return graph;
    }

    public void setGraph(List<String> graph) {
	this.graph = graph;
    }

    @XmlElement(name = "VAR")
    public String getVar() {
	return var;
    }

    public void setVar(String var) {
	this.var = var;
    }

    @XmlElement(name = "PAGESIZE")
    public String getPagesize() {
	return pagesize;
    }

    public void setPagesize(String pagesize) {
	this.pagesize = pagesize;
    }

    @XmlElement(name = "RESTRICTION")
    public String getRestriction() {
	return restriction;
    }

    public void setRestriction(String restriction) {
	this.restriction = restriction;
    }

    @XmlElement(name = "PROPERTY")
    public List<String> getProperty() {
	return property;
    }

    public void setProperty(List<String> property) {
	this.property = property;
    }

    @XmlElement(name = "TYPE")
    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

}
