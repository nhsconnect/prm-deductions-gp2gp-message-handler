package uk.nhs.prm.deductions.gp2gpmessagehandler.gp2gpMessageModels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SOAPEnvelope {
    @JacksonXmlProperty(localName = "Header", namespace = "soap")
    public SOAPHeader header;

    @JacksonXmlProperty(localName = "Body", namespace = "soap")
    public SOAPBody body;
}