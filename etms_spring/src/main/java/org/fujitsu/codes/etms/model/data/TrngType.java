package org.fujitsu.codes.etms.model.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "trng_type")
public class TrngType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trng_type_id")
    private Integer trngTypeId;

    @Column(name = "trng_type_nm", length = 25)
    private String trngTypeNm;

    @Column(name = "description", length = 150)
    private String description;

    // Constructors
    public TrngType() {}

    public TrngType(String trngTypeNm, String description) {
        this.trngTypeNm = trngTypeNm;
        this.description = description;
    }

    // Getters & Setters
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