package com.fetch.points.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fetch.points.model.PointTransaction;
import com.fetch.points.repository.PointTransactionRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class PointsBalanceServiceTest {

    @InjectMocks
    private PointsBalanceService pointsBalanceService = new PointsBalanceService();

    @Mock
    private PointTransactionRepository pointTransactionRepository;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks( this );
    }

    private PointTransaction createTransaction(final String payer, final int points) {
        PointTransaction pointTransaction = new PointTransaction();
        pointTransaction.setPoints( points );
        pointTransaction.setPayer( payer );
        return pointTransaction;
    }

    @Test
    public void calculateBalance_all_positive() {
        when(pointTransactionRepository.getUnprocessedTransactionsOrderedOldestFirst())
            .thenReturn( Arrays.asList(
                createTransaction( "A", 200 ),
                createTransaction( "B", 400 ),
                createTransaction( "A", 500 ),
                createTransaction( "C", 100 )
            ) );

        final Map<String, Integer> points = pointsBalanceService.calculateBalance();
        assertNotNull( points );
        assertEquals( 700, points.get( "A" ) );
        assertEquals( 400, points.get( "B" ) );
        assertEquals( 100, points.get( "C" ) );
    }

    @Test
    public void calculateBalance_some_negative() {
        when(pointTransactionRepository.getUnprocessedTransactionsOrderedOldestFirst())
            .thenReturn( Arrays.asList(
                createTransaction( "A", 200 ),
                createTransaction( "B", 400 ),
                createTransaction( "A", 500 ),
                createTransaction( "C", 100 ),
                createTransaction( "A", -100 )
            ) );

        final Map<String, Integer> points = pointsBalanceService.calculateBalance();
        assertNotNull( points );
        assertEquals( 600, points.get( "A" ) );
        assertEquals( 400, points.get( "B" ) );
        assertEquals( 100, points.get( "C" ) );
    }

    @Test
    public void calculateBalance_zeroBalance() {
        when(pointTransactionRepository.getUnprocessedTransactionsOrderedOldestFirst())
            .thenReturn( Arrays.asList(
                createTransaction( "A", 200 ),
                createTransaction( "B", 400 ),
                createTransaction( "A", 500 ),
                createTransaction( "C", 100 )
            ) );

        when( pointTransactionRepository.getDistinctPayerNames() )
            .thenReturn( Arrays.asList( "A", "B", "C", "D" ) );

        final Map<String, Integer> points = pointsBalanceService.calculateBalance();
        assertNotNull( points );
        assertEquals( 700, points.get( "A" ) );
        assertEquals( 400, points.get( "B" ) );
        assertEquals( 100, points.get( "C" ) );

        assertTrue( points.containsKey( "D" ) );
    }

    @Test
    public void calculateBalance_NoTransactions() {
        when(pointTransactionRepository.getUnprocessedTransactionsOrderedOldestFirst())
            .thenReturn( Collections.emptyList() );

        final Map<String, Integer> points = pointsBalanceService.calculateBalance();
        assertNotNull( points );
        assertTrue( points.isEmpty() );
    }

}