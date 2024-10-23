-- Create table for Impressions
CREATE TABLE impressions (
                             id BIGINT AUTO_INCREMENT NOT NULL,
                             uuid VARCHAR(36) NOT NULL UNIQUE,
                             app_id INT,
                             country_code VARCHAR(10) NOT NULL,
                             advertiser_id INT NOT NULL,
                             PRIMARY KEY (id)
);

-- Create index on app_id and country_code for faster queries
CREATE INDEX idx_impressions_app_country ON impressions (app_id, country_code);

-- Create table for Clicks
CREATE TABLE clicks (
                        id BIGINT AUTO_INCREMENT NOT NULL,
                        impression_id BIGINT NOT NULL,
                        revenue DOUBLE NOT NULL,
                        PRIMARY KEY (id),
                        FOREIGN KEY (impression_id) REFERENCES impressions(id) ON DELETE CASCADE
);

-- Create index on impression_id for faster joins
CREATE INDEX idx_clicks_impression_id ON clicks (impression_id);

