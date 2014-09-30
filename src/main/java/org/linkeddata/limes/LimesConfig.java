package org.linkeddata.limes;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@XmlRootElement(name = "LIMES")
@XmlType(propOrder = { "prefixes", "source", "target", "metric", "acceptance",
	"review", "exectype", "granularity", "outputformat" })
public class LimesConfig {

    private String uuid = "";
    private String metric = "";
    private Dataset source;
    private Dataset target;
    private Acceptance acceptance;
    private Review review;
    private List<Prefix> prefixes;
    private List<String> exectype;
    private List<String> granularity;
    private List<String> outputformat;

    // following fields are for Application logic and should be omitted in the
    // limes xml file configuration
    private String configurationfile = "";
    private String importendpoint = "";
    private String reviewgraph = "";
    private String acceptgraph = "";
    private String uribase = "";

    public LimesConfig() {
	setPrefixes(new ArrayList<Prefix>());
    }

    @Override
    public String toString() {
	Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues()
		.serializeNulls().create();
	return gson.toJson(this);
    }

    @XmlTransient
    public String getConfigurationFile() {
	return configurationfile;
    }

    public void setConfigurationFile(String configurationfile) {
	this.configurationfile = configurationfile;
    }

    @XmlTransient
    public String getConfigurationfile() {
	return configurationfile;
    }

    public void setConfigurationfile(String configurationfile) {
	this.configurationfile = configurationfile;
    }

    @XmlTransient
    public String getImportendpoint() {
	return importendpoint;
    }

    public void setImportendpoint(String importendpoint) {
	this.importendpoint = importendpoint;
    }

    @XmlTransient
    public String getReviewgraph() {
	return reviewgraph;
    }

    public void setReviewgraph(String reviewgraph) {
	this.reviewgraph = reviewgraph;
    }

    @XmlTransient
    public String getAcceptgraph() {
	return acceptgraph;
    }

    public void setAcceptgraph(String acceptgraph) {
	this.acceptgraph = acceptgraph;
    }

    @XmlTransient
    public String getUribase() {
	return uribase;
    }

    public void setUribase(String uribase) {
	this.uribase = uribase;
    }

    @XmlTransient
    public String getUuid() {
	return uuid;
    }

    public void setUuid(String uuid) {
	this.uuid = uuid;
    }

    @XmlElement(name = "METRIC")
    public String getMetric() {
	return metric;
    }

    public void setMetric(String metric) {
	this.metric = metric;
    }

    @XmlElement(name = "GRANULARITY")
    public List<String> getGranularity() {
	return granularity;
    }

    public void setGranularity(List<String> granularity) {
	this.granularity = granularity;
    }

    @XmlElement(name = "OUTPUT")
    public List<String> getOutputformat() {
	return outputformat;
    }

    public void setOutputformat(List<String> outputformat) {
	this.outputformat = outputformat;
    }

    @XmlElement(name = "EXECUTION")
    public List<String> getExectype() {
	return exectype;
    }

    public void setExectype(List<String> exectype) {
	this.exectype = exectype;
    }

    @XmlElement(name = "PREFIX")
    public List<Prefix> getPrefixes() {
	return prefixes;
    }

    public void setPrefixes(List<Prefix> prefixes) {
	this.prefixes = prefixes;
    }

    @XmlElement(name = "SOURCE")
    public Dataset getSource() {
	return source;
    }

    public void setSource(Dataset source) {
	this.source = source;
    }

    @XmlElement(name = "TARGET")
    public Dataset getTarget() {
	return target;
    }

    public void setTarget(Dataset target) {
	this.target = target;
    }

    @XmlElement(name = "ACCEPTANCE")
    public Acceptance getAcceptance() {
	return acceptance;
    }

    public void setAcceptance(Acceptance acceptance) {
	this.acceptance = acceptance;
    }

    @XmlElement(name = "REVIEW")
    public Review getReview() {
	return review;
    }

    public void setReview(Review review) {
	this.review = review;
    }

}
