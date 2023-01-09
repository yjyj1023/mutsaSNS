package com.mutsasns.service;

import com.mutsasns.domain.alarm.Alarm;
import com.mutsasns.domain.alarm.dto.AlarmResponse;
import com.mutsasns.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AlarmService {
    private final AlarmRepository alarmRepository;

    public Page<AlarmResponse> alarm(Pageable pageable) {
        Page<Alarm> alarms = alarmRepository.findAll(pageable);

        List<AlarmResponse> alarmResponses = alarms.stream()
                .map(Alarm::toResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(alarmResponses);
    }
}
