package com.trizic.api.service;

import com.trizic.api.data.AdvisorDocument;
import com.trizic.model.generated.ModelReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdvisorService {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public AdvisorService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public AdvisorDocument createOrUpdatePortfolio(Integer advisorId, ModelReq portfolio) {
        AdvisorDocument advisor = mongoTemplate.findById(advisorId, AdvisorDocument.class);
        if (advisor == null) {
            advisor = new AdvisorDocument();
            advisor.setId(advisorId);
            mongoTemplate.insert(advisor);
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(advisorId));
        query.fields().elemMatch(
                "portfolios",
                Criteria.where("name").is(portfolio.getName())
        );

        boolean isPortfolioExist  = mongoTemplate.exists(query, AdvisorDocument.class);
        if (isPortfolioExist) {
            advisor.getPortfolios().removeIf(value -> value.getName().equals(portfolio.getName()));
        }

        advisor.getPortfolios().add(portfolio);
        mongoTemplate.save(advisor);

        return advisor;
    }

    public Page<ModelReq> findAllPortfoliosById(Integer advisorId, Pageable pageRequest) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(advisorId));
        List<ModelReq> portfolios = mongoTemplate.findOne(query, AdvisorDocument.class).getPortfolios();
        return applyPageRequest(portfolios, pageRequest);
    }

    public boolean isAdvisorExists(Integer advisorId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(advisorId));
        return mongoTemplate.exists(query, AdvisorDocument.class);
    }

    private Page<ModelReq> applyPageRequest(List<ModelReq> portfolios, Pageable pageRequest) {
        int start = pageRequest.getOffset();
        int end = (start + pageRequest.getPageSize()) > portfolios.size() ? portfolios.size() : (start + pageRequest.getPageSize());
        return new PageImpl<>(portfolios.subList(start, end), pageRequest, portfolios.size());
    }

}
