package com.fetch.points.repository;

import java.util.List;

import com.fetch.points.model.PointTransaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointTransactionRepository extends CrudRepository<PointTransaction, Long> {

    /**
     * @return A list of point transactions ordered by timestamp with the most recent timestamps first
     */
    @Query("select pt from PointTransaction as pt where pt.userId = ?1 and pt.processed = false order by pt.timestamp asc")
    public List<PointTransaction> getUnprocessedTransactionsOrderedOldestFirst(int userId);

    @Query("select distinct pt.payer from PointTransaction as pt where pt.userId = ?1")
    public List<String> getDistinctPayerNames(int userId);
}
