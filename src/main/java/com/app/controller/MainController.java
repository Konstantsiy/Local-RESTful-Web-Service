package com.app.controller;

import com.app.exceptions.BadRequestException;
import com.app.exceptions.InternalServiceException;
import com.app.models.RequestCounter;
import com.app.models.Equation;
import com.app.models.ServiceResponse;
import com.app.services.CacheService;
import com.app.services.EquationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;


import java.util.Map;
import java.util.concurrent.Semaphore;

@RestController
@RequestMapping(value = "/equation")
public class MainController {

    RequestCounter requestCounter = new RequestCounter(0, new Semaphore(1));
    CacheService cacheService = new CacheService();
    EquationService equationService = new EquationService();
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping
    public ServiceResponse processEquation(@RequestParam("firstSlog") Double firstSlog,
                                           @RequestParam("sum") Double resultSum,
                                           @RequestParam("rangeFrom") Double min,
                                           @RequestParam("to") Double max) throws InternalServiceException, BadRequestException {
        Equation equation = new Equation(firstSlog, resultSum, min, max);
        this.equationService.globalVerification(equation);
        logger.info("Verification was successful");
        this.requestCounter.increaseNumberOfRequests();
        return this.cacheService.getResponse(equation);
    }

    @GetMapping(value = "/cache")
    public Map<Equation, ServiceResponse> getCache() throws InternalServiceException {
        this.requestCounter.increaseNumberOfRequests();
        logger.info("Get all requests");
        return this.cacheService.getAll();
    }

    @DeleteMapping(value = "/cache/delete")
    public void deleteEquation(@RequestParam("firstSlog") Double firstSlogan,
                               @RequestParam("sum") Double sum,
                               @RequestParam("rangeFrom") Double min,
                               @RequestParam("max") Double max) throws BadRequestException, InternalServiceException {
        Equation equation = new Equation(firstSlogan, sum, min, max);
        this.equationService.globalVerification(equation);
        this.requestCounter.increaseNumberOfRequests();
        if(this.cacheService.find(equation)) {
            this.cacheService.delete(equation);
            logger.info("Request removed from cache");
        }
        else {
            logger.error("No such request in cache");
        }
    }

    @DeleteMapping(value = "/cache/deleteAll")
    public void deleteAllEquations() throws InternalServiceException {
        this.requestCounter.increaseNumberOfRequests();
        this.cacheService.deleteAll();
        logger.info("The cache is cleared");
    }

    @GetMapping(value = "/requestCounter")
    public int getCounter() {
        return this.requestCounter.getCounter();
    }
}
