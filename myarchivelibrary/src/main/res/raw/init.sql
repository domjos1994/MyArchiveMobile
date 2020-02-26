

CREATE TABLE IF NOT EXISTS categories(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    lastUpdated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tags(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    lastUpdated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS persons(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    firstName VARCHAR(255) NOT NULL,
    lastName VARCHAR(255) NOT NULL,
    birthDate DATE DEFAULT NULL,
    image BLOB DEFAULT NULL,
    description TEXT,
    lastUpdated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS companies(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(255) NOT NULL,
    foundation DATE DEFAULT NULL,
    cover BLOB DEFAULT NULL,
    description TEXT,
    lastUpdated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);



CREATE TABLE IF NOT EXISTS albums(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(255) NOT NULL,
    originalTitle VARCHAR(255) NOT NULL,
    releaseDate DATE DEFAULT NULL,
    code VARCHAR(20) DEFAULT '',
    price DOUBLE DEFAULT 0.0,
    category INTEGER DEFAULT 0,
    cover BLOB DEFAULT NULL,
    description TEXT,
    type VARCHAR(10) DEFAULT '',
    numberOfDisks INTEGER DEFAULT 0,
    rating_web DOUBLE DEFAULT 0.0,
    rating_own DOUBLE DEFAULT 0.0,
    rating_note TEXT,
    last_heard DATE DEFAULT NULL,
    lastUpdated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(category) REFERENCES categories(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS albums_tags(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    albums INTEGER NOT NULL,
    tags INTEGER NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(albums) REFERENCES albums(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(tags) REFERENCES tags(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS albums_persons(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    albums INTEGER NOT NULL,
    persons INTEGER NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(albums) REFERENCES albums(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(persons) REFERENCES persons(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS albums_companies(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    albums INTEGER NOT NULL,
    companies INTEGER NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(albums) REFERENCES albums(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(companies) REFERENCES companies(id) ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE IF NOT EXISTS songs(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(255) NOT NULL,
    originalTitle VARCHAR(255) NOT NULL,
    releaseDate DATE DEFAULT NULL,
    code VARCHAR(20) DEFAULT '',
    price DOUBLE DEFAULT 0.0,
    category INTEGER DEFAULT 0,
    cover BLOB DEFAULT NULL,
    description TEXT,
    length DOUBLE DEFAULT 0.0,
    path TEXT,
    album INTEGER NOT NULL,
    rating_web DOUBLE DEFAULT 0.0,
    rating_own DOUBLE DEFAULT 0.0,
    rating_note TEXT,
    lastUpdated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(category) REFERENCES categories(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(album) REFERENCES albums(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS songs_tags(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    songs INTEGER NOT NULL,
    tags INTEGER NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(songs) REFERENCES songs(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(tags) REFERENCES tags(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS songs_persons(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    songs INTEGER NOT NULL,
    persons INTEGER NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(songs) REFERENCES songs(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(persons) REFERENCES persons(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS songs_companies(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    songs INTEGER NOT NULL,
    companies INTEGER NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(songs) REFERENCES songs(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(companies) REFERENCES companies(id) ON DELETE CASCADE ON UPDATE CASCADE
);



CREATE TABLE IF NOT EXISTS movies(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(255) NOT NULL,
    originalTitle VARCHAR(255) NOT NULL,
    releaseDate DATE DEFAULT NULL,
    code VARCHAR(20) DEFAULT '',
    price DOUBLE DEFAULT 0.0,
    category INTEGER DEFAULT 0,
    cover BLOB DEFAULT NULL,
    description TEXT,
    type VARCHAR(10) DEFAULT '',
    length DOUBLE DEFAULT 0.0,
    path TEXT,
    rating_web DOUBLE DEFAULT 0.0,
    rating_own DOUBLE DEFAULT 0.0,
    rating_note TEXT,
    last_seen DATE DEFAULT NULL,
    lastUpdated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(category) REFERENCES categories(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS movies_tags(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    movies INTEGER NOT NULL,
    tags INTEGER NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(movies) REFERENCES movies(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(tags) REFERENCES tags(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS movies_persons(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    movies INTEGER NOT NULL,
    persons INTEGER NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(movies) REFERENCES movies(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(persons) REFERENCES persons(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS movies_companies(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    movies INTEGER NOT NULL,
    companies INTEGER NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(movies) REFERENCES movies(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(companies) REFERENCES companies(id) ON DELETE CASCADE ON UPDATE CASCADE
);



CREATE TABLE IF NOT EXISTS books(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(255) NOT NULL,
    originalTitle VARCHAR(255) NOT NULL,
    releaseDate DATE DEFAULT NULL,
    code VARCHAR(20) DEFAULT '',
    price DOUBLE DEFAULT 0.0,
    category INTEGER DEFAULT 0,
    cover BLOB DEFAULT NULL,
    description TEXT,
    type VARCHAR(10) DEFAULT '',
    numberOfPages INTEGER DEFAULT 0,
    path TEXT,
    edition VARCHAR(255) DEFAULT '',
    topics TEXT,
    rating_web DOUBLE DEFAULT 0.0,
    rating_own DOUBLE DEFAULT 0.0,
    rating_note TEXT,
    last_read DATE DEFAULT NULL,
    lastUpdated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(category) REFERENCES categories(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS books_tags(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    books INTEGER NOT NULL,
    tags INTEGER NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(books) REFERENCES books(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(tags) REFERENCES tags(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS books_persons(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    books INTEGER NOT NULL,
    persons INTEGER NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(books) REFERENCES books(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(persons) REFERENCES persons(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS books_companies(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    books INTEGER NOT NULL,
    companies INTEGER NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(books) REFERENCES books(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(companies) REFERENCES companies(id) ON DELETE CASCADE ON UPDATE CASCADE
);



CREATE TABLE IF NOT EXISTS games(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(255) NOT NULL,
    originalTitle VARCHAR(255) NOT NULL,
    releaseDate DATE DEFAULT NULL,
    code VARCHAR(20) DEFAULT '',
    price DOUBLE DEFAULT 0.0,
    category INTEGER DEFAULT 0,
    cover BLOB DEFAULT NULL,
    description TEXT,
    type VARCHAR(10) DEFAULT '',
    length DOUBLE DEFAULT 0.0,
    rating_web DOUBLE DEFAULT 0.0,
    rating_own DOUBLE DEFAULT 0.0,
    rating_note TEXT,
    last_played DATE DEFAULT NULL,
    lastUpdated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(category) REFERENCES categories(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS games_tags(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    games INTEGER NOT NULL,
    tags INTEGER NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(games) REFERENCES games(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(tags) REFERENCES tags(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS games_persons(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    games INTEGER NOT NULL,
    persons INTEGER NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(games) REFERENCES games(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(persons) REFERENCES persons(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS games_companies(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    games INTEGER NOT NULL,
    companies INTEGER NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(games) REFERENCES games(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(companies) REFERENCES companies(id) ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE IF NOT EXISTS library(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    media INTEGER NOT NULL,
    type VARCHAR(255) NOT NULL,
    person INTEGER NOT NULL,
    numberOfDays INTEGER DEFAULT 0,
    numberOfWeeks INTEGER DEFAULT 0,
    deadLine DATE DEFAULT NULL,
    returnedAt DATE DEFAULT NULL,
    FOREIGN KEY(person) REFERENCES persons(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS lists(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(255) NOT NULL,
    deadLine DATE DEFAULT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS media_lists(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    list INTEGER NOT NULL,
    media INTEGER NOT NULL,
    type VARCHAR(255) NOT NULL,
    FOREIGN KEY(list) REFERENCES lists(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS filters(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(255) NOT NULL,
    search VARCHAR(255) DEFAULT '',
    categories VARCHAR(255) DEFAULT '',
    tags VARCHAR(255) DEFAULT '',
    books INTEGER DEFAULT 1,
    movies INTEGER DEFAULT 1,
    music INTEGER DEFAULT 1,
    games INTEGER DEFAULT 1,
    customFields VARCHAR(255) DEFAULT ''
);


CREATE TABLE IF NOT EXISTS customFields(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL,
    allowedValues TEXT,
    books INTEGER DEFAULT 1,
    movies INTEGER DEFAULT 1,
    albums INTEGER DEFAULT 1,
    games INTEGER DEFAULT 1
);

CREATE TABLE IF NOT EXISTS books_customFields(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    books INTEGER NOT NULL,
    customFields INTEGER NOT NULL,
    value TEXT,
    FOREIGN KEY(books) REFERENCES books(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(customFields) REFERENCES customFields(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS movies_customFields(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    movies INTEGER NOT NULL,
    customFields INTEGER NOT NULL,
    value TEXT,
    FOREIGN KEY(movies) REFERENCES movies(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(customFields) REFERENCES customFields(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS games_customFields(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    games INTEGER NOT NULL,
    customFields INTEGER NOT NULL,
    value TEXT,
    FOREIGN KEY(games) REFERENCES games(id) ON DELETE CASCADE ON UPDATE CASCADE,


    FOREIGN KEY(customFields) REFERENCES customFields(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS albums_customFields(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    albums INTEGER NOT NULL,
    customFields INTEGER NOT NULL,
    value TEXT,
    FOREIGN KEY(albums) REFERENCES albums(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(customFields) REFERENCES customFields(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE VIEW IF NOT EXISTS media AS
        SELECT books.id as id, books.title as title, originalTitle, books.description as description,
            'books' as type, cover, categories.title as category, group_concat(tags.title) as tags,
            group_concat(DISTINCT customFields.title || ':' || books_customFields.value) as customFields
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
            group_concat(DISTINCT customFields.title || ':' || movies_customFields.value) as customFields
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
            group_concat(DISTINCT customFields.title || ':' || albums_customFields.value) as customFields
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
            group_concat(DISTINCT customFields.title || ':' || games_customFields.value) as customFields
	        FROM (((((games
	            LEFT JOIN categories ON categories.ID=games.category)
	            LEFT JOIN games_tags ON games_tags.games=games.id)
	            LEFT JOIN tags ON tags.id=games_tags.tags)
                LEFT JOIN games_customFields ON games_customFields.games=games.id)
                LEFT JOIN customFields ON customFields.id=games_customFields.customFields)
            GROUP BY games.title
    ORDER BY title
;