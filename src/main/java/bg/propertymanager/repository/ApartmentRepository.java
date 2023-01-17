package bg.propertymanager.repository;

import bg.propertymanager.model.entity.ApartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ApartmentRepository extends JpaRepository<ApartmentEntity, Long> {
    Set<ApartmentEntity> findAllByBuildingId(Long buildingId);

    Optional<ApartmentEntity> findByApartmentNumber(String apartmentNumber);

    List<ApartmentEntity> findAllByBuilding_IdOrderById(Long buildingId);

    @Query("SELECT SUM(а.periodicTax)  FROM ApartmentEntity as а WHERE а.building.id = :buildingId")
    Optional<BigDecimal> findAmountOfMonthlyPeriodicTaxes(@Param("buildingId") Long buildingId);

    @Query("SELECT SUM(а.roommateCount)  FROM ApartmentEntity as а WHERE а.building.id = :buildingId")
    Optional<Integer> findTotalCountOfNeighboursByBuildingId(@Param("buildingId") Long buildingId);

    @Query("SELECT SUM(а.dogsCount)  FROM ApartmentEntity as а WHERE а.building.id = :buildingId")
    Optional<Integer> findTotalCountOfDogsByBuildingId(@Param("buildingId") Long buildingId);

    @Query("SELECT SUM(а.elevatorChipsCount)  FROM ApartmentEntity as а WHERE а.building.id = :buildingId")
    Optional<Integer> findTotalCountOfElevatorChipsByBuildingId(@Param("buildingId") Long buildingId);



}
