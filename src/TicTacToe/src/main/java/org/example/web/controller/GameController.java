package org.example.web.controller;

import org.example.datasource.mapper.GameDataMapper;
import org.example.datasource.model.GameEntity;
import org.example.datasource.repository.GameRepository;
import org.example.domain.model.Constant;
import org.example.domain.model.GameResult;
import org.example.domain.model.GameStatus;
import org.example.domain.model.ModelCurrentGame;
import org.example.domain.service.GameService;
import org.example.web.mapper.GameWebMapper;
import org.example.web.model.CurrentGameDto;
import org.example.web.model.GameResultDto;
import org.example.web.model.PlayerMove;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameRepository gameRepository;
    private final GameService gameService;

    public GameController(GameRepository gameRepository, GameService gameService) {
        this.gameRepository = gameRepository;
        this.gameService = gameService;
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<CurrentGameDto> getGame(@PathVariable UUID gameId)  {
        Optional<GameEntity> optionalGame = gameRepository.findById(gameId);
        return optionalGame.map(gameEntity -> ResponseEntity.ok(GameWebMapper.toDto(gameEntity)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping("/{gameId}")
    public ResponseEntity<?> makePlayerMove(
            @PathVariable UUID gameId,
            @AuthenticationPrincipal UUID playerId,
            @RequestBody PlayerMove playerMove) {
        Optional<GameEntity> optionalGame = gameRepository.findById(gameId);
        if (optionalGame.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Игра не найдена!");
        }

        GameResult result;
        if (optionalGame.get().getOpponent().equals(Constant.COMP)) {
            result = gameService.processMove(gameId, playerMove.row(), playerMove.col(), playerId);
        } else {
            result = gameService.makePlayerMove(gameId, playerMove.row(), playerMove.col(), playerId);
        }
        CurrentGameDto currentGameDto = GameWebMapper.toDto(result.getCurrentGame());

        return ResponseEntity.ok(GameResultDto.fromDomain(result, currentGameDto));
    }

    @PostMapping("/create/comp")
    public ResponseEntity<CurrentGameDto> createGameWithComp(@AuthenticationPrincipal UUID playerId) {
        ModelCurrentGame newGame = new ModelCurrentGame();

        int firstPlayer = gameService.determineFirstPlayer(newGame);
        newGame.setIdPlayerX(firstPlayer == Constant.COMPUTER ? null : playerId);
        newGame.setIdPlayerO(firstPlayer == Constant.COMPUTER ? playerId : null);
        newGame.setGameStatus(GameStatus.GAME_CONTINUES);
        newGame.setOpponent(Constant.COMP);
        newGame.setFirstPlayer(firstPlayer);

        gameRepository.save(GameDataMapper.toEntity(newGame));
        return ResponseEntity.ok(GameWebMapper.toDto(newGame));
    }

    @PostMapping("/create/friend")
    public ResponseEntity<CurrentGameDto> createGame(@AuthenticationPrincipal UUID playerId) {
        ModelCurrentGame newGame = new ModelCurrentGame();

        newGame.setIdPlayerX(playerId);
        newGame.setIdPlayerO(null);
        newGame.setGameStatus(GameStatus.WAITING_FOR_PLAYERS);
        newGame.setCurrentPlayer(playerId);
        newGame.setOpponent(Constant.FRIEND);
        newGame.setFirstPlayer(Constant.PLAYER_X);

        gameRepository.save(GameDataMapper.toEntity(newGame));
        return ResponseEntity.ok(GameWebMapper.toDto(newGame));
    }

    @GetMapping("/available")
    public ResponseEntity<Iterable<CurrentGameDto>> getAvailableGames() {
        Iterable<GameEntity> iterableGame = gameRepository.findByGameStatus(GameStatus.WAITING_FOR_PLAYERS);
        List<CurrentGameDto> listGame = new ArrayList<>();
        for (GameEntity game : iterableGame) {
            listGame.add(GameWebMapper.toDto(game));
        }
        return ResponseEntity.ok(listGame);
    }

    @PostMapping("/{gameId}/join")
    public ResponseEntity<GameResultDto> joinToGame(
            @PathVariable UUID gameId,
            @AuthenticationPrincipal UUID playerId) {
        GameResult result = gameService.joinTheGame(gameId, playerId);

        CurrentGameDto currentGameDto = GameWebMapper.toDto(result.getCurrentGame());
        return ResponseEntity.ok(GameResultDto.fromDomain(result, currentGameDto));
    }
}
