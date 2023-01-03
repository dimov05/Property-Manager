package bg.propertymanager.service;

import bg.propertymanager.model.dto.BuildingAddDTO;
import bg.propertymanager.model.dto.BuildingViewDTO;
import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.enums.ImagesOfBuildings;
import bg.propertymanager.repository.BuildingRepository;
import bg.propertymanager.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
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

    public BuildingEntity register(BuildingAddDTO buildingAddDTO) {
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
                        .setManager(userService.findById(1L));
        buildingRepository.save(newBuilding);

        return newBuilding;
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
}
