package org.example.datasource.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.datasource.converter.MatrixConverter;
import org.example.domain.model.GameStatus;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "games")
@Entity
public class GameEntity {

    @Id
    @Column(name = "Game_ID")
    private UUID gameUuid;

    @Convert(converter = MatrixConverter.class)
    @Column(name = "Field", columnDefinition = "TEXT")
    private int[][] field;

    @Column(name = "Player_X")
    private UUID idPlayerX;

    @Column(name = "Player_O")
    private UUID idPlayerO;

    @Column(name = "Opponent")
    private String opponent;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status")
    private GameStatus gameStatus;

    private int firstPlayer;
    private UUID currentPlayer;

    @Column(name = "Created")
    @CreationTimestamp
    private LocalDateTime createdAt;
}
