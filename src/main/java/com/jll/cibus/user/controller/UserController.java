package com.jll.cibus.user.controller;

import com.jll.cibus.user.dto.UserRequestDTO;
import com.jll.cibus.user.dto.UserResponseDTO;
import com.jll.cibus.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAll (@RequestParam(required = false) Long dni,
                                                         @RequestParam(required = false) String name,
                                                         @RequestParam(required = false) String phoneNumber,
                                                         @RequestParam(required = false) String email,
                                                         @RequestParam(required = false) Long branchId,
                                                         @RequestParam(required = false) Long userRoleId) {

        return ResponseEntity.ok(userService.getUsers(dni, name, phoneNumber, email, branchId, userRoleId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getById (@PathVariable Long id) {

        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<UserResponseDTO>> getByName (@PathVariable String name) {

        return  ResponseEntity.ok(userService.findByNameContaining(name));
    }

    @GetMapping ("/email/{email}")
    public ResponseEntity<UserResponseDTO> getByEmail (@PathVariable String email) {

        return ResponseEntity.ok(userService.findByEmail(email));
    }

    @GetMapping("/phoneNumber/{phoneNumber}")
    public ResponseEntity<UserResponseDTO> getByPhoneNumber (@PathVariable String phoneNumber) {

        return ResponseEntity.ok(userService.findByPhoneNumber(phoneNumber));
    }

    @GetMapping ("/branchId/{branchId}")
    public ResponseEntity<List<UserResponseDTO>> getByBranchId (@PathVariable Long branchId) {

        return ResponseEntity.ok(userService.findByBranchId(branchId));
    }

    @GetMapping("/roleId/{roleId}")
    public ResponseEntity<List<UserResponseDTO>> getByRoleId (@PathVariable Long roleId) {

        return ResponseEntity.ok(userService.findByRoleId(roleId));
    }

    @PostMapping ()
    public ResponseEntity<UserResponseDTO> create (@Valid @RequestBody UserRequestDTO dto) {
        UserResponseDTO toCreate = userService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toCreate);
    }

    @PutMapping ("/{id}")
    public ResponseEntity<UserResponseDTO> update (@PathVariable Long id, @Valid @RequestBody UserRequestDTO dto) {

        return ResponseEntity.ok(userService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete (@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    //-------------- ya esta el containing pero por si los quieren dejar

    @GetMapping("/firstName/{firstName}")
    public ResponseEntity<List<UserResponseDTO>> getByFirstName (@PathVariable String firstName) {

        return  ResponseEntity.ok(userService.findByFirstName(firstName));
    }

    @GetMapping("/lastName/{lastName}")
    public ResponseEntity<List<UserResponseDTO>> getByLastName (@PathVariable String lastName) {

        return ResponseEntity.ok(userService.findByLastName(lastName));
    }

    @GetMapping ("/firstName/{firstName}/lastName/{lastName}")
    public ResponseEntity<List<UserResponseDTO>> getByFirstNameAndLastName (@PathVariable String firstName, @PathVariable String lastName) {

        return ResponseEntity.ok(userService.findByFirstNameAndLastName(firstName,lastName));
    }
}
