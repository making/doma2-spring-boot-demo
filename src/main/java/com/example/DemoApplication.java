package com.example;

import org.seasar.doma.boot.autoconfigure.DomaConfig;
import org.seasar.doma.boot.autoconfigure.DomaConfigBuilder;
import org.seasar.doma.boot.autoconfigure.DomaProperties;
import org.seasar.doma.jdbc.Naming;
import org.seasar.doma.jdbc.SqlFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@RestController
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Autowired
    ReservationDao reservationDao;
    @Autowired
    ReservationDao2 reservationDao2;

    @Bean
    CommandLineRunner runner() {
        return args -> Arrays.asList("spring", "spring boot", "spring cloud", "doma").forEach(s -> {
            Reservation r = new Reservation();
            r.name = s;
            reservationDao.insert(r);

            r.id = null;
            r.name += "2";
            reservationDao2.insert(r);
        });
    }


    @Bean(destroyMethod = "close")
    @ConfigurationProperties("spring.datasource.ds1")
    @Primary
    DataSource dataSource1() {
        return DataSourceBuilder.create().build();
    }

    @Bean(destroyMethod = "close")
    @ConfigurationProperties("spring.datasource.ds2")
    DataSource dataSource2() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties("doma.ds1")
    @DS1
    DomaConfig config1(@Value("${doma.ds1.dialect}") String dialectType,
                       SqlFileRepository sqlFileRepository, Naming naming) {
        return new DomaConfig(new DomaConfigBuilder()
                .dialect(DomaProperties.DialectType.valueOf(dialectType.toUpperCase()).create())
                .sqlFileRepository(sqlFileRepository)
                .naming(naming)
                .dataSource(dataSource1()));
    }

    @Bean
    @ConfigurationProperties("doma.ds2")
    @DS2
    DomaConfig config2(@Value("${doma.ds2.dialect}") String dialectType,
                       SqlFileRepository sqlFileRepository, Naming naming) {
        return new DomaConfig(new DomaConfigBuilder()
                .dialect(DomaProperties.DialectType.valueOf(dialectType.toUpperCase()).create())
                .sqlFileRepository(sqlFileRepository)
                .naming(naming)
                .dataSource(dataSource2()));
    }

    @RequestMapping(path = "/")
    List<Reservation> all() {
        return reservationDao.selectAll();
    }

    @RequestMapping(path = "/all2")
    List<Reservation> all2() {
        return reservationDao2.selectAll();
    }

    @RequestMapping(path = "/", params = "name")
    List<Reservation> name(@RequestParam String name) {
        return reservationDao.selectByName(name);
    }
}

