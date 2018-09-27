package com.trizic.api.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.trizic.model.generated.AssetAllocation;
import com.trizic.model.generated.ModelReq;
import com.trizic.model.generated.ModelRes;
import com.trizic.model.generated.ModelsRes;
import config.service.ServicesConfig;
import config.web.WebConfig;
import cz.jirutka.spring.embedmongo.EmbeddedMongoFactoryBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Profile("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AdvisorControllerIT.ITConfig.class,
        webEnvironment = RANDOM_PORT,
        properties="spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration"
)
public class AdvisorControllerIT {

    @Configuration
    @Import({WebConfig.class, ServicesConfig.class})
    @EnableMongoRepositories
    static class ITConfig {

        @Bean
        public MongoTemplate mongoTemplate() throws IOException {
            EmbeddedMongoFactoryBean mongo = new EmbeddedMongoFactoryBean();
            mongo.setBindIp("127.0.0.1");
            MongoClient mongoClient = mongo.getObject();
            return new MongoTemplate(mongoClient, "test");
        }
    }

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate = new TestRestTemplate();
    private HttpHeaders headers = new HttpHeaders();
    {
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    public void smokeTest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(create("bla", "bla")), headers);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/v1/advisor/1/model"), HttpMethod.PUT, entity, String.class);

        mapper.readValue(response.getBody(), ModelRes.class);

        JSONAssert.assertEquals("{\"name\":\"bla\",\"description\":\"bla\"," +
                "\"cashHoldingPercentage\":10,\"driftPercentage\":5,\"modelType\":\"TAXABLE\",\"rebalanceFrequency\":\"QUARTERLY\"," +
                "\"advisorId\":1,\"assetAllocations\":[{\"symbol\":\"AAPL\",\"percentage\":30.0},{\"symbol\":\"GOOG\"," +
                "\"percentage\":20.0},{\"symbol\":\"IBM\",\"percentage\":15.0},{\"symbol\":\"FB\",\"percentage\":35.0}]}",
                response.getBody(), false);


        response = restTemplate.exchange(createURLWithPort("/v1/advisor/1/model?pageSize=5&pageNumber=0"), HttpMethod.GET, entity, String.class);
        mapper.readValue(response.getBody(), ModelsRes.class);

        JSONAssert.assertEquals("{\"pageNumber\":0,\"pageSize\":5,\"numberOfPages\":1,\"totalNumberOfElements\":1," +
                        "\"page\":[{\"name\":\"bla\",\"description\":\"bla\",\"cashHoldingPercentage\":10,\"driftPercentage\":5," +
                        "\"modelType\":\"TAXABLE\",\"rebalanceFrequency\":\"QUARTERLY\",\"assetAllocations\":[{\"symbol\":\"AAPL\"," +
                        "\"percentage\":30.0},{\"symbol\":\"GOOG\",\"percentage\":20.0},{\"symbol\":\"IBM\",\"percentage\":15.0}," +
                        "{\"symbol\":\"FB\",\"percentage\":35.0}]}]}",
                response.getBody(), false);


    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    private ModelReq create(String name, String description) {
        ModelReq portfolio = new ModelReq();
        portfolio.setName(name);
        portfolio.setDescription(description);
        portfolio.setCashHoldingPercentage(10);
        portfolio.setDriftPercentage(5);
        portfolio.setModelType(ModelReq.ModelType.TAXABLE);
        portfolio.setRebalanceFrequency(ModelReq.RebalanceFrequency.QUARTERLY);

        List<AssetAllocation> portfolios = new ArrayList<>(4);
        portfolio.setAssetAllocations(portfolios);
        AssetAllocation assetAllocation = new AssetAllocation();
        assetAllocation.setPercentage(30.0);
        assetAllocation.setSymbol("AAPL");
        portfolios.add(assetAllocation);

        assetAllocation = new AssetAllocation();
        assetAllocation.setPercentage(20.0);
        assetAllocation.setSymbol("GOOG");
        portfolios.add(assetAllocation);

        assetAllocation = new AssetAllocation();
        assetAllocation.setPercentage(15.0);
        assetAllocation.setSymbol("IBM");
        portfolios.add(assetAllocation);

        assetAllocation = new AssetAllocation();
        assetAllocation.setPercentage(35.0);
        assetAllocation.setSymbol("FB");
        portfolios.add(assetAllocation);

        return portfolio;
    }
}
