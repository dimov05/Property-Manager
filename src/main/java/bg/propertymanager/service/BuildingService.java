package bg.propertymanager.service;

import bg.propertymanager.model.dto.building.BuildingAddDTO;
import bg.propertymanager.model.dto.building.BuildingChangeTaxesDTO;
import bg.propertymanager.model.dto.building.BuildingEditDTO;
import bg.propertymanager.model.dto.building.BuildingViewDTO;
import bg.propertymanager.model.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface BuildingService {
    void register(BuildingAddDTO buildingAddDTO);

    void updateBuilding(BuildingEditDTO buildingEditDTO);

    void updateBuildingsPerTaxes(BuildingChangeTaxesDTO buildingChangeTaxesDTO);

    List<BuildingViewDTO> findAll();

    BuildingEntity findEntityById(Long id);

    BuildingViewDTO findById(Long id);

    void addNeighbour(UserEntity owner, BuildingEntity building);

    void removeNeighbour(UserEntity owner, BuildingEntity building);

    String findManagerUsername(Long buildingId);

    void removeApartment(ApartmentEntity apartmentToRemove, BuildingEntity building);

    void addMessage(MessageEntity newMessage, BuildingEntity building);

    void removeMessageFromBuilding(MessageEntity messageToRemove, BuildingEntity building);

    void addNewTaxToBuilding(BuildingEntity buildingToAddTax, TaxEntity taxToAdd);

    void deleteTaxFromBuilding(TaxEntity tax);


    Page<BuildingEntity> findAllBuildingsWithFilterPaginated(Pageable pageable, String searchKeyword);

    Boolean checkIfUserIsANeighbour(String username, Long buildingId);

    Boolean checkIfUserIsHasOnlyOneApartmentInBuilding(String username, Long buildingId);

    boolean checkIfBalanceIsEnoughToPayExpense(Long buildingId, Long expenseId);
}
