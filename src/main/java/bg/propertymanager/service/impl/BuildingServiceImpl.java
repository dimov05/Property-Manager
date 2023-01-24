package bg.propertymanager.service.impl;

import bg.propertymanager.model.dto.building.BuildingAddDTO;
import bg.propertymanager.model.dto.building.BuildingChangeTaxesDTO;
import bg.propertymanager.model.dto.building.BuildingEditDTO;
import bg.propertymanager.model.dto.building.BuildingViewDTO;
import bg.propertymanager.model.entity.*;
import bg.propertymanager.model.enums.ImagesOfBuildings;
import bg.propertymanager.repository.BuildingRepository;
import bg.propertymanager.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BuildingServiceImpl implements BuildingService {
    private final BuildingRepository buildingRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final TaxService taxService;
    private final ApartmentService apartmentService;
    private final ExpenseService expenseService;

    @Autowired
    public BuildingServiceImpl(BuildingRepository buildingRepository, ModelMapper modelMapper, UserService userService, @Lazy TaxService taxService, ApartmentService apartmentService, @Lazy ExpenseService expenseService) {
        this.buildingRepository = buildingRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.taxService = taxService;
        this.apartmentService = apartmentService;
        this.expenseService = expenseService;
    }

    public void register(BuildingAddDTO buildingAddDTO) {
        BuildingEntity newBuilding =
                new BuildingEntity()
                        .setName(buildingAddDTO.getName())
                        .setFloors(buildingAddDTO.getFloors())
                        .setElevators(buildingAddDTO.getElevators())
                        .setImageUrl(getImageUrlBySize(buildingAddDTO.getFloors()))
                        .setTaxPerPerson(buildingAddDTO.getTaxPerPerson())
                        .setTaxPerDog(buildingAddDTO.getTaxPerDog())
                        .setTaxPerElevatorChip(buildingAddDTO.getTaxPerElevatorChip())
                        .setCountry(buildingAddDTO.getCountry())
                        .setCity(buildingAddDTO.getCity())
                        .setStreet(buildingAddDTO.getStreet())
                        .setRegistrationDate(LocalDate.now())
                        .setNeighbours(Collections.emptySet())
                        .setApartments(Collections.emptySet())
                        .setTaxes(Collections.emptySet())
                        .setExpenses(Collections.emptySet())
                        .setMessages(Collections.emptySet())
                        .setManager(userService.findById(1L));
        buildingRepository.save(newBuilding);
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
                .setImageUrl(getImageUrlBySize(buildingEditDTO.getFloors()))
                .setTaxPerPerson(buildingEditDTO.getTaxPerPerson())
                .setTaxPerDog(buildingEditDTO.getTaxPerDog())
                .setTaxPerElevatorChip(buildingEditDTO.getTaxPerElevatorChip())
                .setCountry(buildingEditDTO.getCountry())
                .setCity(buildingEditDTO.getCity())
                .setStreet(buildingEditDTO.getStreet());
        apartmentService.updatePeriodicTax(buildingToSave);
        buildingRepository.save(buildingToSave);
    }

    public void updateBuildingsPerTaxes(BuildingChangeTaxesDTO buildingChangeTaxesDTO) {
        BuildingEntity buildingToSave = buildingRepository
                .findById(buildingChangeTaxesDTO.getId())
                .orElseThrow(() -> new NullPointerException("The building you are searching is missing"));
        buildingToSave.setTaxPerDog(buildingChangeTaxesDTO.getTaxPerDog())
                .setTaxPerPerson(buildingChangeTaxesDTO.getTaxPerPerson())
                .setTaxPerElevatorChip(buildingChangeTaxesDTO.getTaxPerElevatorChip());
        apartmentService.updatePeriodicTax(buildingToSave);
        buildingRepository.save(buildingToSave);
    }

    public List<BuildingViewDTO> findAll() {
        return buildingRepository.findAll()
                .stream()
                .map(building ->
                        modelMapper.map(building, BuildingViewDTO.class))
                .collect(Collectors.toList());
    }

    public BuildingEntity findEntityById(Long id) {
        return buildingRepository
                .findById(id)
                .orElseThrow(() -> new NullPointerException("There is no such a building"));
    }

    public BuildingViewDTO findById(Long id) {
        return buildingRepository
                .findById(id)
                .map(building -> modelMapper.map(building, BuildingViewDTO.class))
                .orElseThrow(() -> new NullPointerException("No building with this Id."));
    }

    public void addNeighbour(UserEntity owner, BuildingEntity building) {
        building.getNeighbours().add(owner);
        buildingRepository.save(building);
    }

    public void removeNeighbour(UserEntity owner, BuildingEntity building) {
        if (checkIfUserIsHasOnlyOneApartmentInBuilding(owner.getUsername(), building.getId())) {
            building.getNeighbours().remove(owner);
            buildingRepository.save(building);
        }
    }

    private UserEntity getManagerToSet(BuildingEditDTO buildingEditDTO) {
        return userService
                .findById(buildingEditDTO.getManager().getId());
    }

    public String findManagerUsername(Long buildingId) {
        BuildingEntity building = findEntityById(buildingId);
        return building.getManager().getUsername();
    }

    public void removeApartment(ApartmentEntity apartmentToRemove, BuildingEntity building) {
        building.getApartments().remove(apartmentToRemove);
        buildingRepository.save(building);
    }

    public void addMessage(MessageEntity newMessage, BuildingEntity building) {
        building.getMessages().add(newMessage);
        buildingRepository.save(building);
    }

    private String getImageUrlBySize(int buildingFloors) {
        String imageUrl;
        if (buildingFloors <= 3) {
            imageUrl = ImagesOfBuildings.SMALL_BUILDING.url;
        } else if (buildingFloors <= 8) {
            imageUrl = ImagesOfBuildings.MEDIUM_BUILDING.url;
        } else {
            imageUrl = ImagesOfBuildings.LARGE_BUILDING.url;
        }
        return imageUrl;
    }

    public void removeMessageFromBuilding(MessageEntity messageToRemove, BuildingEntity building) {
        building.getMessages().remove(messageToRemove);
        buildingRepository.save(building);
    }

    public void addNewTaxToBuilding(BuildingEntity buildingToAddTax, TaxEntity taxToAdd) {
        buildingToAddTax.getTaxes().add(taxToAdd);
        buildingRepository.save(buildingToAddTax);
    }

    public void deleteTaxFromBuilding(TaxEntity tax) {
        BuildingEntity building = tax.getBuilding();
        building.getTaxes().remove(tax);
        buildingRepository.save(building);
    }


    public Page<BuildingEntity> findAllBuildingsWithFilterPaginated(Pageable pageable, String searchKeyword) {
        List<BuildingEntity> users = getBuildingsWithOrWithoutFilter(searchKeyword);
        return getPageOfBuildings(pageable, users);
    }

    private List<BuildingEntity> getBuildingsWithOrWithoutFilter(String searchKeyword) {
        List<BuildingEntity> buildings;
        if (searchKeyword == null) {
            buildings = buildingRepository
                    .findAll();
        } else {
            buildings = buildingRepository
                    .findAllByKeyword(searchKeyword);
        }
        return buildings;
    }

    public Boolean checkIfUserIsANeighbour(String username, Long buildingId) {
        return apartmentService.findAllApartmentsByBuildingIdAndOwnerUsername(buildingId, username);
    }

    public Boolean checkIfUserIsHasOnlyOneApartmentInBuilding(String username, Long buildingId) {
        return apartmentService.findIfUserHasOnlyOneApartmentInBuilding(buildingId, username);
    }

    public boolean checkIfBalanceIsEnoughToPayExpense(Long buildingId, Long expenseId) {
        BigDecimal balance = taxService.calculateBuildingBalance(buildingId);
        BigDecimal expenseAmount = expenseService.getExpenseAmountById(expenseId);
        return balance.compareTo(expenseAmount) > 0;
    }

    private static PageImpl<BuildingEntity> getPageOfBuildings(Pageable pageable, List<BuildingEntity> buildings) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<BuildingEntity> list = getBuildingsPaginated(buildings, pageSize, startItem);
        return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), buildings.size());
    }

    private static List<BuildingEntity> getBuildingsPaginated(List<BuildingEntity> buildings, int pageSize, int startItem) {
        List<BuildingEntity> list;
        if (buildings.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, buildings.size());
            list = buildings.subList(startItem, toIndex);
        }
        return list;
    }
}
