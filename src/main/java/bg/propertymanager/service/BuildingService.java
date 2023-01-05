package bg.propertymanager.service;

import bg.propertymanager.model.dto.apartment.ApartmentAddDTO;
import bg.propertymanager.model.dto.building.BuildingAddDTO;
import bg.propertymanager.model.dto.building.BuildingEditDTO;
import bg.propertymanager.model.dto.building.BuildingViewDTO;
import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.UserEntity;
import bg.propertymanager.model.enums.ImagesOfBuildings;
import bg.propertymanager.repository.BuildingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BuildingService {
    private final BuildingRepository buildingRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public BuildingService(BuildingRepository buildingRepository, ModelMapper modelMapper, UserService userService) {
        this.buildingRepository = buildingRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    public List<BuildingViewDTO> findAll() {
        return buildingRepository.findAll()
                .stream()
                .map(building ->
                        modelMapper.map(building, BuildingViewDTO.class))
                .collect(Collectors.toList());
    }

    public void register(BuildingAddDTO buildingAddDTO) {
        BuildingEntity newBuilding =
                new BuildingEntity()
                        .setName(buildingAddDTO.getName())
                        .setFloors(buildingAddDTO.getFloors())
                        .setElevators(buildingAddDTO.getElevators())
                        .setImageUrl(getImageUrlBySize(buildingAddDTO))
                        .setTaxPerPerson(buildingAddDTO.getTaxPerPerson())
                        .setTaxPerDog(buildingAddDTO.getTaxPerDog())
                        .setTaxPerElevatorChip(buildingAddDTO.getTaxPerElevatorChip())
                        .setCountry(buildingAddDTO.getCountry())
                        .setCity(buildingAddDTO.getCity())
                        .setStreet(buildingAddDTO.getStreet())
                        .setBalance(BigDecimal.valueOf(0))
                        .setRegistrationDate(LocalDate.now())
                        .setNeighbours(Collections.emptySet())
                        .setApartments(Collections.emptySet())
                        .setTaxes(Collections.emptySet())
                        .setMessages(Collections.emptySet())
                        .setManager(userService.findById(1L));
        buildingRepository.save(newBuilding);
    }

    private String getImageUrlBySize(BuildingAddDTO buildingAddDTO) {
        String imageUrl;
        if (buildingAddDTO.getFloors() <= 3) {
            imageUrl = ImagesOfBuildings.SMALL_BUILDING.url;
        } else if (buildingAddDTO.getFloors() <= 8) {
            imageUrl = ImagesOfBuildings.MEDIUM_BUILDING.url;
        } else {
            imageUrl = ImagesOfBuildings.LARGE_BUILDING.url;
        }
        return imageUrl;
    }

    public BuildingViewDTO findById(Long id) {
        return buildingRepository
                .findById(id)
                .map(building -> modelMapper.map(building, BuildingViewDTO.class))
                .orElseThrow(() -> new NullPointerException("No building with this Id."));
    }

    public void updateBuilding(BuildingEditDTO buildingEditDTO) {
        BuildingEntity buildingToSave = buildingRepository
                .findById(buildingEditDTO.getId())
                .orElseThrow(() -> new NullPointerException("The building you are searching is missing"));

        UserEntity managerToSet = getManagerToSet(buildingEditDTO);
        UserEntity currentManager = buildingToSave.getManager();
        if (currentManager != userService.findById(1L) || currentManager != managerToSet) {
            userService.removeManagerRightsInBuilding(currentManager, buildingToSave);
            userService.addManagerRightsInBuilding(managerToSet, buildingToSave);
            buildingToSave.setManager(managerToSet);
        }
        buildingToSave
                .setName(buildingEditDTO.getName())
                .setFloors(buildingEditDTO.getFloors())
                .setElevators(buildingEditDTO.getElevators())
                .setBalance(buildingEditDTO.getBalance())
                .setTaxPerPerson(buildingEditDTO.getTaxPerPerson())
                .setTaxPerDog(buildingEditDTO.getTaxPerDog())
                .setTaxPerElevatorChip(buildingEditDTO.getTaxPerElevatorChip())
                .setCountry(buildingEditDTO.getCountry())
                .setCity(buildingEditDTO.getCity())
                .setStreet(buildingEditDTO.getStreet());
        buildingRepository.save(buildingToSave);
    }

    private UserEntity getManagerToSet(BuildingEditDTO buildingEditDTO) {
        return userService
                .findById(buildingEditDTO.getManager().getId());
    }

    public BuildingEntity findEntityById(Long id) {
        return buildingRepository
                .findById(id)
                .orElseThrow(() -> new NullPointerException("There is no such a building"));
    }

    public void addNeighbour(UserEntity owner, BuildingEntity building) {
        building.getNeighbours().add(owner);
        buildingRepository.save(building);
    }

    public void removeNeighbour(UserEntity owner, BuildingEntity building) {
        building.getNeighbours().remove(owner);
        buildingRepository.save(building);
    }
}
