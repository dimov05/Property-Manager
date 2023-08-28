package bg.propertymanager.service.impl;

import bg.propertymanager.model.dto.apartment.ApartmentViewDTO;
import bg.propertymanager.model.entity.*;
import bg.propertymanager.model.enums.TaxStatusEnum;
import bg.propertymanager.model.enums.TaxTypeEnum;
import bg.propertymanager.model.enums.UserRolesEnum;
import bg.propertymanager.repository.ApartmentRepository;
import bg.propertymanager.service.BuildingService;
import bg.propertymanager.service.TaxService;
import bg.propertymanager.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static bg.propertymanager.util.TestDataUtils.INDEX_ONE;
import static bg.propertymanager.util.TestDataUtils.INDEX_TWO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApartmentServiceImplTest {

    @InjectMocks
    ApartmentServiceImpl apartmentService;
    @Mock
    private ApartmentRepository apartmentRepository;
    @Mock
    private UserService userService;
    @Mock
    private BuildingService buildingService;
    @Mock
    private TaxService taxService;
    private static ApartmentEntity apartment;
    private static ApartmentViewDTO apartmentViewDTO;
    private static BuildingEntity building;
    private static List<RoleEntity> roles;
    private static UserEntity user;

    private static TaxEntity tax;

    @BeforeAll
    static void setup() {
        roles = new ArrayList<>();
        user = new UserEntity();
        initRoles();
        initUser();
        apartment = initApartment();
        apartmentViewDTO = initApartmentViewDTO();
        building = initBuilding();
        tax = initTax();
    }

    @Test
    @Disabled
    void testFindViewById() {
        ApartmentViewDTO expected = apartmentViewDTO;

        when(this.apartmentRepository.findById(expected.getId())).thenReturn(Optional.of(apartment));
        ApartmentViewDTO actual = this.apartmentService.findViewById(apartment.getId());

        assertAll(
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getFloor(), actual.getFloor()),
                () -> assertEquals(expected.getApartmentNumber(), actual.getApartmentNumber()),
                () -> assertEquals(expected.getArea(), actual.getArea()),
                () -> assertEquals(expected.getBuilding().getId(), actual.getBuilding().getId()),
                () -> assertEquals(expected.getDogsCount(), actual.getDogsCount()),
                () -> assertEquals(expected.getElevatorChipsCount(), actual.getElevatorChipsCount()),
                () -> assertEquals(expected.getOwner().getId(), actual.getOwner().getId()),
                () -> assertEquals(expected.getRoommateCount(), actual.getRoommateCount()),
                () -> assertEquals(expected.getPeriodicTax(), actual.getPeriodicTax())
        );
    }

    @ParameterizedTest
    @CsvSource(value = {"-20", "-10", "-4", "-2", "-1"})
    void testFindViewById_ShouldThrowException_OnIncorrectId(long id) {
        assertThrows(NullPointerException.class, () -> this.apartmentService.findViewById(id));
    }

    @Test
    void testUpdatePeriodicTax() {
        Set<ApartmentEntity> expectedApartments = new HashSet<>();
        expectedApartments.add(apartment);
        when(this.apartmentRepository.findAllByBuildingId(building.getId())).thenReturn(expectedApartments);

        this.apartmentService.updatePeriodicTax(building);

        verify(this.apartmentRepository, times(1)).findAllByBuildingId(building.getId());
        verify(this.apartmentRepository, times(expectedApartments.size())).save(any(ApartmentEntity.class));
    }

    @ParameterizedTest
    @CsvSource(value = {"1A,1", "2B,2", "12C,22"})
    void testGetApartmentByApartmentNumberAndBuildingId(String apartmentNumber, long buildingId) {
        ApartmentEntity expected = apartment;
        expected.setApartmentNumber(apartmentNumber);

        when(this.apartmentRepository.findByApartmentNumberAndBuilding_Id(apartmentNumber, buildingId))
                .thenReturn(Optional.of(expected));

        ApartmentEntity actual = this.apartmentService.getApartmentByApartmentNumberAndBuildingId(apartmentNumber, buildingId);

        assertAll(
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getFloor(), actual.getFloor()),
                () -> assertEquals(expected.getApartmentNumber(), actual.getApartmentNumber()),
                () -> assertEquals(expected.getArea(), actual.getArea()),
                () -> assertEquals(expected.getBuilding().getId(), actual.getBuilding().getId()),
                () -> assertEquals(expected.getDogsCount(), actual.getDogsCount()),
                () -> assertEquals(expected.getElevatorChipsCount(), actual.getElevatorChipsCount()),
                () -> assertEquals(expected.getOwner().getId(), actual.getOwner().getId()),
                () -> assertEquals(expected.getRoommateCount(), actual.getRoommateCount()),
                () -> assertEquals(expected.getPeriodicTax(), actual.getPeriodicTax())
        );
    }

    @ParameterizedTest
    @CsvSource(value = {"0A", "C-10", "-as4", "-2B", "sds-1"})
    void testGetApartmentByApartmentNumberAndBuildingId_ShouldThrowException_OnIncorrectApartmentNumber(String apartmentNumber) {
        assertThrows(NullPointerException.class, () -> this.apartmentService.getApartmentByApartmentNumberAndBuildingId(apartmentNumber, building.getId()));
    }

    @Test
    void testAddNewTaxToApartment_ShouldAddNewTaxToApartment() {
        this.apartmentService.addNewTaxToApartment(apartment, tax);

        verify(apartmentRepository, times(1)).save(apartment);

        assertTrue(apartment.getTaxes().contains(tax));
    }

    @Test
    void testDeleteTaxFromApartment_ShouldDeleteTaxFromApartment() {
        tax.setApartment(apartment);
        apartment.getTaxes().add(tax);
        this.apartmentService.deleteTaxFromApartment(tax);

        verify(apartmentRepository, times(1)).save(apartment);

        assertFalse(apartment.getTaxes().contains(tax));
    }

    @Test
    void testCalculateTotalMonthlyPeriodicTaxesByBuildingId_ShouldReturnTaxes_WhenThereArePresentTaxes() {
        BigDecimal expected = BigDecimal.valueOf(250);
        when(this.apartmentRepository.findAmountOfMonthlyPeriodicTaxes(building.getId()))
                .thenReturn(Optional.of(expected));
        BigDecimal actual = this.apartmentService.calculateTotalMonthlyPeriodicTaxesByBuildingId(building.getId());

        assertEquals(expected, actual);
    }

    @Test
    void testCalculateTotalMonthlyPeriodicTaxesByBuildingId_ShouldReturn0_WhenThereAreNoTaxes() {
        BigDecimal expected = BigDecimal.valueOf(0);
        when(this.apartmentRepository.findAmountOfMonthlyPeriodicTaxes(building.getId()))
                .thenReturn(Optional.of(expected));
        BigDecimal actual = this.apartmentService.calculateTotalMonthlyPeriodicTaxesByBuildingId(building.getId());

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource(value = {"1", "3", "6", "9", "15"})
    void testFindTotalCountOfNeighboursInBuilding_ShouldReturnCorrectCount_OnMoreThan0Neighbours(int countOfNeighbours) {
        for (int i = 1; i <= countOfNeighbours; i++) {
            UserEntity neighbour = new UserEntity()
                    .setId((long) i).setUsername("User-" + i);
            building.getNeighbours().add(neighbour);
        }
        int expected = building.getNeighbours().size();

        when(this.apartmentRepository.findTotalCountOfNeighboursByBuildingId(building.getId()))
                .thenReturn(Optional.of(building.getNeighbours().size()));
        int actual = this.apartmentService.findTotalCountOfNeighboursInBuilding(building.getId());

        assertEquals(expected, actual);
    }

    @Test
    void testFindTotalCountOfNeighboursInBuilding_Should0_OnZeroNeighboursInBuilding() {
        int expected = building.getNeighbours().size();

        when(this.apartmentRepository.findTotalCountOfNeighboursByBuildingId(building.getId()))
                .thenReturn(Optional.of(building.getNeighbours().size()));
        int actual = this.apartmentService.findTotalCountOfNeighboursInBuilding(building.getId());

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource(value = {"1", "3", "6", "9", "15"})
    void testFindTotalCountOfDogsInBuilding_ShouldReturnCorrectCount_OnMoreThan0Dogs(int countOfDogs) {
        BuildingEntity buildingEntity = new BuildingEntity()
                .setId(INDEX_ONE).setApartments(new HashSet<>());

        ApartmentEntity apartment = new ApartmentEntity()
                .setId(INDEX_ONE).setDogsCount(countOfDogs);
        buildingEntity.getApartments().add(apartment);

        int expected = buildingEntity.getApartments()
                .stream().mapToInt(ApartmentEntity::getDogsCount).sum();

        when(this.apartmentRepository.findTotalCountOfDogsByBuildingId(building.getId()))
                .thenReturn(Optional.of(countOfDogs));
        int actual = this.apartmentService.findTotalCountOfDogsByBuildingId(building.getId());

        assertEquals(expected, actual);
    }

    @Test
    void testFindTotalCountOfDogsInBuilding_Should0_OnZeroDogsInBuilding() {
        BuildingEntity buildingEntity = new BuildingEntity()
                .setId(INDEX_ONE).setApartments(new HashSet<>());
        int expected = buildingEntity.getApartments().stream()
                .map(ApartmentEntity::getDogsCount).toList()
                .stream().mapToInt(Integer::intValue).sum();


        when(this.apartmentRepository.findTotalCountOfDogsByBuildingId(building.getId()))
                .thenReturn(Optional.of(expected));
        int actual = this.apartmentService.findTotalCountOfDogsByBuildingId(building.getId());

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource(value = {"1", "3", "6", "9", "15"})
    void testFindTotalCountOfElevatorChipsInBuilding_ShouldReturnCorrectCount_OnMoreThan0ElevatorChips(int countOfElevatorChips) {
        BuildingEntity buildingEntity = new BuildingEntity()
                .setId(INDEX_ONE).setApartments(new HashSet<>());

        ApartmentEntity apartment = new ApartmentEntity()
                .setId(INDEX_ONE).setElevatorChipsCount(countOfElevatorChips);
        buildingEntity.getApartments().add(apartment);

        int expected = buildingEntity.getApartments()
                .stream().mapToInt(ApartmentEntity::getElevatorChipsCount).sum();

        when(this.apartmentRepository.findTotalCountOfElevatorChipsByBuildingId(building.getId()))
                .thenReturn(Optional.of(countOfElevatorChips));
        int actual = this.apartmentService.findTotalCountOfElevatorChipsByBuildingId(building.getId());

        assertEquals(expected, actual);
    }

    @Test
    void testFindTotalCountOfElevatorChipsInBuilding_Should0_OnZeroElevatorChipsInBuilding() {
        BuildingEntity buildingEntity = new BuildingEntity()
                .setId(INDEX_ONE).setApartments(new HashSet<>());
        int expected = buildingEntity.getApartments().stream()
                .map(ApartmentEntity::getElevatorChipsCount).toList()
                .stream().mapToInt(Integer::intValue).sum();


        when(this.apartmentRepository.findTotalCountOfElevatorChipsByBuildingId(building.getId()))
                .thenReturn(Optional.of(expected));
        int actual = this.apartmentService.findTotalCountOfElevatorChipsByBuildingId(building.getId());

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource(value = {"0", "4", "7", "10", "21"})
    void testFindAllApartmentsByBuildingId_ShouldReturnCorrectCountOfApartmentsInBuilding(int countOfApartments) {
        BuildingEntity buildingEntity = new BuildingEntity().setId(INDEX_ONE)
                .setApartments(new HashSet<>());
        for (int i = 0; i < countOfApartments; i++) {
            ApartmentEntity apartment = new ApartmentEntity()
                    .setId((long) (i + 1)).setApartmentNumber(i + "A").setBuilding(buildingEntity);
            buildingEntity.getApartments().add(apartment);
        }
        List<ApartmentEntity> expected = buildingEntity
                .getApartments()
                .stream()
                .sorted(Comparator.comparing(ApartmentEntity::getId))
                .toList();

        when(this.apartmentRepository.findAllByBuilding_IdOrderById(buildingEntity.getId()))
                .thenReturn(expected);
        List<ApartmentEntity> actual = this.apartmentService.findAllApartmentsByBuildingId(buildingEntity.getId());

        assertEquals(expected.size(), actual.size());
        assertIterableEquals(expected, actual);
    }

    @Test
    void testFindIfUserHasOnlyOneApartmentInBuilding_ShouldReturnTrue_WhenOwnerHasOnlyOneApartmentInTheBuilding() {
        UserEntity owner = new UserEntity()
                .setId(INDEX_ONE)
                .setUsername("owner");
        user.setUsername("notOwner");
        ApartmentEntity apartment1 = new ApartmentEntity()
                .setId(INDEX_ONE)
                .setBuilding(building)
                .setOwner(owner);
        List<ApartmentEntity> expectedApartments = List.of(apartment1); // count of User's apartments in this building
        when(this.apartmentRepository.findAllByBuilding_IdAndOwner_Username(building.getId(), owner.getUsername()))
                .thenReturn(expectedApartments);

        boolean actual = this.apartmentService.findIfUserHasOnlyOneApartmentInBuilding(building.getId(), owner.getUsername());

        assertTrue(actual);
    }

    @Test
    void testFindIfUserHasOnlyOneApartmentInBuilding_ShouldReturnFalse_WhenOwnerHasMoreThanOneApartmentInTheBuilding() {
        UserEntity owner = new UserEntity()
                .setId(INDEX_ONE)
                .setUsername("owner");
        ApartmentEntity apartment1 = new ApartmentEntity()
                .setId(INDEX_ONE)
                .setBuilding(building)
                .setOwner(owner);
        ApartmentEntity apartment2 = new ApartmentEntity()
                .setId(INDEX_TWO)
                .setBuilding(building)
                .setOwner(owner);
        List<ApartmentEntity> expectedApartments = List.of(apartment1, apartment2); // count of User's apartments in this building
        when(this.apartmentRepository.findAllByBuilding_IdAndOwner_Username(building.getId(), owner.getUsername()))
                .thenReturn(expectedApartments);

        boolean actual = this.apartmentService.findIfUserHasOnlyOneApartmentInBuilding(building.getId(), owner.getUsername());

        assertFalse(actual);
    }

    @Test
    void testFindIfUserHasOnlyOneApartmentInBuilding_ShouldReturnFalse_WhenOwnerHas0ApartmentsInTheBuilding() {
        UserEntity notOwner = new UserEntity()
                .setId(INDEX_ONE)
                .setUsername("owner");
        List<ApartmentEntity> expectedApartments = Collections.emptyList(); // count of User's apartments in this building
        when(this.apartmentRepository.findAllByBuilding_IdAndOwner_Username(building.getId(), notOwner.getUsername()))
                .thenReturn(expectedApartments);

        boolean actual = this.apartmentService.findIfUserHasOnlyOneApartmentInBuilding(building.getId(), notOwner.getUsername());

        assertFalse(actual);
    }

    @ParameterizedTest
    @CsvSource(value = {"0", "3", "10", "11", "22"})
    void testFindAllApartmentsWithPositivePeriodicTax(int countOfApartmentsWithPositivePeriodicTax) {
        Random random = new Random();
        List<ApartmentEntity> expected = new ArrayList<>();
        for (int i = 0; i < countOfApartmentsWithPositivePeriodicTax; i++) {
            int randomPositiveTax = random.nextInt(1, 100);
            BigDecimal positivePeriodicTax = new BigDecimal(randomPositiveTax);
            ApartmentEntity apartment = new ApartmentEntity()
                    .setId((long) (i + 1))
                    .setPeriodicTax(positivePeriodicTax);
            expected.add(apartment);
        }
        when(this.apartmentRepository.findAllByPeriodicTaxGreaterThan(BigDecimal.ZERO))
                .thenReturn(expected);
        List<ApartmentEntity> actual = this.apartmentService.findAllApartmentsWithPositivePeriodicTax();

        assertEquals(expected.size(), actual.size());
        assertIterableEquals(expected, actual);

    }

    @ParameterizedTest
    @CsvSource(value = {"1A,true", "2B,true", "3N,false", "7K,false"})
    void testCheckIfApartmentNumberToAddExistInBuilding(String apartmentNumber, boolean expected) {
        ApartmentEntity apartmentEntity = null;
        if (expected) {
            apartmentEntity = new ApartmentEntity()
                    .setApartmentNumber(apartmentNumber);
        }

        building.getApartments().add(new ApartmentEntity().setApartmentNumber("1A"));
        building.getApartments().add(new ApartmentEntity().setApartmentNumber("2B"));
        building.getApartments().add(new ApartmentEntity().setApartmentNumber("3C"));
        when(this.apartmentRepository.findByApartmentNumberAndBuilding_Id(apartmentNumber, building.getId())).thenReturn(Optional.ofNullable(apartmentEntity));

        boolean actual = this.apartmentService.checkIfApartmentNumberToAddExistInTheBuilding(building.getId(), apartmentNumber);

        assertEquals(expected, actual);
    }

    private static ApartmentEntity initApartment() {
        return new ApartmentEntity()
                .setId(INDEX_ONE)
                .setApartmentNumber("3A")
                .setArea(30)
                .setBuilding(new BuildingEntity().setId(INDEX_ONE))
                .setFloor(3)
                .setDogsCount(3)
                .setElevatorChipsCount(3)
                .setOwner(new UserEntity().setId(INDEX_ONE))
                .setPeriodicTax(new BigDecimal(50))
                .setRoommateCount(3)
                .setTaxes(new HashSet<>());
    }

    private static void initUser() {
        user
                .setUsername("User")
                .setRoles(List.of(roles.get(2)))
                .setPassword("password")
                .setEmail("user@mail.com")
                .setPhoneNumber("0888999999")
                .setFullName("User Userov")
                .setCountry("Bulgaria")
                .setCity("Plovdiv")
                .setStreet("Street Sl")
                .setRegistrationDate(LocalDate.of(2023, Month.AUGUST, 10))
                .setManagerInBuildings(Collections.emptySet())
                .setOwnerInBuildings(Collections.emptySet())
                .setApartments(Collections.emptySet())
                .setMessages(Collections.emptySet());
    }

    private static void initRoles() {
        roles.add(new RoleEntity()
                .setId(INDEX_ONE)
                .setName("USER")
                .setRole(UserRolesEnum.USER));
        roles.add(new RoleEntity()
                .setId(INDEX_TWO)
                .setName("MANAGER")
                .setRole(UserRolesEnum.MANAGER));
        roles.add(new RoleEntity()
                .setId(3L)
                .setName("ADMIN")
                .setRole(UserRolesEnum.ADMIN));
    }

    private static TaxEntity initTax() {
        return new TaxEntity()
                .setId(INDEX_ONE)
                .setAmount(BigDecimal.valueOf(50))
                .setTaxStatus(TaxStatusEnum.PAID)
                .setTaxType(TaxTypeEnum.PERIODIC);
    }

    private static BuildingEntity initBuilding() {
        return new BuildingEntity()
                .setApartments(new HashSet<>())
                .setId(INDEX_ONE)
                .setNeighbours(new HashSet<>())
                .setTaxPerPerson(BigDecimal.valueOf(3))
                .setTaxPerDog(BigDecimal.valueOf(2))
                .setTaxPerElevatorChip(BigDecimal.valueOf(4));
    }

    private static ApartmentViewDTO initApartmentViewDTO() {
        return new ApartmentViewDTO()
                .setId(apartment.getId())
                .setFloor(apartment.getFloor())
                .setApartmentNumber(apartment.getApartmentNumber())
                .setArea(apartment.getArea())
                .setBuilding(apartment.getBuilding())
                .setDogsCount(apartment.getDogsCount())
                .setElevatorChipsCount(apartment.getElevatorChipsCount())
                .setOwner(apartment.getOwner())
                .setRoommateCount(apartment.getRoommateCount())
                .setPeriodicTax(apartment.getPeriodicTax());
    }
}