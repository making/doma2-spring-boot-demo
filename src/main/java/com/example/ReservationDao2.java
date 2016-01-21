package com.example;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@ConfigAutowireableDS2
@Dao
public interface ReservationDao2 {
    @Select
    List<Reservation> selectAll();

    @Select
    List<Reservation> selectByName(String name);

    @Insert
    @Transactional
    int insert(Reservation reservation);
}
