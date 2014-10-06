package org.linkeddata.limes;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@XmlRootElement(name = "LIMES")
@XmlType(propOrder = { "prefix", "source", "target", "metric", "acceptance",
	"review", "execution", "granularity", "output" })
public class LimesConfig {

    private String uuid = "";
    private String metric = "";
    private Dataset source;
    private Dataset target;
    private Result acceptance;
    private Result review;
    private String execution;
    private String granularity;
    private String output;
    private List<Prefix> prefix;

    // following fields are for Application logic and should be omitted in the
    // limes xml file configuration
    private String configurationfile = "";
    private String saveendpoint = "";
    private String reviewgraph = "";
    private String acceptgraph = "";
    private String uribase = "";

    public LimesConfig() {
	setPrefix(new ArrayList<Prefix>());
    }

    @Override
    public String toString() {
	Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues()
		.serializeNulls().create();
	return gson.toJson(this);
    }

    @XmlTransient
    @JsonProperty("configurationfile")
    public String getConfigurationfile() {
	return configurationfile;
    }

    public void setConfigurationfile(String configurationfile) {
	this.configurationfile = configurationfile;
    }

    @XmlTransient
    @JsonProperty("saveendpoint")
    public String getSaveendpoint() {
	return saveendpoint;
    }

    public void setSaveendpoint(String saveendpoint) {
	this.saveendpoint = saveendpoint;
    }

    @XmlTransient
    @JsonProperty("reviewgraph")
    public String getReviewgraph() {
	return reviewgraph;
    }

    public void setReviewgraph(String reviewgraph) {
	this.reviewgraph = reviewgraph;
    }

    @XmlTransient
    @JsonProperty("acceptgraph")
    public String getAcceptgraph() {
	return acceptgraph;
    }

    public void setAcceptgraph(String acceptgraph) {
	this.acceptgraph = acceptgraph;
    }

    @XmlTransient
    @JsonProperty("uribase")
    public String getUribase() {
	return uribase;
    }

    public void setUribase(String uribase) {
	this.uribase = uribase;
    }

    @XmlTransient
    @JsonProperty("uuid")
    public String getUuid() {
	return uuid;
    }

    public void setUuid(String uuid) {
	this.uuid = uuid;
    }

    @XmlElement(name = "METRIC")
    @JsonProperty("metric")
    public String getMetric() {
	return metric;
    }

    public void setMetric(String metric) {
	this.metric = metric;
    }

    @XmlElement(name = "GRANULARITY")
    @JsonProperty("granularity")
    public String getGranularity() {
	return granularity;
    }

    public void setGranularity(String granularity) {
	this.granularity = granularity;
    }

    @XmlElement(name = "OUTPUT")
    @JsonProperty("output")
    public String getOutput() {
	return output;
    }

    public void setOutput(String output) {
	this.output = output;
    }

    @XmlElement(name = "EXECUTION")
    @JsonProperty("execution")
    public String getExecution() {
	return execution;
    }

    public void setExecution(String execution) {
	this.execution = execution;
    }

    @XmlElement(name = "PREFIX")
    @JsonProperty("prefix")
    public List<Prefix> getPrefix() {
	return prefix;
    }

    public void setPrefix(List<Prefix> prefix) {
	this.prefix = prefix;
    }

    @XmlElement(name = "SOURCE")
    @JsonProperty("source")
    public Dataset getSource() {
	return source;
    }

    public void setSource(Dataset source) {
	this.source = source;
    }

    @XmlElement(name = "TARGET")
    @JsonProperty("target")
    public Dataset getTarget() {
	return target;
    }

    public void setTarget(Dataset target) {
	this.target = target;
    }

    @XmlElement(name = "ACCEPTANCE")
    @JsonProperty("acceptance")
    public Result getAcceptance() {
	return acceptance;
    }

    public void setAcceptance(Result acceptance) {
	this.acceptance = acceptance;
    }

    @XmlElement(name = "REVIEW")
    @JsonProperty("review")
    public Result getReview() {
	return review;
    }

    public void setReview(Result review) {
	this.review = review;
    }

}
