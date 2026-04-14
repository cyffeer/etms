package org.fujitsu.codes.etms.model.dto;

public class TrngTypeResponse {

    private Integer trngTypeId;
    private String trngTypeNm;
    private String description;

    public Integer getTrngTypeId() {
        return trngTypeId;
    }

    public void setTrngTypeId(Integer trngTypeId) {
        this.trngTypeId = trngTypeId;
    }

    public String getTrngTypeNm() {
        return trngTypeNm;
    }

    public void setTrngTypeNm(String trngTypeNm) {
        this.trngTypeNm = trngTypeNm;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
