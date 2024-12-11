show tables ;

CREATE TABLE IF NOT EXISTS point_policy (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            name VARCHAR(255),
    amount INT NOT NULL,
    rate DECIMAL(4,2) NOT NULL
    );