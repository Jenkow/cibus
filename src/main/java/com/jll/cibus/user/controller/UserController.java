package com.jll.cibus.user.controller;

import com.jll.cibus.user.dto.UserRequestDTO;
import com.jll.cibus.user.dto.UserResponseDTO;
import com.jll.cibus.user.dto.UserUpdateDTO;
import com.jll.cibus.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<Page<UserResponseDTO>> getAll (Pageable pageable,
                                                         @RequestParam(required = false) Long dni,
                                                         @RequestParam(required = false) String name,
                                                         @RequestParam(required = false) String phoneNumber,
                                                         @RequestParam(required = false) String email,
                                                         @RequestParam(required = false) Long branchId,
                                                         @RequestParam(required = false) Long userRoleId) {

        return ResponseEntity.ok(userService.findAll(pageable, dni, name, phoneNumber, email, branchId, userRoleId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getById (@PathVariable Long id) {

        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping ()
    public ResponseEntity<UserResponseDTO> create (@Valid @RequestBody UserRequestDTO dto) {
        UserResponseDTO user = userService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping ("/{id}")
    public ResponseEntity<UserResponseDTO> update (@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {

        return ResponseEntity.ok(userService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete (@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/roles")
    public ResponseEntity<List<String>> getRoles(){
        return ResponseEntity.ok(userService.getUserRoles());
    }

}
