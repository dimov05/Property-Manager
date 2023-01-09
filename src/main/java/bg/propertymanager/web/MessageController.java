package bg.propertymanager.web;

import bg.propertymanager.model.dto.building.BuildingViewDTO;
import bg.propertymanager.model.entity.MessageEntity;
import bg.propertymanager.service.BuildingService;
import bg.propertymanager.service.MessageService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.Set;

@Controller
public class MessageController {
    private final BuildingService buildingService;
    private final MessageService messageService;

    public MessageController(BuildingService buildingService, MessageService messageService) {
        this.buildingService = buildingService;
        this.messageService = messageService;
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/{buildingId}/messages")
    public ModelAndView viewMessagesAsManager(@PathVariable("buildingId") Long buildingId,
                                              Model model) {
        ModelAndView mav = new ModelAndView("view-messages-as-manager");
        BuildingViewDTO building = buildingService.findById(buildingId);
        mav.addObject("building", building);
        Set<MessageEntity> messagesFromNeighbours = messageService.findAllNeighboursMessagesForBuildingSortedFromNewToOld(building);
        Set<MessageEntity> messagesFromManager = messageService.findAllManagersMessagesForBuildingSortedFromNewToOld(building);
        mav.addObject("messagesFromNeighbours",messagesFromNeighbours);
        mav.addObject("messagesFromManager",messagesFromManager);
        return mav;
    }
}
