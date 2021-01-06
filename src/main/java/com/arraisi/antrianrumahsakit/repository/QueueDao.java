package com.arraisi.antrianrumahsakit.repository;

import com.arraisi.antrianrumahsakit.entity.Patient;
import com.arraisi.antrianrumahsakit.entity.Queue;
import com.arraisi.antrianrumahsakit.entity.QueueNumbers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class QueueDao {
    @Autowired
    private NamedParameterJdbcTemplate parameterJdbcTemplate;

    public List<Queue> findAll() {
        //language=MySQL
        String query = "SELECT q.id, " +
                "q.patient_id, " +
                "q.queue_number, " +
                "q.status, " +
                "p.name, " +
                "p.phone_number\n" +
                "from queue q\n" +
                "         left join patient p on q.patient_id = p.id";
        List<Queue> list = this.parameterJdbcTemplate.query(query, new QueueRowMapper());
        return list;
    }

    public Integer findLastQueueNumber() {
        //language=MySQL
        String finalQuery = "select max(queue_number) as queue_number from queue";
        return this.parameterJdbcTemplate.queryForObject(finalQuery, new MapSqlParameterSource(), (resultSet, i) -> resultSet.getInt("queue_number"));
    }

    public Integer countQueue() {
        //language=MySQL
        String finalQuery = "select count(*) as queue_count from queue";
        return this.parameterJdbcTemplate.queryForObject(finalQuery, new MapSqlParameterSource(), (resultSet, i) -> resultSet.getInt("queue_count"));
    }

    @Transactional
    public void save(Patient value) throws EmptyResultDataAccessException {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("patientId", value.getId());
        params.addValue("queueNumber", findLastQueueNumber() + 1);
        params.addValue("status", "waiting");

        //language=MySQL
        String finalQuery = "INSERT INTO queue (patient_id, queue_number, status)\n" +
                "VALUES (:patientId, :queueNumber, :status)\n";

        if (countQueue() < 15) {
            this.parameterJdbcTemplate.update(finalQuery, params);
        }
    }

    public QueueNumbers findQueueNumbers() {
        //language=MySQL
        String query = "select" +
                "       (SELECT COUNT(*) FROM queue)                               as total,\n" +
                "       (select (min(queue_number) - 1) as queue_number from queue where status = 'waiting' order by queue_number) as queue_number,\n" +
                "       (SELECT COUNT(*) FROM queue WHERE status = 'pharmacy')     as pharmacy,\n" +
                "       (SELECT COUNT(*) FROM queue WHERE status = 'done')         as done";
        return this.parameterJdbcTemplate.queryForObject(query, new MapSqlParameterSource(), new RowMapper<QueueNumbers>() {
            @Override
            public QueueNumbers mapRow(ResultSet resultSet, int i) throws SQLException {
                QueueNumbers value = new QueueNumbers();
                value.setTotalQueue(resultSet.getInt("total"));
                value.setCurrentQueue(resultSet.getInt("queue_number"));
                value.setPharmacyQueue(resultSet.getInt("pharmacy"));
                value.setDoneQueue(resultSet.getInt("done"));
                return value;
            }
        });
    }

    public List<Queue> findPharmacyQueue() {
        String query = "SELECT q.id, q.patient_id, q.queue_number, q.status, p.name, p.phone_number\n" +
                "from queue q\n" +
                "         left join patient p on q.patient_id = p.id\n" +
                "         where status = 'pharmacy'";
        List<Queue> list = this.parameterJdbcTemplate.query(query, new QueueRowMapper());
        return list;
    }

    public List<Queue> findDoneQueue() {
        //language=MySQL
        String query = "SELECT q.id, q.patient_id, q.queue_number, q.status, p.name, p.phone_number\n" +
                "from queue q\n" +
                "         left join patient p on q.patient_id = p.id\n" +
                "         where status = 'done'";
        List<Queue> list = this.parameterJdbcTemplate.query(query, new QueueRowMapper());
        return list;
    }

    public Queue findById(Integer id) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("id", id);
        //language=MySQL
        String query = "SELECT q.id, q.patient_id, q.queue_number, q.status, p.name, p.phone_number\n" +
                "from queue q\n" +
                "         left join patient p on q.patient_id = p.id\n" +
                "         where id = :id";
        return this.parameterJdbcTemplate.queryForObject(query, map, new QueueRowMapper());
    }

    public Queue findByStatusCheckUp() {
        //language=MySQL
        String query = "SELECT q.id, q.patient_id, q.queue_number, q.status, p.name, p.phone_number\n" +
                "from queue q\n" +
                "         left join patient p on q.patient_id = p.id\n" +
                "         where status = 'check_up'";
        return this.parameterJdbcTemplate.queryForObject(query, new MapSqlParameterSource(), new QueueRowMapper());
    }

    @Transactional
    public void updateStatus(Integer queueNumber, String status) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("queueNumber", queueNumber);
        map.addValue("status", status);
        String query = "UPDATE queue SET status = :status WHERE queue_number = :queueNumber \n";
        this.parameterJdbcTemplate.update(query, map);
    }

    @Transactional
    public void updateStatusCheckUpToPharmacy(){
        final Queue byStatusCheckUp = findByStatusCheckUp();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("queueNumber", byStatusCheckUp.getQueueNumber());
        //language=MySQL
        String query = "UPDATE queue SET status = 'pharmacy' WHERE queue_number = :queueNumber\n";
        this.parameterJdbcTemplate.update(query, map);
    }

    @Transactional
    public void remove(Integer queueNumber) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("queueNumber", queueNumber);
        //language=MySQL
        String query = "DELETE FROM queue WHERE queue_number = :queueNumber\n";
        this.parameterJdbcTemplate.update(query, map);
    }
    @Transactional
    public void removeQueu() {
        //language=MySQL
        String query = "DELETE FROM queue\n";
        this.parameterJdbcTemplate.update(query, new MapSqlParameterSource());
    }

    private class QueueRowMapper implements RowMapper<Queue> {
        @Override
        public Queue mapRow(ResultSet rs, int rowNum) throws SQLException {
            Patient patient = new Patient();
            patient.setId(rs.getInt("patient_id"));
            patient.setName(rs.getString("name"));
            patient.setPhoneNumber(rs.getString("phone_number"));

            return new Queue(
                    rs.getInt("id"),
                    patient,
                    rs.getInt("queue_number"),
                    rs.getString("status")
            );
        }
    }
}
