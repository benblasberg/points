package com.fetch.points.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.fetch.points.model.PointTransaction;
import com.fetch.points.model.SpentPoint;
import com.fetch.points.repository.PointTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpendPointsService {

    @Autowired
    private PointTransactionRepository pointTransactionRepository;

    @Transactional
    public List<SpentPoint> spendPoints(int userId, int pointsToSpend) {

        final List<PointTransaction> transactions = pointTransactionRepository.getUnprocessedTransactionsOrderedOldestFirst(userId);

        final Map<String, Integer> pointsSpentByPayer = new HashMap<>();

        final Iterator<PointTransaction> pointTransactionIterator = transactions.iterator();
        while ( pointsToSpend > 0 && pointTransactionIterator.hasNext() ) {

            final PointTransaction pointTransaction = pointTransactionIterator.next();
            if ( pointsToSpend - pointTransaction.getPoints() >= 0 ) { //do we have leftover points after this transaction?
                pointsToSpend -= pointTransaction.getPoints();
                addPointsToPayer( pointTransaction.getPayer(), pointTransaction.getPoints(), pointsSpentByPayer );
                pointTransaction.setProcessed( true );
                pointTransactionRepository.save( pointTransaction );
            } else { //there are leftovers
                pointTransaction.setPoints( pointTransaction.getPoints() - pointsToSpend );
                addPointsToPayer( pointTransaction.getPayer(), pointsToSpend, pointsSpentByPayer );
                pointsToSpend = 0;
                pointTransactionRepository.save( pointTransaction );
            }
        }

        if (pointsToSpend > 0) {
            throw new IllegalArgumentException("There are not enough points in this account");
        }

        return pointsSpentByPayer.entrySet()
            .stream()
            .map( entry -> new SpentPoint( entry.getKey(), entry.getValue() ) )
            .collect(
                Collectors.toList() );
    }

    private void addPointsToPayer(final String payer, final int spentPoints, final Map<String, Integer> points) {
        points.compute( payer,
            (key, value) ->
                (value == null? 0 : value)
                    + Math.negateExact( spentPoints ) );
    }
}
