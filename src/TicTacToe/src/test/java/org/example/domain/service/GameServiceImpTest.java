package org.example.domain.service;

import org.example.domain.model.Constant;
import org.example.domain.model.GameResult;
import org.example.domain.model.GameStatus;
import org.example.domain.model.ModelCurrentGame;
import org.example.domain.port.GameRepositoryPort;
import org.example.domain.port.MoveStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GameServiceImpTest {

    private GameServiceImp gameServiceImp;
    private MoveStrategy moveStrategy;
    private WinChecker winChecker;

    @BeforeEach
    void setUp() {
        gameServiceImp = new GameServiceImp(null, moveStrategy, winChecker);
    }

    @Test
    void testDetermineFirstPlayer_ShouldReturn1or2() {
        ModelCurrentGame game = new ModelCurrentGame();
        int result = gameServiceImp.determineFirstPlayer(game);
        assertTrue(result == 1 || result == 2);
    }

    @Test
    void testIsMoveInvalid_ShouldReturnTrue_WhenRowLessThenZero() {
        ModelCurrentGame game = new ModelCurrentGame();
        boolean result = gameServiceImp.isMoveInvalid(game, -1, 0);
        assertTrue(result);
    }

    @Test
    void testIsMoveInvalid_ShouldReturnTrue_WhenRowMoreThenZero() {
        ModelCurrentGame game = new ModelCurrentGame();
        boolean result = gameServiceImp.isMoveInvalid(game, 3, 0);
        assertTrue(result);
    }

    @Test
    void testIsMoveInvalid_ShouldReturnTrue_WhenColLessThenZero() {
        ModelCurrentGame game = new ModelCurrentGame();
        boolean result = gameServiceImp.isMoveInvalid(game, 0, -2);
        assertTrue(result);
    }

    @Test
    void testIsMoveInvalid_ShouldReturnTrue_WhenColMoreThenZero() {
        ModelCurrentGame game = new ModelCurrentGame();
        boolean result = gameServiceImp.isMoveInvalid(game, 0, 4);
        assertTrue(result);
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    class WithMocksTest {

        @Mock
        private GameRepositoryPort gameRepositoryPort;

        private MoveStrategy moveStrategy;
        private WinChecker winChecker;
        private GameServiceImp gameServiceImpWithMocks;

        private UUID playerId;

        @BeforeEach
        void setUpWithMocks() {
            playerId = UUID.randomUUID();

            winChecker = new WinChecker();
            moveStrategy = new MinimaxStrategy(winChecker);
            Random random = new Random();
            gameServiceImpWithMocks = new GameServiceImp(
                    gameRepositoryPort,
                    random,
                    moveStrategy,
                    winChecker
            );

        }

        @Test
        void testCreateGameWithComp_WhenFirstPlayerIsPlayer() {
            Random fakeRandom = mock(Random.class);
            when(fakeRandom.nextInt(2)).thenReturn(0);

            GameServiceImp gameService = new GameServiceImp(gameRepositoryPort, fakeRandom, moveStrategy, winChecker);
            ModelCurrentGame game = gameService.createGameWithComp(playerId);

            assertEquals(playerId, game.getIdPlayerX());
            assertNull(game.getIdPlayerO());
            assertEquals(GameStatus.GAME_CONTINUES, game.getGameStatus());
            assertEquals(Constant.COMP, game.getOpponent());
            assertEquals(Constant.PLAYER, game.getFirstPlayer());

            verify(gameRepositoryPort, times(1)).save(game);
        }

        @Test
        void testCreateGameWithComp_WhenFirstPlayerIsComp() {
            Random fakeRandom = mock(Random.class);
            when(fakeRandom.nextInt(2)).thenReturn(1);

            GameServiceImp gameService = new GameServiceImp(gameRepositoryPort, fakeRandom, moveStrategy, winChecker);
            ModelCurrentGame game = gameService.createGameWithComp(playerId);

            assertNull(game.getIdPlayerX());
            assertEquals(playerId, game.getIdPlayerO());
            assertEquals(GameStatus.GAME_CONTINUES, game.getGameStatus());
            assertEquals(Constant.COMP, game.getOpponent());
            assertEquals(Constant.COMPUTER, game.getFirstPlayer());

            verify(gameRepositoryPort, times(1)).save(game);
        }

        @Test
        void testCreateGameWithComp_ShouldCallSavWithCorrectGame() {
            ArgumentCaptor<ModelCurrentGame> captor = ArgumentCaptor.forClass(ModelCurrentGame.class);
            Random fakeRandom = mock(Random.class);
            when(fakeRandom.nextInt(2)).thenReturn(0);
            GameServiceImp gameService = new GameServiceImp(gameRepositoryPort, fakeRandom, moveStrategy, winChecker);

            gameService.createGameWithComp(playerId);
            verify(gameRepositoryPort).save(captor.capture());
            ModelCurrentGame captureGame = captor.getValue();

            assertNotNull(captureGame.getUuid());
            assertEquals(playerId, captureGame.getIdPlayerX());
        }

        @Test
        void testCreateGameWithFriend_ShouldCreatedGameWithTwoPlayers() {
            ModelCurrentGame game = gameServiceImpWithMocks.createGameWithFriend(playerId);

            assertEquals(playerId, game.getIdPlayerX());
            assertNull(game.getIdPlayerO());
            assertEquals(GameStatus.WAITING_FOR_PLAYERS, game.getGameStatus());
            assertEquals(Constant.FRIEND, game.getOpponent());
            assertEquals(Constant.PLAYER_X, game.getFirstPlayer());

            verify(gameRepositoryPort, times(1)).save(game);
        }

        @Test
        void testGetGameById_ShouldReturnGame_WhenGameExist() {
            UUID gameId = UUID.randomUUID();
            ModelCurrentGame game = new ModelCurrentGame();
            game.setUuid(gameId);

            when(gameRepositoryPort.findById(gameId)).thenReturn(Optional.of(game));

            Optional<ModelCurrentGame> result = gameServiceImpWithMocks.getGameById(gameId);

            assertTrue(result.isPresent());
            assertEquals(game, result.get());
            verify(gameRepositoryPort, times(1)).findById(gameId);
        }

        @Test
        void testGetGameById_ShouldReturnEmpty_WhenGameDoesNotExist() {
            UUID gameId = UUID.randomUUID();
            when(gameRepositoryPort.findById(gameId)).thenReturn(Optional.empty());

            Optional<ModelCurrentGame> result = gameServiceImpWithMocks.getGameById(gameId);
            assertFalse(result.isPresent());
            verify(gameRepositoryPort, times(1)).findById(gameId);
        }

        @Test
        void testGetGameById_ShouldCallRepositoryWithCorrectId() {
            UUID gameId = UUID.randomUUID();
            ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);

            when(gameRepositoryPort.findById(any(UUID.class))).thenReturn(Optional.empty());

            gameServiceImpWithMocks.getGameById(gameId);
            verify(gameRepositoryPort).findById(captor.capture());
            assertEquals(gameId, captor.getValue());
        }

        @Test
        void testProcessMove_ShouldReturnGameIsEnd_WhenGameIsOver() {
            ModelCurrentGame finishGame = new ModelCurrentGame();
            finishGame.setGameStatus(GameStatus.PLAYER_WIN);

            GameResult result = gameServiceImpWithMocks.processMove(finishGame, 0, 0, UUID.randomUUID());
            assertEquals(GameStatus.GAME_IS_END, result.getStatus());
            verify(gameRepositoryPort, never()).save(any());
        }

        @Test
        void testProcessMove_ShouldReturnInvalidMove_WhenWrongPlayer() {
            ModelCurrentGame game = new ModelCurrentGame();
            game.setGameStatus(GameStatus.GAME_CONTINUES);
            game.setOpponent(Constant.COMP);
            game.setIdPlayerX(UUID.randomUUID());
            game.setIdPlayerO(UUID.randomUUID());

            GameResult result = gameServiceImpWithMocks.processMove(game, 0, 0, UUID.randomUUID());
            assertEquals(GameStatus.INVALID_MOVE, result.getStatus());
            assertTrue(result.getInfoMessage().contains("Это не твоя игра"));
            verify(gameRepositoryPort, times(1)).save(any());
        }

        @Test
        void testProcessMove_ShouldReturnInvalidMove_WhenMoveInvalid() {
            ModelCurrentGame game = createGameWithComp(Constant.PLAYER);
            game.setIdPlayerX(UUID.randomUUID());
            game.getGameField().setValue(0, 0, Constant.PLAYER_X);

            GameResult result = gameServiceImpWithMocks.processMove(game, 0, 0, game.getIdPlayerX());
            assertEquals(GameStatus.INVALID_MOVE, result.getStatus());
            verify(gameRepositoryPort, times(1)).save(any());
        }

        @Test
        void testProcessMove_ShouldReturnPlayerWin_WhenPlayerWin() {
            ModelCurrentGame game = createGameWithComp(Constant.PLAYER);
            UUID playerId = UUID.randomUUID();
            game.setIdPlayerX(playerId);

            int[][] field = {{1, 1, 0}, {0, 2, 0}, {2, 0, 0}};
            game.getGameField().setGameMatrix(field);
            GameResult result = gameServiceImpWithMocks.processMove(game, 0, 2, playerId);
            assertEquals(GameStatus.PLAYER_WIN, result.getStatus());
        }

        @Test
        void testProcessMove_ShouldReturnCompWin_WhenCompWin() {
            ModelCurrentGame game = createGameWithComp(Constant.COMPUTER);
            UUID playerId = UUID.randomUUID();
            game.setIdPlayerO(playerId);

            int[][] field = {{1, 2, 1}, {2, 1, 0}, {0, 0, 0}};
            game.getGameField().setGameMatrix(field);
            GameResult result = gameServiceImpWithMocks.processMove(game, 2, 0, playerId);

            assertNull(game.getIdPlayerX());
            assertEquals(GameStatus.COMPUTER_WIN, result.getStatus());
        }


        @Test
        void testProcessMove_ShouldReturnZeroWin_WhenZeroWin() {
            ModelCurrentGame game = createGameWithComp(Constant.COMPUTER);
            UUID playerId = UUID.randomUUID();
            game.setIdPlayerO(playerId);
            game.setGameStatus(GameStatus.INVALID_MOVE);

            int[][] field = {{1, 2, 1}, {2, 2, 1}, {0, 1, 0}};
            game.getGameField().setGameMatrix(field);
            GameResult result = gameServiceImpWithMocks.processMove(game, 2, 2, playerId);

            assertNull(game.getIdPlayerX());
            assertEquals(GameStatus.ZERO_WIN, result.getStatus());
        }

        @Test
        void testProcessMove_ShouldReturnGameContinue_WhenRegularMove() {
            ModelCurrentGame game = createGameWithComp(Constant.COMPUTER);
            UUID playerId = UUID.randomUUID();
            game.setIdPlayerO(playerId);

            int[][] field = {{1, 0, 0}, {0, 0, 0}, {0, 0, 0}};
            game.getGameField().setGameMatrix(field);
            GameResult result = gameServiceImpWithMocks.processMove(game, 0, 2, playerId);

            assertNull(game.getIdPlayerX());
            assertEquals(GameStatus.GAME_CONTINUES, result.getStatus());
        }

        @Test
        void testProcessMove_ShouldReturnGameContinue_WhenBestCompMove() {
            ModelCurrentGame game = createGameWithComp(Constant.PLAYER);
            UUID playerId = UUID.randomUUID();
            game.setIdPlayerX(playerId);

            int[][] field = {{1, 0, 0}, {0, 2, 0}, {0, 0, 0}};
            game.getGameField().setGameMatrix(field);
            GameResult result = gameServiceImpWithMocks.processMove(game, 0, 2, playerId);

            assertNull(game.getIdPlayerO());
            assertEquals(GameStatus.GAME_CONTINUES, result.getStatus());
            assertEquals(2, game.getGameField().getValue(0, 1));
        }

        @Test
        void testMakeComputerMove_ShouldMakeMoveAndSave_WhenCompTurn() {
            ModelCurrentGame game = createGameWithComp(Constant.PLAYER);
            game.getGameField().setValue(0, 0, Constant.PLAYER_X);

            int valueComp = Constant.PLAYER_O;
            int valuePlayer = Constant.PLAYER_X;

            int[][] before = copyField(game.getGameField().getGameMatrix());
            gameServiceImpWithMocks.makeComputerMove(game, valueComp, valuePlayer);

            assertTrue(isFieldChanged(before, game.getGameField().getGameMatrix()));
            assertEquals(GameStatus.GAME_CONTINUES, game.getGameStatus());
            verify(gameRepositoryPort, atLeastOnce()).save(game);
        }

        @Test
        void  testMakeComputerMove_ShouldNotMakeMove_WhenPlayerTurn() {
            ModelCurrentGame game = createGameWithComp(Constant.PLAYER);
            int valueComp = Constant.PLAYER_O;
            int valuePlayer = Constant.PLAYER_X;

            int[][] before = copyField(game.getGameField().getGameMatrix());
            gameServiceImpWithMocks.makeComputerMove(game, valueComp, valuePlayer);
            assertTrue(isFieldSame(before, game.getGameField().getGameMatrix()));
            verify(gameRepositoryPort, never()).save(game);
        }

        @Test
        void testMakeComputerMove_ShouldReturnTrue_WhenCountXLessThanCountO() {
            ModelCurrentGame game = createGameWithComp(Constant.PLAYER);
            game.getGameField().setValue(0, 0, Constant.PLAYER_O);
            game.getGameField().setValue(0, 1, Constant.PLAYER_O);

            int valueComp = Constant.PLAYER_O;
            int valuePlayer = Constant.PLAYER_X;

            gameServiceImpWithMocks.makeComputerMove(game, valueComp, valuePlayer);
            verify(gameRepositoryPort, never()).save(game);
        }

        @Test
        void testMakePlayerMove_ShouldReturnGameIsEnd_WhenGameIsOver() {
            ModelCurrentGame finishGame = new ModelCurrentGame();
            finishGame.setGameStatus(GameStatus.PLAYER_X_WIN);

            GameResult result = gameServiceImpWithMocks.makePlayerMove(finishGame, 0, 0, UUID.randomUUID());
            assertEquals(GameStatus.GAME_IS_END, result.getStatus());
            verify(gameRepositoryPort, never()).save(any());
        }

        @Test
        void testMakePlayerMove_ShouldReturnWaitingForPlayers_WhenOnlyOnePlayer() {
            UUID playerX = UUID.randomUUID();
            ModelCurrentGame game = gameServiceImpWithMocks.createGameWithFriend(playerX);

            GameResult result = gameServiceImpWithMocks.makePlayerMove(game, 0, 0, playerX);
            assertNull(game.getIdPlayerO());
            assertEquals(GameStatus.WAITING_FOR_PLAYERS, result.getStatus());
            verify(gameRepositoryPort, times(1)).save(game);
        }

        @Test
        void testMakePlayerMove_ShouldReturnInvalidMove_WhenThirdPlayerInGameTryMove() {
            UUID playerX = UUID.randomUUID();
            UUID playerO = UUID.randomUUID();
            ModelCurrentGame game = gameServiceImpWithMocks.createGameWithFriend(playerX);
            game.setIdPlayerO(playerO);
            game.setGameStatus(GameStatus.GAME_CONTINUES);

            GameResult result = gameServiceImpWithMocks.makePlayerMove(game, 0, 0, UUID.randomUUID());
            assertEquals(GameStatus.INVALID_MOVE, result.getStatus());
            assertEquals(GameStatus.INVALID_MOVE, game.getGameStatus());
            assertTrue(result.getInfoMessage().contains("В этой игре слишком тесно"));
            verify(gameRepositoryPort, times(2)).save(game);
        }

        @Test
        void testMakePlayerMove_ShouldReturnInvalidMove_WhenWrongPlayerMove() {
            UUID playerX = UUID.randomUUID();
            UUID playerO = UUID.randomUUID();
            ModelCurrentGame game = gameServiceImpWithMocks.createGameWithFriend(playerX);
            game.setIdPlayerO(playerO);
            game.setGameStatus(GameStatus.GAME_CONTINUES);

            GameResult result = gameServiceImpWithMocks.makePlayerMove(game, 0, 0, playerO);
            assertEquals(GameStatus.INVALID_MOVE, result.getStatus());
            assertEquals(GameStatus.INVALID_MOVE, game.getGameStatus());
            assertTrue(result.getInfoMessage().contains("Терпения"));
            verify(gameRepositoryPort, times(2)).save(game);
        }

        @Test
        void testMakePlayerMove_ShouldReturnInvalidMove_WhenInvalidMove() {
            UUID playerX = UUID.randomUUID();
            UUID playerO = UUID.randomUUID();
            ModelCurrentGame game = gameServiceImpWithMocks.createGameWithFriend(playerX);
            game.setIdPlayerO(playerO);
            game.setGameStatus(GameStatus.GAME_CONTINUES);

            GameResult result = gameServiceImpWithMocks.makePlayerMove(game, 3, -1, playerX);
            assertEquals(GameStatus.INVALID_MOVE, result.getStatus());
            assertEquals(GameStatus.INVALID_MOVE, game.getGameStatus());
            assertTrue(result.getInfoMessage().contains("Некорректный"));
            verify(gameRepositoryPort, times(2)).save(game);
        }

        @Test
        void testMakePlayerMove_ShouldReturnPlayerXWin_WhenPlayerXWin() {
            UUID playerX = UUID.randomUUID();
            UUID playerO = UUID.randomUUID();
            ModelCurrentGame game = gameServiceImpWithMocks.createGameWithFriend(playerX);
            game.setIdPlayerO(playerO);
            game.setGameStatus(GameStatus.GAME_CONTINUES);

            int[][] field = {{1, 2, 1}, {0, 1, 0}, {2, 2, 0}};
            game.getGameField().setGameMatrix(field);

            GameResult result = gameServiceImpWithMocks.makePlayerMove(game, 2, 2, playerX);
            assertEquals(GameStatus.PLAYER_WIN, result.getStatus());
            assertEquals(GameStatus.PLAYER_X_WIN, game.getGameStatus());
            assertEquals(playerX, result.getPlayerId());
            verify(gameRepositoryPort, times(2)).save(game);
        }

        @Test
        void testMakePlayerMove_ShouldReturnPlayerOWin_WhenPlayerOWin() {
            UUID playerX = UUID.randomUUID();
            UUID playerO = UUID.randomUUID();
            ModelCurrentGame game = gameServiceImpWithMocks.createGameWithFriend(playerX);
            game.setIdPlayerO(playerO);
            game.setGameStatus(GameStatus.GAME_CONTINUES);
            game.setCurrentPlayer(playerO);

            int[][] field = {{1, 1, 0}, {2, 2, 0}, {0, 1, 0}};
            game.getGameField().setGameMatrix(field);

            GameResult result = gameServiceImpWithMocks.makePlayerMove(game, 1, 2, playerO);
            assertEquals(GameStatus.PLAYER_WIN, result.getStatus());
            assertEquals(GameStatus.PLAYER_O_WIN, game.getGameStatus());
            assertEquals(playerO, result.getPlayerId());
            verify(gameRepositoryPort, times(2)).save(game);
        }

        @Test
        void testMakePlayerMove_ShouldReturnZeroWin_WhenZeroWin() {
            UUID playerX = UUID.randomUUID();
            UUID playerO = UUID.randomUUID();
            ModelCurrentGame game = gameServiceImpWithMocks.createGameWithFriend(playerX);
            game.setIdPlayerO(playerO);
            game.setGameStatus(GameStatus.GAME_CONTINUES);

            int[][] field = {{1, 2, 1}, {2, 2, 1}, {0, 1, 2}};
            game.getGameField().setGameMatrix(field);

            GameResult result = gameServiceImpWithMocks.makePlayerMove(game, 2, 0, playerX);
            assertEquals(GameStatus.ZERO_WIN, result.getStatus());
            assertEquals(GameStatus.ZERO_WIN, game.getGameStatus());
            assertNull(result.getPlayerId());
            verify(gameRepositoryPort, times(2)).save(game);
        }

        @Test
        void testMakePlayerMove_ShouldReturnContinueGame_WhenSwitchPlayerToPlayerO() {
            UUID playerX = UUID.randomUUID();
            UUID playerO = UUID.randomUUID();
            ModelCurrentGame game = gameServiceImpWithMocks.createGameWithFriend(playerX);
            game.setIdPlayerO(playerO);
            game.setGameStatus(GameStatus.GAME_CONTINUES);

            int[][] field = {{1, 2, 1}, {2, 2, 1}, {0, 0, 0}};
            game.getGameField().setGameMatrix(field);

            GameResult result = gameServiceImpWithMocks.makePlayerMove(game, 2, 0, playerX);
            assertEquals(GameStatus.GAME_CONTINUES, result.getStatus());
            assertEquals(GameStatus.GAME_CONTINUES, game.getGameStatus());
            assertEquals(playerO, result.getPlayerId());
            assertEquals(game.getCurrentPlayer(), playerO);
            verify(gameRepositoryPort, times(2)).save(game);
        }

        @Test
        void testMakePlayerMove_ShouldReturnContinueGame_WhenSwitchPlayerToPlayerX() {
            UUID playerX = UUID.randomUUID();
            UUID playerO = UUID.randomUUID();
            ModelCurrentGame game = gameServiceImpWithMocks.createGameWithFriend(playerX);
            game.setIdPlayerO(playerO);
            game.setGameStatus(GameStatus.GAME_CONTINUES);
            game.setCurrentPlayer(playerO);

            int[][] field = {{1, 0, 0}, {0, 0, 0}, {0, 0, 0}};
            game.getGameField().setGameMatrix(field);

            GameResult result = gameServiceImpWithMocks.makePlayerMove(game, 1, 1, playerO);
            assertEquals(GameStatus.GAME_CONTINUES, result.getStatus());
            assertEquals(GameStatus.GAME_CONTINUES, game.getGameStatus());
            assertEquals(playerX, result.getPlayerId());
            assertEquals(game.getCurrentPlayer(), playerX);
            verify(gameRepositoryPort, times(2)).save(game);
        }

        @Test
        void testJoinTheGame_ShouldReturnInvalidMove_WhenToJoinYourOwnGame() {
            UUID playerX = UUID.randomUUID();
            ModelCurrentGame game = gameServiceImpWithMocks.createGameWithFriend(playerX);

            GameResult result = gameServiceImpWithMocks.joinTheGame(game, playerX);
            assertNull(game.getIdPlayerO());
            assertEquals(GameStatus.INVALID_MOVE, result.getStatus());
            assertEquals(GameStatus.WAITING_FOR_PLAYERS, game.getGameStatus());
            assertTrue(result.getInfoMessage().contains("Нельзя присоединится"));
            verify(gameRepositoryPort, times(1)).save(game);
        }

        @Test
        void testJoinTheGame_ShouldReturnInvalidMove_WhenTryToJoinAFullGame() {
            ModelCurrentGame game = gameServiceImpWithMocks.createGameWithFriend(UUID.randomUUID());
            game.setIdPlayerO(UUID.randomUUID());
            game.setGameStatus(GameStatus.GAME_CONTINUES);

            GameResult result = gameServiceImpWithMocks.joinTheGame(game, UUID.randomUUID());
            assertEquals(GameStatus.INVALID_MOVE, result.getStatus());
            assertEquals(GameStatus.GAME_CONTINUES, game.getGameStatus());
            assertTrue(result.getInfoMessage().contains("К игре нельзя присоединится"));
            verify(gameRepositoryPort, times(1)).save(game);
        }

        @Test
        void testJoinTheGame_ShouldReturnContinueGame_WhenToJoinTheGame() {
            UUID playerX = UUID.randomUUID();
            ModelCurrentGame game = gameServiceImpWithMocks.createGameWithFriend(playerX);

            GameResult result = gameServiceImpWithMocks.joinTheGame(game, UUID.randomUUID());
            assertEquals(GameStatus.GAME_CONTINUES, result.getStatus());
            assertEquals(GameStatus.GAME_CONTINUES, game.getGameStatus());
            assertEquals(playerX, result.getPlayerId());
            assertNotNull(game.getIdPlayerO());
            verify(gameRepositoryPort, times(2)).save(game);
        }

        @Test
        void testGetAvailableGames_ShouldReturnListOfGames_WhenGamesExist() {
            List<ModelCurrentGame> expectedGames = List.of(
                    createGameWithStatus(),
                    createGameWithStatus()
            );
            when(gameRepositoryPort.findByStatus(GameStatus.WAITING_FOR_PLAYERS)).thenReturn(expectedGames);

            List<ModelCurrentGame> result = gameServiceImpWithMocks.getAvailableGames();

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(expectedGames, result);
            verify(gameRepositoryPort, times(1)).findByStatus(GameStatus.WAITING_FOR_PLAYERS);
        }

        @Test
        void testGetAvailableGames_ShouldReturnEmptyList_WhenNoGamesExist() {
            when(gameRepositoryPort.findByStatus(GameStatus.WAITING_FOR_PLAYERS)).thenReturn(Collections.emptyList());

            List<ModelCurrentGame> result = gameServiceImpWithMocks.getAvailableGames();
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(gameRepositoryPort, times(1)).findByStatus(GameStatus.WAITING_FOR_PLAYERS);
        }

        @Test
        void testGetAvailableGames_ShouldQueryCorrectStatus() {
            ArgumentCaptor<GameStatus> captor = ArgumentCaptor.forClass(GameStatus.class);

            when(gameRepositoryPort.findByStatus(any(GameStatus.class))).thenReturn(Collections.emptyList());
            gameServiceImpWithMocks.getAvailableGames();
            verify(gameRepositoryPort).findByStatus(captor.capture());
            assertEquals(GameStatus.WAITING_FOR_PLAYERS, captor.getValue());
        }

        @Test
        void testGetAvailableGames_ShouldNotReturnGamesWithOtherStatus() {
            List<ModelCurrentGame> availableGames = List.of(
                    createGameWithStatus()
            );
            when(gameRepositoryPort.findByStatus(GameStatus.WAITING_FOR_PLAYERS)).thenReturn(availableGames);
            List<ModelCurrentGame> result = gameServiceImpWithMocks.getAvailableGames();
            for (ModelCurrentGame game : result) {
                assertEquals(GameStatus.WAITING_FOR_PLAYERS, game.getGameStatus());
            }
        }
    }

    private ModelCurrentGame createGameWithComp(int firstPlayer) {
        ModelCurrentGame game = new ModelCurrentGame();
        game.setFirstPlayer(firstPlayer);
        game.setGameStatus(GameStatus.GAME_CONTINUES);
        game.setOpponent(Constant.COMP);
        return game;
    }

    private ModelCurrentGame createGameWithStatus() {
        ModelCurrentGame game = new ModelCurrentGame();
        game.setGameStatus(GameStatus.WAITING_FOR_PLAYERS);
        return game;
    }

    private int[][] copyField(int[][] original) {
        int[][] copy = new int[Constant.ROW][Constant.COL];
        for (int i = 0; i < Constant.ROW; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, Constant.COL);
        }
        return copy;
    }

    private boolean isFieldChanged(int[][] before, int[][] after) {
        for (int i = 0; i < Constant.ROW; i++) {
            for (int j = 0; j < Constant.COL; j++) {
                if (before[i][j] != after[i][j]) return true;
            }
        }
        return false;
    }

    private boolean isFieldSame(int[][] before, int[][] after) {
        return !isFieldChanged(before, after);
    }
}
