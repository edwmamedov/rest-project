package com.trizic.api.rest;

import com.trizic.api.service.AdvisorService;
import com.trizic.model.generated.*;
import org.dozer.DozerBeanMapper;
import org.dozer.loader.api.BeanMappingBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@RestController
@RequestMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE, path = "/v1/advisor")
public class AdvisorController {

    private DozerBeanMapper mapper;
    {
        mapper = new DozerBeanMapper();
        mapper.addMapping( new BeanMappingBuilder() {
            protected void configure() {
                mapping(ModelReq.class, ModelRes.class);
                mapping(ModelReq.ModelType.class, ModelRes.ModelType.class);
                mapping(ModelReq.RebalanceFrequency.class, ModelRes.RebalanceFrequency.class);
                mapping(AssetAllocation.class, AssetAllocation_.class);
            }
        });
    }

    private AdvisorService advisorService;

    public AdvisorController(AdvisorService advisorService) {
        this.advisorService = advisorService;
    }

    @RequestMapping(path = "/{advisorId}/model", method = RequestMethod.GET)
    public ResponseEntity<ModelsRes> findAllPortfolios(
            @PathVariable("advisorId") Integer advisorId,
                @RequestParam(name = "pageSize", defaultValue = "15") Integer pageSize,
                    @RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber) {

        Pageable pageRequest = new PageRequest(pageNumber, pageSize);

        if (!advisorService.isAdvisorExists(advisorId)) {
            throw new NotFoundException("advisor.not.found");
        }

        Page<ModelReq> page = advisorService.findAllPortfoliosById(advisorId, pageRequest);

        ModelsRes modelRes = new ModelsRes();
        modelRes.setNumberOfPages(page.getTotalPages());
        modelRes.setPage(page.getContent().stream().map(v -> mapper.map(v, ModelRes.class)).collect(toList()));
        modelRes.setPageNumber(page.getNumber());
        modelRes.setPageSize(page.getSize());
        modelRes.setTotalNumberOfElements((int) page.getTotalElements());

        return new ResponseEntity<>(modelRes, HttpStatus.OK);
    }


    @RequestMapping(path = "/{advisorId}/model", method = RequestMethod.PUT)
    public ResponseEntity<ModelRes> addPortfolio(
            @PathVariable("advisorId") Integer advisorId,
                @Valid @RequestBody ModelReq modelReq) {

        advisorService.createOrUpdatePortfolio(advisorId, modelReq);

        ModelRes modelRes = mapper.map(modelReq, ModelRes.class);
        modelRes.setAdvisorId(advisorId);

        return new ResponseEntity<>(modelRes, HttpStatus.OK);
    }

    @InitBinder
    public void setupBinder(WebDataBinder binder) {
        binder.addValidators(new AllocationValidator());
    }


}
