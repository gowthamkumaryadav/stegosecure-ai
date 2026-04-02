package com.steganography.backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
public class OptionsController {

    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleOptions() {
        return ResponseEntity.ok().build();
    }
}