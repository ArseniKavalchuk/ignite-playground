CREATE TABLE IF NOT EXISTS event_num_test (
    id              BIGINT,
    topic           VARCHAR,
    start_time      BIGINT,
    end_time        BIGINT,
    numval          VARCHAR,
    PRIMARY KEY (id) 
) WITH "template=partitioned,backups=0,affinitykey=id,atomicity=atomic";
CREATE INDEX idx_num_val ON event_num_test(numval);
CREATE INDEX idx_evt_start_time ON event_num_test(start_time);
