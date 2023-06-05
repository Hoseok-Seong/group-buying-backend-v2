package shop.donutmarket.donut.domain.myPage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.donutmarket.donut.domain.blacklist.model.Blacklist;
import shop.donutmarket.donut.domain.blacklist.repository.BlackListRepository;
import shop.donutmarket.donut.domain.board.model.Board;
import shop.donutmarket.donut.domain.board.repository.BoardRepository;
import shop.donutmarket.donut.domain.myPage.dto.MyPageResp;
import shop.donutmarket.donut.domain.payment.model.Payment;
import shop.donutmarket.donut.domain.payment.repository.PaymentRepository;
import shop.donutmarket.donut.domain.report.model.Report;
import shop.donutmarket.donut.domain.report.repository.ReportRepository;
import shop.donutmarket.donut.domain.review.model.Review;
import shop.donutmarket.donut.domain.review.repository.ReviewRepository;
import shop.donutmarket.donut.domain.user.model.User;
import shop.donutmarket.donut.domain.user.repository.UserRepository;
import shop.donutmarket.donut.global.auth.MyUserDetails;
import shop.donutmarket.donut.global.exception.Exception404;
import shop.donutmarket.donut.global.exception.Exception500;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final BoardRepository boardRepository;

    private final PaymentRepository paymentRepository;

    private final BlackListRepository blackListRepository;

    private final ReportRepository reportRepository;

    private final ReviewRepository reviewRepository;

    private final UserRepository userRepository;

//    private final FileLoad fileLoad;

    @Transactional(readOnly = true)
    public MyPageResp.MyBoardDTO 나의게시글보기(Long id) {
        try {
            List<Board> boards = boardRepository.findByOrganizerId(id);
            MyPageResp.MyBoardDTO myBoardDTOS = new MyPageResp.MyBoardDTO(boards);
            return myBoardDTOS;
        } catch (Exception e) {
            throw new Exception500("나의 게시글 보기 실패 : " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public MyPageResp.MyPaymentDTO 나의구매내역보기(Long id) {
        try {
            List<Payment> payments = paymentRepository.findByUserId(id);
            MyPageResp.MyPaymentDTO myPaymentDTOS = new MyPageResp.MyPaymentDTO(payments);
            return myPaymentDTOS;
        } catch (Exception e) {
            throw new Exception500("나의 구매내역 보기 실패 : " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public MyPageResp.MyBlacklistDTO 나의블랙리스트보기(Long id) {
        try {
            List<Blacklist> myBlacklistDTOS = blackListRepository.findByUserId(id);
            MyPageResp.MyBlacklistDTO myBlacklistDTO = new MyPageResp.MyBlacklistDTO(myBlacklistDTOS);
            return myBlacklistDTO;
        } catch (Exception e) {
            throw new Exception500("나의 블랙리스트 보기 실패 : " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public MyPageResp.MyReportDTO 나의신고내역보기(Long id) {
        try {
            List<Report> myReportDTOS = reportRepository.findByReporterId(id);
            MyPageResp.MyReportDTO myReportDTO = new MyPageResp.MyReportDTO(myReportDTOS);
            return myReportDTO;
        } catch (Exception e) {
            throw new Exception500("나의 신고내역 보기 실패 : " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public MyPageResp.MyReviewDTO 나의리뷰목록보기(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        try {
            List<Review> myReviews = reviewRepository.findAllByReviewerId(myUserDetails.getUser().getId());
            MyPageResp.MyReviewDTO resp = new MyPageResp.MyReviewDTO(myReviews);
            return resp;
        } catch (Exception e) {
            throw new Exception500("나의 리뷰목록 보기 실패 : " + e.getMessage());
        }
    }

//    @Transactional
//    public void 프로필변경(String imgPath, @AuthenticationPrincipal MyUserDetails myUserDetails) {
//        try {
//            Long userId = myUserDetails.getUser().getId();
//            Optional<User> userOP = userRepository.findById(userId);
//            if (!(userOP.isPresent())) {
//                throw new Exception404("존재하지 않는 유저입니다.");
//            }
//            User userPS = userOP.get();
//            String imgName = "User"+ Long.toString(userId) + "profile";
//
//            userPS.updateProfile(imgName);
//
//            fileLoad.uploadFile(imgName, imgPath);
//        } catch (Exception e) {
//            throw new Exception500("프로필 수정에 실패했습니다");
//        }
//    }
}
