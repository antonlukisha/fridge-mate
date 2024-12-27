package com.example.fridgemate.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Setter
@Getter
public class CleanupData {
    private LocalDateTime lastCleanupTime;
}
