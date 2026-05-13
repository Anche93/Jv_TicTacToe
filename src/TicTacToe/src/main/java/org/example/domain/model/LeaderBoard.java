package org.example.domain.model;

import java.util.UUID;

public record LeaderBoard(UUID userId,
                          String userLogin,
                          Double winPercent) {
}
