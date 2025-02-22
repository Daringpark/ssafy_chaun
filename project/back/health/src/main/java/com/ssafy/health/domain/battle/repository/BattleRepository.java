package com.ssafy.health.domain.battle.repository;

import com.ssafy.health.domain.battle.dto.response.BattleStatsDto;
import com.ssafy.health.domain.battle.entity.Battle;
import com.ssafy.health.domain.battle.entity.BattleStatus;
import com.ssafy.health.domain.crew.entity.Crew;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BattleRepository extends JpaRepository<Battle, Long> {

    Optional<Battle> findFirstByHomeCrewOrAwayCrewOrderByCreatedAtDesc(Crew homeCrew, Crew awayCrew);

    Optional<Battle> findFirstByHomeCrewIdOrAwayCrewIdOrderByCreatedAtDesc(Long homeCrewId, Long awayCrewId);

    @Query("SELECT new com.ssafy.health.domain.battle.dto.response.BattleStatsDto(" +
            "COUNT(b), " +
            "SUM(CASE " +
            "  WHEN (b.homeCrew.id = :crewId AND b.homeCrewScore > b.awayCrewScore) " +
            "    OR (b.awayCrew.id = :crewId AND b.awayCrewScore > b.homeCrewScore) THEN 1 ELSE 0 END)) " +
            "FROM Battle b " +
            "WHERE (b.homeCrew.id = :crewId OR b.awayCrew.id = :crewId) " +
            "AND b.status = 'FINISHED'")
    Optional<BattleStatsDto> countTotalAndWonBattles(Long crewId);

    @Query("SELECT b FROM Battle b WHERE (b.homeCrew.id = :crewId OR b.awayCrew.id = :crewId) AND b.status = :status")
    Optional<Battle> findBattleByCrewId(Long crewId, BattleStatus status);

    @Query("SELECT DISTINCT b FROM Battle b JOIN FETCH b.awayCrew ac JOIN FETCH b.homeCrew hc WHERE b.status = :status")
    List<Battle> findByStatus(BattleStatus status);

    @Query("SELECT b FROM Battle b JOIN FETCH b.homeCrew JOIN FETCH b.awayCrew WHERE b.id = :id")
    Optional<Battle> findByIdWithCrew(Long id);
}
