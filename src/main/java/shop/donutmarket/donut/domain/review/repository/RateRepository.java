package shop.donutmarket.donut.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.donutmarket.donut.domain.review.model.Rate;
import shop.donutmarket.donut.domain.review.model.Review;

import java.util.Optional;


public interface RateRepository extends JpaRepository<Rate, Long>{

    @Query("select r from Rate r where r.id = :id")
    Optional<Rate> findById(@Param("id") Long id);

}
