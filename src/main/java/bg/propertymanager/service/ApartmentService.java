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
        ApartmentEntity newApartment = new ApartmentEntity()
                .setApartmentNumber(apartmentAddDTO.getApartmentNumber())
                .setFloor(apartmentAddDTO.getFloor())
                .setArea(apartmentAddDTO.getArea())
                .setRoommateCount(apartmentAddDTO.getRoommateCount())
                .setElevatorChipsCount(apartmentAddDTO.getElevatorChipsCount())
                .setDogsCount(apartmentAddDTO.getDogsCount())
                .setOwner(getOwnerEntity(apartmentAddDTO))
                .setBuilding(buildingService.findEntityById(buildingId))
                .setPeriodicTax(calculatePeriodicTax(apartmentAddDTO, buildingId))
                .setTotalMoneyPaid(BigDecimal.valueOf(0))
                .setMoneyOwed(BigDecimal.valueOf(0));
        buildingService.addNeighbour(apartmentAddDTO, buildingId);
        UserEntity ownerToAdd = userService.findById(apartmentAddDTO.getOwner().getId());
//        userService.addApartmentAndBuildingToUser(ownerToAdd,buildingId,newApartment);
        apartmentRepository.save(newApartment);
    }



    private BigDecimal calculatePeriodicTax(ApartmentAddDTO apartmentAddDTO, Long buildingId) {
        BuildingEntity currentBuilding = buildingService
                .findEntityById(buildingId);
//TODO: Make it cleaner / Refactor
        return currentBuilding.getTaxPerPerson().multiply(BigDecimal.valueOf(apartmentAddDTO.getRoommateCount()))
                .add(currentBuilding.getTaxPerDog().multiply(BigDecimal.valueOf(apartmentAddDTO.getDogsCount())))
                .add(currentBuilding.getTaxPerElevatorChip().multiply(BigDecimal.valueOf(apartmentAddDTO.getElevatorChipsCount())));
    }

    private UserEntity getOwnerEntity(ApartmentAddDTO apartmentAddDTO) {
        return userService
                .findById(apartmentAddDTO.getOwner().getId());
    }

    public void updateApartment(ApartmentEditDTO apartmentEditDTO) {
        //TODO
        ApartmentEntity apartmentToUpdate =
                findById(apartmentEditDTO.getId());
        UserEntity ownerToRemove = userService
                .findById(findById(apartmentEditDTO.getId()).getOwner().getId());
        UserEntity ownerToAdd = userService
                .findById(apartmentEditDTO.getOwner().getId());
        apartmentToUpdate
                .setApartmentNumber(apartmentEditDTO.getApartmentNumber())
                .setFloor(apartmentEditDTO.getFloor())
                .setArea(apartmentEditDTO.getArea())
                .setRoommateCount(apartmentEditDTO.getRoommateCount())
                .setElevatorChipsCount(apartmentEditDTO.getElevatorChipsCount())
                .setDogsCount(apartmentEditDTO.getDogsCount())
                .setOwner(ownerToAdd);
//        buildingService.addAndRemoveNeighbour(apartmentAddDTO, apartmentEditDTO.getBuilding().getId());
        userService.removeApartmentFromUser(apartmentToUpdate,ownerToRemove);
        userService.addApartmentToUser(apartmentToUpdate,ownerToAdd);

        apartmentRepository.save(apartmentToUpdate);



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
}
