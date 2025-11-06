package com.moovies.mooviesBackend.movie.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.moovies.mooviesBackend.movie.entity.UserWatchedList;

@Repository
public interface UserWatchedListRepository extends JpaRepository<UserWatchedList, Long> {
    Optional<UserWatchedList> findByUserIdAndMovieId(Long userId, Long movieId);

    boolean existsByUserIdAndMovieId(Long userId, Long movieId);

    List<UserWatchedList> findByUserId(Long userId);

    @Modifying
    void deleteByUserIdAndMovieId(Long userId, Long movieId);

    long countByUserId(Long userId);
    
}
