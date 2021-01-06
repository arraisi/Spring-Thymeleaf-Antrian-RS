package com.arraisi.antrianrumahsakit.repository;

import com.arraisi.antrianrumahsakit.entity.Patient;
import com.arraisi.antrianrumahsakit.entity.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Repository
public class PatientDao {
    @Autowired
    private NamedParameterJdbcTemplate parameterJdbcTemplate;

    @Autowired
    private QueueDao queueDao;

    public Patient findById(Integer id) throws EmptyResultDataAccessException {
        String query = "SELECT id, name, phone_number FROM patient WHERE id = :id";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("id", id);
        Patient patient = this.parameterJdbcTemplate.queryForObject(query, map, new PatientRowMapper());
        return patient;
    }

    @Transactional
    public void save(Patient value) throws EmptyResultDataAccessException {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", value.getName());
        params.addValue("phoneNumber", value.getPhoneNumber());

        String query = "INSERT INTO patient (name, phone_number) VALUES (:name,:phoneNumber)";

        final int update = this.parameterJdbcTemplate.update(query, params, keyHolder);

        if (update == 1) {
            value.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
            queueDao.save(value);
        }
    }

    public List<Patient> findAll() {
        //language=MySQL
        String query = "SELECT id, name, phone_number FROM patient \n";
        List<Patient> list = this.parameterJdbcTemplate.query(query, new RowMapper<Patient>() {
            @Override
            public Patient mapRow(ResultSet resultSet, int i) throws SQLException {
                Patient value = new Patient();
                value.setId(resultSet.getInt("id"));
                value.setName(resultSet.getString("name"));
                value.setPhoneNumber(resultSet.getString("phone_number"));
                return value;
            }
        });
        return list;
    }

    private class PatientRowMapper implements RowMapper<Patient> {
        @Override
        public Patient mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Patient(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("phone_number")
            );
        }
    }
}
