package org.linkeddata.limes;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "reviewthresh", "reviewfilepath", "reviewrelation" })
public class Review {

    private String reviewthresh;
    private String reviewrelation;
    private String reviewfilepath;

    @XmlElement(name = "FILE")
    public String getReviewfilepath() {
	return reviewfilepath;
    }

    public void setReviewfilepath(String reviewfilepath) {
	this.reviewfilepath = reviewfilepath;
    }

    @XmlElement(name = "RELATION")
    public String getReviewrelation() {
	return reviewrelation;
    }

    public void setReviewrelation(String reviewrelation) {
	this.reviewrelation = reviewrelation;
    }

    @XmlElement(name = "THRESHOLD")
    public String getReviewthresh() {
	return reviewthresh;
    }

    public void setReviewthresh(String reviewthresh) {
	this.reviewthresh = reviewthresh;
    }
}
