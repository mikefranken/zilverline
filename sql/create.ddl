DROP TABLE COLLECTIONS;

CREATE TABLE COLLECTIONS(
	ID INTEGER NOT NULL PRIMARY KEY,
	NAME VARCHAR NOT NULL,
	DESCRIPTION VARCHAR,
	CONTENTDIR VARCHAR,
	URL VARCHAR,
	INDEXDIR VARCHAR,
	CACHEDIR VARCHAR,
	CACHEURL VARCHAR,
	ANALYZER VARCHAR,
	KEEPCACHE BOOLEAN
)
CREATE UNIQUE INDEX SYS_IDX_SYS_CT_33_35 ON COLLECTIONS(NAME)

INSERT INTO COLLECTIONS VALUES(0,'problems','All known problems Collection','d:/books/problems',
NULL,NULL,NULL,NULL,NULL,FALSE)

INSERT INTO COLLECTIONS VALUES(1,'example','Example Collection', 'd:/books/example',
'http://localhost/books/example/',
'd:/temp/zilverline/index/example',
'd:/temp/zilverline/cache/example',NULL,NULL,FALSE)

INSERT INTO COLLECTIONS VALUES(2,'Java books','All my java books', 'e:/books/java',
'http://localhost/books_e/java/',
'd:/temp/zilverline/index/java',
'd:/temp/zilverline/cache/java','http://localhost/cachedBooks/java/','org.apache.lucene.analysis.standard.StandardAnalyzer',FALSE)
