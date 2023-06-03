package shop.donutmarket.donut.domain.review.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.donutmarket.donut.domain.review.dto.ReviewReq.ReviewSaveReqDTO;
import shop.donutmarket.donut.domain.review.dto.ReviewResp.ReviewSaveRespDTO;
import shop.donutmarket.donut.domain.review.model.Review;
import shop.donutmarket.donut.domain.review.repository.ReviewRepository;
import shop.donutmarket.donut.domain.user.model.User;
import shop.donutmarket.donut.domain.user.repository.UserRepository;
import shop.donutmarket.donut.global.auth.MyUserDetails;
import shop.donutmarket.donut.global.exception.Exception404;
import shop.donutmarket.donut.global.exception.Exception500;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewSaveRespDTO 리뷰작성(ReviewSaveReqDTO reviewSaveReqDTO, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        Optional<User> reviewedUserOP = userRepository.findByIdJoinFetch(reviewSaveReqDTO.getReviewedUserId());

        Optional<User> reviewerUserOP = userRepository.findByIdJoinFetch(myUserDetails.getUser().getId());

        if(reviewerUserOP.isEmpty()) {
            throw new Exception404("존재하지 않는 회원입니다");
        }

        if (reviewedUserOP.isEmpty()) {
            throw new Exception404("리뷰할 유저를 찾을 수 없습니다");
        }

        try {
            User reviewedUserPS = reviewedUserOP.get();
            User reviewerUserPS = reviewerUserOP.get();

            // 리뷰 생성
            Review review = Review.builder().reviewer(myUserDetails.getUser()).reviewed(reviewedUserPS)
                    .score(reviewSaveReqDTO.getScore()).comment(reviewSaveReqDTO.getComment()).createdAt(LocalDateTime.now()).build();

            Review reviewPS = reviewRepository.save(review);

            // 리뷰 등록시 유저 두명 모두 기본 포인트 1점 추가
            reviewedUserPS.rateUp();
            reviewerUserPS.rateUp();

            // 리뷰 평점별 포인트 추가
            reviewedUserPS.reviewScoreUp(reviewSaveReqDTO.getScore());

            ReviewSaveRespDTO saveRespDTO = new ReviewSaveRespDTO(reviewPS);
            return saveRespDTO;
        } catch (Exception e) {
            throw new Exception500("리뷰 작성 실패 : " + e.getMessage());
        }
    }
}
