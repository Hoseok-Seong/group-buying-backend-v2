package shop.donutmarket.donut.domain.review.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import shop.donutmarket.donut.domain.review.model.Rate;
import shop.donutmarket.donut.domain.review.repository.RateRepository;
import shop.donutmarket.donut.global.exception.Exception404;
import shop.donutmarket.donut.global.exception.Exception500;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RateService {
    
    private final RateRepository rateRepository;

    @Transactional(readOnly = true)
    public Rate 승급하기(int ratePoint) {
        try{
            // 평균 거래 5 이상
            if(ratePoint >= 30) {
                Optional<Rate> rateOP = rateRepository.findById(1L);
                if (rateOP.isEmpty()) {
                    throw new Exception404("존재하지 않는 등급입니다");
                }
                return rateOP.get();
            }

            // 평균 거래 20 이상
            if(ratePoint >= 120) {
                Optional<Rate> rateOP = rateRepository.findById(2L);
                if (rateOP.isEmpty()) {
                    throw new Exception404("존재하지 않는 등급입니다");
                }
                return rateOP.get();
            }

            // 평균 거래 50 이상
            if(ratePoint >= 300) {
                Optional<Rate> rateOP = rateRepository.findById(3L);
                if (rateOP.isEmpty()) {
                    throw new Exception404("존재하지 않는 등급입니다");
                }
                return rateOP.get();
            }

            // 평균 거래 100 이상
            if(ratePoint >= 600) {
                Optional<Rate> rateOP = rateRepository.findById(4L);
                if (rateOP.isEmpty()) {
                    throw new Exception404("존재하지 않는 등급입니다");
                }
                return rateOP.get();
            }
        } catch (Exception e) {
            throw new Exception500("승급하기 실패 : " + e.getMessage());
        }
        return null;
    }
}
