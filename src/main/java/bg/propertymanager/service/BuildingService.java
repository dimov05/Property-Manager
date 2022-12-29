package bg.propertymanager.service;

import bg.propertymanager.model.dto.BuildingViewDTO;
import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.repository.BuildingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BuildingService {
    private final BuildingRepository buildingRepository;
    private final ModelMapper modelMapper;

    public BuildingService(BuildingRepository buildingRepository, ModelMapper modelMapper) {
        this.buildingRepository = buildingRepository;
        this.modelMapper = modelMapper;
    }

    public List<BuildingViewDTO> findAll() {
        return buildingRepository.findAll()
                .stream()
                .map(building ->
                    modelMapper.map(building, BuildingViewDTO.class))
                .collect(Collectors.toList());
    }
//    public BuildingViewDTO map(BuildingEntity building){
//        return new BuildingViewDTO()
//                .s
//    }

}
