package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
public class AdminController {

    private UserService userService;
    private RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/admin")
    public String usersList(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("allUsers", userService.allUsers());
        model.addAttribute("user", user);
        List<Role> roles = (List<Role>) roleService.findAll();
        model.addAttribute("userForm", new User());
        model.addAttribute("allRoles", roles);
        return "admin";
    }

//    ________________________________________________________________________

    @GetMapping("/admin/addUser")
    public String registration(Model model) {
        List<Role> roles = (List<Role>) roleService.findAll();
        model.addAttribute("userForm", new User());
        model.addAttribute("allRoles", roles);
        return "user_forma";
    }

    @PostMapping("/admin/saveNewUser")
    public String adminAddUser(@ModelAttribute("userForm") @Valid User userForm, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleService.findAll());
            return "admin";
        }

        if (!userService.saveUser(userForm)) {
            model.addAttribute("emailError", "Пользователь с таким email уже существует");
            model.addAttribute("allRoles", roleService.findAll());
            return "admin";
        }

        return "redirect:/admin";
    }
//    ============================================================================

    @PostMapping("/admin/deleteUser")
    public String removeUser(@RequestParam("usID") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/updateInfo")
    public String updateUser(@RequestParam("usID") Long id, Model model) {
        List<Role> allRoles = roleService.findAll();
        model.addAttribute("adminForm", userService.findUserById(id));
        model.addAttribute("allRoles", allRoles);
        return "admin_forma";
    }

    @PostMapping("/admin/updateUser")
    public String updateUser(@ModelAttribute("userForm") User user) {
        userService.updateUser(user);
        return "redirect:/admin";
    }
}