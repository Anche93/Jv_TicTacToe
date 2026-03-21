package org.example.web.controller;

import org.example.datasource.repository.GameRepository;
import org.example.domain.model.GameResult;
import org.example.domain.model.ModelCurrentGame;
import org.example.domain.service.GameService;
import org.example.web.mapper.GameWebMapper;
import org.example.web.model.CurrentGameDto;
import org.example.web.model.GameResultDto;
import org.example.web.model.PlayerMove;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/game")
public class GameController {

    private GameRepository gameRepository;
    private GameService gameService;

    public GameController(GameRepository gameRepository, GameService gameService) {
        this.gameRepository = gameRepository;
        this.gameService = gameService;
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<CurrentGameDto> getGame(@PathVariable UUID uuid)  {
        ModelCurrentGame currentGame = gameRepository.getCurrentGame(uuid);
        if (currentGame == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(GameWebMapper.toDto(currentGame));
    }

    @PostMapping("/{uuid}")
    public ResponseEntity<GameResultDto> makePlayerMove(
            @PathVariable UUID uuid,
            @RequestBody PlayerMove playerMove) {
        GameResult result = gameService.processMove(uuid, playerMove.row(), playerMove.col());
        CurrentGameDto currentGameDto = GameWebMapper.toDto(result.getCurrentGame());

        return ResponseEntity.ok(GameResultDto.fromDomain(result, currentGameDto));
    }

    @PostMapping("/create")
    public ResponseEntity<CurrentGameDto> createGame() {
        ModelCurrentGame newGame = new ModelCurrentGame();

        int firstPlayer = gameService.determineFirstPlayer(newGame);
        newGame.setFirstPlayer(firstPlayer);
        gameRepository.saveGame(newGame);

        return ResponseEntity.ok(GameWebMapper.toDto(newGame));
    }
}
