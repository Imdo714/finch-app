package com.joojoo.api.tradeLog.domain.repository;

import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;

public interface TradeLogRepository {
    /** TradeLog 저장 */
    TradeLog save(TradeLog tradeLog);

    /** TradeLog 회원 삭제 (중간 테이블은 블럭에서 삭제해줌) */
    void withdrawByUserId(Long userId);
}
