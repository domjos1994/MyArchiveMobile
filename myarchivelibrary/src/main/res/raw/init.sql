

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
    games INTEGER DEFAULT 1
);