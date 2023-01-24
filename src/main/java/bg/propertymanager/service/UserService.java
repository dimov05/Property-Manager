package bg.propertymanager.service;

import bg.propertymanager.model.dto.user.PasswordChangeDTO;
import bg.propertymanager.model.dto.user.UserEditDTO;
import bg.propertymanager.model.dto.user.UserRegisterDTO;
import bg.propertymanager.model.entity.ApartmentEntity;
import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.MessageEntity;
import bg.propertymanager.model.entity.UserEntity;
import bg.propertymanager.model.view.UserEntityViewModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;
import java.util.List;

public interface UserService {

    UserEntity register(UserRegisterDTO userRegisterDTO);

    void init();

    UserEntity findUserByUsername(String username);


    void updateProfile(UserEditDTO userEditDTO);

    void updatePassword(PasswordChangeDTO passwordChangeDTO);

    boolean checkIfOldPasswordIsCorrect(PasswordChangeDTO passwordChangeDTO, Principal principal);

    List<UserEntityViewModel> findAll();

    UserEntity findById(long id);

    void removeApartmentFromUser(ApartmentEntity apartment, UserEntity ownerToRemove);

    void addApartmentToUser(ApartmentEntity apartment, UserEntity ownerToAdd);

    void removeManagerRightsInBuilding(UserEntity currentManager, BuildingEntity building);

    void addManagerRightsInBuilding(UserEntity managerToSet, BuildingEntity buildingToSave);

    void addBuildingToUser(UserEntity ownerToAdd, BuildingEntity building);

    void removeBuildingFromUser(UserEntity ownerToRemove, BuildingEntity building);

    void addMessageToUser(MessageEntity newMessage, UserEntity author);

    void removeMessageFromUser(MessageEntity messageToRemove, UserEntity author);

    Page<UserEntityViewModel> findAllNeighboursByBuildingPaginated(Pageable pageable, Long buildingId);

    Page<UserEntityViewModel> findAllUsersWithFilterPaginated(Pageable pageable, String searchKeyword);

    void changeRole(String roleName, String username);
}
