package bg.propertymanager.service.impl;

import bg.propertymanager.model.dto.expense.ExpenseAddDTO;
import bg.propertymanager.model.dto.expense.ExpenseEditDTO;
import bg.propertymanager.model.dto.expense.ExpenseViewDTO;
import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.ExpenseEntity;
import bg.propertymanager.model.entity.RoleEntity;
import bg.propertymanager.model.entity.UserEntity;
import bg.propertymanager.model.enums.TaxStatusEnum;
import bg.propertymanager.model.enums.TaxTypeEnum;
import bg.propertymanager.model.enums.UserRolesEnum;
import bg.propertymanager.repository.BuildingRepository;
import bg.propertymanager.repository.ExpenseRepository;
import bg.propertymanager.service.BuildingService;
import bg.propertymanager.service.TaxService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

import static bg.propertymanager.util.TestDataUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ExpenseServiceImplTest {
    @InjectMocks
    @Spy
    ExpenseServiceImpl expenseService;
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private BuildingService buildingService;
    @Mock
    private BuildingRepository buildingRepository;
    @Mock
    private TaxService taxService;

    private static final BigDecimal AMOUNT_OF_200 = BigDecimal.valueOf(200);
    private static ExpenseEntity expenseEntity;
    private static BuildingEntity building;
    private static UserEntity manager;
    private static List<RoleEntity> roles;

    private static ExpenseEditDTO expenseEditDTO;
    private static ExpenseAddDTO expenseAddDTO;
    private static Set<ExpenseEntity> expenses;

    @BeforeAll
    static void setup() {
        expenseAddDTO = initExpenseAddDTO();
        expenses = initExpenses();
        roles = initRoles();
        manager = initManager();
        building = initBuilding();
        manager.getManagerInBuildings().add(building);
        expenseEntity = mapExpenseAddDTOtoExpenseEntity();
        expenseEditDTO = initExpenseEditDTO();
        expenseAddDTO = initExpenseAddDTO();
    }

    @Test
    @Disabled
    void testAddExpenseToBuilding_ShouldAddExpenseToBuilding_OnCorrectData() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        BuildingEntity expected = building;
        expected.setManager(manager);
        ExpenseEntity expenseToAdd = mapExpenseAddDTOtoExpenseEntity();
        when(this.buildingRepository.findById(building.getId())).thenReturn(Optional.of(expected));
        this.expenseService.addExpenseToBuilding(expenseAddDTO, building.getId());
        verify(this.expenseRepository, times(1)).save(expenseToAdd);
        verify(this.buildingRepository, times(1)).findById(expected.getId());
    }

    @ParameterizedTest
    @CsvSource(value = {"-1", "-4", "-22"})
    void testAddExpenseToBuilding_ShouldThrowException_OnNoPresentExpenseWithThisId(long id) {
        assertThrows(NullPointerException.class, () -> this.expenseService.addExpenseToBuilding(expenseAddDTO, id));
    }

    @Test
    @Disabled
    void testFindViewById_ShouldReturnExpenseViewDto_OnCorrectId() {
        ExpenseViewDTO expected = mapExpenseEntityToExpenseViewDTO();
        when(this.expenseRepository.findById(expected.getId())).thenReturn(Optional.ofNullable(expenseEntity));
        ExpenseViewDTO actual = this.expenseService.findViewById(expenseEntity.getId());

        assertAll(
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getDueDate(), actual.getDueDate()),
                () -> assertEquals(expected.getManager(), actual.getManager()),
                () -> assertEquals(expected.getDescription(), actual.getDescription()),
                () -> assertEquals(expected.getAmount(), actual.getAmount()),
                () -> assertEquals(expected.getBuilding(), actual.getBuilding()),
                () -> assertEquals(expected.getStartDate(), actual.getStartDate()),
                () -> assertEquals(expected.getTaxes().size(), actual.getTaxes().size()),
                () -> assertEquals(expected.getDueDate(), actual.getDueDate()),
                () -> assertEquals(expected.getTaxStatus(), actual.getTaxStatus()),
                () -> assertEquals(expected.getTaxType(), actual.getTaxType())
        );
    }

    @ParameterizedTest
    @CsvSource(value = {"-20", "-10", "-4", "-2", "-1"})
    void testFindViewById_ShouldThrowException_OnIncorrectId(long id) {
        assertThrows(NullPointerException.class, () -> this.expenseService.findViewById(id));
    }

    @Test
    void testUpdateExpenseStatus_ShouldUpdateExpenseStatus() {
        ExpenseEntity expected = expenseEntity;
        expenseEditDTO.setTaxStatus(TaxStatusEnum.PAID);

        when(this.expenseRepository.findById(expected.getId())).thenReturn(Optional.of(expected));

        this.expenseService.updateExpenseStatus(expenseEditDTO);

        verify(expenseRepository, times(1)).findById(expected.getId());
        verify(expenseRepository, times(1)).save(any(ExpenseEntity.class));

        assertEquals(TaxStatusEnum.PAID, expected.getTaxStatus());
    }

    @Test
    void testUpdateExpenseStatus_ShouldThrowNullPointerException_OnNotPresentExpenseWithThisId() {
        assertThrows(NullPointerException.class, () -> this.expenseService.updateExpenseStatus(expenseEditDTO));
    }

    @Test
    void testFindAmountOfAllPaidExpensesByBuildingId_ShouldReturnCorrectAmount_OnExpensesMoreThan1() {
        building.setExpenses(expenses); // total amount 600
        BigDecimal expected = new BigDecimal(600);
        when(this.expenseRepository.findAmountOfPaidExpensesByBuildingId(building.getId()))
                .thenReturn(Optional.of(expected));
        BigDecimal actual = this.expenseService.findAmountOfAllPaidExpensesByBuildingId(building.getId());

        assertEquals(expected, actual);
    }

    @Test
    void testFindAmountOfAllPaidExpensesByBuildingId_ShouldReturn0_OnNoPresentExpensesForBuilding() {
        when(this.expenseRepository.findAmountOfPaidExpensesByBuildingId(building.getId())).thenReturn(Optional.of(BigDecimal.ZERO));
        BigDecimal actual = this.expenseService.findAmountOfAllPaidExpensesByBuildingId(building.getId());
        assertEquals(BigDecimal.ZERO, actual);
    }

    @Test
    void testFindAmountOfAllUnpaidExpensesByBuildingId_ShouldReturnCorrectAmount_OnExpensesMoreThan1() {
        for (ExpenseEntity expense : expenses) {
            expense.setTaxStatus(TaxStatusEnum.UNPAID);
        }
        building.setExpenses(expenses); // total amount 600
        BigDecimal expected = new BigDecimal(600);
        when(this.expenseRepository.findAmountOfUnpaidExpensesByBuildingId(building.getId()))
                .thenReturn(Optional.of(expected));
        BigDecimal actual = this.expenseService.findAmountOfAllUnpaidExpensesByBuildingId(building.getId());

        assertEquals(expected, actual);
    }

    @Test
    void testFindAmountOfAllUnpaidExpensesByBuildingId_ShouldReturn0_OnNoPresentExpensesForBuilding() {
        when(this.expenseRepository.findAmountOfUnpaidExpensesByBuildingId(building.getId())).thenReturn(Optional.of(BigDecimal.ZERO));
        BigDecimal actual = this.expenseService.findAmountOfAllUnpaidExpensesByBuildingId(building.getId());
        assertEquals(BigDecimal.ZERO, actual);
    }

    @ParameterizedTest
    @CsvSource(value = {"1", "2", "3", "6"})
    void testFindById_ShouldReturnExpenseWithCorrectId(long id) {
        ExpenseEntity expected = initExpenseWithId(id);

        when(this.expenseRepository.findById(id)).thenReturn(Optional.ofNullable(expected));

        ExpenseEntity actual = this.expenseService.findById(id);

        assertAll(
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getAmount(), actual.getAmount()),
                () -> assertEquals(expected.getDueDate(), actual.getDueDate()),
                () -> assertEquals(expected.getTaxStatus(), actual.getTaxStatus()),
                () -> assertEquals(expected.getDescription(), actual.getDescription()),
                () -> assertEquals(expected.getBuilding(), actual.getBuilding()),
                () -> assertEquals(expected.getManager(), actual.getManager()),
                () -> assertEquals(expected.getStartDate(), actual.getStartDate()),
                () -> assertEquals(expected.getTaxes(), actual.getTaxes()),
                () -> assertEquals(expected.getTaxType(), actual.getTaxType())
        );
    }

    @ParameterizedTest
    @CsvSource(value = {"-1", "-2", "-3", "-12312"})
    void testFindById_ShouldThrowException_WhenIncorrectExpenseIdIsPassed(Long id) {
        assertThrows(NullPointerException.class, () -> this.expenseService.findById(id));
    }

    @ParameterizedTest
    @CsvSource(value = {"1,200", "2,400", "3,600", "6,800"})
    void testGetExpenseAmountById_ShouldReturnExpenseAmount_ForCorrectId(long id, BigDecimal amount) {
        ExpenseEntity expenseEntity = initExpenseWithId(id);
        expenseEntity.setAmount(amount);
        BigDecimal expected = expenseEntity.getAmount();

        when(this.expenseRepository.findById(id)).thenReturn(Optional.of(expenseEntity));
        BigDecimal actual = this.expenseService.getExpenseAmountById(id);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource(value = {"-1", "-2", "-3", "-12312"})
    void testGetExpenseAmountById_ShouldThrowException_WhenIncorrectExpenseIdIsPassed(Long id) {
        assertThrows(NullPointerException.class, () -> this.expenseService.getExpenseAmountById(id));
    }


    private static ExpenseEntity mapExpenseAddDTOtoExpenseEntity() {
        return new ExpenseEntity()
                .setId(expenseAddDTO.getId())
                .setTaxType(expenseAddDTO.getTaxType())
                .setTaxStatus(TaxStatusEnum.UNPAID)
                .setDescription(expenseAddDTO.getDescription())
                .setStartDate(expenseAddDTO.getStartDate())
                .setDueDate(expenseAddDTO.getDueDate())
                .setAmount(expenseAddDTO.getAmount())
                .setManager(building.getManager())
                .setBuilding(building)
                .setTaxes(Collections.emptySet());
    }
    //    }

    private static ExpenseAddDTO initExpenseAddDTO() {
        return new ExpenseAddDTO()
                .setId(INDEX_ONE)
                .setAmount(AMOUNT_OF_200)
                .setDescription("Expense No:1")
                .setTaxType(TaxTypeEnum.EMERGENCY)
                .setStartDate(LocalDateTime.of(2023, Month.AUGUST, 5, 12, 20))
                .setDueDate(LocalDateTime.of(2023, Month.SEPTEMBER, 5, 12, 20))
                .setSelectedApartments(new ArrayList<>());
    }

    private static List<RoleEntity> initRoles() {
        List<RoleEntity> roles = new ArrayList<>();
        roles.add(new RoleEntity()
                .setId(INDEX_ONE)
                .setName("USER")
                .setRole(UserRolesEnum.USER));
        roles.add(new RoleEntity()
                .setId(INDEX_ONE)
                .setName("MANAGER")
                .setRole(UserRolesEnum.MANAGER));
        roles.add(new RoleEntity()
                .setId(INDEX_ONE)
                .setName("ADMIN")
                .setRole(UserRolesEnum.ADMIN));
        return roles;
    }

    private static BuildingEntity initBuilding() {
        return new BuildingEntity()
                .setId(INDEX_ONE)
                .setApartments(new HashSet<>())
                .setExpenses(new HashSet<>())
                .setCity("Plovdiv")
                .setCountry("Bulgaria")
                .setStreet("Slavyanska 95")
                .setElevators(2)
                .setFloors(7)
                .setImageUrl("building#1")
                .setManager(manager)
                .setMessages(new HashSet<>())
                .setName("Building #1")
                .setNeighbours(new HashSet<>())
                .setRegistrationDate(LocalDate.of(2023, Month.AUGUST, 1))
                .setTaxes(new HashSet<>())
                .setTaxPerDog(new BigDecimal(2))
                .setTaxPerPerson(new BigDecimal(4))
                .setTaxPerElevatorChip(new BigDecimal(3));
    }

    private static ExpenseEditDTO initExpenseEditDTO() {
        return new ExpenseEditDTO()
                .setId(expenseEntity.getId())
                .setTaxStatus(expenseEntity.getTaxStatus());
    }

    private static UserEntity initManager() {
        return new UserEntity()
                .setId(INDEX_ONE)
                .setUsername("manager")
                .setCity("Plovdiv")
                .setCountry("Bulgaria")
                .setEmail("manager@mail.com")
                .setFullName("Manager Managerov")
                .setOwnerInBuildings(new HashSet<>())
                .setApartments(new HashSet<>())
                .setManagerInBuildings(new HashSet<>())
                .setMessages(new HashSet<>())
                .setPassword("password")
                .setStreet("Slavyanska 95")
                .setRoles(List.of(roles.get(1)))
                .setRegistrationDate(LocalDate.of(2023, Month.MAY, 3));
    }

    private static ExpenseViewDTO mapExpenseEntityToExpenseViewDTO() {
        return new ExpenseViewDTO()
                .setId(expenseEntity.getId())
                .setDueDate(expenseEntity.getDueDate())
                .setManager(expenseEntity.getManager())
                .setDescription(expenseEntity.getDescription())
                .setAmount(expenseEntity.getAmount())
                .setBuilding(expenseEntity.getBuilding())
                .setStartDate(expenseEntity.getStartDate())
                .setTaxes(expenseEntity.getTaxes())
                .setDueDate(expenseEntity.getDueDate())
                .setTaxStatus(expenseEntity.getTaxStatus())
                .setTaxType(expenseEntity.getTaxType());
    }

    private static Set<ExpenseEntity> initExpenses() {
        Set<ExpenseEntity> expenseEntities = new HashSet<>();
        ExpenseEntity expense1 = new ExpenseEntity().setId(INDEX_ONE)
                .setTaxStatus(TaxStatusEnum.PAID); // amount 200
        ExpenseEntity expense2 = new ExpenseEntity()
                .setId(INDEX_TWO)
                .setAmount(AMOUNT_OF_200)
                .setTaxStatus(TaxStatusEnum.PAID); // amount 100
        ExpenseEntity expense3 = new ExpenseEntity()
                .setId(INDEX_THREE)
                .setAmount(AMOUNT_OF_200)
                .setTaxStatus(TaxStatusEnum.PAID); // amount 300
        expenseEntities.add(expense1);
        expenseEntities.add(expense2);
        expenseEntities.add(expense3);
        return expenseEntities;
    }

    private ExpenseEntity initExpenseWithId(long id) {
        return expenseEntity.setId(id);
    }
}