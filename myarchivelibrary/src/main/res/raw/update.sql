-- Start Update

-- Version 2

-- Version 3
ALTER TABLE albums ADD COLUMN rating_web DOUBLE DEFAULT 0.0;
ALTER TABLE albums ADD COLUMN rating_own DOUBLE DEFAULT 0.0;
ALTER TABLE albums ADD COLUMN rating_note TEXT;

ALTER TABLE songs ADD COLUMN rating_web DOUBLE DEFAULT 0.0;
ALTER TABLE songs ADD COLUMN rating_own DOUBLE DEFAULT 0.0;
ALTER TABLE songs ADD COLUMN rating_note TEXT;

ALTER TABLE movies ADD COLUMN rating_web DOUBLE DEFAULT 0.0;
ALTER TABLE movies ADD COLUMN rating_own DOUBLE DEFAULT 0.0;
ALTER TABLE movies ADD COLUMN rating_note TEXT;

ALTER TABLE games ADD COLUMN rating_web DOUBLE DEFAULT 0.0;
ALTER TABLE games ADD COLUMN rating_own DOUBLE DEFAULT 0.0;
ALTER TABLE games ADD COLUMN rating_note TEXT;

ALTER TABLE books ADD COLUMN rating_web DOUBLE DEFAULT 0.0;
ALTER TABLE books ADD COLUMN rating_own DOUBLE DEFAULT 0.0;
ALTER TABLE books ADD COLUMN rating_note TEXT;

-- End Update