package org.example.web.controller;

import org.example.domain.model.Constant;
import org.example.domain.model.GameResult;
import org.example.domain.model.LeaderBoard;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<CurrentGameDto> getGame(@PathVariable UUID gameId) {
        Optional<ModelCurrentGame> optionalGame = gameService.getGameById(gameId);
        return optionalGame.map(currentGame -> ResponseEntity.ok(GameWebMapper.toDto(currentGame)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping("/{gameId}")
    public ResponseEntity<?> makePlayerMove(
            @PathVariable UUID gameId,
            @AuthenticationPrincipal UUID playerId,
            @RequestBody PlayerMove playerMove) {
        Optional<ModelCurrentGame> optionalGame = gameService.getGameById(gameId);
        if (optionalGame.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Игра не найдена!");
        }
        ModelCurrentGame game = optionalGame.get();
        GameResult result;
        if (game.getOpponent().equals(Constant.COMP)) {
            result = gameService.processMove(game, playerMove.row(), playerMove.col(), playerId);
        } else {
            result = gameService.makePlayerMove(game, playerMove.row(), playerMove.col(), playerId);
        }
        CurrentGameDto currentGameDto = GameWebMapper.toDto(result.getCurrentGame());

        return ResponseEntity.ok(GameResultDto.fromDomain(result, currentGameDto));
    }

    @PostMapping("/create/comp")
    public ResponseEntity<CurrentGameDto> createGameWithComp(@AuthenticationPrincipal UUID playerId) {
        ModelCurrentGame newGame = gameService.createGameWithComp(playerId);
        return ResponseEntity.ok(GameWebMapper.toDto(newGame));
    }

    @PostMapping("/create/friend")
    public ResponseEntity<CurrentGameDto> createGame(@AuthenticationPrincipal UUID playerId) {
        ModelCurrentGame newGame = gameService.createGameWithFriend(playerId);
        return ResponseEntity.ok(GameWebMapper.toDto(newGame));
    }

    @GetMapping("/available")
    public ResponseEntity<List<CurrentGameDto>> getAvailableGames() {
        List<ModelCurrentGame> games = gameService.getAvailableGames();
        List<CurrentGameDto> dtoGames = games.stream().map(GameWebMapper::toDto).toList();
        return ResponseEntity.ok(dtoGames);
    }

    @PostMapping("/{gameId}/join")
    public ResponseEntity<?> joinToGame(
            @PathVariable UUID gameId,
            @AuthenticationPrincipal UUID playerId) {
        Optional<ModelCurrentGame> optionalGame = gameService.getGameById(gameId);
        if (optionalGame.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Игра не найдена!");
        }
        ModelCurrentGame game = optionalGame.get();

        GameResult result = gameService.joinTheGame(game, playerId);
        CurrentGameDto currentGameDto = GameWebMapper.toDto(result.getCurrentGame());
        return ResponseEntity.ok(GameResultDto.fromDomain(result, currentGameDto));
    }

    @GetMapping("/history")
    public ResponseEntity<List<CurrentGameDto>> getFinishedGames(
            @AuthenticationPrincipal UUID playerId) {
        List<ModelCurrentGame> games = gameService.getFinishedGames(playerId);
        List<CurrentGameDto> dtoGames = games.stream().map(GameWebMapper::toDto).toList();
        return ResponseEntity.ok(dtoGames);
    }

    @GetMapping("/leader")
    public ResponseEntity<List<LeaderBoard>> getLeaderBoard(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(gameService.getTopPlayer(limit));
    }
}
