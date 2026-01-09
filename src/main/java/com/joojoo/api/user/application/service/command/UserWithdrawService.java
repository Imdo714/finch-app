package com.joojoo.api.user.application.service.command;

import com.joojoo.api.block.domain.repository.BlockRepository;
import com.joojoo.api.jwt.application.JwtTokenUseCase;
import com.joojoo.api.tradeLog.application.port.in.DeleteTradeLogUseCase;
import com.joojoo.api.tradeLog.domain.repository.TradeLogRepository;
import com.joojoo.api.user.application.auth.withdraw.out.SocialUnlink;
import com.joojoo.api.user.application.port.in.WithdrawUserUseCase;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UserWithdrawService implements WithdrawUserUseCase {

    private final UserRepository userRepository;
    private final JwtTokenUseCase jwtTokenUseCase;
    private final BlockRepository blockRepository;
    private final TradeLogRepository tradeLogRepository;
    private final Map<String, SocialUnlink> socialUnlink;

    @Override
    public void withdraw(Long userId, HttpServletRequest request) {
        User user = userRepository.getUserById(userId);

        unSocialWithdraw(user); // 소셜 계정 탈퇴
        blockRepository.deleteAllBlockMappings(userId); // 블럭(노트) 연관관계 정리
        tradeLogRepository.withdrawByUserId(userId); // 템플릿 정리
        jwtTokenUseCase.clearUserTokens(userId, request); // 토큰 정리
        user.withdraw(); // 회원 DB 정리

        // TODO : 추후에 고도화 이미지 삭제 해야 함 !!
    }

    private void unSocialWithdraw(User user) {
        SocialUnlink strategy = socialUnlink.get(user.getProvider().name());
        if (strategy != null) {
            strategy.unlink(user);
        }
    }
}
