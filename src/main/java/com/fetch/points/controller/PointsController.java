package com.fetch.points.controller;

import java.util.List;
import java.util.Map;

import com.fetch.points.model.PointTransaction;
import com.fetch.points.model.SpendPointsRequest;
import com.fetch.points.model.SpentPoint;
import com.fetch.points.repository.PointTransactionRepository;
import com.fetch.points.service.PointsBalanceService;
import com.fetch.points.service.SpendPointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PointsController {

    @Autowired
    private PointTransactionRepository pointTransactionRepository;

    @Autowired
    private PointsBalanceService pointTransactionTotalService;

    @Autowired
    private SpendPointsService spendPointsService;

    @PostMapping("/points-transactions")
    public void addTransaction(@RequestBody PointTransaction pointTransaction) {
        pointTransactionRepository.save( pointTransaction );
    }

    @GetMapping("/points-balance")
    public Map<String, Integer> getPointBalance() {
        return pointTransactionTotalService.calculateBalance();
    }

    @PostMapping("/points")
    public List<SpentPoint> spendPoints(@RequestBody SpendPointsRequest spendPointsRequest) {
        return spendPointsService.spendPoints( spendPointsRequest.getPoints() );
    }
}
