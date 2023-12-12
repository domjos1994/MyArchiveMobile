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

-- Version 4
ALTER TABLE albums ADD COLUMN last_heard DATE DEFAULT NULL;
ALTER TABLE movies ADD COLUMN last_seen DATE DEFAULT NULL;
ALTER TABLE games ADD COLUMN last_played DATE DEFAULT NULL;
ALTER TABLE books ADD COLUMN last_read DATE DEFAULT NULL;

-- Version 5
ALTER TABLE filters ADD COLUMN customFields VARCHAR(255) DEFAULT '';


-- Version 13
ALTER TABLE filters ADD COLUMN list INTEGER DEFAUL 0 REFERENCES lists(id);
DROP VIEW IF EXISTS media;
CREATE VIEW IF NOT EXISTS media AS
        SELECT books.id as id, books.title as title, originalTitle, books.description as description,
            'books' as type, cover, categories.title as category, group_concat(tags.title) as tags,
            group_concat(DISTINCT customFields.title || ':' || books_customFields.value) as customFields,
            books.releaseDate as releaseDate, books.timestamp as timestamp
            FROM (((((books
                LEFT JOIN categories ON categories.ID=books.category)
                LEFT JOIN books_tags ON books_tags.books=books.id)
                LEFT JOIN tags ON tags.id=books_tags.tags)
                LEFT JOIN books_customFields ON books_customFields.books=books.id)
                LEFT JOIN customFields ON customFields.id=books_customFields.customFields)
            GROUP BY books.title
	UNION
	    SELECT movies.id as id, movies.title as title, originalTitle, movies.description as description,
	        'movies' as type, cover, categories.title as category, group_concat(tags.title) as tags,
            group_concat(DISTINCT customFields.title || ':' || movies_customFields.value) as customFields,
            movies.releaseDate as releaseDate, movies.timestamp as timestamp
	        FROM (((((movies
	            LEFT JOIN categories ON categories.ID=movies.category)
	            LEFT JOIN movies_tags ON movies_tags.movies=movies.id)
	            LEFT JOIN tags ON tags.id=movies_tags.tags)
                LEFT JOIN movies_customFields ON movies_customFields.movies=movies.id)
                LEFT JOIN customFields ON customFields.id=movies_customFields.customFields)
            GROUP BY movies.title
	UNION
	    SELECT albums.id as id, albums.title as title, originalTitle, albums.description as description,
	        'albums' as type, cover, categories.title as category, group_concat(tags.title) as tags,
            group_concat(DISTINCT customFields.title || ':' || albums_customFields.value) as customFields,
            albums.releaseDate as releaseDate, albums.timestamp as timestamp
	        FROM (((((albums
	            LEFT JOIN categories ON categories.ID=albums.category)
	            LEFT JOIN albums_tags ON albums_tags.albums=albums.id)
	            LEFT JOIN tags ON tags.id=albums_tags.tags)
                LEFT JOIN albums_customFields ON albums_customFields.albums=albums.id)
                LEFT JOIN customFields ON customFields.id=albums_customFields.customFields)
            GROUP BY albums.title
	UNION
	    SELECT games.id as id, games.title as title, originalTitle, games.description as description,
	        'games' as type, cover, categories.title as category, group_concat(tags.title) as tags,
            group_concat(DISTINCT customFields.title || ':' || games_customFields.value) as customFields,
            games.releaseDate as releaseDate, games.timestamp as timestamp
	        FROM (((((games
	            LEFT JOIN categories ON categories.ID=games.category)
	            LEFT JOIN games_tags ON games_tags.games=games.id)
	            LEFT JOIN tags ON tags.id=games_tags.tags)
                LEFT JOIN games_customFields ON games_customFields.games=games.id)
                LEFT JOIN customFields ON customFields.id=games_customFields.customFields)
            GROUP BY games.title
    ORDER BY title
;

-- End Update