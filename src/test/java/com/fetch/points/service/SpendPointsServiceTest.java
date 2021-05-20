package com.fetch.points.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fetch.points.model.PointTransaction;
import com.fetch.points.model.SpentPoint;
import com.fetch.points.repository.PointTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SpendPointsServiceTest {
    
    private static final int USER_ID = 1;

    @InjectMocks
    private SpendPointsService spendPointsService;

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
    public void spendPoints() {
        when(pointTransactionRepository.getUnprocessedTransactionsOrderedOldestFirst(USER_ID))
            .thenReturn( Arrays.asList(
                createTransaction( "DANNON", 300 ),
                createTransaction( "UNILEVER", 200 ),
                createTransaction( "DANNON", -200 ),
                createTransaction( "MILLER COORS", 100000 ),
                createTransaction( "DANNON", 1000 )
            ) );

        List<SpentPoint> spentPoints = spendPointsService.spendPoints(USER_ID, 5000 );

        assertNotNull( spentPoints );

        Map<String, Integer> spentPointsByPayer = spentPoints.stream()
            .collect(
                Collectors.toMap(
                    SpentPoint::getPayer,
                    SpentPoint::getPoints
        ) );

        assertEquals( -100, spentPointsByPayer.get( "DANNON" ) );
        assertEquals( -200, spentPointsByPayer.get( "UNILEVER" ));
        assertEquals( -4700, spentPointsByPayer.get( "MILLER COORS" ) );
    }

    @Test
    public void spendPoints_balanceTooSmall() {
        when(pointTransactionRepository.getUnprocessedTransactionsOrderedOldestFirst(USER_ID))
            .thenReturn( Arrays.asList(
                createTransaction( "DANNON", 300 ),
                createTransaction( "UNILEVER", 200 )
            ) );

        assertThrows( IllegalArgumentException.class, () ->
            spendPointsService.spendPoints(USER_ID, 5000 ) );
    }

    @Test
    public void spendPoints_transactionPointsFullySpent() {
        ArgumentCaptor<PointTransaction> arg = ArgumentCaptor.forClass( PointTransaction.class );

        when(pointTransactionRepository.getUnprocessedTransactionsOrderedOldestFirst(USER_ID))
            .thenReturn( Arrays.asList(
                createTransaction( "DANNON", 300 ),
                createTransaction( "UNILEVER", 200 )
            ) );

        List<SpentPoint> spentPoints = spendPointsService.spendPoints( USER_ID,300 );

        verify( pointTransactionRepository, times( 1 )).save(arg.capture());

        PointTransaction processedTransaction = arg.getValue();
        assertNotNull( processedTransaction );
        assertTrue( processedTransaction.isProcessed() );
    }

    @Test
    public void spendPoints_transactionPointsLeftover() {
        ArgumentCaptor<PointTransaction> arg = ArgumentCaptor.forClass( PointTransaction.class );

        when(pointTransactionRepository.getUnprocessedTransactionsOrderedOldestFirst(USER_ID))
            .thenReturn( Arrays.asList(
                createTransaction( "DANNON", 300 ),
                createTransaction( "UNILEVER", 200 )
            ) );

        List<SpentPoint> spentPoints = spendPointsService.spendPoints( USER_ID,400 );

        verify( pointTransactionRepository, times( 2 )).save(arg.capture());
        PointTransaction processedTransaction = arg.getValue();
        assertNotNull( processedTransaction );
        assertFalse( processedTransaction.isProcessed() );
        assertEquals( 100, processedTransaction.getPoints() );
    }
}