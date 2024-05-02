package roomescape.reservation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import roomescape.ranking.RankTheme;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationDao;
import roomescape.reservation.request.ReservationRequest;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeDao;
import roomescape.time.domain.ReservationTime;
import roomescape.time.repository.ReservationTimeDao;

@Service
public class ReservationService {

    private final ReservationDao reservationDao;
    private final ReservationTimeDao reservationTimeDao;
    private final ThemeDao themeDao;

    public ReservationService(ReservationDao reservationDao, ReservationTimeDao reservationTimeDao, ThemeDao themeDao) {
        this.reservationDao = reservationDao;
        this.reservationTimeDao = reservationTimeDao;
        this.themeDao = themeDao;
    }

    public List<Reservation> findAll() {
        return reservationDao.findAll();
    }

    public Reservation save(ReservationRequest request) {
        if (reservationDao.existsByDateTime(request.date(), request.timeId(),request.themeId())) {
            throw new IllegalArgumentException("Reservation already exists");
        }
        ReservationTime reservationTime = reservationTimeDao.findById(request.timeId());
        Theme theme = themeDao.findById(request.themeId());
        return reservationDao.save(new Reservation(0, request.name(), request.date(), reservationTime, theme));
    }

    public Reservation validateFutureAndSave(ReservationRequest request) {
        if (reservationDao.existsByDateTime(request.date(), request.timeId(),request.themeId())) {
            throw new IllegalArgumentException("Reservation already exists");
        }
        ReservationTime reservationTime = reservationTimeDao.findById(request.timeId());
        validateDateTime(request.date(), reservationTime.startAt());
        Theme theme = themeDao.findById(request.themeId());
        return reservationDao.save(new Reservation(0, request.name(), request.date(), reservationTime, theme));

    }

    public void delete(long id) {
        reservationDao.deleteById(id);
    }

    public List<RankTheme> getRanking(){
        return reservationDao.getRanking();
    }

    public List<ReservationTime> available(LocalDate parse,long themeId) {
        return reservationDao.available(parse,themeId);
    }

    private LocalDateTime validateDateTime(LocalDate date, LocalTime time) {
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot create a reservation for a past date and time.");
        }
        return dateTime;
    }
}
