package bg.propertymanager.service;

import bg.propertymanager.model.dto.building.BuildingViewDTO;
import bg.propertymanager.model.dto.expense.TaxAddDTO;
import bg.propertymanager.model.dto.expense.TaxViewDTO;
import bg.propertymanager.repository.TaxRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaxService {
    private final TaxRepository taxRepository;
    private final ModelMapper modelMapper;

    public TaxService(TaxRepository taxRepository, ModelMapper modelMapper) {
        this.taxRepository = taxRepository;
        this.modelMapper = modelMapper;
    }

    public List<TaxViewDTO> findAllTaxes(BuildingViewDTO building) {
        return taxRepository
                .findAllByBuilding_Id(building.getId())
                .stream()
                .map(tax -> modelMapper.map(tax, TaxViewDTO.class))
                .collect(Collectors.toList());

    }

    public void addTax(TaxAddDTO taxAddDTO, Long buildingId) {
    }
}
