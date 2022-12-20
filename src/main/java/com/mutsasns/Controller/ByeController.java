package com.mutsasns.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bye")
public class ByeController {

    @GetMapping
    public ResponseEntity<String> bye(){
        return ResponseEntity.ok().body("bye");
    }
}
