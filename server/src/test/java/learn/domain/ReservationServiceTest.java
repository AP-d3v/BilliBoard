package learn.domain;

import learn.data.ReservationRepository;
import learn.models.BilliardTable;
import learn.models.Reservation;
import learn.notify.OneSignalClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    ReservationRepository repository;
    BilliardTableService billiardTableService;
    OneSignalClient oneSignalClient;
    ReservationService service;

    @BeforeEach
    void setUp() {
        repository = mock(ReservationRepository.class);
        billiardTableService = mock(BilliardTableService.class);
        oneSignalClient = mock(OneSignalClient.class);
        service = new ReservationService(repository, billiardTableService, oneSignalClient);

        when(billiardTableService.findById(anyInt()))
                .thenReturn(new BilliardTable(1, 4, LocalTime.of(23, 0), 1));
        when(repository.findByTableIdAndSessionId(anyInt(), anyString())).thenReturn(null);
        when(repository.findByTableId(anyInt())).thenReturn(List.of());
        when(repository.add(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    private Reservation res(int id, String session, String status) {
        Reservation r = new Reservation(id, "P" + id, "p" + id + "@x.com", session, 1);
        r.setStatus(status);
        return r;
    }

    private Reservation withWindow(Reservation r, String nudgerSession) {
        r.setConfirmRequestedAt(LocalDateTime.now());
        r.setNudgedBySession(nudgerSession);
        return r;
    }

    @Test
    void shouldBecomeCurrentPlayerWhenTableIsEmpty() {
        Result<Reservation> result = service.join(new Reservation(0, "Sam", "sam@x.com", "", 1));
        assertTrue(result.isSuccess());
        assertEquals("PLAYING", result.getPayload().getStatus());
    }

    @Test
    void shouldWaitWhenTableIsNotEmpty() {
        when(repository.findByTableId(1)).thenReturn(List.of(res(1, "other", "PLAYING")));
        Result<Reservation> result = service.join(new Reservation(0, "Sam", "sam@x.com", "", 1));
        assertTrue(result.isSuccess());
        assertEquals("WAITING", result.getPayload().getStatus());
    }

    @Test
    void shouldSendAPushWhenYouJoin() {
        Reservation toJoin = new Reservation(0, "Sam", "sam@x.com", "", 1);
        toJoin.setOnesignalSubscriptionId("sub-123");
        service.join(toJoin);
        verify(oneSignalClient).push(eq("sub-123"), anyString(), anyString());
    }

    @Test
    void shouldNotJoinWithBlankName() {
        Result<Reservation> result = service.join(new Reservation(0, " ", "sam@x.com", "", 1));
        assertFalse(result.isSuccess());
        verify(repository, never()).add(any());
    }

    @Test
    void shouldNotJoinTableThatDoesNotExist() {
        when(billiardTableService.findById(anyInt())).thenReturn(null);
        Result<Reservation> result = service.join(new Reservation(0, "Sam", "sam@x.com", "", 99));
        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getType());
    }

    @Test
    void shouldNotJoinSameTableWithASessionAlreadyInLine() {
        when(repository.findByTableIdAndSessionId(1, "s")).thenReturn(res(1, "s", "WAITING"));
        Result<Reservation> result = service.join(new Reservation(0, "Sam", "new@x.com", "s", 1));
        assertFalse(result.isSuccess());
        verify(repository, never()).add(any());
    }

    @Test
    void shouldLeave() {
        when(repository.findByTableIdAndSessionId(1, "s")).thenReturn(res(3, "s", "WAITING"));
        when(repository.deleteById(3)).thenReturn(true);
        assertTrue(service.leave(1, "s"));
    }

    @Test
    void shouldPromoteNextPlayerWhenTheCurrentPlayerLeaves() {
        when(repository.findByTableIdAndSessionId(1, "cur")).thenReturn(res(1, "cur", "PLAYING"));
        when(repository.deleteById(1)).thenReturn(true);
        when(repository.findByTableId(1)).thenReturn(List.of(res(2, "next", "WAITING")));
        service.leave(1, "cur");
        verify(repository).updateStatus(2, "PLAYING");
    }

    @Test
    void roleForReturnsTheRightRole() {
        assertEquals("NONE", service.roleFor(1, null));

        when(repository.findByTableIdAndSessionId(1, "unknown")).thenReturn(null);
        assertEquals("NONE", service.roleFor(1, "unknown"));

        when(repository.findByTableIdAndSessionId(1, "cur")).thenReturn(res(1, "cur", "PLAYING"));
        assertEquals("CURRENT", service.roleFor(1, "cur"));

        when(repository.findByTableIdAndSessionId(1, "wait")).thenReturn(res(2, "wait", "WAITING"));
        assertEquals("WAITING", service.roleFor(1, "wait"));
    }

    @Test
    void roleForReturnsCheckInWhenYouHaveAnOpenWindow() {
        when(repository.findByTableIdAndSessionId(1, "me"))
                .thenReturn(withWindow(res(2, "me", "WAITING"), "third"));
        assertEquals("CHECK_IN", service.roleFor(1, "me"));
    }

    @Test
    void roleForReturnsNextForTheFirstPersonInLine() {
        when(repository.findByTableIdAndSessionId(1, "first")).thenReturn(res(5, "first", "WAITING"));
        when(repository.findByTableId(1)).thenReturn(List.of(
                res(3, "cur", "PLAYING"),
                res(5, "first", "WAITING"),
                res(6, "second", "WAITING")));
        assertEquals("NEXT", service.roleFor(1, "first"));
    }

    @Test
    void roleForReturnsWaitingForSomeoneFurtherBack() {
        when(repository.findByTableIdAndSessionId(1, "second")).thenReturn(res(6, "second", "WAITING"));
        when(repository.findByTableId(1)).thenReturn(List.of(
                res(5, "first", "WAITING"),
                res(6, "second", "WAITING")));
        assertEquals("WAITING", service.roleFor(1, "second"));
    }

    @Test
    void shouldNudgeTheCurrentPlayerAndPushThem() {
        Reservation current = res(1, "cur", "PLAYING");
        current.setOnesignalSubscriptionId("sub-cur");
        when(repository.findByTableIdAndSessionId(1, "next")).thenReturn(res(2, "next", "WAITING"));
        when(repository.findPlayingByTableId(1)).thenReturn(current);
        Result<Void> result = service.nudge(1, "next");
        assertTrue(result.isSuccess());
        verify(repository).setConfirmWindow(eq(1), any(), eq("next"));
        verify(oneSignalClient).pushConfirmRequest("sub-cur", 1);
    }

    @Test
    void shouldNotNudgeIfYouAreNotInLine() {
        assertFalse(service.nudge(1, "stranger").isSuccess());
        verify(repository, never()).setConfirmWindow(anyInt(), any(), anyString());
        verify(oneSignalClient, never()).pushConfirmRequest(anyString(), anyInt());
    }

    @Test
    void shouldNotNudgeWhenNobodyIsTheCurrentPlayer() {
        when(repository.findByTableIdAndSessionId(1, "next")).thenReturn(res(2, "next", "WAITING"));
        when(repository.findPlayingByTableId(1)).thenReturn(null);
        assertFalse(service.nudge(1, "next").isSuccess());
    }

    @Test
    void shouldConfirmStillHere() {
        when(repository.findByTableIdAndSessionId(1, "cur")).thenReturn(res(1, "cur", "PLAYING"));
        Result<Void> result = service.stillHere(1, "cur");
        assertTrue(result.isSuccess());
        verify(repository).clearConfirmWindow(1);
    }

    @Test
    void shouldNotConfirmStillHereIfNotTheCurrentPlayer() {
        when(repository.findByTableIdAndSessionId(1, "s")).thenReturn(res(2, "s", "WAITING"));
        assertFalse(service.stillHere(1, "s").isSuccess());
    }

    @Test
    void stillHereNotifiesTheNextPlayer() {
        Reservation next = res(2, "next", "WAITING");
        next.setOnesignalSubscriptionId("sub-next");
        when(repository.findByTableIdAndSessionId(1, "cur"))
                .thenReturn(withWindow(res(1, "cur", "PLAYING"), "next"));
        when(repository.findByTableId(1)).thenReturn(List.of(next));
        service.stillHere(1, "cur");
        verify(oneSignalClient).push(eq("sub-next"), eq("Still in play"), anyString());
    }

    @Test
    void stillHereDoesNotNotifyWhenNobodyNudged() {
        when(repository.findByTableIdAndSessionId(1, "cur")).thenReturn(res(1, "cur", "PLAYING"));
        when(repository.findByTableId(1)).thenReturn(List.of(res(2, "next", "WAITING")));
        service.stillHere(1, "cur");
        verify(oneSignalClient, never()).push(anyString(), eq("Still in play"), anyString());
    }

    @Test
    void shouldCheckInWhenYouHaveAWindow() {
        when(repository.findByTableIdAndSessionId(1, "me"))
                .thenReturn(withWindow(res(2, "me", "WAITING"), "me"));
        Result<Void> result = service.checkIn(1, "me");
        assertTrue(result.isSuccess());
        verify(repository).updateStatus(2, "PLAYING");
        verify(repository).clearConfirmWindow(2);
    }

    @Test
    void checkInPushesTheNewNextPlayer() {
        Reservation third = res(3, "third", "WAITING");
        third.setOnesignalSubscriptionId("sub-third");
        when(repository.findByTableIdAndSessionId(1, "me"))
                .thenReturn(withWindow(res(2, "me", "WAITING"), "me"));
        when(repository.findByTableId(1)).thenReturn(List.of(third));
        service.checkIn(1, "me");
        verify(oneSignalClient).push(eq("sub-third"), eq("You're next in line"), anyString());
    }

    @Test
    void shouldNotCheckInWithoutAWindow() {
        when(repository.findByTableIdAndSessionId(1, "me")).thenReturn(res(2, "me", "WAITING"));
        assertFalse(service.checkIn(1, "me").isSuccess());
        verify(repository, never()).updateStatus(anyInt(), anyString());
    }

    @Test
    void shouldGiveUpTheTableAndPromoteTheNextPlayer() {
        when(repository.findByTableIdAndSessionId(1, "cur")).thenReturn(res(1, "cur", "PLAYING"));
        when(repository.findByTableId(1)).thenReturn(List.of(res(2, "next", "WAITING")));
        Result<Void> result = service.giveUp(1, "cur");
        assertTrue(result.isSuccess());
        verify(repository).deleteById(1);
        verify(repository).updateStatus(2, "PLAYING");
    }

    @Test
    void shouldNotGiveUpIfNotTheCurrentPlayer() {
        when(repository.findByTableIdAndSessionId(1, "s")).thenReturn(res(2, "s", "WAITING"));
        assertFalse(service.giveUp(1, "s").isSuccess());
    }

    @Test
    void giveUpPushesTheNewNextPlayer() {
        Reservation third = res(3, "third", "WAITING");
        third.setOnesignalSubscriptionId("sub-third");
        when(repository.findByTableIdAndSessionId(1, "cur")).thenReturn(res(1, "cur", "PLAYING"));
        when(repository.findByTableId(1)).thenReturn(
                List.of(res(2, "next", "WAITING"), third),
                List.of(res(2, "next", "PLAYING"), third));
        service.giveUp(1, "cur");
        verify(repository).updateStatus(2, "PLAYING");
        verify(oneSignalClient).push(eq("sub-third"), eq("You're next in line"), anyString());
    }

    @Test
    void giveUpPushesThePromotedPlayerThatTheyAreUp() {
        Reservation next = res(2, "next", "WAITING");
        next.setOnesignalSubscriptionId("sub-next");
        when(repository.findByTableIdAndSessionId(1, "cur")).thenReturn(res(1, "cur", "PLAYING"));
        when(repository.findByTableId(1)).thenReturn(List.of(next));
        service.giveUp(1, "cur");
        verify(oneSignalClient).push(eq("sub-next"), eq("You're up"), anyString());
    }

    @Test
    void leaveAsTheNextPlayerPushesTheNewNextPlayer() {
        Reservation third = res(3, "third", "WAITING");
        third.setOnesignalSubscriptionId("sub-third");
        when(repository.findByTableIdAndSessionId(1, "next")).thenReturn(res(2, "next", "WAITING"));
        when(repository.deleteById(2)).thenReturn(true);
        when(repository.findByTableId(1)).thenReturn(
                List.of(res(2, "next", "WAITING"), third),
                List.of(third));
        service.leave(1, "next");
        verify(oneSignalClient).push(eq("sub-third"), eq("You're next in line"), anyString());
    }

    @Test
    void timerGivesTheTableToTheNudgerWhenTheyAreNextInLine() {
        when(repository.findExpiredConfirms(any()))
                .thenReturn(List.of(withWindow(res(1, "cur", "PLAYING"), "next")));
        when(repository.findByTableId(1)).thenReturn(List.of(res(2, "next", "WAITING")));
        service.promoteExpiredTables();
        verify(repository).deleteById(1);
        verify(repository).updateStatus(2, "PLAYING");
    }

    @Test
    void timerGivesTheNextPlayerTheirOwnWindowWhenSomeoneFurtherBackNudged() {
        when(repository.findExpiredConfirms(any()))
                .thenReturn(List.of(withWindow(res(1, "cur", "PLAYING"), "third")));
        when(repository.findByTableId(1)).thenReturn(List.of(
                res(2, "next", "WAITING"),
                res(3, "third", "WAITING")));
        service.promoteExpiredTables();
        verify(repository).deleteById(1);
        verify(repository, never()).updateStatus(eq(2), anyString());
        verify(repository).setConfirmWindow(eq(2), any(), eq("third"));
    }

    @Test
    void timerPushesTheNudgerThatTheyAreUpWhenTheTableFallsToThem() {
        Reservation nudger = res(2, "next", "WAITING");
        nudger.setOnesignalSubscriptionId("sub-nudger");
        when(repository.findExpiredConfirms(any()))
                .thenReturn(List.of(withWindow(res(1, "cur", "PLAYING"), "next")));
        when(repository.findByTableId(1)).thenReturn(List.of(nudger));
        service.promoteExpiredTables();
        verify(oneSignalClient).push(eq("sub-nudger"), eq("You're up"), anyString());
    }

    @Test
    void theCascadeFallsThroughEveryPlayerUntilItReachesTheNudger() {
        Reservation d = res(4, "d", "WAITING");
        d.setOnesignalSubscriptionId("sub-d");

        when(repository.findExpiredConfirms(any())).thenReturn(
                List.of(withWindow(res(1, "a", "PLAYING"), "d")),
                List.of(withWindow(res(2, "b", "WAITING"), "d")),
                List.of(withWindow(res(3, "c", "WAITING"), "d")));
        when(repository.findByTableId(1)).thenReturn(
                List.of(res(2, "b", "WAITING"), res(3, "c", "WAITING"), d),
                List.of(res(3, "c", "WAITING"), d),
                List.of(d));

        service.promoteExpiredTables();
        service.promoteExpiredTables();
        service.promoteExpiredTables();

        verify(repository).deleteById(1);
        verify(repository).deleteById(2);
        verify(repository).deleteById(3);
        verify(repository).setConfirmWindow(eq(2), any(), eq("d"));
        verify(repository).setConfirmWindow(eq(3), any(), eq("d"));
        verify(repository).updateStatus(4, "PLAYING");
        verify(oneSignalClient).push(eq("sub-d"), eq("You're up"), anyString());
    }

    @Test
    void timerPushesTheWindowedPlayerWhenSomeoneFurtherBackNudged() {
        Reservation next = res(2, "next", "WAITING");
        next.setOnesignalSubscriptionId("sub-next");
        when(repository.findExpiredConfirms(any()))
                .thenReturn(List.of(withWindow(res(1, "cur", "PLAYING"), "third")));
        when(repository.findByTableId(1)).thenReturn(List.of(next, res(3, "third", "WAITING")));
        service.promoteExpiredTables();
        verify(oneSignalClient).push(eq("sub-next"), eq("It's your turn"), anyString());
    }

    @Test
    void shouldDoNothingWhenNoConfirmTimersHaveRunOut() {
        when(repository.findExpiredConfirms(any())).thenReturn(List.of());
        service.promoteExpiredTables();
        verify(repository, never()).deleteById(anyInt());
    }
}
