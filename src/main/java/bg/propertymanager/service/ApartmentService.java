package bg.propertymanager.service;

import bg.propertymanager.model.dto.apartment.ApartmentAddDTO;
import bg.propertymanager.model.dto.apartment.ApartmentEditDTO;
import bg.propertymanager.model.dto.apartment.ApartmentViewDTO;
import bg.propertymanager.model.entity.ApartmentEntity;
import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.UserEntity;
import bg.propertymanager.repository.ApartmentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;

@Service
@Transactional
public class ApartmentService {
    private final ApartmentRepository apartmentRepository;
    private final UserService userService;
    private final BuildingService buildingService;
    private final ModelMapper modelMapper;

    public ApartmentService(ApartmentRepository apartmentRepository, UserService userService, BuildingService buildingService, ModelMapper modelMapper) {
        this.apartmentRepository = apartmentRepository;
        this.userService = userService;
        this.buildingService = buildingService;
        this.modelMapper = modelMapper;
    }

    public void registerApartment(ApartmentAddDTO apartmentAddDTO, Long buildingId) {
        UserEntity ownerToAdd = getOwnerEntity(apartmentAddDTO);
        BuildingEntity building = buildingService.findEntityById(buildingId);
        ApartmentEntity newApartment = new ApartmentEntity()
                .setApartmentNumber(apartmentAddDTO.getApartmentNumber())
                .setFloor(apartmentAddDTO.getFloor())
                .setArea(apartmentAddDTO.getArea())
                .setRoommateCount(apartmentAddDTO.getRoommateCount())
                .setElevatorChipsCount(apartmentAddDTO.getElevatorChipsCount())
                .setDogsCount(apartmentAddDTO.getDogsCount())
                .setOwner(ownerToAdd)
                .setBuilding(buildingService.findEntityById(buildingId))
                .setPeriodicTax(calculatePeriodicTax(apartmentAddDTO, buildingId))
                .setTotalMoneyPaid(BigDecimal.valueOf(0))
                .setMoneyOwed(BigDecimal.valueOf(0));
        buildingService.addNeighbour(ownerToAdd, building);
        userService.addApartmentToUser(newApartment, ownerToAdd);
        apartmentRepository.save(newApartment);
    }

    public void updateApartment(ApartmentEditDTO apartmentEditDTO) {
        //TODO
        ApartmentEntity apartmentToUpdate = findById(apartmentEditDTO.getId());
        UserEntity ownerToRemove = apartmentToUpdate.getOwner();
        UserEntity ownerToAdd = userService.findById(apartmentEditDTO.getOwner().getId());
        BuildingEntity building = apartmentToUpdate.getBuilding();
        apartmentToUpdate
                .setApartmentNumber(apartmentEditDTO.getApartmentNumber())
                .setFloor(apartmentEditDTO.getFloor())
                .setArea(apartmentEditDTO.getArea())
                .setRoommateCount(apartmentEditDTO.getRoommateCount())
                .setElevatorChipsCount(apartmentEditDTO.getElevatorChipsCount())
                .setDogsCount(apartmentEditDTO.getDogsCount())
                .setOwner(ownerToAdd)
                .setPeriodicTax(calculatePeriodicTax(apartmentEditDTO, apartmentToUpdate.getBuilding().getId()));

        buildingService.removeNeighbour(ownerToRemove, building);
        buildingService.addNeighbour(ownerToAdd, building);

        userService.removeApartmentFromUser(apartmentToUpdate, ownerToRemove);
        userService.addApartmentToUser(apartmentToUpdate, ownerToAdd);

        userService.removeBuildingFromUser(ownerToRemove, building);
        userService.addBuildingToUser(ownerToAdd, building);

        apartmentRepository.save(apartmentToUpdate);


    }

    public void deleteApartmentWithId(Long apartmentId, Long buildingId) {
        ApartmentEntity apartmentToRemove = findById(apartmentId);
        UserEntity ownerToRemove = apartmentToRemove.getOwner();
        BuildingEntity building = buildingService.findEntityById(buildingId);

        buildingService.removeNeighbour(ownerToRemove, building);
        buildingService.removeApartment(apartmentToRemove, building);

        userService.removeApartmentFromUser(apartmentToRemove, ownerToRemove);
        userService.removeBuildingFromUser(ownerToRemove, building);

        apartmentRepository.delete(apartmentToRemove);
    }

    private UserEntity getOwnerEntity(ApartmentAddDTO apartmentAddDTO) {
        return userService
                .findById(apartmentAddDTO.getOwner().getId());
    }

    private ApartmentEntity findById(Long id) {
        return apartmentRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("There is no apartment with this Id"));
    }

    public ApartmentViewDTO findViewById(Long apartmentId) {
        return apartmentRepository.findById(apartmentId)
                .map(a -> modelMapper.map(a, ApartmentViewDTO.class))
                .orElseThrow(() -> new NullPointerException("No such apartment is existing"));
    }

    private BigDecimal calculatePeriodicTax(ApartmentEditDTO apartmentEditDTO, Long buildingId) {
        BuildingEntity currentBuilding = buildingService
                .findEntityById(buildingId);
//TODO: Make it cleaner / Refactor
        return currentBuilding.getTaxPerPerson().multiply(BigDecimal.valueOf(apartmentEditDTO.getRoommateCount()))
                .add(currentBuilding.getTaxPerDog().multiply(BigDecimal.valueOf(apartmentEditDTO.getDogsCount())))
                .add(currentBuilding.getTaxPerElevatorChip().multiply(BigDecimal.valueOf(apartmentEditDTO.getElevatorChipsCount())));
    }

    private BigDecimal calculatePeriodicTax(ApartmentAddDTO apartmentAddDTO, Long buildingId) {
        BuildingEntity currentBuilding = buildingService
                .findEntityById(buildingId);
//TODO: Make it cleaner / Refactor
        return currentBuilding.getTaxPerPerson().multiply(BigDecimal.valueOf(apartmentAddDTO.getRoommateCount()))
                .add(currentBuilding.getTaxPerDog().multiply(BigDecimal.valueOf(apartmentAddDTO.getDogsCount())))
                .add(currentBuilding.getTaxPerElevatorChip().multiply(BigDecimal.valueOf(apartmentAddDTO.getElevatorChipsCount())));
    }
}
