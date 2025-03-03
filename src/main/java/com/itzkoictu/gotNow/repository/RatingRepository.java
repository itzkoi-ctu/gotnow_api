package com.itzkoictu.gotNow.repository;

import com.itzkoictu.gotNow.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends MongoRepository<Rating, Long> {
    public List<Rating> findAllByProductId(Long productId);
}
