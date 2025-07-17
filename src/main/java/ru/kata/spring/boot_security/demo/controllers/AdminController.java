package ru.kata.spring.boot_security.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @GetMapping("")
    public String getAll(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("allUsers", userService.getAll());
        model.addAttribute("user", user);
        List<Role> roles = (List<Role>) roleService.findAll();
        model.addAttribute("userForm", new User());
        model.addAttribute("allRoles", roles);
        return "admin";
    }

//    ________________________________________________________________________


    @PostMapping("/save")
    public String save(@ModelAttribute("userForm") @Valid User userForm, BindingResult bindingResult,
                       @RequestParam("roleIds") List<Long> roleIds, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleService.findAll());
            return "admin";
        }

        if (!userService.save(userForm, roleIds)) {
            model.addAttribute("emailError", "Пользователь с таким email уже существует");
            model.addAttribute("allRoles", roleService.findAll());
            return "admin";
        }

        return "redirect:/admin";
    }
//    ============================================================================

    @PostMapping("/delete")
    public String delete(@RequestParam("usID") Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }

    //=================================================================================

    @PostMapping("/update")
    public String update(@ModelAttribute("userForm") User userForm, @RequestParam("roleIds") List<Long> roleIds) {
        userService.update(userForm, roleIds);
        return "redirect:/admin";
    }
}