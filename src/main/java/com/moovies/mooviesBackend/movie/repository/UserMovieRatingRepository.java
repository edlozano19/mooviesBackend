package com.moovies.mooviesBackend.movie.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moovies.mooviesBackend.movie.entity.UserMovieRating;

@Repository
public interface UserMovieRatingRepository extends JpaRepository<UserMovieRating, Long>{
    Optional<UserMovieRating> findByUserIdAndMovieId(Long userId, Long movieId);

    List<UserMovieRating> findByUserId(Long userId);
    
    List<UserMovieRating> findByMovieId(Long movieId);

    boolean existsByUserIdAndMovieId(Long userId, Long movieId);

    long countByMovieId(Long movieId);

    void deleteByUserIdAndMovieId(Long userId, Long movieId);

    @Query("SELECT AVG(r.rating) FROM UserMovieRating r WHERE r.movie.id = :movieId")
    Double calculateAverageRating(@Param("movieId") Long movieId);
}
