package bg.propertymanager.service.impl;

import bg.propertymanager.model.dto.apartment.ApartmentAddDTO;
import bg.propertymanager.model.dto.apartment.ApartmentEditDTO;
import bg.propertymanager.model.dto.apartment.ApartmentViewDTO;
import bg.propertymanager.model.entity.ApartmentEntity;
import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.TaxEntity;
import bg.propertymanager.model.entity.UserEntity;
import bg.propertymanager.repository.ApartmentRepository;
import bg.propertymanager.service.ApartmentService;
import bg.propertymanager.service.BuildingService;
import bg.propertymanager.service.TaxService;
import bg.propertymanager.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class ApartmentServiceImpl implements ApartmentService {
    private final ApartmentRepository apartmentRepository;
    private final UserService userService;
    private final BuildingService buildingService;
    private final ModelMapper modelMapper;
    private final TaxService taxService;

    @Autowired
    public ApartmentServiceImpl(ApartmentRepository apartmentRepository, UserService userService, @Lazy BuildingService buildingService, ModelMapper modelMapper, @Lazy TaxService taxService) {
        this.apartmentRepository = apartmentRepository;
        this.userService = userService;
        this.buildingService = buildingService;
        this.modelMapper = modelMapper;
        this.taxService = taxService;
    }
@Override
    public void addApartment(ApartmentAddDTO apartmentAddDTO, Long buildingId) {
        UserEntity ownerToAdd = getOwnerEntity(apartmentAddDTO);
        BuildingEntity building = buildingService.findEntityById(buildingId);
        ApartmentEntity newApartment = createNewApartment(apartmentAddDTO, ownerToAdd, building);
        buildingService.addNeighbour(ownerToAdd, building);
        userService.addApartmentToUser(newApartment, ownerToAdd);
        apartmentRepository.save(newApartment);
    }

    private ApartmentEntity createNewApartment(ApartmentAddDTO apartmentAddDTO, UserEntity ownerToAdd, BuildingEntity building) {
        return new ApartmentEntity.ApartmentBuilder(apartmentAddDTO.getApartmentNumber(),ownerToAdd,building)
                .floor(apartmentAddDTO.getFloor())
                .area(apartmentAddDTO.getArea())
                .roommateCount(apartmentAddDTO.getRoommateCount())
                .elevatorChipsCount(apartmentAddDTO.getElevatorChipsCount())
                .dogsCount(apartmentAddDTO.getDogsCount())
                .periodicTax(calculatePeriodicTax(apartmentAddDTO, building))
                .taxes(Collections.emptySet())
                .build();
    }

    @Override
    public void updateApartment(ApartmentEditDTO apartmentEditDTO) {
        ApartmentEntity apartmentToUpdate = findById(apartmentEditDTO.getId());
        UserEntity ownerToRemove = apartmentToUpdate.getOwner();
        UserEntity ownerToAdd = userService.findById(apartmentEditDTO.getOwner().getId());
        BuildingEntity building = apartmentToUpdate.getBuilding();
        updateApartmentData(apartmentEditDTO, apartmentToUpdate, ownerToAdd, building);

        buildingService.removeNeighbour(ownerToRemove, building);
        buildingService.addNeighbour(ownerToAdd, building);

        userService.removeApartmentFromUser(apartmentToUpdate, ownerToRemove);
        userService.addApartmentToUser(apartmentToUpdate, ownerToAdd);

        userService.removeBuildingFromUser(ownerToRemove, building);
        userService.addBuildingToUser(ownerToAdd, building);

        apartmentRepository.save(apartmentToUpdate);


    }

    private void updateApartmentData(ApartmentEditDTO apartmentEditDTO, ApartmentEntity apartmentToUpdate, UserEntity ownerToAdd, BuildingEntity building) {
        apartmentToUpdate
                .setApartmentNumber(apartmentEditDTO.getApartmentNumber())
                .setFloor(apartmentEditDTO.getFloor())
                .setArea(apartmentEditDTO.getArea())
                .setRoommateCount(apartmentEditDTO.getRoommateCount())
                .setElevatorChipsCount(apartmentEditDTO.getElevatorChipsCount())
                .setDogsCount(apartmentEditDTO.getDogsCount())
                .setOwner(ownerToAdd)
                .setPeriodicTax(calculatePeriodicTax(apartmentToUpdate, building));
    }

    @Override
    public void deleteApartmentWithId(Long apartmentId, Long buildingId) {
        ApartmentEntity apartmentToRemove = findById(apartmentId);
        UserEntity ownerToRemove = apartmentToRemove.getOwner();
        BuildingEntity building = buildingService.findEntityById(buildingId);

        buildingService.removeNeighbour(ownerToRemove, building);
        buildingService.removeApartment(apartmentToRemove, building);

        userService.removeApartmentFromUser(apartmentToRemove, ownerToRemove);
        userService.removeBuildingFromUser(ownerToRemove, building);

        taxService.setApartmentToNullWhenDeleting(apartmentToRemove);

        apartmentRepository.delete(apartmentToRemove);
    }

    @Override
    public ApartmentViewDTO findViewById(Long apartmentId) {
        return apartmentRepository.findById(apartmentId)
                .map(a -> modelMapper.map(a, ApartmentViewDTO.class))
                .orElseThrow(() -> new NullPointerException("No such apartment is existing"));
    }
    @Override
    public void updatePeriodicTax(BuildingEntity buildingToSave) {
        Set<ApartmentEntity> apartments = apartmentRepository.findAllByBuildingId(buildingToSave.getId());
        for (ApartmentEntity apartment : apartments) {
            apartment.setPeriodicTax(calculatePeriodicTax(apartment, buildingToSave));
            apartmentRepository.save(apartment);
        }
    }
    @Override
    public ApartmentEntity getApartmentByApartmentNumberAndBuildingId(String apartmentNumber, Long buildingId) {
        return apartmentRepository
                .findByApartmentNumberAndBuilding_Id(apartmentNumber,buildingId)
                .orElseThrow(() -> new NullPointerException("No apartment with this apartmentNumber " + apartmentNumber));
    }
    @Override
    public void addNewTaxToApartment(ApartmentEntity apartmentToAddTax, TaxEntity taxToAdd) {
        apartmentToAddTax.getTaxes().add(taxToAdd);
        apartmentRepository.save(apartmentToAddTax);
    }
    @Override
    public void deleteTaxFromApartment(TaxEntity tax) {
        ApartmentEntity apartment = tax.getApartment();
        apartment.getTaxes().remove(tax);
        apartmentRepository.save(apartment);
    }
    @Override
    public BigDecimal calculateTotalMonthlyPeriodicTaxesByBuildingId(Long buildingId) {
        return apartmentRepository.findAmountOfMonthlyPeriodicTaxes(buildingId)
                .orElse(BigDecimal.ZERO);
    }
    @Override
    public Integer findTotalCountOfNeighboursInBuilding(Long buildingId) {
        return apartmentRepository
                .findTotalCountOfNeighboursByBuildingId(buildingId)
                .orElse(0);
    }
    @Override
    public Integer findTotalCountOfDogsByBuildingId(Long buildingId) {
        return apartmentRepository
                .findTotalCountOfDogsByBuildingId(buildingId)
                .orElse(0);
    }
    @Override
    public Integer findTotalCountOfElevatorChipsByBuildingId(Long buildingId) {
        return apartmentRepository
                .findTotalCountOfElevatorChipsByBuildingId(buildingId)
                .orElse(0);
    }
    @Override
    public List<ApartmentEntity> findAllApartmentsByBuildingId(Long buildingId) {
        return apartmentRepository.findAllByBuilding_IdOrderById(buildingId);
    }
    @Override
    public List<ApartmentEntity> findAllApartmentsInBuilding(Long buildingId) {
        return apartmentRepository.findAllByBuilding_IdOrderById(buildingId);
    }
    @Override
    public boolean findAllApartmentsByBuildingIdAndOwnerUsername(Long buildingId, String username) {
        List<ApartmentEntity> apartments = apartmentRepository.findAllByBuilding_IdAndOwner_Username(buildingId, username);
        return apartments.size() > 0;
    }
    @Override
    public boolean findIfUserHasOnlyOneApartmentInBuilding(Long buildingId, String username) {
        List<ApartmentEntity> apartments = apartmentRepository.findAllByBuilding_IdAndOwner_Username(buildingId, username);
        return apartments.size() == 1;
    }
    @Override
    public List<ApartmentEntity> findAllApartmentsWithPositivePeriodicTax() {
        return apartmentRepository.findAllByPeriodicTaxGreaterThan(BigDecimal.ZERO);
    }
    @Override
    public boolean checkIfApartmentNumberToAddExistInTheBuilding(Long buildingId, String apartmentNumber) {
        Optional<ApartmentEntity> apartmentWithNumberInBuilding = apartmentRepository
                .findByApartmentNumberAndBuilding_Id(apartmentNumber,buildingId);
        return apartmentWithNumberInBuilding.isPresent();
    }

    private UserEntity getOwnerEntity(ApartmentAddDTO apartmentAddDTO) {
        return userService
                .findById(apartmentAddDTO.getOwner().getId());
    }

    private ApartmentEntity findById(Long id) {
        return apartmentRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("There is no apartment with this Id"));
    }

    private BigDecimal calculatePeriodicTax(ApartmentAddDTO apartmentAddDTO, BuildingEntity building) {
        return building.getTaxPerPerson().multiply(BigDecimal.valueOf(apartmentAddDTO.getRoommateCount()))
                .add(building.getTaxPerDog().multiply(BigDecimal.valueOf(apartmentAddDTO.getDogsCount())))
                .add(building.getTaxPerElevatorChip().multiply(BigDecimal.valueOf(apartmentAddDTO.getElevatorChipsCount())));
    }

    private BigDecimal calculatePeriodicTax(ApartmentEntity apartment, BuildingEntity building) {
        return building.getTaxPerPerson().multiply(BigDecimal.valueOf(apartment.getRoommateCount()))
                .add(building.getTaxPerDog().multiply(BigDecimal.valueOf(apartment.getDogsCount())))
                .add(building.getTaxPerElevatorChip().multiply(BigDecimal.valueOf(apartment.getElevatorChipsCount())));
    }
}
