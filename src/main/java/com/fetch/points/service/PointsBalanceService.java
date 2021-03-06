package com.fetch.points.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fetch.points.model.PointTransaction;
import com.fetch.points.repository.PointTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointsBalanceService {

    @Autowired
    private PointTransactionRepository pointTransactionRepository;

    public Map<String, Integer> calculateBalance(int userId) {
        final List<PointTransaction> transactions =
            pointTransactionRepository.getUnprocessedTransactionsOrderedOldestFirst(userId);

        final Map<String, Integer> totalsByPayer = new HashMap<>();
        for (PointTransaction pointTransaction : transactions) {
            totalsByPayer.compute(
                pointTransaction.getPayer(),
                (key, value) ->
                    (value == null? 0 : value)
                        + pointTransaction.getPoints());
        }

        addZeroBalancePayers( userId, totalsByPayer );
        return totalsByPayer;
    }

    private void addZeroBalancePayers(int userId, final Map<String, Integer> totalsByPayer) {
        pointTransactionRepository.getDistinctPayerNames(userId)
            .stream()
            .filter( payer -> !totalsByPayer.containsKey( payer ) )
            .forEach( payer -> totalsByPayer.put( payer, 0 ) );
    }
}
