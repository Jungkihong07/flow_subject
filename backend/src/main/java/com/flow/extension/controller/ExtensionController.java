package com.flow.extension.controller;

import com.flow.extension.dto.ExtensionCreateRequest;
import com.flow.extension.dto.ExtensionDto;
import com.flow.extension.dto.FixedExtensionDto;
import com.flow.extension.dto.StatusUpdateRequest;
import com.flow.extension.entity.Status;
import com.flow.extension.service.ExtensionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/extensions")
@RequiredArgsConstructor
public class ExtensionController {

    private final ExtensionService extensionService;

    @GetMapping("/fixed")
    public ResponseEntity<List<FixedExtensionDto>> getFixedExtensions() {
        return ResponseEntity.ok(extensionService.getAllFixedExtensions());
    }

    @GetMapping("/custom")
    public ResponseEntity<List<ExtensionDto>> getCustomExtensions() {
        return ResponseEntity.ok(extensionService.getAllCustomExtensions());
    }

    @PatchMapping("/fixed/{name}")
    public ResponseEntity<FixedExtensionDto> updateFixedExtension(
            @PathVariable String name,
            @RequestBody StatusUpdateRequest request) {
        try {
            Status status = Status.valueOf(request.getStatus().toUpperCase());
            FixedExtensionDto updated = extensionService.updateFixedExtensionStatus(name, status);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/custom")
    public ResponseEntity<ExtensionDto> addCustomExtension(@RequestBody ExtensionCreateRequest request) {
        try {
            String name = request.getName();
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            ExtensionDto added = extensionService.addCustomExtension(name);
            return ResponseEntity.ok(added);
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @DeleteMapping("/custom/{name}")
    public ResponseEntity<Void> deleteCustomExtension(@PathVariable String name) {
        try {
            extensionService.deleteCustomExtension(name);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
