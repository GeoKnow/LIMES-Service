package org.linkeddata.limes;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "namespace", "label" })
public class Prefix {

    private String label;
    private String namespace;

    @XmlElement(name = "NAMESPACE")
    public String getNamespace() {
	return namespace;
    }

    public void setNamespace(String namespace) {
	this.namespace = namespace;
    }

    @XmlElement(name = "LABEL")
    public String getLabel() {
	return label;
    }

    public void setLabel(String label) {
	this.label = label;
    }

}
