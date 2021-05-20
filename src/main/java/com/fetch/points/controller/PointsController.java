package com.fetch.points.controller;

import java.util.List;
import java.util.Map;

import com.fetch.points.model.PointTransaction;
import com.fetch.points.model.SpendPointsRequest;
import com.fetch.points.model.SpentPoint;
import com.fetch.points.model.User;
import com.fetch.points.repository.PointTransactionRepository;
import com.fetch.points.service.PointsBalanceService;
import com.fetch.points.service.SpendPointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class PointsController {

    @Autowired
    private PointTransactionRepository pointTransactionRepository;

    @Autowired
    private PointsBalanceService pointTransactionTotalService;

    @Autowired
    private SpendPointsService spendPointsService;

    @PostMapping("/users/{userId}/points-transactions")
    public void addTransaction(@PathVariable("userId") int userId, @RequestBody PointTransaction pointTransaction) {
        pointTransaction.setUserId( userId );
        pointTransactionRepository.save( pointTransaction );
    }

    @GetMapping("/users/{userId}/points-balance")
    public Map<String, Integer> getPointBalance(@PathVariable("userId") int userId) {
        return pointTransactionTotalService.calculateBalance(userId);
    }

    @PostMapping("/users/{userId}/points")
    public List<SpentPoint> spendPoints(@PathVariable("userId") int userId, @RequestBody SpendPointsRequest spendPointsRequest) {
        try {
            return spendPointsService.spendPoints( userId, spendPointsRequest.getPoints() );
        } catch ( IllegalArgumentException ex ) {
            throw new ResponseStatusException( HttpStatus.BAD_REQUEST, String.format( "User %d does not %d points", userId, spendPointsRequest.getPoints() ) );
        }
    }
}
