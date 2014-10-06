package org.linkeddata.limes;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlType(propOrder = { "namespace", "label" })
public class Prefix {

    private String label;
    private String namespace;

    @XmlElement(name = "NAMESPACE")
    @JsonProperty("namespace")
    public String getNamespace() {
	return namespace;
    }

    public void setNamespace(String namespace) {
	this.namespace = namespace;
    }

    @XmlElement(name = "LABEL")
    @JsonProperty("label")
    public String getLabel() {
	return label;
    }

    public void setLabel(String label) {
	this.label = label;
    }

}
