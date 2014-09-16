package org.linkeddata.stack.limes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LimesConfig {
    private String uuid;
    private String sourceserviceuri;
    private String targetserviceuri;
    private String sourcegraph;
    private String targetgraph;
    private String sourcevar;
    private String targetvar;
    private String sourcesize;
    private String targetsize;
    private String sourcerestr;
    private String targetrestr;
    private String metric;
    private String granularity;
    private String outputformat;
    private String exectype;
    private String acceptthresh;
    private String reviewthresh;
    private String acceptrelation;
    private String reviewrelation;
    private String acceptancefilepath;
    private String reviewfilepath;
    private String configfilepath;

    private List<String> targetprop;
    private List<String> sourceprop;
    private Map<String, String> prefixes;

    public LimesConfig() {
	targetprop = new ArrayList<String>();
	sourceprop = new ArrayList<String>();
	prefixes = new HashMap<String, String>();
    }

    @Override
    public String toString() {
	Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues()
		.serializeNulls().create();
	return gson.toJson(this);
    }

    public String getSourceserviceuri() {
	return sourceserviceuri;
    }

    public void setSourceserviceuri(String sourceserviceuri) {
	this.sourceserviceuri = sourceserviceuri;
    }

    public String getTargetserviceuri() {
	return targetserviceuri;
    }

    public void setTargetserviceuri(String targetserviceuri) {
	this.targetserviceuri = targetserviceuri;
    }

    public String getSourcegraph() {
	return sourcegraph;
    }

    public void setSourcegraph(String sourcegraph) {
	this.sourcegraph = sourcegraph;
    }

    public String getTargetgraph() {
	return targetgraph;
    }

    public void setTargetgraph(String targetgraph) {
	this.targetgraph = targetgraph;
    }

    public String getSourcevar() {
	return sourcevar;
    }

    public void setSourcevar(String sourcevar) {
	this.sourcevar = sourcevar;
    }

    public String getTargetvar() {
	return targetvar;
    }

    public void setTargetvar(String targetvar) {
	this.targetvar = targetvar;
    }

    public String getSourcesize() {
	return sourcesize;
    }

    public void setSourcesize(String sourcesize) {
	this.sourcesize = sourcesize;
    }

    public String getTargetsize() {
	return targetsize;
    }

    public void setTargetsize(String targetsize) {
	this.targetsize = targetsize;
    }

    public String getSourcerestr() {
	return sourcerestr;
    }

    public void setSourcerestr(String sourcerestr) {
	this.sourcerestr = sourcerestr;
    }

    public String getTargetrestr() {
	return targetrestr;
    }

    public void setTargetrestr(String targetrestr) {
	this.targetrestr = targetrestr;
    }

    public String getMetric() {
	return metric;
    }

    public void setMetric(String metric) {
	this.metric = metric;
    }

    public String getGranularity() {
	return granularity;
    }

    public void setGranularity(String granularity) {
	this.granularity = granularity;
    }

    public String getOutputformat() {
	return outputformat;
    }

    public void setOutputformat(String outputformat) {
	this.outputformat = outputformat;
    }

    public String getExectype() {
	return exectype;
    }

    public void setExectype(String exectype) {
	this.exectype = exectype;
    }

    public String getAcceptthresh() {
	return acceptthresh;
    }

    public void setAcceptthresh(String acceptthresh) {
	this.acceptthresh = acceptthresh;
    }

    public String getReviewthresh() {
	return reviewthresh;
    }

    public void setReviewthresh(String reviewthresh) {
	this.reviewthresh = reviewthresh;
    }

    public String getAcceptrelation() {
	return acceptrelation;
    }

    public void setAcceptrelation(String acceptrelation) {
	this.acceptrelation = acceptrelation;
    }

    public String getReviewrelation() {
	return reviewrelation;
    }

    public void setReviewrelation(String reviewrelation) {
	this.reviewrelation = reviewrelation;
    }

    public String getAcceptancefilepath() {
	return acceptancefilepath;
    }

    public void setAcceptancefilepath(String acceptancefilepath) {
	this.acceptancefilepath = acceptancefilepath;
    }

    public String getReviewfilepath() {
	return reviewfilepath;
    }

    public void setReviewfilepath(String reviewfilepath) {
	this.reviewfilepath = reviewfilepath;
    }

    public List<String> getTargetprop() {
	return targetprop;
    }

    public void setTargetprop(List<String> targetprop) {
	this.targetprop = targetprop;
    }

    public List<String> getSourceprop() {
	return sourceprop;
    }

    public void setSourceprop(List<String> sourceprop) {
	this.sourceprop = sourceprop;
    }

    public Map<String, String> getPrefixes() {
	return prefixes;
    }

    public void setPrefixes(Map<String, String> prefixes) {
	this.prefixes = prefixes;
    }

    public String getConfigfilepath() {
	return configfilepath;
    }

    public void setConfigfilepath(String configfilepath) {
	this.configfilepath = configfilepath;
    }

    public String getUuid() {
	return uuid;
    }

    public void setUuid(String uuid) {
	this.uuid = uuid;
    }
}
