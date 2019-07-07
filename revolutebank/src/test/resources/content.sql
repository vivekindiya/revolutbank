DROP TABLE IF EXISTS User;

CREATE TABLE User (UserId LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
 UserName VARCHAR(50) NOT NULL,
 EmailAddress VARCHAR(100) NOT NULL);

CREATE UNIQUE INDEX idx_usr on User(UserName,EmailAddress);

INSERT INTO User (UserName, EmailAddress) VALUES ('vivek','vivek@gmail.com');
INSERT INTO User (UserName, EmailAddress) VALUES ('jhon','jhon@gmail.com');
INSERT INTO User (UserName, EmailAddress) VALUES ('michael','michael@gmail.com');
INSERT INTO User (UserName, EmailAddress) VALUES ('tom','tom@gmail.com');

DROP TABLE IF EXISTS Account;

CREATE TABLE Account (AccountId LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
UserName VARCHAR(50),
Balance DECIMAL(25,4),
CurrencyCode VARCHAR(100)
);

CREATE UNIQUE INDEX idx_acc on Account(UserName,CurrencyCode);

INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('vivek',100.0000,'USD');
INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('jhon',200.0000,'USD');
INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('vivek',500.0000,'EUR');
INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('jhon',500.0000,'EUR');
INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('vivek',500.0000,'GBP');
INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('jhon',500.0000,'GBP');