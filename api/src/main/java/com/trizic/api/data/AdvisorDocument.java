package com.trizic.api.data;

import com.trizic.model.generated.ModelReq;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedList;
import java.util.List;

@Document(collection = "advisors")
public class AdvisorDocument {

    @Id
    private Integer id;

    private List<ModelReq> portfolios = new LinkedList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<ModelReq> getPortfolios() {
        return portfolios;
    }

    public void setPortfolios(List<ModelReq> portfolios) {
        this.portfolios = portfolios;
    }
}
