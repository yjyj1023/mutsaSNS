package com.mutsasns.domain.alarm;

import com.mutsasns.domain.Base;
import com.mutsasns.domain.alarm.dto.AlarmResponse;
import com.mutsasns.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Alarm extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String alarmType;
    private Long fromUserId;
    private Long targetId;
    private String text;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public AlarmResponse toResponse() {
        return AlarmResponse.builder()
                .id(this.id)
                .alarmType(this.alarmType)
                .fromUserId(this.fromUserId)
                .targetId(this.targetId)
                .text(this.text)
                .userName(this.user.getUserName())
                .createdAt(this.getCreatedAt())
                .build();
    }
}
