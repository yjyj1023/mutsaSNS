package com.mutsasns.controller;

import com.mutsasns.service.AlgorithmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hello")
public class HelloController {

    private final AlgorithmService algorithmService;

    @GetMapping
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok().body("이연재");
    }

    @GetMapping("/{num}")
    public ResponseEntity<Integer> sumOfDigit(@PathVariable int num) {
        int sumNum = algorithmService.sum(num);
        return ResponseEntity.ok().body(sumNum);
    }
}
