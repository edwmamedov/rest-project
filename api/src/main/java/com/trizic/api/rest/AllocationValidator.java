package com.trizic.api.rest;

import com.trizic.model.generated.AssetAllocation;
import com.trizic.model.generated.ModelReq;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class AllocationValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return ModelReq.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ModelReq modelReq = (ModelReq) target;

        double totalAllocation = modelReq.getAssetAllocations().stream().mapToDouble(AssetAllocation::getPercentage).sum();
        if (Double.compare(totalAllocation, 100.0) != 0) {
            errors.reject("allocation.percentage.total.invalid");
        }

    }
}
