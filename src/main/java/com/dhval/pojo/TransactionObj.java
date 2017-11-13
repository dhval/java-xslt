package com.dhval.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class TransactionObj {

    @XmlElement(name = "TrackingId")
    public String trackingId;

    @XmlElement(name = "RecordId")
    public String recordId;

}
