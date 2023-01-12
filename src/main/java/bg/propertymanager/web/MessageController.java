package bg.propertymanager.web;

import bg.propertymanager.model.dto.building.BuildingViewDTO;
import bg.propertymanager.model.dto.message.MessageAddDTO;
import bg.propertymanager.model.dto.message.MessageEditDTO;
import bg.propertymanager.model.entity.MessageEntity;
import bg.propertymanager.service.BuildingService;
import bg.propertymanager.service.MessageService;
import bg.propertymanager.service.TaxService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Set;

@Controller
public class MessageController {
    private final BuildingService buildingService;
    private final MessageService messageService;
    private final TaxService taxService;

    public MessageController(BuildingService buildingService, MessageService messageService, TaxService taxService) {
        this.buildingService = buildingService;
        this.messageService = messageService;
        this.taxService = taxService;
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/{buildingId}/messages")
    public ModelAndView viewMessagesAsManager(@PathVariable("buildingId") Long buildingId,
                                              Model model) {
        ModelAndView mav = new ModelAndView("view-messages-as-manager");
        BuildingViewDTO building = buildingService.findById(buildingId);
        mav.addObject("building", building);
        mav.addObject("buildingBalance", taxService.calculateBuildingBalance(buildingId));
        Set<MessageEntity> messagesFromNeighbours = messageService.findAllNeighboursMessagesForBuildingSortedFromNewToOld(building);
        Set<MessageEntity> messagesFromManager = messageService.findAllManagersMessagesForBuildingSortedFromNewToOld(building);
        mav.addObject("messagesFromNeighbours", messagesFromNeighbours);
        mav.addObject("messagesFromManager", messagesFromManager);
        return mav;
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/{buildingId}/my-messages/{principalName}")
    public ModelAndView viewAllMyMessagesAsManager(@PathVariable("buildingId") Long buildingId,
                                                   @PathVariable("principalName") String principalUsername,
                                                   Model model) {
        ModelAndView mav = new ModelAndView("view-my-messages-as-manager");
        BuildingViewDTO building = buildingService.findById(buildingId);
        Set<MessageEntity> myMessages = messageService.findAllManagersMessagesForBuildingSortedFromNewToOld(building);
        mav.addObject("messagesFromManager", myMessages);
        mav.addObject("building", building);
        mav.addObject("buildingBalance", taxService.calculateBuildingBalance(buildingId));

        return mav;
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/{buildingId}/add-message")
    public String addMessageAsManager(@PathVariable("buildingId") Long buildingId, Model model) {
        if (!model.containsAttribute("messageAddDTO")) {
            model.addAttribute("messageAddDTO", new MessageAddDTO());
        }
        model.addAttribute("building", buildingService.findById(buildingId));
        model.addAttribute("buildingBalance", taxService.calculateBuildingBalance(buildingId));
        return "add-message-as-manager";
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @PostMapping("/manager/buildings/{buildingId}/add-message")
    public String addMessageAsManagerConfirm(@Valid MessageAddDTO messageAddDTO,
                                             BindingResult bindingResult,
                                             RedirectAttributes redirectAttributes,
                                             @PathVariable("buildingId") Long buildingId,
                                             Principal principal) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("messageAddDTO", messageAddDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.messageAddDTO", bindingResult);
            return String.format("redirect:/manager/buildings/%d/add-message", buildingId);
        }
        messageService.addMessage(messageAddDTO, buildingId);
        return String.format("redirect:/manager/buildings/%d/my-messages/%s",
                buildingId, principal.getName());
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/{buildingId}/message/{messageId}")
    public String editMessageAsManager(@PathVariable("buildingId") Long buildingId,
                                       @PathVariable("messageId") Long messageId,
                                       Model model) {
        if (!model.containsAttribute("messageEditDTO")) {
            model.addAttribute("messageEditDTO", new MessageEditDTO());
        }
        model.addAttribute("building", buildingService.findById(buildingId));
        model.addAttribute("buildingBalance", taxService.calculateBuildingBalance(buildingId));
        model.addAttribute("message", messageService.findById(messageId));
        return "edit-message-as-manager";
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @PostMapping("/manager/buildings/{buildingId}/message/{messageId}")
    public String editMessageAsManagerConfirm(@Valid MessageEditDTO messageEditDTO,
                                              BindingResult bindingResult,
                                              RedirectAttributes redirectAttributes,
                                              @PathVariable("buildingId") Long buildingId,
                                              @PathVariable("messageId") Long messageId,
                                              Principal principal) {
        messageEditDTO.setId(messageId);
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("messageEditDTO", messageEditDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.messageEditDTO", bindingResult);
            return String.format("redirect:/manager/buildings/%d/message/%d",
                    buildingId, messageId);
        }
        messageService.updateMessage(messageEditDTO);
        return String.format("redirect:/manager/buildings/%d/my-messages/%s",
                buildingId, principal.getName());
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @DeleteMapping("/manager/buildings/{buildingId}/delete-message/{messageId}")
    public String deleteMessageConfirm(@PathVariable("buildingId") Long buildingId,
                                       @PathVariable("messageId") Long messageId,
                                       Principal principal) {
        messageService.deleteMessageWithId(messageId,buildingId);
        return String.format("redirect:/manager/buildings/%d/my-messages/%s",
                buildingId, principal.getName());
    }
}
