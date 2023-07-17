CREATE TABLE displays_locations (
    id SERIAL,
    display_id SERIAL NOT NULL,
    locations_id SERIAL NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (display_id) REFERENCES displays (id) ON DELETE CASCADE,
    FOREIGN KEY (locations_id) REFERENCES locations (id) ON DELETE CASCADE
);