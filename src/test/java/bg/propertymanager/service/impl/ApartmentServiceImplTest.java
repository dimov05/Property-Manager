package bg.propertymanager.service.impl;

import bg.propertymanager.model.dto.apartment.ApartmentViewDTO;
import bg.propertymanager.model.entity.ApartmentEntity;
import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.TaxEntity;
import bg.propertymanager.model.entity.UserEntity;
import bg.propertymanager.model.enums.TaxStatusEnum;
import bg.propertymanager.model.enums.TaxTypeEnum;
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
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
    private ModelMapper modelMapper;
    @Mock
    private TaxService taxService;

    private static ApartmentEntity apartment;
    private static ApartmentViewDTO apartmentViewDTO;
    private static BuildingEntity building;

    private static TaxEntity tax;

    @BeforeAll
    static void setup() {
        apartment = initApartment();
        apartmentViewDTO = initApartmentViewDTO();
        building = initBuilding();
        tax = initTax();
    }

    private static TaxEntity initTax() {
        return new TaxEntity()
                .setId(1L)
                .setAmount(BigDecimal.valueOf(50))
                .setTaxStatus(TaxStatusEnum.PAID)
                .setTaxType(TaxTypeEnum.PERIODIC);
    }

    private static BuildingEntity initBuilding() {
        return new BuildingEntity()
                .setApartments(new HashSet<>())
                .setId(1L)
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
        ApartmentEntity apartmentToAddTaxTo = apartment;
        TaxEntity taxToAdd = tax;
        this.apartmentService.addNewTaxToApartment(apartmentToAddTaxTo, taxToAdd);

        verify(apartmentRepository, times(1)).save(apartmentToAddTaxTo);

        assert apartmentToAddTaxTo.getTaxes().contains(taxToAdd);
    }

    @Test
    void testDeleteTaxFromApartment_ShouldDeleteTaxFromApartment() {
        ApartmentEntity apartmentToDeleteTaxFrom = apartment;
        TaxEntity taxToDelete = tax;
        taxToDelete.setApartment(apartmentToDeleteTaxFrom);
        apartmentToDeleteTaxFrom.getTaxes().add(taxToDelete);
        this.apartmentService.deleteTaxFromApartment(taxToDelete);

        verify(apartmentRepository, times(1)).save(apartmentToDeleteTaxFrom);

        assert !apartmentToDeleteTaxFrom.getTaxes().contains(taxToDelete);
    }

    @Test
    void testCalculateTotalMonthlyPeriodicTaxesByBuildingId_ShouldReturnTaxes_WhenThereArePresentTaxes() {
        ApartmentEntity apartment1 = apartment;
        apartment1.setId(2L);
        apartment1.getTaxes().add(tax.setAmount(BigDecimal.valueOf(100)));
        BigDecimal expected = BigDecimal.valueOf(250);
        apartment.getTaxes().add(tax.setAmount(BigDecimal.valueOf(150)));
        when(this.apartmentRepository.findAmountOfMonthlyPeriodicTaxes(building.getId()))
                .thenReturn(Optional.of(expected));
        BigDecimal actual = this.apartmentService.calculateTotalMonthlyPeriodicTaxesByBuildingId(building.getId());

        assertEquals(expected, actual);
    }

    @Test
    void testCalculateTotalMonthlyPeriodicTaxesByBuildingId_ShouldReturn0_WhenThereAreNoTaxes() {
        ApartmentEntity apartment1 = apartment;
        BigDecimal expected = BigDecimal.valueOf(0);
        when(this.apartmentRepository.findAmountOfMonthlyPeriodicTaxes(building.getId()))
                .thenReturn(Optional.of(expected));
        BigDecimal actual = this.apartmentService.calculateTotalMonthlyPeriodicTaxesByBuildingId(building.getId());

        assertEquals(expected, actual);
    }

    private static ApartmentEntity initApartment() {
        return new ApartmentEntity()
                .setId(1L)
                .setApartmentNumber("3A")
                .setArea(30)
                .setBuilding(new BuildingEntity().setId(1L))
                .setFloor(3)
                .setDogsCount(3)
                .setElevatorChipsCount(3)
                .setOwner(new UserEntity().setId(1L))
                .setPeriodicTax(new BigDecimal(50))
                .setRoommateCount(3)
                .setTaxes(new HashSet<>());
    }
}