package bg.propertymanager.web;

import bg.propertymanager.model.dto.user.UserEditDTO;
import bg.propertymanager.model.entity.UserEntity;
import bg.propertymanager.model.view.UserEntityViewModel;
import bg.propertymanager.service.RoleService;
import bg.propertymanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService,
                           RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/users")
    public ModelAndView manageUsers(@Param("searchKeyword") String searchKeyword,
                                    @RequestParam(name = "page") Optional<Integer> page,
                                    @RequestParam(name = "size") Optional<Integer> size) {
        ModelAndView mav = new ModelAndView("manage-users");
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        Page<UserEntityViewModel> usersPage = userService
                .findAllUsersWithFilterPaginated(PageRequest.of(currentPage - 1, pageSize), searchKeyword);
        int totalPages = usersPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            mav.addObject("pageNumbers", pageNumbers);
        }
        mav.addObject("totalUsers", userService.findAll().size());
        mav.addObject("usersPage", usersPage);
        mav.addObject("searchKeyword", searchKeyword);
        return mav;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/users/edit/{name}")
    public ModelAndView editUserAsAdmin(@PathVariable("name") String name, Model model) {
        if (!model.containsAttribute("userEditDTO")) {
            model.addAttribute("userEditDTO", new UserEditDTO());
        }
        UserEntity userProfileToEdit = userService.findUserByUsername(name);
        ModelAndView mav = new ModelAndView("edit-profile-as-admin");
        mav.addObject("user", userProfileToEdit);
        return mav;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/users/edit/{name}")
    public String editUserAsAdminConfirm(@Valid UserEditDTO userEditDTO,
                                         BindingResult bindingResult,
                                         RedirectAttributes redirectAttributes,
                                         @PathVariable("name") String name) {
        userEditDTO.setId(userService.
                findUserByUsername(name)
                .getId());
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("userEditDTO", userEditDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userEditDTO", bindingResult);
            return "redirect:/admin/users/edit/" + name;
        }
        userService.updateProfile(userEditDTO);
        return "redirect:/admin/users/edit/" + name;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/users/change-role/{name}")
    public ModelAndView changeUserRoleAsAdmin(@PathVariable("name") String name) {
        UserEntity userProfileToEdit = userService.findUserByUsername(name);
        ModelAndView mav = new ModelAndView("change-roles");
        mav.addObject("user", userProfileToEdit);
        mav.addObject("roles", roleService.findAll());
        return mav;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/users/change-role/{username}")
    public String changeUserRoleAsAdminConfirm(@PathVariable("username") String username,
                                               @RequestParam String role) {
        userService.changeRole(role, username);
        return "redirect:/admin/users/";
    }
}
