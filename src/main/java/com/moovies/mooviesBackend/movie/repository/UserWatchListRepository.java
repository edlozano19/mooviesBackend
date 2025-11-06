package com.moovies.mooviesBackend.movie.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.moovies.mooviesBackend.movie.entity.UserWatchList;

@Repository
public interface UserWatchListRepository extends JpaRepository<UserWatchList, Long> {
    Optional<UserWatchList> findByUserIdAndMovieId(Long userId, Long movieId);

    List<UserWatchList> findByUserId(Long userId);

    boolean existsByUserIdAndMovieId(Long userId, Long movieId);

    @Modifying
    void deleteByUserIdAndMovieId(Long userId, Long movieId);

    long countByUserId(Long userId);
}
