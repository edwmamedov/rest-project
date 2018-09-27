package com.trizic.api.rest;

import com.trizic.api.service.AdvisorService;
import com.trizic.model.generated.*;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdvisorControllerTest {

    @InjectMocks
    private AdvisorController controller;

    @Mock
    private AdvisorService advisorService;

    @Test
    public void testFindAllPortfolios() {

        List<ModelReq> portfolios = new ArrayList<>(4);

        portfolios.add(new ModelReq());
        portfolios.add(new ModelReq());
        portfolios.add(new ModelReq());
        portfolios.add(new ModelReq());

        Page<ModelReq> page = new PageImpl<>(portfolios, new PageRequest(1,4), 8);

        when(advisorService.findAllPortfoliosById(any(), any())).thenReturn(page);
        when(advisorService.isAdvisorExists(any())).thenReturn(true);


        ResponseEntity<ModelsRes> response = controller.findAllPortfolios(1, 4, 1);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getBody().getPage(), Matchers.hasSize(4));
        assertThat(response.getBody().getNumberOfPages(), equalTo(2));
        assertThat(response.getBody().getTotalNumberOfElements(), equalTo(8));
        assertThat(response.getBody().getPageNumber(), equalTo(1));
    }

    @Test
    public void testAdd() {

        ModelReq portfolio = new ModelReq();
        portfolio.setName("test");
        portfolio.setDescription("test");
        portfolio.setCashHoldingPercentage(10);
        portfolio.setDriftPercentage(5);
        portfolio.setModelType(ModelReq.ModelType.TAXABLE);
        portfolio.setRebalanceFrequency(ModelReq.RebalanceFrequency.QUARTERLY);

        List<AssetAllocation> list = new ArrayList<>(1);
        AssetAllocation assetAllocation = new AssetAllocation();
        assetAllocation.setPercentage(100.0);
        assetAllocation.setSymbol("AAPL");
        list.add(assetAllocation);
        portfolio.setAssetAllocations(list);

        ResponseEntity<ModelRes> response = controller.addPortfolio(1, portfolio);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getBody().getAdvisorId(), equalTo(1));

        assertThat(response.getBody().getName(), equalTo("test"));
        assertThat(response.getBody().getDescription(), equalTo("test"));
        assertThat(response.getBody().getCashHoldingPercentage(), equalTo(10));
        assertThat(response.getBody().getDriftPercentage(), equalTo(5));
        assertThat(response.getBody().getModelType().name(), equalTo("TAXABLE"));
        assertThat(response.getBody().getRebalanceFrequency().name(), equalTo("QUARTERLY"));
        assertThat(response.getBody().getAssetAllocations(), Matchers.hasSize(1));

        AssetAllocation_ assetAllocation_ = response.getBody().getAssetAllocations().iterator().next();
        assertThat(assetAllocation_.getSymbol(), equalTo("AAPL"));
        assertThat(assetAllocation_.getPercentage(), equalTo(100.0));
    }


}