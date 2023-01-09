package com.mutsasns.controller;

import com.mutsasns.domain.Response;
import com.mutsasns.domain.alarm.dto.AlarmResponse;
import com.mutsasns.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/alarms")
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping
    public Response<Page<AlarmResponse>> alarm(Pageable pageable) {
        return Response.success(alarmService.alarm(pageable));
    }
}
