package org.linkeddata.limes;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "acceptthresh", "acceptancefilepath", "acceptrelation" })
public class Acceptance {

    private String acceptthresh;
    private String acceptrelation;
    private String acceptancefilepath;

    @XmlElement(name = "FILE")
    public String getAcceptancefilepath() {
	return acceptancefilepath;
    }

    public void setAcceptancefilepath(String acceptancefilepath) {
	this.acceptancefilepath = acceptancefilepath;
    }

    @XmlElement(name = "RELATION")
    public String getAcceptrelation() {
	return acceptrelation;
    }

    public void setAcceptrelation(String acceptrelation) {
	this.acceptrelation = acceptrelation;
    }

    @XmlElement(name = "THRESHOLD")
    public String getAcceptthresh() {
	return acceptthresh;
    }

    public void setAcceptthresh(String acceptthresh) {
	this.acceptthresh = acceptthresh;
    }
}
