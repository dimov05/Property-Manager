package bg.propertymanager.service;

import bg.propertymanager.model.dto.apartment.ApartmentAddDTO;
import bg.propertymanager.model.dto.apartment.ApartmentEditDTO;
import bg.propertymanager.model.dto.apartment.ApartmentViewDTO;
import bg.propertymanager.model.entity.ApartmentEntity;
import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.TaxEntity;

import java.math.BigDecimal;
import java.util.List;


public interface ApartmentService {
    void addApartment(ApartmentAddDTO apartmentAddDTO, Long buildingId);

    void updateApartment(ApartmentEditDTO apartmentEditDTO);

    void deleteApartmentWithId(Long apartmentId, Long buildingId);

    ApartmentViewDTO findViewById(Long apartmentId);


    void updatePeriodicTax(BuildingEntity buildingToSave);

    ApartmentEntity getApartmentByApartmentNumberAndBuildingId(String apartmentNumber, Long buildingId);

    void addNewTaxToApartment(ApartmentEntity apartmentToAddTax, TaxEntity taxToAdd);

    void deleteTaxFromApartment(TaxEntity tax);

    BigDecimal calculateTotalMonthlyPeriodicTaxesByBuildingId(Long buildingId);

    Integer findTotalCountOfNeighboursInBuilding(Long buildingId);

    Integer findTotalCountOfDogsByBuildingId(Long buildingId);

    Integer findTotalCountOfElevatorChipsByBuildingId(Long buildingId);

    List<ApartmentEntity> findAllApartmentsByBuildingId(Long buildingId);

    List<ApartmentEntity> findAllApartmentsInBuilding(Long buildingId);

    boolean findAllApartmentsByBuildingIdAndOwnerUsername(Long buildingId, String username);
    boolean findIfUserHasOnlyOneApartmentInBuilding(Long buildingId, String username);

    List<ApartmentEntity> findAllApartmentsWithPositivePeriodicTax();

    boolean checkIfApartmentNumberToAddExistInTheBuilding(Long buildingId, String apartmentNumber);
}
