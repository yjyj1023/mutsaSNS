package com.mutsasns.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlgorithmService {

    public int sum(int num) {
        int sumNum = 0;

        while (num != 0) {
            sumNum = sumNum + num % 10;
            num = num / 10;
        }

        log.info(Integer.toString(sumNum));
        return sumNum;
    }
}
