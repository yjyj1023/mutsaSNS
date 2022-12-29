package com.mutsasns.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AlgorithmServiceTest {
    AlgorithmService algorithmService = new AlgorithmService();

    @Test
    @DisplayName("자릿수의 합 성공")
    void sumOfDigit() {
        assertEquals(21, algorithmService.sum(687));
        assertEquals(22, algorithmService.sum(787));
        assertEquals(0, algorithmService.sum(0));
        assertEquals(5, algorithmService.sum(11111));
    }
}