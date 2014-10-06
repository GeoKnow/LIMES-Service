package org.linkeddata.limes;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlType(propOrder = { "threshold", "file", "relation" })
public class Result {

    private String threshold;
    private String relation;
    private String file;

    @XmlElement(name = "FILE")
    @JsonProperty("file")
    public String getFile() {
	return file;
    }

    public void setFile(String file) {
	this.file = file;
    }

    @XmlElement(name = "RELATION")
    @JsonProperty("relation")
    public String getRelation() {
	return relation;
    }

    public void setRelation(String relation) {
	this.relation = relation;
    }

    @XmlElement(name = "THRESHOLD")
    @JsonProperty("threshold")
    public String getThreshold() {
	return threshold;
    }

    public void setThreshold(String threshold) {
	this.threshold = threshold;
    }
}
