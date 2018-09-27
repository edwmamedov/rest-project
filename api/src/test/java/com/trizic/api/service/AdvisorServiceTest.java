package com.trizic.api.service;

import com.trizic.api.ApiApplication;
import com.trizic.api.data.AdvisorDocument;
import com.trizic.model.generated.AssetAllocation;
import com.trizic.model.generated.ModelReq;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@DataMongoTest
@ContextConfiguration(classes = AdvisorServiceTest.TestConfig.class)
public class AdvisorServiceTest {

    @Configuration
    @Import(ApiApplication.class)
    static class TestConfig {
    }

    @Autowired
    private AdvisorService advisorService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void testUpdateAdvisor_advisorNotExist() {
        advisorService.createOrUpdatePortfolio(1, create("test", "test"));
        AdvisorDocument advisorDocument = mongoTemplate.findById(1, AdvisorDocument.class);
        assertThat(advisorDocument, notNullValue());
        assertThat(advisorDocument.getId(), equalTo(1));
        assertThat(advisorDocument.getPortfolios(), not(empty()));
    }

    @Test
    public void testUpdateAdvisor_duplictedPortfolioWillBeReplaced() {
        advisorService.createOrUpdatePortfolio(1, create("test", "test"));
        advisorService.createOrUpdatePortfolio(1, create("test", "test2"));

        AdvisorDocument advisorDocument = mongoTemplate.findById(1, AdvisorDocument.class);
        assertThat(advisorDocument, notNullValue());
        assertThat(advisorDocument.getId(), equalTo(1));
        assertThat(advisorDocument.getPortfolios(), hasSize(1));
        assertThat(advisorDocument.getPortfolios().iterator().next().getDescription(), equalTo("test2"));

    }

    @Test
    public void testFindAllPortfoliosById() {
        advisorService.createOrUpdatePortfolio(1, create("test", "test"));
        advisorService.createOrUpdatePortfolio(2, create("test", "test"));
        advisorService.createOrUpdatePortfolio(1, create("test2", "test2"));
        advisorService.createOrUpdatePortfolio(1, create("test3", "test3"));

        Page<ModelReq> page = advisorService.findAllPortfoliosById(1, new PageRequest(0, 1));

        assertThat(page.getContent(), hasSize(1));

        page = advisorService.findAllPortfoliosById(1, new PageRequest(0, 2));

        assertThat(page.getContent(), hasSize(2));

        page = advisorService.findAllPortfoliosById(1, new PageRequest(0, 3));

        assertThat(page.getContent(), hasSize(3));

        page = advisorService.findAllPortfoliosById(1, new PageRequest(1, 2));

        assertThat(page.getContent(), hasSize(1));
    }


    private ModelReq create(String name, String description) {
        ModelReq portfolio = new ModelReq();
        portfolio.setName(name);
        portfolio.setDescription(description);
        portfolio.setCashHoldingPercentage(10);
        portfolio.setDriftPercentage(5);
        portfolio.setModelType(ModelReq.ModelType.TAXABLE);
        portfolio.setRebalanceFrequency(ModelReq.RebalanceFrequency.QUARTERLY);

        List<AssetAllocation> list = new ArrayList<>(4);
        AssetAllocation assetAllocation = new AssetAllocation();
        assetAllocation.setPercentage(30.0);
        assetAllocation.setSymbol("AAPL");
        list.add(assetAllocation);

        assetAllocation = new AssetAllocation();
        assetAllocation.setPercentage(20.0);
        assetAllocation.setSymbol("GOOG");
        list.add(assetAllocation);

        assetAllocation = new AssetAllocation();
        assetAllocation.setPercentage(15.0);
        assetAllocation.setSymbol("IBM");
        list.add(assetAllocation);

        assetAllocation = new AssetAllocation();
        assetAllocation.setPercentage(25.0);
        assetAllocation.setSymbol("FB");
        list.add(assetAllocation);

        return portfolio;
    }
}
